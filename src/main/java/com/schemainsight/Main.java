package com.schemainsight;

import com.schemainsight.userinterface.SideBarManager;
import com.schemainsight.userinterface.TableViewManager;
import com.schemainsight.userinterface.TopBarManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.TableView;

import java.util.Map;

public class Main extends Application {

    private final TableView<Map<String, String>> tableView = new TableView<>();

    @Override
    public void start(Stage primaryStage) {
        TableViewManager tableViewManager = new TableViewManager(tableView);
        SideBarManager sideBarManager = new SideBarManager(tableView);


        TopBarManager topBarManager = new TopBarManager(sideBarManager, tableView);
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 900, 650);

        scene.getStylesheets().add("styles.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("SchemaInsight");
        root.getStyleClass().add("main-pane");

        root.setCenter(tableView);

        sideBarManager.initializeSidebars(root);
        topBarManager.initializeTopBar(root);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
