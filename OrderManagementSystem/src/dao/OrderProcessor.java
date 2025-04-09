package dao;

import entity.*;
import exception.OrderNotFoundException;
import util.DBConnUtil;

import java.sql.*;
import java.util.*;

public class OrderProcessor implements IOrderManagementRepository {
    private Connection conn;

    public OrderProcessor() {
        this.conn = DBConnUtil.getConnection("db.properties");
        if (this.conn == null) {
            throw new RuntimeException("Database connection is null. Please check your db.properties and DB server.");
        }
    }


    public void createUser(User user) {
        String sql = "INSERT INTO user (userId, username, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole());
            pstmt.executeUpdate();
            System.out.println("User created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createProduct(User user, Product product) {
        if (!"Admin".equalsIgnoreCase(user.getRole())) {
            System.out.println("Only Admin can create a product.");
            return;
        }
        String sql = "INSERT INTO product (productId, productName, description, price, quantityInStock, type) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, product.getProductId());
            pstmt.setString(2, product.getProductName());
            pstmt.setString(3, product.getDescription());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setInt(5, product.getQuantityInStock());
            pstmt.setString(6, product.getType());
            pstmt.executeUpdate();
            System.out.println("Product created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createOrder(User user, List<Product> products) {
        try {
            conn.setAutoCommit(false);

            String userCheckSql = "SELECT * FROM user WHERE userId = ?";
            try (PreparedStatement userCheckStmt = conn.prepareStatement(userCheckSql)) {
                userCheckStmt.setInt(1, user.getUserId());
                ResultSet rs = userCheckStmt.executeQuery();
                if (!rs.next()) {
                    createUser(user);
                }
            }

            String insertOrderSql = "INSERT INTO orders (userId) VALUES (?)";
            int orderId = 0;
            try (PreparedStatement orderStmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setInt(1, user.getUserId());
                orderStmt.executeUpdate();
                ResultSet generatedKeys = orderStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                }
            }

            String insertItemSql = "INSERT INTO order_item (orderId, productId) VALUES (?, ?)";
            try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSql)) {
                for (Product product : products) {
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, product.getProductId());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }

            conn.commit();
            System.out.println("Order created successfully with ID: " + orderId);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void cancelOrder(int userId, int orderId) {
        try {
            String orderCheckSql = "SELECT * FROM orders WHERE orderId = ? AND userId = ?";
            try (PreparedStatement orderStmt = conn.prepareStatement(orderCheckSql)) {
                orderStmt.setInt(1, orderId);
                orderStmt.setInt(2, userId);
                ResultSet rs = orderStmt.executeQuery();
                if (!rs.next()) throw new OrderNotFoundException("Order not found.");
            }

            String deleteItems = "DELETE FROM order_item WHERE orderId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteItems)) {
                stmt.setInt(1, orderId);
                stmt.executeUpdate();
            }

            String deleteOrder = "DELETE FROM orders WHERE orderId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteOrder)) {
                stmt.setInt(1, orderId);
                stmt.executeUpdate();
            }

            System.out.println("Order cancelled successfully.");
        } catch (SQLException | OrderNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("productId"),
                        rs.getString("productName"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("quantityInStock"),
                        rs.getString("type")
                );
                products.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getOrderByUser(User user) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.* FROM product p JOIN order_item oi ON p.productId = oi.productId JOIN orders o ON o.orderId = oi.orderId WHERE o.userId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user.getUserId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("productId"),
                        rs.getString("productName"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("quantityInStock"),
                        rs.getString("type")
                );
                products.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
}
