<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.movieweb.util.ViewUtils" %>
<% request.setAttribute("currentPage", "movies"); %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= ViewUtils.h(request.getAttribute("errorTitle")) %> - PhnetPhlyx</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/movies.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/movie-data.css">
</head>
<body>
<section class="Layout1 movie-layout">
    <%@ include file="../../components/header.jsp" %>
    <%@ include file="../../components/left_sidebar.jsp" %>
    <main class="MainBody">
        <section class="movie-section movie-error" role="alert">
            <p class="muted">Error <%= ViewUtils.h(request.getAttribute("errorStatus")) %></p>
            <h1><%= ViewUtils.h(request.getAttribute("errorTitle")) %></h1>
            <p><%= ViewUtils.h(request.getAttribute("errorMessage")) %></p>
            <a class="back-to-movies" href="${pageContext.request.contextPath}/movies">&larr; Return to Movies</a>
        </section>
    </main>
    <%@ include file="../../components/right_sidebar.jsp" %>
    <%@ include file="../../components/footer.jsp" %>
</section>
</body>
</html>
