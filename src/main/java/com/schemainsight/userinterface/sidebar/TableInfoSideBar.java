package com.schemainsight.userinterface.sidebar;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Map;

public class TableInfoSideBar {

    private final VBox sidebar;
    private VBox dataTypeVBox;

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

        dataTypeVBox = new VBox();
        dataTypeVBox.getStyleClass().add("unique-counts-container");

        ScrollPane dataTypeScrollPane = new ScrollPane(dataTypeVBox);
        dataTypeScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        dataTypeScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        dataTypeScrollPane.getStyleClass().add("scroll-pane");

        sidebar.getChildren().addAll(titleLabel, dataTypeScrollPane, createSpacer());
        return sidebar;
    }

    public void updateDataTypes(Map<String, String> detectedDataTypes) {
        dataTypeVBox.getChildren().clear();

        Label dataTypeLabel = new Label("Data Types:");
        dataTypeLabel.getStyleClass().add("data-type-header");
        dataTypeVBox.getChildren().add(dataTypeLabel);

        int count = 1;
        for (Map.Entry<String, String> entry : detectedDataTypes.entrySet()) {
            String labelText = String.format("%d. %s: %s", count++, entry.getKey(), entry.getValue());
            Label headerLabel = new Label(labelText);
            headerLabel.getStyleClass().add("data-type");
            dataTypeVBox.getChildren().add(headerLabel);
        }
    }

    private Region createSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
}
