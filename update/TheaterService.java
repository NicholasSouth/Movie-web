package com.movieweb.service;

import java.util.List;

import com.movieweb.DAO.TheatersDAO;
import com.movieweb.model.Theaters;

public class TheaterService {
    private TheatersDAO theatersDAO;
    public TheaterService() {theatersDAO = new TheatersDAO();}
    public List<Theaters> getTheaters() {return theatersDAO.getTheaters();}
}
