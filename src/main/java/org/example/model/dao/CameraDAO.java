package org.example.model.dao;

import org.example.model.database.DatabaseConnection;
import org.example.model.entities.Camera;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CameraDAO {
    private Connection connection;

    public CameraDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Camera> findAll() {
        List<Camera> camere = new ArrayList<>();
        String sql = "SELECT c.*, h.nume as nume_hotel FROM camera c JOIN hotel h ON c.id_hotel = h.id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Camera camera = new Camera();
                camera.setId(rs.getInt("id"));
                camera.setIdHotel(rs.getInt("id_hotel"));
                camera.setNrCamera(rs.getString("nr_camera"));
                camera.setPretPerNoapte(rs.getFloat("pret_per_noapte"));
                camera.setIdPoze(rs.getInt("id_poze"));
                camere.add(camera);
            }
        } catch (SQLException e) {
            System.err.println("Eroare la citirea camerelor: " + e.getMessage());
        }

        return camere;
    }

    public Object[][] getCamereAsObjects(int hotelId) {
        List<Camera> camere = findByHotelId(hotelId);
        Object[][] result = new Object[camere.size()][4]; // id, nrCamera, pretPerNoapte, disponibil

        for (int i = 0; i < camere.size(); i++) {
            Camera camera = camere.get(i);
            result[i][0] = camera.getId();
            result[i][1] = camera.getNrCamera();
            result[i][2] = camera.getPretPerNoapte();

            // Check availability - default is true, will be updated by presenter
            result[i][3] = true;
        }

        return result;
    }

    public List<Camera> findByHotelId(int hotelId) {
        List<Camera> camere = new ArrayList<>();
        String sql = "SELECT c.*, h.nume as nume_hotel FROM camera c JOIN hotel h ON c.id_hotel = h.id WHERE c.id_hotel = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, hotelId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Camera camera = new Camera();
                    camera.setId(rs.getInt("id"));
                    camera.setIdHotel(rs.getInt("id_hotel"));
                    camera.setNrCamera(rs.getString("nr_camera"));
                    camera.setPretPerNoapte(rs.getFloat("pret_per_noapte"));
                    camera.setIdPoze(rs.getInt("id_poze"));
                    camere.add(camera);
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la căutarea camerelor după hotel: " + e.getMessage());
        }

        return camere;
    }

    public Camera findById(int id) {
        String sql = "SELECT c.* FROM camera c WHERE c.id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Camera camera = new Camera();
                    camera.setId(rs.getInt("id"));
                    camera.setIdHotel(rs.getInt("id_hotel"));
                    camera.setNrCamera(rs.getString("nr_camera"));
                    camera.setPretPerNoapte(rs.getFloat("pret_per_noapte"));
                    camera.setIdPoze(rs.getInt("id_poze"));
                    return camera;
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la găsirea camerei: " + e.getMessage());
        }

        return null;
    }

    public boolean save(Camera camera) {
        String sql = "INSERT INTO camera (id_hotel, nr_camera, pret_per_noapte, id_poze) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, camera.getIdHotel());
            pstmt.setString(2, camera.getNrCamera());
            pstmt.setFloat(3, camera.getPretPerNoapte());
            pstmt.setInt(4, camera.getIdPoze());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    camera.setId(generatedKeys.getInt(1));
                } else {
                    return false;
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Eroare la salvarea camerei: " + e.getMessage());
            return false;
        }
    }

    public boolean save(int idHotel, String nrCamera, float pretPerNoapte, int idPoze) {
        Camera camera = new Camera();
        camera.setIdHotel(idHotel);
        camera.setNrCamera(nrCamera);
        camera.setPretPerNoapte(pretPerNoapte);
        camera.setIdPoze(idPoze);
        return save(camera);
    }

    public boolean update(Camera camera) {
        String sql = "UPDATE camera SET id_hotel = ?, nr_camera = ?, pret_per_noapte = ?, id_poze = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, camera.getIdHotel());
            pstmt.setString(2, camera.getNrCamera());
            pstmt.setFloat(3, camera.getPretPerNoapte());
            pstmt.setInt(4, camera.getIdPoze());
            pstmt.setInt(5, camera.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la actualizarea camerei: " + e.getMessage());
            return false;
        }
    }

    public boolean update(int id, int idHotel, String nrCamera, float pretPerNoapte, int idPoze) {
        Camera camera = new Camera();
        camera.setId(id);
        camera.setIdHotel(idHotel);
        camera.setNrCamera(nrCamera);
        camera.setPretPerNoapte(pretPerNoapte);
        camera.setIdPoze(idPoze);
        return update(camera);
    }

    public boolean delete(int id) {
        // Delete associated reservations first
        String sqlDeleteRezervari = "DELETE FROM rezervari WHERE id_camera = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlDeleteRezervari)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Eroare la ștergerea rezervărilor camerei: " + e.getMessage());
            return false;
        }

        // Then delete the camera
        String sql = "DELETE FROM camera WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la ștergerea camerei: " + e.getMessage());
            return false;
        }
    }

    // Add filtering methods

    public List<Camera> filterByPriceRange(int hotelId, float minPrice, float maxPrice) {
        List<Camera> camere = new ArrayList<>();
        String sql = "SELECT c.* FROM camera c " +
                "WHERE c.id_hotel = ? AND c.pret_per_noapte BETWEEN ? AND ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, hotelId);
            pstmt.setFloat(2, minPrice);
            pstmt.setFloat(3, maxPrice);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Camera camera = new Camera();
                    camera.setId(rs.getInt("id"));
                    camera.setIdHotel(rs.getInt("id_hotel"));
                    camera.setNrCamera(rs.getString("nr_camera"));
                    camera.setPretPerNoapte(rs.getFloat("pret_per_noapte"));
                    camera.setIdPoze(rs.getInt("id_poze"));
                    camere.add(camera);
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la filtrarea camerelor după preț: " + e.getMessage());
        }

        return camere;
    }

    public Object[][] filterByPriceRangeAsObjects(int hotelId, float minPrice, float maxPrice) {
        List<Camera> camere = filterByPriceRange(hotelId, minPrice, maxPrice);
        Object[][] result = new Object[camere.size()][4];

        for (int i = 0; i < camere.size(); i++) {
            Camera camera = camere.get(i);
            result[i][0] = camera.getId();
            result[i][1] = camera.getNrCamera();
            result[i][2] = camera.getPretPerNoapte();
            result[i][3] = true; // Default is available, will be updated by presenter
        }

        return result;
    }

    // Filter rooms by availability on a specific date
    public List<Camera> filterByAvailability(int hotelId, LocalDateTime checkInDate, LocalDateTime checkOutDate) {
        List<Camera> camere = new ArrayList<>();

        // First get all rooms for the hotel that are not reserved during the specified period
        String sql = "SELECT c.* FROM camera c " +
                "WHERE c.id_hotel = ? AND c.id NOT IN (" +
                "SELECT r.id_camera FROM rezervari r " +
                "WHERE r.id_camera IN (SELECT id FROM camera WHERE id_hotel = ?) " +
                "AND NOT (r.end_date <= ? OR r.start_date >= ?))";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, hotelId);
            pstmt.setInt(2, hotelId);
            pstmt.setObject(3, checkInDate);
            pstmt.setObject(4, checkOutDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Camera camera = new Camera();
                    camera.setId(rs.getInt("id"));
                    camera.setIdHotel(rs.getInt("id_hotel"));
                    camera.setNrCamera(rs.getString("nr_camera"));
                    camera.setPretPerNoapte(rs.getFloat("pret_per_noapte"));
                    camera.setIdPoze(rs.getInt("id_poze"));
                    camere.add(camera);
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la filtrarea camerelor după disponibilitate: " + e.getMessage());
        }

        return camere;
    }

    public Object[][] filterByAvailabilityAsObjects(int hotelId, LocalDateTime checkInDate, LocalDateTime checkOutDate) {
        List<Camera> camere = filterByAvailability(hotelId, checkInDate, checkOutDate);
        Object[][] result = new Object[camere.size()][4];

        for (int i = 0; i < camere.size(); i++) {
            Camera camera = camere.get(i);
            result[i][0] = camera.getId();
            result[i][1] = camera.getNrCamera();
            result[i][2] = camera.getPretPerNoapte();
            result[i][3] = true; // All cameras in result are available
        }

        return result;
    }

    // Filter rooms by facilities (search in hotel facilities)
    public List<Camera> filterByFacilities(int hotelId, String facilitiesKeyword) {
        List<Camera> camere = new ArrayList<>();
        String sql = "SELECT c.* FROM camera c " +
                "JOIN hotel h ON c.id_hotel = h.id " +
                "WHERE c.id_hotel = ? AND h.facilitati LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, hotelId);
            pstmt.setString(2, "%" + facilitiesKeyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Camera camera = new Camera();
                    camera.setId(rs.getInt("id"));
                    camera.setIdHotel(rs.getInt("id_hotel"));
                    camera.setNrCamera(rs.getString("nr_camera"));
                    camera.setPretPerNoapte(rs.getFloat("pret_per_noapte"));
                    camera.setIdPoze(rs.getInt("id_poze"));
                    camere.add(camera);
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la filtrarea camerelor după facilități: " + e.getMessage());
        }

        return camere;
    }

    public Object[][] filterByFacilitiesAsObjects(int hotelId, String facilitiesKeyword) {
        List<Camera> camere = filterByFacilities(hotelId, facilitiesKeyword);
        Object[][] result = new Object[camere.size()][4];

        for (int i = 0; i < camere.size(); i++) {
            Camera camera = camere.get(i);
            result[i][0] = camera.getId();
            result[i][1] = camera.getNrCamera();
            result[i][2] = camera.getPretPerNoapte();
            result[i][3] = true; // Default is available, will be updated by presenter
        }

        return result;
    }

    public String getCameraInfoById(int id) {
        Camera camera = findById(id);
        if (camera != null) {
            return "Nr. " + camera.getNrCamera() + " - " + camera.getPretPerNoapte() + " RON/noapte";
        }
        return "";
    }
}