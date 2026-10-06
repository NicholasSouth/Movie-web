package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.movieweb.model.Room_types;
import com.movieweb.util.DBConnection;

public class Room_typesDAO 
{
    public Room_types getRoomTypeById(int room_type_id) 
    {
        String sql = "SELECT * FROM Room_types WHERE room_type_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, room_type_id);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) 
            {
                Room_types room_type = new Room_types();
                room_type.setRoom_type_id(rs.getInt("room_type_id"));
                room_type.setRoom_type_name(rs.getString("room_type_name"));
                room_type.setPrice_modify(rs.getString("price_modify"));
                
                return room_type;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }

    public List<Room_types> getAllRoomTypes() {
        List<Room_types> roomTypes = new ArrayList<>();
        String sql = "SELECT room_type_id, room_type_name, price_modify FROM Room_types ORDER BY room_type_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Room_types roomType = new Room_types();
                roomType.setRoom_type_id(rs.getInt("room_type_id"));
                roomType.setRoom_type_name(rs.getString("room_type_name"));
                roomType.setPrice_modify(rs.getString("price_modify"));
                roomTypes.add(roomType);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return roomTypes;
    }

    public boolean roomTypeExists(int roomTypeId) {
        String sql = "SELECT 1 FROM Room_types WHERE room_type_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomTypeId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}