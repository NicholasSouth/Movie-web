package com.movieweb.servlet;

import com.movieweb.model.Theaters;
import com.movieweb.service.TheaterService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/theaters")
public class TheaterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TheaterService theaterService;

    @Override
    public void init() throws ServletException {
        theaterService = new TheaterService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Fetch the full theater list
        List<Theaters> allTheaters = theaterService.getTheaters();

        // Filter the displayed list by name/address if a search keyword was provided
        String searchParam = request.getParameter("search");
        List<Theaters> theaters;
        if (searchParam != null && !searchParam.trim().isEmpty()) {
            String keyword = searchParam.trim().toLowerCase();
            theaters = new ArrayList<>();
            for (Theaters theater : allTheaters) {
                if (theater.getTheater_name().toLowerCase().contains(keyword) ||
                        theater.getTheater_address().toLowerCase().contains(keyword)) {
                    theaters.add(theater);
                }
            }
        }
        else theaters = allTheaters;
        request.setAttribute("theaters", theaters);

        // Nearest theater is always computed from allTheaters
        String latParam = request.getParameter("userLat");
        String lngParam = request.getParameter("userLng");
        if (latParam != null && lngParam != null) {
            try {
                double userLat = Double.parseDouble(latParam);
                double userLng = Double.parseDouble(lngParam);

                Theaters nearestTheater = null;
                double nearestDistance = Double.MAX_VALUE;
                for (Theaters theater : allTheaters) {
                    if (Double.isNaN(theater.getLatitude()) || Double.isNaN(theater.getLongtitude())) {
                        continue;
                    }

                    double distance = calculateHaversine(userLat, userLng,
                            theater.getLatitude(), theater.getLongtitude());
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearestTheater = theater;
                    }
                }

                if (nearestTheater != null) {
                    request.setAttribute("nearestTheater", nearestTheater);
                    request.setAttribute("nearestDistance", nearestDistance);
                }

            } catch (NumberFormatException e) {

            }
        }
        request.getRequestDispatcher("/theaters.jsp").forward(request, response);
    }

    // Computes the great-circle distance in km between two coordinates using the Haversine formula
    private double calculateHaversine(double lat1, double lng1, double lat2, double lng2) {
        final int EARTH_RADIUS = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}
