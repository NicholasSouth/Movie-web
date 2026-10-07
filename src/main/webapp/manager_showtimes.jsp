<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.LocalTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.format.TextStyle" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.LinkedHashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Locale" %>
<%@ page import="com.movieweb.model.Movies" %>
<%@ page import="com.movieweb.model.Rooms" %>
<%@ page import="com.movieweb.model.Showtimes" %>
<%@ page import="com.movieweb.model.Theaters" %>
<%!
    private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<%
    String contextPath = request.getContextPath();
    List<Rooms> rooms = (List<Rooms>) request.getAttribute("rooms");
    List<Showtimes> scheduleShowtimes = (List<Showtimes>) request.getAttribute("scheduleShowtimes");
    Map<Integer, Movies> scheduleMovies = (Map<Integer, Movies>) request.getAttribute("scheduleMovies");
    List<LocalDate> weekDates = (List<LocalDate>) request.getAttribute("weekDates");
    LocalDate selectedDate = (LocalDate) request.getAttribute("selectedDate");
    if (rooms == null) {
        rooms = new ArrayList<>();
    }
    if (scheduleShowtimes == null) {
        scheduleShowtimes = new ArrayList<>();
    }
    if (scheduleMovies == null) {
        scheduleMovies = new LinkedHashMap<>();
    }
    if (weekDates == null) {
        weekDates = new ArrayList<>();
    }
    List<Theaters> managerTheaters =
            (List<Theaters>) request.getAttribute("managerTheaters");
    Integer selectedTheaterId =
            (Integer) request.getAttribute("selectedTheaterId");
    if (managerTheaters == null) {
        managerTheaters = new ArrayList<>();
    }
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    Set<LocalTime> timeSet = new LinkedHashSet<>();
    for (Showtimes showtime : scheduleShowtimes) {
        if (showtime.getStart_at() != null) {
            timeSet.add(showtime.getStart_at().toLocalDateTime().toLocalTime());
        }
    }
    List<LocalTime> scheduleTimes = new ArrayList<>(timeSet);
    Map<String, Map<Integer, Showtimes>> scheduleTable = new LinkedHashMap<>();
    for (LocalTime time : scheduleTimes) {
        Map<Integer, Showtimes> roomShowtimes = new LinkedHashMap<>();
        for (Showtimes showtime : scheduleShowtimes) {
            if (showtime.getStart_at() == null) {
                continue;
            }
            LocalTime showtimeStart = showtime.getStart_at().toLocalDateTime().toLocalTime();
            if (showtimeStart.equals(time)) {
                roomShowtimes.put(showtime.getRoom_id(), showtime);
            }
        }
        scheduleTable.put(time.format(timeFormatter), roomShowtimes);
    }
%>
<html>
<head>
    <title>Movies & Showtimes</title>
    <link rel="stylesheet" href="<%= contextPath %>/styles/main_manager.css">
    <link rel="stylesheet" href="<%= contextPath %>/styles/theater_details.css">
    <link rel="stylesheet" href="<%= contextPath %>/styles/manager_showtimes.css">
