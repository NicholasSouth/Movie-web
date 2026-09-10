package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.movieweb.model.Payments;
import com.movieweb.util.DBConnection;

public class PaymentsDAO 
{
    public Payments getPaymentById(int payment_id) 
    {
        String sql = "SELECT * FROM Payments WHERE payment_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, payment_id);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) 
            {
                Payments payment = new Payments();
                payment.setPayment_id(rs.getInt("payment_id"));
                payment.setPayment_method_id(rs.getInt("payment_method_id"));
                payment.setBooking_id(rs.getInt("booking_id"));
                payment.setMethod(rs.getString("method"));
                payment.setAmount(rs.getInt("amount"));
                payment.setPaid_at(rs.getString("paid_at"));
                payment.setStatus(rs.getString("status"));
                payment.setTransaction_code(rs.getString("transaction_code"));
                
                return payment;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
}