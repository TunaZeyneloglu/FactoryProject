package com.example.project.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.TextArea;

public class CustomerService {

    public static void addNewCustomer(String customerId, String customerName, String customerEmail, double initialBalance) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false); // Disable autocommit
    
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT count(*) FROM Customers WHERE customer_id = ?")) {
                checkStmt.setString(1, customerId);
                ResultSet rs = checkStmt.executeQuery();
    
                if (rs.next() && rs.getInt(1) == 0) {
                    try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Customers (customer_id, name, email, balance, debt) VALUES (?, ?, ?, ?, 0.00)")) {
                        insertStmt.setString(1, customerId);
                        insertStmt.setString(2, customerName);
                        insertStmt.setString(3, customerEmail);
                        insertStmt.setDouble(4, initialBalance);
    
                        int affectedRows = insertStmt.executeUpdate();
    
                        if (affectedRows > 0) {
                            conn.commit(); // Commit the transaction
                            System.out.println("Yeni müşteri başarıyla eklendi.");
                        } else {
                            conn.rollback(); // Rollback if no rows were affected
                            System.out.println("Müşteri eklenemedi.");
                        }
                    }
                } else {
                    System.out.println("Müşteri ID'si zaten mevcut!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static Customer findCustomerById(String customerId) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT customer_id, name FROM Customers WHERE customer_id = ?")) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Customer(rs.getString("customer_id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void listCustomers() {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT customer_id, name FROM Customers");
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Mevcut Müşterileri:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getString("customer_id") + ", İsim: " + rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void printAllBalances(TextArea output) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT customer_id, name, balance, debt FROM Customers");
             ResultSet rs = pstmt.executeQuery()) {

            StringBuilder sb = new StringBuilder("Müşteri Bakiyeleri:\n");
            while (rs.next()) {
                sb.append(String.format("Customer ID: %s, Name: %s, Balance: %.2f, Debt: %.2f\n", rs.getString("customer_id"), rs.getString("name"), rs.getDouble("balance"), rs.getDouble("debt")));
            }
            output.setText(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void printCustomerBalance(String customerId, TextArea output) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT customer_id, name, balance, debt FROM Customers WHERE customer_id = ?")) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                output.setText(String.format("Customer ID: %s, Name: %s, Balance: %.2f, Debt: %.2f\n", rs.getString("customer_id"), rs.getString("name"), rs.getDouble("balance"), rs.getDouble("debt")));
            } else {
                output.setText("Müşteri bulunamadı!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
}
