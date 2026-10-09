<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
	String contextPath = request.getContextPath();
	List<Map<String, Object>> bookingHistory = (List<Map<String, Object>>) request.getAttribute("bookingHistory");
	Integer dailyRevenueObject = (Integer) request.getAttribute("dailyRevenue");
	int dailyRevenue = dailyRevenueObject != null ? dailyRevenueObject : 0;
	String refundStatus = request.getParameter("refund");
	String refundMessage = request.getParameter("message");
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	request.setAttribute("currentManagerPage", "sales");
%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<meta 
			name="viewport" 
			content="width=device-width, initial-scale=1.0">
		<title>Sales & Bookings</title>
		<link 
			rel="stylesheet"
			href="<%= contextPath %>/styles/main_manager.css">
		<link 
			rel="stylesheet"
			href="<%= contextPath %>/styles/pos.css">
	</head>
	<body>
		<div class="Layout2">
			<%@ include file="components/header_manager.jsp" %>
			<%@ include file="components/manager_navigation.jsp" %>
			<main class="ManagerMain">
				<div class="POSPage">
					<!-- Page Header -->
					<header class="POSHeader">
						<div class="POSHeaderContent">
							<span class="POSBreadcrumb">
								Process Orders / Sales & Bookings
							</span>
							<h1>Sales & Bookings</h1>
							<p>
								Manage manager bookings, payments and refunds.
							</p>
						</div>
						<div class="POSHeaderActions">
							<div class="POSRevenue">
								<span class="POSRevenueLabel">
									TODAY REVENUE
								</span>
								<strong>
									<%= String.format("%,d", dailyRevenue) %> VND
								</strong>
							</div>
							<a
								href="<%= contextPath %>/manager-create-booking"
								class="POSCreateButton">
								+ Create Booking
							</a>
						</div>
					</header>
					
					<!-- Refund Result Message -->
					<% 
						if ("success".equalsIgnoreCase(refundStatus)) { 
					%>
							<div class="POSMessage POSMessageSuccess">
								Refund requested successfully.
							</div>
					<% 
						} 
						else if ("error".equalsIgnoreCase(refundStatus)) { 
					%>
							<div class="POSMessage POSMessageError">
								<%= refundMessage != null ? refundMessage : "Refund request failed." %>
							</div>
					<% 
						} 
					%>
					
					<!-- Booking History -->
					<section class="POSSection">
						<div class="POSSectionHeader">
							<div>
								<h2>Booking History</h2>
								<p>
									Manager bookings and payment records.
								</p>
							</div>
							<span class="POSBookingCount">
								<%= bookingHistory != null ? bookingHistory.size() : 0 %>
								bookings
							</span>
						</div>
						<div class="POSBookingList">
							<% 
								if (bookingHistory != null && !bookingHistory.isEmpty()) {
									for (Map<String, Object> booking : bookingHistory) {
										int bookingId = (Integer) booking.get("bookingId");
										String movieName = (String) booking.get("movieName");
										String theaterName = (String) booking.get("theaterName");
										String roomName = (String) booking.get("roomName");
										String bookingStatus = (String) booking.get("bookingStatus");
										String paymentStatus = (String) booking.get("paymentStatus");
										String paymentMethod = (String) booking.get("paymentMethod");
										String transactionCode = (String) booking.get("transactionCode");
										Object priceObject = booking.get("price");
										int price = priceObject != null ? ((Number) priceObject).intValue() : 0;
										java.util.Date bookAt = (java.util.Date) booking.get("bookAt");
										java.util.Date startAt = (java.util.Date) booking.get("startAt");
										List<Map<String, Object>> seats = (List<Map<String, Object>>) booking.get("seats");
							%>
							<!-- Booking Card -->
							<article class="BookingCard">
								<!-- Booking Header -->
								<div class="BookingCardHeader">
									<div class="BookingTitle">
										<span class="BookingId">
											BOOKING #<%= bookingId %>
										</span>
										<h3>
											<%= movieName != null ? movieName : "Unknown Movie" %>
										</h3>
									</div>
									<span class="BookingStatus <%= "CONFIRMED".equalsIgnoreCase(bookingStatus) ? "BookingStatusSuccess" : "BookingStatusDefault" %>">
										<%= bookingStatus != null ? bookingStatus : "UNKNOWN" %>
									</span>
								</div>
								<!-- Booking Information -->
								<div class="BookingInfo">
									<div class="BookingInfoItem">
										<span>Theater</span>
										<strong>
											<%= theaterName != null ? theaterName : "-" %>
										</strong>
									</div>
									<div class="BookingInfoItem">
										<span>Room</span>
										<strong>
											<%= roomName != null ? roomName : "-" %>
										</strong>
									</div>
									<div class="BookingInfoItem">
										<span>Showtime</span>
										<strong>
											<%= startAt != null ? dateFormat.format(startAt) : "-" %>
										</strong>
									</div>
									<div class="BookingInfoItem">
										<span>Booked At</span>
										<strong>
											<%= bookAt != null ? dateFormat.format(bookAt) : "-" %>
										</strong>
									</div>
								</div>
								<!-- Seats -->
								<div class="BookingSeats">
									<span class="BookingLabel">
										Seats
									</span>
									<div class="BookingSeatList">
										<% 
											if (seats != null && !seats.isEmpty()) {
												for (Map<String, Object> seat : seats) {
													String seatRow = (String) seat.get("seatRow");
													Object seatColObject = seat.get("seatCol");
													int seatCol = seatColObject != null ? ((Number) seatColObject).intValue() : 0;
													String seatType = (String) seat.get("seatType");
													String ticketType = (String) seat.get("ticketType");
										%>
													<%
													    String seatTypeClass = "Normal";
													    if ("VIP".equalsIgnoreCase(seatType)) {
													        seatTypeClass = "VIP";
													    }
													    else if ("BAD".equalsIgnoreCase(seatType)) {
													        seatTypeClass = "Bad";
													    }
													    else if ("COUPLE".equalsIgnoreCase(seatType)) {
													        seatTypeClass = "Couple";
													    }
													%>
													<div class="SeatChip <%= seatTypeClass %>">
													    <strong>
													        <%= seatRow %>-<%= seatCol %>
													    </strong>
													    <span>
													        <%= ticketType != null ? ticketType : "NORMAL" %>
													    </span>
													    <%
													        if (!"NORMAL".equalsIgnoreCase(seatType)) {
													    %>
														        <small>
														            <%= seatType %>
														        </small>
													    <%
													        }
													    %>
													</div>
										<% 
												}
											} 
											else { 
										%>
												<span class="POSEmptyValue">
													No seat information
												</span>
										<% 
											} 
										%>
									</div>
								</div>
								<!-- Payment -->
								<div class="BookingPayment">
									<div class="PaymentInformation">
										<span class="BookingLabel">
											Payment
										</span>
										<div class="PaymentDetails">
											<strong>
												<%= paymentMethod != null ? paymentMethod : "N/A" %>
											</strong>
											<span class="PaymentStatus <%= "SUCCESS".equalsIgnoreCase(paymentStatus) ? "PaymentStatusSuccess" : "PaymentStatusDefault" %>">
												<%= paymentStatus != null ? paymentStatus : "UNPAID" %>
											</span>
											<% 
												if (transactionCode != null && !transactionCode.trim().isEmpty()) { 
											%>
													<span class="TransactionCode">
														<%= transactionCode %>
													</span>
											<% 
												} 
											%>
										</div>
									</div>
									<div class="BookingPrice">
										<span>Total</span>
										<strong>
											<%= String.format("%,d", price) %> VND
										</strong>
									</div>
								</div>
								<!-- Actions -->
								<div class="BookingActions">
									<% 
										if ("SUCCESS".equalsIgnoreCase(paymentStatus) && "CONFIRMED".equalsIgnoreCase(bookingStatus)) { 
									%>
											<button
												type="button"
												class="POSRefundButton"
												onclick="openRefundModal(<%= bookingId %>)">
												Refund
											</button>
									<% 
										} 
									%>
								</div>
							</article>
							<% 
									}
								} 
								else { 
							%>
									<!-- Empty State -->
									<div class="POSEmptyState">
										<div class="POSEmptyIcon">
											—
										</div>
										<h3>
											No bookings yet
										</h3>
										<p>
											Create a booking to see it here.
										</p>
										<a
											href="<%= contextPath %>/manager-create-booking"
											class="POSCreateButton">
											Create Booking
										</a>
									</div>
							<% 
								} 
							%>
						</div>
					</section>
					<!-- Refund Modal -->
					<div id="refundModal" class="RefundModal">
						<div class="RefundModalBox">
							<button
								type="button"
								class="RefundModalClose"
								onclick="closeRefundModal()"
								aria-label="Close refund dialog">
								×
							</button>
							<h2>
								Confirm Refund
							</h2>
							<p>
								Are you sure you want to refund this booking?
							</p>
							<p class="RefundWarning">
								The payment will be marked as REFUNDED and
								the booking will be removed from the active
								booking history.
							</p>
							<form
								id="refundForm"
								action="<%= contextPath %>/manager-refund"
								method="post">
								<input
									type="hidden"
									id="refundBookingId"
									name="booking_id"
									value="">
								<div class="RefundModalActions">
									<button
										type="button"
										class="RefundCancelButton"
										onclick="closeRefundModal()">
										Cancel
									</button>
									<button
										type="submit"
										class="RefundConfirmButton">
										Yes, Refund
									</button>
								</div>
							</form>
						</div>
					</div>
				</div>
			</main>
		</div>
		<script 
			src="<%= contextPath %>/scripts/pos.js">
		</script>
	</body>
</html>