package com.movieweb.DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.movieweb.model.Users;
import com.movieweb.util.DBConnection;

public class UsersDAO 
{
    public Users getUserByUsername(String username) 
    {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) 
            {
                Users user = new Users();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setAvtPath(rs.getString("avt_path"));
                user.setBannerPath(rs.getString("banner_path"));
                user.setRole(rs.getString("role"));
                user.setActive(rs.getBoolean("isActive"));
                return user;
            }

        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return null;
    }
}