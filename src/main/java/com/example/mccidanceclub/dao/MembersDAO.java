package com.example.mccidanceclub.dao;

import com.example.mccidanceclub.entities.Members;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembersDAO {

    private Connection connection;

    public MembersDAO() {
        // Initialise la connexion via DbConnection
        this.connection = DbConnection.getConnection();
    }

    // CREATE
    public boolean addMember(Members member) {
        String sql = "INSERT INTO members (first_name, last_name, email, phone, class) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, member.getFirstName());
            stmt.setString(2, member.getLastName());
            stmt.setString(3, member.getEmail());
            stmt.setString(4, member.getPhone());
            stmt.setString(5, member.getMemberClass());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ ALL
    public List<Members> getAllMembers() {
        List<Members> membersList = new ArrayList<>();
        String sql = "SELECT * FROM members ORDER BY member_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Members member = new Members(
                        rs.getInt("member_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("class")
                );
                membersList.add(member);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return membersList;
    }

    // UPDATE
    public boolean updateMember(Members member) {
        String sql = "UPDATE members SET first_name=?, last_name=?, email=?, phone=?, class=? WHERE member_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, member.getFirstName());
            stmt.setString(2, member.getLastName());
            stmt.setString(3, member.getEmail());
            stmt.setString(4, member.getPhone());
            stmt.setString(5, member.getMemberClass());
            stmt.setInt(6, member.getMemberId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteMember(int memberId) {
        String sql = "DELETE FROM members WHERE member_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
