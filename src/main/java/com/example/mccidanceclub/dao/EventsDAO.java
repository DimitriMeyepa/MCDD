package com.example.mccidanceclub.dao;

import com.example.mccidanceclub.entities.Events;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventsDAO {

    private Connection connection;

    public EventsDAO() {
        connection = DbConnection.getConnection();
    }

    // Ajouter un événement
    public boolean addEvent(Events event) {
        String sql = "INSERT INTO events(event_name, event_date) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, event.getEventName());
            stmt.setDate(2, Date.valueOf(event.getEventDate()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Récupérer tous les événements
    public List<Events> getAllEvents() {
        List<Events> eventsList = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY event_date";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Events event = new Events(
                        rs.getInt("event_id"),
                        rs.getString("event_name"),
                        rs.getDate("event_date").toLocalDate()
                );
                eventsList.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventsList;
    }

    // Mettre à jour un événement
    public boolean updateEvent(Events event) {
        String sql = "UPDATE events SET event_name = ?, event_date = ? WHERE event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, event.getEventName());
            stmt.setDate(2, Date.valueOf(event.getEventDate()));
            stmt.setInt(3, event.getEventId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer un événement
    public boolean deleteEvent(int eventId) {
        String sql = "DELETE FROM events WHERE event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
