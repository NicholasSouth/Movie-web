package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.movieweb.model.Rooms;
import com.movieweb.model.Room_types;
import com.movieweb.util.DBConnection;

public class RoomsDAO {
	public Rooms getRoomById(int room_id) {
	    String sql =
	        "SELECT r.*, " +
	        "       rt.room_type_name, " +
	        "       rt.price_modify " +
	        "FROM Rooms r " +
	        "INNER JOIN Room_types rt " +
	        "    ON r.room_type_id = rt.room_type_id " +
	        "WHERE r.room_id = ? AND r.deleted_at IS NULL";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, room_id);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return mapRoom(rs);
	            }
	        }
	    }
	    catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

    public List<Rooms> getRoomsByTheaterId(int theaterId) {
        List<Rooms> rooms = new ArrayList<>();
        String sql =
            "SELECT r.*, " +
            "       rt.room_type_name, " +
            "       rt.price_modify " +
            "FROM Rooms r " +
            "INNER JOIN Room_types rt " +
            "    ON r.room_type_id = rt.room_type_id " +
            "WHERE r.theater_id = ? AND r.isActive = 1 AND r.deleted_at IS NULL";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
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

    public List<Rooms> getAllRoomsByTheaterId(int theaterId) {
        List<Rooms> rooms = new ArrayList<>();
        String sql =
                "SELECT r.*, " +
                "       rt.room_type_name, " +
                "       rt.price_modify " +
                "FROM Rooms r " +
                "INNER JOIN Room_types rt " +
                "    ON r.room_type_id = rt.room_type_id " +
                "WHERE r.theater_id = ? AND r.deleted_at IS NULL " +
                "ORDER BY r.room_id";
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
    
    public boolean roomNameExists(int theaterId, String roomName) {
        String sql = "SELECT 1 FROM Rooms WHERE theater_id = ? AND room_name = ? AND deleted_at IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, theaterId);
            stmt.setString(2, roomName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertRoom(int theaterId, String roomName, int roomTypeId) {
        String sql = "INSERT INTO Rooms (theater_id, room_name, room_type_id, isActive) VALUES (?, ?, ?, 1)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, theaterId);
            stmt.setString(2, roomName);
            stmt.setInt(3, roomTypeId);
            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean roomNameExistsExcept(int theaterId, String roomName, int excludeRoomId) {
        String sql = "SELECT 1 FROM Rooms WHERE theater_id = ? AND room_name = ? AND room_id <> ? " +
                     "AND deleted_at IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, theaterId);
            stmt.setString(2, roomName);
            stmt.setInt(3, excludeRoomId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateRoom(int roomId, String roomName, int roomTypeId) {
        String sql = "UPDATE Rooms SET room_name = ?, room_type_id = ? WHERE room_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomName);
            stmt.setInt(2, roomTypeId);
            stmt.setInt(3, roomId);
            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateRoomActive(int roomId, boolean active) {
        String sql = "UPDATE Rooms SET isActive = ? WHERE room_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, active);
            stmt.setInt(2, roomId);
            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteRoom(int roomId) {
        String sql = "UPDATE Rooms SET deleted_at = GETDATE() WHERE room_id = ? AND deleted_at IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Rooms mapRoom(ResultSet rs) throws SQLException {
    	Rooms room = new Rooms();
        room.setRoom_id(rs.getInt("room_id"));
        room.setTheater_id(rs.getInt("theater_id"));
        room.setRoom_name(rs.getString("room_name"));
        room.setRoom_type_id(rs.getInt("room_type_id"));
        room.setActive(rs.getBoolean("isActive"));
        Room_types roomType = new Room_types();
        roomType.setRoom_type_id(rs.getInt("room_type_id"));
        roomType.setRoom_type_name(rs.getString("room_type_name"));
        roomType.setPrice_modify(rs.getString("price_modify"));
        room.setRoomType(roomType);
        room.setDeleted_at(rs.getTimestamp("deleted_at"));
        return room;
    }
}