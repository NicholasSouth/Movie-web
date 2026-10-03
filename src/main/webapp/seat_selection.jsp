<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,java.util.ArrayList" %>
<%@ page import="com.movieweb.model.Seats" %>
<%
    String contextPath = request.getContextPath();
    List<Seats> seats = (List<Seats>) request.getAttribute("seats");

    // Row names in order of appearance (query is already sorted by row, then column)
    List<String> rowNames = new ArrayList<>();
    int maxCol = 0;
    if (seats != null) {
        for (Seats seat : seats) {
            String row = seat.getSeat_row().trim();
            if (!rowNames.contains(row)) {
                rowNames.add(row);
            }
            if (seat.getSeat_col() > maxCol) {
                maxCol = seat.getSeat_col();
            }
        }
    }
%>
<!DOCTYPE html>
<html lang="vi">
	<head>
	    <meta charset="UTF-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1">
	    <title>Seat Selection</title>
	    <link rel="stylesheet" href="<%= contextPath %>/styles/seat_selection.css">
	</head>
	<body>
		<div class="theater">
		    <div class="screen">SCREEN</div>
		    <div class="seats" style="grid-template-columns: repeat(<%= maxCol %>, minmax(0, 1fr)); grid-template-rows: repeat(<%= rowNames.size() %>, minmax(var(--seat-size), 1fr));">
		        <%
		            if (seats != null) {
		                for (Seats seat : seats) {
		                    String row = seat.getSeat_row().trim();
		                    String stateClass = "";
		                    String label = row + seat.getSeat_col();
		                    String typeName = seat.getSeat_type_name();
		                    String typeClass = "";
		                    String typeLabel = "";
		
		                    if (typeName != null && !typeName.trim().equalsIgnoreCase("Normal")) {
		                        typeClass = " type-" + typeName.trim().toLowerCase();
		                        typeLabel = typeName.trim();
		                    }
		
		                    if (!seat.isActive()) {
		                        stateClass = " unavailable";
		                        label = "X";
		                        typeClass = "";
		                        typeLabel = "";
		                    } 
		                    else if (seat.isBooked()) {
		                        stateClass = " booked";
		                    }
		        %>
				        <div 
				            class="seat<%= typeClass %><%= stateClass %>" 
				            data-seat-id="<%= seat.getSeat_id() %>" 
				            data-name="<%= row %><%= seat.getSeat_col() %>" 
				            data-price="<%= seat.getPrice() %>" 
				            style="grid-row: <%= rowNames.indexOf(row) + 1 %>; grid-column: <%= seat.getSeat_col() %>;">
				            <span class="seat-label"><%= label %></span>
				            <%
				                if (!typeLabel.isEmpty()) {
				            %>
				                	<span class="seat-type"><%= typeLabel %></span>
				            <%
				                }
				            %>
				        </div>
		        <%
		                }
		            }
		        %>
		    </div>
		
		    <div class="legend">
		        <div class="legend-item"><span class="swatch"></span>Available</div>
		        <div class="legend-item"><span class="swatch selected"></span>Selected</div>
		        <div class="legend-item"><span class="swatch booked"></span>Booked</div>
		        <div class="legend-item"><span class="swatch unavailable">X</span>Unavailable</div>
		    </div>
		</div>
		
		<div class="summary">
		    <div class="summary-info">
		        <span>Seat: <span class="summary-seat">-</span></span>
		        <span class="summary-total">Total: 0đ</span>
		    </div>
		    <form id="bookingForm" class="confirm-form" action="<%= contextPath %>/create-booking" method="post">
		        <input 
		        	type="hidden" 
		        	name="showtime_id" 
		        	value="<%= request.getAttribute("showtimeId") %>">
		        <button 
		        	type="submit" 
		        	class="confirm-btn" 
		        	disabled>Confirm</button>
		    </form>
		</div>
		<%
		    if ("1".equals(request.getAttribute("booking_error"))) {
		%>
		        <p class="booking-error">
		            Booking failed. Please select your seats again and try again.
		        </p>
		<%
		    }
		%>	
		<script src="<%= contextPath %>/scripts/seat_selection.js"></script>
	</body>
</html>