package com.schemainsight.userinterface.sidebar;

import com.schemainsight.processing.CSVImportConfig;
import com.schemainsight.processing.CSVProcessor;
import com.schemainsight.processing.DataLoader;
import com.schemainsight.userinterface.CustomButton;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class UploadSideBar {
    private final VBox sidebar;
    private final BiConsumer<String, Character> loadDataCallback;
    private static final List<String> uploadHistory = new ArrayList<>();

    // Updated constructor to accept DataLoader and retrieve the loadData method
    public UploadSideBar(DataLoader dataLoader) {
        this.loadDataCallback = dataLoader::uploadData; // Reference to the uploadData method
        this.sidebar = createUploadSideBar();
    }

    public VBox getSidebar() {
        return sidebar;
    }

    private VBox createUploadSideBar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("uploadSideBar");

        Label titleLabel = createLabel();
        CustomButton uploadButton = createUploadButton();
        CustomButton viewUploadHistoryButton = createHistoryButton();
        CustomButton exitButton = createExitButton();

        sidebar.getChildren().addAll(titleLabel, uploadButton, viewUploadHistoryButton, createSpacer(), exitButton);
        return sidebar;
    }

    private Label createLabel() {
        Label label = new Label("SchemaInsight");
        label.getStyleClass().add("sidebar-title");
        return label;
    }

    private CustomButton createUploadButton() {
        return CustomButton.createSidebarButton("Upload File", "Supports CSV file uploads only.", event -> uploadFile());
    }

    private CustomButton createHistoryButton() {
        return CustomButton.createSidebarButton("View Upload History", "See previously uploaded files.", event -> viewUploadHistory());
    }

    private CustomButton createExitButton() {
        return CustomButton.createSidebarButton("Exit", null, event -> System.exit(0));
    }

    private void uploadFile() {
        Stage primaryStage = (Stage) sidebar.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            String filePath = file.getAbsolutePath();
            char detectedDelimiter = CSVProcessor.detectDelimiter(filePath);
            Optional<CSVImportConfig> confirmedConfigOpt = CSVProcessor.ConfigurationTable(detectedDelimiter);

            confirmedConfigOpt.ifPresent(config -> {
                loadDataCallback.accept(filePath, detectedDelimiter); // Pass the detected delimiter here
                updateUploadHistory(filePath);
            });
        }
    }

    private void updateUploadHistory(String filePath) {
        uploadHistory.remove(filePath);
        uploadHistory.add(filePath);
    }

    private void viewUploadHistory() {
        Dialog<String> historyDialog = new Dialog<>();
        historyDialog.setTitle("Upload History");
        historyDialog.setHeaderText("Previously Uploaded Files");
        historyDialog.getDialogPane().getStylesheets().add("styles.css");
        historyDialog.getDialogPane().getStyleClass().add("myDialog");
        historyDialog.initStyle(StageStyle.UTILITY);
        historyDialog.setWidth(500);
        historyDialog.setHeight(550);

        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(10));
        ListView<String> fileListView = new ListView<>();

        if (uploadHistory.isEmpty()) {
            content.getChildren().add(new Label("No files have been uploaded yet."));
        } else {
            fileListView.getItems().addAll(uploadHistory);
            content.getChildren().addAll(new Label("Select a file to re-upload:"), fileListView);
        }

        historyDialog.getDialogPane().setContent(content);
        historyDialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) historyDialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.getStyleClass().add("ok");
        Button cancelButton = (Button) historyDialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.getStyleClass().add("cancel");

        historyDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK && fileListView.getSelectionModel().getSelectedItem() != null) {
                return fileListView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        Optional<String> result = historyDialog.showAndWait();
        result.ifPresent(this::reUploadFile);
    }

    private void reUploadFile(String filePath) {
        char detectedDelimiter = CSVProcessor.detectDelimiter(filePath);
        Optional<CSVImportConfig> confirmedConfigOpt = CSVProcessor.ConfigurationTable(detectedDelimiter);
        confirmedConfigOpt.ifPresent(config -> loadDataCallback.accept(filePath, detectedDelimiter)); // Pass the detected delimiter here
    }

    public static String getLatestFilePath() {
        return uploadHistory.isEmpty() ? null : uploadHistory.get(uploadHistory.size() - 1); // Fixed to get last item
    }

    private Region createSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

}
