package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.movieweb.model.Movie_authors;
import com.movieweb.util.DBConnection;

public class Movie_authorsDAO 
{
    public Movie_authors getMovieAuthor(int movie_id, int author_id) 
    {
        String sql = "SELECT * FROM Movie_authors WHERE movie_id = ? AND author_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, movie_id);
            stmt.setInt(2, author_id);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) 
            {
                Movie_authors movie_author = new Movie_authors();
                movie_author.setMovie_id(rs.getInt("movie_id"));
                movie_author.setAuthor_id(rs.getInt("author_id"));
                return movie_author;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
}