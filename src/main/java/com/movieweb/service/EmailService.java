package com.movieweb.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.sql.Timestamp;

public class EmailService
{
    //Phnetphlyx's gmail
    private static final String SENDER_EMAIL = "giaydepthanhhuyenbienhoa@gmail.com";
    private static final String APP_PASSWORD = "posk hzrm zqwb fgec";

    // Create Gmail SMTP session
    private Session createMailSession()
    {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        return Session.getInstance(
                properties,
                new Authenticator()
                {
                    @Override
                    protected PasswordAuthentication
                    getPasswordAuthentication()
                    {
                        return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
                    }
                });
    }

    // Send verification code
    public boolean sendVerificationCode(
            String recipientEmail,
            String code,
            String purpose)
    {
        try
        {
            Session session = createMailSession();
            String subject;
            String messageText;
            if ("register".equals(purpose))
            {
                subject = "PhnetPhlyx - Email Verification";
                messageText =
                        "Hello,\n\n"
                        + "Your PhnetPhlyx email verification code is:\n\n"
                        + code
                        + "\n\n"
                        + "This code will expire in 5 minutes.\n\n"
                        + "If you did not request this code, "
                        + "you can safely ignore this email.\n\n"
                        + "PhnetPhlyx";
            }
            else if ("forgot_password".equals(purpose))
            {
                subject = "PhnetPhlyx - Password Reset Verification";
                messageText =
                        "Hello,\n\n"
                        + "Your PhnetPhlyx password reset verification code is:\n\n"
                        + code
                        + "\n\n"
                        + "This code will expire in 5 minutes.\n\n"
                        + "If you did not request this code, "
                        + "you can safely ignore this email.\n\n"
                        + "PhnetPhlyx";
            }
            else if ("change_password".equals(purpose))
            {
                subject = "PhnetPhlyx - Password Change Verification";
                messageText =
                        "Hello,\n\n"
                        + "Your PhnetPhlyx password change verification code is:\n\n"
                        + code
                        + "\n\n"
                        + "This code will expire in 5 minutes.\n\n"
                        + "If you did not request this code, "
                        + "you can safely ignore this email.\n\n"
                        + "PhnetPhlyx";
            }
            else if ("manager_change_password".equals(purpose))
            {
                subject = "PhnetPhlyx - Manager Password Change Verification";
                messageText =
                        "Hello Manager,\n\n"
                        + "Your PhnetPhlyx manager password change "
                        + "verification code is:\n\n"
                        + code
                        + "\n\n"
                        + "This code will expire in 5 minutes.\n\n"
                        + "If you did not request this code, "
                        + "you can safely ignore this email.\n\n"
                        + "PhnetPhlyx";
            }
            else if ("admin_change_password".equals(purpose))
            {
                subject = "PhnetPhlyx - Administrator Password Change Verification";
                messageText =
                        "Hello Administrator,\n\n"
                        + "Your PhnetPhlyx administrator password change "
                        + "verification code is:\n\n"
                        + code
                        + "\n\n"
                        + "This code will expire in 5 minutes.\n\n"
                        + "If you did not request this code, "
                        + "you can safely ignore this email.\n\n"
                        + "PhnetPhlyx";
            }
            else
            {
                return false;
            }

            Message message = new MimeMessage(session);
            message.setFrom( new InternetAddress(SENDER_EMAIL));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(messageText);
            Transport.send(message);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    //Send report comment email
    public boolean sendReport(
            Timestamp commentCreatedAt,
            String commentText,
            String commentUsername,
            String reporterUsername) {
        try {
            Session session = createMailSession();
            String subject = "PhnetPhlyx - Comment Report";
            String messageText =
                    "A comment has been reported on PhnetPhlyx.\n\n" +
                    "Comment author: " + commentUsername + "\n" +
                    "Reporter: " + reporterUsername + "\n" +
                    "Comment created at: " + commentCreatedAt + "\n\n" +
                    "Comment:\n" +
                    commentText + "\n";
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(SENDER_EMAIL)
            );
            message.setSubject(subject);
            message.setText(messageText);
            Transport.send(message);
            return true;
        } 
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //Send refund request email
	public boolean sendRefundRequest(
	        int bookingId,
	        String movieName,
	        String theaterName,
	        String roomName) {
	    try {
	        Session session = createMailSession();
	        Message message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(SENDER_EMAIL));
	        message.setRecipients(
	            Message.RecipientType.TO,
	            InternetAddress.parse(SENDER_EMAIL)
	        );
	        message.setSubject(
	            "PhnetPhlyx - Refund Request #" + bookingId
	        );
	        String content =
	            "A manager has submitted a refund request.\n\n" +
	            "Booking ID: " + bookingId + "\n" +
	            "Movie: " + movieName + "\n" +
	            "Theater: " + theaterName + "\n" +
	            "Room: " + roomName + "\n\n" +
	            "Current booking status: REFUNDED\n" +
	            "Action required: Review the request in the admin panel.\n";
	        message.setText(content);
	        Transport.send(message);
	        return true;
	    } 
	    catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	//Tell the booking owner how the admin decided a refund request
	public boolean sendRefundDecision(
	        String recipientEmail,
	        String fullName,
	        int bookingId,
	        String movieName,
	        String theaterName,
	        String roomName,
	        Timestamp showtimeStart,
	        int paymentAmount,
	        String transactionCode,
	        boolean approved) {
	    if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
	        return false;
	    }
	    try {
	        Session session = createMailSession();
	        Message message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(SENDER_EMAIL));
	        message.setRecipients(
	            Message.RecipientType.TO,
	            InternetAddress.parse(recipientEmail.trim())
	        );
	        message.setSubject(
	            "PhnetPhlyx - Refund " + (approved ? "Approved" : "Declined")
	            + " (Booking #" + bookingId + ")"
	        );
	        String name = (fullName == null || fullName.trim().isEmpty()) ? "there" : fullName.trim();
	        String when = showtimeStart == null
	            ? "--"
	            : new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(showtimeStart);
	        String details =
	            "Booking ID: " + bookingId + "\n" +
	            "Movie: " + movieName + "\n" +
	            "Theater: " + theaterName + " - " + roomName + "\n" +
	            "Showtime: " + when + "\n" +
	            "Payment: " + String.format("%,d", paymentAmount) + " VND"
	            + " (transaction " + transactionCode + ")\n";
	        String content;
	        if (approved) {
	            content =
	                "Hello " + name + ",\n\n" +
	                "The refund request for the booking below has been approved. " +
	                "The booking is now cancelled and its payment is marked as refunded.\n\n" +
	                details + "\n" +
	                "If you have any questions, please contact PhnetPhlyx.\n";
	        } else {
	            content =
	                "Hello " + name + ",\n\n" +
	                "The refund request for the booking below has been declined. " +
	                "The booking remains valid and your tickets are unchanged.\n\n" +
	                details + "\n" +
	                "If you have any questions, please contact PhnetPhlyx.\n";
	        }
	        message.setText(content);
	        Transport.send(message);
	        return true;
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	//Tell a manager how the admin decided their theater assign/remove request
	public boolean sendTheaterRequestDecision(
	        String recipientEmail,
	        String fullName,
	        String theaterName,
	        String requestType,
	        boolean approved,
	        String adminMessage) {
	    if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
	        return false;
	    }
	    try {
	        Session session = createMailSession();
	        Message message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(SENDER_EMAIL));
	        message.setRecipients(
	            Message.RecipientType.TO,
	            InternetAddress.parse(recipientEmail.trim())
	        );
	        message.setSubject(
	            "PhnetPhlyx - Theater Request " + (approved ? "Approved" : "Declined")
	        );
	        String name = (fullName == null || fullName.trim().isEmpty()) ? "there" : fullName.trim();
	        boolean assign = "ASSIGN".equals(requestType);
	        String what = assign
	            ? "to be assigned to " + theaterName
	            : "to be removed from " + theaterName;
	        StringBuilder content = new StringBuilder();
	        content.append("Hello ").append(name).append(",\n\n");
	        if (approved) {
	            content.append("Your request ").append(what).append(" has been approved.");
	            content.append(assign
	                ? " You can now select this theater on your dashboard.\n\n"
	                : " You no longer have access to this theater.\n\n");
	        } else {
	            content.append("Your request ").append(what).append(" has been declined.");
	            content.append(" Your theater assignments are unchanged.\n\n");
	        }
	        if (adminMessage != null && !adminMessage.trim().isEmpty()) {
	            content.append("Message from the administrator:\n").append(adminMessage.trim()).append("\n\n");
	        }
	        content.append("If you have any questions, please contact PhnetPhlyx.\n");
	        message.setText(content.toString());
	        Transport.send(message);
	        return true;
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

}
