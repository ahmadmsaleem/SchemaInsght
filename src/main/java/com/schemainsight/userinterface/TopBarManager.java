package com.schemainsight.userinterface;

import com.schemainsight.processing.FileSearcher;
import com.schemainsight.userinterface.sidebar.UploadSideBar;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TopBarManager {
    private final SideBarManager sideBarManager;
    private final TableView<Map<String, String>> tableView;

    public TopBarManager(SideBarManager sideBarManager, TableView<Map<String, String>> tableView) {
        this.sideBarManager = sideBarManager;
        this.tableView = tableView;
    }

    public void initializeTopBar(BorderPane root) {
        CustomButton toggleUploadSideBarButton = CustomButton.createTopButton("Upload");
        CustomButton tableInfoButton = CustomButton.createTopButton("Table Info");
        CustomButton connectionButton = CustomButton.createTopButton("Connection");
        CustomButton settingsButton = CustomButton.createTopButton("Settings");

        CustomButton searchButton = CustomButton.createTopButton("Search");
        searchButton.setOnAction(event -> handleSearch());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toggleUploadSideBarButton.setOnAction(event -> sideBarManager.toggleSideBar(root, "upload"));
        tableInfoButton.setOnAction(event -> sideBarManager.toggleSideBar(root, "tableInfo"));
        connectionButton.setOnAction(event -> sideBarManager.toggleSideBar(root, "connection"));

        HBox topBarBox = new HBox(10, toggleUploadSideBarButton, tableInfoButton, connectionButton, spacer, searchButton, settingsButton);
        topBarBox.getStyleClass().add("top-bar-box");
        root.setTop(topBarBox);
    }

    private void handleSearch() {
        String latestFilePath = UploadSideBar.getLatestFilePath();

        if (latestFilePath == null) {
            showDialog("Warning", "No file uploaded.", "Please upload a file before searching.");
            return;
        }

        Dialog<ButtonType> searchDialog = createSearchDialog();
        Optional<ButtonType> result = searchDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            TextField searchTermField = (TextField) searchDialog.getDialogPane().lookup(".search-term-field");
            TextField batchSizeField = (TextField) searchDialog.getDialogPane().lookup(".batch-size-field");

            String searchTerm = searchTermField.getText().trim();
            String batchTerm = batchSizeField.getText().trim();

            if (searchTerm.isEmpty()) {
                showDialog("Warning", "Invalid Input", "Search term cannot be empty.");
                return;
            }

            // Check if batchTerm is a valid integer
            if (!isValidInteger(batchTerm)) {
                showDialog("Warning", "Invalid Batch Size", "Please enter a valid number for batch size.");
                return;
            }
            int batchSize = Integer.parseInt(batchTerm);

            FileSearcher fileSearcher = new FileSearcher();
            List<Map<String, String>> filteredRows = fileSearcher.searchInFileAsMap(latestFilePath, searchTerm, batchSize);

            if (filteredRows.isEmpty()) {
                showDialog("Info", "No matches found.", "No rows matched the search term.");
            } else {
                tableView.getItems().clear();
                tableView.getItems().addAll(filteredRows);
            }
        }
    }

    private void showDialog(String title, String headerText, String contentText) {
        Dialog<Void> warningDialog = new Dialog<>();
        warningDialog.setTitle(title);
        warningDialog.setHeaderText(headerText);
        warningDialog.setContentText(contentText);
        warningDialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
        warningDialog.getDialogPane().getStylesheets().add("styles.css");
        warningDialog.getDialogPane().getStyleClass().add("myDialog");
        warningDialog.initStyle(StageStyle.UTILITY);
        warningDialog.setWidth(350);
        warningDialog.setHeight(280);
        Button okButton = (Button) warningDialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.getStyleClass().add("ok");

        warningDialog.showAndWait();
    }

    private Dialog<ButtonType> createSearchDialog() {
        Dialog<ButtonType> searchDialog = new Dialog<>();
        searchDialog.setTitle("Search");
        searchDialog.setHeaderText("Enter search details.");

        TextField searchTermField = new TextField();
        searchTermField.setPromptText("Search:");
        searchTermField.getStyleClass().add("search-term-field");
        TextField batchSizeField = new TextField("1000");
        batchSizeField.setPromptText("Batch:");
        batchSizeField.getStyleClass().add("batch-size-field");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Search Term:"), 0, 0);
        grid.add(searchTermField, 1, 0);
        grid.add(new Label("Batch Size:"), 0, 1);
        grid.add(batchSizeField, 1, 1);

        searchDialog.getDialogPane().setContent(grid);
        searchDialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        searchDialog.getDialogPane().getStylesheets().add("styles.css");
        searchDialog.getDialogPane().getStyleClass().add("myDialog");
        searchDialog.initStyle(StageStyle.UTILITY);
        searchDialog.setWidth(350);
        searchDialog.setHeight(280);
        Button okButton = (Button) searchDialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.getStyleClass().add("ok");

        Button cancelButton = (Button) searchDialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.getStyleClass().add("cancel");

        return searchDialog;
    }

    private boolean isValidInteger(String str) {
        return str != null && str.matches("\\d+");
    }
}
