<%@ page import="java.util.List" %>
<%@ page import="com.movieweb.model.Theaters" %>

<%
    List<Theaters> theaters =
        (List<Theaters>) request.getAttribute("theaters");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PhnetPhlyx - Theaters</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/styles/main.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/styles/theaters.css">
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
            <p>
                Find a cinema near you.
            </p>
        </div>
        <!--Theater browse-->
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
			                        <h2>
			                            <%= theater.getTheater_name() %>
			                        </h2>
			                        <p class="theater-address">
			                            <%= theater.getTheater_address() %>
			                        </p>
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
		                    No theaters found.
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
</body>
</html>
