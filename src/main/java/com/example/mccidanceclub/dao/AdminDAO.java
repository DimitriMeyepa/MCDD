package com.example.mccidanceclub.dao;

import com.example.mccidanceclub.entities.Admin;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    private Connection connection;

    public AdminDAO() {
        connection = DbConnection.getConnection();
    }

    // Hasher le mot de passe avec BCrypt
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Vérifier le mot de passe avec BCrypt
    private boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    // Ajouter un admin
    public boolean addAdmin(Admin admin) {
        String sql = "INSERT INTO admin (username, password) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, admin.getUsername());
            ps.setString(2, hashPassword(admin.getPassword()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Mettre à jour un admin
    public boolean updateAdmin(Admin admin) {
        String sql = "UPDATE admin SET username=?, password=? WHERE id_admin=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, admin.getUsername());

            // Si le mot de passe a changé, on le hash, sinon on garde l'ancien
            if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
                ps.setString(2, hashPassword(admin.getPassword()));
            } else {
                // Récupérer l'ancien mot de passe de la base
                String currentPassword = getCurrentPassword(admin.getIdAdmin());
                ps.setString(2, currentPassword);
            }

            ps.setInt(3, admin.getIdAdmin());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Récupérer le mot de passe actuel depuis la base
    private String getCurrentPassword(int idAdmin) {
        String sql = "SELECT password FROM admin WHERE id_admin = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idAdmin);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    // Supprimer un admin
    public boolean deleteAdmin(int idAdmin) {
        String sql = "DELETE FROM admin WHERE id_admin=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idAdmin);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Récupérer un admin par son ID
    public Admin getAdminById(int idAdmin) {
        String sql = "SELECT * FROM admin WHERE id_admin=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idAdmin);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Admin(
                        rs.getInt("id_admin"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Vérifier si un username existe déjà
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM admin WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Vérifier si un username existe déjà (pour un autre admin)
    public boolean usernameExistsForOtherAdmin(String username, int excludedAdminId) {
        String sql = "SELECT COUNT(*) FROM admin WHERE username = ? AND id_admin != ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, excludedAdminId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lister tous les admins
    public List<Admin> getAllAdmins() {
        List<Admin> list = new ArrayList<>();
        String sql = "SELECT * FROM admin ORDER BY id_admin";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Admin admin = new Admin(
                        rs.getInt("id_admin"),
                        rs.getString("username"),
                        rs.getString("password")
                );
                list.add(admin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Vérifier login
    public Admin login(String username, String password) {
        String sql = "SELECT * FROM admin WHERE username=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password");

                // Vérifier le mot de passe avec BCrypt
                if (verifyPassword(password, storedHash)) {
                    return new Admin(
                            rs.getInt("id_admin"),
                            rs.getString("username"),
                            storedHash
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Compter le nombre d'admins (pour empêcher la suppression du dernier admin)
    public int countAdmins() {
        String sql = "SELECT COUNT(*) FROM admin";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}