package com.movieweb.model;

public class Showtimes {
	private int showtime_id;
    private int room_id;
    private int movie_id;
    private String start_at;
    private String end_at;
    private String status;
    //
    public Showtimes() {
    }
    public int getShowtime_id() {
        return showtime_id;
    }
    public void setShowtime_id(int showtime_id) {
        this.showtime_id = showtime_id;
    }
    public int getRoom_id() {
        return room_id;
    }
    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }
    public int getMovie_id() {
        return movie_id;
    }
    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }
    public String getStart_at() {
        return start_at;
    }
    public void setStart_at(String start_at) {
        this.start_at = start_at;
    }
    public String getEnd_at() {
        return end_at;
    }
    public void setEnd_at(String end_at) {
        this.end_at = end_at;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
