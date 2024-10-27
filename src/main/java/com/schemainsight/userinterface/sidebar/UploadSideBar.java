package com.schemainsight.userinterface.sidebar;

import com.schemainsight.processing.CSVProcessor;
import com.schemainsight.processing.CsvImportConfig;
import com.schemainsight.processing.DataLoader; // Make sure to import the DataLoader class
import com.schemainsight.userinterface.CustomButton;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.io.File;
import java.util.Optional;
import java.util.function.BiConsumer;

public class UploadSideBar {
    private VBox sidebar;
    private Label titleLabel;
    private BiConsumer<String, Character> loadDataCallback;
    private DataLoader dataLoader; // Reference to the DataLoader instance

    public UploadSideBar(Label uploadStatusLabel, BiConsumer<String, Character> loadDataCallback, DataLoader dataLoader) {
        this.loadDataCallback = loadDataCallback;
        this.dataLoader = dataLoader; // Initialize the DataLoader
        sidebar = createUploadSideBar();
    }

    public VBox getSidebar() {
        return sidebar;
    }

    private VBox createUploadSideBar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("uploadSideBar");

        titleLabel = new Label("SchemaInsight");
        titleLabel.getStyleClass().add("sidebar-title");

        VBox buttonContainer = new VBox();

        CustomButton uploadButton = CustomButton.createSidebarButton(
                "Upload File",
                "Supports CSV file uploads only.",
                event -> uploadFile()
        );
        buttonContainer.getChildren().add(uploadButton);

        CustomButton setBatchSizeButton = CustomButton.createSidebarButton(
                "Set Batch Size",
                "Specify the number of rows to load at once.",
                event -> setBatchSize()
        );
        buttonContainer.getChildren().add(setBatchSizeButton);

        CustomButton viewSchemaButton = CustomButton.createSidebarButton(
                "View Schema",
                null,
                event -> System.out.println("viewSchemaButton")
        );
        buttonContainer.getChildren().add(viewSchemaButton);

        CustomButton exitButton = CustomButton.createSidebarButton(
                "Exit",
                null,
                event -> System.exit(0)
        );
        buttonContainer.getChildren().add(exitButton);

        sidebar.getChildren().addAll(titleLabel, uploadButton, setBatchSizeButton, viewSchemaButton, createSpacer(), exitButton);
        return sidebar;
    }

    private void uploadFile() {
        Stage primaryStage = (Stage) sidebar.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            String filePath = file.getAbsolutePath();
            char detectedDelimiter = CSVProcessor.detectDelimiter(filePath);

            Optional<CsvImportConfig> confirmedConfigOpt = CSVProcessor.ConfigurationTable(detectedDelimiter, filePath);

            confirmedConfigOpt.ifPresent(config -> {
                loadDataCallback.accept(filePath, config.getDelimiter());
            });
        }
    }

    private void setBatchSize() {
        TextInputDialog dialog = new TextInputDialog("1000"); // Default value
        dialog.setTitle("Set Batch Size");
        dialog.setHeaderText("Specify Batch Size");
        dialog.setContentText("Enter the number of rows to load at once:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(size -> {
            try {
                int batchSize = Integer.parseInt(size);
                if (batchSize > 0) {
                    dataLoader.updateBatchSize(batchSize); // Call updateBatchSize on the DataLoader
                    System.out.println("Batch size set to: " + batchSize); // For debugging
                } else {
                    // Handle invalid batch size (e.g., show an alert)
                    System.out.println("Invalid batch size. Please enter a positive number.");
                }
            } catch (NumberFormatException e) {
                // Handle non-integer input
                System.out.println("Invalid input. Please enter a valid number.");
            }
        });
    }

    private Region createSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
}
