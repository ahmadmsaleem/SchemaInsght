package com.schemainsight.userinterface;

import com.schemainsight.processing.FileSearcher;
import com.schemainsight.userinterface.sidebar.UploadSideBar;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.StageStyle;

import java.util.Optional;

public class TopBarManager {

    private final SideBarManager sideBarManager;

    public TopBarManager(SideBarManager sideBarManager) {
        this.sideBarManager = sideBarManager;
    }


    public void initializeTopBar(BorderPane root) {
        CustomButton toggleUploadSideBarButton = CustomButton.createTopButton("Upload");
        CustomButton tableInfoButton = CustomButton.createTopButton("Table Info");
        CustomButton connectionButton = CustomButton.createTopButton("Connection");
        CustomButton settingsButton = CustomButton.createTopButton("Settings"); // New settings button

        // Create the search button
        CustomButton searchButton = CustomButton.createTopButton("Search");
        searchButton.setOnAction(event -> handleSearch());

        // Create a Region to push the search button to the far right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Make the spacer grow

        // Create the top bar layout
        HBox topBarBox = new HBox(10, toggleUploadSideBarButton, tableInfoButton, connectionButton, spacer, searchButton, settingsButton);
        topBarBox.getStyleClass().add("top-bar-box");
        root.setTop(topBarBox);
    }

    private void handleSearch() {
        String latestFilePath = UploadSideBar.getLatestFilePath(); // Get the latest file path

        if (latestFilePath == null) {
            showDialog("Warning", "No file uploaded.", "Please upload a file before searching.");
            return;
        }

        // Prompt user for search term and batch size
        Dialog<ButtonType> searchDialog = createSearchDialog();
        Optional<ButtonType> result = searchDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            TextField searchTermField = (TextField) searchDialog.getDialogPane().lookup(".text-field"); // Find the search term field
            if (!searchTermField.getText().trim().isEmpty()) {
                String searchTerm = searchTermField.getText(); // Get the search term

                FileSearcher fileSearcher = new FileSearcher();
                Optional<String> searchResult = fileSearcher.searchInFile(latestFilePath, searchTerm);

                System.out.println("Search Term: " + searchTerm);
                System.out.println("Search Result: " + searchResult.orElse("No matches found."));
            }
        }
    }

    private void handleSettings() {
        // Implement settings dialog or action here
        System.out.println("Settings button clicked");
    }

    private void showDialog(String title, String headerText, String contentText) {
        Dialog<Void> warningDialog = new Dialog<>();
        warningDialog.setTitle(title);
        warningDialog.setHeaderText(headerText);
        warningDialog.setContentText(contentText);
        warningDialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK); // Add the OK button
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

        // UI elements for the dialog
        TextField searchTermField = new TextField();
        searchTermField.setPromptText("Search for:");
        TextField batchSizeField = new TextField("1000");
        batchSizeField.setPromptText("Batch size:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Add labels and fields to the grid
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
}
