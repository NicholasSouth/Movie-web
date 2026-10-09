<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
	String contextPath = request.getContextPath();
	List<Map<String, Object>> refundHistory = (List<Map<String, Object>>) request.getAttribute("refundHistory");
	String selectedStatus = request.getParameter("status");
	if (!"REFUNDED".equals(selectedStatus) && !"CANCELLED".equals(selectedStatus)) {
		selectedStatus = "ALL";
	}
	String refundError = (String) request.getAttribute("refundError");
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	request.setAttribute("currentManagerPage", "refunds");
	int displayedCount = 0;
	if (refundHistory != null) {
		for (Map<String, Object> booking : refundHistory) {
			String status = (String) booking.get("bookingStatus");
			if ("ALL".equals(selectedStatus) || selectedStatus.equalsIgnoreCase(status)) {
				displayedCount++;
			}
		}
	}
%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<title>Refund History | PhnetPhlyx</title>
		<link rel="stylesheet" href="<%= contextPath %>/styles/main_manager.css">
		<link rel="stylesheet" href="<%= contextPath %>/styles/pos_refund.css">
	</head>
	
	<body>
		<div class="Layout2">
			<%@ include file="components/header_manager.jsp" %>
			<%@ include file="components/manager_navigation.jsp" %>
			<main class="ManagerMain">
				<div class="POSRefundPage">
					<!-- Page Header -->
					<header class="RefundPageHeader">
						<div>
							<span class="RefundBreadcrumb">
								Process Orders / Refunds
							</span>
							<h1>Refund History</h1>
							<p>
								Review refund requests and completed cancellations 
								for your selected theater.
							</p>
						</div>
						<a href="<%= contextPath %>/sales-bookings" class="RefundBackButton">
							Back to Sales
						</a>
					</header>
					<!-- Error Message -->
					<%
						if (refundError != null) {
					%>
							<div class="RefundMessage RefundMessageError">
								<%= refundError %>
							</div>
					<%
						}
					%>
					<!-- Status Explanation -->
					<section class="RefundStatusGuide">
						<div class="RefundGuideItem">
							<span class="RefundStatus RefundStatusPending">
								REFUNDED
							</span>
							<p>
								Refund request submitted. Awaiting admin review.
							</p>
						</div>
						<div class="RefundGuideItem">
							<span class="RefundStatus RefundStatusCancelled">
								CANCELLED
							</span>
							<p>
								Admin-approved cancellation completed.
							</p>
						</div>
					</section>
					<!-- Refund History -->
					<section class="RefundSection">
						<div class="RefundSectionHeader">
							<div>
								<h2>Refund Records</h2>
								<p>
									Booking and payment statuses are shown separately.
								</p>
							</div>
							<span class="RefundCount">
								<%= displayedCount %> records
							</span>
						</div>
						<!-- Status Filter -->
						<form class="RefundFilter" action="<%= contextPath %>/manager-refunds" method="get">
							<label for="statusFilter">Booking status</label>
							<select id="statusFilter" name="status">
								<option value="ALL" <%= "ALL".equals(selectedStatus) ? "selected" : "" %>>
									All Refund Records
								</option>
								<option value="REFUNDED" <%= "REFUNDED".equals(selectedStatus) ? "selected" : "" %>>
									Awaiting Admin Review
								</option>
								<option value="CANCELLED" <%= "CANCELLED".equals(selectedStatus) ? "selected" : "" %>>
									Cancelled
								</option>
							</select>
							<button type="submit" class="RefundFilterButton">
								Apply Filter
							</button>
						</form>
						<div class="RefundBookingList">
						<%
							if (refundHistory != null && !refundHistory.isEmpty()) {
								for (Map<String, Object> booking : refundHistory) {
									String bookingStatus = (String) booking.get("bookingStatus");
									if (!"ALL".equals(selectedStatus) && !selectedStatus.equalsIgnoreCase(bookingStatus)) {
										continue;
									}
									int bookingId = ((Number) booking.get("bookingId")).intValue();
									String movieName = (String) booking.get("movieName");
									String theaterName = (String) booking.get("theaterName");
									String roomName = (String) booking.get("roomName");
									String paymentStatus = (String) booking.get("paymentStatus");
									String paymentMethod = (String) booking.get("paymentMethod");
									String transactionCode = (String) booking.get("transactionCode");
									Object priceObject = booking.get("price");
									int price = priceObject != null ? ((Number) priceObject).intValue() : 0;
									java.util.Date bookAt = (java.util.Date) booking.get("bookAt");
									java.util.Date startAt = (java.util.Date) booking.get("startAt");
									boolean cancelled = "CANCELLED".equalsIgnoreCase(bookingStatus);
						%>
									<article class="RefundBookingCard">
										<!-- Card Header -->
										<div class="RefundCardHeader">
											<div>
												<span class="RefundBookingId">
													BOOKING #<%= bookingId %>
												</span>
												<h3>
													<%= movieName != null ? movieName : "Unknown Movie" %>
												</h3>
											</div>
											<span class="RefundStatus <%= cancelled ? "RefundStatusCancelled" : "RefundStatusPending" %>">
												<%= bookingStatus != null ? bookingStatus : "UNKNOWN" %>
											</span>
										</div>
										<!-- Booking Information -->
										<div class="RefundInfoGrid">
											<div class="RefundInfoItem">
												<span>Theater</span>
												<strong><%= theaterName != null ? theaterName : "-" %></strong>
											</div>
											<div class="RefundInfoItem">
												<span>Room</span>
												<strong><%= roomName != null ? roomName : "-" %></strong>
											</div>
											<div class="RefundInfoItem">
												<span>Showtime</span>
												<strong><%= startAt != null ? dateFormat.format(startAt) : "-" %></strong>
											</div>
											<div class="RefundInfoItem">
												<span>Original Booking Date</span>
												<strong><%= bookAt != null ? dateFormat.format(bookAt) : "-" %></strong>
											</div>
										</div>
										<!-- Payment Information -->
										<div class="RefundPayment">
											<div class="RefundPaymentDetails">
												<span class="RefundFieldLabel">Payment Method</span>
												<strong><%= paymentMethod != null ? paymentMethod : "N/A" %></strong>
											</div>
											<div class="RefundPaymentDetails">
												<span class="RefundFieldLabel">Payment Status</span>
												<span class="RefundPaymentStatus <%= "REFUNDED".equalsIgnoreCase(paymentStatus) ? "RefundPaymentStatusComplete" : "RefundPaymentStatusDefault" %>">
													<%= paymentStatus != null ? paymentStatus : "UNKNOWN" %>
												</span>
											</div>
											<div class="RefundPaymentDetails">
												<span class="RefundFieldLabel">Transaction Code</span>
												<strong><%= transactionCode != null && !transactionCode.trim().isEmpty() ? transactionCode : "-" %></strong>
											</div>
											<div class="RefundTotal">
												<span>Booking Total</span>
												<strong><%= String.format("%,d", price) %> VND</strong>
											</div>
										</div>
										<!-- Status Explanation -->
										<div class="RefundCardNote <%= cancelled ? "RefundCardNoteComplete" : "RefundCardNotePending" %>">
											<%
												if (cancelled) {
											%>
													This booking has been cancelled. 
													Payment status: <%= paymentStatus != null ? paymentStatus : "UNKNOWN" %>.
											<%
												} else {
											%>
													This refund request is awaiting admin review. 
													The payment has not necessarily been refunded yet.
											<%
												}
											%>
										</div>
									</article>
						<%
								}
							}
						%>
						<%
							if (displayedCount == 0) {
						%>
								<div class="RefundEmptyState">
									<div class="RefundEmptyIcon">—</div>
									<h3>No refund records found</h3>
									<p>
										Refund requests and cancelled bookings 
										will appear here.
									</p>
								</div>
						<%
							}
						%>
						</div>
					</section>
				</div>
			</main>
		</div>
	</body>
</html>