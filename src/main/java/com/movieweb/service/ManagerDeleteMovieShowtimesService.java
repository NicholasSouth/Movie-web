package com.movieweb.service;

import com.movieweb.DAO.ShowtimesDAO;

public class ManagerDeleteMovieShowtimesService {
    private ShowtimesDAO showtimesDAO;

    public ManagerDeleteMovieShowtimesService() {showtimesDAO = new ShowtimesDAO();}

    // {deletable, blocked}, or null when the query failed
    public int[] getSummary(int userId, int movieId) {
        return showtimesDAO.getMovieShowtimeCountsByManager(userId, movieId);
    }

    // Number of deleted showtimes, or null when the update failed
    public Integer deleteMovieShowtimes(int userId, int movieId) {
        int deleted = showtimesDAO.softDeleteMovieShowtimesByManager(userId, movieId);
        return deleted < 0 ? null : deleted;
    }
}