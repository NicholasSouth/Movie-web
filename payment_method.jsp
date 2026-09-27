<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <title>Payment</title>

    <!-- Main CSS -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/main.css">

    <!-- Payment CSS -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/payment.css">
</head>

<body>

<div class="payment-page">

    <!-- ========================= -->
    <!-- PAYMENT METHODS -->
    <!-- ========================= -->

    <section class="payment-section">

        <div class="payment-section-header">
            <div>
                <h1>Payment</h1>
                <p>Payment Methods</p>
            </div>
        </div>


        <div class="payment-method-list">

            <!-- VISA -->
            <div class="payment-method-card">

                <div class="payment-method-left">

                    <div class="payment-method-icon visa-icon">
                        VISA
                    </div>

                    <div class="payment-method-info">

                        <h3>VISA</h3>

                        <p>
                            Card Number
                        </p>

                        <p>
                            Expired Date
                        </p>

                    </div>

                </div>


                <div class="payment-method-actions">

                    <button
                        type="button"
                        class="payment-btn payment-btn-select">
                        Select
                    </button>

                    <button
                        type="button"
                        class="payment-btn payment-btn-remove">
                        Remove
                    </button>

                </div>

            </div>


            <!-- CASH -->
            <div class="payment-method-card">

                <div class="payment-method-left">

                    <div class="payment-method-icon">
                        $
                    </div>

                    <div class="payment-method-info">

                        <h3>Cash</h3>

                        <p>
                            Cash payment
                        </p>

                        <span class="payment-role">
                            Theatre Manager / Admin only
                        </span>

                    </div>

                </div>


                <div class="payment-method-actions">

                    <button
                        type="button"
                        class="payment-btn payment-btn-select">
                        Select
                    </button>

                </div>

            </div>


            <!-- BANKING QR -->
            <div class="payment-method-card">

                <div class="payment-method-left">

                    <div class="payment-method-icon qr-icon">
                        QR
                    </div>

                    <div class="payment-method-info">

                        <h3>Banking QR</h3>

                        <p>
                            Banking QR payment
                        </p>

                        <span class="payment-role">
                            Theatre Manager / Admin only
                        </span>

                    </div>

                </div>


                <div class="payment-method-actions">

                    <button
                        type="button"
                        class="payment-btn payment-btn-select">
                        Select
                    </button>

                </div>

            </div>

        </div>

    </section>


    <!-- ========================= -->
    <!-- ADD PAYMENT METHOD -->
    <!-- ========================= -->

    <section class="payment-section">

        <div class="payment-section-title">

            <h2>Add Payment Method</h2>

        </div>


        <div class="payment-form">

            <div class="payment-form-group">

                <label for="paymentMethod">
                    Payment Method
                </label>

                <select
                    id="paymentMethod"
                    name="paymentMethod">

                    <option value="VISA">
                        VISA
                    </option>

                    <option value="CASH">
                        Cash
                    </option>

                    <option value="QR">
                        Banking QR
                    </option>

                </select>

            </div>


            <div class="payment-form-group">

                <label for="cardNumber">
                    Card Number
                </label>

                <input
                    type="text"
                    id="cardNumber"
                    name="cardNumber"
                    placeholder="Card Number">

            </div>


            <div class="payment-form-group">

                <label for="expiredDate">
                    Expired Date
                </label>

                <input
                    type="text"
                    id="expiredDate"
                    name="expiredDate"
                    placeholder="MM/YY">

            </div>


            <button
                type="button"
                class="payment-add-btn">
                Add Payment Method
            </button>

        </div>

    </section>


    <!-- ========================= -->
    <!-- PAYMENT STATUS -->
    <!-- ========================= -->

    <section class="payment-section">

        <div class="payment-section-title">

            <h2>Payment Status</h2>

        </div>


        <div class="payment-status-list">

            <span class="payment-status status-unpaid">
                UNPAID
            </span>

            <span class="payment-status status-processing">
                PROCESSING
            </span>

            <span class="payment-status status-paid">
                PAID
            </span>

            <span class="payment-status status-failed">
                FAILED
            </span>

            <span class="payment-status status-refunded">
                REFUNDED
            </span>

        </div>

    </section>


    <!-- ========================= -->
    <!-- PAYMENT HISTORY -->
    <!-- ========================= -->

    <section class="payment-section">

        <div class="payment-section-title">

            <h2>Payment History</h2>

        </div>


        <div class="payment-history-container">

            <table class="payment-history">

                <thead>

                    <tr>
                        <th>Payment Amount</th>
                        <th>Payment Date</th>
                        <th>Payment Method</th>
                        <th>Payment Status</th>
                    </tr>

                </thead>

                <tbody>

                    <!-- Payment history will be displayed here -->

                </tbody>

            </table>

        </div>

    </section>

</div>

</body>
</html>