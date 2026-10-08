package com.movieweb.service;

import java.net.URI;

import jakarta.servlet.http.HttpSession;

import com.movieweb.DAO.ManagersDAO;
import com.movieweb.DAO.MoviesDAO;
import com.movieweb.model.Users;

public class ManagerRequestMovieService {
    private static final int MAX_MOVIE_NAME_LENGTH = 150;
    private static final long COOLDOWN_MILLIS = 60_000;
    private static final String COOLDOWN_ATTRIBUTE = "lastMovieRequestAt";

    private ManagersDAO managersDAO;
    private MoviesDAO moviesDAO;
    private EmailService emailService;

    public ManagerRequestMovieService() {
        managersDAO = new ManagersDAO();
        moviesDAO = new MoviesDAO();
        emailService = new EmailService();
    }

    // Returns an error message or null when the request was sent.
    public String requestMovie(Users currentUser, String movieName, String trailerUrl,
                               HttpSession session) {
        if (movieName == null || movieName.trim().isEmpty()) return "Movie name is required.";

        movieName = movieName.trim();
        if (movieName.length() > MAX_MOVIE_NAME_LENGTH) return "Movie name is too long.";

        if (trailerUrl == null || trailerUrl.trim().isEmpty()) return "Trailer link is required.";

        trailerUrl = trailerUrl.trim();
        if (!isValidHttpUrl(trailerUrl)) {
            return "Trailer link must be a valid http(s) link.";
        }

        if (managersDAO.getTheatersByManagerId(currentUser.getUserId()).isEmpty()) {
            return "You are not assigned to any theater.";
        }

        if (moviesDAO.getActiveMovieByName(movieName) != null) return "This movie already exists.";

        Long lastRequestAt = (Long) session.getAttribute(COOLDOWN_ATTRIBUTE);
        long now = System.currentTimeMillis();
        if (lastRequestAt != null && now - lastRequestAt < COOLDOWN_MILLIS) {
            return "Please wait a moment before sending another request.";
        }

        boolean sent = emailService.sendManagerMovieRequest(currentUser.getUserId(),
                currentUser.getUsername(), movieName, trailerUrl);
        if (!sent) return "Failed to send the request. Please try again later.";

        session.setAttribute(COOLDOWN_ATTRIBUTE, now);
        return null;
    }

    private boolean isValidHttpUrl(String text) {
        try {
            URI uri = new URI(text);
            String scheme = uri.getScheme();
            return scheme != null
                    && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    && uri.getHost() != null;
        }
        catch (Exception e) {
            return false;
        }
    }
}