package com.schemainsight.processing;

import com.schemainsight.userinterface.sidebar.TableInfoSideBar;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.Map;

public class DataLoader {
    private final TableView<Map<String, String>> tableView;
    private final ObservableList<Map<String, String>> tableData = FXCollections.observableArrayList();
    private final TableInfoSideBar tableInfoSideBar;

    public DataLoader(TableView<Map<String, String>> tableView, TableInfoSideBar tableInfoSideBar) {
        this.tableView = tableView;
        this.tableInfoSideBar = tableInfoSideBar;
    }

    public void loadData(String filePath, char confirmedDelimiter, String batchTerm) {
        clearPreviousData();
        if (!CSVProcessor.validateCSV(filePath, confirmedDelimiter).isValid()) {
            return;
        }
        List<String> headers = CSVProcessor.getHeaders(filePath, confirmedDelimiter);
        Map<String, String> detectedDataTypes = DataTypeDetector.detectDataTypes(filePath, confirmedDelimiter);
        List<Map<String, String>> allData = CSVProcessor.readCSVFile(filePath, confirmedDelimiter, Integer.parseInt(batchTerm));
        tableInfoSideBar.updateDataTypes(detectedDataTypes);
        updateTableView(headers);
        tableData.addAll(allData);
        tableView.setItems(tableData);
    }
    public void uploadData(String filePath, char confirmedDelimiter) {
        loadData(filePath, confirmedDelimiter, String.valueOf(CSVImportConfig.getBatchSize()));
    }

    private void clearPreviousData() {
        tableData.clear();
        tableView.getItems().clear();
    }

    private void updateTableView(List<String> headers) {
        tableView.getColumns().clear();
        for (String header : headers) {
            TableColumn<Map<String, String>, String> column = new TableColumn<>(header);
            column.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().get(header)));
            tableView.getColumns().add(column);
        }
    }
}
