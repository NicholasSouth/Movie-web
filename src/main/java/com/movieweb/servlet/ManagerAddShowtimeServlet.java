package com.movieweb.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.movieweb.model.Users;
import com.movieweb.service.ManagerAddShowtimeService;

@WebServlet("/manager-add-showtime")
public class ManagerAddShowtimeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ManagerAddShowtimeService service;

    public ManagerAddShowtimeServlet() {
        service = new ManagerAddShowtimeService();
    }

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

        String movieName = req.getParameter("movie");
        String startText = req.getParameter("start");
        String endText = req.getParameter("end");
        String roomText = req.getParameter("room");
        String theaterText = req.getParameter("theaterId");

        int roomId;
        int theaterId;
        try {
            roomId = Integer.parseInt(roomText);
            theaterId = Integer.parseInt(theaterText);
        }
        catch (Exception e) {
            setError(req, "Invalid room or theater.");
            redirectBack(req, resp, theaterText, null);
            return;
        }

        LocalDate selectedDate;
        try {
            selectedDate = LocalDate.parse(req.getParameter("date"));
        }
        catch (Exception e) {
            selectedDate = LocalDate.now();
        }

        Timestamp startAt;
        Timestamp endAt;
        try {
            LocalTime startTime = LocalTime.parse(startText);
            LocalTime endTime = LocalTime.parse(endText);

            startAt = Timestamp.valueOf(LocalDateTime.of(selectedDate, startTime));
            endAt = Timestamp.valueOf(LocalDateTime.of(selectedDate, endTime));
        }
        catch (Exception e) {
            setError(req, "Invalid start or end time.");
            redirectBack(req, resp, theaterText, selectedDate);
            return;
        }

        int price;
        try {
            price = Integer.parseInt(req.getParameter("price").trim());
        }
        catch (Exception e) {
            setError(req, "Invalid price.");
            redirectBack(req, resp, theaterText, selectedDate);
            return;
        }

        String error = service.addShowtime(currentUser.getUserId(), movieName, roomId, startAt,
                                            endAt, price);
        if (error != null) {
            setError(req, error);
            redirectBack(req, resp, theaterText, selectedDate);
            return;
        }

        req.getSession().setAttribute("showtimeSuccess", "Showtime added successfully.");
        redirectBack(req, resp, theaterText, selectedDate);
    }

    private void setError(
            HttpServletRequest req,
            String message) {
        req.getSession().setAttribute("showtimeError", message);
    }

    private void redirectBack(
            HttpServletRequest req,
            HttpServletResponse resp,
            String theaterId,
            LocalDate date)
            throws IOException {
        StringBuilder url = new StringBuilder(req.getContextPath()).append("/manager-showtimes");

        boolean hasParameter = false;
        if (theaterId != null && !theaterId.trim().isEmpty()) {
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