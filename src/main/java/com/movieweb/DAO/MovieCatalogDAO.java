package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.movieweb.model.Actors;
import com.movieweb.model.Authors;
import com.movieweb.model.Directors;
import com.movieweb.model.Genres;
import com.movieweb.model.MovieCatalog;
import com.movieweb.model.MovieDetails;
import com.movieweb.model.MovieFilter;
import com.movieweb.model.Movies;
import com.movieweb.model.ShowtimeView;
import com.movieweb.model.Tags;
import com.movieweb.model.Theaters;
import com.movieweb.util.DBConnection;

/** Queries used by the public movie pages; SQL failures must reach the controller. */
public class MovieCatalogDAO {
    private static final String VISIBLE = "m.isActive = 1 AND m.deleted_at IS NULL";
    private static final String NOW_SHOWING = "m.available_from <= GETDATE()"
            + " AND (m.available_until IS NULL OR m.available_until >= CAST(GETDATE() AS date))";
    private static final String COMING_SOON = "m.available_from > GETDATE()";
    private static final String SHOWTIME_JOINS = " FROM Showtimes s"
            + " INNER JOIN Rooms r ON r.room_id = s.room_id"
            + " INNER JOIN Theaters t ON t.theater_id = r.theater_id";
    private static final String AVAILABLE_SHOWTIME = "s.start_at > GETDATE()"
            + " AND UPPER(s.status) = 'SCHEDULED' AND r.isActive = 1 AND t.isActive = 1 AND t.deleted_at IS NULL";

    @FunctionalInterface
    public interface ConnectionFactory { Connection open() throws SQLException; }

    private final ConnectionFactory connections;

    public MovieCatalogDAO() { this(DBConnection::getConnection); }
    public MovieCatalogDAO(ConnectionFactory connections) { this.connections = connections; }

