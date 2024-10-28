package com.schemainsight.processing;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CSVProcessor {

    private static final Character[] POSSIBLE_DELIMITERS = {
            ',', '.', ';', '=', ':', ' ', '–', '>', '<', '%'
    };

    // Method to detect delimiter from a file
    public static char detectDelimiter(String filePath) {
        Map<Character, Integer> delimiterCounts = new HashMap<>();
        try (Reader in = new FileReader(filePath)) {
            char[] buffer = new char[1024];
            int length = in.read(buffer);
            for (int i = 0; i < length; i++) {
                char ch = buffer[i];
                if (Arrays.asList(POSSIBLE_DELIMITERS).contains(ch)) {
                    delimiterCounts.put(ch, delimiterCounts.getOrDefault(ch, 0) + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return delimiterCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(';');
    }

    // Configuration Dialog to gather inputs
    public static Optional<CsvImportConfig> ConfigurationTable(char detectedDelimiter, String filePath) {
        Dialog<Optional<CsvImportConfig>> configurationDialog = new Dialog<>();



        configurationDialog.setTitle("Configuration Table");
        configurationDialog.setHeaderText("Configure your CSV import settings.");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // UI elements for the dialog
        TextField delimiterField = new TextField(String.valueOf(detectedDelimiter));
        TextField columnCountField = new TextField("10");
        TextField skipRowsField = new TextField("0"); // Allow users to skip initial rows
        TextField dateFormatField = new TextField("yyyy-MM-dd");
        TextField encodingField = new TextField("UTF-8"); // Default encoding, users can change
        TextField batchSizeField = new TextField("1000"); // How many rows per batch to process
        CheckBox trimWhitespaceCheckBox = new CheckBox("Trim whitespace");
        trimWhitespaceCheckBox.setSelected(true); // Default selection
        CheckBox enableHeaderCheckBox = new CheckBox("Use first row as header");
        enableHeaderCheckBox.setSelected(true); // Default selection

        // Add all fields and labels to the grid
        grid.add(new Label("Enter delimiter:"), 0, 1);
        grid.add(delimiterField, 1, 1);
        grid.add(new Label("Expected column count:"), 0, 2);
        grid.add(columnCountField, 1, 2);
        grid.add(new Label("Rows to skip:"), 0, 3);
        grid.add(skipRowsField, 1, 3);
        grid.add(new Label("Date format:"), 0, 4);
        grid.add(dateFormatField, 1, 4);
        grid.add(new Label("Encoding:"), 0, 5);
        grid.add(encodingField, 1, 5);
        grid.add(new Label("Batch size:"), 0, 6);
        grid.add(batchSizeField, 1, 6);
        grid.add(trimWhitespaceCheckBox, 0, 7, 2, 1);
        grid.add(enableHeaderCheckBox, 0, 8, 2, 1);

        configurationDialog.getDialogPane().setContent(grid);
        configurationDialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        configurationDialog.initStyle(StageStyle.UTILITY);

        configurationDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    // Retrieve input values from the dialog fields
                    CsvImportConfig.delimiter = delimiterField.getText().charAt(0);
                    CsvImportConfig.columnCount = Integer.parseInt(columnCountField.getText());
                    CsvImportConfig.skipRows = Integer.parseInt(skipRowsField.getText());
                    CsvImportConfig.dateFormat = dateFormatField.getText();
                    CsvImportConfig.encoding = encodingField.getText();
                    CsvImportConfig.batchSize = Integer.parseInt(batchSizeField.getText());
                    CsvImportConfig.trimWhitespace = trimWhitespaceCheckBox.isSelected();
                    CsvImportConfig.enableHeader = enableHeaderCheckBox.isSelected();

                    return Optional.of(new CsvImportConfig(
                            CsvImportConfig.delimiter,
                            CsvImportConfig.columnCount,
                            CsvImportConfig.skipRows,
                            filePath,
                            CsvImportConfig.dateFormat,
                            CsvImportConfig.encoding,
                            CsvImportConfig.batchSize,
                            CsvImportConfig.trimWhitespace,
                            CsvImportConfig.enableHeader
                    ));
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid numeric values.");
                    alert.initStyle(StageStyle.UTILITY);
                    alert.showAndWait();
                }
            }
            return Optional.empty();
        });
        Button okButton = (Button) configurationDialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.getStyleClass().add("ok");
        Button cancelButton = (Button) configurationDialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.getStyleClass().add("cancel");
        configurationDialog.getDialogPane().getStylesheets().add("styles.css");
        configurationDialog.getDialogPane().getStyleClass().add("myDialog");

        return configurationDialog.showAndWait().flatMap(result -> result);
    }


    public static List<String> getHeaders(String filePath, char delimiter) {
        try (Reader in = new FileReader(filePath)) {
            CSVParser parser = CSVFormat.DEFAULT.withDelimiter(delimiter).withFirstRecordAsHeader().parse(in);
            return new ArrayList<>(parser.getHeaderNames());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static List<Map<String, String>> readCSVFile(String filePath, char delimiter, int limit) {
        List<Map<String, String>> data = new ArrayList<>();

        try (Reader in = new FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(in)) {
            CSVParser parser = CSVFormat.DEFAULT
                    .withDelimiter(delimiter)
                    .withFirstRecordAsHeader()
                    .parse(bufferedReader);

            Map<String, Integer> headerMap = parser.getHeaderMap();
            int count = 0;

            for (CSVRecord record : parser) {
                if (count >= limit) break;

                Map<String, String> row = new LinkedHashMap<>();
                for (String header : headerMap.keySet()) {
                    row.put(header, record.get(header));
                }
                data.add(row);
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
    public static ValidationResult validateCSV(String filePath, char delimiter) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            if (line == null || line.trim().isEmpty()) {
                return new ValidationResult(false, "The file is empty or contains only whitespace.");
            }

            String[] columns = line.split(String.valueOf(delimiter));
            if (columns.length == 0 || Arrays.stream(columns).anyMatch(String::isEmpty)) {
                return new ValidationResult(false, "The header line does not contain valid column names.");
            }

            if (br.readLine() == null) {
                return new ValidationResult(true, "The CSV file is valid but contains only headers with no data.");
            }
            return new ValidationResult(true, "The CSV file is valid.");
        } catch (IOException e) {
            return new ValidationResult(false, "An error occurred while reading the file: " + e.getMessage());
        }
    }

    public static class ValidationResult {
        private final boolean isValid;

        public ValidationResult(boolean isValid, String message) {
            this.isValid = isValid;
        }

        public boolean isValid() {
            return isValid;
        }
    }
}
