package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.movieweb.model.Payment_methods;
import com.movieweb.util.DBConnection;

public class Payment_methodsDAO 
{
    public Payment_methods getPaymentMethodById(int payment_method_id) 
    {
        String sql = "SELECT * FROM Payment_methods WHERE payment_method_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, payment_method_id);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) 
            {
                Payment_methods payment_method = new Payment_methods();
                payment_method.setPayment_method_id(rs.getInt("payment_method_id"));
                payment_method.setUser_id(rs.getInt("user_id"));
                payment_method.setMethod(rs.getString("method"));
                payment_method.setCard_number(rs.getString("card_number"));
                payment_method.setExpired_date(rs.getString("expired_date"));
                payment_method.setCreated_at(rs.getString("created_at"));
                payment_method.setActive(rs.getBoolean("isActive"));
                
                return payment_method;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
}