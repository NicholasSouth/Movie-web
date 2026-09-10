package com.movieweb.model;

public class Movies {
	private int movie_id;
	private String movie_name;
	private String description;
	private String age_rating;
	private double avg_rating;
	private int duration_minute;
	private String available_from;
	private String available_until;
	private String poster_path;
	private String trailer_path;
	private boolean isActive;
	private String deleted_at;
	//
	public Movies(){
	}
	public int getMovie_id() {
        return movie_id;
    }
    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }
    public String getMovie_name() {
        return movie_name;
    }
    public void setMovie_name(String movie_name) {
        this.movie_name = movie_name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getAge_rating() {
        return age_rating;
    }
    public void setAge_rating(String age_rating) {
        this.age_rating = age_rating;
    }
    public double getAvg_rating() {
        return avg_rating;
    }
    public void setAvg_rating(double avg_rating) {
        this.avg_rating = avg_rating;
    }
    public int getDuration_minute() {
        return duration_minute;
    }
    public void setDuration_minute(int duration_minute) {
        this.duration_minute = duration_minute;
    }
    public String getAvailable_from() {
        return available_from;
    }
    public void setAvailable_from(String available_from) {
        this.available_from = available_from;
    }
    public String getAvailable_until() {
        return available_until;
    }
    public void setAvailable_until(String available_until) {
        this.available_until = available_until;
    }
    public String getPoster_path() {
        return poster_path;
    }
    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }
    public String getTrailer_path() {
        return trailer_path;
    }
    public void setTrailer_path(String trailer_path) {
        this.trailer_path = trailer_path;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    public String getDeleted_at() {
        return deleted_at;
    }
    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }
}
