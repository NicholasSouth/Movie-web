<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,java.util.ArrayList" %>
<%@ page import="com.movieweb.model.Users" %>
<%@ page import="com.movieweb.model.Theaters" %>
<%@ page import="com.movieweb.model.Rooms" %>
<%@ page import="com.movieweb.model.Seats" %>
<%@ page import="com.movieweb.model.Seat_types" %>
<%
    Users currentUser = (Users) session.getAttribute("user");
    if (currentUser == null || !"MANAGER".equalsIgnoreCase(currentUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/main.jsp");
        return;
    }

    String contextPath = request.getContextPath();
    Rooms room = (Rooms) request.getAttribute("room");
    Theaters theater = (Theaters) request.getAttribute("theater");
    List<Seats> seats = (List<Seats>) request.getAttribute("seats");

    if (room == null) {
        response.sendRedirect(contextPath + "/manager-theaters");
        return;
    }

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
    List<Seat_types> seatTypes = (List<Seat_types>) request.getAttribute("seatTypes");
    String seatMessage = (String) request.getAttribute("seatMessage");
    String seatError = (String) request.getAttribute("seatError");
%>
<!DOCTYPE html>
<html lang="vi">
	<head>
	    <meta charset="UTF-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1">
	    <title><%= room.getRoom_name() %> - Seats</title>
	    <link rel="stylesheet" href="<%= contextPath %>/styles/seat_selection.css">
	    <link rel="stylesheet" href="<%= contextPath %>/styles/manager_room_seats.css">
	</head>
        <body>
            <dialog id="addSeatDialog" class="seat-dialog">
                <form id="addSeatForm" method="post" action="<%= contextPath %>/manager-seats/add" novalidate>
                    <input type="hidden" name="roomId" value="<%= room.getRoom_id() %>">
                    <h2>Add seat</h2>

                    <label for="seatName">Seat name</label>
                    <input type="text" id="seatName" name="seatName" placeholder="e.g. A1, B12"
                           maxlength="4" autocomplete="off">
                    <p class="field-hint">One uppercase letter (row) followed by a number (column).</p>
                    <p class="field-error" id="seatNameError" hidden></p>

                    <label for="seatType">Seat type</label>
                    <select id="seatType" name="seatTypeId">
                        <%
                            if (seatTypes != null) {
                                for (Seat_types st : seatTypes) {
                        %>
                                    <option value="<%= st.getSeat_type_id() %>"><%= st.getSeat_type_name() %></option>
                        <%
                                }
                            }
                        %>
                    </select>

                    <div class="dialog-actions">
                        <button type="button" class="btn-cancel" id="cancelAddSeat">Cancel</button>
                        <button type="submit" class="btn-primary">Add</button>
                    </div>
                </form>
            </dialog>
            <dialog id="editSeatDialog" class="seat-dialog">
                <form id="editSeatForm" method="post" action="<%= contextPath %>/manager-seats/edit">
                    <input type="hidden" name="roomId" value="<%= room.getRoom_id() %>">
                    <input type="hidden" name="seatNames" id="editSeatNames">
                    <h2>Edit seats</h2>
                    <p class="dialog-summary" id="editSeatSummary"></p>

                    <div id="editSeatNameGroup">
                        <label for="editSeatName">Seat name</label>
                        <input type="text" id="editSeatName" name="seatName" placeholder="e.g. A1, B12"
                               maxlength="4" autocomplete="off" required>
                        <p class="field-hint">One uppercase letter (row) followed by a number (column).</p>
                        <p class="field-error" id="editSeatNameError" hidden></p>
                    </div>

                    <label for="editSeatType">Seat type</label>
                    <select id="editSeatType" name="seatTypeId" required>
                        <option value="" disabled selected hidden>Select a type</option>
                        <%
                            if (seatTypes != null) {
                                for (Seat_types st : seatTypes) {
                        %>
                                    <option value="<%= st.getSeat_type_id() %>"><%= st.getSeat_type_name() %></option>
                        <%
                                }
                            }
                        %>
                    </select>

                    <div class="dialog-actions">
                        <button type="button" class="btn-cancel" id="cancelEditSeat">Cancel</button>
                        <button type="submit" class="btn-primary">Save</button>
                    </div>
                </form>
            </dialog>

            <dialog id="deactivateSeatDialog" class="seat-dialog">
                <form id="deactivateSeatForm" method="post" action="<%= contextPath %>/manager-seats/toggle">
                    <input type="hidden" name="roomId" value="<%= room.getRoom_id() %>">
                    <input type="hidden" name="seatNames" id="deactivateSeatNames">
                    <input type="hidden" name="active" value="false">
                    <h2>Deactivate seats</h2>
                    <p class="dialog-summary" id="deactivateSeatSummary"></p>
                    <p class="field-hint">Deactivated seats are shown as unavailable and cannot be booked.</p>

                    <div class="dialog-actions">
                        <button type="button" class="btn-cancel" id="cancelDeactivateSeat">Cancel</button>
                        <button type="submit" class="btn-danger">Deactivate</button>
                    </div>
                </form>
            </dialog>
            <dialog id="removeSeatDialog" class="seat-dialog">
                <form id="removeSeatForm" method="post" action="<%= contextPath %>/manager-seats/remove">
                    <input type="hidden" name="roomId" value="<%= room.getRoom_id() %>">
                    <input type="hidden" name="seatNames" id="removeSeatNames">
                    <h2>Remove seats</h2>
                    <p class="dialog-summary" id="removeSeatSummary"></p>
                    <p class="field-hint">Removed seats no longer appear in this room. To keep a seat on the map but make it unavailable, use Deactivate instead.</p>

                    <div class="dialog-actions">
                        <button type="button" class="btn-cancel" id="cancelRemoveSeat">Cancel</button>
                        <button type="submit" class="btn-danger">Remove</button>
                    </div>
                </form>
            </dialog>
		<div class="theater">
		    <div class="manager-seat-header">
                <div class="manager-seat-top">
                    <a href="<%= contextPath %>/manager-theater-details?id=<%= room.getTheater_id() %>">
                        &larr; Back to theater
                    </a>

                    <h1><%= room.getRoom_name() %></h1>

                    <span>
                        <%= theater != null ? theater.getTheater_name() + " - " : "" %>
                        <%= room.getRoomType() != null ? room.getRoomType().getRoom_type_name() : "Standard" %>
                    </span>
                </div>

                <div class="seat-section-tools">
                    <button type="button"
                            class="icon-button"
                            id="openAddSeat"
                            title="Add seat"
                            aria-label="Add seat">
                        +
                    </button>

                    <button type="button"
                            class="icon-button"
                            id="editModeButton"
                            title="Edit seats"
                            aria-label="Edit seats">
                        &#9998;
                    </button>
                </div>
            </div>
            <% if (seatMessage != null) { %>
                <div class="alert alert-success"><%= seatMessage %></div>
            <% } %>
            <% if (seatError != null) { %>
                <div class="alert alert-error"><%= seatError %></div>
            <% } %>

		    <div class="screen">SCREEN</div>
		    <%
		        if (seats != null && !seats.isEmpty()) {
		    %>
				    <div class="seats" style="grid-template-columns: repeat(<%= maxCol %>, minmax(0, 1fr)); grid-template-rows: repeat(<%= rowNames.size() %>, minmax(var(--seat-size), 1fr));">
				        <%
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

				                String tooltip = row + seat.getSeat_col() + (seat.isActive() ? "" : " (inactive)");

				                if (!seat.isActive()) {
				                    stateClass = " unavailable";
				                    label = "X";
				                    typeClass = "";
				                    typeLabel = "";
				                }
				        %>
						        <div class="seat<%= typeClass %><%= stateClass %>"
						             data-seat="<%= row + seat.getSeat_col() %>"
						             data-seat-type-id="<%= seat.getSeat_type_id() %>"
						             data-active="<%= seat.isActive() %>"
						             title="<%= tooltip %>"
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
				        %>
				    </div>

				    <div class="legend">
				        <div class="legend-item"><span class="swatch"></span>Available</div>
				        <div class="legend-item"><span class="swatch unavailable">X</span>Unavailable</div>
				    </div>
		    <%
		        }
		        else {
		    %>
				    <p class="no-seats">This room has no seats yet.</p>
		    <%
		        }
		    %>
		    <div class="seat-edit-toolbar" id="seatEditToolbar">
                <span id="selectedSeatCount">0 seats selected</span>
                <div class="seat-edit-actions">
                    <button type="button" class="seat-action-btn" id="editSelectedBtn">
                        Edit
                    </button>
                    <button type="button" class="seat-action-btn seat-action-activate" id="activateSelectedBtn">
                        Activate
                    </button>
                    <button type="button" class="seat-action-btn seat-action-danger" id="deactivateSelectedBtn">
                        Deactivate
                    </button>
                    <button type="button" class="seat-action-btn" id="clearSelectionBtn">
                        Clear selection
                    </button>
                    <button type="button" class="seat-action-btn seat-action-remove" id="removeSelectedBtn">
                        Remove
                    </button>
                </div>
            </div>
		</div>
        <script src="<%= contextPath %>/scripts/manager_room_seats.js"></script>
	</body>
</html>