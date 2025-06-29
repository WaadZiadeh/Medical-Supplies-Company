import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class MySQLConnection {
    static final String DB_URL = "jdbc:mysql://localhost:3306/medical_supplies"; // Database URL
    static final String USER = "root"; // MySQL username
    static final String PASS = "0598451852Amereid"; // MySQL password
    static final String QUERY = "SELECT * FROM Product"; // SQL query to select from Product table

    public static void main(String[] args) {
        
        // Open a connection
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(QUERY)) {

            // Extract data from result set
            while (rs.next()) {
                // Retrieve by column name
                System.out.print("ProductID: " + rs.getInt("ProductID")); // Assuming ProductID is an INT
                System.out.print(", ProductName: " + rs.getString("ProductName"));
                System.out.print(", Price: " + rs.getDouble("Price"));
                System.out.print(", StockQuantity: " + rs.getDouble("StockQuantity"));
                System.out.println(", ProductionCompany: " + rs.getString("ProductionCompany"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Equipment> getEquipmentList() throws SQLException {
        ObservableList<Equipment> equipmentList = FXCollections.observableArrayList();
        String selectQuery = "SELECT p.productID, p.productName, p.Price, p.StockQuantity, p.productioncompany, " +
                "m.Warranty_Period, m.power_Source, m.Certification, m.Expected_Lifespan " +
                "FROM product p JOIN medical_equipment m ON p.productID = m.productID";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement pstmt = conn.prepareStatement(selectQuery);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Retrieve product attributes
                int productID = rs.getInt("productID");
                String productName = rs.getString("productName");
                double price = rs.getDouble("Price");
                int stockQuantity = rs.getInt("StockQuantity");
                String productionCompany = rs.getString("productioncompany");

                // Retrieve medical equipment attributes
                String warrantyPeriod = rs.getString("Warranty_Period");
                String powerSource = rs.getString("power_Source");
                String certification = rs.getString("Certification");
                String expectedLifeSpan = rs.getString("Expected_LifeSpan");

                // Create and add Equipment object to the list
                Equipment equipment = new Equipment(productID, productName, price, stockQuantity, productionCompany,
                        warrantyPeriod, powerSource, certification, expectedLifeSpan);
                equipmentList.add(equipment);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving equipment list: " + e.getMessage());
            throw e; // Re-throw exception for higher-level handling
        }
        return equipmentList;
    }

    public ObservableList<Supply> getSupplies() throws SQLException {
        ObservableList<Supply> supplyList = FXCollections.observableArrayList();
        String selectQuery = "SELECT p.productID, p.productName, p.price, p.StockQuantity, p.productioncompany, " +
                "m.expiration_date, m.Recyclability, m.usage_Instructions, m.storage_requirements " +
                "FROM product p " +
                "JOIN medical_supplies m ON p.productID = m.productID";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement pstmt = conn.prepareStatement(selectQuery);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Fetch data from ResultSet
                int productID = rs.getInt("productID");
                String productName = rs.getString("productName");
                double price = rs.getDouble("price");
                int stockQuantity = rs.getInt("StockQuantity");
                String productionCompany = rs.getString("productioncompany");

                Date expirationDate = rs.getDate("expiration_date");
                boolean recyclability = rs.getBoolean("Recyclability");
                String usageInstructions = rs.getString("usage_Instructions");
                String storageRequirements = rs.getString("storage_requirements");

                // Create Supply object
                Supply supply = new Supply(productID, productName, price, stockQuantity, productionCompany,
                        expirationDate, recyclability, usageInstructions, storageRequirements);

                // Add to list
                supplyList.add(supply);
            }
        }
        return supplyList;
    }

    public void insertProduct(
            int productId, String productName, double price, int stockQuantity,
            String productionCompany,
            String type,
            Equipment additionalAttributes,
            Supply SuppliesAdditionalAtteributes) {
        String productInsertQuery = "INSERT INTO product (productID, productName, Price, StockQuantity, productioncompany) VALUES (?, ?, ?, ?, ?)";
        String equipmentInsertQuery = "INSERT INTO medical_equipment (productID, Warranty_Period, power_Source, Certification, Expected_Lifespan) VALUES (?, ?, ?, ?, ?)";
        String suppliesInsertQuery = "INSERT INTO Medical_supplies (productID, expiration_date, Recyclability, usage_Instructions, storage_requirements) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            conn.setAutoCommit(false); // Start transaction

            // Check if productID already exists
            String checkQuery = "SELECT COUNT(*) FROM product WHERE productID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {
                pstmt.setInt(1, productId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new IllegalArgumentException("ProductID already exists.");
                    }
                }
            }

            // Insert base product details
            try (PreparedStatement pstmt = conn.prepareStatement(productInsertQuery)) {
                pstmt.setInt(1, productId);
                pstmt.setString(2, productName);
                pstmt.setDouble(3, price);
                pstmt.setInt(4, stockQuantity);
                pstmt.setString(5, productionCompany);
                pstmt.executeUpdate();
            }

            // Insert specific type details
            if ("MedicalEquipment".equalsIgnoreCase(type)) {
                if (additionalAttributes.length != 4) {
                    throw new IllegalArgumentException(
                            "MedicalEquipment requires 4 additional attributes: WarrantyPeriod, PowerSource, Certification, ExpectedLifespan");
                }
                try (PreparedStatement pstmt = conn.prepareStatement(equipmentInsertQuery)) {
                    pstmt.setInt(1, productId);
                    pstmt.setString(2, (String) additionalAttributes.getWarrantyPeriod()); // WarrantyPeriod
                    pstmt.setString(3, (String) additionalAttributes.getPowerSource()); // PowerSource
                    pstmt.setString(4, (String) additionalAttributes.getCertification()); // Certification
                    pstmt.setString(5, (String) additionalAttributes.getExpectedLifespanl()); // ExpectedLifespan
                    pstmt.executeUpdate();
                }
            } else if ("MedicalSupplies".equalsIgnoreCase(type)) {
                if (SuppliesAdditionalAtteributes.length != 4) {
                    throw new IllegalArgumentException(
                            "MedicalEquipment requires 4 additional attributes: WarrantyPeriod, PowerSource, Certification, ExpectedLifespan. Provided: ");
                }
                try (PreparedStatement pstmt = conn.prepareStatement(suppliesInsertQuery)) {
                    pstmt.setInt(1, productId);
                    pstmt.setDate(2, (java.sql.Date) SuppliesAdditionalAtteributes.getExpirationDate()); // ExpirationDate
                    pstmt.setBoolean(3, (Boolean) SuppliesAdditionalAtteributes.isRecyclability()); // Recyclability
                    pstmt.setString(4, (String) SuppliesAdditionalAtteributes.getUsageInstructions()); // UsageInstructions
                    pstmt.setString(5, (String) SuppliesAdditionalAtteributes.getStorageRequirements()); // StorageRequirements
                    pstmt.executeUpdate();
                }
            } else {
                throw new IllegalArgumentException(
                        "Invalid product type. Must be 'MedicalEquipment' or 'MedicalSupplies'.");
            }

            conn.commit(); // Commit transaction
            System.out.println("Product inserted successfully.");
        } catch (SQLException e) {
            System.err.println("Error inserting product: " + e.getMessage());
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                if (conn != null) {
                    conn.rollback(); // Rollback changes
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Validation Error: " + e.getMessage());
        }
    }

    public void updateProduct(
            int productId, // The existing product ID to be updated
            String productName, Double price, Integer stockQuantity,
            String productionCompany,
            String type, // "MedicalEquipment" or "MedicalSupplies"
            Equipment additionalAttributes,
            Supply suppliesAdditionalAttributes) {
        // Check if the product ID exists before proceeding with the update
        if (!productExists(productId)) {
            System.err.println("Product with ID " + productId + " does not exist. Update aborted.");
            return;
        }

        String productUpdateQuery = "UPDATE product SET productName = ?, Price = ?, StockQuantity = ?, productioncompany = ? WHERE productID = ?";
        String equipmentUpdateQuery = "UPDATE medical_equipment SET Warranty_Period = ?, power_Source = ?, Certification = ?, Expected_Lifespan = ? WHERE productID = ?";
        String suppliesUpdateQuery = "UPDATE Medical_supplies SET expiration_date = ?, Recyclability = ?, usage_Instructions = ?, storage_requirements = ? WHERE productID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Update common product attributes
            try (PreparedStatement pstmt = conn.prepareStatement(productUpdateQuery)) {
                pstmt.setString(1, productName); // Update ProductName
                pstmt.setDouble(2, price); // Update Price
                pstmt.setInt(3, stockQuantity); // Update StockQuantity
                pstmt.setString(4, productionCompany); // Update ProductionCompany
                pstmt.setInt(5, productId); // Where ProductID = productId
                pstmt.executeUpdate();
            }

            // Update type-specific attributes
            if ("MedicalEquipment".equalsIgnoreCase(type)) {
                if (additionalAttributes == null) {
                    throw new IllegalArgumentException("MedicalEquipment requires additional attributes.");
                }
                try (PreparedStatement pstmt = conn.prepareStatement(equipmentUpdateQuery)) {
                    pstmt.setString(1, additionalAttributes.getWarrantyPeriod()); // WarrantyPeriod
                    pstmt.setString(2, additionalAttributes.getPowerSource()); // PowerSource
                    pstmt.setString(3, additionalAttributes.getCertification()); // Certification
                    pstmt.setString(4, additionalAttributes.getExpectedLifespanl()); // ExpectedLifespan
                    pstmt.setInt(5, productId); // Where ProductID = productId
                    pstmt.executeUpdate();
                }
            } else if ("MedicalSupplies".equalsIgnoreCase(type)) {
                if (suppliesAdditionalAttributes == null) {
                    throw new IllegalArgumentException("MedicalSupplies requires additional attributes.");
                }
                try (PreparedStatement pstmt = conn.prepareStatement(suppliesUpdateQuery)) {
                    pstmt.setDate(1, suppliesAdditionalAttributes.getExpirationDate()); // ExpirationDate
                    pstmt.setBoolean(2, suppliesAdditionalAttributes.isRecyclability()); // Recyclability
                    pstmt.setString(3, suppliesAdditionalAttributes.getUsageInstructions()); // UsageInstructions
                    pstmt.setString(4, suppliesAdditionalAttributes.getStorageRequirements()); // StorageRequirements
                    pstmt.setInt(5, productId); // Where ProductID = productId
                    pstmt.executeUpdate();
                }
            } else {
                throw new IllegalArgumentException(
                        "Invalid product type. Must be 'MedicalEquipment' or 'MedicalSupplies'.");
            }

            System.out.println("Product updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
        }
    }

    public boolean productExists(int productId) {
        String searchQuery = "SELECT 1 FROM Product WHERE ProductID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement pstmt = conn.prepareStatement(searchQuery)) {
            pstmt.setInt(1, productId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // If a product with this ID exists, it will return true
            }
        } catch (SQLException e) {
            System.err.println("Error checking product existence: " + e.getMessage());
            return false;
        }
    }

    public boolean isProductIdAlreadyExists(int newProductId) {
        String searchQuery = "SELECT 1 FROM Product WHERE ProductID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement pstmt = conn.prepareStatement(searchQuery)) {
            pstmt.setInt(1, newProductId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // If a product with this ID exists, it will return true
            }
        } catch (SQLException e) {
            System.err.println("Error checking if new ProductID already exists: " + e.getMessage());
            return false;
        }
    }

    public void deleteProduct(int productID) {
        String searchQuery = "SELECT * FROM product WHERE productID = ?";
        String deleteQuery = "DELETE FROM product WHERE productID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement searchStmt = conn.prepareStatement(searchQuery);
                PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            // Set the productID for the search query
            searchStmt.setInt(1, productID);

            try (ResultSet rs = searchStmt.executeQuery()) {
                if (rs.next()) {
                    // Product found, print details
                    System.out.println("Found Product:");
                    System.out.println("Product ID: " + rs.getInt("productID"));
                    System.out.println("Product Name: " + rs.getString("productName"));

                    // Set the productID for the delete query
                    deleteStmt.setInt(1, productID);

                    // Execute delete query
                    int rowsDeleted = deleteStmt.executeUpdate();
                    if (rowsDeleted > 0) {
                        System.out.println("Record deleted successfully.");
                    } else {
                        System.out.println("Failed to delete the record.");
                    }
                } else {
                    // No product found with the given productID
                    System.out.println("No record found with productID: " + productID);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error deleting record: " + e.getMessage());
        }
    }

    public ObservableList<Equipment> searchEquipment(String search) throws SQLException {
        ObservableList<Equipment> searchResults = FXCollections.observableArrayList();
        String searchQuery = "SELECT p.productID, p.productName, p.Price, p.StockQuantity, p.productioncompany, " +
                "m.Warranty_Period, m.power_Source, m.Certification, m.Expected_Lifespan " +
                "FROM product p JOIN medical_equipment m ON p.productID = m.productID " +
                "WHERE p.productName LIKE ? or p.productID =?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement pstmt = conn.prepareStatement(searchQuery)) {

            // Set search parameter (use % for wildcard search)
            String searchPattern = "%" + search + "%";
            pstmt.setString(1, searchPattern);

            try {
                int productID = Integer.parseInt(search);
                pstmt.setInt(2, productID);
            } catch (NumberFormatException e) {
                // If the search term is not a valid integer, set the product ID to an invalid
                // value
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Retrieve product attributes
                    int productID = rs.getInt("productID");
                    String productName = rs.getString("productName");
                    double price = rs.getDouble("Price");
                    int stockQuantity = rs.getInt("StockQuantity");
                    String productionCompany = rs.getString("productioncompany");

                    // Retrieve medical equipment attributes
                    String warrantyPeriod = rs.getString("Warranty_Period");
                    String powerSource = rs.getString("power_Source");
                    String certification = rs.getString("Certification");
                    String expectedLifeSpan = rs.getString("Expected_LifeSpan");

                    // Create and add Equipment object to the list
                    Equipment equipment = new Equipment(productID, productName, price, stockQuantity, productionCompany,
                            warrantyPeriod, powerSource, certification, expectedLifeSpan);
                    searchResults.add(equipment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching equipment: " + e.getMessage());
            throw e; // Re-throw exception for higher-level handling
        }
        return searchResults;
    }

    public ObservableList<Supply> searchSupply(String search) throws SQLException {
        ObservableList<Supply> searchResults = FXCollections.observableArrayList();
        String searchQuery = "SELECT p.productID, p.productName, p.Price, p.StockQuantity, p.productioncompany, " +
                "m.expiration_date, m.Recyclability, m.usage_Instructions, m.storage_requirements " +
                "FROM product p JOIN Medical_supplies m ON p.productID = m.productID " +
                "WHERE p.productName LIKE ? Or p.productID = ?  ";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement pstmt = conn.prepareStatement(searchQuery)) {

            // Set search parameter (use % for wildcard search)
            String searchPattern = "%" + search + "%";
            pstmt.setString(1, searchPattern);

            try {
                int productID = Integer.parseInt(search);
                pstmt.setInt(2, productID);
            } catch (NumberFormatException e) {
                // If the search term is not a valid integer, set the product ID to an invalid
                // value
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Retrieve product attributes
                    int productID = rs.getInt("productID");
                    String productName = rs.getString("productName");
                    double price = rs.getDouble("Price");
                    int stockQuantity = rs.getInt("StockQuantity");
                    String productionCompany = rs.getString("productioncompany");

                    // Retrieve medical equipment attributes
                    Date expirationDate = rs.getDate("expiration_date");
                    Boolean Recyclability = rs.getBoolean("Recyclability");
                    String usageInstructions = rs.getString("usage_Instructions");
                    String storageRequirements = rs.getString("storage_requirements");
                    // Create and add Equipment object to the list
                    Supply supply = new Supply(productID, productName, price, stockQuantity, productionCompany,
                            expirationDate, Recyclability, usageInstructions, storageRequirements);
                    searchResults.add(supply);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching equipment: " + e.getMessage());
            throw e; // Re-throw exception for higher-level handling
        }
        return searchResults;
    }

    public String getUserRole(String username, String password) {
        String result = "Error: User not found.";
    
        String getSaltQuery = "SELECT salt FROM login WHERE username = ?";
        String getUserRoleQuery = "SELECT 'Customer' AS Role, c.customername AS Name, l.username " +
                "FROM customer c " +
                "JOIN login l ON l.username = c.username " +
                "WHERE l.username = ? AND l.password = ? " +
                "UNION " +
                "SELECT 'Employee' AS Role, e.employeename AS Name, l.username " +
                "FROM employee e " +
                "JOIN login l ON l.username = e.username " +
                "WHERE l.username = ? AND l.password = ?";
    
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Step 1: Retrieve the salt for the user
            String salt = null;
            try (PreparedStatement saltStmt = conn.prepareStatement(getSaltQuery)) {
                saltStmt.setString(1, username);
                try (ResultSet saltRs = saltStmt.executeQuery()) {
                    if (saltRs.next()) {
                        salt = saltRs.getString("salt");
                    } else {
                        return "Error: User not found.";
                    }
                }
            }
    
            // Step 2: Hash the provided password with the retrieved salt
            String hashedPassword = hashPassword(password, salt);
    
            // Step 3: Retrieve the user's role
            try (PreparedStatement roleStmt = conn.prepareStatement(getUserRoleQuery)) {
                roleStmt.setString(1, username);
                roleStmt.setString(2, hashedPassword);
                roleStmt.setString(3, username);
                roleStmt.setString(4, hashedPassword);
    
                try (ResultSet roleRs = roleStmt.executeQuery()) {
                    if (roleRs.next()) {
                        result = roleRs.getString("Role");
                    }
                }
            }
        } catch (SQLException e) {
            result = "Error: Database issue.";
            e.printStackTrace(); // Log the error securely
        }
    
        return result;
    }

    public boolean registerUser(String name, String username, String password, String contactNumber) {
        String loginSql = "INSERT INTO Login (username, password, salt) VALUES (?, ?, ?)";
        String customerSql = "INSERT INTO Customer (customername, username) VALUES (?, ?)";
        String contactNumberSql = "INSERT INTO contact_number (contact, CustomerID) VALUES (?, (SELECT CustomerID FROM customer WHERE username = ?))";
    
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Start a transaction
            connection.setAutoCommit(false);
    
            try (PreparedStatement loginStatement = connection.prepareStatement(loginSql);
                 PreparedStatement customerStatement = connection.prepareStatement(customerSql);
                 PreparedStatement contactStatement = connection.prepareStatement(contactNumberSql)) {
    
                // Step 1: Generate a unique salt for this user
                String salt = generateSalt(); // Create a method to generate a secure salt
    
                // Step 2: Hash the password with the salt
                String hashedPassword = hashPassword(password, salt);
    
                // Step 3: Set parameters for the login table
                loginStatement.setString(1, username);
                loginStatement.setString(2, hashedPassword);
                loginStatement.setString(3, salt);
    
                // Execute the login insert
                int loginRowsInserted = loginStatement.executeUpdate();
    
                // Step 4: Set parameters for the customer table
                customerStatement.setString(1, name);
                customerStatement.setString(2, username);
    
                // Execute the customer insert
                int customerRowsInserted = customerStatement.executeUpdate();
    
                // Step 5: Set parameters for the contact number table
                contactStatement.setString(1, contactNumber);
                contactStatement.setString(2, username);
    
                // Execute the contact number insert
                int contactRowsInserted = contactStatement.executeUpdate();
    
                // Commit transaction if all inserts are successful
                if (loginRowsInserted > 0 && customerRowsInserted > 0 && contactRowsInserted > 0) {
                    connection.commit();
                    System.out.println(
                            "Sign-up successful! You are registered as a Customer. Redirecting to the Customer screen...");
                    return true;
                } else {
                    connection.rollback();
                    System.out.println("Sign-up failed. Please try again.");
                    return false;
                }
    
            } catch (SQLIntegrityConstraintViolationException e) {
                // Handle duplicate entries (e.g., username or contact number already exists)
                connection.rollback();
                System.out.println("Error: Duplicate entry. The username or contact number may already exist.");
                return false;
            } catch (Exception e) {
                // Rollback in case of any exception
                connection.rollback();
                e.printStackTrace();
                System.out.println("Error occurred: " + e.getMessage());
                return false;
            }
    
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred: " + e.getMessage());
            return false;
        }
    }  

    public void insertOrder(int customerID, ObservableList<Cart> tableItems, double totalAmount) {
        String orderQuery = "INSERT INTO ord_er (CustomerID, amount, orderDate, status) VALUES (?, ?, CURDATE(), 'Pending')";
        String orderDetailsQuery = "INSERT INTO OrderDetails (orderID, productID, quantity) VALUES (?, ?, ?)";
        String updateProductStockQuery = "UPDATE product SET StockQuantity = StockQuantity - ? WHERE productID = ?";
    
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            PreparedStatement orderStmt = null;
            PreparedStatement detailsStmt = null;
            PreparedStatement updateProductStmt = null;
    
            try {
                // Insert into ord_er (Order table)
                orderStmt = connection.prepareStatement(orderQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                orderStmt.setInt(1, customerID);
                orderStmt.setDouble(2, totalAmount);
                int rowsAffected = orderStmt.executeUpdate();
    
                if (rowsAffected == 0) {
                    throw new SQLException("Inserting order failed, no rows affected.");
                }
    
                // Get generated orderID
                ResultSet generatedKeys = orderStmt.getGeneratedKeys();
                int orderID = 0;
                if (generatedKeys.next()) {
                    orderID = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve orderID.");
                }
    
                // Insert into OrderDetails (OrderDetails table)
                detailsStmt = connection.prepareStatement(orderDetailsQuery);
                for (Cart item : tableItems) {
                    detailsStmt.setInt(1, orderID);
                    detailsStmt.setInt(2, item.getProductID());
                    detailsStmt.setInt(3, item.getQuantity());
                    detailsStmt.addBatch();
                    
                    // Update stock quantity of the product
                    updateProductStmt = connection.prepareStatement(updateProductStockQuery);
                    updateProductStmt.setInt(1, item.getQuantity());
                    updateProductStmt.setInt(2, item.getProductID());
                    updateProductStmt.executeUpdate(); // Decrease the stock quantity
                }
    
                // Execute batch insert for order details
                int[] batchResults = detailsStmt.executeBatch();
                System.out.println("Batch insert complete. Rows inserted: " + batchResults.length);
    
                System.out.println("Order and order details inserted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Close all resources
                if (orderStmt != null) orderStmt.close();
                if (detailsStmt != null) detailsStmt.close();
                if (updateProductStmt != null) updateProductStmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error occurred: " + e.getMessage());
        }
    }

    public int getCustomerIdFromUsername(String username) {
        String query = "SELECT CustomerID FROM customer WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            PreparedStatement stmt = null;
            ResultSet rs = null;
            int customerId = -1; // Default value if not found

            try {
                stmt = connection.prepareStatement(query);
                stmt.setString(1, username);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    customerId = rs.getInt("CustomerID");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null)
                        rs.close();
                    if (stmt != null)
                        stmt.close();
                    if (connection != null)
                        connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return customerId;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred: " + e.getMessage());
            return 0;
        }
    }

    public List<ObservableList<PurchaseHistory>> selectOrderDetails(int customerID) {
        String selectQuery = "SELECT o.orderID, (p.Price * d.quantity) AS amount, o.orderDate, o.status, " +
                "d.productID, p.productName, p.Price, d.quantity " +
                "FROM ord_er o " +
                "JOIN orderdetails d ON o.orderID = d.orderID " +
                "JOIN product p ON d.productID = p.productID " +
                "WHERE o.CustomerID = ?";

        // Use a Map to group items by orderID
        Map<Integer, ObservableList<PurchaseHistory>> groupedMap = new HashMap<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement statement = connection.prepareStatement(selectQuery)) {

            // Set parameter for the query
            statement.setInt(1, customerID);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int orderID = resultSet.getInt("orderID");
                PurchaseHistory purchaseHistory = new PurchaseHistory(
                        orderID,
                        resultSet.getString("productName"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("Price"),
                        resultSet.getDouble("amount"),
                        resultSet.getDate("orderDate"),
                        resultSet.getString("status"));

                // Add the PurchaseHistory object to the corresponding group
                groupedMap
                        .computeIfAbsent(orderID, k -> FXCollections.observableArrayList())
                        .add(purchaseHistory);
            }

            if (groupedMap.isEmpty()) {
                System.out.println("No order found with Customer ID: " + customerID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while retrieving the order details.");
        }

        // Convert the grouped Map values to a List of ObservableLists
        return new ArrayList<>(groupedMap.values());
    }

    public double getOrderAmount(int orderID) {
        String query = "SELECT amount FROM ord_er WHERE orderID = ?";

        double amount = 0.0;

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement statement = connection.prepareStatement(query)) {

            // Set the orderID parameter
            statement.setInt(1, orderID);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Retrieve the amount
            if (resultSet.next()) {
                amount = resultSet.getDouble("amount");
            } else {
                System.out.println("Order ID not found: " + orderID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while retrieving the order amount.");
        }

        return amount;
    }

    public boolean updateCustomerInfo(
        int customerID,
        String name,
        String email,
        String contactNumber,
        String oldPassword,
        String newPassword) {

    String fetchSaltQuery = "SELECT l.salt, l.password FROM login l " +
            "JOIN customer c ON l.username = c.username " +
            "WHERE c.CustomerID = ?";

    String updateQuery = "UPDATE customer c " +
            "JOIN login l ON c.username = l.username " +
            "JOIN contact_number cn ON c.CustomerID = cn.CustomerID " +
            "SET c.customername = ?, " +
            "    c.email = ?, " +
            "    cn.contact = ?, " +
            "    l.password = IF(? IS NULL OR ? = '', l.password, ?) " +
            "WHERE c.CustomerID = ?";

    try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
        connection.setAutoCommit(false); // Start transaction

        // Step 1: Fetch the salt and hashed password
        String currentSalt = null;
        String currentHashedPassword = null;
        try (PreparedStatement fetchStatement = connection.prepareStatement(fetchSaltQuery)) {
            fetchStatement.setInt(1, customerID);

            try (ResultSet rs = fetchStatement.executeQuery()) {
                if (rs.next()) {
                    currentSalt = rs.getString("salt");
                    currentHashedPassword = rs.getString("password");
                } else {
                    System.out.println("Customer not found.");
                    return false;
                }
            }
        }

        // Step 2: Validate the old password
        if (currentSalt == null || currentHashedPassword == null) {
            System.out.println("Failed to retrieve salt or password.");
            return false;
        }

        String hashedOldPassword = hashPassword(oldPassword, currentSalt);
        if (!currentHashedPassword.equals(hashedOldPassword)) {
            System.out.println("Old password is incorrect.");
            return false;
        }

        // Step 3: Hash the new password (if provided)
        String hashedNewPassword = null;
        String newSalt = null;
        if (newPassword != null && !newPassword.isEmpty()) {
            newSalt = generateSalt(); // Generate a new salt for the new password
            hashedNewPassword = hashPassword(newPassword, newSalt);
        }

        // Step 4: Update customer details
        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, name); // New customer name
            updateStatement.setString(2, email); // New email
            updateStatement.setString(3, contactNumber); // New contact number
            updateStatement.setString(4, hashedNewPassword); // New hashed password
            updateStatement.setString(5, hashedNewPassword); // New hashed password (comparison for IF)
            updateStatement.setString(6, hashedNewPassword); // New hashed password (actual update)
            updateStatement.setInt(7, customerID); // Customer ID for targeting

            int rowsUpdated = updateStatement.executeUpdate();
            if (rowsUpdated > 0) {
                // Commit transaction
                connection.commit();
                System.out.println("Customer information updated successfully.");
                return true;
            } else {
                connection.rollback(); // Rollback if no rows updated
                System.out.println("Failed to update customer information.");
                return false;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("An error occurred while updating customer information.");
        return false;
    }
}

    public Customer getCustomerInfo(int customerID) {
        String query = "SELECT c.customername, c.email, cn.contact " +
                "FROM customer c " +
                "JOIN contact_number cn ON c.CustomerID = cn.CustomerID " +
                "WHERE c.CustomerID = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement statement = connection.prepareStatement(query)) {

            // Set the CustomerID parameter
            statement.setInt(1, customerID);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("customername");
                String email = resultSet.getString("email");
                String contact = resultSet.getString("contact");

                // Return a Customer object with the retrieved information
                return new Customer(name, email, contact);
            } else {
                System.out.println("No customer found with CustomerID: " + customerID);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while retrieving customer information.");
            return null;
        }
    }

    public boolean checkLoginCredentials(String username, String enteredPassword) {
        String query = "SELECT password, salt FROM login WHERE username = ?";
    
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            // Set the parameter for the query (username)
            statement.setString(1, username);
    
            // Execute the query
            ResultSet resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                // Retrieve the stored password and salt from the result set
                String storedHashedPassword = resultSet.getString("password");
                String storedSalt = resultSet.getString("salt");
    
                // Hash the entered password using the stored salt
                String hashedEnteredPassword = hashPassword(enteredPassword, storedSalt);
    
                // Compare the hashed entered password with the stored hashed password
                return storedHashedPassword.equals(hashedEnteredPassword);
            } else {
                // If no user was found with the given username
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while checking the login credentials.");
            return false;
        }
    }
    
    public ObservableList<EmployeeOrders> LoadOrders() {
        ObservableList<EmployeeOrders> result = FXCollections.observableArrayList();

        String query = "SELECT o.orderID AS order_number, c.customername, cn.contact, e.email, o.amount AS total_amount, o.orderDate, o.status "
                +
                "FROM ord_er o " +
                "JOIN customer c ON o.CustomerID = c.CustomerID " +
                "JOIN contact_number cn ON c.CustomerID = cn.CustomerID " +
                "LEFT JOIN Employee e ON c.username = e.username " +
                "WHERE o.status = 'Pending' ";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int orderNumber = resultSet.getInt("order_number");
                String customerName = resultSet.getString("customername");
                String contactNumber = resultSet.getString("contact");
                String email = resultSet.getString("email");
                double totalAmount = resultSet.getDouble("total_amount");
                Date orderDate = resultSet.getDate("orderDate");
                String status = resultSet.getString("status");

                EmployeeOrders employeeOrders = new EmployeeOrders(orderNumber, customerName, contactNumber, email,
                        totalAmount, orderDate, status);
                result.add(employeeOrders);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ObservableList<EmployeeOrderDetails> fetchOrderDetails(int orderID) {
        ObservableList<EmployeeOrderDetails> result = FXCollections.observableArrayList();
        String query = "SELECT " +
                "o.orderID, " +
                "p.productName, " +
                "od.quantity " +
                "FROM ord_er o " +
                "JOIN OrderDetails od ON o.orderID = od.orderID " +
                "JOIN product p ON od.productID = p.productID " +
                "WHERE o.orderID = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the parameter for the query
            preparedStatement.setInt(1, orderID);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int fetchedOrderID = resultSet.getInt("orderID");
                String productName = resultSet.getString("productName");
                int quantity = resultSet.getInt("quantity");

                EmployeeOrderDetails employeeOrderDetails = new EmployeeOrderDetails(fetchedOrderID, productName,
                        quantity);
                result.add(employeeOrderDetails);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void updateOrderStatus(int orderId, String newStatus) {
        String updateQuery = "UPDATE ord_er SET status = ? WHERE orderID = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, newStatus);
            preparedStatement.setInt(2, orderId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Order status updated successfully.");
            } else {
                System.out.println("Failed to update order status. Please check the order ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Customer> getCustomerInfo() throws SQLException {
        ObservableList<Customer> result = FXCollections.observableArrayList();
        String query = "SELECT c.CustomerID, c.customername, c.email, cn.contact " +
                "FROM customer c " +
                "JOIN contact_number cn ON c.CustomerID = cn.CustomerID ";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement statement = connection.prepareStatement(query)) {

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("CustomerID"); // Retrieve CustomerID
                String name = resultSet.getString("customername");
                String email = resultSet.getString("email");
                String contact = resultSet.getString("contact");

                // Return a Customer object with the retrieved information, including CustomerID
                Customer customer = new Customer(id, name, email, contact);
                result.add(customer);
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while retrieving customer information.");
            return null;
        }

        return result;
    }

    public ObservableList<EmployeeOrders> fetchOrderStatus(int CustomerID) {
        String query = "SELECT o.orderID, o.amount, o.status " +
                "FROM ord_er o " +
                "JOIN customer c ON o.CustomerID = c.CustomerID " +
                "WHERE c.CustomerID = ?";

        // ObservableList to hold the orders
        ObservableList<EmployeeOrders> orders = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, CustomerID);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Processing the result and adding to ObservableList
            while (resultSet.next()) {
                int orderID = resultSet.getInt("orderID");
                double amount = resultSet.getDouble("amount");
                String status = resultSet.getString("status");

                // Creating a new Order object and adding it to the ObservableList
                EmployeeOrders order = new EmployeeOrders(orderID, amount, status);
                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public ObservableList<Customer> searchCustomer(String search) throws SQLException {
        ObservableList<Customer> searchResults = FXCollections.observableArrayList();
        String searchQuery = "SELECT c.CustomerID, c.customername, c.email, cn.contact " +
                "FROM customer c " +
                "LEFT JOIN contact_number cn ON c.CustomerID = cn.CustomerID " +
                "WHERE c.customername LIKE ? OR cn.contact LIKE ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement pstmt = conn.prepareStatement(searchQuery)) {

            // Set search parameters (use % for wildcard search)
            String searchPattern = "%" + search + "%";
            pstmt.setString(1, searchPattern); // Search by customer name
            pstmt.setString(2, searchPattern); // Search by contact number

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Retrieve customer attributes
                    int customerID = rs.getInt("CustomerID");
                    String customerName = rs.getString("customername");
                    String email = rs.getString("email");
                    String contact = rs.getString("contact");

                    // Create and add Customer object to the list
                    Customer customer = new Customer(customerID, customerName, email, contact);
                    searchResults.add(customer);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching customer: " + e.getMessage());
            throw e; // Re-throw exception for higher-level handling
        }
        return searchResults;
    }

    public Employee getEmployeeInfo(int employeeID) {
        String query = "SELECT e.employeename, l.username, p.phone " +
                       "FROM Employee e " +
                       "JOIN login l ON e.username = l.username " +
                       "JOIN phone_number p ON e.IDNumber = p.IDNumber " +
                       "WHERE e.IDNumber = ?";
    
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            statement.setInt(1, employeeID);
    
            ResultSet resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                String name = resultSet.getString("employeename");
                String username = resultSet.getString("username");
                String phone = resultSet.getString("phone");
    
                // Return the employee object without the password
                return new Employee(name, username, phone);
            } else {
                System.out.println("No employee found with IDNumber: " + employeeID);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while retrieving employee information.");
            return null;
        }
    }
    
    public int getEmployeeIdFromUsername(String username) {
        String query = "SELECT IDNumber FROM employee WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            PreparedStatement stmt = null;
            ResultSet rs = null;
            int employeeId = -1; // Default value if not found

            try {
                stmt = connection.prepareStatement(query);
                stmt.setString(1, username);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    employeeId = rs.getInt("IDNumber");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null)
                        rs.close();
                    if (stmt != null)
                        stmt.close();
                    if (connection != null)
                        connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return employeeId;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred: " + e.getMessage());
            return 0;
        }
    }

    public boolean registerEmployee(String name, String username, String password, String phoneNumber, String role, String email) {
        String loginSql = "INSERT INTO login (username, password, salt) VALUES (?, ?, ?)";
        String employeeSql = "INSERT INTO Employee (employeename, role, email, username) VALUES (?, ?, ?, ?)";
        String phoneSql = "INSERT INTO phone_number (phone, IDNumber) VALUES (?, (SELECT IDNumber FROM Employee WHERE username = ?))";
        
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            
            connection.setAutoCommit(false);
    
            try (PreparedStatement loginStatement = connection.prepareStatement(loginSql);
                 PreparedStatement employeeStatement = connection.prepareStatement(employeeSql);
                 PreparedStatement phoneStatement = connection.prepareStatement(phoneSql)) {
    
                // Step 1: Generate a salt and hash the password
                String salt = generateSalt(); // Generate a unique salt
                String hashedPassword = hashPassword(password, salt); // Hash password with salt
    
                // Step 2: Insert into the login table
                loginStatement.setString(1, username);
                loginStatement.setString(2, hashedPassword); // Store hashed password
                loginStatement.setString(3, salt); // Store salt
                int loginRowsInserted = loginStatement.executeUpdate();
    
                // Step 3: Insert into the employee table
                employeeStatement.setString(1, name);
                employeeStatement.setString(2, role);
                employeeStatement.setString(3, email);
                employeeStatement.setString(4, username);
                int employeeRowsInserted = employeeStatement.executeUpdate();
    
                // Step 4: Insert into the phone_number table
                phoneStatement.setString(1, phoneNumber);
                phoneStatement.setString(2, username);
                int phoneRowsInserted = phoneStatement.executeUpdate();
    
                // Step 5: Commit transaction if all inserts are successful
                if (loginRowsInserted > 0 && employeeRowsInserted > 0 && phoneRowsInserted > 0) {
                    connection.commit();
                    System.out.println("Employee registration successful!");
                    return true;
                } else {
                    connection.rollback();
                    System.out.println("Employee registration failed. Rolling back transaction.");
                    return false;
                }
    
            } catch (SQLIntegrityConstraintViolationException e) {
                System.out.println("Error: Duplicate entry. The username or phone number may already exist.");
                connection.rollback();
                return false;
            } catch (Exception e) {
                // Rollback in case of any exception
                connection.rollback();
                e.printStackTrace();
                System.out.println("Error occurred: " + e.getMessage());
                return false;
            }
    
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred: " + e.getMessage());
            return false;
        }
    }

    public boolean updateEmployeeInfo(
        int employeeID,
        String name,
        String email,
        String contactNumber,
        String oldPassword,
        String newPassword) {

    String fetchSaltQuery = "SELECT l.salt, l.password FROM login l " +
            "JOIN employee e ON l.username = e.username " +
            "WHERE e.IDNumber = ?";

    String updateQuery = "UPDATE employee e " +
            "JOIN login l ON e.username = l.username " +
            "JOIN phone_number pn ON e.IDNumber = pn.IDNumber " +
            "SET e.employeename = ?, " +
            "    e.email = ?, " +
            "    pn.phone = ?, " +
            "    l.password = IF(? IS NULL OR ? = '', l.password, ?), " +
            "    l.salt = IF(? IS NULL OR ? = '', l.salt, ?) " +
            "WHERE e.IDNumber = ?";

    try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
        connection.setAutoCommit(false); // Start transaction

        // Step 1: Fetch the salt and hashed password
        String currentSalt = null;
        String currentHashedPassword = null;
        try (PreparedStatement fetchStatement = connection.prepareStatement(fetchSaltQuery)) {
            fetchStatement.setInt(1, employeeID);

            try (ResultSet rs = fetchStatement.executeQuery()) {
                if (rs.next()) {
                    currentSalt = rs.getString("salt");
                    currentHashedPassword = rs.getString("password");
                } else {
                    System.out.println("Employee not found.");
                    return false;
                }
            }
        }

        // Step 2: Validate the old password
        if (currentSalt == null || currentHashedPassword == null) {
            System.out.println("Failed to retrieve salt or password.");
            return false;
        }

        String hashedOldPassword = hashPassword(oldPassword, currentSalt);
        if (!currentHashedPassword.equals(hashedOldPassword)) {
            System.out.println("Old password is incorrect.");
            return false;
        }

        // Step 3: Hash the new password (if provided)
        String hashedNewPassword = null;
        String newSalt = null;
        if (newPassword != null && !newPassword.isEmpty()) {
            newSalt = generateSalt(); // Generate a new salt for the new password
            hashedNewPassword = hashPassword(newPassword, newSalt);
        }

        // Step 4: Update employee details
        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, name); // New employee name
            updateStatement.setString(2, email); // New email
            updateStatement.setString(3, contactNumber); // New contact number
            updateStatement.setString(4, hashedNewPassword); // New hashed password
            updateStatement.setString(5, hashedNewPassword); // Comparison for IF
            updateStatement.setString(6, hashedNewPassword); // Actual update
            updateStatement.setString(7, newSalt);           // New salt
            updateStatement.setString(8, newSalt);           // Comparison for IF
            updateStatement.setString(9, newSalt);           // Actual update
            updateStatement.setInt(10, employeeID);          // Employee ID for targeting

            int rowsUpdated = updateStatement.executeUpdate();
            if (rowsUpdated > 0) {
                // Commit transaction
                connection.commit();
                System.out.println("Employee information updated successfully.");
                return true;
            } else {
                connection.rollback(); // Rollback if no rows updated
                System.out.println("Failed to update employee information.");
                return false;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("An error occurred while updating employee information.");
        return false;
    }
}

    public boolean isEmployeeManager(int employeeID) throws SQLException {
        String query = "SELECT role FROM Employee WHERE IDNumber = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set the employee ID parameter
            pstmt.setInt(1, employeeID);


            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");

                    return "Manager".equalsIgnoreCase(role);
                } else {
                    // Employee not found
                    System.out.println("Employee with ID " + employeeID + " not found.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking employee role: " + e.getMessage());
            throw e; // Re-throw exception for higher-level handling
        }
    }

    public double getTotalSales() {
        double totalSales = 0.0;
        String query = "SELECT SUM(amount) AS total_sales FROM ord_er";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                totalSales = rs.getDouble("total_sales");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalSales;
    }

    public int getTotalOrders() {
        int totalOrders = 0;

        String query = "SELECT COUNT(orderID) AS TotalOrders FROM ord_er";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                totalOrders = resultSet.getInt("TotalOrders");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalOrders;
    }

    public int getActiveOrders() {
        int activeOrders = 0;

        String query = "SELECT COUNT(orderID) AS ActiveOrders FROM ord_er WHERE status = 'pending'";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                activeOrders = resultSet.getInt("ActiveOrders");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return activeOrders;
    }

    public ObservableList<Product> findTopThreeFrequentProducts() {
        ObservableList<Product> topProductList = FXCollections.observableArrayList();

        String query = "SELECT p.productName, p.Price, COUNT(od.productID) AS order_count, (p.Price * COUNT(od.productID)) AS total_sales " +
                       "FROM product p " +
                       "LEFT JOIN OrderDetails od ON p.productID = od.productID " +
                       "LEFT JOIN Medical_supplies ms ON p.productID = ms.productID " +
                       "LEFT JOIN Medical_equipment me ON p.productID = me.productID " +
                       "GROUP BY p.productID, p.productName, p.Price " +
                       "ORDER BY total_sales DESC LIMIT 3";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // Process the result set and add the details to the list
            while (rs.next()) {
                String productName = rs.getString("productName");
                double price = rs.getDouble("Price");
                int orderCount = rs.getInt("order_count");
                double totalSales = rs.getDouble("total_sales");

                // Add product details to the list
                topProductList.add(new Product(productName, price, orderCount, totalSales));
            }

            // If no products found, add a message
            if (topProductList.isEmpty()) {
                System.out.println("No products ordered.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topProductList;
    }

    public ObservableList<Customer> findTopThreeFrequentCustomers() {
        ObservableList<Customer> topCustomerList = FXCollections.observableArrayList();

        String query = "SELECT c.customername, SUM(o.amount) AS total_amount " +
                       "FROM customer c " +
                       "JOIN ord_er o ON c.CustomerID = o.CustomerID " +
                       "GROUP BY c.CustomerID, c.customername " +
                       "ORDER BY total_amount DESC LIMIT 3";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // Process the result set and add the details to the list
            while (rs.next()) {
                String customerName = rs.getString("customername");
                double totalAmount = rs.getDouble("total_amount");

                // Add the customer name and total amount spent to the list
                topCustomerList.add(new Customer(customerName, totalAmount));
            }

            // If no customers found, add a message
            if (topCustomerList.isEmpty()) {
                System.out.println("No customer orders found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topCustomerList;
    }

    public ObservableList<XYChart.Data<String, Double>> getMonthlySalesChartData() {
        ObservableList<XYChart.Data<String, Double>> chartData = FXCollections.observableArrayList();

        String query = "SELECT YEAR(orderDate) AS year, MONTH(orderDate) AS month, SUM(amount) AS total_sales " +
                       "FROM ord_er " +
                       "GROUP BY YEAR(orderDate), MONTH(orderDate) " +
                       "ORDER BY year, month";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int year = rs.getInt("year");
                int month = rs.getInt("month");
                double totalSales = rs.getDouble("total_sales");

                // Format the month as "Month-Year" (e.g., "January-2024")
                String monthYear = getMonthName(month) + "-" + year;

                // Add the data point to the chart data
                chartData.add(new XYChart.Data<>(monthYear, totalSales));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chartData;
    }

    private String getMonthName(int month) {
        String[] monthNames = {
            "January", "February", "March", "April", "May", "June", 
            "July", "August", "September", "October", "November", "December"
        };
        return monthNames[month - 1];
    }

    public ObservableList<Employee> getAllEmployeeDetails() {
        ObservableList<Employee> employeeList = FXCollections.observableArrayList();

        String query = "SELECT e.IDNumber, e.employeename, e.email, e.username, c.phone " +
                       "FROM Employee e " +
                       "JOIN login l ON e.username = l.username " +
                       "JOIN phone_number c ON e.IDNumber = c.IDNumber";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // Process the result set and add employee details to the list
            while (rs.next()) {
                int id = rs.getInt("IDNumber");
                String name = rs.getString("employeename");
                String email = rs.getString("email");
                String username = rs.getString("username");
                String contact = rs.getString("phone");

                // Add employee details along with contact number to the list
                employeeList.add(new Employee(name, username, contact, email, id));
            }

            // If no employees found, add a message
            if (employeeList.isEmpty()) {
                System.out.println("No employees found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employeeList;
    }

    // Generate a random salt
    public static String generateSalt() {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16]; // 16 bytes = 128 bits
            random.nextBytes(salt);
            return Base64.getEncoder().encodeToString(salt); // Encode to make it database-friendly
        } catch (Exception e) {
            throw new RuntimeException("Error generating salt", e);
        }
    }

    // Hash the password with SHA-256
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String passwordWithSalt = password + salt; // Combine password and salt
            byte[] hashedBytes = md.digest(passwordWithSalt.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes); // Encode to make it readable
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verifyPassword(String enteredPassword, String storedHash, String storedSalt) {
        // Hash the entered password using the stored salt
        String hashedEnteredPassword = hashPassword(enteredPassword, storedSalt);
        // Compare the newly hashed password with the stored hash
        return hashedEnteredPassword.equals(storedHash);
    }
}


