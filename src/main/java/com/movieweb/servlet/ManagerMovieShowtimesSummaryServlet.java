package com.movieweb.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.movieweb.model.Users;
import com.movieweb.service.ManagerDeleteMovieShowtimesService;

@WebServlet("/manager-movie-showtimes-summary")
public class ManagerMovieShowtimesSummaryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ManagerDeleteMovieShowtimesService service =
            new ManagerDeleteMovieShowtimesService();

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Users currentUser = session == null ? null : (Users) session.getAttribute("user");
        if (currentUser == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int movieId;
        try {
            movieId = Integer.parseInt(req.getParameter("movieId").trim());
        }
        catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int[] counts = service.getSummary(currentUser.getUserId(), movieId);
        if (counts == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("{\"deletable\":" + counts[0] + ",\"blocked\":" + counts[1] + "}");
    }
}