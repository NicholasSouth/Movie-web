package com.movieweb.servlet;

import com.movieweb.model.Theaters;
import com.movieweb.service.TheaterService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
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
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        List<Theaters> theaters = theaterService.getTheaters();
        request.setAttribute("theaters", theaters);
        request.getRequestDispatcher("/theaters.jsp").forward(request, response);
    }
}
