package com.movieweb.service;

import java.sql.SQLException;
import java.util.Objects;

import com.movieweb.DAO.MovieCatalogDAO;
import com.movieweb.model.MovieCatalog;
import com.movieweb.model.MovieDetails;
import com.movieweb.model.MovieFilter;

/** Business entry point shared by catalogue, details, and favourite controllers. */
public class MovieService {
    private final MovieCatalogDAO movies;

    public MovieService() { this(new MovieCatalogDAO()); }
    public MovieService(MovieCatalogDAO movies) { this.movies = Objects.requireNonNull(movies); }

    public MovieCatalog getCatalog(MovieFilter filter) throws SQLException {
        return movies.findCatalog(Objects.requireNonNull(filter));
    }

    /** Returns null for a missing, inactive, or deleted movie. */
    public MovieDetails getDetails(int movieId, Integer userId) throws SQLException {
        requirePositive(movieId, "Mã phim");
        if (userId != null) requirePositive(userId, "Mã tài khoản");
        return movies.findDetails(movieId, userId);
    }

    /** Returns false when the movie is no longer publicly available. */
    public boolean setFavourite(int userId, int movieId, boolean add) throws SQLException {
        requirePositive(userId, "Mã tài khoản");
        requirePositive(movieId, "Mã phim");
        return movies.setFavourite(userId, movieId, add);
    }

    private static void requirePositive(int value, String label) {
        if (value < 1) throw new IllegalArgumentException(label + " phải là số nguyên dương.");
    }
}