</head>
<body>
    <section class="Layout2">
        <jsp:include page="/components/header_manager.jsp" />
        <%
            request.setAttribute("currentManagerPage", "showtimes");
        %>
        <%@ include file="components/manager_navigation.jsp" %>
        <main class="ManagerMain">
            <section class="theater-section">
                <%
                    String showtimeError = (String) session.getAttribute("showtimeError");
                    String showtimeSuccess = (String) session.getAttribute("showtimeSuccess");
                    session.removeAttribute("showtimeError");
                    session.removeAttribute("showtimeSuccess");
                %>
                <% if (showtimeError != null) { %>
                    <div class="showtime-message showtime-message-error"><%= showtimeError %></div>
                <% } %>
                <% if (showtimeSuccess != null) { %>
                    <div class="showtime-message showtime-message-success"><%= showtimeSuccess %></div>
                <% } %>
                <div class="manager-showtimes-header">
                    <h2>Movies & Showtimes</h2>
                    <form method="get" action="<%= contextPath %>/manager-showtimes" class="manager-theater-selector">
                        <label for="theaterId">Theater</label>
                        <select id="theaterId" name="theaterId" onchange="this.form.submit()">
                            <%
                                for (Theaters theater : managerTheaters) {
                            %>
                                <option value="<%= theater.getTheater_id() %>" <%= selectedTheaterId != null && selectedTheaterId == theater.getTheater_id() ? "selected" : "" %>>
                                    <%= theater.getTheater_name() %>
                                </option>
                            <%
                                }
                            %>
                        </select>
                    </form>
                </div>
                <div class="schedule-week-selector">
                    <%
                        for (LocalDate date : weekDates) {
                            boolean selected = selectedDate != null && selectedDate.equals(date);
                            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                    %>
                    <a href="<%= contextPath %>/manager-showtimes?theaterId=<%= selectedTheaterId %>&date=<%= date.format(dateFormatter) %>" class="schedule-day <%= selected ? "selected" : "" %>">
                        <span class="schedule-day-name"><%= dayName %></span>
                        <span class="schedule-day-date"><%= date.getDayOfMonth() %>/<%= date.getMonthValue() %></span>
                    </a>
                    <%
                        }
                    %>
                </div>
                <div class="manager-showtime-action">
                    <button type="button" class="add-showtime-button" id="openAddShowtimeDialog">
                        + Add Showtime
                    </button>
                    <button type="button" class="delete-showtime-toggle" id="toggleDeleteMode">
                        Delete Showtime
                    </button>
                </div>
                <div class="schedule-table-wrapper" id="scheduleTableWrapper">
                    <table class="schedule-table">
                        <thead>
                            <tr>
                                <th>Time</th>
                                <%
                                    for (Rooms room : rooms) {
                                %>
                                    <th><%= room.getRoom_name() %></th>
                                <%
                                    }
                                %>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (scheduleTimes.isEmpty()) {
                            %>
                                <tr>
                                    <td colspan="<%= rooms.size() + 1 %>">
                                        <div class="schedule-no-movies">No movies scheduled for this date.</div>
                                    </td>
                                </tr>
                            <%
                                } else {
                                    for (LocalTime time : scheduleTimes) {
                                        Map<Integer, Showtimes> roomShowtimes = scheduleTable.get(time.format(timeFormatter));
                            %>
                                <tr>
                                    <td class="schedule-time"><%= time.format(timeFormatter) %></td>
                                    <%
                                        for (Rooms room : rooms) {
                                            Showtimes showtime = roomShowtimes.get(room.getRoom_id());
                                            if (showtime == null) {
                                    %>
                                        <td>
                                            <div class="schedule-empty">No showtime</div>
                                        </td>
                                    <%
                                                continue;
                                            }
                                            Movies movie = scheduleMovies.get(showtime.getMovie_id());
                                            if (movie == null) {
                                    %>
                                        <td>
                                            <div class="schedule-empty">No movie</div>
                                        </td>
                                    <%
                                                continue;
                                            }
                                    %>
                                        <%
                                            String startText = showtime.getStart_at().toLocalDateTime().toLocalTime().format(timeFormatter);
                                            String endText = showtime.getEnd_at().toLocalDateTime().toLocalTime().format(timeFormatter);
                                        %>
                                        <td>
                                            <div class="schedule-showtime">
                                                <a href="<%= contextPath %>/movie-details?id=<%= movie.getMovie_id() %>" class="schedule-movie">
                                                    <img src="<%= contextPath %>/<%= movie.getPoster_path() %>" alt="<%= esc(movie.getMovie_name()) %>">
                                                    <span class="schedule-movie-name"><%= movie.getMovie_name() %></span>
                                                    <span class="schedule-movie-time"><%= startText %> - <%= endText %></span>
                                                </a>
                                                <button type="button"
                                                        class="delete-showtime-button"
                                                        data-showtime-id="<%= showtime.getShowtime_id() %>"
                                                        data-movie="<%= esc(movie.getMovie_name()) %>"
                                                        data-room="<%= esc(room.getRoom_name()) %>"
                                                        data-time="<%= startText %> - <%= endText %>">
                                                    Delete
                                                </button>
                                            </div>
                                        </td>
                                    <%
                                        }
                                    %>
                                </tr>
                            <%
                                    }
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    </section>
    <dialog class="showtime-dialog" id="addShowtimeDialog">
        <div class="showtime-dialog-header">
            <h2>Add Showtime</h2>
            <button type="button" class="showtime-dialog-close" id="closeAddShowtimeDialog">
                &times;
            </button>
        </div>

        <form
            class="showtime-form"
            method="post"
            action="<%= contextPath %>/manager-add-showtime">
            <input
                type="hidden"
                name="theaterId"
                value="<%= selectedTheaterId %>">

            <input
                type="hidden"
                name="date"
                value="<%= selectedDate %>">
            <div class="showtime-form-group">
                <label for="showtimeMovie">Movie</label>
                <input
                    type="text"
                    id="showtimeMovie"
                    name="movie"
                    placeholder="Movie name">
            </div>

            <div class="showtime-form-row">
                <div class="showtime-form-group">
                    <label for="showtimeStart">Start</label>
                    <input
                        type="text"
                        id="showtimeStart"
                        name="start"
                        placeholder="HH:mm"
                        pattern="([01][0-9]|2[0-3]):[0-5][0-9]"
                        maxlength="5"
                        required>
                </div>

                <div class="showtime-form-group">
                    <label for="showtimeEnd">End</label>
                    <input
                        type="text"
                        id="showtimeEnd"
                        name="end"
                        placeholder="HH:mm"
                        pattern="([01][0-9]|2[0-3]):[0-5][0-9]"
                        maxlength="5"
                        required>
                </div>
            </div>

            <div class="showtime-form-group">
                <label for="showtimeRoom">Room</label>
                <select
                    id="showtimeRoom"
                    name="room"
                    required>
                    <option value="">
                        Select room
                    </option>
                    <%
                        for (Rooms room : rooms) {
                    %>

                    <option value="<%= room.getRoom_id() %>">
                        <%= room.getRoom_name() %>
                    </option>

                    <%
                        }
                    %>
                </select>
            </div>
            <div class="showtime-form-group">
                <label for="showtimePrice">Price (VND)</label>
                <input
                    type="text"
                    id="showtimePrice"
                    name="price"
                    placeholder="Normal ticket price"
                    inputmode="numeric"
                    pattern="[1-9][0-9]*"
                    title="Enter a positive whole number"
                    maxlength="9"
                    required>
            </div>
            <div class="showtime-dialog-actions">
                <button
                    type="button"
                    class="showtime-cancel-button"
                    id="cancelAddShowtime">
                    Cancel
                </button>

                <button
                    type="submit"
                    class="showtime-save-button">
                    Add Showtime
                </button>
            </div>
        </form>
    </dialog>
    <dialog class="showtime-dialog" id="deleteShowtimeDialog">
        <div class="showtime-dialog-header">
            <h2>Delete Showtime</h2>
            <button type="button" class="showtime-dialog-close" id="closeDeleteShowtimeDialog">&times;</button>
        </div>
        <form class="showtime-form" id="deleteShowtimeForm" method="post"
              action="<%= contextPath %>/manager-delete-showtime">
            <input type="hidden" name="theaterId" value="<%= selectedTheaterId %>">
            <input type="hidden" name="date" value="<%= selectedDate %>">
            <input type="hidden" name="showtimeId" id="deleteShowtimeId">

            <p class="delete-showtime-message">Are you sure you want to delete this showtime?</p>
            <div class="delete-showtime-info">
                <strong id="deleteShowtimeMovie"></strong>
                <span id="deleteShowtimeDetail"></span>
            </div>

            <div class="showtime-dialog-actions">
                <button type="button" class="showtime-cancel-button" id="cancelDeleteShowtime">Cancel</button>
                <button type="submit" class="showtime-save-button">Delete</button>
            </div>
        </form>
    </dialog>
    <script src="<%= contextPath %>/scripts/manager_showtimes.js"></script>
</body>
</html>