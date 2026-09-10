package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.movieweb.model.Movie_directors;
import com.movieweb.util.DBConnection;

public class Movie_directorsDAO 
{
    public Movie_directors getMovieDirector(int movie_id, int director_id) 
    {
        String sql = "SELECT * FROM Movie_directors WHERE movie_id = ? AND director_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, movie_id);
            stmt.setInt(2, director_id);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) 
            {
                Movie_directors movie_director = new Movie_directors();
                movie_director.setMovie_id(rs.getInt("movie_id"));
                movie_director.setDirector_id(rs.getInt("director_id"));
                return movie_director;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
}