package com.schemainsight.userinterface;

import com.schemainsight.processing.DataLoader;
import com.schemainsight.userinterface.sidebar.ConnectionSideBar;
import com.schemainsight.userinterface.sidebar.TableInfoSideBar;
import com.schemainsight.userinterface.sidebar.UploadSideBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.util.Map;

public class SideBarManager {

    private final UploadSideBar uploadSideBarManager;
    private final TableInfoSideBar tableInfoSideBar;
    private final ConnectionSideBar connectionSideBar;
    private final DataLoader dataLoader;
    private static final double MIN_SIDEBAR_WIDTH = 130;
    private static final double MAX_SIDEBAR_WIDTH = 350;

    public SideBarManager(TableView<Map<String, String>> tableView) {
        Label sideBarLabel = new Label();

        this.tableInfoSideBar = new TableInfoSideBar(tableView, sideBarLabel);
        this.dataLoader = new DataLoader(tableView, sideBarLabel, tableInfoSideBar);
        this.uploadSideBarManager = new UploadSideBar(sideBarLabel, dataLoader::loadData, dataLoader);
        this.connectionSideBar = new ConnectionSideBar();
    }

    public void initializeSidebars(Stage primaryStage, BorderPane root) {
        VBox initialSideBar = uploadSideBarManager.getSidebar();
        root.setLeft(initialSideBar);
        addWidthListener(root, initialSideBar);
    }

    // Method to add a width listener to any sidebar
    private void addWidthListener(BorderPane root, VBox sideBar) {
        root.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double calculatedWidth = newWidth.doubleValue() * 0.25;

            if (calculatedWidth < MIN_SIDEBAR_WIDTH) {
                sideBar.setPrefWidth(MIN_SIDEBAR_WIDTH);
            } else if (calculatedWidth > MAX_SIDEBAR_WIDTH) {
                sideBar.setPrefWidth(MAX_SIDEBAR_WIDTH);
            } else {
                sideBar.setPrefWidth(calculatedWidth);
            }
        });
    }

    public void toggleSideBar(BorderPane root, String sideBarType) {
        VBox selectedSidebar = switch (sideBarType) {
            case "upload" -> uploadSideBarManager.getSidebar();
            case "tableInfo" -> tableInfoSideBar.getSidebar();
            case "connection" -> connectionSideBar.getSidebar();
            default -> null;
        };

        if (selectedSidebar != null) {
            if (selectedSidebar.equals(root.getLeft())) {
                root.setLeft(null);
            } else {
                root.setLeft(selectedSidebar);
                addWidthListener(root, selectedSidebar);
            }
        }
    }
}
