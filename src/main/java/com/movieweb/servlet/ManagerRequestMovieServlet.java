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
import com.movieweb.service.ManagerRequestMovieService;

@WebServlet("/manager-request-movie")
public class ManagerRequestMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ManagerRequestMovieService service = new ManagerRequestMovieService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

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

        String error = service.requestMovie(currentUser, req.getParameter("movieName"),
                req.getParameter("trailerUrl"), session);
        if (error != null) session.setAttribute("showtimeError", error);
        else {
            session.setAttribute("showtimeSuccess",
                    "Movie request sent to the admin successfully.");
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

    private void redirectBack(HttpServletRequest req, HttpServletResponse resp, Integer theaterId,
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