package com.movieweb.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.movieweb.DAO.ManagersDAO;
import com.movieweb.DAO.MoviesDAO;
import com.movieweb.DAO.RoomsDAO;
import com.movieweb.DAO.ShowtimesDAO;
import com.movieweb.model.Movies;
import com.movieweb.model.Rooms;
import com.movieweb.model.Showtimes;
import com.movieweb.model.Theaters;

public class ManagerShowtimesService {
    private ManagersDAO managersDAO;
    private ShowtimesDAO showtimesDAO;
    private RoomsDAO roomsDAO;
    private MoviesDAO moviesDAO;

    public ManagerShowtimesService() {
        managersDAO = new ManagersDAO();
        showtimesDAO = new ShowtimesDAO();
        roomsDAO = new RoomsDAO();
        moviesDAO = new MoviesDAO();
    }

    public List<Theaters> getTheatersByManagerId(int userId) {
        List<Theaters> theaters = managersDAO.getTheatersByManagerId(userId);
        if (theaters == null) {
            return new ArrayList<>();
        }
        return theaters;
    }

    public boolean isManagerAssignedToTheater(int userId, int theaterId) {
        List<Theaters> theaters = getTheatersByManagerId(userId);
        for (Theaters theater : theaters) {
            if (theater.getTheater_id() == theaterId) {
                return true;
            }
        }
        return false;
    }

    public List<Rooms> getRoomsByTheaterId(int theaterId) {
        List<Rooms> rooms = roomsDAO.getRoomsByTheaterId(theaterId);
        if (rooms == null) {
            return new ArrayList<>();
        }
        return rooms;
    }

    public List<Showtimes> getShowtimesByTheaterAndDate(
            int theaterId,
            LocalDate date) {
        List<Showtimes> showtimes =
                showtimesDAO.getShowtimesByTheaterAndDate(
                        theaterId,
                        date
                );
        if (showtimes == null) {
            return new ArrayList<>();
        }
        return showtimes;
    }

    public List<Movies> getMoviesByIds(List<Integer> movieIds) {
        if (movieIds == null || movieIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Movies> movies = moviesDAO.getMoviesByIds(movieIds);
        if (movies == null) {
            return new ArrayList<>();
        }
        return movies;
    }
}