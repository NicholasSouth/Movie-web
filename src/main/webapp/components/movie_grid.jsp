<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,com.movieweb.model.Movies,com.movieweb.util.ViewUtils" %>
<div class="movie-grid">
<%
    List<Movies> gridMovies = (List<Movies>) request.getAttribute("movieGridMovies");
    if (gridMovies == null || gridMovies.isEmpty()) {
%>
    <p class="movie-empty" role="status">No movies found.</p>
<%  } else { for (Movies gridMovie : gridMovies) { %>
    <a href="<%= request.getContextPath() %>/movie-details?id=<%= gridMovie.getMovie_id() %>" class="movie-card">
        <div class="movie-poster">
            <img src="<%= ViewUtils.h(ViewUtils.asset(request.getContextPath(), gridMovie.getPoster_path())) %>"
                 data-fallback="<%= request.getContextPath() %>/pictures/movie-placeholder.svg"
                 alt="<%= ViewUtils.h(gridMovie.getMovie_name()) %>" loading="lazy">
        </div>
        <div class="movie-card-content">
            <h3><%= ViewUtils.h(gridMovie.getMovie_name()) %></h3>
            <p class="movie-rating">&#9733; <%= ViewUtils.h(ViewUtils.rating(gridMovie.getAvg_rating())) %></p>
            <p class="movie-card-meta"><%= gridMovie.getDuration_minute() > 0 ? gridMovie.getDuration_minute() + " min" : "Duration unavailable" %>
                &middot; <%= ViewUtils.h(ViewUtils.text(gridMovie.getAge_rating(), "Unclassified")) %></p>
            <p class="movie-card-meta">Release: <%= ViewUtils.date(gridMovie.getAvailable_from()) %></p>
        </div>
    </a>
<%  } } %>
</div>
