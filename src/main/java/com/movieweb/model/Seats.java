package com.movieweb.model;

public class Seats {
	private int seat_id;
    private int room_id;
    private String seat_row;
    private int seat_col;
    private int seat_type_id;
    private boolean isActive;
    //
    public Seats() {
    }
    public int getSeat_id() {
        return seat_id;
    }
    public void setSeat_id(int seat_id) {
        this.seat_id = seat_id;
    }
    public int getRoom_id() {
        return room_id;
    }
    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }
    public String getSeat_row() {
        return seat_row;
    }
    public void setSeat_row(String seat_row) {
        this.seat_row = seat_row;
    }
    public int getSeat_col() {
        return seat_col;
    }
    public void setSeat_col(int seat_col) {
        this.seat_col = seat_col;
    }
    public int getSeat_type_id() {
        return seat_type_id;
    }
    public void setSeat_type_id(int seat_type_id) {
        this.seat_type_id = seat_type_id;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
