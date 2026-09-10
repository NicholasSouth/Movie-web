package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.movieweb.model.Promotions;
import com.movieweb.util.DBConnection;

public class PromotionsDAO 
{
    public Promotions getPromotionById(int promotion_id) 
    {
        String sql = "SELECT * FROM Promotions WHERE promotion_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, promotion_id);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) 
            {
                Promotions promotion = new Promotions();
                promotion.setPromotion_id(rs.getInt("promotion_id"));
                promotion.setPromotion_code(rs.getString("promotion_code"));
                promotion.setDescription(rs.getString("description"));
                promotion.setPrice_modify(rs.getString("price_modify"));
                promotion.setStart_at(rs.getString("start_at"));
                promotion.setEnd_at(rs.getString("end_at"));
                promotion.setUsage_limit(rs.getInt("usage_limit"));
                promotion.setActive(rs.getBoolean("isActive"));
                
                return promotion;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
}