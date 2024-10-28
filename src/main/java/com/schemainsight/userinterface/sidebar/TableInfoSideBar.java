package com.schemainsight.userinterface.sidebar;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.Map;

public class TableInfoSideBar {
    private final VBox sidebar;
    private final VBox dataTypeVBox;
    private final Label uploadStatusLabel;

    public TableInfoSideBar(Label uploadStatusLabel) {
        this.uploadStatusLabel = uploadStatusLabel;
        this.sidebar = createSidebar();
        this.dataTypeVBox = createDataTypeVBox();
    }

    public VBox getSidebar() {
        return sidebar;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("tableInfoSideBar");

        Label titleLabel = createLabel("Table Info", "sidebar-title", "table-info-label");
        ScrollPane dataTypeScrollPane = createDataTypeScrollPane();

        sidebar.getChildren().addAll(titleLabel, createSpacer(), dataTypeScrollPane, createSpacer(), uploadStatusLabel);
        return sidebar;
    }

    private VBox createDataTypeVBox() {
        VBox vBox = new VBox();
        vBox.getStyleClass().add("unique-counts-container");
        return vBox;
    }

    private ScrollPane createDataTypeScrollPane() {
        ScrollPane scrollPane = new ScrollPane(dataTypeVBox);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scroll-pane");
        return scrollPane;
    }

    private Label createLabel(String text, String... styleClasses) {
        Label label = new Label(text);
        label.getStyleClass().addAll(styleClasses);
        return label;
    }

    public void updateDataTypes(Map<String, String> detectedDataTypes) {
        dataTypeVBox.getChildren().clear();
        Label dataTypeLabel = createLabel("Data Types:", "data-type-header");
        dataTypeVBox.getChildren().add(dataTypeLabel);

        int count = 1;
        for (Map.Entry<String, String> entry : detectedDataTypes.entrySet()) {
            String labelText = String.format("%d. %s: %s", count++, entry.getKey(), entry.getValue());
            Label headerLabel = createLabel(labelText, "data-type");
            dataTypeVBox.getChildren().add(headerLabel);
        }
    }

    private Region createSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
}
