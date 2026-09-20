<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.movieweb.model.*,com.movieweb.util.ViewUtils" %>
<%
    MovieDetails details = (MovieDetails) request.getAttribute("details");
    Movies movie = details.getMovie();
    String trailerUrl = ViewUtils.externalUrl(movie.getTrailer_link());
    request.setAttribute("currentPage", "movies");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= ViewUtils.h(movie.getMovie_name()) %> - PhnetPhlyx</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/movie_details.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/movie-data.css">
</head>
<body>
<section class="Layout1 movie-layout">
    <%@ include file="../../components/header.jsp" %>
    <%@ include file="../../components/left_sidebar.jsp" %>
    <main class="MainBody">
        <a class="back-to-movies" href="${pageContext.request.contextPath}/movies">&larr; All movies</a>
        <% if (request.getAttribute("favouriteMessage") != null) { %>
        <p class="movie-notice" role="status"><%= ViewUtils.h(request.getAttribute("favouriteMessage")) %></p>
        <% } %>
        <section class="movie-info">
            <div class="movie-poster">
                <img src="<%= ViewUtils.h(ViewUtils.asset(request.getContextPath(), movie.getPoster_path())) %>"
                     data-fallback="${pageContext.request.contextPath}/pictures/movie-placeholder.svg"
                     alt="<%= ViewUtils.h(movie.getMovie_name()) %>" width="400" height="600">
            </div>
            <div class="movie-information">
                <h1><%= ViewUtils.h(movie.getMovie_name()) %></h1>
                <div class="movie-rating">&#9733; <%= ViewUtils.h(ViewUtils.rating(movie.getAvg_rating())) %></div>
                <div class="movie-meta-group">
                    <strong>Genres:</strong>
                    <div class="movie-tags">
                        <% if (details.getGenres().isEmpty()) { %><span class="muted">Not available</span><% } %>
                        <% for (Genres genre : details.getGenres()) { %>
                        <a class="genre-tag" href="${pageContext.request.contextPath}/movies?genre=<%= genre.getGenre_id() %>"><%= ViewUtils.h(genre.getGenre_name()) %></a>
                        <% } %>
                    </div>
                </div>
                <div class="movie-meta-group">
                    <strong>Tags:</strong>
                    <div class="movie-tags">
                        <% if (details.getTags().isEmpty()) { %><span class="muted">Not available</span><% } %>
                        <% for (Tags tag : details.getTags()) { %>
                        <a class="movie-tag" href="${pageContext.request.contextPath}/movies?tag=<%= tag.getTag_id() %>"><%= ViewUtils.h(tag.getTag_name()) %></a>
                        <% } %>
                    </div>
                </div>
                <p><strong>Directors:</strong>
                    <% if (details.getDirectors().isEmpty()) { %>Not available<% } %>
                    <% for (int i = 0; i < details.getDirectors().size(); i++) { %><%= i == 0 ? "" : ", " %><%= ViewUtils.h(details.getDirectors().get(i).getDirector_name()) %><% } %>
                </p>
                <p><strong>Actors:</strong>
                    <% if (details.getActors().isEmpty()) { %>Not available<% } %>
                    <% for (int i = 0; i < details.getActors().size(); i++) { %><%= i == 0 ? "" : ", " %><%= ViewUtils.h(details.getActors().get(i).getActor_name()) %><% } %>
                </p>
                <p><strong>Authors:</strong>
                    <% if (details.getAuthors().isEmpty()) { %>Not available<% } %>
                    <% for (int i = 0; i < details.getAuthors().size(); i++) { %><%= i == 0 ? "" : ", " %><%= ViewUtils.h(details.getAuthors().get(i).getAuthor_name()) %><% } %>
                </p>
                <p><strong>Duration:</strong> <%= movie.getDuration_minute() > 0 ? movie.getDuration_minute() + " minutes" : "Not available" %></p>
                <p><strong>Age Rating:</strong> <%= ViewUtils.h(ViewUtils.text(movie.getAge_rating(), "Unclassified")) %></p>
                <p><strong>Release Date:</strong> <%= ViewUtils.date(movie.getAvailable_from()) %></p>
                <% if (movie.getAvailable_until() != null) { %>
                <p><strong>Available Until:</strong> <%= ViewUtils.date(movie.getAvailable_until()) %></p>
                <% } %>
                <div class="movie-actions">
                    <% if (session.getAttribute("user") != null) { %>
                    <form action="${pageContext.request.contextPath}/movie-favourite" method="post">
                        <input type="hidden" name="movieId" value="<%= movie.getMovie_id() %>">
                        <input type="hidden" name="csrfToken" value="<%= ViewUtils.h(request.getAttribute("csrfToken")) %>">
                        <input type="hidden" name="action" value="<%= details.isFavourite() ? "remove" : "add" %>">
                        <button type="submit" class="favorite-button"><%= details.isFavourite() ? "♥ Remove from Favorites" : "♡ Add to Favorites" %></button>
                    </form>
                    <% } else { %>
                    <a class="favorite-button" href="${pageContext.request.contextPath}/log_in.jsp">Sign in to add favorites</a>
                    <% } %>
                    <a class="booking-button" href="#showtimes">View Showtimes</a>
                </div>
                <% if (session.getAttribute("user") == null) { %>
                <p class="movie-help">After signing in, return to this movie to save it to your favorites.</p>
                <% } %>
            </div>
        </section>
        <section class="movie-section">
            <h2>Description</h2>
            <p class="movie-description"><%= ViewUtils.h(ViewUtils.text(movie.getDescription(), "No description available for this movie yet.")) %></p>
        </section>
        <section class="movie-section">
            <h2>Trailer</h2>
            <% if (!trailerUrl.isEmpty()) { %>
            <a class="trailer-link" href="<%= ViewUtils.h(trailerUrl) %>" target="_blank" rel="noopener noreferrer">
                <% if (ViewUtils.hasText(movie.getTrailer_path())) { %>
                <img class="trailer-image" src="<%= ViewUtils.h(ViewUtils.asset(request.getContextPath(), movie.getTrailer_path())) %>"
                     data-fallback="${pageContext.request.contextPath}/pictures/movie-placeholder.svg"
                     alt="<%= ViewUtils.h(movie.getMovie_name()) %> trailer" loading="lazy">
                <% } %>
                <span class="trailer-action">&#9654; Watch trailer (opens a new tab)</span>
            </a>
            <% } else { %>
            <p class="movie-empty">No trailer available for this movie yet.</p>
            <% } %>
        </section>
        <section class="movie-section" id="showtimes">
            <h2>Available Showtimes</h2>
            <p class="movie-help">Times are shown in the cinema's local time.</p>
            <% if (details.getShowtimes().isEmpty()) { %>
            <p class="movie-empty" role="status">No upcoming showtimes are currently available for this movie.</p>
            <% } else { %>
            <div class="showtime-list">
            <% for (ShowtimeView showtime : details.getShowtimes()) { %>
                <article class="showtime-card">
                    <h3><%= ViewUtils.h(ViewUtils.text(showtime.getTheaterName(), "Theater")) %></h3>
                    <p class="showtime-address"><%= ViewUtils.h(showtime.getTheaterAddress()) %></p>
                    <dl class="showtime-facts">
                        <div><dt>Room</dt><dd><%= ViewUtils.h(ViewUtils.text(showtime.getRoomName(), "Not available")) %></dd></div>
                        <div><dt>Starts</dt><dd><%= ViewUtils.dateTime(showtime.getStartAt()) %></dd></div>
                        <% if (showtime.getEndAt() != null) { %><div><dt>Ends</dt><dd><%= ViewUtils.dateTime(showtime.getEndAt()) %></dd></div><% } %>
                    </dl>
                </article>
            <% } %>
            </div>
            <% } %>
        </section>
    </main>
    <%@ include file="../../components/right_sidebar.jsp" %>
    <%@ include file="../../components/footer.jsp" %>
</section>
<script src="${pageContext.request.contextPath}/scripts/movie-media.js" defer></script>
</body>
</html>
