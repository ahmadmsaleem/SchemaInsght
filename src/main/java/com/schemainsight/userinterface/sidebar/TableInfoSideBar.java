package com.schemainsight.userinterface.sidebar;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.Map;

public class TableInfoSideBar {

    private VBox sidebar;
    private TableView<Map<String, String>> tableView;
    private ObservableList<Map<String, String>> tableData = FXCollections.observableArrayList();
    private VBox dataTypeVBox;
    private Label uploadStatusLabel;

    public TableInfoSideBar(TableView<Map<String, String>> tableView, Label uploadStatusLabel) {
        this.tableView = tableView;
        this.uploadStatusLabel = uploadStatusLabel;
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



        dataTypeVBox = new VBox();
        dataTypeVBox.getStyleClass().add("unique-counts-container");

        ScrollPane dataTypeScrollPane = new ScrollPane(dataTypeVBox);
        dataTypeScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        dataTypeScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        dataTypeScrollPane.getStyleClass().add("scroll-pane");

        sidebar.getChildren().addAll(titleLabel, createSpacer(), dataTypeScrollPane, createSpacer(), uploadStatusLabel);
        return sidebar;
    }


    public void updateDataTypes(Map<String, String> detectedDataTypes) {
        dataTypeVBox.getChildren().clear();
        int count = 1;
        Label dataTypeLabel = new Label("Data Types:");
        dataTypeLabel.getStyleClass().add("data-type-header");
        dataTypeVBox.getChildren().add(dataTypeLabel);

        for (Map.Entry<String, String> entry : detectedDataTypes.entrySet()) {
            String header = entry.getKey();
            String dataType = entry.getValue();
            String labelText = String.format("%d. %s: %s", count, header, dataType);
            Label headerLabel = new Label(labelText);
            headerLabel.getStyleClass().add("data-type");
            dataTypeVBox.getChildren().add(headerLabel);
            count++;
        }
    }

    private Region createSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
}
