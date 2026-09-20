<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.movieweb.model.*,com.movieweb.util.ViewUtils" %>
<%
    MovieCatalog catalog = (MovieCatalog) request.getAttribute("catalog");
    MovieFilter filter = (MovieFilter) request.getAttribute("filter");
    request.setAttribute("currentPage", "movies");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Movies - PhnetPhlyx</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/movies.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/movie-data.css">
</head>
<body>
<section class="Layout1 movie-layout">
    <%@ include file="../../components/header.jsp" %>
    <%@ include file="../../components/left_sidebar.jsp" %>
    <main class="MainBody">
        <section class="movies-header">
            <h1>Movies</h1>
            <p>Discover movies currently showing, coming soon, and popular movies.</p>
        </section>
        <form class="movie-filters" action="${pageContext.request.contextPath}/movies" method="get">
            <div class="movie-search">
                <label class="sr-only" for="movie-search">Search by movie, actor, director, or author</label>
                <input id="movie-search" type="search" name="search" maxlength="200"
                       value="<%= ViewUtils.h(filter.getSearch()) %>"
                       placeholder="Search by movie, actor, director, or author...">
                <button type="submit">Search</button>
            </div>
            <div class="movie-filter-options">
                <div class="movie-filter">
                    <label for="genre">Genre</label>
                    <select id="genre" name="genre">
                        <option value="">All Genres</option>
                        <% for (Genres genre : catalog.getGenres()) { %>
                        <option value="<%= genre.getGenre_id() %>" <%= Integer.valueOf(genre.getGenre_id()).equals(filter.getGenreId()) ? "selected" : "" %>><%= ViewUtils.h(genre.getGenre_name()) %></option>
                        <% } %>
                    </select>
                </div>
                <div class="movie-filter">
                    <label for="tag">Tag</label>
                    <select id="tag" name="tag">
                        <option value="">All Tags</option>
                        <% for (Tags tag : catalog.getTags()) { %>
                        <option value="<%= tag.getTag_id() %>" <%= Integer.valueOf(tag.getTag_id()).equals(filter.getTagId()) ? "selected" : "" %>><%= ViewUtils.h(tag.getTag_name()) %></option>
                        <% } %>
                    </select>
                </div>
                <div class="movie-filter">
                    <label for="theater">Theater</label>
                    <select id="theater" name="theater">
                        <option value="">All Theaters</option>
                        <% for (Theaters theater : catalog.getTheaters()) { %>
                        <option value="<%= theater.getTheater_id() %>" <%= Integer.valueOf(theater.getTheater_id()).equals(filter.getTheaterId()) ? "selected" : "" %>><%= ViewUtils.h(theater.getTheater_name()) %></option>
                        <% } %>
                    </select>
                </div>
                <div class="movie-filter">
                    <label for="date">Showtime Date</label>
                    <input type="date" id="date" name="date" value="<%= ViewUtils.h(filter.getDate()) %>">
                </div>
                <div class="movie-filter">
                    <label for="age-rating">Age Rating</label>
                    <select id="age-rating" name="age-rating">
                        <option value="">All Age Ratings</option>
                        <% for (String ageRating : catalog.getAgeRatings()) { %>
                        <option value="<%= ViewUtils.h(ageRating) %>" <%= ageRating.equals(filter.getAgeRating()) ? "selected" : "" %>><%= ViewUtils.h(ageRating) %></option>
                        <% } %>
                    </select>
                </div>
                <div class="movie-filter">
                    <label for="rating">Minimum Rating (0–10)</label>
                    <input type="number" id="rating" name="rating" min="0" max="10" step="0.1"
                           placeholder="Any rating" value="<%= ViewUtils.h(filter.getMinRating()) %>">
                </div>
                <div class="movie-filter">
                    <label for="status">Availability</label>
                    <select id="status" name="status">
                        <option value="all" <%= "all".equals(filter.getStatus()) ? "selected" : "" %>>All Movies</option>
                        <option value="now-showing" <%= "now-showing".equals(filter.getStatus()) ? "selected" : "" %>>Now Showing</option>
                        <option value="coming-soon" <%= "coming-soon".equals(filter.getStatus()) ? "selected" : "" %>>Coming Soon</option>
                    </select>
                </div>
                <div class="movie-filter">
                    <label for="sort">Sort By</label>
                    <select id="sort" name="sort">
                        <option value="newest" <%= "newest".equals(filter.getSort()) ? "selected" : "" %>>Newest Release</option>
                        <option value="rating" <%= "rating".equals(filter.getSort()) ? "selected" : "" %>>Highest Rating</option>
                        <option value="popular" <%= "popular".equals(filter.getSort()) ? "selected" : "" %>>Most Favorited</option>
                        <option value="name" <%= "name".equals(filter.getSort()) ? "selected" : "" %>>Title A–Z</option>
                        <option value="duration" <%= "duration".equals(filter.getSort()) ? "selected" : "" %>>Shortest Duration</option>
                    </select>
                </div>
            </div>
            <div class="movie-filter-actions">
                <a class="clear-filter" href="${pageContext.request.contextPath}/movies">Clear Filters</a>
                <button type="submit" class="apply-filter">Apply Filters</button>
            </div>
        </form>
        <% if (!filter.isHasFilters() && catalog.getPage() == 1) { %>
        <section class="movie-section">
            <div class="section-header"><h2>Now Showing</h2><a class="section-link" href="${pageContext.request.contextPath}/movies?status=now-showing">View all</a></div>
            <% request.setAttribute("movieGridMovies", catalog.getNowShowing()); %>
            <jsp:include page="/components/movie_grid.jsp" />
        </section>
        <section class="movie-section">
            <div class="section-header"><h2>Coming Soon</h2><a class="section-link" href="${pageContext.request.contextPath}/movies?status=coming-soon">View all</a></div>
            <% request.setAttribute("movieGridMovies", catalog.getComingSoon()); %>
            <jsp:include page="/components/movie_grid.jsp" />
        </section>
        <section class="movie-section">
            <div class="section-header"><h2>Popular Movies</h2><a class="section-link" href="${pageContext.request.contextPath}/movies?sort=popular">View all</a></div>
            <% request.setAttribute("movieGridMovies", catalog.getPopular()); %>
            <jsp:include page="/components/movie_grid.jsp" />
        </section>
        <% } %>
        <section class="movie-section" id="catalog-results">
            <div class="section-header">
                <h2><%= filter.isHasFilters() ? "Search Results" : "All Movies" %></h2>
                <span class="movie-count"><%= catalog.getTotal() %> movies</span>
            </div>
            <% request.setAttribute("movieGridMovies", catalog.getMovies()); %>
            <jsp:include page="/components/movie_grid.jsp" />
            <% if (catalog.getTotalPages() > 1) {
                String pageLink = request.getContextPath() + "/movies?search=" + ViewUtils.query(filter.getSearch())
                        + "&genre=" + ViewUtils.query(filter.getGenreId()) + "&tag=" + ViewUtils.query(filter.getTagId())
                        + "&theater=" + ViewUtils.query(filter.getTheaterId()) + "&date=" + ViewUtils.query(filter.getDate())
                        + "&age-rating=" + ViewUtils.query(filter.getAgeRating()) + "&rating=" + ViewUtils.query(filter.getMinRating())
                        + "&sort=" + ViewUtils.query(filter.getSort()) + "&status=" + ViewUtils.query(filter.getStatus()) + "&page=";
            %>
            <nav class="movie-pagination" aria-label="Movie pages">
                <% if (catalog.isHasPrevious()) { %><a href="<%= ViewUtils.h(pageLink + (catalog.getPage() - 1)) %>#catalog-results">Previous</a><% } %>
                <span>Page <%= catalog.getPage() %> of <%= catalog.getTotalPages() %></span>
                <% if (catalog.isHasNext()) { %><a href="<%= ViewUtils.h(pageLink + (catalog.getPage() + 1)) %>#catalog-results">Next</a><% } %>
            </nav>
            <% } %>
        </section>
    </main>
    <%@ include file="../../components/right_sidebar.jsp" %>
    <%@ include file="../../components/footer.jsp" %>
</section>
<script src="${pageContext.request.contextPath}/scripts/movie-media.js" defer></script>
</body>
</html>
