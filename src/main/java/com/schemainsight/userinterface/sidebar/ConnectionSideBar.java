package com.schemainsight.userinterface.sidebar;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ConnectionSideBar {

    private VBox sidebar;
    private TextField urlField;
    private TextField usernameField;
    private TextField passwordField;
    private Button connectButton;
    private Label statusLabel;

    public ConnectionSideBar() {
        sidebar = new VBox();
        sidebar.getStyleClass().add("connectionSideBar");
        createConnectionUI();
    }

    private void createConnectionUI() {
        Label titleLabel = new Label("Database");
        titleLabel.getStyleClass().add("sidebar-title");

        urlField = new TextField();
        urlField.setPromptText("Database URL");
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        passwordField = new TextField();
        passwordField.setPromptText("Password");

        connectButton = new Button("Connect");
        connectButton.setOnAction(event -> connectToDatabase());

        statusLabel = new Label();
        statusLabel.getStyleClass().add("connection-status");

        sidebar.getChildren().addAll(titleLabel, urlField, usernameField, passwordField, connectButton, statusLabel);
    }

    public VBox getSidebar() {
        return sidebar;
    }

    private void connectToDatabase() {
        String url = urlField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (!url.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            statusLabel.setText("SOON");
        } else {
            statusLabel.setText("SOON.");
        }
    }
}
