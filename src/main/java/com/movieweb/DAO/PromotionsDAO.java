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
                return mapPromotion(rs);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }       
        return null;
    }

    public Promotions getPromotionByCode(String promotion_code) 
    {
        String sql =
                "SELECT * FROM Promotions " +
                "WHERE promotion_code = ?";       
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setString(1, promotion_code);          
            try (ResultSet rs = stmt.executeQuery()) 
            {
                if (rs.next()) 
                {
                    return mapPromotion(rs);
                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }      
        return null;
    }

    public int getPromotionUsageCount(int promotion_id) 
    {
        String sql =
                "SELECT COUNT(*) " +
                "FROM Booking_showtimes " +
                "WHERE promotion_id = ? " +
                "AND delete_at IS NULL " +
                "AND status <> 'CANCELLED'";       
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, promotion_id);
            
            try (ResultSet rs = stmt.executeQuery()) 
            {
                if (rs.next()) 
                {
                    return rs.getInt(1);
                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return -1;
    }

    private Promotions mapPromotion(ResultSet rs) throws SQLException
    {
        Promotions promotion = new Promotions();
        promotion.setPromotion_id(rs.getInt("promotion_id"));
        promotion.setPromotion_code(rs.getString("promotion_code"));
        promotion.setDescription(rs.getString("description"));
        promotion.setPrice_modify(rs.getString("price_modify"));
        promotion.setStart_at(rs.getTimestamp("start_at"));
        promotion.setEnd_at(rs.getTimestamp("end_at"));
        promotion.setUsage_limit(rs.getInt("usage_limit"));
        promotion.setActive(rs.getBoolean("isActive"));

        return promotion;
    }

}