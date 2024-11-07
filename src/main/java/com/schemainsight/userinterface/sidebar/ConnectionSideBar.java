package com.schemainsight.userinterface.sidebar;

import com.schemainsight.userinterface.CustomButton;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

import java.sql.*;
import java.util.Map;

public class ConnectionSideBar {

    private VBox sidebar;
    private TextField urlField;
    private TextField usernameField;
    private TextField passwordField;
    private Button connectButton;
    private TextArea statusTextArea;
    private Button closeConnectionButton;
    private Button helpButton;
    private Connection connection;

    public ConnectionSideBar() {
        sidebar = new VBox();
        sidebar.getStyleClass().add("connectionSideBar");
        createConnectionUI();
    }

    public VBox getSidebar() {
        return sidebar;
    }

    public Connection getConnection() {
        return connection;
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
        connectButton = CustomButton.createSidebarButton("Connect", "Connect database", event -> connectToDatabase());

        statusTextArea = new TextArea();
        statusTextArea.setEditable(false);
        statusTextArea.setWrapText(true);
        statusTextArea.setPrefHeight(100);
        statusTextArea.getStyleClass().add("myTextArea");

        closeConnectionButton = CustomButton.createSidebarButton("Close Connection", "Close the current database connection", event -> closeConnection());
        closeConnectionButton.setDisable(true);

        helpButton = CustomButton.createSidebarButton("Help", "Get started with database connection", event -> showHelpDialog());

        sidebar.getChildren().addAll(titleLabel, urlField, usernameField, passwordField, connectButton, closeConnectionButton, statusTextArea, helpButton);
    }

    private void connectToDatabase() {
        String url = urlField.getText().trim();
        String username = usernameField.getText();
        String password = passwordField.getText().trim();

        if (!url.matches("jdbc:postgresql://[^/]+:\\d+/[^/]+")) {
            statusTextArea.setText("Invalid URL: Please include a database name in the URL.");
            return;
        }

        if (!url.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            try {
                connection = DriverManager.getConnection(url, username, password);
                statusTextArea.setText("Connected successfully!");
                closeConnectionButton.setDisable(false);
            } catch (SQLException e) {
                statusTextArea.setText("Connection failed:\n" + e.getMessage());
                e.printStackTrace();
                closeConnectionButton.setDisable(true);
            }
        } else {
            statusTextArea.setText("Please fill in all fields.");
        }
    }

    private void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                statusTextArea.setText("Connection closed.");
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
            helpTextArea.setWrapText(true);
            helpTextArea.setPrefHeight(500);
            helpTextArea.setPrefWidth(550);

            VBox dialogPaneContent = new VBox(helpTextArea);
            helpDialog.getDialogPane().setContent(dialogPaneContent);
            helpDialog.getDialogPane().getButtonTypes().setAll(ButtonType.CANCEL);
            Button cancelButton = (Button) helpDialog.getDialogPane().lookupButton(ButtonType.CANCEL);
            cancelButton.getStyleClass().add("cancel");

            helpDialog.showAndWait();
        }
}
