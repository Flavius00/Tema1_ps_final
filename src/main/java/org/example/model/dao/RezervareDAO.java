package org.example.model.dao;

import org.example.model.database.DatabaseConnection;
import org.example.model.entities.Rezervare;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RezervareDAO {
    private Connection connection;
    // Format de dată modificat - fără oră
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public RezervareDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Rezervare> findAll() {
        List<Rezervare> rezervari = new ArrayList<>();
        String sql = "SELECT r.*, c.nr_camera, h.nume as nume_hotel " +
                "FROM rezervari r " +
                "JOIN camera c ON r.id_camera = c.id " +
                "JOIN hotel h ON c.id_hotel = h.id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Rezervare rezervare = new Rezervare();
                rezervare.setId(rs.getInt("id"));
                rezervare.setStartDate(rs.getObject("start_date", LocalDateTime.class));
                rezervare.setEndDate(rs.getObject("end_date", LocalDateTime.class));
                rezervare.setIdCamera(rs.getInt("id_camera"));
                rezervare.setNumeClient(rs.getString("nume_client"));
                rezervare.setPrenumeClient(rs.getString("prenume_client"));
                rezervare.setTelefonClient(rs.getString("telefon_client"));
                rezervare.setEmailClient(rs.getString("email_client"));
                rezervari.add(rezervare);
            }
        } catch (SQLException e) {
            System.err.println("Eroare la citirea rezervărilor: " + e.getMessage());
        }

        return rezervari;
    }

    public Object[][] getRezervariAsObjects(int cameraId) {
        List<Rezervare> rezervari = findByCameraId(cameraId);
        Object[][] result = new Object[rezervari.size()][5];

        for (int i = 0; i < rezervari.size(); i++) {
            Rezervare rezervare = rezervari.get(i);
            result[i][0] = rezervare.getId();
            // Convertim în LocalDate pentru afișare
            result[i][1] = rezervare.getStartDate().toLocalDate().format(dateFormatter);
            result[i][2] = rezervare.getEndDate().toLocalDate().format(dateFormatter);
            result[i][3] = rezervare.getNumeClient() + " " + rezervare.getPrenumeClient();
            result[i][4] = rezervare.getTelefonClient();
        }

        return result;
    }

    public List<Rezervare> findByCameraId(int cameraId) {
        List<Rezervare> rezervari = new ArrayList<>();
        String sql = "SELECT r.* FROM rezervari r WHERE r.id_camera = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, cameraId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Rezervare rezervare = new Rezervare();
                    rezervare.setId(rs.getInt("id"));
                    rezervare.setStartDate(rs.getObject("start_date", LocalDateTime.class));
                    rezervare.setEndDate(rs.getObject("end_date", LocalDateTime.class));
                    rezervare.setIdCamera(rs.getInt("id_camera"));
                    rezervare.setNumeClient(rs.getString("nume_client"));
                    rezervare.setPrenumeClient(rs.getString("prenume_client"));
                    rezervare.setTelefonClient(rs.getString("telefon_client"));
                    rezervare.setEmailClient(rs.getString("email_client"));
                    rezervari.add(rezervare);
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la căutarea rezervărilor după cameră: " + e.getMessage());
        }

        return rezervari;
    }

    public Rezervare findById(int id) {
        String sql = "SELECT r.* FROM rezervari r WHERE r.id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Rezervare rezervare = new Rezervare();
                    rezervare.setId(rs.getInt("id"));
                    rezervare.setStartDate(rs.getObject("start_date", LocalDateTime.class));
                    rezervare.setEndDate(rs.getObject("end_date", LocalDateTime.class));
                    rezervare.setIdCamera(rs.getInt("id_camera"));
                    rezervare.setNumeClient(rs.getString("nume_client"));
                    rezervare.setPrenumeClient(rs.getString("prenume_client"));
                    rezervare.setTelefonClient(rs.getString("telefon_client"));
                    rezervare.setEmailClient(rs.getString("email_client"));
                    return rezervare;
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la găsirea rezervării: " + e.getMessage());
        }

        return null;
    }

    public boolean save(Rezervare rezervare) {
        String sql = "INSERT INTO rezervari (start_date, end_date, id_camera, nume_client, prenume_client, telefon_client, email_client) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setObject(1, rezervare.getStartDate());
            pstmt.setObject(2, rezervare.getEndDate());
            pstmt.setInt(3, rezervare.getIdCamera());
            pstmt.setString(4, rezervare.getNumeClient());
            pstmt.setString(5, rezervare.getPrenumeClient());
            pstmt.setString(6, rezervare.getTelefonClient());
            pstmt.setString(7, rezervare.getEmailClient());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    rezervare.setId(generatedKeys.getInt(1));
                } else {
                    return false;
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Eroare la salvarea rezervării: " + e.getMessage());
            return false;
        }
    }

    public boolean save(String startDate, String endDate, int idCamera,
                        String numeClient, String prenumeClient, String telefonClient, String emailClient) {
        Rezervare rezervare = new Rezervare();
        try {
            // Convertim din LocalDate în LocalDateTime pentru stocarea în baza de date
            LocalDate startLocalDate = LocalDate.parse(startDate, dateFormatter);
            LocalDate endLocalDate = LocalDate.parse(endDate, dateFormatter);

            // Transformăm în LocalDateTime (începutul zilei)
            LocalDateTime startDateTime = startLocalDate.atStartOfDay();
            LocalDateTime endDateTime = endLocalDate.atStartOfDay();

            rezervare.setStartDate(startDateTime);
            rezervare.setEndDate(endDateTime);
            rezervare.setIdCamera(idCamera);
            rezervare.setNumeClient(numeClient);
            rezervare.setPrenumeClient(prenumeClient);
            rezervare.setTelefonClient(telefonClient);
            rezervare.setEmailClient(emailClient);
            return save(rezervare);
        } catch (Exception e) {
            System.err.println("Eroare la parsarea datelor: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Rezervare rezervare) {
        String sql = "UPDATE rezervari SET start_date = ?, end_date = ?, id_camera = ?, " +
                "nume_client = ?, prenume_client = ?, telefon_client = ?, email_client = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, rezervare.getStartDate());
            pstmt.setObject(2, rezervare.getEndDate());
            pstmt.setInt(3, rezervare.getIdCamera());
            pstmt.setString(4, rezervare.getNumeClient());
            pstmt.setString(5, rezervare.getPrenumeClient());
            pstmt.setString(6, rezervare.getTelefonClient());
            pstmt.setString(7, rezervare.getEmailClient());
            pstmt.setInt(8, rezervare.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la actualizarea rezervării: " + e.getMessage());
            return false;
        }
    }

    public boolean update(int id, String startDate, String endDate, int idCamera,
                          String numeClient, String prenumeClient, String telefonClient, String emailClient) {
        Rezervare rezervare = new Rezervare();
        try {
            // Convertim din LocalDate în LocalDateTime pentru stocarea în baza de date
            LocalDate startLocalDate = LocalDate.parse(startDate, dateFormatter);
            LocalDate endLocalDate = LocalDate.parse(endDate, dateFormatter);

            // Transformăm în LocalDateTime (începutul zilei)
            LocalDateTime startDateTime = startLocalDate.atStartOfDay();
            LocalDateTime endDateTime = endLocalDate.atStartOfDay();

            rezervare.setId(id);
            rezervare.setStartDate(startDateTime);
            rezervare.setEndDate(endDateTime);
            rezervare.setIdCamera(idCamera);
            rezervare.setNumeClient(numeClient);
            rezervare.setPrenumeClient(prenumeClient);
            rezervare.setTelefonClient(telefonClient);
            rezervare.setEmailClient(emailClient);
            return update(rezervare);
        } catch (Exception e) {
            System.err.println("Eroare la parsarea datelor: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM rezervari WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la ștergerea rezervării: " + e.getMessage());
            return false;
        }
    }

    // Method to check room availability on a specific date
    public boolean isCameraDisponibila(int cameraId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT COUNT(*) FROM rezervari " +
                "WHERE id_camera = ? AND NOT (end_date <= ? OR start_date >= ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, cameraId);
            pstmt.setObject(2, startDate);
            pstmt.setObject(3, endDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0; // If count = 0, room is available
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la verificarea disponibilității camerei: " + e.getMessage());
        }

        return false; // In case of error, assume it's not available
    }

    // Find reservations for a hotel on a specific date
    public List<Rezervare> findReservationsByHotelAndDate(int hotelId, LocalDateTime date) {
        List<Rezervare> rezervari = new ArrayList<>();
        String sql = "SELECT r.* FROM rezervari r " +
                "JOIN camera c ON r.id_camera = c.id " +
                "WHERE c.id_hotel = ? AND " +
                "(? BETWEEN r.start_date AND r.end_date)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, hotelId);
            pstmt.setObject(2, date);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Rezervare rezervare = new Rezervare();
                    rezervare.setId(rs.getInt("id"));
                    rezervare.setStartDate(rs.getObject("start_date", LocalDateTime.class));
                    rezervare.setEndDate(rs.getObject("end_date", LocalDateTime.class));
                    rezervare.setIdCamera(rs.getInt("id_camera"));
                    rezervare.setNumeClient(rs.getString("nume_client"));
                    rezervare.setPrenumeClient(rs.getString("prenume_client"));
                    rezervare.setTelefonClient(rs.getString("telefon_client"));
                    rezervare.setEmailClient(rs.getString("email_client"));
                    rezervari.add(rezervare);
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la găsirea rezervărilor pentru hotel și dată: " + e.getMessage());
        }

        return rezervari;
    }

    public Object[][] findReservationsByHotelAndDateAsObjects(int hotelId, LocalDateTime date) {
        List<Rezervare> rezervari = findReservationsByHotelAndDate(hotelId, date);
        Object[][] result = new Object[rezervari.size()][5];

        for (int i = 0; i < rezervari.size(); i++) {
            Rezervare rezervare = rezervari.get(i);
            result[i][0] = rezervare.getId();
            // Modificat să afișeze doar data fără oră
            result[i][1] = rezervare.getStartDate().toLocalDate().format(dateFormatter);
            result[i][2] = rezervare.getEndDate().toLocalDate().format(dateFormatter);
            result[i][3] = rezervare.getNumeClient() + " " + rezervare.getPrenumeClient();
            result[i][4] = rezervare.getTelefonClient();
        }

        return result;
    }
}