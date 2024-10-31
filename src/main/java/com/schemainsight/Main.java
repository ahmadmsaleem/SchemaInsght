package com.schemainsight;

import com.schemainsight.userinterface.SideBarManager;
import com.schemainsight.userinterface.TableViewManager;
import com.schemainsight.userinterface.TopBarManager;
import com.schemainsight.userinterface.sidebar.ConnectionSideBar;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.TableView;

import java.util.Map;

public class Main extends Application {

    private SideBarManager sideBarManager;
    private TableView<Map<String, String>> tableView = new TableView<>();
    private ConnectionSideBar connectionSideBar;

    @Override
    public void start(Stage primaryStage) {
        // Initialize the TableViewManager and SideBarManager first
        TableViewManager tableViewManager = new TableViewManager(tableView);
        sideBarManager = new SideBarManager(tableView);  // Initialize sideBarManager here
        connectionSideBar = new ConnectionSideBar(tableView);


        // Now pass the initialized sideBarManager to TopBarManager
        TopBarManager topBarManager = new TopBarManager(sideBarManager, tableView);
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 900, 650);

        scene.getStylesheets().add("styles.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("SchemaInsight");
        root.getStyleClass().add("main-pane");

        root.setCenter(tableView);

        // Initialize sidebars and top bar
        sideBarManager.initializeSidebars(root);
        topBarManager.initializeTopBar(root);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
