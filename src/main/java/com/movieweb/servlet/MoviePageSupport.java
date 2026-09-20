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

import com.movieweb.model.MovieFilter;
import com.movieweb.model.Users;

final class MoviePageSupport {
    private static final String CSRF_SESSION_KEY = "movieCsrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private MoviePageSupport() { }

    static void configure(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("X-Content-Type-Options", "nosniff");
    }

    static int movieId(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        String id = MovieFilter.value(parameters, "id");
        String legacy = MovieFilter.value(parameters, "movie_id");
        if (!id.isEmpty() && !legacy.isEmpty() && !id.equals(legacy))
            throw new IllegalArgumentException("Hai mã phim trong đường dẫn không khớp nhau.");
        return MovieFilter.requiredId(id.isEmpty() ? legacy : id, "Mã phim");
    }

    static Users user(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object value = session == null ? null : session.getAttribute("user");
        if (!(value instanceof Users)) return null;
        Users user = (Users) value;
        return user.getUserId() > 0 && user.isActive() && user.getDeletedAt() == null ? user : null;
    }

    static String csrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        synchronized (session) {
            String token = (String) session.getAttribute(CSRF_SESSION_KEY);
            if (token == null) {
                byte[] bytes = new byte[32];
                RANDOM.nextBytes(bytes);
                token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
                session.setAttribute(CSRF_SESSION_KEY, token);
            }
            return token;
        }
    }

    static boolean validCsrf(HttpServletRequest request, String supplied) {
        HttpSession session = request.getSession(false);
        Object stored = session == null ? null : session.getAttribute(CSRF_SESSION_KEY);
        return stored instanceof String && supplied != null && supplied.length() <= 100
                && MessageDigest.isEqual(((String) stored).getBytes(StandardCharsets.UTF_8),
                        supplied.getBytes(StandardCharsets.UTF_8));
    }

    static void error(HttpServletRequest request, HttpServletResponse response, int status,
            String title, String message) throws ServletException, IOException {
        response.setStatus(status);
        if (status == 503) response.setHeader("Retry-After", "30");
        request.setAttribute("errorStatus", status);
        request.setAttribute("errorTitle", title);
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/WEB-INF/views/movie-error.jsp").forward(request, response);
    }
}
