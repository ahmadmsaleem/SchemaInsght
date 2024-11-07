package com.schemainsight.userinterface.sidebar;

import com.schemainsight.processing.CSVProcessor;
import com.schemainsight.userinterface.CustomButton;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableInfoSideBar {

    private final VBox sidebar;
    private Map<String, String> detectedDataTypes;
    private final ConnectionSideBar connectionSideBar;

    public TableInfoSideBar(ConnectionSideBar connectionSideBar) {
        this.sidebar = createSidebar();
        this.connectionSideBar = connectionSideBar;
    }

    public VBox getSidebar() {
        return sidebar;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("tableInfoSideBar");

        Label titleLabel = new Label("Table Info");
        titleLabel.getStyleClass().addAll("sidebar-title", "table-info-label");

        CustomButton viewSchemaButton = createSchemaButton();

        sidebar.getChildren().addAll(titleLabel, viewSchemaButton, createSpacer());
        return sidebar;
    }

    private CustomButton createSchemaButton() {
        return CustomButton.createSidebarButton("View Schema", null, event -> viewSchema());
    }

    private void viewSchema() {
        String filePath = UploadSideBar.getLatestFilePath();
        if (filePath == null) {
            showAlert("No File Uploaded", "Please upload a file to view its schema.");
            return;
        }

        if (filePath.trim().startsWith("Data Repository:")) {
            handleDatabaseSchema(filePath);
        } else {
            handleCSVSchema(filePath);
        }
    }

    private void handleDatabaseSchema(String filePath) {
        Connection connection = connectionSideBar.getConnection();
        if (connection == null) {
            showAlert("No connection", "Check your connection.");
            return;
        }

        String[] schemaTableParts = filePath.split(":")[1].trim().split("\\.");
        String schemaName = schemaTableParts[0];
        String tableName = schemaTableParts[1];

        Map<String, String> schema = getTableSchema(connection, schemaName, tableName);
        if (schema.isEmpty()) {
            showAlert("Schema Not Found", "Could not retrieve schema for table: " + tableName);
            return;
        }

        String tableSize = getTableSize(connection, schemaName, tableName);
        showSchemaDialog("Table Schema", "Schema for table: " + tableName + " (Size: " + tableSize + ")", schema);
    }

    private void handleCSVSchema(String filePath) {
        File file = new File(filePath);
        long fileSize = file.length();
        String fileSizeFormatted = formatFileSize(fileSize);
        char detectedDelimiter = CSVProcessor.detectDelimiter(filePath);
        List<String> schema = CSVProcessor.getSchema(filePath, detectedDelimiter);

        Map<String, String> schemaMap = new HashMap<>();
        for (String column : schema) {
            schemaMap.put(column, detectedDataTypes.get(column));
        }

        String fileName = file.getName();
        showSchemaDialog("CSV Schema", "Schema for file: " + fileName + " (File Size: " + fileSizeFormatted + ")", schemaMap);
    }

    private void showSchemaDialog(String title, String header, Map<String, String> schema) {
        Dialog<Map<String, String>> schemaDialog = new Dialog<>();
        schemaDialog.setTitle(title);
        schemaDialog.setHeaderText(header);
        schemaDialog.getDialogPane().getStylesheets().add("styles.css");
        schemaDialog.getDialogPane().getStyleClass().add("myDialog");
        schemaDialog.initStyle(StageStyle.UTILITY);

        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(10));
        ListView<String> schemaListView = new ListView<>();
        schema.forEach((column, dataType) -> schemaListView.getItems().add(column + " - " + dataType));
        content.getChildren().addAll(new Label("Schema:"), schemaListView);

        schemaDialog.getDialogPane().setContent(content);
        schemaDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        Button okButton = (Button) schemaDialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.getStyleClass().add("ok");

        schemaDialog.showAndWait();
    }

    private Map<String, String> getTableSchema(Connection connection, String schemaName, String tableName) {
        Map<String, String> columnDataTypes = new HashMap<>();
        String query = "SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = ? AND table_name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, schemaName);
            pstmt.setString(2, tableName);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    columnDataTypes.put(rs.getString("column_name"), rs.getString("data_type"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return columnDataTypes;
    }

    private String formatFileSize(long sizeInBytes) {
        String[] units = {"Bytes", "KB", "MB", "GB"};
        int unitIndex = 0;
        double size = sizeInBytes;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }


    public String getTableSize(Connection connection, String schemaName, String tableName) {
        String query = "SELECT pg_size_pretty(pg_total_relation_size(?::regclass)) AS table_size";
        String tableSize = null;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, schemaName + "." + tableName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    tableSize = rs.getString("table_size");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tableSize;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.initStyle(StageStyle.UTILITY);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void updateDataTypes(Map<String, String> detectedDataTypes) {
        this.detectedDataTypes = detectedDataTypes;
    }

    private Region createSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
}
