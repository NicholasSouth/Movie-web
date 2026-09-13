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
        String sql =
                "SELECT * " +
                "FROM Users " +
                "WHERE username = ? " +
                "AND deleted_at IS NULL";
        try (
                Connection connection =
                        DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql))
        {
            statement.setString(1, username);
            try (ResultSet result =
                    statement.executeQuery())
            {
                if (result.next())
                {
                    return mapUser(result);
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public Users getUserByEmail(String email)
    {
        String sql =
                "SELECT * " +
                "FROM Users " +
                "WHERE email = ? " +
                "AND deleted_at IS NULL";
        try (
                Connection connection =
                        DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql))
        {
            statement.setString(1, email);
            try (ResultSet result =
                    statement.executeQuery())
            {
                if (result.next())
                {
                    return mapUser(result);
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public Users getUserByPhone(String phone)
    {
        String sql =
                "SELECT * " +
                "FROM Users " +
                "WHERE phone = ? " +
                "AND deleted_at IS NULL";
        try (
                Connection connection =
                        DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql))
        {
            statement.setString(1, phone);
            try (ResultSet result =
                    statement.executeQuery())
            {
                if (result.next())
                {
                    return mapUser(result);
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertUser(Users user)
    {
        String sql =
                "INSERT INTO Users " +
                "(username, full_name, password, email, phone, " +
                "avt_path, banner_path, role, isActive, created_at, deleted_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), NULL)";
        try (
                Connection connection =
                        DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql))
        {
            statement.setString(
                    1,
                    user.getUsername());
            statement.setString(
                    2,
                    user.getFullName());
            statement.setString(
                    3,
                    user.getPassword());
            statement.setString(
                    4,
                    user.getEmail());
            /*Phone is optional. If it is null or empty, store SQL NULL.*/
            if (user.getPhone() == null ||
                user.getPhone().trim().isEmpty())
            {
                statement.setNull(
                        5,
                        java.sql.Types.VARCHAR);
            }
            else
            {
                statement.setString(
                        5,
                        user.getPhone());
            }
            statement.setString(
                    6,
                    user.getAvtPath());
            statement.setString(
                    7,
                    user.getBannerPath());
            statement.setString(
                    8,
                    user.getRole());
            statement.setBoolean(
                    9,
                    user.isActive());
            int rows =
                    statement.executeUpdate();
            return rows > 0;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(
            int userId,
            String newPassword)
    {
        String sql =
                "UPDATE Users " +
                "SET password = ? " +
                "WHERE user_id = ? " +
                "AND deleted_at IS NULL";
        try (
                Connection connection =
                        DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql))
        {
            statement.setString(
                    1,
                    newPassword);
            statement.setInt(
                    2,
                    userId);
            int rows =
                    statement.executeUpdate();
            return rows > 0;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean usernameExists(String username)
    {
        return getUserByUsername(username) != null;
    }

    public boolean emailExists(String email)
    {
        return getUserByEmail(email) != null;
    }

    public boolean phoneExists(String phone)
    {
        if (phone == null ||
            phone.trim().isEmpty())
        {
            return false;
        }
        return getUserByPhone(phone) != null;
    }

    private Users mapUser(ResultSet result)
            throws SQLException
    {
        Users user = new Users();
        user.setUserId(result.getInt("user_id"));
        user.setUsername(result.getString("username"));
        user.setFullName(result.getString("full_name"));
        user.setPassword(result.getString("password"));
        user.setEmail(result.getString("email"));
        user.setPhone(result.getString("phone"));
        user.setAvtPath(result.getString("avt_path"));
        user.setBannerPath(result.getString("banner_path"));
        user.setRole(result.getString("role"));
        user.setActive(result.getBoolean("isActive"));
        user.setCreatedAt(result.getTimestamp("created_at"));
        user.setDeletedAt(result.getTimestamp("deleted_at"));
        return user;
    }
}