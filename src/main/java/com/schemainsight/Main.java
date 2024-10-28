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

    private SideBarManager sideBarManager;
    private TableView<Map<String, String>> tableView = new TableView<>();
    private TableViewManager tableViewManager;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 900, 650);
        scene.getStylesheets().add("styles.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("SchemaInsight - Data Engineering Tool");
        root.getStyleClass().add("main-pane");

        root.setCenter(tableView);
        sideBarManager = new SideBarManager(tableView);
        sideBarManager.initializeSidebars(primaryStage, root);

        TopBarManager topBarManager = new TopBarManager(sideBarManager, tableView);
        topBarManager.initializeTopBar(root);

        tableViewManager = new TableViewManager(tableView);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
