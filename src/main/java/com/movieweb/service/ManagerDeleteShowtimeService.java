package com.movieweb.service;

import com.movieweb.DAO.ManagersDAO;
import com.movieweb.DAO.RoomsDAO;
import com.movieweb.DAO.ShowtimesDAO;
import com.movieweb.model.Rooms;
import com.movieweb.model.Showtimes;

public class ManagerDeleteShowtimeService {
    private ManagersDAO managersDAO;
    private RoomsDAO roomsDAO;
    private ShowtimesDAO showtimesDAO;

    public ManagerDeleteShowtimeService() {
        managersDAO = new ManagersDAO();
        roomsDAO = new RoomsDAO();
        showtimesDAO = new ShowtimesDAO();
    }

    public String deleteShowtime(int userId, int showtimeId) {
        Showtimes showtime = showtimesDAO.getShowtimeById(showtimeId);
        if (showtime == null || showtime.getDeleted_at() != null) {
            return "Showtime does not exist.";
        }

        Rooms room = roomsDAO.getRoomById(showtime.getRoom_id());
        if (room == null) {
            return "Room does not exist.";
        }

        if (!isManagerAssignedToTheater(userId, room.getTheater_id())) {
            return "You are not assigned to this theater.";
        }

        if (showtimesDAO.hasActiveBooking(showtimeId)) {
            return "This showtime already has active bookings and cannot be deleted.";
        }

        if (!showtimesDAO.softDeleteShowtime(showtimeId)) {
            return "Failed to delete showtime. A booking may have just been made.";
        }
        return null;
    }

    private boolean isManagerAssignedToTheater(int userId, int theaterId) {
        return managersDAO.getTheatersByManagerId(userId).stream()
                .anyMatch(theater -> theater.getTheater_id() == theaterId);
    }
}