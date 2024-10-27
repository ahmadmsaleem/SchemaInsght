package com.schemainsight.userinterface;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class TopBarManager {

    private final SideBarManager sideBarManager;

    public TopBarManager(SideBarManager sideBarManager) {
        this.sideBarManager = sideBarManager;
    }

    public void initializeTopBar(BorderPane root) {
        CustomButton toggleUploadSideBarButton = CustomButton.createTopButton("Upload");
        CustomButton tableInfoButton = CustomButton.createTopButton("Table Info");
        CustomButton connectionButton = CustomButton.createTopButton("Connection");

        toggleUploadSideBarButton.setOnAction(event -> sideBarManager.toggleSideBar(root, "upload"));
        tableInfoButton.setOnAction(event -> sideBarManager.toggleSideBar(root, "tableInfo"));
        connectionButton.setOnAction(event -> sideBarManager.toggleSideBar(root, "connection"));

        HBox topBarBox = new HBox(10, toggleUploadSideBarButton, tableInfoButton, connectionButton);
        topBarBox.getStyleClass().add("top-bar-box");
        root.setTop(topBarBox);
    }
}
