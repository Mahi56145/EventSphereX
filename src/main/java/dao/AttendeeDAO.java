package dao;

import model.Attendee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendeeDAO {

    public List<Attendee> getAttendeesByEvent(int eventId) {
        List<Attendee> list = new ArrayList<>();

        String sql =
                "SELECT a.attendee_id, a.name, a.email, a.phone " +
                        "FROM event_attendees ea " +
                        "JOIN attendees a ON ea.attendee_id = a.attendee_id " +
                        "WHERE ea.event_id = ? " +
                        "ORDER BY a.attendee_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Attendee a = new Attendee();
                a.setId(rs.getInt("attendee_id"));
                a.setEventId(eventId);
                a.setName(rs.getString("name"));
                a.setEmail(rs.getString("email"));
                a.setPhone(rs.getString("phone"));
                list.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean addAttendee(Attendee attendee) {
        String findSql =
                "SELECT attendee_id FROM attendees " +
                        "WHERE name = ? AND (email = ? OR email IS NULL) AND (phone = ? OR phone IS NULL) " +
                        "LIMIT 1";

        String insertAttendeeSql =
                "INSERT INTO attendees (name, email, phone) VALUES (?, ?, ?)";

        String linkSql =
                "INSERT INTO event_attendees (event_id, attendee_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int attendeeId = -1;

            try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                ps.setString(1, attendee.getName());
                ps.setString(2, attendee.getEmail());
                ps.setString(3, attendee.getPhone());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    attendeeId = rs.getInt("attendee_id");
                }
            }

            if (attendeeId == -1) {
                try (PreparedStatement ps = conn.prepareStatement(
                        insertAttendeeSql, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, attendee.getName());
                    ps.setString(2, attendee.getEmail());
                    ps.setString(3, attendee.getPhone());

                    int rows = ps.executeUpdate();
                    if (rows == 0) {
                        conn.rollback();
                        return false;
                    }

                    ResultSet keys = ps.getGeneratedKeys();
                    if (keys.next()) {
                        attendeeId = keys.getInt(1);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(linkSql)) {
                ps.setInt(1, attendee.getEventId());
                ps.setInt(2, attendeeId);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean deleteAttendee(int eventId, int attendeeId) {
        String deleteLinkSql =
                "DELETE FROM event_attendees WHERE event_id = ? AND attendee_id = ?";

        String cleanupSql =
                "DELETE FROM attendees " +
                        "WHERE attendee_id = ? AND NOT EXISTS (" +
                        "   SELECT 1 FROM event_attendees WHERE attendee_id = ?" +
                        ")";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(deleteLinkSql)) {
                ps.setInt(1, eventId);
                ps.setInt(2, attendeeId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(cleanupSql)) {
                ps.setInt(1, attendeeId);
                ps.setInt(2, attendeeId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int countAttendeesForUser(int userId) {
        String sql =
                "SELECT COUNT(*) AS c " +
                        "FROM event_attendees ea " +
                        "JOIN events e ON ea.event_id = e.event_id " +
                        "WHERE e.created_by = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("c");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
