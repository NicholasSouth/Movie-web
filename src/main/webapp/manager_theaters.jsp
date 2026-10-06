<%@ page import="java.util.List" %>
<%@ page import="com.movieweb.model.Users" %>
<%@ page import="com.movieweb.model.Theaters" %>
<%
    Users currentUser = (Users) session.getAttribute("user");
    if (currentUser == null || !"MANAGER".equalsIgnoreCase(currentUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/main.jsp");
        return;
    }
    request.setAttribute("currentManagerPage", "theaters");

    List<Theaters> theaters = (List<Theaters>) request.getAttribute("theaters");
    String searchParam = request.getParameter("search");
    boolean hasSearch = searchParam != null && !searchParam.trim().isEmpty();
%>
<!DOCTYPE html>
<html>
	<head>
	    <meta charset="UTF-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	    <title>Theater Management - PhnetPhlyx</title>
	    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main_manager.css">
	    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/manager_theaters.css">
	</head>
	<body>
		<section class="Layout2">

			<jsp:include page="/components/header_manager.jsp" />

			<%@ include file="components/manager_navigation.jsp" %>

		    <main class="ManagerMain">
		        <div class="theater-header">
		            <h1>Theater Management</h1>
		            <p>Theaters you are managing.</p>

		            <form class="theater-search" action="<%= request.getContextPath() %>/manager-theaters" method="get">
		                <input
		                    type="search"
		                    name="search"
		                    placeholder="Search by theater name or address..."
		                    value="<%= searchParam != null ? searchParam : "" %>">
		                <button type="submit">Search</button>
		            </form>
		        </div>

		        <h3 class="all-theaters-title">
		            <%= hasSearch ? "Search Results" : "Managed Theaters" %>
		        </h3>

		        <section class="theater-grid">
		            <%
		                if (theaters != null && !theaters.isEmpty()) {
		                    for (Theaters theater : theaters) {
		            %>
				                <article class="theater-card">
				                    <div class="theater-image">
				                        <img
				                            src="<%= request.getContextPath() %>/<%= theater.getTheater_image_path() %>"
				                            alt="<%= theater.getTheater_name() %>">
				                    </div>
				                    <div class="theater-content">
				                        <h2><%= theater.getTheater_name() %></h2>
				                        <p class="theater-address"><%= theater.getTheater_address() %></p>
				                        <div class="theater-info">
				                            <%
				                                if (theater.getOpen_time() != null && theater.getClosing_time() != null) {
				                            %>
					                                <span>Open: <%= theater.getOpen_time() %> - <%= theater.getClosing_time() %></span>
				                            <%
				                                }
				                            %>
				                        </div>
				                        <a href="<%= request.getContextPath() %>/manager-theater-details?id=<%= theater.getTheater_id() %>" class="theater-button">
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
			                    <%= hasSearch ? "No theaters found matching your search." : "You are not managing any theater yet." %>
			                </p>
		            <%
		                }
		            %>
		        </section>
		    </main>
		</section>
	</body>
</html>