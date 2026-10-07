package com.movieweb.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.movieweb.DAO.ManagersDAO;
import com.movieweb.DAO.MoviesDAO;
import com.movieweb.DAO.RoomsDAO;
import com.movieweb.DAO.ShowtimesDAO;
import com.movieweb.model.Movies;
import com.movieweb.model.Rooms;
import com.movieweb.model.Showtime_ticket_types;

public class ManagerAddShowtimeService {
    private static final int NORMAL_TICKET_TYPE_ID = 1;
    private static final int LAST_TICKET_TYPE_ID = 4;
    private static final int DISCOUNT_PERCENT = 20;

    private ManagersDAO managersDAO;
    private MoviesDAO moviesDAO;
    private RoomsDAO roomsDAO;
    private ShowtimesDAO showtimesDAO;

    public ManagerAddShowtimeService() {
        managersDAO = new ManagersDAO();
        moviesDAO = new MoviesDAO();
        roomsDAO = new RoomsDAO();
        showtimesDAO = new ShowtimesDAO();
    }

    public String addShowtime(int userId, String movieName, int roomId, Timestamp startAt,
            Timestamp endAt, int price) {
        if (movieName == null || movieName.trim().isEmpty()) {
            return "Movie name is required.";
        }

        if (price <= 0) {
            return "Price must be greater than 0.";
        }

        if (startAt == null || endAt == null) {
            return "Start time and end time are required.";
        }

        if (!startAt.before(endAt)) {
            return "End time must be after start time.";
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (!startAt.after(now)) {
            return "Showtime must start in the future.";
        }

        Rooms room = roomsDAO.getRoomById(roomId);
        if (room == null) {
            return "Room does not exist.";
        }

        if (!room.isActive()) {
            return "Room is not active.";
        }

        Movies movie = moviesDAO.getActiveMovieByName(movieName.trim());
        if (movie == null) {
            return "Movie does not exist or is not active.";
        }

        if (!isManagerAssignedToTheater(
                userId,
                room.getTheater_id())) {
            return "You are not assigned to this theater.";
        }

        if (showtimesDAO.hasOverlappingShowtime(roomId, startAt, endAt)) {
            return "This room already has an overlapping upcoming showtime.";
        }

        boolean inserted = showtimesDAO.insertShowtime(roomId, movie.getMovie_id(), startAt, endAt,
                                                        buildTicketPrices(price));
        if (!inserted) {
            return "Failed to add showtime.";
        }
        return null;
    }

    private List<Showtime_ticket_types> buildTicketPrices(int normalPrice) {
        List<Showtime_ticket_types> ticketPrices = new ArrayList<>();
        int discountedPrice = (int) Math.round(normalPrice * (100 - DISCOUNT_PERCENT) / 100.0);

        for (int ticketTypeId = NORMAL_TICKET_TYPE_ID;
             ticketTypeId <= LAST_TICKET_TYPE_ID;
             ticketTypeId++) {
            Showtime_ticket_types ticketPrice = new Showtime_ticket_types();
            ticketPrice.setTicket_type_id(ticketTypeId);
            ticketPrice.setPrice(ticketTypeId == NORMAL_TICKET_TYPE_ID ? normalPrice : discountedPrice);
            ticketPrices.add(ticketPrice);
        }
        return ticketPrices;
    }

    private boolean isManagerAssignedToTheater(
            int userId,
            int theaterId) {
        return managersDAO
                .getTheatersByManagerId(userId)
                .stream()
                .anyMatch(
                        theater ->
                                theater.getTheater_id() == theaterId
                );
    }
}
