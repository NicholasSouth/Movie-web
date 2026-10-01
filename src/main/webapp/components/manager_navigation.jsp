<%
    String currentManagerPage = (String) request.getAttribute("currentManagerPage");

    if (currentManagerPage == null) {
        currentManagerPage = "";
    }
%>

<aside class="ManagerNavigation">
    <nav class="manager-navigation-nav">
        <a
            href="${pageContext.request.contextPath}/main_manager.jsp"
            class="manager-nav-item <%= "dashboard".equals(currentManagerPage) ? "active" : "" %>">
            Dashboard
        </a>

        <div class="manager-nav-section">
            <div class="manager-nav-section-title">
                PROCESS ORDERS
            </div>
            <a
                href="${pageContext.request.contextPath}/manager-sales"
                class="manager-nav-item <%= "sales".equals(currentManagerPage) ? "active" : "" %>">
                Sales & Bookings
            </a>
            <a
                href="${pageContext.request.contextPath}/manager-payments"
                class="manager-nav-item <%= "payments".equals(currentManagerPage) ? "active" : "" %>">
                Payments
            </a>
            <a
                href="${pageContext.request.contextPath}/manager-refunds"
                class="manager-nav-item <%= "refunds".equals(currentManagerPage) ? "active" : "" %>">
                Refunds
            </a>
        </div>

        <div class="manager-nav-section">
            <div class="manager-nav-section-title">
                MANAGE THEATERS
            </div>
            <a
                href="${pageContext.request.contextPath}/manager-showtimes"
                class="manager-nav-item <%= "showtimes".equals(currentManagerPage) ? "active" : "" %>">
                Movies & Showtimes
            </a>
            <a
                href="${pageContext.request.contextPath}/manager-theaters"
                class="manager-nav-item <%= "theaters".equals(currentManagerPage) ? "active" : "" %>">
                Theater Management
            </a>
            <a
                href="${pageContext.request.contextPath}/manager-promotions"
                class="manager-nav-item <%= "promotions".equals(currentManagerPage) ? "active" : "" %>">
                Promotions
            </a>
            <a
                href="${pageContext.request.contextPath}/manager-statistics"
                class="manager-nav-item <%= "statistics".equals(currentManagerPage) ? "active" : "" %>">
                Statistics
            </a>
        </div>
    </nav>
</aside>