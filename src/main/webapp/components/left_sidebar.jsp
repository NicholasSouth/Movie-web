<%
    String currentPage = (String) request.getAttribute("currentPage");
    if (currentPage == null) {
        currentPage = "";
    }
%>
<aside class="LeftSidebar">
    <nav class="sidebar-nav">
        <a href="${pageContext.request.contextPath}/home"
           class="nav-item <%= "home".equals(currentPage) ? "active" : "" %>">
            Home
        </a>
        <a href="${pageContext.request.contextPath}/movies.jsp"
           class="nav-item <%= "movies".equals(currentPage) ? "active" : "" %>">
            Movies
        </a>
        <a href="${pageContext.request.contextPath}/theaters"
           class="nav-item <%= "theaters".equals(currentPage) ? "active" : "" %>">
            Theaters
        </a>
        <a href="${pageContext.request.contextPath}/bookings.jsp"
           class="nav-item <%= "bookings".equals(currentPage) ? "active" : "" %>">
            My Bookings
        </a>
        <a href="${pageContext.request.contextPath}/favourites.jsp"
           class="nav-item <%= "favorites".equals(currentPage) ? "active" : "" %>">
            Favorites
        </a>
    </nav>
</aside>
