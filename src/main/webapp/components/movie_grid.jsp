<%@ page import="java.util.List" %>
<%@ page import="com.movieweb.model.Movies" %>

<div class="movie-grid">
<%
    List<Movies> movies = (List<Movies>) request.getAttribute("movieGridMovies");
    if (movies != null) {
        for (Movies movie : movies) {
			%>
	            <a href="<%= request.getContextPath() %>/movie-details?id=<%= movie.getMovie_id() %>"
	               class="movie-card">
	                <img
	                    src="<%= request.getContextPath() %>/<%= movie.getPoster_path() %>"
	                    alt="<%= movie.getMovie_name() %>">
	                <h3>
	                    <%= movie.getMovie_name() %>
	                </h3>
	            </a>
			<%
        }
    }
%>
</div>