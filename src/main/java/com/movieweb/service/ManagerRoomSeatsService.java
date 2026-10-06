package com.movieweb.service;

import java.util.Collections;
import java.util.List;

import com.movieweb.DAO.RoomsDAO;
import com.movieweb.DAO.Seat_typesDAO;
import com.movieweb.DAO.SeatsDAO;
import com.movieweb.model.Rooms;
import com.movieweb.model.Seat_types;
import com.movieweb.model.Seats;
import com.movieweb.model.Theaters;

public class ManagerRoomSeatsService {
    private final ManagerTheaterDetailsService theaterService = new ManagerTheaterDetailsService();
    private final RoomsDAO roomsDAO = new RoomsDAO();
    private final SeatsDAO seatsDAO = new SeatsDAO();
    private final Seat_typesDAO seatTypesDAO = new Seat_typesDAO();

    public Rooms getManagedRoom(int managerUserId, int roomId) {
        Rooms room = roomsDAO.getRoomById(roomId);
        if (room == null) {
            return null;
        }
        if (theaterService.getManagedTheater(managerUserId, room.getTheater_id()) == null) {
            return null;
        }
        return room;
    }

    public Theaters getTheater(int managerUserId, int theaterId) {
        return theaterService.getManagedTheater(managerUserId, theaterId);
    }

    public List<Seats> getSeats(int roomId) {
        List<Seats> seats = seatsDAO.getSeatsByRoomId(roomId);
        return seats != null ? seats : Collections.emptyList();
    }

    public List<Seat_types> getSeatTypes() {
        return seatTypesDAO.getAllSeatTypes();
    }
}