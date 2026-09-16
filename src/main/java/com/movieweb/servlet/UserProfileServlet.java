package com.movieweb.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.movieweb.model.Movies;
import com.movieweb.model.Users;
import com.movieweb.service.FavouriteMoviesService;

@WebServlet("/user-profile")
public class UserProfileServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private FavouriteMoviesService favouriteMoviesService;

    @Override
    public void init()
            throws ServletException
    {
        favouriteMoviesService = new FavouriteMoviesService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException
    {
        HttpSession session = request.getSession(false);
        /*User must be logged in.*/
        if (session == null)
        {
            response.sendRedirect(request.getContextPath() + "/log_in.jsp");
            return;
        }
        Users currentUser = (Users) session.getAttribute("user");
        if (currentUser == null)
        {
            response.sendRedirect(request.getContextPath() + "/log_in.jsp");
            return;
        }

        /*Get the user's favourite movies.*/
        List<Movies> favouriteMovies = favouriteMoviesService.getFavouriteMovies(currentUser.getUserId());

        /*Give the movies to movie_grid.jsp.*/
        request.setAttribute("movieGridMovies", favouriteMovies);

        /*Display the profile page.*/
        request.getRequestDispatcher("/user_profile.jsp").forward(request, response);
    }
}

