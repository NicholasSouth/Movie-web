package com.movieweb.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.movieweb.model.Movie_actors;
import com.movieweb.util.DBConnection;

public class Movie_actorsDAO 
{
    public Movie_actors getMovieActor(int movie_id, int actor_id) 
    {
        String sql = "SELECT * FROM Movie_actors WHERE movie_id = ? AND actor_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, movie_id);
            stmt.setInt(2, actor_id);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) 
            {
                Movie_actors movie_actor = new Movie_actors();
                movie_actor.setMovie_id(rs.getInt("movie_id"));
                movie_actor.setActor_id(rs.getInt("actor_id"));
                return movie_actor;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
}