package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.movieweb.model.Promotion_times;
import com.movieweb.util.DBConnection;

public class Promotion_timesDAO 
{
    public Promotion_times getPromotionTime(int promotion_id, String start_time) 
    {
        String sql = "SELECT * FROM Promotion_times WHERE promotion_id = ? AND start_time = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, promotion_id);
            stmt.setString(2, start_time);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) 
            {
                Promotion_times promotion_time = new Promotion_times();
                promotion_time.setPromotion_id(rs.getInt("promotion_id"));
                promotion_time.setStart_time(rs.getString("start_time"));
                
                return promotion_time;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
}