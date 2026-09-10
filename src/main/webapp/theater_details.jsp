<%@ page import="java.util.List" %>
<%@ page import="com.movieweb.model.Theaters" %>
<%@ page import="com.movieweb.model.Rooms" %>
<%@ page import="com.movieweb.model.Movies" %>

<%
    Theaters theater =(Theaters) request.getAttribute("theater");
    List<Rooms> rooms =(List<Rooms>) request.getAttribute("rooms");
    List<Movies> currentlyShowingMovies =(List<Movies>) request.getAttribute("currentlyShowingMovies");
    List<Movies> upcomingMovies =(List<Movies>) request.getAttribute("upcomingMovies");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">
    <title>
        <%= theater != null
            ? theater.getTheater_name() + " - PhnetPhlyx"
            : "Theater Details - PhnetPhlyx" %>
    </title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/styles/main.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/styles/theater_details.css">
</head>

<body>
<section class="Layout1">

    <!-- Header -->
    <%@ include file="components/header.jsp" %>

    <!-- Left Sidebar -->
    <%
        request.setAttribute("currentPage", "theaters");
    %>
    <%@ include file="components/left_sidebar.jsp" %>

    <!-- Main Content -->
    <main class="MainBody">

        <%
            if (theater != null) {
        %>

        <!--Theater Information-->
        <section class="theater-details">
            <div class="theater-details-image">
                <img
                    src="${pageContext.request.contextPath}/<%= theater.getTheater_image_path() %>"
                    alt="<%= theater.getTheater_name() %>">
            </div>
            <div class="theater-details-information">
                <h1>
                    <%= theater.getTheater_name() %>
                </h1>
                <p>
                    <strong>Address:</strong>
                    <%= theater.getTheater_address() %>
                </p>
                <p>
                    <strong>Opening Hours:</strong>
                    <%= theater.getOpen_time() != null
                        ? theater.getOpen_time()
                        : "Not available" %>
                    -
                    <%= theater.getClosing_time() != null
                        ? theater.getClosing_time()
                        : "Not available" %>
                </p>
                <div class="theater-actions">
                    <button
                        type="button"
                        class="location-button">
                        View Location
                    </button>
                    <a
                        href="${pageContext.request.contextPath}/movies.jsp?theater_id=<%= theater.getTheater_id() %>"
                        class="booking-button">
                        Find Showtimes
                    </a>
                </div>
            </div>
        </section>

        <!--About This Theater-->
        <section class="theater-section">
            <h2>
                About This Theater
            </h2>
            <%
                if (theater.getDescription() != null
                        && !theater.getDescription().isEmpty()) {
		            %>
		                <p>
		                    <%= theater.getDescription() %>
		                </p>
		
		            <%
                } 
                else {
		            %>
		                <p>
		                    No description available.
		                </p>
		            <%
                }
            %>
        </section>

        <!--Available Rooms -->
        <section class="theater-section">
            <h2>
                Available Rooms
            </h2>
            <div class="room-grid">
                <%
                    if (rooms != null && !rooms.isEmpty()) {
                        for (Rooms room : rooms) {
			                %>
			                    <article class="room-card">
			                        <h3>
			                            <%= room.getRoom_name() %>
			                        </h3>
			                        <p>
			                            Room Type:
			                            <%= room.getRoom_type_id() %>
			                        </p>
			                    </article>
			                <%
                        }
                    } 
                    else {
		                %>
		                    <p class="no-data">
		                        No available rooms.
		                    </p>
		                <%
                    }
                %>
            </div>
        </section>

        <!--Currently Showing-->
        <section class="theater-section">
            <h2>
                Currently Showing
            </h2>
            <div class="movie-list">
                <%
                    if (currentlyShowingMovies != null
                            && !currentlyShowingMovies.isEmpty()) {
                        int movieCount = 0;
                        for (Movies movie : currentlyShowingMovies) {
                            if (movieCount >= 3) {
                                break;
                            }
                            movieCount++;
			                %>
			                    <article class="showing-movie">
			                        <img
			                            src="${pageContext.request.contextPath}/<%= movie.getPoster_path() %>"
			                            alt="<%= movie.getMovie_name() %>">
			                        <div class="showing-movie-content">
			                            <h3>
			                                <%= movie.getMovie_name() %>
			                            </h3>
			                            <p>
			                                <%= movie.getDuration_minute() %> min
			                                ·
			                                <%= movie.getAge_rating() %>
			                            </p>
			                            <a
			                                href="${pageContext.request.contextPath}/movie_details.jsp?movie_id=<%= movie.getMovie_id() %>"
			                                class="movie-button">
			                                View Movie
			                            </a>
			                        </div>
			                    </article>
			                <%
                        }
                    } 
                    else {
		                %>
		                    <p class="no-data">
		                        No movies are currently showing.
		                    </p>
		
		                <%
                    }
                %>
            </div>
        </section>

        <!--Upcoming Movies-->
        <section class="theater-section">
            <h2>
                Upcoming Movies
            </h2>
            <div class="upcoming-list">
                <%
                    if (upcomingMovies != null
                            && !upcomingMovies.isEmpty()) {
                        int movieCount = 0;
                        for (Movies movie : upcomingMovies) {
                            if (movieCount >= 3) {
                                break;
                            }
                            movieCount++;
			                %>
			                    <article class="upcoming-movie">
			                        <h3>
			                            <%= movie.getMovie_name() %>
			                        </h3>
			                        <p>
			                            Coming
			                            <%= movie.getAvailable_from() %>
			                        </p>
			                        <a
			                            href="${pageContext.request.contextPath}/movie_details.jsp?movie_id=<%= movie.getMovie_id() %>"
			                            class="movie-button">
			                            View Movie
			                        </a>
			                    </article>
			                <%
                        }
                    } 
                    else {
		                %>
		                    <p class="no-data">
		                        No upcoming movies.
		                    </p>
		
		                <%
                    }
                %>
            </div>
        </section>
        <%
            } 
            else {
		        %>
		        <!--Theater Not Found-->
		        <section class="theater-section">
		            <h2>
		                Theater Not Found
		            </h2>
		            <p>
		                The requested theater could not be found.
		            </p>
		        </section>
		        <%
            }
        %>
    </main>

    <!-- Right Sidebar -->
    <%@ include file="components/right_sidebar.jsp" %>

    <!-- Footer -->
    <%@ include file="components/footer.jsp" %>

</section>
</body>
</html>