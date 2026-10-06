<%@ page import="java.util.List" %>
<%@ page import="com.movieweb.model.Users" %>
<%@ page import="com.movieweb.model.Theaters" %>
<%@ page import="com.movieweb.model.Rooms" %>
<%@ page import="com.movieweb.model.Room_types" %>
<%
    Users currentUser = (Users) session.getAttribute("user");
    if (currentUser == null || !"MANAGER".equalsIgnoreCase(currentUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/main.jsp");
        return;
    }
    request.setAttribute("currentManagerPage", "theaters");

    Theaters theater = (Theaters) request.getAttribute("theater");
    List<Rooms> rooms = (List<Rooms>) request.getAttribute("rooms");
    String contextPath = request.getContextPath();
    List<Room_types> roomTypes = (List<Room_types>) request.getAttribute("roomTypes");
    String roomMessage = (String) request.getAttribute("roomMessage");
    String roomError = (String) request.getAttribute("roomError");
%>
<!DOCTYPE html>
<html>
	<head>
	    <meta charset="UTF-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	    <title>
	        <%= theater != null ? theater.getTheater_name() + " - Manager - PhnetPhlyx" : "Theater Details - PhnetPhlyx" %>
	    </title>
	    <link rel="stylesheet" href="<%= contextPath %>/styles/main_manager.css">
	    <link rel="stylesheet" href="<%= contextPath %>/styles/manager_theater_details.css">
	</head>
	<body>
		<section class="Layout2">

			<jsp:include page="/components/header_manager.jsp" />

			<%@ include file="components/manager_navigation.jsp" %>

		    <main class="ManagerMain">
		        <%
		            if (theater != null) {
		        %>
				        <a href="<%= contextPath %>/manager-theaters" class="back-link">&larr; Back to Theaters</a>
				        <% if (roomMessage != null) { %>
                            <div class="alert alert-success"><%= roomMessage %></div>
                        <% } %>
                        <% if (roomError != null) { %>
                            <div class="alert alert-error"><%= roomError %></div>
                        <% } %>

				        <!-- Theater Information -->
				        <section class="theater-details">
				            <div class="theater-details-image">
				                <%
				                    if (theater.getTheater_image_path() != null && !theater.getTheater_image_path().isEmpty()) {
				                %>
				                    	<img
					                    	src="<%= contextPath %>/<%= theater.getTheater_image_path() %>"
					                    	alt="<%= theater.getTheater_name() %>">
				                <%
				                    }
				                    else {
				                %>
				                    	<div class="no-image">No Image Available</div>
				                <%
				                    }
				                %>
				            </div>
				            <div class="theater-details-information">
				                <h1><%= theater.getTheater_name() %></h1>
				                <p>
				                    <strong>Address:</strong>
				                    <%= theater.getTheater_address() != null ? theater.getTheater_address() : "Not available" %>
				                </p>
				                <p>
				                    <strong>Opening Hours:</strong>
				                    <%
				                        if (theater.getOpen_time() != null && theater.getClosing_time() != null) {
				                    %>
				                        	<%= theater.getOpen_time().toLocalTime() %> - <%= theater.getClosing_time().toLocalTime() %>
				                    <%
				                        }
				                        else {
				                    %>
				                        	Not available
				                    <%
				                        }
				                    %>
				                </p>
				            </div>
				        </section>

				        <!-- About This Theater -->
				        <section class="theater-section">
				            <h2>About This Theater</h2>
				            <%
				                if (theater.getDescription() != null && !theater.getDescription().isEmpty()) {
				            %>
				                	<p><%= theater.getDescription() %></p>
				            <%
				                }
				                else {
				            %>
				                	<p class="no-data">No description available.</p>
				            <%
				                }
				            %>
				        </section>

				        <!-- All Rooms -->
				        <section class="theater-section " id="roomsSection">
				            <div class="section-header">
                                <h2>Rooms</h2>
                                <div class="section-tools">
                                    <button type="button" class="icon-button" title="Add room" aria-label="Add room"
                                            onclick="openAddRoomDialog()">+</button>
                                    <button type="button" class="icon-button" id="editModeButton" title="Edit rooms"
                                            aria-label="Edit rooms" onclick="toggleEditMode()">&#9998;</button>
                                </div>
                            </div>
                            <div class="room-grid">
                                <%
                                    if (rooms != null && !rooms.isEmpty()) {
                                        for (Rooms room : rooms) {
                                %>
                                            <article class="room-card <%= room.isActive() ? "" : "room-inactive" %>">
                                                <h3><%= room.getRoom_name() %></h3>
                                                <p>
                                                    <%= room.getRoomType() != null ? room.getRoomType().getRoom_type_name() : "Standard" %>
                                                </p>
                                                <div class="room-card-footer">
                                                    <span class="room-status <%= room.isActive() ? "status-active" : "status-inactive" %>">
                                                        <%= room.isActive() ? "Active" : "Inactive" %>
                                                    </span>
                                                    <a href="<%= contextPath %>/manager-room-seats?roomId=<%= room.getRoom_id() %>"
                                                       class="room-button view-seats-btn">View seats</a>
                                                </div>
                                                <div class="room-actions">
                                                    <button type="button" class="room-button edit-btn"
                                                            data-room-id="<%= room.getRoom_id() %>"
                                                            data-room-name="<%= room.getRoom_name() %>"
                                                            data-room-type-id="<%= room.getRoom_type_id() %>">
                                                        <b>Edit</b>
                                                    </button>

                                                    <% if (room.isActive()) { %>
                                                        <button type="button" class="room-button room-button-danger deactivate-btn"
                                                                data-room-id="<%= room.getRoom_id() %>"
                                                                data-room-name="<%= room.getRoom_name() %>">
                                                            <b>Deactivate</b>
                                                        </button>
                                                    <% } else { %>
                                                        <form method="post" action="<%= contextPath %>/manager-rooms/toggle">
                                                            <input type="hidden" name="roomId" value="<%= room.getRoom_id() %>">
                                                            <input type="hidden" name="active" value="true">
                                                            <button type="submit" class="room-button"><b>Activate</b></button>
                                                        </form>
                                                    <% } %>
                                                    <button type="button" class="room-button room-button-delete delete-btn"
                                                            data-room-id="<%= room.getRoom_id() %>"
                                                            data-room-name="<%= room.getRoom_name() %>">
                                                        <b>Delete</b>
                                                    </button>
                                                </div>
                                            </article>
                                <%
                                        }
                                    }
                                    else {
                                %>
                                        <p class="no-data">This theater has no rooms yet.</p>
                                <%
                                    }
                                %>
                            </div>
				        </section>
				        <dialog id="addRoomDialog" class="room-dialog">
                            <form method="post" action="<%= contextPath %>/manager-rooms/add">
                                <h3>Add room</h3>
                                <input type="hidden" name="theaterId" value="<%= theater.getTheater_id() %>">

                                <label for="addRoomName">Room name</label>
                                <input type="text" id="addRoomName" name="roomName" required>

                                <label for="addRoomType">Room type</label>
                                <select id="addRoomType" name="roomTypeId" required>
                                    <%
                                        if (roomTypes != null) {
                                            for (Room_types roomType : roomTypes) {
                                    %>
                                                <option value="<%= roomType.getRoom_type_id() %>"><%= roomType.getRoom_type_name() %></option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>

                                <div class="room-dialog-actions">
                                    <button type="button" class="room-button" onclick="addRoomDialog.close()">Cancel</button>
                                    <button type="submit" class="room-button">Add room</button>
                                </div>
                            </form>
                        </dialog>
                        <dialog id="editRoomDialog" class="room-dialog">
                            <form method="post" action="<%= contextPath %>/manager-rooms/edit">
                                <h3>Edit room</h3>
                                <input type="hidden" name="theaterId" value="<%= theater.getTheater_id() %>">
                                <input type="hidden" name="roomId" id="editRoomId">

                                <label for="editRoomName">Room name</label>
                                <input type="text" id="editRoomName" name="roomName" required>

                                <label for="editRoomType">Room type</label>
                                <select id="editRoomType" name="roomTypeId" required>
                                    <%
                                        if (roomTypes != null) {
                                            for (Room_types roomType : roomTypes) {
                                    %>
                                                <option value="<%= roomType.getRoom_type_id() %>"><%= roomType.getRoom_type_name() %></option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>

                                <div class="room-dialog-actions">
                                    <button type="button" class="room-button" onclick="editRoomDialog.close()">Cancel</button>
                                    <button type="submit" class="room-button">Save</button>
                                </div>
                            </form>
                        </dialog>
				        <dialog id="deactivateDialog" class="room-dialog">
                            <form method="post" action="<%= contextPath %>/manager-rooms/toggle">
                                <h3>Deactivate room</h3>
                                <p>Deactivate <strong id="deactivateRoomName"></strong>? It will no longer be available for new showtimes.</p>
                                <input type="hidden" name="roomId" id="deactivateRoomId">
                                <input type="hidden" name="active" value="false">
                                <div class="room-dialog-actions">
                                    <button type="button" class="room-button" onclick="deactivateDialog.close()">Cancel</button>
                                    <button type="submit" class="room-button room-button-danger">Deactivate</button>
                                </div>
                            </form>
                        </dialog>
                        <dialog id="deleteDialog" class="room-dialog">
                            <form method="post" action="<%= contextPath %>/manager-rooms/delete">
                                <h3>Delete room</h3>
                                <p>Delete <strong id="deleteRoomName"></strong>? The room will be removed from this theater and cannot be restored from here.</p>
                                <p class="dialog-note">If you only want to stop using it for a while, use Deactivate instead.</p>
                                <input type="hidden" name="roomId" id="deleteRoomId">
                                <div class="room-dialog-actions">
                                    <button type="button" class="room-button" onclick="deleteDialog.close()">Cancel</button>
                                    <button type="submit" class="room-button room-button-delete">Delete</button>
                                </div>
                            </form>
                        </dialog>
		        <%
		            }
		            else {
		        %>
				        <section class="theater-section">
				            <h2>Theater Not Found</h2>
				            <p>The requested theater could not be found.</p>
				        </section>
		        <%
		            }
		        %>
		    </main>
		</section>
		<script src="<%= contextPath %>/scripts/manager_theater_details.js"></script>
	</body>
</html>