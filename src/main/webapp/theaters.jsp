<%@ page import="java.util.List" %>
<%@ page import="com.movieweb.model.Theaters" %>

<%
    List<Theaters> theaters = (List<Theaters>) request.getAttribute("theaters");
    String searchParam = request.getParameter("search");
    boolean hasSearch = searchParam != null && !searchParam.trim().isEmpty();
    Theaters nearestTheater = (Theaters) request.getAttribute("nearestTheater");
    Double nearestDistance = (Double) request.getAttribute("nearestDistance");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PhnetPhlyx - Theaters</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/theaters.css">
</head>

<body>
<section class="Layout1">
	<!--Header-->
    <%@ include file="components/header.jsp" %>

    <!--Left Sidebar-->
    <%
        request.setAttribute("currentPage", "theaters");
    %>
    <%@ include file="components/left_sidebar.jsp" %>

	<!--Main body-->
    <main class="MainBody">
        <div class="theater-header">
            <h1>Theaters</h1>

            <!-- Filters the theater list by name/address -->
            <form class="theater-search" action="${pageContext.request.contextPath}/theaters" method="get">
                <input
                    type="search"
                    name="search"
                    placeholder="Search by theater name or address..."
                    value="<%= request.getParameter("search") != null ? request.getParameter("search") : "" %>">
                <button type="submit">Search</button>
                <%
                    // When the Search button is clicked, userLat and userLng are preserved
                    // so the nearest theater result remains
                    String currentLat = request.getParameter("userLat");
                    String currentLng = request.getParameter("userLng");
                    if (currentLat != null && currentLng != null) {
                        %>
                            <input type="hidden" name="userLat" value="<%= currentLat %>">
                            <input type="hidden" name="userLng" value="<%= currentLng %>">
                        <%
                    }
                %>
            </form>
            <div class="theater-header-row">
                <p>Find a theater near you</p>
                <button id="find-near-btn" class="theater-button">Find theater near me</button>
            </div>

            <!-- Pending status shown while waiting for finding theater process -->
            <span id="find-near-status" class="find-near-status" style="display:none;">
                Finding the theater near you...
            </span>
        </div>

        <%
            if (nearestTheater != null) {
                %>
                    <section id="nearest-theater-result">
                        <h3 class="nearest-title">Nearest theater to you</h3>
                        <div class="theater-grid">
                            <article class="theater-card nearest-card">
                                <div class="theater-image">
                                    <img
                                        src="${pageContext.request.contextPath}/<%= nearestTheater.getTheater_image_path() %>"
                                        alt="<%= nearestTheater.getTheater_name() %>">
                                </div>
                                <div class="theater-content">
                                    <h2><%= nearestTheater.getTheater_name() %></h2>
                                    <p class="theater-address"><%= nearestTheater.getTheater_address() %></p>
                                    <div class="theater-info">
                                        <%
                                            if (nearestTheater.getOpen_time() != null && nearestTheater.getClosing_time() != null) {
                                                %>
                                                    <span>
                                                        Open:
                                                        <%= nearestTheater.getOpen_time() %>
                                                        -
                                                        <%= nearestTheater.getClosing_time() %>
                                                    </span>
                                                <%
                                            }
                                        %>
                                        <span class="theater-distance">
                                            <%= String.format("%.2f", nearestDistance) %> km away
                                        </span>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/theater_details?theater_id=5" class="theater-button">
                                        View Theater
                                    </a>
                                </div>
                            </article>
                        </div>
                    </section>
                <%
            }
            else if (request.getParameter("userLat") != null && request.getParameter("userLng") != null) {
                // When no theater list was retrieved and the user has clicked
                // the Find theater near me button, show this error message
                %>
                    <section id="nearest-theater-result">
                        <p class="no-theaters">Unable to find a nearby theater</p>
                    </section>
                <%
            }
        %>

        <!--Theater browse-->
        <h3 class="all-theaters-title"><%= hasSearch ? "Search Results" : "All Theaters" %></h3>
        <section class="theater-grid">
            <%
                if (theaters != null && !theaters.isEmpty()) {
                    for (Theaters theater : theaters) {
			            %>
			                <article class="theater-card">
			                    <div class="theater-image">
			                        <img
			                            src="${pageContext.request.contextPath}/<%= theater.getTheater_image_path() %>"
			                            alt="<%= theater.getTheater_name() %>">
			                    </div>
			                    <div class="theater-content">
			                        <h2><%= theater.getTheater_name() %></h2>
			                        <p class="theater-address"><%= theater.getTheater_address() %></p>
			                        <div class="theater-info">
			                            <%
			                                if (theater.getOpen_time() != null &&
			                                    theater.getClosing_time() != null) {
					                            %>
					                                <span>
					                                    Open:
					                                    <%= theater.getOpen_time() %>
					                                    -
					                                    <%= theater.getClosing_time() %>
					                                </span>
					                            <%
			                                }
			                            %>
			                        </div>
			                        <a
			                            href="${pageContext.request.contextPath}/theater_details?theater_id=<%= theater.getTheater_id() %>"
			                            class="theater-button">
			                            View Theater
			                        </a>
			                    </div>
			                </article>
			            <%
                    }
                }
                else {
		            %>
		                <p class="no-theaters">
		                    <%= hasSearch ? "No theaters available" : "No theaters found" %>
                        </p>
		            <%
                }
            %>
        </section>
    </main>

	<!--Right Sidebar-->
    <%@ include file="components/right_sidebar.jsp" %>

	<!--Footer-->
    <%@ include file="components/footer.jsp" %>
</section>

<script src="${pageContext.request.contextPath}/scripts/theaters.js"></script>
</body>
</html>