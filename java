package com.portfolio;

import static spark.Spark.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PortfolioServer {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/personal_portfolio";

    private static final String DB_USER =
            System.getenv().getOrDefault("DB_USER", "root");

    private static final String DB_PASSWORD =
            System.getenv().getOrDefault("DB_PASSWORD", "");

    public static void main(String[] args) {

        port(8080);
        staticFiles.location("/static");

        get("/api/status", (request, response) -> {
            response.type("application/json");
            return "{\"message\"😕"Portfolio server is running.\"}";
        });

        post("/api/contact", (request, response) -> {

            response.type("application/json");

            String name = request.queryParams("name");
            String email = request.queryParams("email");
            String message = request.queryParams("message");

            if (name == null || email == null || message == null) {
                response.status(400);
                return "{\"message\"😕"Please complete all fields.\"}";
            }

            name = name.trim();
            email = email.trim();
            message = message.trim();

            if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
                response.status(400);
                return "{\"message\"😕"All fields are required.\"}";
            }

            if (name.length() > 100
                    || email.length() > 150
                    || message.length() > 5000) {
                response.status(400);
                return "{\"message\"😕"One or more fields are too long.\"}";
            }

            if (!email.matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                response.status(400);
                return "{\"message\"😕"Please enter a valid email.\"}";
            }

            String sql = """
                    INSERT INTO contact_messages (name, email, message)
                    VALUES (?, ?, ?)
                    """;

            try (Connection connection = DriverManager.getConnection(
                        DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement statement =
                        connection.prepareStatement(sql)) {

                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, message);

                statement.executeUpdate();

                response.status(201);
                return "{\"message\"😕"Your message was sent successfully!\"}";

            } catch (SQLException exception) {
                exception.printStackTrace();
                response.status(500);
                return "{\"message\"😕"Could not save your message. Please check the database connection.\"}";
            }
        });

        System.out.println(
                "Personal Portfolio is running at http://localhost:8080");
    }
}
