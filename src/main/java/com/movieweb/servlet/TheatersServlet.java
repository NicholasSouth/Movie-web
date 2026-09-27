package com.movieweb.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.movieweb.model.Theaters;
import com.movieweb.service.TheatersService;

@WebServlet(urlPatterns = {"/theaters", "/Theaters"})
public class TheatersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final TheatersService theatersService;
    public TheatersServlet() {
        this(new TheatersService());
    }
    public TheatersServlet(TheatersService theaterService) {
        this.theatersService = Objects.requireNonNull(theaterService);
    }

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {
        List<Theaters> theaters = theatersService.getAllTheaters();
        req.setAttribute("theaters", theaters);
        req.getRequestDispatcher("/theaters.jsp")
           .forward(req, resp);
    }
}