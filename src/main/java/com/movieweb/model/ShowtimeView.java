package com.movieweb.model;

import java.sql.Timestamp;

public final class ShowtimeView {
    private final int showtimeId;
    private final String theaterName, theaterAddress, roomName;
    private final Timestamp startAt, endAt;

    public ShowtimeView(int showtimeId, String theaterName, String theaterAddress, String roomName,
            Timestamp startAt, Timestamp endAt) {
        this.showtimeId = showtimeId;
        this.theaterName = theaterName;
        this.theaterAddress = theaterAddress;
        this.roomName = roomName;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public int getShowtimeId() { return showtimeId; }
    public String getTheaterName() { return theaterName; }
    public String getTheaterAddress() { return theaterAddress; }
    public String getRoomName() { return roomName; }
    public Timestamp getStartAt() { return startAt; }
    public Timestamp getEndAt() { return endAt; }
}
