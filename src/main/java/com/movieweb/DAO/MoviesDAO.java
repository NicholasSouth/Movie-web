package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.movieweb.model.Movies;
import com.movieweb.util.DBConnection;

public class MoviesDAO {
    public Movies getMovieById(int movie_id) {
        String sql = "SELECT * FROM Movies WHERE movie_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movie_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapMovie(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Movies> getTop10Movies() {
        List<Movies> movies = new ArrayList<>();
        String sql =
            "SELECT TOP 10 * " +
            "FROM Movies " +
            "WHERE isActive = 1 " +
            "AND deleted_at IS NULL " +
            "ORDER BY avg_rating DESC, movie_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                movies.add(mapMovie(rs));
            }

        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    private Movies mapMovie(ResultSet rs) throws SQLException {
        Movies movie = new Movies();
        movie.setMovie_id(rs.getInt("movie_id"));
        movie.setMovie_name(rs.getString("movie_name"));
        movie.setDescription(rs.getString("description"));
        movie.setAge_rating(rs.getString("age_rating"));
        movie.setAvg_rating(rs.getDouble("avg_rating"));
        movie.setDuration_minute(rs.getInt("duration_minute"));
        movie.setAvailable_from(rs.getTimestamp("available_from"));
        movie.setAvailable_until(rs.getTimestamp("available_until"));
        movie.setPoster_path(rs.getString("poster_path"));
        movie.setTrailer_path(rs.getString("trailer_path"));
        movie.setTrailer_link(rs.getString("trailer_link"));
        movie.setActive(rs.getBoolean("isActive"));
        movie.setDeleted_at(rs.getTimestamp("deleted_at"));
        return movie;
    }
}