package com.movieweb.service;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

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

    // Send manager-theater request email to admin
    public boolean sendManagerTheaterRequest(int userId, String username, int theaterId,
                                             String theaterName) {
        try {
            Session session = createMailSession();
            String subject = "PhnetPhlyx - Manager Theater Request";
            String messageText =
                    "A manager has requested to manage a theater on PhnetPhlyx.\n\n" +
                    "Username: " + username + "\n" +
                    "User ID: " + userId + "\n" +
                    "Theater ID: " + theaterId + "\n" +
                    "Theater name: " + theaterName + "\n\n" +
                    "Please review this request and approve it if appropriate, " +
                    "so that this user can manage the theater above.\n";
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(SENDER_EMAIL));
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

    // Send manager request to stop managing a theater to admin
    public boolean sendManagerLeaveTheaterRequest(int userId, String username, int theaterId,
                                                  String theaterName, String reason) {
        try {
            Session session = createMailSession();
            String subject = "PhnetPhlyx - Manager Leave Theater Request";
            String messageText =
                    "A manager has requested to stop managing a theater on PhnetPhlyx.\n\n" +
                    "Username: " + username + "\n" +
                    "User ID: " + userId + "\n" +
                    "Theater ID: " + theaterId + "\n" +
                    "Theater name: " + theaterName + "\n\n" +
                    "Reason:\n" +
                    ((reason == null || reason.isEmpty()) ? "(no reason provided)" : reason) + "\n\n" +
                    "Please review this request and remove this user from the theater above " +
                    "if appropriate.\n";
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(SENDER_EMAIL));
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

    // Send manager request to add a movie to admin
    public boolean sendManagerMovieRequest(int userId, String username,
                                           String movieName, String trailerUrl) {
        try {
            Session session = createMailSession();
            String subject = "PhnetPhlyx - Manager Movie Request";
            String messageText =
                    "A manager has requested to add a movie on PhnetPhlyx.\n\n" +
                    "Username: " + username + "\n" +
                    "User ID: " + userId + "\n\n" +
                    "Movie name: " + movieName + "\n" +
                    "Trailer link: " + trailerUrl + "\n\n" +
                    "Please review this request and add the movie if appropriate, " +
                    "so that this manager can create showtimes for it.\n";
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(SENDER_EMAIL));
            message.setSubject(subject);
            message.setText(messageText, "UTF-8");
            Transport.send(message);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}