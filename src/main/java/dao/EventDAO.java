package dao;

import model.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    public List<Event> getEventsByUser(int userId) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT event_id, title, event_date, location, description " +
                "FROM events WHERE created_by = ? ORDER BY event_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Event e = new Event();
                e.setId(rs.getInt("event_id"));
                e.setTitle(rs.getString("title"));
                e.setEventDate(rs.getTimestamp("event_date"));
                e.setLocation(rs.getString("location"));
                e.setDescription(rs.getString("description"));
                events.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return events;
    }

    public boolean addEvent(Event event, int userId) {
        String sql = "INSERT INTO events (title, event_date, location, description, created_by) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getTitle());
            ps.setTimestamp(2, event.getEventDate());
            ps.setString(3, event.getLocation());
            ps.setString(4, event.getDescription());
            ps.setInt(5, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Event getEventById(int eventId, int userId) {
        String sql = "SELECT event_id, title, event_date, location, description " +
                "FROM events WHERE event_id = ? AND created_by = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Event e = new Event();
                e.setId(rs.getInt("event_id"));
                e.setTitle(rs.getString("title"));
                e.setEventDate(rs.getTimestamp("event_date"));
                e.setLocation(rs.getString("location"));
                e.setDescription(rs.getString("description"));
                return e;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean updateEvent(Event event, int userId) {
        String sql = "UPDATE events SET title = ?, event_date = ?, location = ?, description = ? " +
                "WHERE event_id = ? AND created_by = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getTitle());
            ps.setTimestamp(2, event.getEventDate());
            ps.setString(3, event.getLocation());
            ps.setString(4, event.getDescription());
            ps.setInt(5, event.getId());
            ps.setInt(6, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
