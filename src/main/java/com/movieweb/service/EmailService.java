package com.movieweb.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService
{
    //Phnetphlyx's gmail
    private static final String SENDER_EMAIL = System.getenv("MOVIEWEB_MAIL_USER");
    private static final String APP_PASSWORD = System.getenv("MOVIEWEB_MAIL_PASSWORD");

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
            if (SENDER_EMAIL == null || APP_PASSWORD == null) {
                System.err.println("Email is not configured: set MOVIEWEB_MAIL_USER and MOVIEWEB_MAIL_PASSWORD.");
                return false;
            }
            Session session = createMailSession();
            String subject;
            if ("register".equals(purpose))
            {
                subject = "PhnetPhlyx - Email Verification";
            }
            else
            {
                subject = "PhnetPhlyx - Password Reset Verification";
            }

            String messageText =
                    "Hello,\n\n"
                    + "Your PhnetPhlyx verification code is:\n\n"
                    + code
                    + "\n\n"
                    + "This code will expire in 5 minutes.\n\n"
                    + "If you did not request this code, "
                    + "you can safely ignore this email.\n\n"
                    + "PhnetPhlyx";

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
}