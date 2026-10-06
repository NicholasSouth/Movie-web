package com.movieweb.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.movieweb.model.Movies;
import com.movieweb.model.Rooms;
import com.movieweb.model.Showtimes;
import com.movieweb.model.Theaters;
import com.movieweb.model.Users;
import com.movieweb.service.ManagerShowtimesService;

@WebServlet("/manager-showtimes")
public class ManagerShowtimesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ManagerShowtimesService managerShowtimesService;

    public ManagerShowtimesServlet() {
        this(new ManagerShowtimesService());
    }

    public ManagerShowtimesServlet(ManagerShowtimesService managerShowtimesService) {
        this.managerShowtimesService = Objects.requireNonNull(managerShowtimesService);
    }

    @Override
    protected void doGet(
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

        int userId = currentUser.getUserId();
        List<Theaters> theaters =
                managerShowtimesService.getTheatersByManagerId(userId);
        if (theaters == null) {
            theaters = new ArrayList<>();
        }

        LocalDate today = LocalDate.now();
        LocalDate lastDate = today.plusDays(6);
        String theaterParameter = req.getParameter("theaterId");
        String dateParameter = req.getParameter("date");
        Integer selectedTheaterId = null;
        if (theaterParameter != null && !theaterParameter.trim().isEmpty()) {
            try {
                selectedTheaterId = Integer.parseInt(theaterParameter);
            }
            catch (NumberFormatException e) {
                selectedTheaterId = null;
            }
        }
        if (selectedTheaterId == null && !theaters.isEmpty()) {
            selectedTheaterId = theaters.get(0).getTheater_id();
        }
        if (selectedTheaterId != null &&
                !managerShowtimesService.isManagerAssignedToTheater(
                        userId,
                        selectedTheaterId)) {
            selectedTheaterId = theaters.isEmpty()
                    ? null
                    : theaters.get(0).getTheater_id();
        }

        LocalDate selectedDate = today;
        if (dateParameter != null && !dateParameter.trim().isEmpty()) {
            try {
                selectedDate = LocalDate.parse(dateParameter);
            }
            catch (Exception e) {
                selectedDate = today;
            }
        }

        if (selectedDate.isBefore(today) ||
                selectedDate.isAfter(lastDate)) {
            selectedDate = today;
        }

        List<Rooms> rooms = new ArrayList<>();
        List<Showtimes> scheduleShowtimes = new ArrayList<>();
        Map<Integer, Movies> scheduleMovies = new HashMap<>();
        if (selectedTheaterId != null) {
            rooms = managerShowtimesService.getRoomsByTheaterId(
                    selectedTheaterId
            );
            scheduleShowtimes =
                    managerShowtimesService.getShowtimesByTheaterAndDate(
                            selectedTheaterId,
                            selectedDate
                    );

            Set<Integer> movieIdSet = new LinkedHashSet<>();
            for (Showtimes showtime : scheduleShowtimes) {
                movieIdSet.add(showtime.getMovie_id());
            }

            List<Integer> movieIds = new ArrayList<>(movieIdSet);
            List<Movies> scheduleMovieList =
                    managerShowtimesService.getMoviesByIds(movieIds);
            for (Movies movie : scheduleMovieList) {
                scheduleMovies.put(movie.getMovie_id(), movie);
            }
        }

        List<LocalDate> weekDates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDates.add(today.plusDays(i));
        }

        Theaters selectedTheater = null;
        for (Theaters theater : theaters) {
            if (selectedTheaterId != null &&
                    theater.getTheater_id() == selectedTheaterId) {
                selectedTheater = theater;
                break;
            }
        }

        req.setAttribute("currentManagerPage", "showtimes");
        req.setAttribute("managerTheaters", theaters);
        req.setAttribute("selectedTheater", selectedTheater);
        req.setAttribute("selectedTheaterId", selectedTheaterId);
        req.setAttribute("rooms", rooms);
        req.setAttribute("scheduleShowtimes", scheduleShowtimes);
        req.setAttribute("scheduleMovies", scheduleMovies);
        req.setAttribute("today", today);
        req.setAttribute("selectedDate", selectedDate);
        req.setAttribute("weekDates", weekDates);
        req.getRequestDispatcher("/manager_showtimes.jsp").forward(req, resp);
    }
}