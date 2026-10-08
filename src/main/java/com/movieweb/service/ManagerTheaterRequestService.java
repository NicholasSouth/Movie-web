package com.movieweb.service;

import java.util.List;

import com.movieweb.DAO.TheatersDAO;
import com.movieweb.DAO.ManagersDAO;
import com.movieweb.model.Theaters;
import com.movieweb.model.Users;

import jakarta.servlet.http.HttpSession;

public class ManagerTheaterRequestService {
    private static final String LAST_SENT_KEY = "theaterRequestLastSent";
    private static final long COOLDOWN_MS = 60_000; // 1 request per minute per session
    private static final int MAX_REASON_LENGTH = 500;

    private final TheatersDAO theatersDAO = new TheatersDAO();
    private final ManagersDAO managersDAO = new ManagersDAO();
    private final EmailService emailService = new EmailService();

    // Active theaters the manager is not managing yet
    public List<Theaters> getRequestableTheaters(int userId) {
        return theatersDAO.getTheatersNotManagedByUser(userId);
    }

    // Returns null on success, otherwise an error message
    public String requestTheater(Users user, String theaterIdParam, HttpSession session) {
        if (theaterIdParam == null || theaterIdParam.trim().isEmpty()) return "Please select a theater.";

        int theaterId;
        try {
            theaterId = Integer.parseInt(theaterIdParam.trim());
        } catch (NumberFormatException e) {
            return "Invalid theater.";
        }

        // Cooldown: stop the manager from spamming the admin's mailbox
        Long lastSent = (Long) session.getAttribute(LAST_SENT_KEY);
        if (lastSent != null && System.currentTimeMillis() - lastSent < COOLDOWN_MS) {
            return "Please wait a moment before sending another request.";
        }

        // The theater must be active and not already managed by this user
        Theaters target = null;
        for (Theaters t : getRequestableTheaters(user.getUserId())) {
            if (t.getTheater_id() == theaterId) {
                target = t;
                break;
            }
        }
        if (target == null) {
            return "This theater is not available to request (already managed by you or inactive).";
        }

        boolean sent = emailService.sendManagerTheaterRequest(user.getUserId(), user.getUsername(),
                target.getTheater_id(), target.getTheater_name());
        if (!sent) return "Could not send your request. Please try again later.";

        session.setAttribute(LAST_SENT_KEY, System.currentTimeMillis());
        return null;
    }

    // Returns null on success, otherwise an error message
    public String requestLeaveTheater(Users user, String theaterIdParam, String reason, HttpSession session) {
        if (theaterIdParam == null || theaterIdParam.trim().isEmpty()) return "Please select a theater.";

        int theaterId;
        try {
            theaterId = Integer.parseInt(theaterIdParam.trim());
        } catch (NumberFormatException e) {
            return "Invalid theater.";
        }

        // Enforce max length of reason textarea
        String cleanReason = (reason == null) ? "" : reason.trim();
        if (cleanReason.length() > MAX_REASON_LENGTH) cleanReason = cleanReason.substring(0, MAX_REASON_LENGTH);

        Long lastSent = (Long) session.getAttribute(LAST_SENT_KEY);
        if (lastSent != null && System.currentTimeMillis() - lastSent < COOLDOWN_MS) {
            return "Please wait a moment before sending another request.";
        }

        Theaters target = null;
        List<Theaters> managed = managersDAO.getTheatersByManagerId(user.getUserId());
        if (managed != null) {
            for (Theaters t : managed) {
                if (t.getTheater_id() == theaterId) {
                    target = t;
                    break;
                }
            }
        }
        if (target == null) return "You are not managing this theater.";

        boolean sent = emailService.sendManagerLeaveTheaterRequest(user.getUserId(), user.getUsername(),
                target.getTheater_id(), target.getTheater_name(), cleanReason);
        if (!sent) return "Could not send your request. Please try again later.";

        session.setAttribute(LAST_SENT_KEY, System.currentTimeMillis());
        return null;
    }
}