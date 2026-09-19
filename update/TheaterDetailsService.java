package com.movieweb.service;

import java.util.List;

import com.movieweb.DAO.RoomsDAO;
import com.movieweb.DAO.TheatersDAO;
import com.movieweb.model.Rooms;
import com.movieweb.model.Theaters;

public class TheaterDetailsService {
    private TheatersDAO theatersDAO;
    private RoomsDAO roomsDAO;
    public TheaterDetailsService() {
        theatersDAO = new TheatersDAO();
        roomsDAO = new RoomsDAO();
    }
    public Theaters getTheaterById(int theater_id) {return theatersDAO.getTheaterById(theater_id);}
    public List<Rooms> getRoomsByTheaterId(int theaterId) {return roomsDAO.getRoomsByTheaterId(theaterId);}
}
