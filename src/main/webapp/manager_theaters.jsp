<%@ page import="java.util.List" %>
<%@ page import="com.movieweb.model.Users" %>
<%@ page import="com.movieweb.model.Theaters" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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
    List<Theaters> requestableTheaters = (List<Theaters>) request.getAttribute("requestableTheaters");
    boolean canRequest = requestableTheaters != null && !requestableTheaters.isEmpty();
    String requestSuccess = (String) session.getAttribute("theaterRequestSuccess");
    String requestError = (String) session.getAttribute("theaterRequestError");
    session.removeAttribute("theaterRequestSuccess");
    session.removeAttribute("theaterRequestError");
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
		            <div class="theater-header-top">
                        <div class="theater-header-info">
                            <h1>Theater Management</h1>
                            <p>Theaters you are managing.</p>
                        </div>
                        <button type="button" class="request-theater-button" data-open-request>
                            + Request Theater
                        </button>
                    </div>

                    <% if (requestSuccess != null) { %>
                        <div class="request-message request-success"><c:out value="<%= requestSuccess %>"/></div>
                    <% } %>
                    <% if (requestError != null) { %>
                        <div class="request-message request-error"><c:out value="<%= requestError %>"/></div>
                    <% } %>

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
				                        <div class="theater-actions">
                                            <a href="<%= request.getContextPath() %>/manager-theater-details?id=<%= theater.getTheater_id() %>" class="theater-button">
                                                View Theater
                                            </a>
                                            <button type="button" class="leave-theater-button"
                                                    data-leave-id="<%= theater.getTheater_id() %>"
                                                    data-leave-name="<c:out value="<%= theater.getTheater_name() %>"/>">
                                                Stop Managing
                                            </button>
                                        </div>
				                    </div>
				                </article>
		            <%
		                    }
		                }
		                else {
		            %>
			                <p class="no-theaters">
                                <%= hasSearch ? "No theaters found matching your search." : "You are not managing any theater yet." %>
                                <% if (!hasSearch) { %>
                                    <br>
                                    <button type="button" class="request-theater-button" data-open-request>
                                        + Request Theater
                                    </button>
                                <% } %>
                            </p>
		            <%
		                }
		            %>
		        </section>
		        <dialog class="request-dialog" id="requestDialog">
                    <form method="post" action="<%= request.getContextPath() %>/manager-request-theater">
                        <h3>Request to manage a theater</h3>
                        <p class="request-dialog-note">
                            Your request will be reviewed by an administrator.
                        </p>

                        <label for="requestTheater">Theater</label>
                        <select id="requestTheater" name="theaterId" required <%= canRequest ? "" : "disabled" %>>
                            <% if (canRequest) { %>
                                <option value="" disabled selected>Select a theater</option>
                                <% for (Theaters t : requestableTheaters) { %>
                                    <option value="<%= t.getTheater_id() %>">
                                        <c:out value="<%= t.getTheater_name() %>"/> - <c:out value="<%= t.getTheater_address() %>"/>
                                    </option>
                                <% } %>
                            <% } else { %>
                                <option>No theater available to request</option>
                            <% } %>
                        </select>

                        <div class="request-dialog-actions">
                            <button type="button" class="request-cancel" id="closeRequestDialog">Cancel</button>
                            <button type="submit" class="request-submit" <%= canRequest ? "" : "disabled" %>>Send Request</button>
                        </div>
                    </form>
                </dialog>
                <dialog class="request-dialog" id="leaveDialog">
                    <form method="post" action="<%= request.getContextPath() %>/manager-leave-theater">
                        <h3>Stop managing this theater?</h3>
                        <p class="request-dialog-note">
                            Your request will be reviewed by an administrator.
                            You keep access to this theater until it is approved.
                        </p>

                        <input type="hidden" name="theaterId" id="leaveTheaterId">
                        <div class="leave-theater-name" id="leaveTheaterName"></div>

                        <label for="leaveReason">Reason (optional)</label>
                        <textarea id="leaveReason" name="reason" rows="4" maxlength="500"
                                  placeholder="Tell the administrator why you want to stop managing this theater..."></textarea>

                        <div class="request-dialog-actions">
                            <button type="button" class="request-cancel" id="closeLeaveDialog">Cancel</button>
                            <button type="submit" class="request-submit">Send Request</button>
                        </div>
                    </form>
                </dialog>
		    </main>
		</section>
		<script src="${pageContext.request.contextPath}/scripts/manager_theaters.js"></script>
	</body>
</html>