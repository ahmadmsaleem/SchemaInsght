package com.schemainsight.userinterface.sidebar;

import com.schemainsight.processing.CSVProcessor;
import com.schemainsight.userinterface.CustomButton;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.List;
import java.util.Map;

public class TableInfoSideBar {

    private final VBox sidebar;
    private Map<String, String> detectedDataTypes;

    public TableInfoSideBar() {
        this.sidebar = createSidebar();
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
        if (filePath.trim().startsWith("Data Repository")) {
            showAlert("Feature Coming Soon", "The ability to view the schema for database tables will be available soon.");
            return;
        }


        File file = new File(filePath);
        long fileSize = file.length();
        String fileSizeFormatted = formatFileSize(fileSize);

        char detectedDelimiter = CSVProcessor.detectDelimiter(filePath);
        List<String> schema = CSVProcessor.getSchema(filePath, detectedDelimiter);

        Dialog<List<String>> schemaDialog = new Dialog<>();
        schemaDialog.setTitle("CSV Schema");
        schemaDialog.setHeaderText("Schema for file: " + filePath);
        schemaDialog.getDialogPane().getStylesheets().add("styles.css");
        schemaDialog.getDialogPane().getStyleClass().add("myDialog");
        schemaDialog.initStyle(StageStyle.UTILITY);

        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(10));
        ListView<String> schemaListView = new ListView<>();
        schemaListView.getItems().addAll(schema);

        Label fileSizeLabel = new Label("File Size: " + fileSizeFormatted);
        content.getChildren().addAll(fileSizeLabel);

        schemaListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String dataType = detectedDataTypes.get(newValue);
                showDataTypeInfo(dataType, content);
            }
        });

        content.getChildren().addAll(new Label("Schema:"), schemaListView);
        schemaDialog.getDialogPane().setContent(content);
        schemaDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        schemaDialog.showAndWait();
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

    private void showDataTypeInfo(String dataType, VBox content) {
        if (content.getChildren().size() > 4) {
            content.getChildren().remove(4, content.getChildren().size());
        }
        Label dataTypeLabel = new Label("Data Type: " + (dataType != null ? dataType : "N/A"));
        dataTypeLabel.getStyleClass().add("data-type-info");
        content.getChildren().add(dataTypeLabel);
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
