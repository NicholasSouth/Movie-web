package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.movieweb.model.Rooms;
import com.movieweb.util.DBConnection;

public class RoomsDAO 
{
    public Rooms getRoomById(int room_id) 
    {
        String sql = "SELECT * FROM Rooms WHERE room_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, room_id);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) 
            {
                mapRoom(rs);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }

    public List<Rooms> getRoomsByTheaterId(int theaterId) {
        List<Rooms> rooms = new ArrayList<>();
        String sql = "SELECT * FROM Rooms WHERE theater_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, theaterId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapRoom(rs));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    private Rooms mapRoom(ResultSet rs) throws SQLException {
        Rooms room = new Rooms();
        room.setRoom_id(rs.getInt("room_id"));
        room.setTheater_id(rs.getInt("theater_id"));
        room.setRoom_name(rs.getString("room_name"));
        room.setRoom_type_id(rs.getInt("room_type_id"));
        room.setActive(rs.getBoolean("isActive"));

        return room;
    }
}