<%
    request.setAttribute("currentPage", "home");
%>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">
    <title>PhnetPhlyx</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/styles/main.css">
</head>

<body>
    <section class="Layout1">
        <!-- Header -->
        <%@ include file="components/header.jsp" %>
        <!-- Left Sidebar -->
        <%@ include file="components/left_sidebar.jsp" %>
        <!-- Main Content -->
        <main class="MainBody">
            <h2>Current hot take!</h2>
            <%@ include file="components/movie_grid.jsp" %>
        </main>
        <!-- Right Sidebar -->
        <%@ include file="components/right_sidebar.jsp" %>
        <!-- Footer -->
        <%@ include file="components/footer.jsp" %>
    </section>
</body>
</html>
