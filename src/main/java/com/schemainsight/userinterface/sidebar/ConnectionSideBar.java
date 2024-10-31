package com.schemainsight.userinterface.sidebar;

import com.schemainsight.userinterface.CustomButton;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ConnectionSideBar {

    private VBox sidebar;
    private TextField urlField;
    private TextField usernameField;
    private TextField passwordField;
    private Button connectButton;
    private TextArea statusTextArea; // Changed to TextArea
    private Button showTablesButton;
    private Button closeConnectionButton;
    private Button helpButton;
    private Button fetchDataButton;

    private TableView<Map<String, String>> tableView;

    public ConnectionSideBar(TableView<Map<String, String>> tableView) {
        this.tableView = tableView;
        sidebar = new VBox();
        sidebar.getStyleClass().add("connectionSideBar");
        createConnectionUI();
    }

    private Connection connection;

    public VBox getSidebar() {
        return sidebar;
    }

    public Connection getConnection() {
        return connection;
    }

    public ConnectionSideBar() {
        sidebar = new VBox();
        sidebar.getStyleClass().add("connectionSideBar");
        createConnectionUI();
    }

    private void createConnectionUI() {
        Label titleLabel = new Label("Database");
        titleLabel.getStyleClass().add("sidebar-title");

        urlField = new TextField("jdbc:postgresql://localhost:5432/test");
        urlField.setPromptText("Database URL");

        usernameField = new TextField("postgres");
        usernameField.setPromptText("Username");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        connectButton = new Button("Connect");
        connectButton.setOnAction(event -> connectToDatabase());

        statusTextArea = new TextArea();
        statusTextArea.setEditable(false);
        statusTextArea.setWrapText(true);
        statusTextArea.setPrefHeight(100);
        statusTextArea.getStyleClass().add("myTextArea");

        fetchDataButton = new Button("Fetch Data");
        fetchDataButton.setOnAction(event -> fetchData());


        showTablesButton = CustomButton.createSidebarButton("Show Tables", "Show all tables in the database", event -> showTables());
        showTablesButton.setDisable(true);

        closeConnectionButton = CustomButton.createSidebarButton("Close Connection", "Close the current database connection", event -> closeConnection());
        closeConnectionButton.setDisable(true);

        helpButton = CustomButton.createSidebarButton("Help", "Get started with database connection", event -> showHelpDialog());

        sidebar.getChildren().addAll(titleLabel, urlField, usernameField, passwordField, connectButton, statusTextArea, showTablesButton, closeConnectionButton, helpButton,fetchDataButton);
    }

    private void connectToDatabase() {
        String url = urlField.getText().trim();
        String username = usernameField.getText();
        String password = passwordField.getText().trim();

        if (!url.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            try {
                connection = DriverManager.getConnection(url, username, password);
                statusTextArea.setText("Connected successfully!");
                showTablesButton.setDisable(false);
                closeConnectionButton.setDisable(false);
            } catch (SQLException e) {
                statusTextArea.setText("Connection failed:\n" + e.getMessage());
                e.printStackTrace();
                showTablesButton.setDisable(true);
                closeConnectionButton.setDisable(true);
            }
        } else {
            statusTextArea.setText("Please fill in all fields.");
        }
    }

    private void showTables() {
        if (connection != null) {
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema='public'")) {

                StringBuilder tables = new StringBuilder("Tables:\n");
                while (rs.next()) {
                    tables.append(rs.getString("table_name")).append("\n");
                }
                showTablesDialog(tables.toString());

            } catch (SQLException e) {
                statusTextArea.setText("Error retrieving tables:\n " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            statusTextArea.setText("Not connected to a database.");
        }
    }

    private void showTablesDialog(String tables) {
        Dialog<String> tablesDialog = new Dialog<>();
        tablesDialog.setTitle("Database Tables");
        tablesDialog.setHeaderText("List of Tables in the Database");
        tablesDialog.getDialogPane().getStylesheets().add("styles.css");
        tablesDialog.getDialogPane().getStyleClass().add("myDialog");
        tablesDialog.initStyle(StageStyle.UTILITY);
        TextArea tablesTextArea = new TextArea(tables);
        tablesTextArea.setEditable(false);
        tablesTextArea.setWrapText(true);
        VBox dialogPaneContent = new VBox(tablesTextArea);
        tablesDialog.getDialogPane().setContent(dialogPaneContent);
        ButtonType closeButtonType = new ButtonType("CLOSE", ButtonBar.ButtonData.OK_DONE);
        tablesDialog.getDialogPane().getButtonTypes().add(closeButtonType);
        Button closeButton = (Button) tablesDialog.getDialogPane().lookupButton(closeButtonType);
        closeButton.getStyleClass().add("cancel");
        tablesDialog.showAndWait();
    }

    private void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                statusTextArea.setText("Connection closed.");
                showTablesButton.setDisable(true);
                closeConnectionButton.setDisable(true);
            } catch (SQLException e) {
                statusTextArea.setText("Failed to close connection: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            statusTextArea.setText("No connection to close.");
        }
    }

    private void showHelpDialog() {
        Dialog<String> helpDialog = new Dialog<>();
        helpDialog.setTitle("Database Connection Help");
        helpDialog.setHeaderText("Guidance for Setting Up Database Connection");

        helpDialog.getDialogPane().getStylesheets().add("styles.css");
        helpDialog.getDialogPane().getStyleClass().add("myDialog");
        helpDialog.initStyle(StageStyle.UTILITY);

        helpDialog.setWidth(600);
        helpDialog.setHeight(650);

        TextArea helpTextArea = new TextArea();
        helpTextArea.setText(
                "To connect to a PostgreSQL database, please fill in the following details:\n\n" +
                        "- Database URL: Format - jdbc:postgresql://<host>:<port>/<database>\n" +
                        "  Example: jdbc:postgresql://localhost:5432/mydatabase\n\n" +
                        "    - <host>: The address of the PostgreSQL server (e.g., localhost for local connections).\n" +
                        "    - <port>: The port number on which the PostgreSQL server is listening (default is 5432).\n" +
                        "    - <database>: The name of the database you want to connect to.\n\n" +
                        "- Username: Your PostgreSQL username (e.g., postgres). Make sure you have the right privileges.\n\n" +
                        "- Password: The password associated with the username. Ensure this is kept secure.\n\n" +
                        "Once filled, click 'Connect' to establish the connection. If successful, the application will allow you to view the database tables.\n\n" +
                        "If you encounter any issues:\n" +
                        "1. Check that the PostgreSQL server is running.\n" +
                        "2. Ensure the firewall settings allow connections on the specified port.\n" +
                        "3. Verify that the username and password are correct."
        );
        helpTextArea.setEditable(false);

        helpTextArea.getStyleClass().add("myDialog");
        helpTextArea.setPrefHeight(500);
        helpTextArea.setPrefWidth(550);
        helpTextArea.setWrapText(true);

        VBox dialogPaneContent = new VBox(helpTextArea);
        helpDialog.getDialogPane().setContent(dialogPaneContent);
        ButtonType closeButtonType = new ButtonType("CLOSE", ButtonBar.ButtonData.OK_DONE);
        helpDialog.getDialogPane().getButtonTypes().add(closeButtonType);
        Button closeButton = (Button) helpDialog.getDialogPane().lookupButton(closeButtonType);
        closeButton.getStyleClass().add("cancel");
        helpDialog.showAndWait();
    }



    // Inside ConnectionSideBar
    private void fetchData() {
        if (connection != null && tableView != null) {
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM public.test")) {

                // Set up columns based on ResultSet metadata
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                tableView.getColumns().clear();  // Clear old columns
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    TableColumn<Map<String, String>, String> column = new TableColumn<>(columnName);
                    column.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().get(columnName)));
                    tableView.getColumns().add(column);
                }

                // Fetch data and add to TableView
                ObservableList<Map<String, String>> data = FXCollections.observableArrayList();
                while (rs.next()) {
                    Map<String, String> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), rs.getString(i));
                    }
                    data.add(row);
                }
                tableView.setItems(data);
                statusTextArea.setText("Data fetched successfully!");

            } catch (SQLException e) {
                statusTextArea.setText("Error fetching data:\n" + e.getMessage());
                e.printStackTrace();
            }
        } else {
            statusTextArea.setText("Not connected to a database or table view is not initialized.");
        }
    }

}
