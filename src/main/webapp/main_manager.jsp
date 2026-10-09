<%@ page import="com.movieweb.model.Users" %>
<%@ page import="com.movieweb.model.Theaters" %>
<%@ page import="java.util.List" %>

<%
    Users currentUser = (Users) session.getAttribute("user");
    if (currentUser == null
        || !"MANAGER".equalsIgnoreCase(currentUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/main.jsp");
        return;
    }
    request.setAttribute("currentManagerPage", "dashboard");
    List<Theaters> theaters = (List<Theaters>)request.getAttribute("theaters");
    Integer currentTheaterId = (Integer)request.getAttribute("currentTheaterId");
    String selectedTheaterId = currentTheaterId == null
            ? ""
            : String.valueOf(currentTheaterId);
%>
<!DOCTYPE html>
<html>
	<head>
	    <meta charset="UTF-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	    <title>Manager Dashboard - PhnetPhlyx</title>
	    <link 
	    	rel="stylesheet" 
	    	href="${pageContext.request.contextPath}/styles/main_manager.css">
	</head>
	<body>
		<section class="Layout2">
		
			<jsp:include page="/components/header_manager.jsp" />
			
		    <%@ include file="components/manager_navigation.jsp" %>
		
		    <main class="ManagerMain">
		        <div class="manager-header">
		            <h2>Manager Dashboard</h2>
		            <p>
		                Welcome back, <strong><%= currentUser.getFullName() %></strong>.
		            </p>
		            <form
					    class="manager-theater-filter"
					    method="get"
					    action="${pageContext.request.contextPath}/manager-theater">
					
					    <label for="theater-filter">
					        Theater
					    </label>
					
					    <select
					        id="theater-filter"
					        name="theaterId"
					        onchange="this.form.submit()">
					
					        <%
					            if (theaters != null) {
					                for (Theaters theater : theaters) {
					        %>
							            <option
							                value="<%= theater.getTheater_id() %>"
							                <%= String.valueOf(
							                        theater.getTheater_id())
							                        .equals(selectedTheaterId)
							                        ? "selected"
							                        : "" %>>
							                <%= theater.getTheater_name() %>
							            </option>
					        <%
					                }
					            }
					        %>
					    </select>
					</form>
		        </div>
				
		        <section class="manager-summary">
		            <div class="manager-summary-card">
		                <h3>Today's Bookings</h3>
		                <p>--</p>
		            </div>
		            <div class="manager-summary-card">
		                <h3>Today's Revenue</h3>
		                <p>--</p>
		            </div>		         
		            <div class="manager-summary-card">
		                <h3>Upcoming Showtimes</h3>
		                <p>--</p>
		            </div>
		        </section>
		
		        <section class="manager-panel">
		            <div class="manager-panel-header">
		                <h3>Upcoming Showtimes</h3>
		                <a href="${pageContext.request.contextPath}/manager-showtimes">View All</a>
		            </div>
		            <div class="manager-panel-content">
		                <p>Upcoming showtimes will appear here.</p>
		            </div>
		        </section>
		
		        <section class="manager-panel">
		            <div class="manager-panel-header">
		                <h3>Recent Bookings</h3>
		                <a href="${pageContext.request.contextPath}/manager-sales">View All</a>
		            </div>
		            <div class="manager-panel-content">
		                <p>Recent customer bookings will appear here.</p>
		            </div>
		        </section>
		
		        <section class="manager-panel">
		            <div class="manager-panel-header">
		                <h3>Statistics</h3>
		                <a href="${pageContext.request.contextPath}/manager-statistics">View Statistics</a>
		            </div>
		            <div class="manager-panel-content">
		                <p>Booking and revenue statistics will appear here.</p>
		            </div>
		        </section>
		    </main>
		</section>
	</body>
</html>