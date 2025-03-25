package org.example.model.dao;

import org.example.model.database.DatabaseConnection;
import org.example.model.entities.Lant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LantDAO {
    private Connection connection;

    public LantDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Lant> findAll() {
        List<Lant> lanturi = new ArrayList<>();
        String sql = "SELECT * FROM lant";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Lant lant = new Lant();
                lant.setId(rs.getInt("id"));
                lant.setNume(rs.getString("nume"));
                lanturi.add(lant);
            }
        } catch (SQLException e) {
            System.err.println("Eroare la citirea lanțurilor: " + e.getMessage());
        }

        return lanturi;
    }

    public Object[][] getLanturiAsObjects() {
        List<Lant> lanturi = findAll();
        Object[][] result = new Object[lanturi.size()][2];

        for (int i = 0; i < lanturi.size(); i++) {
            Lant lant = lanturi.get(i);
            result[i][0] = lant.getId();
            result[i][1] = lant.getNume();
        }

        return result;
    }

    public Lant findById(int id) {
        String sql = "SELECT * FROM lant WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Lant lant = new Lant();
                    lant.setId(rs.getInt("id"));
                    lant.setNume(rs.getString("nume"));
                    return lant;
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la găsirea lanțului: " + e.getMessage());
        }

        return null;
    }

    public boolean save(Lant lant) {
        String sql = "INSERT INTO lant (nume) VALUES (?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, lant.getNume());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    lant.setId(generatedKeys.getInt(1));
                } else {
                    return false;
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Eroare la salvarea lanțului: " + e.getMessage());
            return false;
        }
    }

    public boolean save(String nume) {
        Lant lant = new Lant();
        lant.setNume(nume);
        return save(lant);
    }

    public boolean update(Lant lant) {
        String sql = "UPDATE lant SET nume = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, lant.getNume());
            pstmt.setInt(2, lant.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la actualizarea lanțului: " + e.getMessage());
            return false;
        }
    }

    public boolean update(int id, String nume) {
        Lant lant = new Lant();
        lant.setId(id);
        lant.setNume(nume);
        return update(lant);
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM lant WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la ștergerea lanțului: " + e.getMessage());
            return false;
        }
    }
}