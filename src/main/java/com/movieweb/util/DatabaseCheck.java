package com.movieweb.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import com.movieweb.model.MovieCatalog;
import com.movieweb.model.MovieDetails;
import com.movieweb.model.MovieFilter;
import com.movieweb.service.MovieService;

/** Read-only local diagnostic, launched by check-database.cmd. */
public final class DatabaseCheck {
    private DatabaseCheck() { }
    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(15);
            try (ResultSet result = statement.executeQuery("SELECT DB_NAME(), ORIGINAL_LOGIN()")) {
                result.next();
                System.out.println("Connected to " + result.getString(1) + " as " + result.getString(2));
            }
        }
        MovieService service = new MovieService();
        MovieCatalog catalog = service.getCatalog(MovieFilter.fromParameters(Map.of()));
        System.out.println("Visible movies: " + catalog.getTotal());
        System.out.println("Genres: " + catalog.getGenres().size() + "; active theaters: " + catalog.getTheaters().size());
        if (!catalog.getMovies().isEmpty()) {
            MovieDetails details = service.getDetails(catalog.getMovies().get(0).getMovie_id(), null);
            System.out.println("Details loaded: " + details.getMovie().getMovie_name());
            System.out.println("Future showtimes for this movie: " + details.getShowtimes().size());
        }
        System.out.println("PASS: SQL Server connection and movie queries. No data was changed.");
    }
}
