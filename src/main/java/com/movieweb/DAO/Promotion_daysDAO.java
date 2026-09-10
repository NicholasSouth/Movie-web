package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.movieweb.model.Promotion_days;
import com.movieweb.util.DBConnection;

public class Promotion_daysDAO 
{
    public Promotion_days getPromotionDay(int promotion_id, String day_of_week) 
    {
        String sql = "SELECT * FROM Promotion_days WHERE promotion_id = ? AND day_of_week = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, promotion_id);
            stmt.setString(2, day_of_week);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) 
            {
                Promotion_days promotion_day = new Promotion_days();
                promotion_day.setPromotion_id(rs.getInt("promotion_id"));
                promotion_day.setDay_of_week(rs.getString("day_of_week"));
                
                return promotion_day;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
}