package com.movieweb.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.movieweb.model.MovieFilter;
import com.movieweb.model.Users;
import com.movieweb.service.MovieService;

@WebServlet("/movie-favourite")
public class MovieFavouriteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(MovieFavouriteServlet.class.getName());
    private final MovieService movies;

    public MovieFavouriteServlet() { this(new MovieService()); }
    public MovieFavouriteServlet(MovieService movies) { this.movies = Objects.requireNonNull(movies); }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MoviePageSupport.configure(request, response);
        Users user = MoviePageSupport.user(request);
        if (user == null) {
            MoviePageSupport.error(request, response, 401, "Vui lòng đăng nhập",
                    "Bạn cần đăng nhập để quản lý danh sách phim yêu thích.");
            return;
        }
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            if (!MoviePageSupport.validCsrf(request, MovieFilter.value(parameters, "csrfToken"))) {
                MoviePageSupport.error(request, response, 403, "Yêu cầu đã hết hạn",
                        "Vui lòng tải lại trang chi tiết phim rồi thử lại.");
                return;
            }
            int id = MovieFilter.requiredId(MovieFilter.value(parameters, "movieId"), "Mã phim");
            String action = MovieFilter.value(parameters, "action");
            if (!"add".equals(action) && !"remove".equals(action))
                throw new IllegalArgumentException("Thao tác yêu thích không hợp lệ.");
            if (!movies.setFavourite(user.getUserId(), id, "add".equals(action))) {
                MoviePageSupport.error(request, response, 404, "Không tìm thấy phim",
                        "Phim không tồn tại hoặc hiện không được công khai.");
                return;
            }
            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            response.setHeader("Location", request.getContextPath() + "/movie-details?id=" + id
                    + "&favourite=" + ("add".equals(action) ? "added" : "removed"));
        } catch (IllegalArgumentException ex) {
            MoviePageSupport.error(request, response, 400, "Yêu cầu không hợp lệ", ex.getMessage());
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Cannot update movie favourite in database", ex);
            MoviePageSupport.error(request, response, 503, "Chưa thể lưu danh sách yêu thích",
                    "Hệ thống chưa lưu được thay đổi. Vui lòng thử lại sau.");
        }
    }
}
