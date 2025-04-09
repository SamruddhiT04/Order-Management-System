package main;

import dao.*;
import entity.*;
import java.util.*;

public class MainModule {
    public static void main(String[] args) {
        IOrderManagementRepository repo = new OrderProcessor(); // Handles DB connection
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Order Management Menu =====");
            System.out.println("1. Create User");
            System.out.println("2. Create Product");
            System.out.println("3. Create Order");
            System.out.println("4. Cancel Order");
            System.out.println("5. Get All Products");
            System.out.println("6. Get Order By User");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter User ID: ");
                    int userId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Username: ");
                    String username = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String password = sc.nextLine();
                    System.out.print("Enter Role (Admin/User): ");
                    String role = sc.nextLine();
                    User user = new User(userId, username, password, role);
                    repo.createUser(user); // ✅ Un-commented to allow user creation
                    break;

                case 2:
                    System.out.print("Enter Admin User ID: ");
                    int adminId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Admin Username: ");
                    String adminUsername = sc.nextLine();
                    System.out.print("Enter Admin Password: ");
                    String adminPassword = sc.nextLine();
                    User adminUser = new User(adminId, adminUsername, adminPassword, "Admin");

                    System.out.print("Enter Product ID: ");
                    int pid = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Product Name: ");
                    String pname = sc.nextLine();
                    System.out.print("Enter Description: ");
                    String desc = sc.nextLine();
                    System.out.print("Enter Price: ");
                    double price = sc.nextDouble();
                    System.out.print("Enter Quantity: ");
                    int qty = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Type (Electronics/Clothing): ");
                    String type = sc.nextLine();

                    Product product = new Product(pid, pname, desc, price, qty, type);
                    repo.createProduct(adminUser, product);
                    break;

                case 3:
                    System.out.print("Enter User ID: ");
                    int ouid = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Username: ");
                    String ouname = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String oupass = sc.nextLine();
                    System.out.print("Enter Role: ");
                    String ourole = sc.nextLine();
                    User orderUser = new User(ouid, ouname, oupass, ourole);

                    List<Product> orderProducts = new ArrayList<>();
                    System.out.print("Enter number of products to order: ");
                    int num = sc.nextInt();
                    for (int i = 0; i < num; i++) {
                        System.out.print("Enter Product ID: ");
                        int opid = sc.nextInt();
                        orderProducts.add(new Product(opid, null, null, 0.0, 0, null));
                    }
                    repo.createOrder(orderUser, orderProducts);
                    break;

                case 4:
                    System.out.print("Enter User ID: ");
                    int cuid = sc.nextInt();
                    System.out.print("Enter Order ID: ");
                    int oid = sc.nextInt();
                    repo.cancelOrder(cuid, oid);
                    break;

                case 5:
                    List<Product> allProducts = repo.getAllProducts();
                    allProducts.forEach(System.out::println); // ✅ Works only if toString() is implemented
                    break;

                case 6:
                    System.out.print("Enter User ID: ");
                    int guid = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Username: ");
                    String guname = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String gupass = sc.nextLine();
                    System.out.print("Enter Role: ");
                    String gurole = sc.nextLine();
                    User gu = new User(guid, guname, gupass, gurole);

                    List<Product> userOrders = repo.getOrderByUser(gu);
                    userOrders.forEach(System.out::println); // ✅ Works only if toString() is implemented
                    break;

                case 7:
                    System.out.println("Exiting...");
                    sc.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
