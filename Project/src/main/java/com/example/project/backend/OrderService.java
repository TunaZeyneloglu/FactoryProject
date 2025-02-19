package com.example.project.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.scene.control.TextArea;

public class OrderService {

    public static void handleProductOrder(String customerId, String productId, double orderAmount, TextArea output) {
        Connection conn = null;
        PreparedStatement pstmtUpdateOrder = null;

        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Disable autocommit to manage transaction manually

            String orderId = createOrder(conn, customerId, orderAmount);
            if (orderId == null) {
                output.appendText("Sipariş oluşturulamadı!\n");
                conn.rollback();
                return;
            }

            Customer customer = CustomerService.findCustomerById(customerId);
            if (customer == null) {
                output.appendText("Müşteri bulunamadı!\n");
                conn.rollback();
                return;
            }

            Product product = ProductService.findProductById(productId);
            if (product == null) {
                output.appendText("Ürün bulunamadı!\n");
                conn.rollback();
                return;
            }

            CustomerProduct cp = findOrCreateCustomerProduct(conn, customer, product, orderId);
            if (cp == null) {
                output.appendText("Sipariş detayı bulunamadı/oluşturulamadı!\n");
                conn.rollback();
                return;
            }

            if (!updateOrderDetails(conn, orderId, productId, orderAmount)) {
                output.appendText("Sipariş güncellenemedi!\n");
                conn.rollback();
                return;
            }

            adjustCustomerBalanceAndDebt(conn, customerId, orderAmount);

            conn.commit(); // Commit the transaction if all operations are successful
            output.appendText("Sipariş kaydı başarıyla oluşturuldu!\n");
        } catch (SQLException e) {
            try {
                output.appendText("Sipariş oluşturulamadı!\n");
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                output.appendText("Rollback error: " + ex.getMessage() + "\n");
            }
            e.printStackTrace();
        } finally {
            try {
                if (pstmtUpdateOrder != null) pstmtUpdateOrder.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Restore autocommit mode before closing
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void recordProductDelivery(String customerId, String productId, double deliveredAmount, TextArea output) {
        Connection conn = null;
        PreparedStatement pstmtUpdateOrder = null;

        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            String updateOrderSql = "UPDATE OrderDetails SET delivered_quantity = delivered_quantity + ? WHERE order_id IN (SELECT order_id FROM Orders WHERE customer_id = ?) AND product_id = ?";
            pstmtUpdateOrder = conn.prepareStatement(updateOrderSql);
            pstmtUpdateOrder.setDouble(1, deliveredAmount);
            pstmtUpdateOrder.setString(2, customerId);
            pstmtUpdateOrder.setString(3, productId);

            int affectedRows = pstmtUpdateOrder.executeUpdate();

            // Check the affected rows
            if (affectedRows == 0) {
                conn.rollback();  // Rollback the transaction if no rows were updated
                output.appendText("Teslimat kaydı oluşturulamadı!\n");
                return;
            }

            // Update the customer's balance and debt after delivery
            updateCustomerBalanceAndDebt(conn, customerId, deliveredAmount);

            // Commit the transaction if all updates were successful
            conn.commit();
            output.appendText("Teslimat kaydı başarıyla oluşturuldu!\n");
        } catch (SQLException e) {
            // Catch SQLException and attempt rollback
            try {
                output.appendText("Teslimat kaydı oluşturulamadı!\n");
                if (conn != null) conn.rollback();  // Rollback the transaction in case of error
            } catch (SQLException ex) {
                output.appendText("Rollback error: " + ex.getMessage() + "\n");
            }
            e.printStackTrace();
        } finally {
            // Clean up resources
            try {
                if (pstmtUpdateOrder != null) pstmtUpdateOrder.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Restore the connection to its original state
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    private static String createOrder(Connection conn, String customerId, double totalAmount) throws SQLException {
        try (PreparedStatement pstmtInsertOrder = conn.prepareStatement("INSERT INTO Orders (customer_id, order_date, total_amount) VALUES (?, CURDATE(), ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmtInsertOrder.setString(1, customerId);
            pstmtInsertOrder.setDouble(2, totalAmount);
            int affectedRows = pstmtInsertOrder.executeUpdate();

            if (affectedRows == 0) {
                conn.rollback();
                return null;
            }

            ResultSet rs = pstmtInsertOrder.getGeneratedKeys();
            if (rs.next()) {
                return rs.getString(1);
            } else {
                conn.rollback();
                return null;
            }
        }
    }

    private static CustomerProduct findOrCreateCustomerProduct(Connection conn, Customer customer, Product product, String orderId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT quantity FROM OrderDetails WHERE order_id = ? AND customer_id = ? AND product_id = ?")) {
            pstmt.setString(1, orderId);
            pstmt.setString(2, customer.getId());
            pstmt.setString(3, product.getId());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int quantity = rs.getInt("quantity");
                return new CustomerProduct(product, quantity);
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO OrderDetails (order_id, customer_id, product_id, quantity, delivered_quantity) VALUES (?, ?, ?, 0, 0)")) {
                    insertStmt.setString(1, orderId);
                    insertStmt.setString(2, customer.getId());
                    insertStmt.setString(3, product.getId());
                    insertStmt.executeUpdate();
                    return new CustomerProduct(product, 0);
                }
            }
        }
    }

    private static boolean updateOrderDetails(Connection conn, String orderId, String productId, double amount) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE OrderDetails SET quantity = quantity + ? WHERE order_id = ? AND product_id = ?")) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, orderId);
            pstmt.setString(3, productId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    private static void adjustCustomerBalanceAndDebt(Connection conn, String customerId, double orderAmount) throws SQLException {
        PreparedStatement pstmtGetCustomer = null;
        PreparedStatement pstmtUpdateCustomer = null;
        ResultSet rs = null;

        try {
            // Retrieve current customer balance and debt
            String getCustomerSql = "SELECT balance, debt FROM Customers WHERE customer_id = ?";
            pstmtGetCustomer = conn.prepareStatement(getCustomerSql);
            pstmtGetCustomer.setString(1, customerId);
            rs = pstmtGetCustomer.executeQuery();

            double currentBalance = 0;
            double currentDebt = 0;

            if (rs.next()) {
                currentBalance = rs.getDouble("balance");
                currentDebt = rs.getDouble("debt");
            }

            // Adjust balance and debt based on order amount and existing debt
            if (currentDebt > 0) {
                if (currentDebt >= orderAmount) {
                    currentDebt -= orderAmount; // Subtract order amount from debt if sufficient
                } else {
                    orderAmount -= currentDebt;
                    currentDebt = 0;
                    currentBalance += orderAmount; // Add remaining amount to balance
                }
            } else {
                currentBalance += orderAmount; // Add order amount to balance directly
            }

            // Update the customer's balance and debt
            String updateCustomerSql = "UPDATE Customers SET balance = ?, debt = ? WHERE customer_id = ?";
            pstmtUpdateCustomer = conn.prepareStatement(updateCustomerSql);
            pstmtUpdateCustomer.setDouble(1, currentBalance);
            pstmtUpdateCustomer.setDouble(2, currentDebt);
            pstmtUpdateCustomer.setString(3, customerId);

            pstmtUpdateCustomer.executeUpdate();
        } catch (SQLException e) {
            throw e; // Propagate the exception for handling in the calling method
        } finally {
            // Clean up resources
            try {
                if (rs != null) rs.close();
                if (pstmtGetCustomer != null) pstmtGetCustomer.close();
                if (pstmtUpdateCustomer != null) pstmtUpdateCustomer.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void updateCustomerBalanceAndDebt(Connection conn, String customerId, double deliveredAmount) throws SQLException {
        PreparedStatement pstmtGetCustomer = null;
        PreparedStatement pstmtUpdateCustomer = null;
        ResultSet rs = null;

        try {
            // Retrieve current customer balance and debt
            String getCustomerSql = "SELECT balance, debt FROM Customers WHERE customer_id = ?";
            pstmtGetCustomer = conn.prepareStatement(getCustomerSql);
            pstmtGetCustomer.setString(1, customerId);
            rs = pstmtGetCustomer.executeQuery();

            double currentBalance = 0;
            double currentDebt = 0;

            if (rs.next()) {
                currentBalance = rs.getDouble("balance");
                currentDebt = rs.getDouble("debt");
            }

            // Calculate adjustments based on the delivered amount
            if (currentBalance > 0) {
                if (currentBalance >= deliveredAmount) {
                    currentBalance -= deliveredAmount; // Subtract all from balance if sufficient
                } else {
                    deliveredAmount -= currentBalance;
                    currentBalance = 0;
                    currentDebt= deliveredAmount;
                }
            }
            else{
                currentDebt+=deliveredAmount;
            }

            // Update the customer's balance and debt
            String updateCustomerSql = "UPDATE Customers SET balance = ?, debt = ? WHERE customer_id = ?";
            pstmtUpdateCustomer = conn.prepareStatement(updateCustomerSql);
            pstmtUpdateCustomer.setDouble(1, currentBalance);
            pstmtUpdateCustomer.setDouble(2, currentDebt);
            pstmtUpdateCustomer.setString(3, customerId);

            pstmtUpdateCustomer.executeUpdate();
        } catch (SQLException e) {
            throw e; // Propagate the exception for handling in the calling method
        } finally {
            // Clean up resources
            try {
                if (rs != null) rs.close();
                if (pstmtGetCustomer != null) pstmtGetCustomer.close();
                if (pstmtUpdateCustomer != null) pstmtUpdateCustomer.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}



