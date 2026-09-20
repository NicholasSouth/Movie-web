package com.movieweb.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.movieweb.model.Movies;
import com.movieweb.model.Users;

final class MoviePageServlet {
    private static final String CSRF_KEY = "movieCsrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();
    private MoviePageServlet() {}
    static void configure(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        resp.setHeader(
                "X-Content-Type-Options",
                "nosniff"
        );
    }

    /*Reads movie ID from id or movie_id parameter.*/
    static int movieId(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        String id = Movies.value(params, "id");
        String legacy = Movies.value(params, "movie_id");
        if (!id.isEmpty()
                && !legacy.isEmpty()
                && !id.equals(legacy)) {
            throw new IllegalArgumentException("Movie IDs do not match.");
        }
        String selected = id.isEmpty() ? legacy : id;
        if (selected.isEmpty()) {
            throw new IllegalArgumentException("Movie ID is required.");
        }
        try {

            if (!selected.matches("[0-9]{1,10}")) {
                throw new NumberFormatException();
            }
            int value = Integer.parseInt(selected);
            if (value < 1) {
                throw new NumberFormatException();
            }
            return value;
        } 
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Movie ID must be a valid positive integer.");
        }
    }

    /*Returns the currently logged-in active user.*/
    static Users user(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        Object val = session == null
                ? null
                : session.getAttribute("user");
        if (!(val instanceof Users)) {
            return null;
        }
        Users u = (Users) val;
        return u.getUserId() > 0
                && u.isActive()
                && u.getDeletedAt() == null
                ? u
                : null;
    }

    /*Generates or retrieves CSRF token.*/
    static String csrfToken(HttpServletRequest req) {
        HttpSession session = req.getSession();
        synchronized (session) {
            String token = (String) session.getAttribute(CSRF_KEY);
            if (token == null) {
                byte[] b = new byte[32];
                RANDOM.nextBytes(b);
                token = Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(b);
                session.setAttribute(
                        CSRF_KEY,
                        token
                );
            }
            return token;
        }
    }

    /*Validates CSRF token.*/
    static boolean validCsrf(
            HttpServletRequest req,
            String supplied) {
        HttpSession session = req.getSession(false);
        Object stored = session == null
                ? null
                : session.getAttribute(CSRF_KEY);
        return stored instanceof String
                && supplied != null
                && supplied.length() <= 100
                && MessageDigest.isEqual(
                        ((String) stored)
                                .getBytes(StandardCharsets.UTF_8),
                        supplied.getBytes(StandardCharsets.UTF_8)
                );
    }

    /*Forwards to error JSP.*/
    static void error(
            HttpServletRequest req,
            HttpServletResponse resp,
            int status,
            String title,
            String msg)
            throws ServletException, IOException {
        resp.setStatus(status);
        if (status == 503) {
            resp.setHeader("Retry-After", "30");
        }
        req.setAttribute("errorStatus", status);
        req.setAttribute("errorTitle", title);
        req.setAttribute("errorMessage", msg);
        req.getRequestDispatcher("/WEB-INF/views/movie-error.jsp").forward(req, resp);
    }
}