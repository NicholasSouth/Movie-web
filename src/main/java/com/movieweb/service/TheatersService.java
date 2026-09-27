package com.movieweb.service;

import java.util.List;

import com.movieweb.DAO.TheatersDAO;
import com.movieweb.model.Theaters;

public class TheatersService {
    private TheatersDAO theatersDAO;
    public TheatersService() {theatersDAO = new TheatersDAO();}
    public List<Theaters> getAllTheaters() {return theatersDAO.getAllTheaters();}
}