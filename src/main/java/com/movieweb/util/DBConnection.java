package com.movieweb.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** SQL Server configuration stays outside source control and the deployed WAR. */
public final class DBConnection {
    private DBConnection() { }

    public static Connection getConnection() throws SQLException {
        Properties config = new Properties();
        String configPath = setting("movieweb.config", "MOVIEWEB_CONFIG", "");
        if (!configPath.isEmpty()) {
            try (Reader reader = Files.newBufferedReader(Path.of(configPath), StandardCharsets.UTF_8)) {
                config.load(reader);
            } catch (IOException | RuntimeException e) {
                throw new SQLException("Cannot read database configuration. Check MOVIEWEB_CONFIG.", e);
            }
        }
        String url = setting("movieweb.db.url", "MOVIEWEB_DB_URL", config.getProperty("db.url", ""));
        if (url.isBlank()) {
            throw new SQLException("Database is not configured. Set MOVIEWEB_CONFIG or MOVIEWEB_DB_URL.");
        }
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server JDBC driver is missing from WEB-INF/lib.", e);
        }
        Properties credentials = new Properties();
        String user = setting("movieweb.db.user", "MOVIEWEB_DB_USER", config.getProperty("db.user", ""));
        String password = setting("movieweb.db.password", "MOVIEWEB_DB_PASSWORD", config.getProperty("db.password", ""));
        if (!user.isBlank()) {
            credentials.setProperty("user", user);
            credentials.setProperty("password", password);
        }
        credentials.setProperty("loginTimeout", "5");
        credentials.setProperty("socketTimeout", "15000");
        return DriverManager.getConnection(url, credentials);
    }

    private static String setting(String property, String environment, String fallback) {
        String value = System.getProperty(property);
        if (value == null) value = System.getenv(environment);
        return value == null ? fallback : value;
    }
}
