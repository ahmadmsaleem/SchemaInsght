package com.schemainsight.userinterface;

import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;
import javafx.scene.control.SelectionMode;

import java.util.List;
import java.util.Map;

public class TableViewManager {

    private final TableView<Map<String, String>> tableView;

    public TableViewManager(TableView<Map<String, String>> tableView) {
        this.tableView = tableView;
        initializeTableView();
    }

    private void initializeTableView() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Tooltip tooltip = new Tooltip("Content copied");
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (tableView.getSelectionModel().getSelectedItems().size() > 1) {
                copySelectedRowsToClipboard();
                showTooltip(tooltip);
            }
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY
                    && tableView.getSelectionModel().getSelectedItems().size() == 1) {
                copySelectedRowsToClipboard();
                showTooltip(tooltip, event.getScreenX(), event.getScreenY(), "Row copied");
            }
            else if (event.getClickCount() == 1 && event.getButton() == MouseButton.SECONDARY
                    && tableView.getSelectionModel().getSelectedItems().size() == 1) {
                Object cellValue = tableView.getSelectionModel().getSelectedItem()
                        .get(tableView.getFocusModel().getFocusedCell().getTableColumn().getText());
                if (cellValue != null) {
                    copyToClipboard(cellValue.toString());
                    showTooltip(tooltip, event.getScreenX(), event.getScreenY(), "Cell copied");
                }
            }
        });
    }

    private void copyToClipboard(String content) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        clipboard.setContent(clipboardContent);
    }

    private void showTooltip(Tooltip tooltip, double mouseX, double mouseY, String message) {
        tooltip.setText(message);
        tooltip.show(tableView.getScene().getWindow(), mouseX + 10, mouseY + 10);
        tooltip.setAutoHide(true);
        tooltip.setHideDelay(Duration.seconds(1));
    }

    private void copySelectedRowsToClipboard() {
        List<Map<String, String>> selectedRows = tableView.getSelectionModel().getSelectedItems();
        if (!selectedRows.isEmpty()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();

            StringBuilder rowData = new StringBuilder();
            String headers = String.join(";", selectedRows.getFirst().keySet());
            rowData.append(headers).append("\n");

            for (Map<String, String> row : selectedRows) {
                String rowString = String.join(";", row.values());
                rowData.append(rowString).append("\n");
            }

            content.putString(rowData.toString().trim());
            clipboard.setContent(content);
        }
    }

    private void showTooltip(Tooltip tooltip) {
        double sceneCenterX = tableView.getScene().getWindow().getX() + tableView.getScene().getWidth() / 2;
        double sceneCenterY = tableView.getScene().getWindow().getY() + tableView.getScene().getHeight() / 2;
        tooltip.show(tableView.getScene().getWindow(), sceneCenterX - tooltip.getWidth() / 2, sceneCenterY - tooltip.getHeight() / 2);
        tooltip.setAutoHide(true);
        tooltip.setHideDelay(Duration.seconds(1));
    }



}
