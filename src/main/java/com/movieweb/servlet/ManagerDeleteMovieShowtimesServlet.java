package com.movieweb.servlet;

import java.io.IOException;
import java.time.LocalDate;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.movieweb.model.Users;
import com.movieweb.service.ManagerDeleteMovieShowtimesService;

@WebServlet("/manager-delete-movie-showtimes")
public class ManagerDeleteMovieShowtimesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ManagerDeleteMovieShowtimesService service =
            new ManagerDeleteMovieShowtimesService();

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        Users currentUser = (Users) session.getAttribute("user");
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        Integer theaterId = parseInt(req.getParameter("theaterId"));
        LocalDate date = parseDate(req.getParameter("date"));

        Integer movieId = parseInt(req.getParameter("movieId"));
        if (movieId == null) {
            session.setAttribute("showtimeError", "Invalid movie.");
            redirectBack(req, resp, theaterId, date);
            return;
        }

        Integer deleted = service.deleteMovieShowtimes(currentUser.getUserId(), movieId);
        if (deleted == null) {
            session.setAttribute("showtimeError", "Failed to delete showtimes.");
        }
        else if (deleted == 0) {
            session.setAttribute("showtimeError",
                    "No showtimes were deleted. They may have active bookings.");
        }
        else {
            session.setAttribute("showtimeSuccess",
                    "Deleted " + deleted + (deleted == 1 ? " showtime." : " showtimes."));
        }
        redirectBack(req, resp, theaterId, date);
    }

    private Integer parseInt(String text) {
        try {
            return Integer.parseInt(text.trim());
        }
        catch (Exception e) {
            return null;
        }
    }

    private LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text);
        }
        catch (Exception e) {
            return null;
        }
    }

    private void redirectBack(
            HttpServletRequest req,
            HttpServletResponse resp,
            Integer theaterId,
            LocalDate date)
            throws IOException {
        StringBuilder url = new StringBuilder(req.getContextPath()).append("/manager-showtimes");

        boolean hasParameter = false;
        if (theaterId != null) {
            url.append("?theaterId=").append(theaterId);
            hasParameter = true;
        }
        if (date != null) {
            url.append(hasParameter ? "&" : "?");
            url.append("date=").append(date);
        }
        resp.sendRedirect(url.toString());
    }
}