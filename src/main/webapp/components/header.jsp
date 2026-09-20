<%@ page import="com.movieweb.model.Users,com.movieweb.util.ViewUtils" %>
<%
    Users headerUser = (Users) session.getAttribute("user");
    String headerName = headerUser == null ? "" : ViewUtils.text(headerUser.getFullName(), "Member");
%>
<header class="Header">
    <a href="${pageContext.request.contextPath}/main.jsp" class="website-name">PhnetPhlyx</a>
    <form class="search-container" action="${pageContext.request.contextPath}/movies" method="get" role="search">
        <input type="search" name="search" maxlength="200" placeholder="Search movies..."
               aria-label="Search movies" class="search-bar">
    </form>
    <div class="header-menu">
        <button type="button" class="menu-button" id="menuButton" aria-label="Open menu" aria-expanded="false">
            <span class="menu-dots">...</span>
        </button>
        <div class="menu-dropdown" id="menuDropdown">
            <% if (headerUser == null) { %>
            <div class="guest-menu">
                <a href="${pageContext.request.contextPath}/log_in.jsp" class="menu-item login-item">Log in / Register</a>
            </div>
            <% } else { %>
            <a href="${pageContext.request.contextPath}/user-profile" class="user-menu-header user-profile-link">
                <div class="user-avatar">
                    <% if (ViewUtils.hasText(headerUser.getAvtPath())) { %>
                    <img src="<%= ViewUtils.h(ViewUtils.asset(request.getContextPath(), headerUser.getAvtPath())) %>"
                         alt="<%= ViewUtils.h(headerName) %>">
                    <% } else { %>
                    <div class="default-avatar"><%= ViewUtils.h(headerName.substring(0, 1).toUpperCase(java.util.Locale.ROOT)) %></div>
                    <% } %>
                </div>
                <div class="user-information">
                    <div class="user-name"><%= ViewUtils.h(headerName) %></div>
                    <div class="user-role"><%= ViewUtils.h(headerUser.getRole()) %></div>
                </div>
            </a>
            <div class="menu-divider"></div>
            <a href="${pageContext.request.contextPath}/settings.jsp" class="menu-item">
                <img src="${pageContext.request.contextPath}/pictures/assessments/setting.png" alt="Settings" class="menu-item-icon">
                <span>Settings</span>
            </a>
            <a href="${pageContext.request.contextPath}/logout" class="menu-item logout-item">
                <img src="${pageContext.request.contextPath}/pictures/assessments/log_out.png" alt="Log out" class="menu-item-icon">
                <span>Log out</span>
            </a>
            <% } %>
        </div>
    </div>
</header>
<script src="${pageContext.request.contextPath}/scripts/header.js"></script>