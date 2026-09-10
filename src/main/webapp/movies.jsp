<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.movieweb.model.Movies" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">
    <title>Movies - PhnetPhlyx</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/styles/main.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/styles/movies.css">
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
        <!-- Movies Header -->
        <section class="movies-header">
            <h1>Movies</h1>
            <p>
                Discover movies currently showing,
                coming soon, and popular movies.
            </p>
        </section>

        <!--Search & Filters-->
        <section class="movie-filters">
            <!-- Search -->
            <div class="movie-search">
                <input
                    type="text"
                    name="search"
                    placeholder="Search by movie, actor, director, or author...">
                <button type="button">
                    Search
                </button>
            </div>

            <!-- Filters -->
            <div class="movie-filter-options">
                <div class="movie-filter">
                    <label for="genre">
                        Genre
                    </label>
                    <select id="genre"
                            name="genre">
                        <option value="">
                            All Genres
                        </option>
                        <!-- Database options will be added later -->
                    </select>
                </div>
                <div class="movie-filter">
                    <label for="tag">
                        Tags
                    </label>
                    <select id="tag"
                            name="tag">
                        <option value="">
                            All Tags
                        </option>
                        <!-- Database options will be added later -->
                    </select>
                </div>
                <div class="movie-filter">
                    <label for="theater">
                        Theater
                    </label>
                    <select id="theater"
                            name="theater">
                        <option value="">
                            All Theaters
                        </option>
                        <!-- Database options will be added later -->
                    </select>
                </div>
                <div class="movie-filter">
                    <label for="price">
                        Ticket Price
                    </label>
                    <select id="price"
                            name="price">
                        <option value="">
                            Any Price
                        </option>
                        <!-- Price ranges can be added later -->
                    </select>
                </div>
                <div class="movie-filter">
                    <label for="date">
                        Date
                    </label>
                    <input
                        type="date"
                        id="date"
                        name="date">
                </div>
                <div class="movie-filter">
                    <label for="age-rating">
                        Age Rating
                    </label>
                    <select id="age-rating"
                            name="age-rating">
                        <option value="">
                            All Ratings
                        </option>
                        <!-- Database options can be added later -->
                    </select>
                </div>
                <div class="movie-filter">
                    <label for="rating">
                        Minimum Rating
                    </label>
                    <select id="rating"
                            name="rating">
                        <option value="">
                            Any Rating
                        </option>
                        <!-- Rating options can be added later -->
                    </select>
                </div>
                <div class="movie-filter">
                    <label for="sort">
                        Sort By
                    </label>
                    <select id="sort"
                            name="sort">
                        <option value="">
                            Default
                        </option>
                        <!-- Sorting options can be added later -->
                    </select>
                </div>
            </div>

            <!-- Filter Buttons -->
            <div class="movie-filter-actions">
                <button
                    type="button"
                    class="clear-filter">
                    Clear Filters
                </button>
                <button
                    type="button"
                    class="apply-filter">
                    Apply Filters
                </button>
            </div>
        </section>

        <!--Now Showing-->
        <section class="movie-section">
            <div class="section-header">
                <h2>Now Showing</h2>
                <span class="movie-count">
                    <%
                        List<Movies> nowShowingMovies =
                            (List<Movies>) request.getAttribute("nowShowingMovies");
                        if (nowShowingMovies != null) {
                            out.print(nowShowingMovies.size());
                        } 
                        else {
                            out.print(0);
                        }
                    %>
                    movies
                </span>
            </div>
            <div class="movie-grid">
                <%
                    if (nowShowingMovies != null) {
                        for (Movies movie : nowShowingMovies) {
			                %>
			                    <a href="${pageContext.request.contextPath}/movie_details.jsp?movie_id=<%= movie.getMovie_id() %>"
			                       class="movie-card">
			                        <div class="movie-poster">
			                            <img
			                                src="${pageContext.request.contextPath}/<%= movie.getPoster_path() %>"
			                                alt="<%= movie.getMovie_name() %>">
			                        </div>
			                        <div class="movie-card-content">
			                            <h3>
			                                <%= movie.getMovie_name() %>
			                            </h3>
			                            <p class="movie-rating">
			                                ★ <%= movie.getAvg_rating() %>
			                            </p>
			                            <p class="movie-info">
			                                <%= movie.getDuration_minute() %> min
			                            </p>
			                        </div>
			                    </a>
			                <%
                        }
                    }
                %>
            </div>
        </section>

        <!--Coming Soon-->
        <section class="movie-section">
            <div class="section-header">
                <h2>Coming Soon</h2>
                <span class="movie-count">
                    <%
                        List<Movies> comingSoonMovies =
                            (List<Movies>) request.getAttribute("comingSoonMovies");
                        if (comingSoonMovies != null) {
                            out.print(comingSoonMovies.size());
                        } 
                        else {
                            out.print(0);
                        }
                    %>
                    movies
                </span>
            </div>
            <div class="movie-grid">
                <%
                    if (comingSoonMovies != null) {
                        for (Movies movie : comingSoonMovies) {
			                %>
			                    <a href="${pageContext.request.contextPath}/movie_details.jsp?movie_id=<%= movie.getMovie_id() %>"
			                       class="movie-card">
			                        <div class="movie-poster">
			                            <img
			                                src="${pageContext.request.contextPath}/<%= movie.getPoster_path() %>"
			                                alt="<%= movie.getMovie_name() %>">
			                        </div>
			                        <div class="movie-card-content">
			                            <h3>
			                                <%= movie.getMovie_name() %>
			                            </h3>
			                            <p class="movie-rating">
			                                ★ <%= movie.getAvg_rating() %>
			                            </p>
			                            <p class="movie-info">
			                                <%= movie.getDuration_minute() %> min
			                            </p>
			                        </div>
			                    </a>
			                <%
                        }
                    }
                %>
            </div>
        </section>

        <!--Popular Movies-->
        <section class="movie-section">
            <div class="section-header">
                <h2>Popular Movies</h2>
                <span class="movie-count">
                    <%
                        List<Movies> popularMovies =
                            (List<Movies>) request.getAttribute("popularMovies");
                        if (popularMovies != null) {
                            out.print(popularMovies.size());
                        } 
                        else {
                            out.print(0);
                        }
                    %>
                    movies
                </span>
            </div>
            <div class="movie-grid">
                <%
                    if (popularMovies != null) {
                        for (Movies movie : popularMovies) {
			                %>
			                    <a href="${pageContext.request.contextPath}/movie_details.jsp?movie_id=<%= movie.getMovie_id() %>"
			                       class="movie-card">
			                        <div class="movie-poster">
			                            <img
			                                src="${pageContext.request.contextPath}/<%= movie.getPoster_path() %>"
			                                alt="<%= movie.getMovie_name() %>">
			                        </div>
			                        <div class="movie-card-content">
			                            <h3>
			                                <%= movie.getMovie_name() %>
			                            </h3>
			                            <p class="movie-rating">
			                                ★ <%= movie.getAvg_rating() %>
			                            </p>
			                            <p class="movie-info">
			                                <%= movie.getDuration_minute() %> min
			                            </p>
			                        </div>
			                    </a>
			                <%
                        }
                    }
                %>
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
