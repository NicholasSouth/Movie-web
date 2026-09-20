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

import com.movieweb.model.MovieCatalog;
import com.movieweb.model.MovieFilter;
import com.movieweb.service.MovieService;

@WebServlet(urlPatterns = {"/movies", "/Movies"})
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(MoviesServlet.class.getName());
    private final MovieService movies;

    public MoviesServlet() { this(new MovieService()); }
    public MoviesServlet(MovieService movies) { this.movies = Objects.requireNonNull(movies); }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MoviePageSupport.configure(request, response);
        try {
            MovieFilter filter = MovieFilter.fromParameters(request.getParameterMap());
            MovieCatalog catalog = movies.getCatalog(filter);
            request.setAttribute("filter", filter);
            request.setAttribute("catalog", catalog);
            request.setAttribute("movies", catalog.getMovies());
            request.setAttribute("nowShowing", catalog.getNowShowing());
            request.setAttribute("comingSoon", catalog.getComingSoon());
            request.setAttribute("popular", catalog.getPopular());
            request.setAttribute("genres", catalog.getGenres());
            request.setAttribute("tags", catalog.getTags());
            request.setAttribute("theaters", catalog.getTheaters());
            request.setAttribute("ageRatings", catalog.getAgeRatings());
            request.getRequestDispatcher("/WEB-INF/views/movies.jsp").forward(request, response);
        } catch (IllegalArgumentException ex) {
            MoviePageSupport.error(request, response, 400, "Bộ lọc không hợp lệ", ex.getMessage());
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Cannot load movie catalogue from database", ex);
            MoviePageSupport.error(request, response, 503, "Chưa thể tải danh sách phim",
                    "Hệ thống chưa kết nối được cơ sở dữ liệu. Vui lòng thử lại sau hoặc kiểm tra cấu hình kết nối.");
        }
    }
}
