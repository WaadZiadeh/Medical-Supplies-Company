import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class App extends Application {

        MySQLConnection db = new MySQLConnection();

        TableView<Equipment> EquipmentTableView = new TableView<>();
        TableView<Supply> supplyTableView = new TableView<>();
        TableView<Cart> customerCartTableView = new TableView<>();

        TableView<EmployeeOrders> EmployeeOrdersTableView = new TableView<>();
        EmployeeOrders EmployeeOrderSelected = null;

        Button dashboardButton = new Button("Dashboard");

        int historyCurrentIndex = 0;

        String userName = null;

        @Override
        public void start(Stage primaryStage) {
                try {

                        CreateLoginPage(primaryStage);
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public static void main(String[] args) {
                launch(args);
        }

        private void CreateLoginPage(Stage primaryStage) {
                // Root VBox
                VBox root = new VBox();
                root.setPrefSize(640, 400);

                // AnchorPane
                AnchorPane anchorPane = new AnchorPane();
                anchorPane.setPrefSize(662, 412);
                anchorPane.setStyle("-fx-background-color: #A2D2DF;");

                // Background Image
                ImageView backgroundImage = new ImageView(new Image("istockphoto-468784678-612x612.jpg"));
                backgroundImage.setFitWidth(655);
                backgroundImage.setFitHeight(400);
                backgroundImage.setLayoutX(-1);

                // Pane
                Pane pane = new Pane();
                pane.setPrefSize(252, 400);
                pane.setStyle("-fx-background-color: #A2D2DF; -fx-background-radius: 18;");
                AnchorPane.setLeftAnchor(pane, -10.0);

                // Username TextField
                TextField usernameField = new TextField();
                usernameField.setLayoutX(22);
                usernameField.setLayoutY(152);
                usernameField.setPrefSize(211, 38);
                usernameField.setPromptText("Username");
                usernameField.setStyle("-fx-background-radius: 20;");
                usernameField.setFont(new Font(15));

                // Password TextField
                PasswordField passwordField = new PasswordField();
                passwordField.setLayoutX(22);
                passwordField.setLayoutY(200);
                passwordField.setPrefSize(211, 38);
                passwordField.setPromptText("Password");
                passwordField.setStyle("-fx-background-radius: 20;");
                passwordField.setFont(new Font(15));

                // Login Button
                Button loginButton = new Button("Login");
                loginButton.setLayoutX(22);
                loginButton.setLayoutY(260);
                loginButton.setPrefSize(211, 38);
                loginButton.setStyle("-fx-background-radius: 20;");
                loginButton.setFont(new Font("System Bold", 16));
                // loginButton.setOnAction(e -> CreateProductsPage(primaryStage));
                loginButton.setOnAction(e -> {
                        if (db.getUserRole(usernameField.getText(), passwordField.getText())
                                        .equalsIgnoreCase("customer")) {
                                createCustomerPage(primaryStage);
                                userName = usernameField.getText();
                        } else if (db.getUserRole(usernameField.getText(), passwordField.getText())
                                        .equalsIgnoreCase("employee")) {
                                userName = usernameField.getText();
                                try {
                                        if (db.isEmployeeManager(db.getEmployeeIdFromUsername(userName))) {
                                                createDashboardPage(primaryStage);
                                        } else {
                                                dashboardButton.setVisible(false);
                                                CreateProductsPage(primaryStage);
                                        }
                                } catch (SQLException e1) {
                                        // TODO Auto-generated catch block
                                        e1.printStackTrace();
                                }
                        } else {
                                showAlert(AlertType.INFORMATION, "Didn't find the user",
                                                "Username or Password may be wrong");
                        }

                });

                // Pane ImageView
                ImageView paneImageView = new ImageView(new Image("download.png"));
                paneImageView.setFitWidth(152);
                paneImageView.setFitHeight(145);
                paneImageView.setLayoutX(51);
                paneImageView.setLayoutY(14);

                // Adding elements to Pane
                pane.getChildren().addAll(usernameField, passwordField, loginButton, paneImageView);

                // Sign Up Button
                Button signUpButton = new Button("Sign Up");
                signUpButton.setLayoutX(346);
                signUpButton.setLayoutY(22);
                signUpButton.setPrefSize(76, 38);
                signUpButton.setStyle("-fx-background-radius: 20; -fx-background-color: #A2D2DF;");
                signUpButton.setFont(new Font("System Bold", 15));
                signUpButton.setOnAction(e -> CreateSignUpPage(primaryStage));

                // Login Button (Top)
                Button loginTopButton = new Button("Login");
                loginTopButton.setLayoutX(258);
                loginTopButton.setLayoutY(22);
                loginTopButton.setPrefSize(68, 38);
                loginTopButton.setStyle("-fx-background-radius: 20; -fx-background-color: #A2D2DF;");
                loginTopButton.setFont(new Font("System Bold", 15));

                // Rotated Image
                ImageView rotatedImage = new ImageView(new Image("download (1).png"));
                rotatedImage.setFitWidth(292);
                rotatedImage.setFitHeight(187);
                rotatedImage.setLayoutX(310);
                rotatedImage.setLayoutY(107);
                rotatedImage.setRotate(13);

                // Adding elements to AnchorPane
                anchorPane.getChildren().addAll(backgroundImage, pane, signUpButton, loginTopButton, rotatedImage);

                // Adding AnchorPane to VBox
                root.getChildren().add(anchorPane);

                // Setting up the Scene
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Bethlehem Medical Supplies");
                primaryStage.show();

        }

        private void CreateSignUpPage(Stage primaryStage) {
                // Main VBox container
                VBox vBox = new VBox();
                vBox.setPrefSize(640, 400);

                // AnchorPane as child of VBox
                AnchorPane anchorPane = new AnchorPane();
                anchorPane.setPrefSize(662, 412);
                anchorPane.setStyle("-fx-background-color: #A2D2DF;");

                // Background image
                ImageView backgroundImage = new ImageView(new Image("istockphoto-468784678-612x612.jpg"));
                backgroundImage.setFitHeight(400);
                backgroundImage.setFitWidth(655);
                backgroundImage.setLayoutX(-1);

                // Pane for form elements
                Pane formPane = new Pane();
                formPane.setPrefSize(252, 400);
                formPane.setStyle("-fx-background-color: #A2D2DF; -fx-background-radius: 18;");
                AnchorPane.setLeftAnchor(formPane, -10.0);

                // Form fields
                TextField nameField = new TextField();
                nameField.setLayoutX(26);
                nameField.setLayoutY(159);
                nameField.setPrefSize(203, 31);
                nameField.setPromptText("Name");
                nameField.setStyle("-fx-background-radius: 20;");
                nameField.setFont(new Font(15));

                TextField usernameField = new TextField();
                usernameField.setLayoutX(26);
                usernameField.setLayoutY(200);
                usernameField.setPrefSize(203, 31);
                usernameField.setPromptText("Username");
                usernameField.setStyle("-fx-background-radius: 20;");
                usernameField.setFont(new Font(15));

                TextField contactField = new TextField();
                contactField.setLayoutX(26);
                contactField.setLayoutY(244);
                contactField.setPrefSize(203, 31);
                contactField.setPromptText("Contact Number");
                contactField.setStyle("-fx-background-radius: 20;");
                contactField.setFont(new Font(15));

                TextField passwordField = new TextField();
                passwordField.setLayoutX(26);
                passwordField.setLayoutY(288);
                passwordField.setPrefSize(203, 31);
                passwordField.setPromptText("Password");
                passwordField.setStyle("-fx-background-radius: 20;");
                passwordField.setFont(new Font(15));

                Button signUpButton = new Button("Sign Up");
                signUpButton.setLayoutX(22);
                signUpButton.setLayoutY(336);
                signUpButton.setPrefSize(211, 38);
                signUpButton.setStyle("-fx-background-radius: 20;");
                signUpButton.setFont(new Font("System Bold", 16));
                // signUpButton.setOnAction(e -> createCustomerPage(primaryStage));
                signUpButton.setOnAction(e -> {
                        if (db.registerUser(nameField.getText(), usernameField.getText(), passwordField.getText(),
                                        contactField.getText())) {
                                db.registerUser(nameField.getText(), usernameField.getText(), passwordField.getText(),
                                                contactField.getText());
                                showAlert(AlertType.INFORMATION, "Account Created", "Account Created");
                        } else {
                                showAlert(AlertType.ERROR, "Error", "Error");
                        }
                });

                ImageView profileImage = new ImageView(new Image("download.png"));
                profileImage.setFitHeight(145);
                profileImage.setFitWidth(152);
                profileImage.setLayoutX(51);
                profileImage.setLayoutY(14);

                // Add form elements to the form pane
                formPane.getChildren().addAll(nameField, usernameField, contactField, passwordField, signUpButton,
                                profileImage);

                // Top buttons
                Button signUpTopButton = new Button("Sign Up");
                signUpTopButton.setLayoutX(346);
                signUpTopButton.setLayoutY(22);
                signUpTopButton.setPrefSize(76, 38);
                signUpTopButton.setStyle("-fx-background-radius: 20; -fx-background-color: #A2D2DF;");
                signUpTopButton.setFont(new Font("System Bold", 15));

                Button loginButton = new Button("Login");
                loginButton.setLayoutX(258);
                loginButton.setLayoutY(22);
                loginButton.setPrefSize(68, 38);
                loginButton.setStyle("-fx-background-radius: 20; -fx-background-color: #A2D2DF;");
                loginButton.setFont(new Font("System Bold", 15));
                loginButton.setOnAction(event -> CreateLoginPage(primaryStage));

                // Rotated image
                ImageView rotatedImage = new ImageView(new Image("download (1).png"));
                rotatedImage.setFitHeight(187);
                rotatedImage.setFitWidth(292);
                rotatedImage.setLayoutX(310);
                rotatedImage.setLayoutY(107);
                rotatedImage.setRotate(13);

                // Add all elements to the anchor pane
                anchorPane.getChildren().addAll(backgroundImage, formPane, signUpTopButton, loginButton, rotatedImage);

                // Add anchor pane to the VBox
                vBox.getChildren().add(anchorPane);

                // Set up the scene
                Scene scene = new Scene(vBox);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Bethlehem Medical Supplies");
                primaryStage.show();
        }

        private void CreateProductsPage(Stage primaryStage) {
                try {
                        if (db.isEmployeeManager(db.getEmployeeIdFromUsername(userName))) {

                        } else {
                                dashboardButton.setVisible(false);
                        }
                } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                // Main Pane
                Pane mainPane = new Pane();
                mainPane.setPrefSize(811, 497);
                mainPane.setStyle("-fx-background-color: D4EBF8;");

                // Top Pane
                Pane topPane = new Pane();
                topPane.setLayoutX(11);
                topPane.setLayoutY(14);
                topPane.setPrefSize(790, 48);
                topPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Adding children to Top Pane
                ImageView logo = new ImageView(new Image("downloadsmallLogo.png"));
                logo.setLayoutX(30);
                topPane.getChildren().add(logo);

                // Buttons in Header Pane
                dashboardButton.setLayoutX(136);
                dashboardButton.setLayoutY(1);
                dashboardButton.setPrefSize(78, 48);
                dashboardButton.setStyle("-fx-background-color: transparent;");
                dashboardButton.setFont(new Font("System Bold", 12));
                dashboardButton.setOnAction(e -> createDashboardPage(primaryStage));
                addTopButtonsEffect(dashboardButton);

                Button productsButton = new Button("Products");
                productsButton.setLayoutX(289);
                productsButton.setPrefSize(66, 48);
                productsButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;");
                productsButton.setFont(new Font("System Bold", 12));
                productsButton.setOnAction(e -> CreateProductsPage(primaryStage));
                productsButton.setOnMouseEntered(e -> productsButton.setStyle(
                                "-fx-background-color: F2F9FF; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));
                productsButton.setOnMouseExited(e -> productsButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));

                Button ordersButton = new Button("Orders");
                ordersButton.setLayoutX(419);
                ordersButton.setPrefSize(76, 48);
                ordersButton.setStyle("-fx-background-color: transparent;");
                ordersButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(ordersButton);
                ordersButton.setOnAction(e -> createEmployeeOrdersPage(primaryStage));

                Button customersButton = new Button("Customers");
                customersButton.setLayoutX(547);
                customersButton.setPrefSize(76, 48);
                customersButton.setStyle("-fx-background-color: transparent;");
                customersButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(customersButton);
                customersButton.setOnAction(e -> createEmployeeCustomersPage(primaryStage));

                Button settingsButton = new Button("Settings");
                settingsButton.setLayoutX(679);
                settingsButton.setLayoutY(1);
                settingsButton.setPrefSize(66, 48);
                settingsButton.setStyle("-fx-background-color: transparent;");
                settingsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(settingsButton);
                settingsButton.setOnAction(e -> createEmployeeSettingsPage(primaryStage));

                // Add all buttons to the top pane
                topPane.getChildren().addAll(dashboardButton, productsButton, ordersButton, customersButton,
                                settingsButton);

                mainPane.getChildren().add(topPane);

                // Bottom Pane
                Pane bottomPane = new Pane();
                bottomPane.setLayoutX(11);
                bottomPane.setLayoutY(69);
                bottomPane.setPrefSize(790, 416);
                bottomPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Button 1
                Button button1 = new Button("Medical Supplies And Consumables");
                button1.setLayoutX(47);
                button1.setLayoutY(103);
                button1.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 2 2 2 2; -fx-border-color: black; -fx-border-radius: 20;");
                button1.setFont(new Font("System Bold", 12));
                button1.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);
                ImageView image1 = new ImageView(new Image("1681454275602-768x512.jpeg"));
                image1.setFitHeight(177);
                image1.setFitWidth(310);
                button1.setGraphic(image1);
                addButtonEffect(button1);

                button1.setOnAction(e -> {
                        try {
                                createSuppliesPage(primaryStage);
                        } catch (SQLException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                        }
                });

                // Button 2
                Button button2 = new Button("Medical Equipments And Devices");
                button2.setLayoutX(395);
                button2.setLayoutY(103);
                button2.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 2 2 2 2; -fx-border-color: black; -fx-border-radius: 20;");
                button2.setFont(new Font("System Bold", 12));
                button2.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);
                ImageView image2 = new ImageView(new Image(
                                "equipment-medical-devices-modern-operating-room-operating-theatre-selective-focus_1028938-62298.jpg"));
                image2.setFitHeight(177);
                image2.setFitWidth(310);
                button2.setGraphic(image2);
                addButtonEffect(button2);

                button2.setOnAction(e -> {
                        try {
                                createEquipmentPage(primaryStage);
                        } catch (SQLException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                        }
                });

                bottomPane.getChildren().addAll(button1, button2);
                mainPane.getChildren().add(bottomPane);

                // Scene and Stage
                Scene scene = new Scene(mainPane);
                primaryStage.setScene(scene);
                primaryStage.setTitle("JavaFX Layout");
                primaryStage.show();
        }

        private void createSuppliesPage(Stage primaryStage) throws SQLException {
                Pane mainPane = new Pane();
                mainPane.setPrefSize(811, 497);
                mainPane.setStyle("-fx-background-color: D4EBF8;");

                // Top Pane
                Pane topPane = new Pane();
                topPane.setLayoutX(11);
                topPane.setLayoutY(14);
                topPane.setPrefSize(790, 48);
                topPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Adding children to Top Pane
                ImageView logo = new ImageView(new Image("downloadsmallLogo.png"));
                logo.setLayoutX(30);
                topPane.getChildren().add(logo);

                // Buttons in Header Pane
                dashboardButton.setLayoutX(136);
                dashboardButton.setLayoutY(1);
                dashboardButton.setPrefSize(78, 48);
                dashboardButton.setStyle("-fx-background-color: transparent;");
                dashboardButton.setFont(new Font("System Bold", 12));
                dashboardButton.setOnAction(e -> createDashboardPage(primaryStage));
                addTopButtonsEffect(dashboardButton);

                Button productsButton = new Button("Products");
                productsButton.setLayoutX(289);
                productsButton.setPrefSize(66, 48);
                productsButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;");
                productsButton.setFont(new Font("System Bold", 12));
                productsButton.setOnAction(e -> CreateProductsPage(primaryStage));
                productsButton.setOnMouseEntered(e -> productsButton.setStyle(
                                "-fx-background-color: F2F9FF; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));
                productsButton.setOnMouseExited(e -> productsButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));

                Button ordersButton = new Button("Orders");
                ordersButton.setLayoutX(419);
                ordersButton.setPrefSize(76, 48);
                ordersButton.setStyle("-fx-background-color: transparent;");
                ordersButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(ordersButton);
                ordersButton.setOnAction(e -> createEmployeeOrdersPage(primaryStage));

                Button customersButton = new Button("Customers");
                customersButton.setLayoutX(547);
                customersButton.setPrefSize(76, 48);
                customersButton.setStyle("-fx-background-color: transparent;");
                customersButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(customersButton);
                customersButton.setOnAction(e -> createEmployeeCustomersPage(primaryStage));

                Button settingsButton = new Button("Settings");
                settingsButton.setLayoutX(679);
                settingsButton.setLayoutY(1);
                settingsButton.setPrefSize(66, 48);
                settingsButton.setStyle("-fx-background-color: transparent;");
                settingsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(settingsButton);
                settingsButton.setOnAction(e -> createEmployeeSettingsPage(primaryStage));

                // Add all buttons to the top pane
                topPane.getChildren().addAll(dashboardButton, productsButton, ordersButton, customersButton,
                                settingsButton);

                mainPane.getChildren().add(topPane);

                // Bottom Pane
                Pane bottomPane = new Pane();
                bottomPane.setLayoutX(11);
                bottomPane.setLayoutY(69);
                bottomPane.setPrefSize(790, 416);
                bottomPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Search Pane
                Pane searchPane = new Pane();
                searchPane.setLayoutX(396);
                searchPane.setLayoutY(17);
                searchPane.setPrefSize(195, 30);
                searchPane.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;");

                ImageView searchIcon = new ImageView(new Image("searchsmall.png"));
                searchIcon.setFitHeight(20);
                searchIcon.setFitWidth(20);
                searchIcon.setLayoutX(4);
                searchIcon.setLayoutY(6);

                TextField searchField = new TextField();
                searchField.setLayoutX(24);
                searchField.setLayoutY(3);
                searchField.setPrefSize(166, 24);
                searchField.setPromptText("Search Product...");
                searchField.setStyle("-fx-background-color: transparent;");
                searchField.setOnKeyPressed(e -> {
                        if (e.getCode() == KeyCode.ENTER) {
                                try {
                                        supplyTableView.setItems(db.searchSupply(searchField.getText()));
                                } catch (SQLException e1) {
                                        e1.printStackTrace();
                                }
                        }
                });

                searchPane.getChildren().addAll(searchIcon, searchField);

                // Add New Product Button
                Button addProductButton = new Button("Add New Product +");
                addProductButton.setLayoutX(631);
                addProductButton.setLayoutY(17);
                addProductButton.setPrefSize(135, 30);
                addProductButton.setFont(new Font("System Bold", 12));
                addProductButton.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;");
                addProductButton.setOnAction(e -> createAddSupplyForm());
                addButtonEffect(addProductButton);

                // Section Separator
                Label separator = new Label("|");
                separator.setLayoutX(609);
                separator.setLayoutY(15);
                separator.setPrefSize(3, 30);
                separator.setFont(new Font(23));

                // Section Title
                Label sectionTitle = new Label("Medical Supplies And Consumables");
                sectionTitle.setLayoutX(16);
                sectionTitle.setLayoutY(18);
                sectionTitle.setFont(new Font("System Bold", 19));

                // TableView
                supplyTableView.setLayoutX(16);
                supplyTableView.setLayoutY(89);
                supplyTableView.setPrefSize(761, 239);

                // Define columns
                TableColumn<Supply, String> idColumn = new TableColumn<>("ID");
                idColumn.setCellValueFactory(new PropertyValueFactory<>("ProductID"));
                idColumn.setPrefWidth(75);

                TableColumn<Supply, String> nameColumn = new TableColumn<>("Name");
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("ProductName"));
                nameColumn.setPrefWidth(75);

                TableColumn<Supply, String> priceColumn = new TableColumn<>("Price");
                priceColumn.setCellValueFactory(new PropertyValueFactory<>("Price"));
                priceColumn.setPrefWidth(75);

                TableColumn<Supply, String> quantityColumn = new TableColumn<>("Quantity");
                quantityColumn.setCellValueFactory(new PropertyValueFactory<>("StockQuantity"));
                quantityColumn.setPrefWidth(75);

                TableColumn<Supply, String> companyColumn = new TableColumn<>("Company");
                companyColumn.setCellValueFactory(new PropertyValueFactory<>("ProductionCompany"));
                companyColumn.setPrefWidth(86.857);

                TableColumn<Supply, String> expirationDateColumn = new TableColumn<>("Expiration Date");
                expirationDateColumn.setCellValueFactory(new PropertyValueFactory<>("ExpirationDate"));
                expirationDateColumn.setPrefWidth(101.486);

                TableColumn<Supply, String> usageInstructionsColumn = new TableColumn<>("Usage Instructions");
                usageInstructionsColumn.setCellValueFactory(new PropertyValueFactory<>("UsageInstructions"));
                usageInstructionsColumn.setPrefWidth(116.114);

                TableColumn<Supply, String> storageRequirementsColumn = new TableColumn<>("Storage Requirements");
                storageRequirementsColumn.setCellValueFactory(new PropertyValueFactory<>("StorageRequirements"));
                storageRequirementsColumn.setPrefWidth(149.943);

                // Add columns to TableView
                supplyTableView.getColumns().addAll(idColumn, nameColumn, priceColumn, quantityColumn, companyColumn,
                                expirationDateColumn, usageInstructionsColumn, storageRequirementsColumn);

                supplyTableView.setItems(db.getSupplies());

                // Action Buttons
                Button deleteProductButton = new Button("Delete Product");
                deleteProductButton.setLayoutX(243);
                deleteProductButton.setLayoutY(356);
                deleteProductButton.setPrefSize(135, 30);
                deleteProductButton.setFont(new Font("System Bold", 12));
                deleteProductButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;");
                deleteProductButton.setOnAction(e -> {
                        Supply selectedSupply = supplyTableView.getSelectionModel().getSelectedItem();
                        if (selectedSupply != null) {
                                db.deleteProduct(selectedSupply.getProductID());
                                try {
                                        supplyTableView.setItems(db.getSupplies());
                                } catch (SQLException e1) {
                                        e1.printStackTrace();
                                }
                        } else {
                                showAlert(AlertType.WARNING, "No Selection", "Please select a supply to delete.");
                        }
                });
                addButtonEffect(deleteProductButton);

                Button updateProductButton = new Button("Update Product");
                updateProductButton.setLayoutX(426);
                updateProductButton.setLayoutY(356);
                updateProductButton.setPrefSize(135, 30);
                updateProductButton.setFont(new Font("System Bold", 12));
                updateProductButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;");
                updateProductButton.setOnAction(e -> createUpdateSupplyForm());
                addButtonEffect(updateProductButton);

                // Add children to Bottom Pane
                bottomPane.getChildren().addAll(searchPane, separator, addProductButton, sectionTitle, supplyTableView,
                                deleteProductButton, updateProductButton);

                // Add Bottom Pane to Main Pane
                mainPane.getChildren().add(bottomPane);

                // Scene and Stage
                Scene scene = new Scene(mainPane);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Product Management");
                primaryStage.show();
        }

        private void createEquipmentPage(Stage primaryStage) throws SQLException {
                Pane root = new Pane();
                root.setPrefSize(811, 497);
                root.setStyle("-fx-background-color: D4EBF8;");

                // Top Pane
                Pane topPane = new Pane();
                topPane.setLayoutX(11);
                topPane.setLayoutY(14);
                topPane.setPrefSize(790, 48);
                topPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Adding children to Top Pane
                ImageView logo = new ImageView(new Image("downloadsmallLogo.png"));
                logo.setLayoutX(30);
                topPane.getChildren().add(logo);

                // Buttons in Header Pane
                dashboardButton.setLayoutX(136);
                dashboardButton.setLayoutY(1);
                dashboardButton.setPrefSize(78, 48);
                dashboardButton.setStyle("-fx-background-color: transparent;");
                dashboardButton.setFont(new Font("System Bold", 12));
                dashboardButton.setOnAction(e -> createDashboardPage(primaryStage));
                addTopButtonsEffect(dashboardButton);

                Button productsButton = new Button("Products");
                productsButton.setLayoutX(289);
                productsButton.setPrefSize(66, 48);
                productsButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;");
                productsButton.setFont(new Font("System Bold", 12));
                productsButton.setOnAction(e -> CreateProductsPage(primaryStage));
                productsButton.setOnMouseEntered(e -> productsButton.setStyle(
                                "-fx-background-color: F2F9FF; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));
                productsButton.setOnMouseExited(e -> productsButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));

                Button ordersButton = new Button("Orders");
                ordersButton.setLayoutX(419);
                ordersButton.setPrefSize(76, 48);
                ordersButton.setStyle("-fx-background-color: transparent;");
                ordersButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(ordersButton);
                ordersButton.setOnAction(e -> createEmployeeOrdersPage(primaryStage));

                Button customersButton = new Button("Customers");
                customersButton.setLayoutX(547);
                customersButton.setPrefSize(76, 48);
                customersButton.setStyle("-fx-background-color: transparent;");
                customersButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(customersButton);
                customersButton.setOnAction(e -> createEmployeeCustomersPage(primaryStage));

                Button settingsButton = new Button("Settings");
                settingsButton.setLayoutX(679);
                settingsButton.setLayoutY(1);
                settingsButton.setPrefSize(66, 48);
                settingsButton.setStyle("-fx-background-color: transparent;");
                settingsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(settingsButton);
                settingsButton.setOnAction(e -> createEmployeeSettingsPage(primaryStage));

                // Add all buttons to the top pane
                topPane.getChildren().addAll(dashboardButton, productsButton, ordersButton, customersButton,
                                settingsButton);

                // Main Content Pane
                Pane contentPane = new Pane();
                contentPane.setLayoutX(11);
                contentPane.setLayoutY(69);
                contentPane.setPrefSize(790, 416);
                contentPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                Pane searchPane = new Pane();
                searchPane.setLayoutX(396);
                searchPane.setLayoutY(17);
                searchPane.setPrefSize(195, 30);
                searchPane.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;");

                ImageView searchIcon = new ImageView(new Image("searchsmall.png"));
                searchIcon.setFitWidth(20);
                searchIcon.setFitHeight(20);
                searchIcon.setLayoutX(4);
                searchIcon.setLayoutY(6);

                TextField searchField = new TextField();
                searchField.setPromptText("Search Product...");
                searchField.setStyle("-fx-background-color: transparent;");
                searchField.setPrefSize(166, 24);
                searchField.setLayoutX(24);
                searchField.setLayoutY(3);
                searchField.setOnKeyPressed(e -> {
                        if (e.getCode() == KeyCode.ENTER) {
                                try {
                                        EquipmentTableView.setItems(db.searchEquipment(searchField.getText()));
                                } catch (SQLException e1) {
                                        e1.printStackTrace();
                                }
                        }
                });

                searchPane.getChildren().addAll(searchIcon, searchField);

                Label separator = new Label("|");
                separator.setFont(new Font(23));
                separator.setLayoutX(609);
                separator.setLayoutY(15);

                Button addProductButton = new Button("Add New Product +");
                addProductButton.setLayoutX(631);
                addProductButton.setLayoutY(17);
                addProductButton.setPrefSize(135, 30);
                addProductButton.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;");
                addProductButton.setFont(new Font("System Bold", 12));
                addProductButton.setOnAction(e -> createAddEquipmentForm());
                addButtonEffect(addProductButton);

                Label titleLabel = new Label("Medical Equipment And Devices");
                titleLabel.setFont(new Font("System Bold", 19));
                titleLabel.setLayoutX(16);
                titleLabel.setLayoutY(18);

                EquipmentTableView.setLayoutX(16);
                EquipmentTableView.setLayoutY(89);
                EquipmentTableView.setPrefSize(761, 239);

                // ID Column
                TableColumn<Equipment, String> idColumn = new TableColumn<>("ID");
                idColumn.setPrefWidth(50);
                idColumn.setCellValueFactory(new PropertyValueFactory<>("ProductID"));

                // Name Column
                TableColumn<Equipment, String> nameColumn = new TableColumn<>("Name");
                nameColumn.setPrefWidth(90);
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("ProductName"));

                // Price Column
                TableColumn<Equipment, String> priceColumn = new TableColumn<>("Price");
                priceColumn.setPrefWidth(50);
                priceColumn.setCellValueFactory(new PropertyValueFactory<>("Price"));

                // Quantity Column
                TableColumn<Equipment, String> quantityColumn = new TableColumn<>("Quantity");
                quantityColumn.setPrefWidth(50);
                quantityColumn.setCellValueFactory(new PropertyValueFactory<>("StockQuantity"));

                // Company Column
                TableColumn<Equipment, String> companyColumn = new TableColumn<>("Company");
                companyColumn.setPrefWidth(120);
                companyColumn.setCellValueFactory(new PropertyValueFactory<>("ProductionCompany"));

                // Warranty Period Column
                TableColumn<Equipment, String> warrantyColumn = new TableColumn<>("Warranty Period");
                warrantyColumn.setPrefWidth(110);
                warrantyColumn.setCellValueFactory(new PropertyValueFactory<>("WarrantyPeriod"));

                // Power Source Column
                TableColumn<Equipment, String> powerSourceColumn = new TableColumn<>("Power Source");
                powerSourceColumn.setPrefWidth(80);
                powerSourceColumn.setCellValueFactory(new PropertyValueFactory<>("PowerSource"));

                // Certification Column
                TableColumn<Equipment, String> certificationColumn = new TableColumn<>("Certification");
                certificationColumn.setPrefWidth(80);
                certificationColumn.setCellValueFactory(new PropertyValueFactory<>("Certification"));

                // Expected Lifespan Column
                TableColumn<Equipment, String> lifespanColumn = new TableColumn<>("Expected Lifespan");
                lifespanColumn.setPrefWidth(130);
                lifespanColumn.setCellValueFactory(new PropertyValueFactory<>("ExpectedLifespanl"));

                // Add columns to the TableView
                EquipmentTableView.getColumns().addAll(
                                idColumn, nameColumn, priceColumn, quantityColumn, companyColumn,
                                warrantyColumn, powerSourceColumn, certificationColumn, lifespanColumn);

                EquipmentTableView.setItems(db.getEquipmentList());

                Button deleteButton = new Button("Delete Product");
                deleteButton.setLayoutX(243);
                deleteButton.setLayoutY(356);
                deleteButton.setPrefSize(135, 30);
                deleteButton.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;");
                deleteButton.setFont(new Font("System Bold", 12));
                addButtonEffect(deleteButton);
                deleteButton.setOnAction(e -> {
                        Equipment selectedEquipment = EquipmentTableView.getSelectionModel().getSelectedItem();
                        if (selectedEquipment != null) {
                                db.deleteProduct(selectedEquipment.getProductID());
                                try {
                                        EquipmentTableView.setItems(db.getEquipmentList());
                                } catch (SQLException e1) {
                                        e1.printStackTrace();
                                }
                        } else {
                                showAlert(AlertType.WARNING, "No Selection", "Please select a equipment to delete.");
                        }

                });

                Button updateButton = new Button("Update Product");
                updateButton.setLayoutX(426);
                updateButton.setLayoutY(356);
                updateButton.setPrefSize(135, 30);
                updateButton.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;");
                updateButton.setFont(new Font("System Bold", 12));
                addButtonEffect(updateButton);
                updateButton.setOnAction(e -> {
                        try {
                                createUpdateEquipmentForm();
                        } catch (SQLException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                        }
                });

                contentPane.getChildren().addAll(searchPane, separator, addProductButton, titleLabel,
                                EquipmentTableView,
                                deleteButton,
                                updateButton);

                root.getChildren().addAll(topPane, contentPane);

                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Medical Equipment Management");
                primaryStage.show();
        }

        private void createAddEquipmentForm() {
                // Main Pane
                Stage stage = new Stage();
                Pane mainPane = new Pane();
                mainPane.setPrefSize(396, 533);
                mainPane.setStyle("-fx-background-color: D4EBF8;");

                // Inner Pane
                Pane innerPane = new Pane();
                innerPane.setLayoutX(13);
                innerPane.setLayoutY(14);
                innerPane.setPrefSize(371, 506);
                innerPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Title Label
                Label titleLabel = new Label("Add Equipment");
                titleLabel.setLayoutX(116);
                titleLabel.setLayoutY(14);
                titleLabel.setFont(new Font("System Bold", 20));

                // Labels and TextFields
                Label productIdLabel = new Label("Product ID");
                productIdLabel.setLayoutX(27);
                productIdLabel.setLayoutY(71);
                productIdLabel.setFont(new Font("System Bold", 12));

                TextField productIdField = new TextField();
                productIdField.setLayoutX(189);
                productIdField.setLayoutY(67);

                Label productNameLabel = new Label("Product Name");
                productNameLabel.setLayoutX(27);
                productNameLabel.setLayoutY(107);
                productNameLabel.setFont(new Font("System Bold", 12));

                TextField productNameField = new TextField();
                productNameField.setLayoutX(189);
                productNameField.setLayoutY(103);

                Label priceLabel = new Label("Price");
                priceLabel.setLayoutX(27);
                priceLabel.setLayoutY(145);
                priceLabel.setFont(new Font("System Bold", 12));

                TextField priceField = new TextField();
                priceField.setLayoutX(189);
                priceField.setLayoutY(141);

                Label stockQuantityLabel = new Label("Stock Quantity");
                stockQuantityLabel.setLayoutX(27);
                stockQuantityLabel.setLayoutY(184);
                stockQuantityLabel.setFont(new Font("System Bold", 12));

                TextField stockQuantityField = new TextField();
                stockQuantityField.setLayoutX(189);
                stockQuantityField.setLayoutY(180);

                Label productionCompanyLabel = new Label("Production Company");
                productionCompanyLabel.setLayoutX(27);
                productionCompanyLabel.setLayoutY(223);
                productionCompanyLabel.setFont(new Font("System Bold", 12));

                TextField productionCompanyField = new TextField();
                productionCompanyField.setLayoutX(189);
                productionCompanyField.setLayoutY(219);

                Label warrantyPeriodLabel = new Label("Warranty Period");
                warrantyPeriodLabel.setLayoutX(27);
                warrantyPeriodLabel.setLayoutY(263);
                warrantyPeriodLabel.setFont(new Font("System Bold", 12));

                TextField warrantyPeriodField = new TextField();
                warrantyPeriodField.setLayoutX(189);
                warrantyPeriodField.setLayoutY(259);

                Label powerSourceLabel = new Label("Power Source");
                powerSourceLabel.setLayoutX(27);
                powerSourceLabel.setLayoutY(302);
                powerSourceLabel.setFont(new Font("System Bold", 12));

                ComboBox<String> powerSourceField = new ComboBox<>();
                powerSourceField.setLayoutX(189);
                powerSourceField.setLayoutY(298);
                powerSourceField.setPrefWidth(150);
                powerSourceField.getItems().addAll("Battery", "AC", "DC");

                Label certificationLabel = new Label("Certification");
                certificationLabel.setLayoutX(27);
                certificationLabel.setLayoutY(340);
                certificationLabel.setFont(new Font("System Bold", 12));

                TextField certificationField = new TextField();
                certificationField.setLayoutX(189);
                certificationField.setLayoutY(336);

                Label expectedLifespanLabel = new Label("Expected Lifespan");
                expectedLifespanLabel.setLayoutX(27);
                expectedLifespanLabel.setLayoutY(380);
                expectedLifespanLabel.setFont(new Font("System Bold", 12));

                TextField expectedLifespanField = new TextField();
                expectedLifespanField.setLayoutX(189);
                expectedLifespanField.setLayoutY(376);

                // Button
                Button addButton = new Button("Add");
                addButton.setLayoutX(150);
                addButton.setLayoutY(441);
                addButton.setPrefSize(72, 30);
                addButton.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;");
                addButton.setFont(new Font("System Bold", 17));
                addButton.setOnMouseEntered(
                                e -> addButton.setStyle("-fx-background-color: #AFD3E2; -fx-background-radius: 20;"));
                addButton.setOnMouseExited(
                                e -> addButton.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;"));
                addButton.setOnAction(e -> {
                        try {
                                int productId = Integer.parseInt(productIdField.getText());
                                String productName = productNameField.getText();
                                double price = Double.parseDouble(priceField.getText());
                                int stockQuantity = Integer.parseInt(stockQuantityField.getText());
                                String productionCompany = productionCompanyField.getText();
                                String warrantyPeriod = warrantyPeriodField.getText();
                                String powerSource = powerSourceField.getValue();
                                String certification = certificationField.getText();
                                String expectedLifespan = expectedLifespanField.getText();

                                if (powerSource == null) {
                                        throw new IllegalArgumentException("Power Source must be selected.");
                                }

                                Equipment equipment = new Equipment(warrantyPeriod, powerSource, certification,
                                                expectedLifespan);
                                db.insertProduct(productId, productName, price, stockQuantity, productionCompany,
                                                "MedicalEquipment", equipment, null);

                                EquipmentTableView.setItems(db.getEquipmentList());
                                stage.close();
                        } catch (NumberFormatException ex) {
                                showAlert(AlertType.ERROR, "Invalid Input",
                                                "Please enter valid numeric values for Product ID, Price, and Stock Quantity.");
                        } catch (IllegalArgumentException ex) {
                                showAlert(AlertType.ERROR, "Missing Input", ex.getMessage());
                        } catch (SQLException ex) {
                                ex.printStackTrace();
                                showAlert(AlertType.ERROR, "Database Error",
                                                "An error occurred while interacting with the database.");
                        }
                });

                // Add all components to the inner pane
                innerPane.getChildren().addAll(
                                titleLabel, productIdLabel, productIdField, productNameLabel, productNameField,
                                priceLabel, priceField, stockQuantityLabel, stockQuantityField, productionCompanyLabel,
                                productionCompanyField, warrantyPeriodLabel, warrantyPeriodField, powerSourceLabel,
                                powerSourceField, certificationLabel, certificationField, expectedLifespanLabel,
                                expectedLifespanField, addButton);

                // Add the inner pane to the main pane
                mainPane.getChildren().add(innerPane);

                // Set up the scene and the stage
                Scene scene = new Scene(mainPane);
                stage.setTitle("Add Equipment Form");
                stage.setScene(scene);
                stage.show();
        }

        private void createAddSupplyForm() {
                // Main Pane
                Stage stage = new Stage();
                Pane mainPane = new Pane();
                mainPane.setPrefSize(396, 533);
                mainPane.setStyle("-fx-background-color: D4EBF8;");

                // Inner Pane
                Pane innerPane = new Pane();
                innerPane.setLayoutX(13);
                innerPane.setLayoutY(14);
                innerPane.setPrefSize(371, 506);
                innerPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Title Label
                Label titleLabel = new Label("Add Supply");
                titleLabel.setLayoutX(134);
                titleLabel.setLayoutY(14);
                titleLabel.setFont(new Font("System Bold", 20));

                // Labels
                Label productIdLabel = new Label("Product ID");
                productIdLabel.setLayoutX(27);
                productIdLabel.setLayoutY(71);
                productIdLabel.setFont(new Font("System Bold", 12));

                Label productNameLabel = new Label("Product Name");
                productNameLabel.setLayoutX(27);
                productNameLabel.setLayoutY(107);
                productNameLabel.setFont(new Font("System Bold", 12));

                Label priceLabel = new Label("Price");
                priceLabel.setLayoutX(27);
                priceLabel.setLayoutY(145);
                priceLabel.setFont(new Font("System Bold", 12));

                Label stockQuantityLabel = new Label("Stock Quantity");
                stockQuantityLabel.setLayoutX(27);
                stockQuantityLabel.setLayoutY(184);
                stockQuantityLabel.setFont(new Font("System Bold", 12));

                Label productionCompanyLabel = new Label("Production Company");
                productionCompanyLabel.setLayoutX(27);
                productionCompanyLabel.setLayoutY(223);
                productionCompanyLabel.setFont(new Font("System Bold", 12));

                Label expirationDateLabel = new Label("Expiration Date");
                expirationDateLabel.setLayoutX(27);
                expirationDateLabel.setLayoutY(263);
                expirationDateLabel.setFont(new Font("System Bold", 12));

                Label recyclabilityLabel = new Label("Recyclability");
                recyclabilityLabel.setLayoutX(27);
                recyclabilityLabel.setLayoutY(302);
                recyclabilityLabel.setFont(new Font("System Bold", 12));

                Label usageInstructionsLabel = new Label("Usage Instructions");
                usageInstructionsLabel.setLayoutX(27);
                usageInstructionsLabel.setLayoutY(340);
                usageInstructionsLabel.setFont(new Font("System Bold", 12));

                Label storageRequirementsLabel = new Label("Storage Requirements");
                storageRequirementsLabel.setLayoutX(27);
                storageRequirementsLabel.setLayoutY(380);
                storageRequirementsLabel.setFont(new Font("System Bold", 12));

                // TextFields
                TextField productIdField = new TextField();
                productIdField.setLayoutX(189);
                productIdField.setLayoutY(67);

                TextField productNameField = new TextField();
                productNameField.setLayoutX(189);
                productNameField.setLayoutY(103);

                TextField priceField = new TextField();
                priceField.setLayoutX(189);
                priceField.setLayoutY(141);

                TextField stockQuantityField = new TextField();
                stockQuantityField.setLayoutX(189);
                stockQuantityField.setLayoutY(180);

                TextField productionCompanyField = new TextField();
                productionCompanyField.setLayoutX(189);
                productionCompanyField.setLayoutY(219);

                DatePicker expirationDatePicker = new DatePicker();
                expirationDatePicker.setLayoutX(189);
                expirationDatePicker.setLayoutY(259);

                ComboBox<String> recyclabilityComboBox = new ComboBox<>();
                recyclabilityComboBox.setLayoutX(188);
                recyclabilityComboBox.setLayoutY(298);
                recyclabilityComboBox.setPrefWidth(150);
                recyclabilityComboBox.getItems().addAll("Yes", "No");

                TextField usageInstructionsField = new TextField();
                usageInstructionsField.setLayoutX(189);
                usageInstructionsField.setLayoutY(336);

                TextField storageRequirementsField = new TextField();
                storageRequirementsField.setLayoutX(189);
                storageRequirementsField.setLayoutY(376);

                // Add Button
                Button addButton = new Button("Add");
                addButton.setLayoutX(150);
                addButton.setLayoutY(441);
                addButton.setPrefSize(72, 30);
                addButton.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;");
                addButton.setFont(new Font("System Bold", 17));
                addButton.setOnMouseEntered(
                                e -> addButton.setStyle("-fx-background-color: #AFD3E2; -fx-background-radius: 20;"));
                addButton.setOnMouseExited(
                                e -> addButton.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;"));
                addButton.setOnAction(e -> {
                        try {
                                if (productIdField.getText().isEmpty() || !productIdField.getText().matches("\\d+")) {
                                        showAlert(AlertType.ERROR, "Invalid Input",
                                                        "Product ID must be a numeric value.");
                                        return;
                                }
                                if (priceField.getText().isEmpty()
                                                || !priceField.getText().matches("\\d+(\\.\\d{1,2})?")) {
                                        showAlert(AlertType.ERROR, "Invalid Input", "Price must be a valid number.");
                                        return;
                                }
                                if (stockQuantityField.getText().isEmpty()
                                                || !stockQuantityField.getText().matches("\\d+")) {
                                        showAlert(AlertType.ERROR, "Invalid Input",
                                                        "Stock Quantity must be a numeric value.");
                                        return;
                                }
                                if (expirationDatePicker.getValue() == null) {
                                        showAlert(AlertType.ERROR, "Invalid Input",
                                                        "Expiration Date must be selected.");
                                        return;
                                }
                                int productId = Integer.parseInt(productIdField.getText());
                                String productName = productNameField.getText();
                                double price = Double.parseDouble(priceField.getText());
                                int stockQuantity = Integer.parseInt(stockQuantityField.getText());
                                String productionCompany = productionCompanyField.getText();
                                LocalDate expirationDate = expirationDatePicker.getValue();
                                boolean isRecyclable = recyclabilityComboBox.getValue().equalsIgnoreCase("Yes");
                                String usageInstructions = usageInstructionsField.getText();
                                String storageRequirements = storageRequirementsField.getText();

                                Supply supply = new Supply(Date.valueOf(expirationDate), isRecyclable,
                                                usageInstructions, storageRequirements);
                                db.insertProduct(productId, productName, price, stockQuantity, productionCompany,
                                                "MedicalSupplies", null, supply);

                                supplyTableView.setItems(db.getSupplies());
                                showAlert(AlertType.INFORMATION, "Success", "Supply added successfully!");
                                stage.close();
                        } catch (NumberFormatException nfe) {
                                showAlert(AlertType.ERROR, "Invalid Input",
                                                "Please enter valid numeric values for Price and Stock Quantity.");
                        } catch (SQLException ex) {
                                showAlert(AlertType.ERROR, "Database Error",
                                                "Failed to add supply: " + ex.getMessage());
                        }
                });

                // Add components to inner pane
                innerPane.getChildren().addAll(
                                titleLabel, productIdLabel, productIdField, productNameLabel, productNameField,
                                priceLabel, priceField, stockQuantityLabel, stockQuantityField, productionCompanyLabel,
                                productionCompanyField, expirationDateLabel, expirationDatePicker, recyclabilityLabel,
                                recyclabilityComboBox, usageInstructionsLabel, usageInstructionsField,
                                storageRequirementsLabel, storageRequirementsField, addButton);

                // Add inner pane to main pane
                mainPane.getChildren().add(innerPane);

                // Set up the scene and the stage
                Scene scene = new Scene(mainPane);
                stage.setTitle("Add Supply Form");
                stage.setScene(scene);
                stage.show();
        }

        private void createUpdateEquipmentForm() throws SQLException {
                // Main Pane
                Equipment equipment = EquipmentTableView.getSelectionModel().getSelectedItem();
                Stage stage = new Stage();
                Pane mainPane = new Pane();
                mainPane.setPrefSize(396, 533);
                mainPane.setStyle("-fx-background-color: D4EBF8;");

                // Inner Pane
                Pane innerPane = new Pane();
                innerPane.setLayoutX(13);
                innerPane.setLayoutY(14);
                innerPane.setPrefSize(371, 506);
                innerPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Title Label
                Label titleLabel = new Label("Update Equipment");
                titleLabel.setLayoutX(97);
                titleLabel.setLayoutY(14);
                titleLabel.setFont(new Font("System Bold", 20));

                // Labels
                Label productIdLabel = new Label("Product ID");
                productIdLabel.setLayoutX(27);
                productIdLabel.setLayoutY(71);
                productIdLabel.setFont(new Font("System Bold", 12));

                Label productNameLabel = new Label("Product Name");
                productNameLabel.setLayoutX(27);
                productNameLabel.setLayoutY(107);
                productNameLabel.setFont(new Font("System Bold", 12));

                Label priceLabel = new Label("Price");
                priceLabel.setLayoutX(27);
                priceLabel.setLayoutY(145);
                priceLabel.setFont(new Font("System Bold", 12));

                Label stockQuantityLabel = new Label("Stock Quantity");
                stockQuantityLabel.setLayoutX(27);
                stockQuantityLabel.setLayoutY(184);
                stockQuantityLabel.setFont(new Font("System Bold", 12));

                Label productionCompanyLabel = new Label("Production Company");
                productionCompanyLabel.setLayoutX(27);
                productionCompanyLabel.setLayoutY(223);
                productionCompanyLabel.setFont(new Font("System Bold", 12));

                Label warrantyPeriodLabel = new Label("Warranty Period");
                warrantyPeriodLabel.setLayoutX(27);
                warrantyPeriodLabel.setLayoutY(263);
                warrantyPeriodLabel.setFont(new Font("System Bold", 12));

                Label powerSourceLabel = new Label("Power Source");
                powerSourceLabel.setLayoutX(27);
                powerSourceLabel.setLayoutY(302);
                powerSourceLabel.setFont(new Font("System Bold", 12));

                Label certificationLabel = new Label("Certification");
                certificationLabel.setLayoutX(27);
                certificationLabel.setLayoutY(340);
                certificationLabel.setFont(new Font("System Bold", 12));

                Label expectedLifespanLabel = new Label("Expected Lifespan");
                expectedLifespanLabel.setLayoutX(27);
                expectedLifespanLabel.setLayoutY(380);
                expectedLifespanLabel.setFont(new Font("System Bold", 12));

                // TextFields
                if (equipment != null) {
                        TextField productIdField = new TextField(Integer.toString(equipment.getProductID()));
                        productIdField.setLayoutX(189);
                        productIdField.setLayoutY(67);
                        productIdField.setEditable(false);

                        TextField productNameField = new TextField(equipment.getProductName());
                        productNameField.setLayoutX(189);
                        productNameField.setLayoutY(103);

                        TextField priceField = new TextField(Double.toString(equipment.getPrice()));
                        priceField.setLayoutX(189);
                        priceField.setLayoutY(141);

                        TextField stockQuantityField = new TextField(Integer.toString(equipment.getStockQuantity()));
                        stockQuantityField.setLayoutX(189);
                        stockQuantityField.setLayoutY(180);

                        TextField productionCompanyField = new TextField(equipment.getProductionCompany());
                        productionCompanyField.setLayoutX(189);
                        productionCompanyField.setLayoutY(219);

                        TextField warrantyPeriodField = new TextField(equipment.getWarrantyPeriod());
                        warrantyPeriodField.setLayoutX(189);
                        warrantyPeriodField.setLayoutY(259);

                        TextField certificationField = new TextField(equipment.getCertification());
                        certificationField.setLayoutX(189);
                        certificationField.setLayoutY(336);

                        TextField expectedLifespanField = new TextField(equipment.getExpectedLifespanl());
                        expectedLifespanField.setLayoutX(189);
                        expectedLifespanField.setLayoutY(376);

                        // ComboBox for Power Source (or another field)
                        ComboBox<String> powerSourceComboBox = new ComboBox<>();
                        powerSourceComboBox.setLayoutX(188);
                        powerSourceComboBox.setLayoutY(298);
                        powerSourceComboBox.setPrefWidth(150);
                        powerSourceComboBox.getItems().addAll("Battery", "AC", "DC");
                        powerSourceComboBox.setValue(equipment.getPowerSource());

                        // Update Button
                        Button updateButton = new Button("Update");
                        updateButton.setLayoutX(147);
                        updateButton.setLayoutY(441);
                        updateButton.setPrefSize(84, 37);
                        updateButton.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;");
                        updateButton.setFont(new Font("System Bold", 17));
                        updateButton.setOnMouseEntered(e -> updateButton
                                        .setStyle("-fx-background-color: #AFD3E2; -fx-background-radius: 20;"));
                        updateButton.setOnMouseExited(e -> updateButton
                                        .setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;"));
                        updateButton.setOnAction(e -> {
                                try {
                                        // Retrieve and validate the data from the form fields
                                        int productId = Integer.parseInt(productIdField.getText()); // Ensure valid
                                                                                                    // numeric input
                                        String productName = productNameField.getText();
                                        double price = Double.parseDouble(priceField.getText()); // Ensure valid numeric
                                                                                                 // input
                                        int stockQuantity = Integer.parseInt(stockQuantityField.getText()); // Ensure
                                                                                                            // valid
                                                                                                            // numeric
                                                                                                            // input
                                        String productionCompany = productionCompanyField.getText();
                                        String warrantyPeriod = warrantyPeriodField.getText();
                                        String powerSource = powerSourceComboBox.getValue();
                                        String certification = certificationField.getText();
                                        String expectedLifespan = expectedLifespanField.getText();

                                        Equipment equipment1 = new Equipment(warrantyPeriod, powerSource, certification,
                                                        expectedLifespan);

                                        db.updateProduct(productId, productName, price, stockQuantity,
                                                        productionCompany,
                                                        "MedicalEquipment", equipment1, null);

                                        showAlert(AlertType.INFORMATION, "Success", "Equipment updated successfully.");
                                        EquipmentTableView.setItems(db.getEquipmentList());

                                        stage.close();

                                } catch (NumberFormatException ex) {
                                        showAlert(AlertType.ERROR, "Invalid Input",
                                                        "Please enter valid numeric values for Product ID, Price, or Stock Quantity.");
                                } catch (IllegalArgumentException ex) {
                                        showAlert(AlertType.ERROR, "Invalid Date Format", ex.getMessage());
                                } catch (SQLException ex) {
                                        showAlert(AlertType.ERROR, "Database Error",
                                                        "Failed to update equipment: " + ex.getMessage());
                                        ex.printStackTrace();
                                } catch (Exception ex) {
                                        showAlert(AlertType.ERROR, "Error",
                                                        "An unexpected error occurred: " + ex.getMessage());
                                        ex.printStackTrace();
                                }
                        });

                        // Add all components to the inner pane
                        innerPane.getChildren().addAll(
                                        titleLabel, productIdLabel, productIdField, productNameLabel, productNameField,
                                        priceLabel, priceField, stockQuantityLabel, stockQuantityField,
                                        productionCompanyLabel,
                                        productionCompanyField, warrantyPeriodLabel, warrantyPeriodField,
                                        powerSourceLabel,
                                        powerSourceComboBox, certificationLabel, certificationField,
                                        expectedLifespanLabel,
                                        expectedLifespanField, updateButton);

                        // Add the inner pane to the main pane
                        mainPane.getChildren().add(innerPane);

                        // Set up the scene and the stage
                        Scene scene = new Scene(mainPane);
                        stage.setTitle("Update Equipment Form");
                        stage.setScene(scene);
                        stage.show();
                } else {
                        showAlert(AlertType.WARNING, "No Selection", "Please select a equipment to update.");
                }
        }

        private void createUpdateSupplyForm() {
                // Main Pane
                Supply supply = supplyTableView.getSelectionModel().getSelectedItem();
                Stage stage = new Stage();
                Pane mainPane = new Pane();
                mainPane.setPrefSize(396, 533);
                mainPane.setStyle("-fx-background-color: D4EBF8;");

                // Inner Pane
                Pane innerPane = new Pane();
                innerPane.setLayoutX(13);
                innerPane.setLayoutY(14);
                innerPane.setPrefSize(371, 506);
                innerPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Title Label
                Label titleLabel = new Label("Update Supply");
                titleLabel.setLayoutX(116);
                titleLabel.setLayoutY(14);
                titleLabel.setFont(new Font("System Bold", 20));

                // Labels
                Label productIdLabel = new Label("Product ID");
                productIdLabel.setLayoutX(27);
                productIdLabel.setLayoutY(71);
                productIdLabel.setFont(new Font("System Bold", 12));

                Label productNameLabel = new Label("Product Name");
                productNameLabel.setLayoutX(27);
                productNameLabel.setLayoutY(107);
                productNameLabel.setFont(new Font("System Bold", 12));

                Label priceLabel = new Label("Price");
                priceLabel.setLayoutX(27);
                priceLabel.setLayoutY(145);
                priceLabel.setFont(new Font("System Bold", 12));

                Label stockQuantityLabel = new Label("Stock Quantity");
                stockQuantityLabel.setLayoutX(27);
                stockQuantityLabel.setLayoutY(184);
                stockQuantityLabel.setFont(new Font("System Bold", 12));

                Label productionCompanyLabel = new Label("Production Company");
                productionCompanyLabel.setLayoutX(27);
                productionCompanyLabel.setLayoutY(223);
                productionCompanyLabel.setFont(new Font("System Bold", 12));

                Label expirationDateLabel = new Label("Expiration Date");
                expirationDateLabel.setLayoutX(27);
                expirationDateLabel.setLayoutY(263);
                expirationDateLabel.setFont(new Font("System Bold", 12));

                Label recyclabilityLabel = new Label("Recyclability");
                recyclabilityLabel.setLayoutX(27);
                recyclabilityLabel.setLayoutY(302);
                recyclabilityLabel.setFont(new Font("System Bold", 12));

                Label usageInstructionsLabel = new Label("Usage Instructions");
                usageInstructionsLabel.setLayoutX(27);
                usageInstructionsLabel.setLayoutY(340);
                usageInstructionsLabel.setFont(new Font("System Bold", 12));

                Label storageRequirementsLabel = new Label("Storage Requirements");
                storageRequirementsLabel.setLayoutX(27);
                storageRequirementsLabel.setLayoutY(380);
                storageRequirementsLabel.setFont(new Font("System Bold", 12));

                // TextFields
                if (supply != null) {
                        TextField productIdField = new TextField(Integer.toString(supply.getProductID()));
                        productIdField.setLayoutX(189);
                        productIdField.setLayoutY(67);
                        productIdField.setEditable(false);

                        TextField productNameField = new TextField(supply.getProductName());
                        productNameField.setLayoutX(189);
                        productNameField.setLayoutY(103);

                        TextField priceField = new TextField(Double.toString(supply.getPrice()));
                        priceField.setLayoutX(189);
                        priceField.setLayoutY(141);

                        TextField stockQuantityField = new TextField(Integer.toString(supply.getStockQuantity()));
                        stockQuantityField.setLayoutX(189);
                        stockQuantityField.setLayoutY(180);

                        TextField productionCompanyField = new TextField(supply.getProductionCompany());
                        productionCompanyField.setLayoutX(189);
                        productionCompanyField.setLayoutY(219);

                        // DatePicker for Expiration Date
                        DatePicker expirationDatePicker = new DatePicker(supply.getExpirationDate().toLocalDate());
                        expirationDatePicker.setLayoutX(189);
                        expirationDatePicker.setLayoutY(259);

                        TextField usageInstructionsField = new TextField(supply.getUsageInstructions());
                        usageInstructionsField.setLayoutX(189);
                        usageInstructionsField.setLayoutY(336);

                        TextField storageRequirementsField = new TextField(supply.getStorageRequirements());
                        storageRequirementsField.setLayoutX(189);
                        storageRequirementsField.setLayoutY(376);

                        // ComboBox for Recyclability
                        ComboBox<String> recyclabilityComboBox = new ComboBox<>();
                        recyclabilityComboBox.setLayoutX(188);
                        recyclabilityComboBox.setLayoutY(298);
                        recyclabilityComboBox.setPrefWidth(150);
                        recyclabilityComboBox.getItems().addAll("Yes", "No");
                        recyclabilityComboBox.setValue(supply.isRecyclability() ? "Yes" : "No");

                        // Update Button
                        Button updateButton = new Button("Update");
                        updateButton.setLayoutX(141);
                        updateButton.setLayoutY(439);
                        updateButton.setPrefSize(88, 37);
                        updateButton.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;");
                        updateButton.setFont(new Font("System Bold", 17));
                        updateButton.setOnMouseEntered(e -> updateButton
                                        .setStyle("-fx-background-color: #AFD3E2; -fx-background-radius: 20;"));
                        updateButton.setOnMouseExited(e -> updateButton
                                        .setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;"));

                        updateButton.setOnAction(e -> {
                                try {
                                        // Validate the fields
                                        if (priceField.getText().isEmpty()
                                                        || !priceField.getText().matches("\\d+(\\.\\d{1,2})?")) {
                                                showAlert(AlertType.ERROR, "Invalid Input",
                                                                "Price must be a valid number.");
                                                return;
                                        }
                                        if (stockQuantityField.getText().isEmpty()
                                                        || !stockQuantityField.getText().matches("\\d+")) {
                                                showAlert(AlertType.ERROR, "Invalid Input",
                                                                "Stock Quantity must be a numeric value.");
                                                return;
                                        }

                                        int productId = Integer.parseInt(productIdField.getText());
                                        String productName = productNameField.getText();
                                        double price = Double.parseDouble(priceField.getText());
                                        int stockQuantity = Integer.parseInt(stockQuantityField.getText());
                                        String productionCompany = productionCompanyField.getText();
                                        LocalDate expirationDate = expirationDatePicker.getValue();
                                        boolean recyclability = recyclabilityComboBox.getValue()
                                                        .equalsIgnoreCase("Yes");
                                        String usageInstructions = usageInstructionsField.getText();
                                        String storageRequirements = storageRequirementsField.getText();

                                        Supply supply1 = new Supply(Date.valueOf(expirationDate), recyclability,
                                                        usageInstructions, storageRequirements);

                                        // Update the supply in the database
                                        db.updateProduct(productId, productName, price, stockQuantity,
                                                        productionCompany,
                                                        "MedicalSupplies", null, supply1);

                                        // Inform the user of the successful update
                                        showAlert(AlertType.INFORMATION, "Success", "Supply updated successfully.");
                                        supplyTableView.setItems(db.getSupplies());

                                        // Close the stage
                                        stage.close();
                                } catch (NumberFormatException ex) {
                                        showAlert(AlertType.ERROR, "Invalid Input",
                                                        "Please enter valid numeric values.");
                                } catch (SQLException ex) {
                                        showAlert(AlertType.ERROR, "Database Error",
                                                        "Failed to update supply: " + ex.getMessage());
                                        ex.printStackTrace();
                                } catch (Exception ex) {
                                        showAlert(AlertType.ERROR, "Error",
                                                        "An unexpected error occurred: " + ex.getMessage());
                                        ex.printStackTrace();
                                }
                        });

                        // Add components to the inner pane
                        innerPane.getChildren().addAll(
                                        titleLabel, productIdLabel, productIdField, productNameLabel, productNameField,
                                        priceLabel, priceField, stockQuantityLabel, stockQuantityField,
                                        productionCompanyLabel,
                                        productionCompanyField, expirationDateLabel, expirationDatePicker,
                                        recyclabilityLabel,
                                        recyclabilityComboBox, usageInstructionsLabel, usageInstructionsField,
                                        storageRequirementsLabel, storageRequirementsField, updateButton);

                        // Add the inner pane to the main pane
                        mainPane.getChildren().add(innerPane);

                        // Set up the scene and the stage
                        Scene scene = new Scene(mainPane);
                        stage.setTitle("Update Supply Form");
                        stage.setScene(scene);
                        stage.show();
                } else {
                        showAlert(AlertType.WARNING, "No Selection", "Please select a supply to update.");
                }

        }

        private void createCustomerPage(Stage primaryStage) {
                // Main Pane
                Pane mainPane = new Pane();
                mainPane.setPrefSize(811, 497);
                mainPane.setStyle("-fx-background-color: D4EBF8;");

                // Top Pane
                Pane topPane = new Pane();
                topPane.setLayoutX(11);
                topPane.setLayoutY(14);
                topPane.setPrefSize(790, 48);
                topPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Logo Image
                ImageView logoImageView = new ImageView(new Image("downloadsmallLogo.png"));
                logoImageView.setLayoutX(30);

                // "Products" Button
                Button productsButton = new Button("Products");
                productsButton.setLayoutX(181);
                productsButton.setLayoutY(1);
                productsButton.setPrefSize(113, 48);
                productsButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;");
                productsButton.setFont(new Font("System Bold", 12));
                productsButton.setOnMouseEntered(e -> productsButton.setStyle(
                                "-fx-background-color: F2F9FF; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));
                productsButton.setOnMouseExited(e -> productsButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));

                // "Cart" Button
                Button cartButton = new Button("Cart");
                cartButton.setLayoutX(338);
                cartButton.setLayoutY(1);
                cartButton.setPrefSize(113, 48);
                cartButton.setStyle("-fx-background-color: transparent;");
                cartButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(cartButton);
                cartButton.setOnAction(e -> createCustomerCartPage(primaryStage));

                // "Purchase History" Button
                Button purchaseHistoryButton = new Button("Purchase History");
                purchaseHistoryButton.setLayoutX(495);
                purchaseHistoryButton.setLayoutY(1);
                purchaseHistoryButton.setPrefSize(113, 48);
                purchaseHistoryButton.setStyle("-fx-background-color: transparent;");
                purchaseHistoryButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(purchaseHistoryButton);
                purchaseHistoryButton.setOnAction(e -> createCustomerPurchaseHistoryPage(primaryStage));

                // "Settings" Button
                Button settingsButton = new Button("Settings");
                settingsButton.setLayoutX(645);
                settingsButton.setLayoutY(1);
                settingsButton.setPrefSize(113, 48);
                settingsButton.setStyle("-fx-background-color: transparent;");
                settingsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(settingsButton);
                settingsButton.setOnAction(e -> createCustomerSettingsPage(primaryStage));

                // Add elements to the top pane
                topPane.getChildren().addAll(logoImageView, productsButton, cartButton, purchaseHistoryButton,
                                settingsButton);

                // Bottom Pane
                Pane bottomPane = new Pane();
                bottomPane.setLayoutX(11);
                bottomPane.setLayoutY(69);
                bottomPane.setPrefSize(790, 416);
                bottomPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // "Medical Supplies" Button
                Button medicalSuppliesButton = new Button("Medical Supplies And Consumables");
                medicalSuppliesButton.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);
                medicalSuppliesButton.setLayoutX(47);
                medicalSuppliesButton.setLayoutY(103);
                medicalSuppliesButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;");
                medicalSuppliesButton.setFont(new Font("System Bold", 12));
                medicalSuppliesButton.setOnAction(e -> {
                        try {
                                createCustomerSuppliesPage(primaryStage);
                        } catch (SQLException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                        }
                });

                ImageView medicalSuppliesImage = new ImageView(new Image("1681454275602-768x512.jpeg"));
                medicalSuppliesImage.setFitWidth(310);
                medicalSuppliesImage.setFitHeight(177);
                medicalSuppliesButton.setGraphic(medicalSuppliesImage);

                // "Medical Equipment" Button
                Button medicalEquipmentButton = new Button("Medical Equipments And Devices");
                medicalEquipmentButton.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);
                medicalEquipmentButton.setLayoutX(395);
                medicalEquipmentButton.setLayoutY(103);
                medicalEquipmentButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;");
                medicalEquipmentButton.setFont(new Font("System Bold", 12));
                medicalEquipmentButton.setOnAction(e -> {
                        try {
                                createCustomerEquipmentPage(primaryStage);
                        } catch (SQLException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                        }
                });

                ImageView medicalEquipmentImage = new ImageView(new Image(
                                "equipment-medical-devices-modern-operating-room-operating-theatre-selective-focus_1028938-62298.jpg"));
                medicalEquipmentImage.setFitWidth(310);
                medicalEquipmentImage.setFitHeight(177);
                medicalEquipmentButton.setGraphic(medicalEquipmentImage);

                // Add buttons to the bottom pane
                bottomPane.getChildren().addAll(medicalSuppliesButton, medicalEquipmentButton);

                // Add panes to the main pane
                mainPane.getChildren().addAll(topPane, bottomPane);

                // Set up the scene
                Scene scene = new Scene(mainPane);
                primaryStage.setScene(scene);
                primaryStage.setTitle("FXML to JavaFX");
                primaryStage.show();
        }

        private void createCustomerEquipmentPage(Stage primaryStage) throws SQLException {
                // Main Pane
                Pane mainPane = new Pane();
                mainPane.setPrefSize(811, 497);
                mainPane.setStyle("-fx-background-color: D4EBF8;");

                // Top Navigation Pane
                Pane topPane = new Pane();
                topPane.setLayoutX(11);
                topPane.setLayoutY(14);
                topPane.setPrefSize(790, 48);
                topPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Logo Image
                ImageView logoImageView = new ImageView(new Image("downloadsmallLogo.png"));
                logoImageView.setLayoutX(30);

                // "Products" Button
                Button productsButton = new Button("Products");
                productsButton.setLayoutX(181);
                productsButton.setLayoutY(1);
                productsButton.setPrefSize(113, 48);
                productsButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;");
                productsButton.setFont(new Font("System Bold", 12));
                productsButton.setOnMouseEntered(e -> productsButton.setStyle(
                                "-fx-background-color: F2F9FF; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));
                productsButton.setOnMouseExited(e -> productsButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));
                productsButton.setOnAction(e -> createCustomerPage(primaryStage));

                // "Cart" Button
                Button cartButton = new Button("Cart");
                cartButton.setLayoutX(338);
                cartButton.setLayoutY(1);
                cartButton.setPrefSize(113, 48);
                cartButton.setStyle("-fx-background-color: transparent;");
                cartButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(cartButton);
                cartButton.setOnAction(e -> createCustomerCartPage(primaryStage));

                // "Purchase History" Button
                Button purchaseHistoryButton = new Button("Purchase History");
                purchaseHistoryButton.setLayoutX(495);
                purchaseHistoryButton.setLayoutY(1);
                purchaseHistoryButton.setPrefSize(113, 48);
                purchaseHistoryButton.setStyle("-fx-background-color: transparent;");
                purchaseHistoryButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(purchaseHistoryButton);
                purchaseHistoryButton.setOnAction(e -> createCustomerPurchaseHistoryPage(primaryStage));

                // "Settings" Button
                Button settingsButton = new Button("Settings");
                settingsButton.setLayoutX(645);
                settingsButton.setLayoutY(1);
                settingsButton.setPrefSize(113, 48);
                settingsButton.setStyle("-fx-background-color: transparent;");
                settingsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(settingsButton);
                settingsButton.setOnAction(e -> createCustomerSettingsPage(primaryStage));

                topPane.getChildren().addAll(logoImageView, productsButton, cartButton, purchaseHistoryButton,
                                settingsButton);

                // Bottom Pane
                Pane bottomPane = new Pane();
                bottomPane.setLayoutX(11);
                bottomPane.setLayoutY(69);
                bottomPane.setPrefSize(790, 416);
                bottomPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Search Bar
                Pane searchPane = new Pane();
                searchPane.setLayoutX(581);
                searchPane.setLayoutY(17);
                searchPane.setPrefSize(195, 30);
                searchPane.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;");

                ImageView searchIcon = new ImageView(new Image("searchsmall.png"));
                searchIcon.setFitHeight(20);
                searchIcon.setFitWidth(20);
                searchIcon.setLayoutX(4);
                searchIcon.setLayoutY(6);

                TextField searchField = new TextField();
                searchField.setLayoutX(24);
                searchField.setLayoutY(3);
                searchField.setPrefSize(166, 24);
                searchField.setPromptText("Search Product...");
                searchField.setStyle("-fx-background-color: transparent;");

                searchPane.getChildren().addAll(searchIcon, searchField);

                // Title Label
                Label titleLabel = new Label("Medical Equipment And Devices");
                titleLabel.setLayoutX(16);
                titleLabel.setLayoutY(18);
                titleLabel.setFont(new Font("System Bold", 19));

                // Separator Line
                Label separatorLine = new Label(
                                "_______________________________________________________________________________________________________________________________________________________________");
                separatorLine.setLayoutX(2);
                separatorLine.setLayoutY(49);

                // TableView
                TableView<Equipment> tableView = new TableView<>();
                tableView.setLayoutX(16);
                tableView.setLayoutY(89);
                tableView.setPrefSize(761, 239);

                // ID Column
                TableColumn<Equipment, String> idColumn = new TableColumn<>("ID");
                idColumn.setPrefWidth(50);
                idColumn.setCellValueFactory(new PropertyValueFactory<>("ProductID"));

                // Name Column
                TableColumn<Equipment, String> nameColumn = new TableColumn<>("Name");
                nameColumn.setPrefWidth(90);
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("ProductName"));

                // Price Column
                TableColumn<Equipment, String> priceColumn = new TableColumn<>("Price");
                priceColumn.setPrefWidth(50);
                priceColumn.setCellValueFactory(new PropertyValueFactory<>("Price"));

                // Quantity Column
                // TableColumn<Equipment, String> quantityColumn = new
                // TableColumn<>("Quantity");
                // quantityColumn.setPrefWidth(50);
                // quantityColumn.setCellValueFactory(new
                // PropertyValueFactory<>("StockQuantity"));

                // Company Column
                TableColumn<Equipment, String> companyColumn = new TableColumn<>("Company");
                companyColumn.setPrefWidth(120);
                companyColumn.setCellValueFactory(new PropertyValueFactory<>("ProductionCompany"));

                // Warranty Period Column
                TableColumn<Equipment, String> warrantyColumn = new TableColumn<>("Warranty Period");
                warrantyColumn.setPrefWidth(110);
                warrantyColumn.setCellValueFactory(new PropertyValueFactory<>("WarrantyPeriod"));

                // Power Source Column
                TableColumn<Equipment, String> powerSourceColumn = new TableColumn<>("Power Source");
                powerSourceColumn.setPrefWidth(80);
                powerSourceColumn.setCellValueFactory(new PropertyValueFactory<>("PowerSource"));

                // Certification Column
                TableColumn<Equipment, String> certificationColumn = new TableColumn<>("Certification");
                certificationColumn.setPrefWidth(80);
                certificationColumn.setCellValueFactory(new PropertyValueFactory<>("Certification"));

                // Expected Lifespan Column
                TableColumn<Equipment, String> lifespanColumn = new TableColumn<>("Expected Lifespan");
                lifespanColumn.setPrefWidth(130);
                lifespanColumn.setCellValueFactory(new PropertyValueFactory<>("ExpectedLifespanl"));

                tableView.getColumns().addAll(idColumn, nameColumn, priceColumn, companyColumn,
                                warrantyColumn, powerSourceColumn, certificationColumn, lifespanColumn);

                tableView.setItems(db.getEquipmentList());

                // ComboBox for Quantity
                ComboBox<String> quantityComboBox = new ComboBox<>();
                quantityComboBox.setLayoutX(282);
                quantityComboBox.setLayoutY(357);
                quantityComboBox.setPrefSize(97, 24);
                quantityComboBox.setPromptText("Quantity");
                quantityComboBox.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;");
                for (int i = 1; i <= 10; i++) {
                        quantityComboBox.getItems().add(String.valueOf(i));
                }
                quantityComboBox.setOnMouseEntered(e -> quantityComboBox.setStyle(
                                "-fx-background-color: F2F9FF; -fx-background-radius: 20; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;"));
                quantityComboBox.setOnMouseExited(e -> quantityComboBox.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;"));

                // Add to Cart Button
                Button addToCartButton = new Button("Add To Cart");
                addToCartButton.setLayoutX(385);
                addToCartButton.setLayoutY(356);
                addToCartButton.setPrefSize(135, 30);
                addToCartButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;");
                addToCartButton.setFont(new Font("System Bold", 12));
                addButtonEffect(addToCartButton);
                addToCartButton.setOnAction(e -> {
                        try {
                                Equipment selected = tableView.getSelectionModel().getSelectedItem();

                                if (selected != null) {
                                        int quantity = Integer.parseInt(
                                                        quantityComboBox.getSelectionModel().getSelectedItem());
                                        if (quantity <= selected.getStockQuantity()) {
                                                Cart cart = new Cart(selected.getProductID(), selected.getProductName(),
                                                                selected.getPrice(), quantity, 0);
                                                customerCartTableView.getItems().add(cart);
                                                showAlert(AlertType.INFORMATION, "Product added to cart",
                                                                "The product has been added to the cart");
                                        } else {
                                                showAlert(AlertType.INFORMATION, "Out of stock",
                                                                "Stock Quantity: " + selected.getStockQuantity());
                                        }

                                } else {
                                        showAlert(AlertType.INFORMATION, "No selected product",
                                                        "Please select a product to add to cart");
                                }
                        } catch (NumberFormatException E) {
                                showAlert(AlertType.INFORMATION, "Select Quantity", "Please select a quantity");
                        }
                });

                // Add elements to bottom pane
                bottomPane.getChildren().addAll(searchPane, titleLabel, separatorLine, tableView, quantityComboBox,
                                addToCartButton);

                // Add panes to main pane
                mainPane.getChildren().addAll(topPane, bottomPane);

                searchField.setOnKeyPressed(e -> {
                        if (e.getCode() == KeyCode.ENTER) {
                                try {
                                        tableView.setItems(db.searchEquipment(searchField.getText()));
                                } catch (SQLException e1) {
                                        e1.printStackTrace();
                                }
                        }
                });

                // Set Scene and Stage
                Scene scene = new Scene(mainPane);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Medical Equipment UI");
                primaryStage.show();
        }

        private void createCustomerSuppliesPage(Stage primaryStage) throws SQLException {
                // Main Pane
                Pane mainPane = new Pane();
                mainPane.setPrefSize(811, 497);
                mainPane.setStyle("-fx-background-color: D4EBF8;");

                // Top Navigation Pane
                Pane topPane = new Pane();
                topPane.setLayoutX(11);
                topPane.setLayoutY(14);
                topPane.setPrefSize(790, 48);
                topPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Logo Image
                ImageView logoImageView = new ImageView(new Image("downloadsmallLogo.png"));
                logoImageView.setLayoutX(30);

                // "Products" Button
                Button productsButton = new Button("Products");
                productsButton.setLayoutX(181);
                productsButton.setLayoutY(1);
                productsButton.setPrefSize(113, 48);
                productsButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;");
                productsButton.setFont(new Font("System Bold", 12));
                productsButton.setOnMouseEntered(e -> productsButton.setStyle(
                                "-fx-background-color: F2F9FF; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));
                productsButton.setOnMouseExited(e -> productsButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));
                productsButton.setOnAction(e -> createCustomerPage(primaryStage));

                // "Cart" Button
                Button cartButton = new Button("Cart");
                cartButton.setLayoutX(338);
                cartButton.setLayoutY(1);
                cartButton.setPrefSize(113, 48);
                cartButton.setStyle("-fx-background-color: transparent;");
                cartButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(cartButton);
                cartButton.setOnAction(e -> createCustomerCartPage(primaryStage));

                // "Purchase History" Button
                Button purchaseHistoryButton = new Button("Purchase History");
                purchaseHistoryButton.setLayoutX(495);
                purchaseHistoryButton.setLayoutY(1);
                purchaseHistoryButton.setPrefSize(113, 48);
                purchaseHistoryButton.setStyle("-fx-background-color: transparent;");
                purchaseHistoryButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(purchaseHistoryButton);
                purchaseHistoryButton.setOnAction(e -> createCustomerPurchaseHistoryPage(primaryStage));

                // "Settings" Button
                Button settingsButton = new Button("Settings");
                settingsButton.setLayoutX(645);
                settingsButton.setLayoutY(1);
                settingsButton.setPrefSize(113, 48);
                settingsButton.setStyle("-fx-background-color: transparent;");
                settingsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(settingsButton);
                settingsButton.setOnAction(e -> createCustomerSettingsPage(primaryStage));

                topPane.getChildren().addAll(logoImageView, productsButton, cartButton, purchaseHistoryButton,
                                settingsButton);

                // Bottom Pane
                Pane bottomPane = new Pane();
                bottomPane.setLayoutX(11);
                bottomPane.setLayoutY(69);
                bottomPane.setPrefSize(790, 416);
                bottomPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Search Bar
                Pane searchPane = new Pane();
                searchPane.setLayoutX(581);
                searchPane.setLayoutY(17);
                searchPane.setPrefSize(195, 30);
                searchPane.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;");

                ImageView searchIcon = new ImageView(new Image("searchsmall.png"));
                searchIcon.setFitHeight(20);
                searchIcon.setFitWidth(20);
                searchIcon.setLayoutX(4);
                searchIcon.setLayoutY(6);

                TextField searchField = new TextField();
                searchField.setLayoutX(24);
                searchField.setLayoutY(3);
                searchField.setPrefSize(166, 24);
                searchField.setPromptText("Search Product...");
                searchField.setStyle("-fx-background-color: transparent;");

                searchPane.getChildren().addAll(searchIcon, searchField);

                // Title Label
                Label titleLabel = new Label("Medical Supplies And Consumables");
                titleLabel.setLayoutX(16);
                titleLabel.setLayoutY(18);
                titleLabel.setFont(new Font("System Bold", 19));

                // Separator Line
                Label separatorLine = new Label(
                                "_______________________________________________________________________________________________________________________________________________________________");
                separatorLine.setLayoutX(2);
                separatorLine.setLayoutY(49);

                // TableView
                TableView<Supply> tableView = new TableView<>();
                tableView.setLayoutX(16);
                tableView.setLayoutY(89);
                tableView.setPrefSize(761, 239);

                // Define columns
                TableColumn<Supply, String> idColumn = new TableColumn<>("ID");
                idColumn.setCellValueFactory(new PropertyValueFactory<>("ProductID"));
                idColumn.setPrefWidth(75);

                TableColumn<Supply, String> nameColumn = new TableColumn<>("Name");
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("ProductName"));
                nameColumn.setPrefWidth(75);

                TableColumn<Supply, String> priceColumn = new TableColumn<>("Price");
                priceColumn.setCellValueFactory(new PropertyValueFactory<>("Price"));
                priceColumn.setPrefWidth(75);

                // TableColumn<Supply, String> quantityColumn = new TableColumn<>("Quantity");
                // quantityColumn.setCellValueFactory(new
                // PropertyValueFactory<>("StockQuantity"));
                // quantityColumn.setPrefWidth(75);

                TableColumn<Supply, String> companyColumn = new TableColumn<>("Company");
                companyColumn.setCellValueFactory(new PropertyValueFactory<>("ProductionCompany"));
                companyColumn.setPrefWidth(86.857);

                TableColumn<Supply, String> expirationDateColumn = new TableColumn<>("Expiration Date");
                expirationDateColumn.setCellValueFactory(new PropertyValueFactory<>("ExpirationDate"));
                expirationDateColumn.setPrefWidth(101.486);

                TableColumn<Supply, String> usageInstructionsColumn = new TableColumn<>("Usage Instructions");
                usageInstructionsColumn.setCellValueFactory(new PropertyValueFactory<>("UsageInstructions"));
                usageInstructionsColumn.setPrefWidth(116.114);

                TableColumn<Supply, String> storageRequirementsColumn = new TableColumn<>("Storage Requirements");
                storageRequirementsColumn.setCellValueFactory(new PropertyValueFactory<>("StorageRequirements"));
                storageRequirementsColumn.setPrefWidth(149.943);

                // Add columns to TableView
                tableView.getColumns().addAll(idColumn, nameColumn, priceColumn, companyColumn,
                                expirationDateColumn, usageInstructionsColumn, storageRequirementsColumn);

                tableView.setItems(db.getSupplies());

                // ComboBox for Quantity
                ComboBox<String> quantityComboBox = new ComboBox<>();
                quantityComboBox.setLayoutX(282);
                quantityComboBox.setLayoutY(357);
                quantityComboBox.setPrefSize(97, 24);
                quantityComboBox.setPromptText("Quantity");
                quantityComboBox.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;");
                for (int i = 1; i <= 10; i++) {
                        quantityComboBox.getItems().add(String.valueOf(i));
                }
                quantityComboBox.setOnMouseEntered(e -> quantityComboBox.setStyle(
                                "-fx-background-color: F2F9FF; -fx-background-radius: 20; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;"));
                quantityComboBox.setOnMouseExited(e -> quantityComboBox.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;"));

                // Add to Cart Button
                Button addToCartButton = new Button("Add To Cart");
                addToCartButton.setLayoutX(385);
                addToCartButton.setLayoutY(356);
                addToCartButton.setPrefSize(135, 30);
                addToCartButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;");
                addToCartButton.setFont(new Font("System Bold", 12));
                addButtonEffect(addToCartButton);
                addToCartButton.setOnAction(e -> {
                        try {
                                Supply selected = tableView.getSelectionModel().getSelectedItem();

                                if (selected != null) {
                                        int quantity = Integer.parseInt(
                                                        quantityComboBox.getSelectionModel().getSelectedItem());
                                        if (quantity <= selected.getStockQuantity()) {
                                                Cart cart = new Cart(selected.getProductID(), selected.getProductName(),
                                                                selected.getPrice(), quantity, 0);
                                                customerCartTableView.getItems().add(cart);
                                                showAlert(AlertType.INFORMATION, "Product added to cart",
                                                                "The product has been added to the cart");
                                        } else {
                                                showAlert(AlertType.INFORMATION, "Out of stock",
                                                                "Stock Quantity: " + selected.getStockQuantity());
                                        }

                                } else {
                                        showAlert(AlertType.INFORMATION, "No selected product",
                                                        "Please select a product to add to cart");
                                }
                        } catch (NumberFormatException E) {
                                showAlert(AlertType.INFORMATION, "Select Quantity", "Please select a quantity");
                        }
                });

                // Add elements to bottom pane
                bottomPane.getChildren().addAll(searchPane, titleLabel, separatorLine, tableView, quantityComboBox,
                                addToCartButton);

                // Add panes to main pane
                mainPane.getChildren().addAll(topPane, bottomPane);

                searchField.setOnKeyPressed(e -> {
                        if (e.getCode() == KeyCode.ENTER) {
                                try {
                                        tableView.setItems(db.searchSupply(searchField.getText()));
                                } catch (SQLException e1) {
                                        e1.printStackTrace();
                                }
                        }
                });

                // Set Scene and Stage
                Scene scene = new Scene(mainPane);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Medical Equipment UI");
                primaryStage.show();
        }

        private void showAlert(AlertType type, String title, String message) {
                Alert alert = new Alert(type);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
        }

        private void addButtonEffect(Button button) {
                button.setOnMouseEntered(e -> button.setStyle(
                                "-fx-background-color: F2F9FF; -fx-background-radius: 20; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;"));
                button.setOnMouseExited(e -> button.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-width: 2; -fx-border-color: black; -fx-border-radius: 20;"));
        }

        private void addTopButtonsEffect(Button button) {
                button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: F2F9FF;"));
                button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent;"));
                // "-fx-background-color: D4EBF8;"

        }

        private void createCustomerCartPage(Stage primaryStage) {
                // Root Pane
                Pane root = new Pane();
                root.setPrefSize(811, 497);
                root.setStyle("-fx-background-color: D4EBF8;");

                // Top Pane
                Pane topPane = new Pane();
                topPane.setLayoutX(11);
                topPane.setLayoutY(14);
                topPane.setPrefSize(790, 48);
                topPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Logo
                ImageView logo = new ImageView(new Image("downloadsmallLogo.png"));
                logo.setLayoutX(30);
                topPane.getChildren().add(logo);

                // Products Button
                Button productsButton = new Button("Products");
                productsButton.setLayoutX(181);
                productsButton.setLayoutY(1);
                productsButton.setPrefSize(113, 48);
                productsButton.setStyle("-fx-background-color: transparent;");
                productsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(productsButton);
                productsButton.setOnAction(e -> createCustomerPage(primaryStage));

                // Purchase History Button
                Button purchaseHistoryButton = new Button("Purchase History");
                purchaseHistoryButton.setLayoutX(495);
                purchaseHistoryButton.setLayoutY(1);
                purchaseHistoryButton.setPrefSize(113, 48);
                purchaseHistoryButton.setStyle("-fx-background-color: transparent;");
                purchaseHistoryButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(purchaseHistoryButton);
                purchaseHistoryButton.setOnAction(e -> createCustomerPurchaseHistoryPage(primaryStage));

                // Settings Button
                Button settingsButton = new Button("Settings");
                settingsButton.setLayoutX(645);
                settingsButton.setLayoutY(1);
                settingsButton.setPrefSize(113, 48);
                settingsButton.setStyle("-fx-background-color: transparent;");
                settingsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(settingsButton);
                settingsButton.setOnAction(e -> createCustomerSettingsPage(primaryStage));

                // Cart Button
                Button cartButton = new Button("Cart");
                cartButton.setLayoutX(338);
                cartButton.setLayoutY(1);
                cartButton.setPrefSize(113, 48);
                cartButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;");
                cartButton.setFont(new Font("System Bold", 12));
                cartButton.setOnMouseEntered(e -> cartButton.setStyle(
                                "-fx-background-color: F2F9FF; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));
                cartButton.setOnMouseExited(e -> cartButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));

                topPane.getChildren().addAll(productsButton, purchaseHistoryButton, settingsButton, cartButton);

                // Main Pane
                Pane mainPane = new Pane();
                mainPane.setLayoutX(11);
                mainPane.setLayoutY(69);
                mainPane.setPrefSize(790, 416);
                mainPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // TableView
                customerCartTableView.setLayoutX(155);
                customerCartTableView.setLayoutY(36);
                customerCartTableView.setPrefSize(480, 237);

                TableColumn<Cart, String> idColumn = new TableColumn<>("ID");
                idColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));
                idColumn.setPrefWidth(84);

                TableColumn<Cart, String> nameColumn = new TableColumn<>("Name");
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
                nameColumn.setPrefWidth(94);

                TableColumn<Cart, String> priceColumn = new TableColumn<>("Price");
                priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
                priceColumn.setPrefWidth(91);

                TableColumn<Cart, String> quantityColumn = new TableColumn<>("Quantity");
                quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
                quantityColumn.setPrefWidth(89);

                TableColumn<Cart, String> totalPriceColumn = new TableColumn<>("Total Price");
                totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
                totalPriceColumn.setPrefWidth(120);

                customerCartTableView.getColumns().addAll(idColumn, nameColumn, priceColumn, quantityColumn,
                                totalPriceColumn);

                // Total Price Label
                TextField totalPriceLabel = new TextField("Total Price");
                totalPriceLabel.setLayoutX(169);
                totalPriceLabel.setLayoutY(285);
                totalPriceLabel.setPrefSize(148, 31);
                totalPriceLabel.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;");
                totalPriceLabel.setFont(new Font("System Bold", 15));
                totalPriceLabel.setEditable(false);
                totalPriceLabel.setAlignment(javafx.geometry.Pos.CENTER);

                // Total Price Field
                TextField totalPriceField = new TextField("0");
                totalPriceField.setLayoutX(336);
                totalPriceField.setLayoutY(285);
                totalPriceField.setPrefSize(148, 31);
                totalPriceField.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;");
                totalPriceField.setFont(new Font("System Bold", 15));
                totalPriceField.setEditable(false);
                totalPriceField.setAlignment(javafx.geometry.Pos.CENTER);
                ObservableList<Cart> cartList = customerCartTableView.getItems();
                double total = 0;
                if (cartList != null) {
                        for (int j = 0; j < cartList.size(); j++) {
                                total += cartList.get(j).getPrice() * cartList.get(j).getQuantity();
                        }

                        totalPriceField.setText(Double.toString(total));
                }

                // Checkout Button
                Button checkoutButton = new Button("Checkout");
                checkoutButton.setLayoutX(361);
                checkoutButton.setLayoutY(348);
                checkoutButton.setPrefSize(98, 39);
                checkoutButton.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-width: 2 2 2 2; -fx-border-color: black black black black; -fx-border-radius: 20;");
                checkoutButton.setFont(new Font("System Bold", 15));
                checkoutButton.setAlignment(javafx.geometry.Pos.CENTER);
                addButtonEffect(checkoutButton);
                checkoutButton.setOnAction(e -> {
                        if (customerCartTableView.getItems() != null) {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("Confirmation");
                                alert.setContentText("Are you sure you want to checkout?");
                                alert.showAndWait().ifPresent(response -> {
                                        if (response == ButtonType.OK) {
                                                db.insertOrder(db.getCustomerIdFromUsername(userName), cartList,
                                                                Double.parseDouble(totalPriceField.getText()));
                                                customerCartTableView.getItems().clear();
                                                totalPriceField.setText("0");
                                        }
                                });
                        }
                });

                // Remove From Cart Button
                Button removeFromCartButton = new Button("Remove From Cart");
                removeFromCartButton.setLayoutX(500);
                removeFromCartButton.setLayoutY(285);
                removeFromCartButton.setPrefSize(127, 31);
                removeFromCartButton.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-width: 2 2 2 2; -fx-border-color: black black black black; -fx-border-radius: 20;");
                removeFromCartButton.setFont(new Font("System Bold", 12));
                addButtonEffect(removeFromCartButton);
                removeFromCartButton.setOnAction(e -> {
                        Cart cart = customerCartTableView.getSelectionModel().getSelectedItem();
                        if (cart != null) {
                                customerCartTableView.getItems().remove(cart);
                                ObservableList<Cart> cartList1 = customerCartTableView.getItems();
                                double total1 = 0;
                                if (cartList != null) {
                                        for (int j = 0; j < cartList1.size(); j++) {
                                                total1 += cartList1.get(j).getPrice() * cartList1.get(j).getQuantity();
                                        }
                                }
                                totalPriceField.setText(Double.toString(total1));
                        }
                });

                mainPane.getChildren().addAll(customerCartTableView, totalPriceLabel, totalPriceField, checkoutButton,
                                removeFromCartButton);

                // Add everything to the root pane
                root.getChildren().addAll(topPane, mainPane);

                // Create and set the scene
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                primaryStage.setTitle("JavaFX Example");
                primaryStage.show();
        }

        private void createCustomerPurchaseHistoryPage(Stage primaryStage) {
                // Root Pane
                Pane root = new Pane();
                root.setPrefSize(811, 497);
                root.setStyle("-fx-background-color: D4EBF8;");

                // Top Pane
                Pane topPane = new Pane();
                topPane.setLayoutX(11);
                topPane.setLayoutY(14);
                topPane.setPrefSize(790, 48);
                topPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Logo
                ImageView logo = new ImageView(new Image("downloadsmallLogo.png"));
                logo.setLayoutX(30);

                // Products Button
                Button productsButton = new Button("Products");
                productsButton.setLayoutX(181);
                productsButton.setLayoutY(1);
                productsButton.setPrefSize(113, 48);
                productsButton.setStyle("-fx-background-color: transparent;");
                productsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(productsButton);
                productsButton.setOnAction(e -> createCustomerPage(primaryStage));

                // Purchase History Button
                Button purchaseHistoryButton = new Button("Purchase History");
                purchaseHistoryButton.setLayoutX(495);
                purchaseHistoryButton.setLayoutY(1);
                purchaseHistoryButton.setPrefSize(113, 48);
                purchaseHistoryButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;");
                purchaseHistoryButton.setFont(new Font("System Bold", 12));
                purchaseHistoryButton.setOnMouseEntered(e -> purchaseHistoryButton.setStyle(
                                "-fx-background-color: F2F9FF; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));
                purchaseHistoryButton.setOnMouseExited(e -> purchaseHistoryButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));

                // Settings Button
                Button settingsButton = new Button("Settings");
                settingsButton.setLayoutX(645);
                settingsButton.setLayoutY(1);
                settingsButton.setPrefSize(113, 48);
                settingsButton.setStyle("-fx-background-color: transparent;");
                settingsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(settingsButton);
                settingsButton.setOnAction(e -> createCustomerSettingsPage(primaryStage));

                // Cart Button
                Button cartButton = new Button("Cart");
                cartButton.setLayoutX(338);
                cartButton.setLayoutY(1);
                cartButton.setPrefSize(113, 48);
                cartButton.setStyle("-fx-background-color: transparent;");
                cartButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(cartButton);
                cartButton.setOnAction(e -> createCustomerCartPage(primaryStage));

                // Add components to the top pane
                topPane.getChildren().addAll(logo, productsButton, purchaseHistoryButton, settingsButton, cartButton);

                // Main Pane
                Pane mainPane = new Pane();
                mainPane.setLayoutX(11);
                mainPane.setLayoutY(69);
                mainPane.setPrefSize(790, 416);
                mainPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Table View
                TableView<PurchaseHistory> tableView = new TableView<>();
                tableView.setLayoutX(116);
                tableView.setLayoutY(84);
                tableView.setPrefSize(559, 248);

                TableColumn<PurchaseHistory, String> productNameColumn = new TableColumn<>("Product Name");
                productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
                productNameColumn.setPrefWidth(104.2);

                TableColumn<PurchaseHistory, String> quantityColumn = new TableColumn<>("Quantity");
                quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
                quantityColumn.setPrefWidth(70.4);

                TableColumn<PurchaseHistory, String> priceColumn = new TableColumn<>("Price");
                priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
                priceColumn.setPrefWidth(54.85);

                TableColumn<PurchaseHistory, String> totalPriceColumn = new TableColumn<>("Total Price");
                totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
                totalPriceColumn.setPrefWidth(113.37);

                TableColumn<PurchaseHistory, String> dateColumn = new TableColumn<>("Date");
                dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
                dateColumn.setPrefWidth(113.37);

                TableColumn<PurchaseHistory, String> statusColumn = new TableColumn<>("Status");
                statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
                statusColumn.setPrefWidth(100.57);

                tableView.getColumns().addAll(productNameColumn, quantityColumn, priceColumn, totalPriceColumn,
                                dateColumn, statusColumn);

                // Labels and TextField
                Label orderNumberLabel = new Label("Order Number:");
                orderNumberLabel.setLayoutX(116);
                orderNumberLabel.setLayoutY(51);
                orderNumberLabel.setFont(new Font("System Bold", 14));

                Label totalPriceLabel = new Label("Total Price");
                totalPriceLabel.setLayoutX(254);
                totalPriceLabel.setLayoutY(349);
                totalPriceLabel.setPrefSize(133, 40);
                totalPriceLabel.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;");
                totalPriceLabel.setFont(new Font("System Bold", 18));
                totalPriceLabel.setAlignment(javafx.geometry.Pos.CENTER);

                TextField totalPriceField = new TextField();
                totalPriceField.setLayoutX(405);
                totalPriceField.setLayoutY(349);
                totalPriceField.setPrefSize(133, 40);
                totalPriceField.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;");
                totalPriceField.setFont(new Font("System Bold", 18));
                totalPriceField.setEditable(false);
                totalPriceField.setAlignment(javafx.geometry.Pos.CENTER);

                List<ObservableList<PurchaseHistory>> history = db
                                .selectOrderDetails(db.getCustomerIdFromUsername(userName));

                if (history != null && !history.isEmpty()) {
                        tableView.setItems(history.get(historyCurrentIndex));
                        orderNumberLabel.setText(
                                        "Order Number: " + history.get(historyCurrentIndex).get(0).getOrderID());
                        totalPriceField.setText(Double.toString(
                                        db.getOrderAmount(history.get(historyCurrentIndex).get(0).getOrderID())));
                } else {
                        orderNumberLabel.setText("Order Number: N/A");
                        totalPriceField.setText("0.0");
                }

                // Navigation Buttons
                Button nextButton = new Button();
                nextButton.setLayoutX(696);
                nextButton.setLayoutY(194);
                nextButton.setStyle("-fx-background-color: transparent;");
                ImageView greyNextImageView = new ImageView(new Image("rightGrey.png"));
                greyNextImageView.setFitWidth(70);
                greyNextImageView.setFitHeight(70);
                ImageView nextImageView = new ImageView(new Image("right.png"));
                nextImageView.setFitWidth(70);
                nextImageView.setFitHeight(70);
                nextButton.setGraphic(nextImageView);
                nextButton.setOnMouseEntered(e -> nextButton.setGraphic(greyNextImageView));
                nextButton.setOnMouseExited(e -> nextButton.setGraphic(nextImageView));
                nextButton.setOnAction(event -> {
                        if (historyCurrentIndex < history.size() - 1) {
                                historyCurrentIndex++;
                                ObservableList<PurchaseHistory> currentOrder = history.get(historyCurrentIndex);
                                tableView.setItems(currentOrder);
                                orderNumberLabel.setText("Order Number: " + currentOrder.get(0).getOrderID());
                                totalPriceField.setText(Double.toString(db
                                                .getOrderAmount(history.get(historyCurrentIndex).get(0).getOrderID())));
                        }
                });

                Button prevButton = new Button();
                prevButton.setLayoutX(7);
                prevButton.setLayoutY(195);
                prevButton.setStyle("-fx-background-color: transparent;");
                ImageView greyPrevImageView = new ImageView(new Image("leftGrey.png"));
                greyPrevImageView.setFitWidth(70);
                greyPrevImageView.setFitHeight(70);
                ImageView prevImageView = new ImageView(new Image("left.png"));
                prevImageView.setFitWidth(70);
                prevImageView.setFitHeight(70);
                prevButton.setGraphic(prevImageView);
                prevButton.setOnMouseEntered(e -> prevButton.setGraphic(greyPrevImageView));
                prevButton.setOnMouseExited(e -> prevButton.setGraphic(prevImageView));
                prevButton.setOnAction(event -> {
                        if (historyCurrentIndex > 0) {
                                historyCurrentIndex--;
                                ObservableList<PurchaseHistory> currentOrder = history.get(historyCurrentIndex);
                                tableView.setItems(currentOrder);
                                orderNumberLabel.setText("Order Number: " + currentOrder.get(0).getOrderID());
                                totalPriceField.setText(Double.toString(db
                                                .getOrderAmount(history.get(historyCurrentIndex).get(0).getOrderID())));
                        }
                });

                // Add the table view to the main pane
                mainPane.getChildren().addAll(tableView, orderNumberLabel, totalPriceLabel, totalPriceField, nextButton,
                                prevButton);

                // Add panes to the root pane
                root.getChildren().addAll(topPane, mainPane);

                // Set up the stage and scene
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                primaryStage.setTitle("JavaFX Example");
                primaryStage.show();
        }

        private void createCustomerSettingsPage(Stage primaryStage) {

                // Root Pane
                Pane root = new Pane();
                root.setPrefSize(811, 497);
                root.setStyle("-fx-background-color: D4EBF8;");

                // Top Pane
                Pane topPane = new Pane();
                topPane.setLayoutX(11);
                topPane.setLayoutY(14);
                topPane.setPrefSize(790, 48);
                topPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Logo Image
                ImageView logo = new ImageView(new Image("downloadsmallLogo.png"));
                logo.setLayoutX(30);

                // Navigation Buttons
                Button productsButton = new Button("Products");
                productsButton.setLayoutX(181);
                productsButton.setLayoutY(1);
                productsButton.setPrefSize(113, 48);
                productsButton.setStyle("-fx-background-color: transparent;");
                productsButton.setFont(new Font("System Bold", 12));
                productsButton.setOnAction(e -> createCustomerPage(primaryStage));
                addTopButtonsEffect(productsButton);

                Button purchaseHistoryButton = new Button("Purchase History");
                purchaseHistoryButton.setLayoutX(495);
                purchaseHistoryButton.setLayoutY(1);
                purchaseHistoryButton.setPrefSize(113, 48);
                purchaseHistoryButton.setStyle("-fx-background-color: transparent;");
                purchaseHistoryButton.setFont(new Font("System Bold", 12));
                purchaseHistoryButton.setOnAction(e -> createCustomerPurchaseHistoryPage(primaryStage));
                addTopButtonsEffect(purchaseHistoryButton);

                Button settingsButton = new Button("Settings");
                settingsButton.setLayoutX(645);
                settingsButton.setLayoutY(1);
                settingsButton.setPrefSize(113, 48);
                settingsButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;");
                settingsButton.setFont(new Font("System Bold", 12));
                settingsButton.setOnMouseEntered(e -> settingsButton.setStyle(
                                "-fx-background-color: F2F9FF; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));
                settingsButton.setOnMouseExited(e -> settingsButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));

                Button cartButton = new Button("Cart");
                cartButton.setLayoutX(338);
                cartButton.setLayoutY(1);
                cartButton.setPrefSize(113, 48);
                cartButton.setStyle("-fx-background-color: transparent;");
                cartButton.setFont(new Font("System Bold", 12));
                cartButton.setOnAction(e -> createCustomerCartPage(primaryStage));
                addTopButtonsEffect(cartButton);

                topPane.getChildren().addAll(logo, productsButton, purchaseHistoryButton, settingsButton, cartButton);

                // Main Pane
                Pane mainPane = new Pane();
                mainPane.setLayoutX(11);
                mainPane.setLayoutY(69);
                mainPane.setPrefSize(790, 416);
                mainPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // User Info Pane
                Pane userInfoPane = new Pane();
                userInfoPane.setLayoutX(21);
                userInfoPane.setLayoutY(14);
                userInfoPane.setPrefSize(239, 388);
                userInfoPane.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;");

                // Labels and Text Fields
                Label titleLabel = new Label("User Informations");
                titleLabel.setLayoutX(43);
                titleLabel.setLayoutY(14);
                titleLabel.setFont(new Font("System Bold", 18));

                Label nameLabel = new Label("Name");
                nameLabel.setLayoutX(46);
                nameLabel.setLayoutY(57);
                nameLabel.setFont(new Font("System Bold Italic", 12));

                Label emailLabel = new Label("Email");
                emailLabel.setLayoutX(49);
                emailLabel.setLayoutY(108);
                emailLabel.setFont(new Font("System Bold Italic", 12));

                Label contactLabel = new Label("Contact Number");
                contactLabel.setLayoutX(45);
                contactLabel.setLayoutY(164);
                contactLabel.setFont(new Font("System Bold Italic", 12));

                Label oldPasswordLabel = new Label("Old Password");
                oldPasswordLabel.setLayoutX(46);
                oldPasswordLabel.setLayoutY(222);
                oldPasswordLabel.setFont(new Font("System Bold Italic", 12));

                Label newPasswordLabel = new Label("New Password");
                newPasswordLabel.setLayoutX(45);
                newPasswordLabel.setLayoutY(279);
                newPasswordLabel.setFont(new Font("System Bold Italic", 12));

                TextField nameField = new TextField();
                nameField.setLayoutX(40);
                nameField.setLayoutY(74);
                nameField.setPrefSize(153, 24);
                nameField.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                nameField.setFont(new Font("System Bold", 12));
                nameField.setText(db.getCustomerInfo(db.getCustomerIdFromUsername(userName)).getName());

                TextField emailField = new TextField();
                emailField.setLayoutX(41);
                emailField.setLayoutY(125);
                emailField.setPrefSize(153, 24);
                emailField.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                emailField.setFont(new Font("System Bold", 12));
                emailField.setText(db.getCustomerInfo(db.getCustomerIdFromUsername(userName)).getEmail());

                TextField contactField = new TextField();
                contactField.setLayoutX(40);
                contactField.setLayoutY(182);
                contactField.setPrefSize(153, 24);
                contactField.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                contactField.setFont(new Font("System Bold", 12));
                contactField.setText(db.getCustomerInfo(db.getCustomerIdFromUsername(userName)).getContact());

                TextField oldPasswordField = new TextField();
                oldPasswordField.setLayoutX(40);
                oldPasswordField.setLayoutY(239);
                oldPasswordField.setPrefSize(153, 24);
                oldPasswordField.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                oldPasswordField.setFont(new Font("System Bold", 12));

                TextField newPasswordField = new TextField();
                newPasswordField.setLayoutX(40);
                newPasswordField.setLayoutY(296);
                newPasswordField.setPrefSize(153, 24);
                newPasswordField.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                newPasswordField.setFont(new Font("System Bold", 12));

                Button saveButton = new Button("Save");
                saveButton.setLayoutX(83);
                saveButton.setLayoutY(342);
                saveButton.setPrefSize(69, 32);
                saveButton.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;");
                saveButton.setFont(new Font("System Bold", 13));
                saveButton.setOnMouseEntered(e -> saveButton.setStyle(
                                "-fx-background-color: white; -fx-background-radius: 20; -fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;"));
                saveButton.setOnMouseExited(e -> saveButton.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;"));

                saveButton.setOnAction(event -> {
                        if (oldPasswordField.getText() != null && !oldPasswordField.getText().isEmpty() &&
                                        nameField.getText() != null && !nameField.getText().isEmpty() &&
                                        contactField.getText() != null && !contactField.getText().isEmpty()) {

                                if (db.checkLoginCredentials(userName, oldPasswordField.getText())) {
                                        int customerID = db.getCustomerIdFromUsername(userName);

                                        boolean updateSuccess = db.updateCustomerInfo(
                                                        customerID,
                                                        nameField.getText(),
                                                        emailField.getText(),
                                                        contactField.getText(),
                                                        oldPasswordField.getText(),
                                                        newPasswordField.getText());

                                        if (updateSuccess) {
                                                showAlert(AlertType.INFORMATION, "Update Successful",
                                                                "Customer information has been updated.");
                                        } else {
                                                showAlert(AlertType.ERROR, "Update Failed",
                                                                "An error occurred while updating customer information.");
                                        }
                                } else {
                                        showAlert(AlertType.INFORMATION, "Wrong Password",
                                                        "Wrong password, please try again.");
                                }
                        } else {
                                showAlert(AlertType.INFORMATION, "Missing Required Fields",
                                                "The Name, Contact Number, and Old Password are required fields.");
                        }
                });

                // Required indicators
                Label nameRequired = new Label("*");
                nameRequired.setLayoutX(83);
                nameRequired.setLayoutY(57);
                nameRequired.setFont(new Font(13));
                nameRequired.setStyle("-fx-text-fill: RED;");

                Label contactRequired = new Label("*");
                contactRequired.setLayoutX(138);
                contactRequired.setLayoutY(164);
                contactRequired.setFont(new Font(13));
                contactRequired.setStyle("-fx-text-fill: RED;");

                Label oldPasswordRequired = new Label("*");
                oldPasswordRequired.setLayoutX(123);
                oldPasswordRequired.setLayoutY(222);
                oldPasswordRequired.setFont(new Font(13));
                oldPasswordRequired.setStyle("-fx-text-fill: RED;");

                userInfoPane.getChildren().addAll(titleLabel, nameLabel, nameRequired, emailLabel, contactLabel,
                                contactRequired, oldPasswordLabel, oldPasswordRequired, newPasswordLabel, nameField,
                                emailField, contactField, oldPasswordField, newPasswordField, saveButton);

                Button logOutButton = new Button("Log Out");
                logOutButton.setLayoutX(630);
                logOutButton.setLayoutY(331);
                logOutButton.setFont(new Font("System Bold", 15));
                ImageView logOutImageView = new ImageView(new Image("exit.png"));
                logOutImageView.setFitWidth(40);
                logOutImageView.setFitHeight(40);
                logOutButton.setGraphic(logOutImageView);
                logOutButton.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;");
                logOutButton.setOnMouseEntered(e -> logOutButton.setStyle(
                                "-fx-background-color:  F2F9FF; -fx-background-radius: 20; -fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;"));
                logOutButton.setOnMouseExited(e -> logOutButton.setStyle(
                                "-fx-background-color:  transparent; -fx-background-radius: 20; -fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;"));
                logOutButton.setOnAction(e -> {
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation Dialog");
                        alert.setContentText("Are you sure you want to log out?");
                        alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                        CreateLoginPage(primaryStage);
                                }
                        });
                });

                mainPane.getChildren().addAll(userInfoPane, logOutButton);

                // Add everything to root
                root.getChildren().addAll(topPane, mainPane);

                // Set up the stage
                primaryStage.setScene(new Scene(root));
                primaryStage.setTitle("User Information");
                primaryStage.show();
        }

        private void createEmployeeOrdersPage(Stage primaryStage) {

                // Root Pane
                Pane root = new Pane();
                root.setPrefSize(811, 497);
                root.setStyle("-fx-background-color: D4EBF8;");

                // Top Pane (Header)
                Pane headerPane = new Pane();
                headerPane.setLayoutX(11);
                headerPane.setLayoutY(14);
                headerPane.setPrefSize(790, 48);
                headerPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Logo ImageView
                ImageView logoImageView = new ImageView(new Image("downloadsmallLogo.png"));
                logoImageView.setLayoutX(30);

                // Buttons in Header Pane
                dashboardButton = new Button("Dashboard");
                dashboardButton.setLayoutX(136);
                dashboardButton.setLayoutY(1);
                dashboardButton.setPrefSize(78, 48);
                dashboardButton.setStyle("-fx-background-color: transparent;");
                dashboardButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(dashboardButton);
                dashboardButton.setOnAction(e -> createDashboardPage(primaryStage));

                try {
                        if (db.isEmployeeManager(db.getEmployeeIdFromUsername(userName))) {

                        } else {
                                dashboardButton.setVisible(false);
                        }
                } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }

                Button productsButton = new Button("Products");
                productsButton.setLayoutX(289);
                productsButton.setPrefSize(66, 48);
                productsButton.setStyle("-fx-background-color: transparent;");
                productsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(productsButton);
                productsButton.setOnAction(e -> CreateProductsPage(primaryStage));

                Button ordersButton = new Button("Orders");
                ordersButton.setLayoutX(419);
                ordersButton.setPrefSize(76, 48);
                ordersButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;");
                ordersButton.setFont(new Font("System Bold", 12));
                ordersButton.setOnMouseEntered(e -> ordersButton.setStyle(
                                "-fx-background-color: F2F9FF; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));
                ordersButton.setOnMouseExited(e -> ordersButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));

                Button customersButton = new Button("Customers");
                customersButton.setLayoutX(547);
                customersButton.setPrefSize(76, 48);
                customersButton.setStyle("-fx-background-color: transparent;");
                customersButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(customersButton);
                customersButton.setOnAction(e -> createEmployeeCustomersPage(primaryStage));

                Button settingsButton = new Button("Settings");
                settingsButton.setLayoutX(679);
                settingsButton.setLayoutY(1);
                settingsButton.setPrefSize(66, 48);
                settingsButton.setStyle("-fx-background-color: transparent;");
                settingsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(settingsButton);
                settingsButton.setOnAction(e -> createEmployeeSettingsPage(primaryStage));

                // Add components to Header Pane
                headerPane.getChildren().addAll(logoImageView, dashboardButton, productsButton, ordersButton,
                                customersButton, settingsButton);

                // Content Pane
                Pane contentPane = new Pane();
                contentPane.setLayoutX(11);
                contentPane.setLayoutY(69);
                contentPane.setPrefSize(790, 416);
                contentPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // TableView
                EmployeeOrdersTableView.setLayoutX(40);
                EmployeeOrdersTableView.setLayoutY(50);
                EmployeeOrdersTableView.setPrefSize(710, 263);

                // Columns for TableView
                TableColumn<EmployeeOrders, String> orderNumberColumn = new TableColumn<>("Order Number");
                orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderID"));
                orderNumberColumn.setPrefWidth(101.49);

                TableColumn<EmployeeOrders, String> customerNameColumn = new TableColumn<>("Customer Name");
                customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
                customerNameColumn.setPrefWidth(122.51);

                TableColumn<EmployeeOrders, String> contactNumberColumn = new TableColumn<>("Contact Number");
                contactNumberColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
                contactNumberColumn.setPrefWidth(122.51);

                TableColumn<EmployeeOrders, String> emailColumn = new TableColumn<>("Email");
                emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
                emailColumn.setPrefWidth(97.83);

                TableColumn<EmployeeOrders, String> totalAmountColumn = new TableColumn<>("Total Amount");
                totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
                totalAmountColumn.setPrefWidth(91.43);

                TableColumn<EmployeeOrders, String> orderDateColumn = new TableColumn<>("Order Date");
                orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
                orderDateColumn.setPrefWidth(94.17);

                TableColumn<EmployeeOrders, String> statusColumn = new TableColumn<>("Status");
                statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
                statusColumn.setPrefWidth(79.54);

                // Add Columns to TableView
                EmployeeOrdersTableView.getColumns().addAll(orderNumberColumn, customerNameColumn, contactNumberColumn,
                                emailColumn,
                                totalAmountColumn, orderDateColumn, statusColumn);

                EmployeeOrdersTableView.setItems(db.LoadOrders());

                // Order Details Button
                Button orderDetailsButton = new Button("Order Details");
                orderDetailsButton.setLayoutX(336);
                orderDetailsButton.setLayoutY(344);
                orderDetailsButton.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;");
                orderDetailsButton.setFont(new Font("System Bold", 15));
                orderDetailsButton.setOnAction(e -> {
                        EmployeeOrderSelected = EmployeeOrdersTableView.getSelectionModel().getSelectedItem();
                        if (EmployeeOrderSelected != null) {
                                createEmployeeOrderDetailsForm();
                        }
                });

                // Add components to Content Pane
                contentPane.getChildren().addAll(EmployeeOrdersTableView, orderDetailsButton);

                // Add Panes to Root
                root.getChildren().addAll(headerPane, contentPane);

                // Scene and Stage
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                primaryStage.setTitle("JavaFX Application");
                primaryStage.show();
        }

        private void createEmployeeOrderDetailsForm() {
                Stage stage = new Stage();
                Pane rootPane = new Pane();
                rootPane.setPrefSize(407, 437);
                rootPane.setStyle("-fx-background-color: D4EBF8;");

                // Inner Pane
                Pane innerPane = new Pane();
                innerPane.setLayoutX(18);
                innerPane.setLayoutY(15);
                innerPane.setPrefSize(372, 407);
                innerPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // TableView
                TableView<EmployeeOrderDetails> tableView = new TableView<>();
                tableView.setLayoutX(41);
                tableView.setLayoutY(71);
                tableView.setPrefSize(290, 251);

                // Table Columns
                TableColumn<EmployeeOrderDetails, String> productNameColumn = new TableColumn<>("Product Name");
                productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
                productNameColumn.setPrefWidth(196.57);

                TableColumn<EmployeeOrderDetails, Integer> quantityColumn = new TableColumn<>("Quantity");
                quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
                quantityColumn.setPrefWidth(90.51);

                tableView.getColumns().addAll(productNameColumn, quantityColumn);
                tableView.setItems(db.fetchOrderDetails(EmployeeOrderSelected.getOrderID()));

                // Label
                Label orderNumberLabel = new Label("Order Number: " + EmployeeOrderSelected.getOrderID());
                orderNumberLabel.setLayoutX(41);
                orderNumberLabel.setLayoutY(44);
                orderNumberLabel.setFont(new Font("System Bold", 13));

                // ComboBox
                ComboBox<String> statusComboBox = new ComboBox<>();
                statusComboBox.setLayoutX(60);
                statusComboBox.setLayoutY(349);
                statusComboBox.setPrefSize(115, 33);
                statusComboBox.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-width: 2; -fx-border-radius: 20; -fx-border-color: black;");
                statusComboBox.getItems().addAll("Pending", "Out For Delivery");
                statusComboBox.setValue("Pending");

                // Button
                Button updateStatusButton = new Button("Update Status");
                updateStatusButton.setLayoutX(186);
                updateStatusButton.setLayoutY(349);
                updateStatusButton.setPrefHeight(33);
                updateStatusButton.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;");
                updateStatusButton.setFont(new Font("System Bold", 14));
                updateStatusButton.setOnAction(e -> {
                        db.updateOrderStatus(EmployeeOrderSelected.getOrderID(), statusComboBox.getValue());
                        stage.close();
                });

                // Adding children to inner pane
                innerPane.getChildren().addAll(tableView, orderNumberLabel, statusComboBox, updateStatusButton);

                // Adding inner pane to root pane
                rootPane.getChildren().add(innerPane);

                // Setting up the stage
                Scene scene = new Scene(rootPane);
                stage.setScene(scene);
                stage.setTitle("Order Details");
                stage.show();
        }

        private void createEmployeeCustomersPage(Stage primaryStage) {

                // Main Pane
                Pane mainPane = new Pane();
                mainPane.setPrefSize(811, 497);
                mainPane.setStyle("-fx-background-color: D4EBF8;");

                // Top Navigation Pane
                Pane topPane = new Pane();
                topPane.setLayoutX(11);
                topPane.setLayoutY(14);
                topPane.setPrefSize(790, 48);
                topPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Logo ImageView
                ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("downloadsmallLogo.png")));
                logo.setLayoutX(30);
                topPane.getChildren().add(logo);

                // Navigation Buttons
                dashboardButton = new Button("Dashboard");
                dashboardButton.setLayoutX(136);
                dashboardButton.setLayoutY(1);
                dashboardButton.setPrefSize(78, 48);
                dashboardButton.setStyle("-fx-background-color: transparent;");
                dashboardButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(dashboardButton);
                dashboardButton.setOnAction(e -> createDashboardPage(primaryStage));

                try {
                        if (db.isEmployeeManager(db.getEmployeeIdFromUsername(userName))) {

                        } else {
                                dashboardButton.setVisible(false);
                        }
                } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }

                Button productsButton = new Button("Products");
                productsButton.setLayoutX(289);
                productsButton.setPrefSize(66, 48);
                productsButton.setStyle("-fx-background-color: transparent;");
                productsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(productsButton);
                productsButton.setOnAction(e -> CreateProductsPage(primaryStage));

                Button ordersButton = new Button("Orders");
                ordersButton.setLayoutX(419);
                ordersButton.setPrefSize(76, 48);
                ordersButton.setStyle("-fx-background-color: transparent;");
                ordersButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(ordersButton);
                ordersButton.setOnAction(e -> createEmployeeOrdersPage(primaryStage));

                Button customersButton = new Button("Customers");
                customersButton.setLayoutX(547);
                customersButton.setPrefSize(76, 48);
                customersButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;");
                customersButton.setFont(new Font("System Bold", 12));
                customersButton.setOnMouseEntered(e -> customersButton.setStyle(
                                "-fx-background-color: F2F9FF; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));
                customersButton.setOnMouseExited(e -> customersButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));

                Button settingsButton = new Button("Settings");
                settingsButton.setLayoutX(679);
                settingsButton.setPrefSize(66, 48);
                settingsButton.setStyle("-fx-background-color: transparent;");
                settingsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(settingsButton);
                settingsButton.setOnAction(e -> createEmployeeSettingsPage(primaryStage));

                topPane.getChildren().addAll(dashboardButton, productsButton, ordersButton, customersButton,
                                settingsButton);
                mainPane.getChildren().add(topPane);

                // Main Content Pane
                Pane contentPane = new Pane();
                contentPane.setLayoutX(11);
                contentPane.setLayoutY(69);
                contentPane.setPrefSize(790, 416);
                contentPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Customer TableView
                TableView<Customer> customerTable = new TableView<>();
                customerTable.setLayoutX(28);
                customerTable.setLayoutY(77);
                customerTable.setPrefSize(432, 263);

                TableColumn<Customer, String> customerNumberColumn = new TableColumn<>("Customer Number");
                customerNumberColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
                customerNumberColumn.setPrefWidth(114.2857);

                TableColumn<Customer, String> customerNameColumn = new TableColumn<>("Customer Name");
                customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                customerNameColumn.setPrefWidth(102.4);

                TableColumn<Customer, String> contactNumberColumn = new TableColumn<>("Contact Number");
                contactNumberColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
                contactNumberColumn.setPrefWidth(98.7429);

                TableColumn<Customer, String> emailColumn = new TableColumn<>("Email");
                emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
                emailColumn.setPrefWidth(113.3714);

                customerTable.getColumns().addAll(customerNumberColumn, customerNameColumn, contactNumberColumn,
                                emailColumn);

                try {
                        customerTable.setItems(db.getCustomerInfo());
                } catch (SQLException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                }

                // Order TableView
                TableView<EmployeeOrders> orderTable = new TableView<>();
                orderTable.setLayoutX(500);
                orderTable.setLayoutY(77);
                orderTable.setPrefSize(265, 263);

                TableColumn<EmployeeOrders, String> orderNumberColumn = new TableColumn<>("Order Number");
                orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderID"));
                orderNumberColumn.setPrefWidth(93.2571);

                TableColumn<EmployeeOrders, String> amountColumn = new TableColumn<>("Amount");
                amountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
                amountColumn.setPrefWidth(71.3143);

                TableColumn<EmployeeOrders, String> statusColumn = new TableColumn<>("Status");
                statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
                statusColumn.setPrefWidth(98.6);

                orderTable.getColumns().addAll(orderNumberColumn, amountColumn, statusColumn);

                customerTable.setOnMouseClicked(e -> {
                        Customer customer = customerTable.getSelectionModel().getSelectedItem();
                        if (customer != null) {
                                orderTable.setItems(db.fetchOrderStatus(customer.getCustomerID()));
                        }
                });

                // Search Pane
                Pane searchPane = new Pane();
                searchPane.setLayoutX(28);
                searchPane.setLayoutY(40);
                searchPane.setPrefSize(164, 30);
                searchPane.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;");

                ImageView searchIcon = new ImageView(new Image(getClass().getResourceAsStream("searchsmall.png")));
                searchIcon.setFitWidth(25);
                searchIcon.setFitHeight(25);
                searchIcon.setLayoutX(5);
                searchIcon.setLayoutY(3);

                TextField searchField = new TextField();
                searchField.setLayoutX(30);
                searchField.setLayoutY(3);
                searchField.setPrefSize(115, 13);
                searchField.setPromptText("Search....");
                searchField.setStyle("-fx-background-color: transparent;");
                searchField.setOnKeyPressed(e -> {
                        if (e.getCode() == KeyCode.ENTER) {
                                try {
                                        customerTable.setItems(db.searchCustomer(searchField.getText()));
                                } catch (SQLException e1) {
                                        // TODO Auto-generated catch block
                                        e1.printStackTrace();
                                }
                        }
                });

                searchPane.getChildren().addAll(searchIcon, searchField);

                contentPane.getChildren().addAll(customerTable, orderTable, searchPane);
                mainPane.getChildren().add(contentPane);

                // Set Scene
                Scene scene = new Scene(mainPane);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Customer and Order Management");
                primaryStage.show();
        }

        private void createEmployeeSettingsPage(Stage primaryStage) {
                Button showAllEmployeesButton=new Button();

                try {
                        if (db.isEmployeeManager(db.getEmployeeIdFromUsername(userName))) {

                        } else {
                                dashboardButton.setVisible(false);
                                showAllEmployeesButton.setVisible(false);
                        }
                } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                // Root Pane
                Pane root = new Pane();
                root.setPrefSize(811, 497);
                root.setStyle("-fx-background-color: D4EBF8;");

                // Top Pane
                Pane topPane = new Pane();
                topPane.setLayoutX(11);
                topPane.setLayoutY(14);
                topPane.setPrefSize(790, 48);
                topPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Adding children to Top Pane
                ImageView logo = new ImageView(new Image("downloadsmallLogo.png"));
                logo.setLayoutX(30);
                topPane.getChildren().add(logo);

                // Buttons in Header Pane
                dashboardButton.setLayoutX(136);
                dashboardButton.setLayoutY(1);
                dashboardButton.setPrefSize(78, 48);
                dashboardButton.setStyle("-fx-background-color: transparent;");
                dashboardButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(dashboardButton);
                dashboardButton.setOnAction(e -> createDashboardPage(primaryStage));

                Button productsButton = new Button("Products");
                productsButton.setLayoutX(289);
                productsButton.setPrefSize(66, 48);
                productsButton.setStyle(
                                "-fx-background-color: transparent;");
                productsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(productsButton);
                productsButton.setOnAction(e -> CreateProductsPage(primaryStage));

                Button ordersButton = new Button("Orders");
                ordersButton.setLayoutX(419);
                ordersButton.setPrefSize(76, 48);
                ordersButton.setStyle("-fx-background-color: transparent;");
                ordersButton.setFont(new Font("System Bold", 12));
                ordersButton.setOnAction(e -> createEmployeeOrdersPage(primaryStage));
                addTopButtonsEffect(ordersButton);

                Button customersButton = new Button("Customers");
                customersButton.setLayoutX(547);
                customersButton.setPrefSize(76, 48);
                customersButton.setStyle("-fx-background-color: transparent;");
                customersButton.setFont(new Font("System Bold", 12));
                customersButton.setOnAction(e -> createEmployeeCustomersPage(primaryStage));
                addTopButtonsEffect(customersButton);

                Button settingsButton = new Button("Settings");
                settingsButton.setLayoutX(679);
                settingsButton.setLayoutY(1);
                settingsButton.setPrefSize(66, 48);
                settingsButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;");
                settingsButton.setFont(new Font("System Bold", 12));
                settingsButton.setOnAction(e -> createEmployeeSettingsPage(primaryStage));
                settingsButton.setOnMouseEntered(e -> settingsButton.setStyle(
                                "-fx-background-color: F2F9FF; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));
                settingsButton.setOnMouseExited(e -> settingsButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));

                // Add all buttons to the top pane
                topPane.getChildren().addAll(dashboardButton, productsButton, ordersButton, customersButton,
                                settingsButton);

                // Main Pane
                Pane mainPane = new Pane();
                mainPane.setLayoutX(11);
                mainPane.setLayoutY(69);
                mainPane.setPrefSize(790, 416);
                mainPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // User Info Pane
                Pane userInfoPane = new Pane();
                userInfoPane.setLayoutX(21);
                userInfoPane.setLayoutY(14);
                userInfoPane.setPrefSize(356, 388);
                userInfoPane.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;");
                Label userInfoLabel = new Label(db.getEmployeeInfo(db.getEmployeeIdFromUsername(userName)).getUserName()
                                + " Informations");
                userInfoLabel.setLayoutX(109);
                userInfoLabel.setLayoutY(14);
                userInfoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");

                Label nameLabel = new Label("Name");
                nameLabel.setLayoutX(48);
                nameLabel.setLayoutY(78);
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-style: italic; -fx-font-size: 12;");

                Label emailLabel = new Label("Email");
                emailLabel.setLayoutX(48);
                emailLabel.setLayoutY(129);
                emailLabel.setStyle("-fx-font-weight: bold; -fx-font-style: italic; -fx-font-size: 12;");

                Label contactLabel = new Label("Contact Number");
                contactLabel.setLayoutX(45);
                contactLabel.setLayoutY(186);
                contactLabel.setStyle("-fx-font-weight: bold; -fx-font-style: italic; -fx-font-size: 12;");

                Label oldPasswordLabel = new Label("Old Password");
                oldPasswordLabel.setLayoutX(46);
                oldPasswordLabel.setLayoutY(243);
                oldPasswordLabel.setStyle("-fx-font-weight: bold; -fx-font-style: italic; -fx-font-size: 12;");

                Label newPasswordLabel = new Label("New Password");
                newPasswordLabel.setLayoutX(43);
                newPasswordLabel.setLayoutY(300);
                newPasswordLabel.setStyle("-fx-font-weight: bold; -fx-font-style: italic; -fx-font-size: 12;");

                TextField nameField = new TextField();
                nameField.setLayoutX(168);
                nameField.setLayoutY(74);
                nameField.setPrefSize(153, 24);
                nameField.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                nameField.setText(db.getEmployeeInfo(db.getEmployeeIdFromUsername(userName)).getName());

                TextField emailField = new TextField();
                emailField.setLayoutX(168);
                emailField.setLayoutY(125);
                emailField.setPrefSize(153, 24);
                emailField.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                TextField contactField = new TextField();
                contactField.setLayoutX(167);
                contactField.setLayoutY(182);
                contactField.setPrefSize(153, 24);
                contactField.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                contactField.setText(db.getEmployeeInfo(db.getEmployeeIdFromUsername(userName)).getPhoneNumber());

                TextField oldPasswordField = new TextField();
                oldPasswordField.setLayoutX(167);
                oldPasswordField.setLayoutY(239);
                oldPasswordField.setPrefSize(153, 24);
                oldPasswordField.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                TextField newPasswordField = new TextField();
                newPasswordField.setLayoutX(163);
                newPasswordField.setLayoutY(296);
                newPasswordField.setPrefSize(153, 24);
                newPasswordField.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                Button saveButton = new Button("Save");
                saveButton.setLayoutX(128);
                saveButton.setLayoutY(338);
                saveButton.setPrefSize(69, 32);
                saveButton.setFont(new Font("System Bold", 13));
                saveButton.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;");

                saveButton.setOnMouseEntered(
                                e -> saveButton.setStyle("-fx-background-color: white; -fx-background-radius: 20; "
                                                + "-fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;"));

                saveButton.setOnMouseExited(e -> saveButton
                                .setStyle("-fx-background-color: transparent; -fx-background-radius: 20; "
                                                + "-fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;"));
                saveButton.setOnAction(event -> {
                        if (oldPasswordField.getText() != null && !oldPasswordField.getText().isEmpty() &&
                                        nameField.getText() != null && !nameField.getText().isEmpty() &&
                                        contactField.getText() != null && !contactField.getText().isEmpty()) {

                                if (db.checkLoginCredentials(userName, oldPasswordField.getText())) {
                                        int employeeID = db.getEmployeeIdFromUsername(userName);

                                        boolean updateSuccess = db.updateEmployeeInfo(
                                                        employeeID,
                                                        nameField.getText(),
                                                        emailField.getText(),
                                                        contactField.getText(),
                                                        oldPasswordField.getText(),
                                                        newPasswordField.getText());

                                        if (updateSuccess) {
                                                showAlert(AlertType.INFORMATION, "Update Successful",
                                                                "Employee information has been updated.");
                                        } else {
                                                showAlert(AlertType.ERROR, "Update Failed",
                                                                "An error occurred while updating Employee information.");
                                        }
                                } else {
                                        showAlert(AlertType.INFORMATION, "Wrong Password",
                                                        "Wrong password, please try again.");
                                }
                        } else {
                                showAlert(AlertType.INFORMATION, "Missing Required Fields",
                                                "The Name, Contact Number, and Old Password are required fields.");
                        }
                });

                Label nameAsterisk = new Label("*");
                nameAsterisk.setLayoutX(82);
                nameAsterisk.setLayoutY(78);
                nameAsterisk.setStyle("-fx-text-fill: RED;");

                Label contactAsterisk = new Label("*");
                contactAsterisk.setLayoutX(138);
                contactAsterisk.setLayoutY(186);
                contactAsterisk.setStyle("-fx-text-fill: RED;");

                Label passwordAsterisk = new Label("*");
                passwordAsterisk.setLayoutX(123);
                passwordAsterisk.setLayoutY(243);
                passwordAsterisk.setStyle("-fx-text-fill: RED;");

                userInfoPane.getChildren().addAll(userInfoLabel, nameLabel, emailLabel, contactLabel, oldPasswordLabel,
                                newPasswordLabel, nameField, emailField, contactField, oldPasswordField,
                                newPasswordField, saveButton, nameAsterisk, contactAsterisk, passwordAsterisk);

                // Employee Pane
                Pane employeePane = new Pane();
                employeePane.setLayoutX(413);
                employeePane.setLayoutY(14);
                employeePane.setPrefSize(356, 304);
                employeePane.setStyle("-fx-background-color: D4EBF8; -fx-background-radius: 20;");

                // Title Label
                Label titleLabel = new Label("Insert Employee");
                titleLabel.setLayoutX(110);
                titleLabel.setLayoutY(14);
                titleLabel.setFont(new Font("System Bold", 18));
                employeePane.getChildren().add(titleLabel);

                // Labels
                Label EmployeeNameLabel = new Label("Name");
                EmployeeNameLabel.setLayoutX(43);
                EmployeeNameLabel.setLayoutY(78);
                EmployeeNameLabel.setFont(new Font("System Bold Italic", 12));
                employeePane.getChildren().add(EmployeeNameLabel);

                Label usernameLabel = new Label("UserName");
                usernameLabel.setLayoutX(40);
                usernameLabel.setLayoutY(120);
                usernameLabel.setFont(new Font("System Bold Italic", 12));
                employeePane.getChildren().add(usernameLabel);

                Label phoneLabel = new Label("Phone Number");
                phoneLabel.setLayoutX(40);
                phoneLabel.setLayoutY(167);
                phoneLabel.setFont(new Font("System Bold Italic", 12));
                employeePane.getChildren().add(phoneLabel);

                Label passwordLabel = new Label("Password");
                passwordLabel.setLayoutX(42);
                passwordLabel.setLayoutY(211);
                passwordLabel.setFont(new Font("System Bold Italic", 12));
                employeePane.getChildren().add(passwordLabel);

                // TextFields
                TextField EmployeeNameField = new TextField();
                EmployeeNameField.setLayoutX(168);
                EmployeeNameField.setLayoutY(74);
                EmployeeNameField.setPrefSize(153, 24);
                EmployeeNameField.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                employeePane.getChildren().add(EmployeeNameField);

                TextField usernameField = new TextField();
                usernameField.setLayoutX(168);
                usernameField.setLayoutY(116);
                usernameField.setPrefSize(153, 24);
                usernameField.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                employeePane.getChildren().add(usernameField);

                TextField phoneField = new TextField();
                phoneField.setLayoutX(167);
                phoneField.setLayoutY(163);
                phoneField.setPrefSize(153, 24);
                phoneField.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                employeePane.getChildren().add(phoneField);

                TextField passwordField = new TextField();
                passwordField.setLayoutX(167);
                passwordField.setLayoutY(207);
                passwordField.setPrefSize(153, 24);
                passwordField.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
                employeePane.getChildren().add(passwordField);

                // Insert Button
                Button insertButton = new Button("Insert");
                insertButton.setLayoutX(134);
                insertButton.setLayoutY(260);
                insertButton.setPrefSize(69, 32);
                insertButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 20; "
                                + "-fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;");

                insertButton.setOnMouseEntered(
                                e -> insertButton.setStyle("-fx-background-color: white; -fx-background-radius: 20; "
                                                + "-fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;"));

                insertButton.setOnMouseExited(e -> insertButton
                                .setStyle("-fx-background-color: transparent; -fx-background-radius: 20; "
                                                + "-fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;"));
                insertButton.setFont(new Font("System Bold", 13));
                employeePane.getChildren().add(insertButton);
                insertButton.setOnAction(e -> {
                        String employeeName = EmployeeNameField.getText();
                        String username = usernameField.getText();
                        String phone = phoneField.getText();
                        String password = passwordField.getText();
                        if (employeeName.isEmpty() || username.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                        } else {
                                Alert alert = new Alert(AlertType.CONFIRMATION);
                                alert.setTitle("Confirmation");
                                alert.setContentText("Are you sure you want to insert this employee?");
                                alert.showAndWait().ifPresent(response -> {
                                        if (response == ButtonType.OK) {
                                                db.registerEmployee(employeeName, username, password, phone, "Employee",
                                                                password);
                                        }
                                });

                        }

                });

                try {
                        if (db.isEmployeeManager(db.getEmployeeIdFromUsername(userName))) {

                        } else {
                                employeePane.setVisible(false);
                        }
                } catch (SQLException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                }
                showAllEmployeesButton.setText("Show All Employees");
                showAllEmployeesButton.setLayoutX(450);
                showAllEmployeesButton.setLayoutY(347);
                showAllEmployeesButton.setPrefSize(180, 55);
                showAllEmployeesButton.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;");
                showAllEmployeesButton.setFont(new Font("System Bold", 15));
                addButtonEffect(showAllEmployeesButton);
                showAllEmployeesButton.setOnAction(e -> createManagerShowEmployeesForm());

                Button logOutButton = new Button("Log Out");
                logOutButton.setLayoutX(644);
                logOutButton.setLayoutY(347);
                logOutButton.setFont(new Font("System Bold", 15));
                ImageView logOutImageView = new ImageView(new Image("exit.png"));
                logOutImageView.setFitWidth(40);
                logOutImageView.setFitHeight(40);
                logOutButton.setGraphic(logOutImageView);
                logOutButton.setStyle(
                                "-fx-background-color: transparent; -fx-background-radius: 20; -fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;");
                logOutButton.setOnMouseEntered(e -> logOutButton.setStyle(
                                "-fx-background-color:  F2F9FF; -fx-background-radius: 20; -fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;"));
                logOutButton.setOnMouseExited(e -> logOutButton.setStyle(
                                "-fx-background-color:  transparent; -fx-background-radius: 20; -fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;"));
                logOutButton.setOnAction(e -> {
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation Dialog");
                        alert.setContentText("Are you sure you want to log out?");
                        alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                        CreateLoginPage(primaryStage);
                                }
                        });
                });

                mainPane.getChildren().addAll(userInfoPane, employeePane, logOutButton, showAllEmployeesButton);

                // Add to root
                root.getChildren().addAll(topPane, mainPane);

                // Scene and Stage
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Employee Settings");
                primaryStage.show();
        }

        private void createDashboardPage(Stage primaryStage) {
                Pane root = new Pane();
                root.setPrefSize(811, 497);
                root.setStyle("-fx-background-color: D4EBF8;");

                // Top Pane
                Pane topPane = new Pane();
                topPane.setLayoutX(11);
                topPane.setLayoutY(14);
                topPane.setPrefSize(790, 48);
                topPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("downloadsmallLogo.png")));
                logo.setLayoutX(30);
                topPane.getChildren().add(logo);

                dashboardButton = new Button("Dashboard");
                dashboardButton.setLayoutX(136);
                dashboardButton.setPrefSize(78, 48);
                dashboardButton.setFont(new Font("System Bold", 12));
                dashboardButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-color: transparent transparent black transparent; -fx-border-width: 0 0 2 0;");
                dashboardButton.setOnMouseEntered(e -> dashboardButton.setStyle(
                                "-fx-background-color: F2F9FF; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));
                dashboardButton.setOnMouseExited(e -> dashboardButton.setStyle(
                                "-fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: transparent transparent black transparent;"));

                topPane.getChildren().add(dashboardButton);

                Button productsButton = new Button("Products");
                productsButton.setLayoutX(289);
                productsButton.setPrefSize(66, 48);
                productsButton.setStyle("-fx-background-color: transparent;");
                productsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(productsButton);
                productsButton.setOnAction(e -> CreateProductsPage(primaryStage));
                topPane.getChildren().add(productsButton);

                Button ordersButton = new Button("Orders");
                ordersButton.setLayoutX(419);
                ordersButton.setPrefSize(76, 48);
                ordersButton.setFont(new Font("System Bold", 12));
                ordersButton.setStyle("-fx-background-color: transparent;");
                addTopButtonsEffect(ordersButton);
                ordersButton.setOnAction(e -> createEmployeeOrdersPage(primaryStage));
                topPane.getChildren().add(ordersButton);

                Button customersButton = new Button("Customers");
                customersButton.setLayoutX(547);
                customersButton.setPrefSize(76, 48);
                customersButton.setStyle("-fx-background-color: transparent;");
                customersButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(customersButton);
                customersButton.setOnAction(e -> createEmployeeCustomersPage(primaryStage));
                topPane.getChildren().add(customersButton);

                Button settingsButton = new Button("Settings");
                settingsButton.setLayoutX(679);
                settingsButton.setPrefSize(66, 48);
                settingsButton.setStyle("-fx-background-color: transparent;");
                settingsButton.setFont(new Font("System Bold", 12));
                addTopButtonsEffect(settingsButton);
                settingsButton.setOnAction(e -> createEmployeeSettingsPage(primaryStage));
                topPane.getChildren().add(settingsButton);

                root.getChildren().add(topPane);

                // Main Pane
                Pane mainPane = new Pane();
                mainPane.setLayoutX(11);
                mainPane.setLayoutY(69);
                mainPane.setPrefSize(790, 416);
                mainPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // Sales Line Chart
                CategoryAxis xAxis = new CategoryAxis();
                // xAxis.setLabel("Month-Year");

                NumberAxis yAxis = new NumberAxis();
                // yAxis.setLabel("Total Sales");

                LineChart<String, Number> salesChart = new LineChart<>(xAxis, yAxis);
                salesChart.setLayoutX(14);
                salesChart.setLayoutY(160);
                salesChart.setPrefSize(410, 244);
                salesChart.setTitle("Sales");
                salesChart.setStyle("-fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;");

                // Get the chart data from the database
                ObservableList<XYChart.Data<String, Double>> chartData = db.getMonthlySalesChartData();

                // Create a series for the chart
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Sales");

                // Add each data point from chartData to the series
                for (XYChart.Data<String, Double> dataPoint : chartData) {
                        series.getData().add(new XYChart.Data<>(dataPoint.getXValue(), dataPoint.getYValue()));
                }

                // Add the series to the chart
                salesChart.getData().add(series);

                // Add the chart to the main pane
                mainPane.getChildren().add(salesChart);

                // Total Sales Pane
                Pane totalSalesPane = new Pane();
                totalSalesPane.setLayoutX(14);
                totalSalesPane.setLayoutY(14);
                totalSalesPane.setPrefSize(223, 117);
                totalSalesPane.setStyle("-fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;");

                ImageView totalSalesIcon = new ImageView(new Image(getClass().getResourceAsStream("money-bag.png")));
                totalSalesIcon.setFitHeight(35);
                totalSalesIcon.setFitWidth(35);
                totalSalesIcon.setLayoutX(14);
                totalSalesIcon.setLayoutY(14);
                totalSalesPane.getChildren().add(totalSalesIcon);

                Label totalSalesLabel = new Label("Total Sales");
                totalSalesLabel.setLayoutX(60);
                totalSalesLabel.setLayoutY(18);
                totalSalesLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
                totalSalesPane.getChildren().add(totalSalesLabel);

                TextField totalSalesField = new TextField();
                totalSalesField.setEditable(false);
                totalSalesField.setLayoutX(22);
                totalSalesField.setLayoutY(58);
                totalSalesField.setPrefSize(166, 35);
                totalSalesField.setStyle(
                                "-fx-background-color: transparent; -fx-font-size: 18px; -fx-font-weight: bold;");
                totalSalesPane.getChildren().add(totalSalesField);
                totalSalesField.setText("₪" + Double.toString(db.getTotalSales()));

                mainPane.getChildren().add(totalSalesPane);

                // Total Orders Pane
                Pane totalOrdersPane = new Pane();
                totalOrdersPane.setLayoutX(284);
                totalOrdersPane.setLayoutY(14);
                totalOrdersPane.setPrefSize(223, 117);
                totalOrdersPane.setStyle("-fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;");

                ImageView totalOrdersIcon = new ImageView(new Image(getClass().getResourceAsStream("box.png")));
                totalOrdersIcon.setFitHeight(35);
                totalOrdersIcon.setFitWidth(35);
                totalOrdersIcon.setLayoutX(14);
                totalOrdersIcon.setLayoutY(14);
                totalOrdersPane.getChildren().add(totalOrdersIcon);

                Label totalOrdersLabel = new Label("Total Orders");
                totalOrdersLabel.setLayoutX(60);
                totalOrdersLabel.setLayoutY(18);
                totalOrdersLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
                totalOrdersPane.getChildren().add(totalOrdersLabel);

                TextField totalOrdersField = new TextField();
                totalOrdersField.setEditable(false);
                totalOrdersField.setLayoutX(22);
                totalOrdersField.setLayoutY(58);
                totalOrdersField.setPrefSize(166, 35);
                totalOrdersField.setStyle(
                                "-fx-background-color: transparent; -fx-font-size: 18px; -fx-font-weight: bold;");
                totalOrdersPane.getChildren().add(totalOrdersField);
                totalOrdersField.setText(String.valueOf(db.getTotalOrders()));

                mainPane.getChildren().add(totalOrdersPane);

                // Active Orders Pane
                Pane activeOrdersPane = new Pane();
                activeOrdersPane.setLayoutX(553);
                activeOrdersPane.setLayoutY(14);
                activeOrdersPane.setPrefSize(223, 117);
                activeOrdersPane.setStyle("-fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 2;");

                ImageView activeOrdersIcon = new ImageView(new Image(getClass().getResourceAsStream("shiping.png")));
                activeOrdersIcon.setFitHeight(40);
                activeOrdersIcon.setFitWidth(40);
                activeOrdersIcon.setLayoutX(14);
                activeOrdersIcon.setLayoutY(10);
                activeOrdersPane.getChildren().add(activeOrdersIcon);

                Label activeOrdersLabel = new Label("Active Orders");
                activeOrdersLabel.setLayoutX(60);
                activeOrdersLabel.setLayoutY(18);
                activeOrdersLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
                activeOrdersPane.getChildren().add(activeOrdersLabel);

                TextField activeOrdersField = new TextField();
                activeOrdersField.setEditable(false);
                activeOrdersField.setLayoutX(22);
                activeOrdersField.setLayoutY(58);
                activeOrdersField.setPrefSize(166, 35);
                activeOrdersField.setStyle(
                                "-fx-background-color: transparent; -fx-font-size: 18px; -fx-font-weight: bold;");
                activeOrdersPane.getChildren().add(activeOrdersField);
                activeOrdersField.setText(String.valueOf(db.getActiveOrders()));

                mainPane.getChildren().add(activeOrdersPane);

                // Top Products Table
                TableView<Product> topProductsTable = new TableView<>();
                topProductsTable.setLayoutX(439);
                topProductsTable.setLayoutY(160);
                topProductsTable.setPrefSize(337, 107);
                topProductsTable.setStyle("-fx-border-color: black; -fx-border-width: 2;");

                TableColumn<Product, String> productColumn = new TableColumn<>("Product");
                productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
                productColumn.setPrefWidth(137);
                productColumn.setStyle("-fx-background-color: white;");

                TableColumn<Product, String> priceColumn = new TableColumn<>("Price");
                priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
                priceColumn.setPrefWidth(74);
                priceColumn.setStyle("-fx-background-color: white;");

                TableColumn<Product, String> soldColumn = new TableColumn<>("Sold");
                soldColumn.setCellValueFactory(new PropertyValueFactory<>("soldCount"));
                soldColumn.setPrefWidth(51);
                soldColumn.setStyle("-fx-background-color: white;");

                TableColumn<Product, String> salesColumn = new TableColumn<>("Sales");
                salesColumn.setCellValueFactory(new PropertyValueFactory<>("totalSales"));
                salesColumn.setPrefWidth(76);
                salesColumn.setStyle("-fx-background-color: white;");

                topProductsTable.getColumns().addAll(productColumn, priceColumn, soldColumn, salesColumn);
                topProductsTable.setItems(db.findTopThreeFrequentProducts());
                mainPane.getChildren().add(topProductsTable);

                // Top Customers Table
                TableView<Customer> topCustomersTable = new TableView<>();
                topCustomersTable.setLayoutX(439);
                topCustomersTable.setLayoutY(295);
                topCustomersTable.setPrefSize(337, 107);
                topCustomersTable.setStyle("-fx-border-color: black; -fx-border-width: 2;");

                TableColumn<Customer, String> customerColumn = new TableColumn<>("Customer");
                customerColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                customerColumn.setPrefWidth(195);
                customerColumn.setStyle("-fx-background-color: white;");

                TableColumn<Customer, String> customerSalesColumn = new TableColumn<>("Sales");
                customerSalesColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
                customerSalesColumn.setPrefWidth(142);
                customerSalesColumn.setStyle("-fx-background-color: white;");

                topCustomersTable.getColumns().addAll(customerColumn, customerSalesColumn);
                topCustomersTable.setItems(db.findTopThreeFrequentCustomers());
                mainPane.getChildren().add(topCustomersTable);

                Label topProductsLabel = new Label("Top Products");
                topProductsLabel.setLayoutX(439);
                topProductsLabel.setLayoutY(141);
                topProductsLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold italic;");
                mainPane.getChildren().add(topProductsLabel);

                Label topCustomersLabel = new Label("Top Customers");
                topCustomersLabel.setLayoutX(441);
                topCustomersLabel.setLayoutY(276);
                topCustomersLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold italic;");
                mainPane.getChildren().add(topCustomersLabel);

                root.getChildren().add(mainPane);

                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Dashboard");
                primaryStage.show();
        }

        private void createManagerShowEmployeesForm() {
                Stage stage = new Stage();
                Pane mainPane = new Pane();
                mainPane.setPrefSize(550, 381);
                mainPane.setStyle("-fx-background-color: D4EBF8;");

                // Inner pane with rounded white background
                Pane innerPane = new Pane();
                innerPane.setLayoutX(16);
                innerPane.setLayoutY(14);
                innerPane.setPrefSize(519, 354);
                innerPane.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

                // TableView
                TableView<Employee> tableView = new TableView<>();
                tableView.setLayoutX(21);
                tableView.setLayoutY(70);
                tableView.setPrefSize(476, 257);

                // Define table columns
                TableColumn<Employee, String> columnEmployeeID = new TableColumn<>("Employee ID");
                columnEmployeeID.setCellValueFactory(new PropertyValueFactory<>("ID"));
                columnEmployeeID.setPrefWidth(81.371337890625);

                TableColumn<Employee, String> columnEmployeeName = new TableColumn<>("Employee Name");
                columnEmployeeName.setCellValueFactory(new PropertyValueFactory<>("name"));
                columnEmployeeName.setPrefWidth(120.685791015625);

                TableColumn<Employee, String> columnPhoneNumber = new TableColumn<>("Phone Number");
                columnPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
                columnPhoneNumber.setPrefWidth(91.42851257324219);

                TableColumn<Employee, String> columnEmail = new TableColumn<>("Email");
                columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
                columnEmail.setPrefWidth(106.05712890625);

                TableColumn<Employee, String> columnUsername = new TableColumn<>("Username");
                columnUsername.setCellValueFactory(new PropertyValueFactory<>("userName"));
                columnUsername.setPrefWidth(75.88565063476562);

                // Add columns to the table
                tableView.getColumns().addAll(
                                columnEmployeeID,
                                columnEmployeeName,
                                columnPhoneNumber,
                                columnEmail,
                                columnUsername);

                tableView.setItems(db.getAllEmployeeDetails());

                // Label for title
                Label titleLabel = new Label("Employees");
                titleLabel.setLayoutX(202);
                titleLabel.setLayoutY(14);
                titleLabel.setFont(new Font("System Bold", 22.0));

                // Add components to the inner pane
                innerPane.getChildren().addAll(tableView, titleLabel);

                // Add inner pane to the main pane
                mainPane.getChildren().add(innerPane);

                // Set up the scene and stage
                Scene scene = new Scene(mainPane);
                stage.setScene(scene);
                stage.setTitle("Employee Table");
                stage.show();
        }

}
