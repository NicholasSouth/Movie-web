<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.movieweb.model.Movies" %>
<%@ page import="com.movieweb.model.Genres" %>
<%@ page import="com.movieweb.model.Tags" %>
<%@ page import="com.movieweb.model.Actors" %>
<%@ page import="com.movieweb.model.Directors" %>
<%@ page import="com.movieweb.model.Authors" %>
<%@ page import="com.movieweb.model.Users" %>

<%@ page import="java.text.SimpleDateFormat" %>

<%
    request.setAttribute("currentPage", "movies");
    Movies movie = (Movies) request.getAttribute("movie");
    if (movie == null) {
        response.sendError(
                HttpServletResponse.SC_NOT_FOUND,
                "Movie not found."
        );
        return;
    }
    List<Genres> genres = movie.getGenres();
    List<Tags> tags = movie.getTags();
    String contextPath = request.getContextPath();
    String posterPath = movie.getPoster_path();
    String trailerPath = movie.getTrailer_path();
    String trailerLink = movie.getTrailer_link();
    List<Actors> actors = (List<Actors>) request.getAttribute("actors");
    if (actors == null) {
        actors = new java.util.ArrayList<>();
    }
    List<Directors> directors = (List<Directors>) request.getAttribute("directors");
    if (directors == null) {
        directors = new java.util.ArrayList<>();
    }
    List<Authors> authors = (List<Authors>) request.getAttribute("authors");
    if (authors == null) {
        authors = new java.util.ArrayList<>();
    }
    Users loggedInUser = (Users) request.getAttribute("loggedInUser");
    Boolean favouriteAttribute = (Boolean) request.getAttribute("isFavourite");
    boolean isFavourite = favouriteAttribute != null && favouriteAttribute;
    String csrfToken = (String) request.getAttribute("csrfToken");
    String favouriteMessage = (String) request.getAttribute("favouriteMessage");
%>
<%
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");;
    String releaseDate = movie.getAvailable_from() != null
            ? dateFormat.format(movie.getAvailable_from())
            : "Not available.";
%>

