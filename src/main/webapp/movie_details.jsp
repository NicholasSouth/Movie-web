<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.movieweb.model.Movies" %>
<%@ page import="com.movieweb.model.Genres" %>
<%@ page import="com.movieweb.model.Tags" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">
    <title>Movie Details - PhnetPhlyx</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/styles/main.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/styles/movie_details.css">
</head>

<body>
<section class="Layout1">
    <!--Header-->
    <%@ include file="components/header.jsp" %>

    <!--Left Sidebar-->
    <%
        request.setAttribute("currentPage", "movies");
    %>
    <%@ include file="components/left_sidebar.jsp" %>

    <!--Main Body-->
    <main class="MainBody">
        <!--Movie Information-->
        <%
            Movies movie =(Movies) request.getAttribute("movie");
            List<Genres> genres =(List<Genres>) request.getAttribute("genres");
            List<Tags> tags =(List<Tags>) request.getAttribute("tags");
        %>
        <section class="movie-info">

            <!-- Movie Poster -->
            <div class="movie-poster">
                <img
                    src="${pageContext.request.contextPath}/<%= movie.getPoster_path() %>"
                    alt="<%= movie.getMovie_name() %>">
            </div>

            <!-- Movie Information -->
            <div class="movie-information">
                <h1>
                    <%= movie.getMovie_name() %>
                </h1>

                <!-- Rating -->
                <div class="movie-rating">
                    ⭐ <%= movie.getAvg_rating() %> / 10
                </div>

                <!--Genres-->
                <div class="movie-meta-group">
                    <strong>
                        Genres:
                    </strong>
                    <div class="movie-tags">
                        <%
                            if (genres != null) {
                                for (Genres genre : genres) {
			                        %>
			                            <a
			                                href="${pageContext.request.contextPath}/movies.jsp?genre=<%= genre.getGenre_name() %>"
			                                class="genre-tag">
			                                <%= genre.getGenre_name() %>
			                            </a>
			                        <%
                                }
                            }
                        %>
                    </div>
                </div>

                <!--Tags-->
                <div class="movie-meta-group">
                    <strong>
                        Tags:
                    </strong>
                    <div class="movie-tags">
                        <%
                            if (tags != null) {
                                for (Tags tag : tags) {
			                        %>
			                            <a
			                                href="${pageContext.request.contextPath}/movies.jsp?tag=<%= tag.getTag_name() %>"
			                                class="movie-tag">
			                                <%= tag.getTag_name() %>
			                            </a>
			                        <%
                                }
                            }
                        %>
                    </div>
                </div>

                <!-- Director -->
                <p>
                    <strong>
                        Director:
                    </strong>
                    <%= request.getAttribute("directors") != null
                        ? request.getAttribute("directors")
                        : "" %>
                </p>

                <!-- Actors -->
                <p>
                    <strong>
                        Actors:
                    </strong>
                    <%= request.getAttribute("actors") != null
                        ? request.getAttribute("actors")
                        : "" %>
                </p>

                <!-- Duration -->
                <p>
                    <strong>
                        Duration:
                    </strong>
                    <%= movie.getDuration_minute() %> minutes
                </p>

                <!-- Age Rating -->
                <p>
                    <strong>
                        Age Rating:
                    </strong>
                    <%= movie.getAge_rating() %>
                </p>

                <!-- Release Date -->
                <p>
                    <strong>
                        Release Date:
                    </strong>
                    <%= movie.getAvailable_from() %>
                </p>

                <!-- Actions -->
                <div class="movie-actions">
                    <button
                        type="button"
                        class="favorite-button">
                        ♡ Add to Favorites
                    </button>
                    <button
                        type="button"
                        class="booking-button">
                        Book Ticket
                    </button>
                </div>
            </div>
        </section>

        <!--Description-->
        <section class="movie-section">
            <h2>
                Description
            </h2>
            <p>
                <%= movie.getDescription() %>
            </p>
        </section>

        <!--Trailer-->
        <section class="movie-section">
            <h2>
                Trailer
            </h2>
            <div class="trailer-container">
                <div class="trailer-placeholder">
                    <a
                        href="<%= movie.getTrailer_path() %>"
                        class="trailer-link"
                        target="_blank">
                        <img
                            src="${pageContext.request.contextPath}/test_movie_details/maxresdefault.jpg"
                            alt="Movie Trailer"
                            class="trailer-image">
                    </a>
                </div>
            </div>
        </section>

        <!--Showtimes-->
        <section class="movie-section">
            <h2>
                Available Showtimes
            </h2>
            <!--Showtimes should be implement later-->
            <div class="showtime-card">
                <h3>
                    Theater
                </h3>
                <p>
                    Date
                </p>
                <div class="showtimes">
                    <!-- Showtime buttons will be added later -->
                </div>
            </div>
        </section>
        
        <!--Reviews-->
        <section class="movie-section">
            <h2>
                Reviews
            </h2>
            <!--Reviews should be implement later-->
            <div class="review">
                <strong>
                    User
                </strong>
                <span>
                    ⭐⭐⭐⭐⭐
                </span>
                <p>
                    Reviews will appear here.
                </p>
            </div>
        </section>
    </main>

    <!--Right Sidebar-->
    <%@ include file="components/right_sidebar.jsp" %>

    <!--Footer-->
    <%@ include file="components/footer.jsp" %>
</section>
</body>
</html>