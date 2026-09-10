package com.movieweb.DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.movieweb.model.Tags;
import com.movieweb.util.DBConnection;

public class TagsDAO 
{
    public Tags getTagById(int tag_id) 
    {
        String sql = "SELECT * FROM Tags WHERE tag_id = ?";      
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, tag_id);
            ResultSet rs = stmt.executeQuery();        
            if (rs.next()) 
            {
                Tags tag = new Tags();
                tag.setTag_id(rs.getInt("tag_id"));
                tag.setTag_name(rs.getString("tag_name"));
                return tag;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }       
        return null;
    }
}