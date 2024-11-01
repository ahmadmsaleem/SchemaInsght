package com.schemainsight.userinterface.sidebar;

import com.schemainsight.processing.CSVImportConfig;
import com.schemainsight.processing.CSVProcessor;
import com.schemainsight.processing.DataLoader;
import com.schemainsight.processing.DataTypeDetector;
import com.schemainsight.userinterface.CustomButton;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.StageStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;

import java.sql.*;
import java.util.*;
import java.io.File;
import java.util.function.BiConsumer;

public class UploadSideBar {
    private final VBox sidebar;
    private final BiConsumer<String, Character> loadDataCallback;
    private final ConnectionSideBar connectionSideBar;
    private final TableView<Map<String, String>> tableView;
    private static final List<String> uploadHistory = new ArrayList<>();
    private final TableInfoSideBar tableInfoSideBar;

    public UploadSideBar(DataLoader dataLoader, ConnectionSideBar connectionSideBar, TableView<Map<String, String>> tableView, TableInfoSideBar tableInfoSideBar) {
        this.tableView = tableView;
        this.loadDataCallback = dataLoader::uploadData;
        this.tableInfoSideBar = tableInfoSideBar;
        this.sidebar = createUploadSideBar();
        this.connectionSideBar = connectionSideBar;
    }

    public VBox getSidebar() {
        return sidebar;
    }

    private VBox createUploadSideBar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("uploadSideBar");

        Label titleLabel = createLabel();
        CustomButton uploadButton = createUploadButton();
        CustomButton uploadFromDatabaseButton = createUploadFromDatabaseButton();
        CustomButton viewUploadHistoryButton = createHistoryButton();
        CustomButton exitButton = createExitButton();

        sidebar.getChildren().addAll(titleLabel, uploadButton, uploadFromDatabaseButton, viewUploadHistoryButton, createSpacer(), exitButton);
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

    private CustomButton createUploadFromDatabaseButton() {
        return CustomButton.createSidebarButton("Upload from Database", "Upload data directly from the database.", event -> uploadFromDatabase());
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
                loadDataCallback.accept(filePath, detectedDelimiter);
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
        confirmedConfigOpt.ifPresent(config -> {
            loadDataCallback.accept(filePath, detectedDelimiter);
            updateUploadHistory(filePath);
        });
    }

    public static String getLatestFilePath() {
        return uploadHistory.isEmpty() ? null : uploadHistory.getLast();
    }

    private Region createSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private void uploadFromDatabase() {
        Connection connection = connectionSideBar.getConnection();
        if (connection == null) {
            showNoConnectionDialog();
            return;
        }

        ObservableList<String> tableNames = FXCollections.observableArrayList();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema='public'")) {

            while (rs.next()) {
                tableNames.add(rs.getString("table_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        Dialog<String> tablesDialog = new Dialog<>();
        tablesDialog.setTitle("Database Tables");
        tablesDialog.setHeaderText("Select a Table to Upload Data From");
        tablesDialog.getDialogPane().getStylesheets().add("styles.css");
        tablesDialog.getDialogPane().getStyleClass().add("myDialog");
        tablesDialog.initStyle(StageStyle.UTILITY);

        ListView<String> tableListView = new ListView<>(tableNames);
        tableListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        VBox dialogPaneContent = new VBox(tableListView);
        tablesDialog.getDialogPane().setContent(dialogPaneContent);

        tablesDialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) tablesDialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.getStyleClass().add("ok");
        Button cancelButton = (Button) tablesDialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.getStyleClass().add("cancel");

        tablesDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK && !tableListView.getSelectionModel().isEmpty()) {
                return tableListView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        Optional<String> selectedTableOpt = tablesDialog.showAndWait();
        selectedTableOpt.ifPresent(selectedTable -> {
            if (selectedTable != null) {
                fetchData(selectedTable);
            }
        });
    }

    private void showNoConnectionDialog() {
        Dialog<Void> noConnectionDialog = new Dialog<>();
        noConnectionDialog.setTitle("No Connection");
        noConnectionDialog.setHeaderText("No database connection available.");
        noConnectionDialog.getDialogPane().getStylesheets().add("styles.css");
        noConnectionDialog.getDialogPane().getStyleClass().add("myDialog");
        noConnectionDialog.initStyle(StageStyle.UTILITY);

        Label messageLabel = new Label("Please connect to the database before trying to upload data.");
        noConnectionDialog.getDialogPane().setContent(messageLabel);

        noConnectionDialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);

        Button okButton = (Button) noConnectionDialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.getStyleClass().add("ok");

        noConnectionDialog.showAndWait();
    }

    private void fetchData(String tableName) {
        Connection connection = connectionSideBar.getConnection();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM public." + tableName)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            tableView.getColumns().clear();

            // Prepare a list to hold data for type detection
            List<Map<String, String>> dataList = new ArrayList<>();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                TableColumn<Map<String, String>, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().get(columnName)));
                tableView.getColumns().add(column);
            }

            ObservableList<Map<String, String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    row.put(metaData.getColumnName(i), value);
                }
                data.add(row);
                dataList.add(row);
            }

            Map<String, String> detectedTypes = DataTypeDetector.detectDataTypesForDatabase(dataList);
            tableInfoSideBar.updateDataTypes(detectedTypes);

            for (Map.Entry<String, String> entry : detectedTypes.entrySet()) {
                System.out.println("Column: " + entry.getKey() + ", Detected Type: " + entry.getValue());

            }

            tableView.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
