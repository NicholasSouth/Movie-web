function sendVerificationCode() {
    const account =
        document.getElementById("account").value.trim();
    if (account === "") {
        alert(
            "Please enter your email or phone number first."
        );
        document.getElementById("account").focus();
        return;
    }
    /*
     * For now, verification is sent directly
     * to the entered email.
     *
     * Later the ForgotPasswordServlet will:
     * email/phone -> find user -> get user's email
     */
    const emailPattern =
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(account)) {
        alert(
            "Please enter your account email."
        );
        return;
    }
    const button =
        document.querySelector(".send-button");
    button.disabled = true;
    button.textContent = "Sending...";
    fetch(
        "send-verification",
        {
            method: "POST",
            headers: {
                "Content-Type":
                    "application/x-www-form-urlencoded"
            },
            body:
                "email=" +
                encodeURIComponent(account) +
                "&purpose=forgot_password"
        }
    )
    .then(response => response.text())
    .then(message => {
        alert(message);
        button.disabled = false;
        button.textContent = "Send Code";
    })
    .catch(error => {
        console.error(error);
        alert(
            "Unable to send verification code. " +
            "Please try again."
        );
        button.disabled = false;
        button.textContent = "Send Code";
    });
}

function validateForgotPasswordForm() {
    const account =
        document.getElementById("account").value.trim();
    const password =
        document.getElementById("password").value;
    const confirmPassword =
        document.getElementById("confirm_password").value;
    const verificationCode =
        document.getElementById("verification_code").value.trim();
		
    if (account === "") {
        alert(
            "Please enter your email or phone number."
        );
        return false;
    }
    if (password === "") {
        alert("Please enter a new password.");
        return false;
    }
    if (confirmPassword === "") {
        alert("Please confirm your new password.");
        return false;
    }
    if (password !== confirmPassword) {
        alert("Passwords do not match.");
        return false;
    }
    if (verificationCode === "") {
        alert("Please enter the verification code.");
        return false;
    }
    return true;
}