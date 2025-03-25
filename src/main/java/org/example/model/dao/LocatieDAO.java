package org.example.model.dao;

import org.example.model.database.DatabaseConnection;
import org.example.model.entities.Locatie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocatieDAO {
    private Connection connection;

    public LocatieDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Locatie> findAll() {
        List<Locatie> locatii = new ArrayList<>();
        String sql = "SELECT * FROM locatie";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Locatie locatie = new Locatie();
                locatie.setId(rs.getInt("id"));
                locatie.setTara(rs.getString("tara"));
                locatie.setOras(rs.getString("oras"));
                locatie.setStrada(rs.getString("strada"));
                locatie.setNumar(rs.getString("numar"));
                locatii.add(locatie);
            }
        } catch (SQLException e) {
            System.err.println("Eroare la citirea locațiilor: " + e.getMessage());
        }

        return locatii;
    }

    public Object[][] getLocatiiAsObjects() {
        List<Locatie> locatii = findAll();
        Object[][] result = new Object[locatii.size()][2];

        for (int i = 0; i < locatii.size(); i++) {
            Locatie locatie = locatii.get(i);
            result[i][0] = locatie.getId();

            // Create a display string that combines all location information
            String displayText = locatie.getOras() + ", " + locatie.getStrada() + " " +
                    locatie.getNumar() + ", " + locatie.getTara();
            result[i][1] = displayText;
        }

        return result;
    }

    public Locatie findById(int id) {
        String sql = "SELECT * FROM locatie WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Locatie locatie = new Locatie();
                    locatie.setId(rs.getInt("id"));
                    locatie.setTara(rs.getString("tara"));
                    locatie.setOras(rs.getString("oras"));
                    locatie.setStrada(rs.getString("strada"));
                    locatie.setNumar(rs.getString("numar"));
                    return locatie;
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la găsirea locației: " + e.getMessage());
        }

        return null;
    }

    public boolean save(Locatie locatie) {
        String sql = "INSERT INTO locatie (tara, oras, strada, numar) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, locatie.getTara());
            pstmt.setString(2, locatie.getOras());
            pstmt.setString(3, locatie.getStrada());
            pstmt.setString(4, locatie.getNumar());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    locatie.setId(generatedKeys.getInt(1));
                } else {
                    return false;
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Eroare la salvarea locației: " + e.getMessage());
            return false;
        }
    }

    public boolean save(String tara, String oras, String strada, String numar) {
        Locatie locatie = new Locatie();
        locatie.setTara(tara);
        locatie.setOras(oras);
        locatie.setStrada(strada);
        locatie.setNumar(numar);
        return save(locatie);
    }

    public boolean update(Locatie locatie) {
        String sql = "UPDATE locatie SET tara = ?, oras = ?, strada = ?, numar = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, locatie.getTara());
            pstmt.setString(2, locatie.getOras());
            pstmt.setString(3, locatie.getStrada());
            pstmt.setString(4, locatie.getNumar());
            pstmt.setInt(5, locatie.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la actualizarea locației: " + e.getMessage());
            return false;
        }
    }

    public boolean update(int id, String tara, String oras, String strada, String numar) {
        Locatie locatie = new Locatie();
        locatie.setId(id);
        locatie.setTara(tara);
        locatie.setOras(oras);
        locatie.setStrada(strada);
        locatie.setNumar(numar);
        return update(locatie);
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM locatie WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la ștergerea locației: " + e.getMessage());
            return false;
        }
    }

    public String getDisplayString(int id) {
        Locatie locatie = findById(id);
        if (locatie != null) {
            return locatie.getOras() + ", " + locatie.getStrada() + " " +
                    locatie.getNumar() + ", " + locatie.getTara();
        }
        return "";
    }
}