<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<meta 
			name="viewport" 
			content="width=device-width, initial-scale=1.0">
		<title>Forgot Password - PhnetPhlyx</title>
		<link 
			rel="stylesheet"
			href="${pageContext.request.contextPath}/styles/forget.css">
	</head>
	<body>
		<main class="forgot-page">
		    <div class="forgot-container">
		        <!-- Website Name -->
		        <a href="${pageContext.request.contextPath}/main.jsp" class="website-name">
		            PhnetPhlyx
		        </a>
		        <h1>
		            Forgot Password
		        </h1>
		        <p class="subtitle">
		            Reset your password using your email or phone number.
		        </p>
		
		        <!-- Server Error -->
		        <% 
		        	if (request.getAttribute("error") != null) { 
		        %>
			            <div class="error-message">
			                <%= request.getAttribute("error") %>
			            </div>
		        <% 
		        	} 
		        %>
		
		        <!-- Server Success -->
		        <% 
		        	if (request.getAttribute("message") != null) { 
		        %>
			            <div class="success-message">
			                <%= request.getAttribute("message") %>
			            </div>
		        <% 
		        	} 
		        %>
		
		        <form
		            class="forgot-form"
		            action="${pageContext.request.contextPath}/forgot-password"
		            method="post"
		            onsubmit="return validateForgotForm();">
		            <!-- Account -->
		            <div class="input-group">
		                <label for="account">
		                    Email or Phone Number
		                </label>
		                <input
		                    type="text"
		                    id="account"
		                    name="account"
		                    placeholder="Enter your email or phone number"
		                    required
		                >
		            </div>
		
		            <p class="verification-note">
		                We will send the verification code to the email
		                associated with your account.
		            </p>
		
		            <!-- New Password -->
		            <div class="input-group">
		                <label for="new-password">
		                    New Password
		                </label>
		                <div class="password-wrapper">
		                    <input
		                        type="password"
		                        id="new-password"
		                        name="new_password"
		                        placeholder="Enter your new password"
		                        required
		                    >
		                    <button 
		                    	type="button" 
		                    	class="password-toggle" 
		                    	onclick="togglePassword('new-password', this)">
		                        Show
		                    </button>
		                </div>
		            </div>
		
		            <!-- Confirm Password -->
		            <div class="input-group">
		                <label for="confirm-password">
		                    Confirm New Password
		                </label>
		                <div class="password-wrapper">
		                    <input
		                        type="password"
		                        id="confirm-password"
		                        name="confirm_password"
		                        placeholder="Confirm your new password"
		                        required
		                    >
		                    <button 
		                    	type="button" 
		                    	class="password-toggle" 
		                    	onclick="togglePassword('confirm-password', this)">
		                        Show
		                    </button>
		                </div>
		            </div>
		
		            <!-- Email Verification -->
		            <div class="verification-section">
		                <div class="verification-title">
		                    Email Verification
		                </div>
		                <p class="verification-note">
		                    Enter the verification code sent to your
		                    account email.
		                </p>
		                <div class="verification-row">
		                    <input
		                        type="text"
		                        id="verification-code"
		                        name="verification_code"
		                        placeholder="Enter verification code"
		                        maxlength="6"
		                        inputmode="numeric"
		                        autocomplete="one-time-code"
		                        required
		                    >
		                    <button 
		                    	type="button" 
		                    	class="send-button" 
		                    	onclick="sendVerificationCode()">
		                        Send Code
		                    </button>
		                </div>
		            </div>
		
		            <!-- Reset Password -->
		            <button type="submit" class="reset-button">
		                Reset Password
		            </button>
		        </form>
		
		        <!-- Back to Login -->
		        <p class="login-text">
		            Remember your password?
		            <a href="${pageContext.request.contextPath}/log_in.jsp" class="login-link">
		                Back to Login
		            </a>
		        </p>
		    </div>
		</main>
		
		<script
			src="${pageContext.request.contextPath}/scripts/forget.js">		    
		</script>
		<script 
			src="${pageContext.request.contextPath}/scripts/password_toggle.js">
		</script>
	</body>
</html>