package org.example.model.dao;

import org.example.model.database.DatabaseConnection;
import org.example.model.entities.Hotel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelDAO {
    private Connection connection;
    private LocatieDAO locatieDAO;
    private LantDAO lantDAO;

    public HotelDAO() {
        this.connection = DatabaseConnection.getConnection();
        this.locatieDAO = new LocatieDAO();
        this.lantDAO = new LantDAO();
    }

    public List<Hotel> findAll() {
        List<Hotel> hoteluri = new ArrayList<>();
        String sql = "SELECT h.*, l.nume as nume_lant, loc.oras, loc.tara " +
                "FROM hotel h " +
                "JOIN lant l ON h.id_lant = l.id " +
                "JOIN locatie loc ON h.id_locatie = loc.id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Hotel hotel = new Hotel();
                hotel.setId(rs.getInt("id"));
                hotel.setNume(rs.getString("nume"));
                hotel.setIdLocatie(rs.getInt("id_locatie"));
                hotel.setTelefon(rs.getString("telefon"));
                hotel.setEmail(rs.getString("email"));
                hotel.setFacilitati(rs.getString("facilitati"));
                hotel.setIdLant(rs.getInt("id_lant"));
                hoteluri.add(hotel);
            }
        } catch (SQLException e) {
            System.err.println("Eroare la citirea hotelurilor: " + e.getMessage());
        }

        return hoteluri;
    }

    public Object[][] getHoteluriAsObjects() {
        List<Hotel> hoteluri = findAll();
        Object[][] result = new Object[hoteluri.size()][7];

        for (int i = 0; i < hoteluri.size(); i++) {
            Hotel hotel = hoteluri.get(i);
            result[i][0] = hotel.getId();
            result[i][1] = hotel.getNume();
            result[i][2] = locatieDAO.getDisplayString(hotel.getIdLocatie());
            result[i][3] = hotel.getTelefon();
            result[i][4] = hotel.getEmail();
            result[i][5] = hotel.getFacilitati();

            // Get lant name
            try {
                result[i][6] = lantDAO.findById(hotel.getIdLant()).getNume();
            } catch (Exception e) {
                result[i][6] = "";
            }
        }

        return result;
    }

    public List<Hotel> findByLantId(int lantId) {
        List<Hotel> hoteluri = new ArrayList<>();
        String sql = "SELECT h.*, l.nume as nume_lant, loc.oras, loc.tara " +
                "FROM hotel h " +
                "JOIN lant l ON h.id_lant = l.id " +
                "JOIN locatie loc ON h.id_locatie = loc.id " +
                "WHERE h.id_lant = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, lantId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Hotel hotel = new Hotel();
                    hotel.setId(rs.getInt("id"));
                    hotel.setNume(rs.getString("nume"));
                    hotel.setIdLocatie(rs.getInt("id_locatie"));
                    hotel.setTelefon(rs.getString("telefon"));
                    hotel.setEmail(rs.getString("email"));
                    hotel.setFacilitati(rs.getString("facilitati"));
                    hotel.setIdLant(rs.getInt("id_lant"));
                    hoteluri.add(hotel);
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la căutarea hotelurilor după lanț: " + e.getMessage());
        }

        return hoteluri;
    }

    public Hotel findById(int id) {
        String sql = "SELECT h.* FROM hotel h WHERE h.id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Hotel hotel = new Hotel();
                    hotel.setId(rs.getInt("id"));
                    hotel.setNume(rs.getString("nume"));
                    hotel.setIdLocatie(rs.getInt("id_locatie"));
                    hotel.setTelefon(rs.getString("telefon"));
                    hotel.setEmail(rs.getString("email"));
                    hotel.setFacilitati(rs.getString("facilitati"));
                    hotel.setIdLant(rs.getInt("id_lant"));
                    return hotel;
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la găsirea hotelului: " + e.getMessage());
        }

        return null;
    }

    public boolean save(Hotel hotel) {
        String sql = "INSERT INTO hotel (nume, id_locatie, telefon, email, facilitati, id_lant) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, hotel.getNume());
            pstmt.setInt(2, hotel.getIdLocatie());
            pstmt.setString(3, hotel.getTelefon());
            pstmt.setString(4, hotel.getEmail());
            pstmt.setString(5, hotel.getFacilitati());
            pstmt.setInt(6, hotel.getIdLant());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    hotel.setId(generatedKeys.getInt(1));
                } else {
                    return false;
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Eroare la salvarea hotelului: " + e.getMessage());
            return false;
        }
    }

    public boolean save(String nume, int idLocatie, String telefon, String email, String facilitati, int idLant) {
        Hotel hotel = new Hotel();
        hotel.setNume(nume);
        hotel.setIdLocatie(idLocatie);
        hotel.setTelefon(telefon);
        hotel.setEmail(email);
        hotel.setFacilitati(facilitati);
        hotel.setIdLant(idLant);
        return save(hotel);
    }

    public boolean update(Hotel hotel) {
        String sql = "UPDATE hotel SET nume = ?, id_locatie = ?, telefon = ?, " +
                "email = ?, facilitati = ?, id_lant = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, hotel.getNume());
            pstmt.setInt(2, hotel.getIdLocatie());
            pstmt.setString(3, hotel.getTelefon());
            pstmt.setString(4, hotel.getEmail());
            pstmt.setString(5, hotel.getFacilitati());
            pstmt.setInt(6, hotel.getIdLant());
            pstmt.setInt(7, hotel.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la actualizarea hotelului: " + e.getMessage());
            return false;
        }
    }

    public boolean update(int id, String nume, int idLocatie, String telefon, String email, String facilitati, int idLant) {
        Hotel hotel = new Hotel();
        hotel.setId(id);
        hotel.setNume(nume);
        hotel.setIdLocatie(idLocatie);
        hotel.setTelefon(telefon);
        hotel.setEmail(email);
        hotel.setFacilitati(facilitati);
        hotel.setIdLant(idLant);
        return update(hotel);
    }

    public boolean delete(int id) {
        // Mai întâi trebuie să ștergem toate camerele asociate acestui hotel
        String sqlDeleteCamere = "DELETE FROM camera WHERE id_hotel = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlDeleteCamere)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Eroare la ștergerea camerelor hotelului: " + e.getMessage());
            return false;
        }

        // Apoi putem șterge hotelul
        String sql = "DELETE FROM hotel WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la ștergerea hotelului: " + e.getMessage());
            return false;
        }
    }
}