<!DOCTYPE html>
<html>
	<head>
	    <meta charset="UTF-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	    <title>
	        <%= movie.getMovie_name() %> - PhnetPhlyx
	    </title>
	    <link rel="stylesheet"
	          href="<%= contextPath %>/styles/main.css">
	    <link rel="stylesheet"
	          href="<%= contextPath %>/styles/movie_details.css">
	</head>
	
	<body>
		<section class="Layout1">
		    <%@ include file="components/header.jsp" %>
		    <%@ include file="components/left_sidebar.jsp" %>
		
		    <main class="MainBody">
		        <% if (favouriteMessage != null
		                && !favouriteMessage.isEmpty()) { %>
		            <div class="favourite-message">
		                <%= favouriteMessage %>
		            </div>
		        <% } %>
		
		        <!-- Movie Information -->
		        <section class="movie-info">
		            <div class="movie-poster">
		                <% if (posterPath != null
		                        && !posterPath.isEmpty()) { %>
		                    <img src="<%= contextPath %>/<%= posterPath %>"
		                         alt="<%= movie.getMovie_name() %>">
		                <% } 
		                else { %>
		                    <div class="poster-placeholder">
		                        Poster unavailable.
		                    </div>
		                <% } %>
		            </div>
		
		            <div class="movie-information">
		                <h1>
		                    <%= movie.getMovie_name() %>
		                </h1>
		
		                <!-- Rating -->
		                <div class="movie-rating">
		                    <img src="<%= contextPath %>/pictures/assessments/star.png"
		                         alt="Rating"
		                         class="rating-star">
		                    <span>
		                        <%= movie.getAvg_rating() %> / 10
		                    </span>
		                </div>
		
		                <!-- Genres -->
		                <div class="movie-meta-group">
		                    <strong>Genres:</strong>
		                    <div class="movie-tags">
		                        <% if (genres != null
		                                && !genres.isEmpty()) { %>
		                            <% for (Genres genre : genres) { %>
		                                <a href="<%= contextPath %>/movies?genre=<%= genre.getGenre_id() %>"
										   class="genre-tag">										
										    <%= genre.getGenre_name() %>								
										</a>
		                            <% } %>
		                        <% } 
		                        else { %>
		                            <span class="empty-meta">
		                                No genres available.
		                            </span>
		                        <% } %>
		                    </div>
		                </div>
		
		                <!-- Tags -->
		                <div class="movie-meta-group">
		                    <strong>Tags:</strong>
		                    <div class="movie-tags">
		                        <% if (tags != null
		                                && !tags.isEmpty()) { %>
		                            <% for (Tags tag : tags) { %>
		                                <a href="<%= contextPath %>/movies?tag=<%= tag.getTag_id() %>"
										   class="movie-tag">										
										    #<%= tag.getTag_name() %>										
										</a>
		                            <% } %>
		                        <% } 
		                        else { %>
		                            <span class="empty-meta">
		                                No tags available.
		                            </span>
		                        <% } %>
		                    </div>
		                </div>
		
		                <!-- Movie Metadata -->
		                <!-- Directors -->
						<div class="movie-meta-group">
						    <strong>Directors:</strong>						
						    <% 
						    	if (directors.isEmpty()) { 
						    %>
						        	<span class="empty-meta">Not available.</span>
						    <% 
						    	} 
						    	else { 
						    %>
						        <div class="movie-people">
						            <% 
						            	for (Directors director : directors) { 
						            %>
							                <span class="person-name">
							                    <%= director.getDirector_name() %>
							                </span>
						            <% 
						            	} 
						            %>
						        </div>
						    <% 
						    	} 
						    %>
						</div>
						
						<!-- Authors -->
						<div class="movie-meta-group">
						    <strong>Authors:</strong>						
						    <% 
						    	if (authors.isEmpty()) { 
						    %>
						        	<span class="empty-meta">Not available.</span>
						    <% 
						    	} 
						    	else { 
						    %>
						        <div class="movie-people">
						            <% 
						            	for (Authors author : authors) { 
						            %>
							                <span class="person-name">
							                    <%= author.getAuthor_name() %>
							                </span>
						            <% 
						            	} 
						            %>
						        </div>
						    <% 
						    	} 
						    %>
						</div>
						
						<!-- Actors -->
						<div class="movie-meta-group">
						    <strong>Actors:</strong>						
						    <% 
						    	if (actors.isEmpty()) { 
						    %>
						        	<span class="empty-meta">Not available.</span>
						    <% 
						    	} 
						    	else { 
						    %>
						        <div class="movie-people">
						            <% 
						            	for (Actors actor : actors) { 
						            %>
							                <span class="person-name">
							                    <%= actor.getActor_name() %>
							                </span>
						            <% 
						            	} 
						            %>
						        </div>
						    <% 
						    	} 
						    %>
						</div>
		                <p>
		                    <strong>Duration:</strong>
		                    <%= movie.getDuration_minute() %> minutes
		                </p>
		                <p>
		                    <strong>Age Rating:</strong>
		                    <%= movie.getAge_rating() != null
		                            ? movie.getAge_rating()
		                            : "Not available." %>
		                </p>
		                <p>
		                	<strong>
		                		Release Date:
		                	</strong> 
		                	<%= releaseDate %>
		                </p>
		
		                <!-- Actions -->
		                <div class="movie-actions">
		                    <% if (loggedInUser != null) { %>
							    <form action="<%= contextPath %>/favorite"
							          method="post"
							          class="favorite-form">						
							        <input type="hidden"
							               name="movieId"
							               value="<%= movie.getMovie_id() %>">						
							        <input type="hidden"
							               name="action"
							               value="<%= isFavourite ? "remove" : "add" %>">						
							        <input type="hidden"
							               name="csrfToken"
							               value="<%= csrfToken %>">					
							        <button type="submit"
							                class="favorite-button<%= isFavourite ? " active" : "" %>">							
							            <img src="<%= contextPath %>/pictures/assessments/heart.png"
							                 alt="Favorite"
							                 class="favorite-icon">					
							            <span>
							                <%= isFavourite
							                        ? "Remove from Favorites"
							                        : "Add to Favorites" %>
							            </span>						
							        </button>
							    </form>							
							<% } 
		                    else { %>							
							    <button type="button"
							            class="favorite-button"
							            onclick="window.location.href='<%= contextPath %>/log_in.jsp'">							
							        <img src="<%= contextPath %>/pictures/assessments/heart.png"
							             alt="Login"
							             class="favorite-icon">							
							        <span>Login to Add Favorites</span>
							    </button>							
							<% } %>
		                    <button type="button"
		                            class="booking-button">
		                        Book Ticket
		                    </button>
		                </div>
		            </div>
		        </section>
		
		        <!-- Description -->
		        <section class="movie-section">
		            <h2>Description</h2>
		            <p>
		                <%= movie.getDescription() != null
		                        ? movie.getDescription()
		                        : "No description available." %>
		            </p>
		        </section>
		
		        <!-- Trailer -->
		        <section class="movie-section">
		            <h2>Trailer</h2>
		            <div class="trailer-container">
		                <% if (trailerPath != null
		                        && !trailerPath.isEmpty()) { %>
		                    <div class="trailer-placeholder">
		                        <% if (trailerLink != null
		                                && !trailerLink.isEmpty()) { %>
		                            <a href="<%= trailerLink %>"
		                               class="trailer-link"
		                               target="_blank"
		                               rel="noopener noreferrer">
		                                <img src="<%= contextPath %>/<%= trailerPath %>"
		                                     alt="<%= movie.getMovie_name() %> Trailer"
		                                     class="trailer-image">
		                            </a>
		                        <% } 
		                        else { %>
		                            <img src="<%= contextPath %>/<%= trailerPath %>"
		                                 alt="<%= movie.getMovie_name() %> Trailer"
		                                 class="trailer-image">
		                        <% } %>
		                    </div>
		                <% } 
		                else { %>
		                    <p class="empty-meta">
		                        Trailer unavailable.
		                    </p>
		                <% } %>
		            </div>
		        </section>
		
		        <!-- Showtimes -->
		        <section class="movie-section">
		            <h2>Available Showtimes</h2>
		            <div class="showtime-card">
		                <h3>Theater</h3>
		                <p>Date</p>
		                <div class="showtimes">
		                    Showtimes will appear here.
		                </div>
		            </div>
		        </section>
		
		        <!-- Reviews -->
		        <section class="movie-section">
		            <h2>Reviews</h2>
		            <div class="review">
		                <strong>User</strong>
		                <span>⭐⭐⭐⭐⭐</span>
		                <p>
		                    Reviews will appear here.
		                </p>
		            </div>
		        </section>
		    </main>
		
		    <%@ include file="components/right_sidebar.jsp" %>
		
		    <%@ include file="components/footer.jsp" %>
		
		</section>
	</body>
</html>