    public MovieCatalog findCatalog(MovieFilter filter) throws SQLException {
        try (Connection connection = connections.open()) {
            Query query = filterQuery(filter);
            int total;
            try (PreparedStatement statement = prepare(connection, "SELECT COUNT(*) FROM Movies m WHERE " + query.where)) {
                bind(statement, query.parameters);
                try (ResultSet rows = statement.executeQuery()) {
                    rows.next();
                    total = rows.getInt(1);
                }
            }
            List<Object> pageParameters = new ArrayList<>(query.parameters);
            pageParameters.add(filter.getOffset());
            pageParameters.add(MovieFilter.PAGE_SIZE);
            List<Movies> movies = movies(connection, "SELECT m.* FROM Movies m WHERE " + query.where
                    + " ORDER BY " + orderBy(filter.getSort()) + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", pageParameters);
            List<Movies> nowShowing = List.of(), comingSoon = List.of(), popular = List.of();
            if (!filter.isHasFilters() && filter.getPage() == 1) {
                nowShowing = movies(connection, "SELECT TOP (8) m.* FROM Movies m WHERE " + VISIBLE + " AND "
                        + NOW_SHOWING + " ORDER BY m.available_from DESC, m.movie_id DESC", List.of());
                comingSoon = movies(connection, "SELECT TOP (8) m.* FROM Movies m WHERE " + VISIBLE + " AND "
                        + COMING_SOON + " ORDER BY m.available_from, m.movie_id", List.of());
                // Popularity is the real number of favourites, with rating as a tie breaker.
                popular = movies(connection, "SELECT TOP (8) m.* FROM Movies m WHERE " + VISIBLE
                        + " ORDER BY (SELECT COUNT(*) FROM Favourite_movies f WHERE f.movie_id = m.movie_id) DESC,"
                        + " m.avg_rating DESC, m.movie_id DESC", List.of());
            }
            return new MovieCatalog(movies, nowShowing, comingSoon, popular, genres(connection, null),
                    tags(connection, null), theaters(connection), ageRatings(connection), total, filter.getPage());
        }
    }

    public MovieDetails findDetails(int movieId, Integer userId) throws SQLException {
        try (Connection connection = connections.open()) {
            List<Movies> found = movies(connection, "SELECT m.* FROM Movies m WHERE " + VISIBLE
                    + " AND m.movie_id = ?", List.of(movieId));
            if (found.isEmpty()) return null;
            return new MovieDetails(found.get(0), actors(connection, movieId), directors(connection, movieId),
                    authors(connection, movieId), genres(connection, movieId), tags(connection, movieId),
                    showtimes(connection, movieId), userId != null && isFavourite(connection, userId, movieId));
        }
    }

    /** Idempotent add/remove; range locks also prevent two simultaneous adds creating duplicate rows. */
    public boolean setFavourite(int userId, int movieId, boolean add) throws SQLException {
        try (Connection connection = connections.open()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = prepare(connection,
                        "SELECT 1 FROM Movies m WITH (HOLDLOCK) WHERE " + VISIBLE + " AND m.movie_id = ?")) {
                    statement.setInt(1, movieId);
                    try (ResultSet rows = statement.executeQuery()) {
                        if (!rows.next()) {
                            connection.rollback();
                            return false;
                        }
                    }
                }
                String sql = add
                        ? "INSERT INTO Favourite_movies (user_id, movie_id, added_at)"
                                + " SELECT ?, ?, GETDATE() WHERE NOT EXISTS"
                                + " (SELECT 1 FROM Favourite_movies WITH (UPDLOCK, HOLDLOCK) WHERE user_id = ? AND movie_id = ?)"
                        : "DELETE FROM Favourite_movies WHERE user_id = ? AND movie_id = ?";
                try (PreparedStatement statement = prepare(connection, sql)) {
                    statement.setInt(1, userId);
                    statement.setInt(2, movieId);
                    if (add) {
                        statement.setInt(3, userId);
                        statement.setInt(4, movieId);
                    }
                    statement.executeUpdate();
                }
                connection.commit();
                return true;
            } catch (SQLException ex) {
                try { connection.rollback(); } catch (SQLException rollback) { ex.addSuppressed(rollback); }
                throw ex;
            }
        }
    }

    private Query filterQuery(MovieFilter filter) {
        StringBuilder where = new StringBuilder(VISIBLE);
        List<Object> parameters = new ArrayList<>();
        if (!filter.getSearch().isEmpty()) {
            String match = "%" + escapeLike(filter.getSearch()) + "%";
            where.append(" AND (m.movie_name LIKE ? ESCAPE '\\'")
                    .append(" OR EXISTS (SELECT 1 FROM Movie_actors ma INNER JOIN Actors a ON a.actor_id = ma.actor_id")
                    .append(" WHERE ma.movie_id = m.movie_id AND a.actor_name LIKE ? ESCAPE '\\')")
                    .append(" OR EXISTS (SELECT 1 FROM Movie_directors md INNER JOIN Directors d ON d.director_id = md.director_id")
                    .append(" WHERE md.movie_id = m.movie_id AND d.director_name LIKE ? ESCAPE '\\')")
                    .append(" OR EXISTS (SELECT 1 FROM Movie_authors mw INNER JOIN Authors w ON w.author_id = mw.author_id")
                    .append(" WHERE mw.movie_id = m.movie_id AND w.author_name LIKE ? ESCAPE '\\'))");
            for (int i = 0; i < 4; i++) parameters.add(match);
        }
        if (filter.getGenreId() != null) {
            where.append(" AND EXISTS (SELECT 1 FROM Movie_genres mg WHERE mg.movie_id = m.movie_id AND mg.genre_id = ?)");
            parameters.add(filter.getGenreId());
        }
        if (filter.getTagId() != null) {
            where.append(" AND EXISTS (SELECT 1 FROM Movie_tags mt WHERE mt.movie_id = m.movie_id AND mt.tag_id = ?)");
            parameters.add(filter.getTagId());
        }
        if (!filter.getAgeRating().isEmpty()) {
            where.append(" AND m.age_rating = ?");
            parameters.add(filter.getAgeRating());
        }
        if (filter.getMinRating() != null) {
            where.append(" AND m.avg_rating >= ?");
            parameters.add(filter.getMinRating());
        }
        if ("now-showing".equals(filter.getStatus())) where.append(" AND ").append(NOW_SHOWING);
        if ("coming-soon".equals(filter.getStatus())) where.append(" AND ").append(COMING_SOON);
        if (filter.getTheaterId() != null || filter.getDate() != null) {
            where.append(" AND EXISTS (SELECT 1").append(SHOWTIME_JOINS)
                    .append(" WHERE s.movie_id = m.movie_id AND ").append(AVAILABLE_SHOWTIME);
            if (filter.getTheaterId() != null) {
                where.append(" AND t.theater_id = ?");
                parameters.add(filter.getTheaterId());
            }
            if (filter.getDate() != null) {
                where.append(" AND CAST(s.start_at AS date) = ?");
                parameters.add(Date.valueOf(filter.getDate()));
            }
            where.append(')');
        }
        return new Query(where.toString(), parameters);
    }

    private static String orderBy(String sort) {
        switch (sort) {
            case "popular": return "(SELECT COUNT(*) FROM Favourite_movies f WHERE f.movie_id = m.movie_id) DESC,"
                    + " m.avg_rating DESC, m.movie_id DESC";
            case "name": return "m.movie_name ASC, m.movie_id ASC";
            case "rating": return "m.avg_rating DESC, m.movie_id DESC";
            case "duration": return "m.duration_minute ASC, m.movie_id ASC";
            default: return "m.available_from DESC, m.movie_id DESC";
        }
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_").replace("[", "\\[");
    }

    private List<Movies> movies(Connection connection, String sql, List<Object> parameters) throws SQLException {
        List<Movies> result = new ArrayList<>();
        try (PreparedStatement statement = prepare(connection, sql)) {
            bind(statement, parameters);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Movies movie = new Movies();
                    movie.setMovie_id(rows.getInt("movie_id"));
                    movie.setMovie_name(rows.getString("movie_name"));
                    movie.setDescription(rows.getString("description"));
                    movie.setAge_rating(rows.getString("age_rating"));
                    movie.setAvg_rating(rows.getDouble("avg_rating"));
                    movie.setDuration_minute(rows.getInt("duration_minute"));
                    movie.setAvailable_from(rows.getTimestamp("available_from"));
                    movie.setAvailable_until(rows.getTimestamp("available_until"));
                    movie.setPoster_path(rows.getString("poster_path"));
                    movie.setTrailer_path(rows.getString("trailer_path"));
                    movie.setTrailer_link(rows.getString("trailer_link"));
                    movie.setActive(rows.getBoolean("isActive"));
                    movie.setDeleted_at(rows.getTimestamp("deleted_at"));
                    result.add(movie);
                }
            }
        }
        return result;
    }

    private List<Actors> actors(Connection connection, int movieId) throws SQLException {
        List<Actors> result = new ArrayList<>();
        try (PreparedStatement statement = prepare(connection,
                "SELECT DISTINCT a.actor_id, a.actor_name FROM Actors a INNER JOIN Movie_actors ma"
                        + " ON a.actor_id = ma.actor_id WHERE ma.movie_id = ? ORDER BY a.actor_name, a.actor_id")) {
            statement.setInt(1, movieId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Actors actor = new Actors();
                    actor.setActor_id(rows.getInt(1));
                    actor.setActor_name(rows.getString(2));
                    result.add(actor);
                }
            }
        }
        return result;
    }

    private List<Directors> directors(Connection connection, int movieId) throws SQLException {
        List<Directors> result = new ArrayList<>();
        try (PreparedStatement statement = prepare(connection,
                "SELECT DISTINCT d.director_id, d.director_name FROM Directors d INNER JOIN Movie_directors md"
                        + " ON d.director_id = md.director_id WHERE md.movie_id = ? ORDER BY d.director_name, d.director_id")) {
            statement.setInt(1, movieId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Directors director = new Directors();
                    director.setDirector_id(rows.getInt(1));
                    director.setDirector_name(rows.getString(2));
                    result.add(director);
                }
            }
        }
        return result;
    }

    private List<Authors> authors(Connection connection, int movieId) throws SQLException {
        List<Authors> result = new ArrayList<>();
        try (PreparedStatement statement = prepare(connection,
                "SELECT DISTINCT a.author_id, a.author_name FROM Authors a INNER JOIN Movie_authors ma"
                        + " ON a.author_id = ma.author_id WHERE ma.movie_id = ? ORDER BY a.author_name, a.author_id")) {
            statement.setInt(1, movieId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Authors author = new Authors();
                    author.setAuthor_id(rows.getInt(1));
                    author.setAuthor_name(rows.getString(2));
                    result.add(author);
                }
            }
        }
        return result;
    }

    private List<Genres> genres(Connection connection, Integer movieId) throws SQLException {
        List<Genres> result = new ArrayList<>();
        String sql = "SELECT DISTINCT g.genre_id, g.genre_name FROM Genres g"
                + (movieId == null ? "" : " INNER JOIN Movie_genres mg ON mg.genre_id = g.genre_id WHERE mg.movie_id = ?")
                + " ORDER BY g.genre_name, g.genre_id";
        try (PreparedStatement statement = prepare(connection, sql)) {
            if (movieId != null) statement.setInt(1, movieId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Genres genre = new Genres();
                    genre.setGenre_id(rows.getInt(1));
                    genre.setGenre_name(rows.getString(2));
                    result.add(genre);
                }
            }
        }
        return result;
    }

    private List<Tags> tags(Connection connection, Integer movieId) throws SQLException {
        List<Tags> result = new ArrayList<>();
        String sql = "SELECT DISTINCT t.tag_id, t.tag_name FROM Tags t"
                + (movieId == null ? "" : " INNER JOIN Movie_tags mt ON mt.tag_id = t.tag_id WHERE mt.movie_id = ?")
                + " ORDER BY t.tag_name, t.tag_id";
        try (PreparedStatement statement = prepare(connection, sql)) {
            if (movieId != null) statement.setInt(1, movieId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Tags tag = new Tags();
                    tag.setTag_id(rows.getInt(1));
                    tag.setTag_name(rows.getString(2));
                    result.add(tag);
                }
            }
        }
        return result;
    }

    private List<Theaters> theaters(Connection connection) throws SQLException {
        List<Theaters> result = new ArrayList<>();
        try (PreparedStatement statement = prepare(connection,
                "SELECT theater_id, theater_name, theater_address FROM Theaters"
                        + " WHERE isActive = 1 AND deleted_at IS NULL ORDER BY theater_name, theater_id");
                ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                Theaters theater = new Theaters();
                theater.setTheater_id(rows.getInt(1));
                theater.setTheater_name(rows.getString(2));
                theater.setTheater_address(rows.getString(3));
                theater.setActive(true);
                result.add(theater);
            }
        }
        return result;
    }

    private List<String> ageRatings(Connection connection) throws SQLException {
        List<String> result = new ArrayList<>();
        try (PreparedStatement statement = prepare(connection,
                "SELECT DISTINCT m.age_rating FROM Movies m WHERE " + VISIBLE
                        + " AND m.age_rating IS NOT NULL AND m.age_rating <> '' ORDER BY m.age_rating");
                ResultSet rows = statement.executeQuery()) {
            while (rows.next()) result.add(rows.getString(1));
        }
        return result;
    }

    private List<ShowtimeView> showtimes(Connection connection, int movieId) throws SQLException {
        List<ShowtimeView> result = new ArrayList<>();
        try (PreparedStatement statement = prepare(connection,
                "SELECT s.showtime_id, t.theater_name, t.theater_address, r.room_name, s.start_at, s.end_at"
                        + SHOWTIME_JOINS + " WHERE s.movie_id = ? AND " + AVAILABLE_SHOWTIME
                        + " ORDER BY s.start_at, t.theater_name, r.room_name, s.showtime_id")) {
            statement.setInt(1, movieId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) result.add(new ShowtimeView(rows.getInt(1), rows.getString(2), rows.getString(3),
                        rows.getString(4), rows.getTimestamp(5), rows.getTimestamp(6)));
            }
        }
        return result;
    }

    private boolean isFavourite(Connection connection, int userId, int movieId) throws SQLException {
        try (PreparedStatement statement = prepare(connection,
                "SELECT 1 FROM Favourite_movies WHERE user_id = ? AND movie_id = ?")) {
            statement.setInt(1, userId);
            statement.setInt(2, movieId);
            try (ResultSet rows = statement.executeQuery()) { return rows.next(); }
        }
    }

    private static PreparedStatement prepare(Connection connection, String sql) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setQueryTimeout(15);
        return statement;
    }

    private static void bind(PreparedStatement statement, List<Object> values) throws SQLException {
        for (int i = 0; i < values.size(); i++) statement.setObject(i + 1, values.get(i));
    }

    private static final class Query {
        final String where;
        final List<Object> parameters;
        Query(String where, List<Object> parameters) { this.where = where; this.parameters = parameters; }
    }
}
