<%@ page import="com.movieweb.model.Users" %>
<%
	Users currentUser = (Users) session.getAttribute("user");
	if (currentUser == null) {
	    response.sendRedirect(request.getContextPath() + "/log_in.jsp");
	    return;
	}
%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<meta 
			name="viewport" 
			content="width=device-width, initial-scale=1.0">
		<title>
		    <%= currentUser.getFullName() %> - Profile
		</title>
		<link 
			rel="stylesheet" 
			href="${pageContext.request.contextPath}/styles/main.css">
		<link 
			rel="stylesheet" 
			href="${pageContext.request.contextPath}/styles/user_profile.css">
	</head>
	<body>
		<div class="Layout1">
		    <!-- Header -->
		    <jsp:include page="/components/header.jsp" />
		
		    <!-- Left sidebar -->
		    <aside class="LeftSidebar">
		        <jsp:include page="/components/left_sidebar.jsp" />
		    </aside>
		
		    <!-- Main body -->
		    <main class="MainBody profile-page">
		        <!-- Profile header != header -->
		        <section class="profile-header">
		
		            <!-- Banner -->
		            <div class="profile-banner">
		                <%
		                    if (currentUser.getBannerPath() != null &&
		                        !currentUser.getBannerPath().isEmpty()) {
		                %>
		                    <img src="${pageContext.request.contextPath}/<%= currentUser.getBannerPath() %>" alt="Profile banner">
		                <%
		                    } else {
		                %>
		                    <div class="default-banner"></div>
		                <%
		                    }
		                %>
		            </div>
		
		            <!-- Profile Information -->
		            <div class="profile-header-info">
		
		                <!-- Avatar -->
		                <div class="profile-avatar">
		                    <%
		                        if (currentUser.getAvtPath() != null &&
		                            !currentUser.getAvtPath().isEmpty()) {
		                    %>
		                        <img src="${pageContext.request.contextPath}/<%= currentUser.getAvtPath() %>" alt="<%= currentUser.getFullName() %>">
		                    <%
		                        } 
		                        else {
		                    %>
		                        <div class="profile-default-avatar">
		                            <%= currentUser.getFullName()
		                                .substring(0, 1)
		                                .toUpperCase() %>
		                        </div>
		                    <%
		                        }
		                    %>
		                </div>
		
		                <!-- User Information -->
		                <div class="profile-identity">
		                    <h1>
		                        <%= currentUser.getFullName() %>
		                    </h1>
		                    <p class="profile-username">
		                        @<%= currentUser.getUsername() %>
		                    </p>
		                    <span class="profile-role">
		                        <%= currentUser.getRole() %>
		                    </span>
		                </div>
		
		                <!-- Settings -->
		                <div class="profile-actions">
		                    <a 
		                    	href="${pageContext.request.contextPath}/settings.jsp" 
		                    	class="profile-settings-button">
		                        <img 
		                        	src="${pageContext.request.contextPath}/pictures/assessments/setting.png" 
		                        	alt="Settings">
		                        <span>
		                            Settings
		                        </span>
		                    </a>
		                </div>
		            </div>
		        </section>
		
		        <!-- Favourite movie -->
		        <section class="profile-section favourite-section">
		
		            <!-- Section Header -->
		            <div class="section-header">
		                <div>
		                    <h2>
		                        Favourite Movies
		                    </h2>
		                    <p class="section-description">
		                        Movies this user has added to their favourites.
		                    </p>
		                </div>
		
		                <!-- Public / Private -->
		                <button
		                    type="button"
		                    class="visibility-button"
		                    id="favouriteVisibilityButton"
		                    data-public="<%= currentUser.getFavouriteMoviesVisibility() %>" 
		                    data-url="${pageContext.request.contextPath}/favourite-visibility">
		                    <img
		                        id="visibilityIcon"
		                        src="${pageContext.request.contextPath}/pictures/assessments/public.png"
		                        alt="Public"
		                        class="visibility-icon">
		                    <span id="visibilityText">
		                        Public
		                    </span>
		                </button>
		            </div>
			        <!-- Favourite movie grid -->
		            <jsp:include page="/components/movie_grid.jsp" />
		        </section>
		
		        <!-- Booking -->
		        <section class="profile-section booking-section">
		
		            <div class="section-header">
		                <div>
		                    <h2>
		                        Bookings
		                    </h2>
		                    <p class="section-description">
		                        Your movie booking history.
		                    </p>
		                </div>
		                <span class="private-label">
		                    Private
		                </span>
		            </div>
		
		            <!-- Booking will be implemented later -->
		            <div class="booking-placeholder">
		                <div class="booking-lock">
		                    placeholderlock
		                </div>
		                <h3>
		                    Booking History
		                </h3>
		                <p>
		                    Your bookings are private and can only
		                    be viewed by you.
		                </p>
		            </div>
		        </section>
		    </main>
		
		    <!-- Right side bar -->
		    <aside class="RightSidebar">
		        <jsp:include page="/components/right_sidebar.jsp" />
		    </aside>
		
		    <!-- Footer -->
		    <jsp:include page="/components/footer.jsp" />
		</div>
		
		<script 
			src="${pageContext.request.contextPath}/scripts/favourite_movies_visibility.js">
		</script>
	</body>
</html>