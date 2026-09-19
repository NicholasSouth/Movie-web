package com.movieweb.servlet;

import com.movieweb.model.Rooms;
import com.movieweb.model.Theaters;
import com.movieweb.service.TheaterDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/theater_details")
public class TheaterDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TheaterDetailsService theaterDetailsService;

    @Override
    public void init() throws ServletException {
        theaterDetailsService = new TheaterDetailsService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        int theaterId;
        try {
            theaterId = Integer.parseInt(request.getParameter("theater_id"));
        } catch (NumberFormatException e) {
            request.setAttribute("theater", null);
            request.setAttribute("rooms", null);
            request.getRequestDispatcher("theater_details.jsp").forward(request, response);
            return;
        }

        Theaters theater = theaterDetailsService.getTheaterById(theaterId);
        request.setAttribute("theater", theater);
        List<Rooms> rooms = theaterDetailsService.getRoomsByTheaterId(theaterId);
        request.setAttribute("rooms", rooms);
        request.getRequestDispatcher("/theater_details.jsp").forward(request, response);
    }
}
