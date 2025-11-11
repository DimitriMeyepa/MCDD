package com.example.mccidanceclub.dao;

import com.example.mccidanceclub.entities.Contribution;
import com.example.mccidanceclub.entities.Members;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContributionDAO {

    private Connection conn;

    public ContributionDAO() {
        conn = DbConnection.getConnection();
    }

    // Ajouter une contribution et mettre à jour le solde global
    public boolean addContribution(Contribution contribution) {
        try {
            double currentBalance = getCurrentBalance();
            double newBalance = currentBalance + contribution.getAmount();

            String sql = "INSERT INTO contributions (member_id, amount, total_balance) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, contribution.getMember().getMemberId());
            ps.setDouble(2, contribution.getAmount());
            ps.setDouble(3, newBalance);

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Modifier une contribution existante
    public boolean updateContribution(Contribution contribution) {
        try {
            // On pourrait recalculer le total_balance ici si nécessaire
            String sql = "UPDATE contributions SET member_id = ?, amount = ? WHERE contribution_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, contribution.getMember().getMemberId());
            ps.setDouble(2, contribution.getAmount());
            ps.setInt(3, contribution.getContributionId());

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer une contribution
    public boolean deleteContribution(int contributionId) {
        try {
            String sql = "DELETE FROM contributions WHERE contribution_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, contributionId);

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtenir toutes les contributions avec le membre
    public List<Contribution> getAllContributions() {
        List<Contribution> contributions = new ArrayList<>();
        try {
            String sql = "SELECT c.contribution_id, c.member_id, c.amount, c.total_balance, " +
                    "m.first_name, m.last_name, m.email, m.phone, m.class " +
                    "FROM contributions c " +
                    "JOIN members m ON c.member_id = m.member_id " +
                    "ORDER BY c.contribution_id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Members member = new Members(
                        rs.getInt("member_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("class")
                );

                Contribution contribution = new Contribution(
                        rs.getInt("contribution_id"),
                        member,
                        rs.getDouble("amount"),
                        rs.getDouble("total_balance")
                );
                contributions.add(contribution);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contributions;
    }

    // Récupérer le solde global actuel
    public double getCurrentBalance() {
        double balance = 0;
        try {
            String sql = "SELECT total_balance FROM contributions ORDER BY contribution_id DESC LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) balance = rs.getDouble("total_balance");
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }
}
