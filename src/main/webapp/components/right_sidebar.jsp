<%@ page import="java.util.List" %>
<%@ page import="com.movieweb.model.Ads" %>
<aside class="RightSidebar">
<%
    List<Ads> advertisements = (List<Ads>) request.getAttribute("advertisements");
    if (advertisements != null) {
        for (Ads ad : advertisements) {
			%>
	            <a href="<%= ad.getLink() %>" class="ad-link" target="_blank">	
	                <img
	                    src="<%= request.getContextPath() %>/<%= ad.getImage_path() %>"
	                    alt="Advertisement"
	                    class="ad">	
	            </a>
			<%
        }
    }
%>
</aside>