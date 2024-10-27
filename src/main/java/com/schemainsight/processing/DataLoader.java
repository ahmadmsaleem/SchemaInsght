package com.schemainsight.processing;

import com.schemainsight.userinterface.sidebar.TableInfoSideBar;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataLoader {
    private TableView<Map<String, String>> tableView;
    private ObservableList<Map<String, String>> tableData = FXCollections.observableArrayList();
    private Label uploadStatusLabel;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private List<Map<String, String>> allData;
    private int currentBatchStart = 0;
    private int batchSize; // Default batch size, set during loading
    private TableInfoSideBar tableInfoSideBar;

    public DataLoader(TableView<Map<String, String>> tableView, Label uploadStatusLabel, TableInfoSideBar tableInfoSideBar) {
        this.tableView = tableView;
        this.uploadStatusLabel = uploadStatusLabel;
        this.tableInfoSideBar = tableInfoSideBar;
    }

    public void loadData(String filePath, char confirmedDelimiter) {
        clearPreviousData();

        if (!CSVProcessor.validateCSV(filePath, confirmedDelimiter).isValid()) {
            return;
        }

        List<String> headers = CSVProcessor.getHeaders(filePath, confirmedDelimiter);
        Map<String, String> detectedDataTypes = DataTypeDetector.detectDataTypes(filePath, confirmedDelimiter);

        // Set the batch size dynamically from CsvImportConfig
        batchSize = CsvImportConfig.getBatchSize();
        allData = CSVProcessor.readCSVFile(filePath, confirmedDelimiter, batchSize);

        tableInfoSideBar.updateDataTypes(detectedDataTypes);
        updateTableView(headers);
        currentBatchStart = 0;
        uploadNextBatch();
    }

    private void clearPreviousData() {
        tableData.clear();
        tableView.getItems().clear();
        uploadStatusLabel.setText("Upload Progress: 0.0%");
    }

    private void uploadNextBatch() {
        if (allData == null || currentBatchStart >= allData.size()) {
            Platform.runLater(() -> uploadStatusLabel.setText("Upload Progress: 100.0%"));
            scheduler.shutdown();
            return;
        }

        int nextBatchEnd = Math.min(currentBatchStart + batchSize, allData.size());
        List<Map<String, String>> batchData = allData.subList(currentBatchStart, nextBatchEnd);
        tableData.addAll(batchData);

        double progress = (double) currentBatchStart / allData.size() * 100;
        updateUploadStatus(progress);

        currentBatchStart += batchSize;

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.schedule(this::uploadNextBatch, 50, TimeUnit.MILLISECONDS);
        }
    }

    private void updateTableView(List<String> headers) {
        tableView.getColumns().clear();
        for (String header : headers) {
            TableColumn<Map<String, String>, String> column = new TableColumn<>(header);
            column.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().get(header)));
            tableView.getColumns().add(column);
        }
        tableView.setItems(tableData);
    }

    private void updateUploadStatus(double progress) {
        Platform.runLater(() -> uploadStatusLabel.setText(String.format("Upload Progress: %.1f%%", progress)));
    }

    // Method to set a custom batch size dynamically
    public void updateBatchSize(int newBatchSize) {
        if (newBatchSize > 0) { // Ensure the batch size is positive
            this.batchSize = newBatchSize;
        }
        // Optionally, you can reset the current batch start to resume from the beginning
        currentBatchStart = Math.min(currentBatchStart, allData.size()); // Ensure not to go out of bounds
    }
}
