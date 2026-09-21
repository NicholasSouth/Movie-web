package com.movieweb.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.movieweb.model.Movies;
import com.movieweb.model.Users;
import com.movieweb.service.FavouriteMoviesService;
import com.movieweb.service.MoviesService;

@WebServlet(urlPatterns = {
        "/movie-details",
        "/movie_details",
        "/Movie_details"
})
public class MovieDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(MovieDetailsServlet.class.getName());
    private final MoviesService moviesService;
    private final FavouriteMoviesService favouriteMoviesService;
    public MovieDetailsServlet() {
        this(
                new MoviesService(),
                new FavouriteMoviesService()
        );
    }
    public MovieDetailsServlet(
            MoviesService moviesService,
            FavouriteMoviesService favouriteMoviesService) {
        this.moviesService = Objects.requireNonNull(moviesService);
        this.favouriteMoviesService = Objects.requireNonNull(favouriteMoviesService);
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        MoviePageServlet.configure(request, response);
        try {
            int movieId = MoviePageServlet.movieId(request);
            Users user = MoviePageServlet.user(request);
            String favourite = request.getParameter("favourite");
            Movies movie = moviesService.getDetails(
                            movieId,
                            user == null
                                    ? null
                                    : user.getUserId()
                    );
            if (movie == null) {
                MoviePageServlet.error(
                        request,
                        response,
                        404,
                        "Movie Not Found",
                        "The movie does not exist or is not currently public."
                );
                return;
            }
            boolean isFavourite = false;

            if (user != null) {
                isFavourite = favouriteMoviesService.isFavourite(
                                user.getUserId(),
                                movieId
                        );
            }

            // Pass data to JSP
            request.setAttribute("movie", movie);
            request.setAttribute("loggedInUser", user);
            request.setAttribute("isFavourite", isFavourite);
            request.setAttribute("csrfToken", MoviePageServlet.csrfToken(request)
            );
            if ("added".equals(favourite)) {
                request.setAttribute(
                        "favouriteMessage",
                        "Movie added to your favorites."
                );
            } 
            else if ("removed".equals(favourite)) {
                request.setAttribute(
                        "favouriteMessage",
                        "Movie removed from your favorites."
                );
            } 
            else if ("error".equals(favourite)) {
                request.setAttribute(
                        "favouriteMessage",
                        "Unable to update your favorites."
                );
            }

            // JSP is directly inside webapp
            request.getRequestDispatcher("/movie_details.jsp").forward(request, response);
        } 
        catch (IllegalArgumentException ex) {
            MoviePageServlet.error(
                    request,
                    response,
                    400,
                    "Invalid Movie ID",
                    ex.getMessage()
            );
        } 
        catch (SQLException ex) {
            LOG.log(
                    Level.SEVERE,
                    "Cannot load movie details from database",
                    ex
            );
            MoviePageServlet.error(
                    request,
                    response,
                    503,
                    "Unable to Load Movie Details",
                    "The database is currently unavailable. Please try again later."
            );
        }
    }
}