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

import com.movieweb.model.MovieDetails;
import com.movieweb.model.MovieFilter;
import com.movieweb.model.Users;
import com.movieweb.service.MovieService;

@WebServlet(urlPatterns = {"/movie-details", "/movie_details", "/Movie_details"})
public class MovieDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(MovieDetailsServlet.class.getName());
    private final MovieService movies;

    public MovieDetailsServlet() { this(new MovieService()); }
    public MovieDetailsServlet(MovieService movies) { this.movies = Objects.requireNonNull(movies); }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MoviePageSupport.configure(request, response);
        try {
            int id = MoviePageSupport.movieId(request);
            Users user = MoviePageSupport.user(request);
            String favourite = MovieFilter.value(request.getParameterMap(), "favourite");
            MovieDetails details = movies.getDetails(id, user == null ? null : user.getUserId());
            if (details == null) {
                MoviePageSupport.error(request, response, 404, "Không tìm thấy phim",
                        "Phim không tồn tại hoặc hiện không được công khai.");
                return;
            }
            request.setAttribute("details", details);
            request.setAttribute("movie", details.getMovie());
            request.setAttribute("csrfToken", MoviePageSupport.csrfToken(request));
            if ("added".equals(favourite)) request.setAttribute("favouriteMessage", "Đã thêm phim vào danh sách yêu thích.");
            if ("removed".equals(favourite)) request.setAttribute("favouriteMessage", "Đã bỏ phim khỏi danh sách yêu thích.");
            request.getRequestDispatcher("/WEB-INF/views/movie_details.jsp").forward(request, response);
        } catch (IllegalArgumentException ex) {
            MoviePageSupport.error(request, response, 400, "Mã phim không hợp lệ", ex.getMessage());
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Cannot load movie details from database", ex);
            MoviePageSupport.error(request, response, 503, "Chưa thể tải thông tin phim",
                    "Hệ thống chưa kết nối được cơ sở dữ liệu. Vui lòng thử lại sau hoặc kiểm tra cấu hình kết nối.");
        }
    }
}
