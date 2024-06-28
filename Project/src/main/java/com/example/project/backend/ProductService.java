package com.example.project.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductService {

    public static void addNewProduct(String productId, String productName, double productPrice) {
        Connection conn = null;
        PreparedStatement pstmtInsert = null;

        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Disable autocommit to manage transaction manually

            // Check if the product already exists
            if (isProductExists(conn, productId)) {
                System.out.println("Ürün ID'si zaten bulunuyor!");
                return;
            }

            // Insert new product
            String sql = "INSERT INTO Products (product_id, name, price) VALUES (?, ?, ?)";
            pstmtInsert = conn.prepareStatement(sql);
            pstmtInsert.setString(1, productId);
            pstmtInsert.setString(2, productName);
            pstmtInsert.setDouble(3, productPrice);

            int affectedRows = pstmtInsert.executeUpdate();

            if (affectedRows > 0) {
                conn.commit(); // Commit the transaction if insertion is successful
                System.out.println("Ürün başarıyla eklendi.");
            } else {
                conn.rollback(); // Rollback the transaction if insertion fails
                System.out.println("Ürün eklenemedi.");
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback the transaction on SQL exception
                }
                e.printStackTrace();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (pstmtInsert != null) pstmtInsert.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Restore autocommit mode before closing
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static boolean isProductExists(Connection conn, String productId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Products WHERE product_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        }
        return false;
    }

    public static Product findProductById(String productId) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT product_id, name, price FROM Products WHERE product_id = ?")) {
            pstmt.setString(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Product(rs.getString("product_id"), rs.getString("name"), rs.getDouble("price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void listProducts() {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT product_id, name, price FROM Products");
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Mevcut Ürünler:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getString("product_id") + ", İsim: " + rs.getString("name") + ", Fiyat: " + rs.getDouble("price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
