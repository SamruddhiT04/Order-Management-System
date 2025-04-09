package dao;

import entity.Product;
import entity.User;
import java.util.List;

public interface IOrderManagementRepository {
    void createUser(User user);
    void createProduct(User user, Product product);
    void createOrder(User user, List<Product> products);
    void cancelOrder(int userId, int orderId);
    List<Product> getAllProducts();
    List<Product> getOrderByUser(User user);
}
