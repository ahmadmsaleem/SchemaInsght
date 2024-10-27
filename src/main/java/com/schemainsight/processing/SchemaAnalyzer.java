package com.schemainsight.processing;

import java.util.*;
import java.util.stream.Collectors;

public class SchemaAnalyzer {

    private final List<Map<String, String>> data;

    public SchemaAnalyzer(List<Map<String, String>> data) {
        this.data = data;
    }

    /**
     * Analyzes the schema of the provided data.
     * @return A map containing null counts and unique counts for each column.
     */
    public Map<String, Object> analyzeSchema() {
        Map<String, Object> schemaReport = new HashMap<>();
        Map<String, Integer> nullCounts = new HashMap<>();
        Map<String, Set<String>> uniqueValues = new HashMap<>();

        for (Map<String, String> row : data) {
            for (String column : row.keySet()) {
                String value = row.get(column);

                // Count nulls
                nullCounts.put(column, nullCounts.getOrDefault(column, 0) + (value == null || value.trim().isEmpty() ? 1 : 0));

                // Collect unique values
                uniqueValues.putIfAbsent(column, new HashSet<>());
                if (value != null && !value.trim().isEmpty()) {
                    uniqueValues.get(column).add(value);
                }
            }
        }

        schemaReport.put("nullCounts", nullCounts);
        schemaReport.put("uniqueCounts", getUniqueCounts(uniqueValues));

        return schemaReport;
    }

    /**
     * Gets unique counts for each column.
     * @param uniqueValues A map containing sets of unique values for each column.
     * @return A map with the count of unique values per column.
     */
    private Map<String, Integer> getUniqueCounts(Map<String, Set<String>> uniqueValues) {
        Map<String, Integer> uniqueCounts = new HashMap<>();
        for (String column : uniqueValues.keySet()) {
            uniqueCounts.put(column, uniqueValues.get(column).size());
        }
        return uniqueCounts;
    }

    /**
     * Retrieves rows where the specified column is null.
     * @param column The name of the column to check for null values.
     * @return A list of rows with null values in the specified column.
     */
    public List<Integer> getRowsWithNullsIndices(String column) {
        List<Integer> indicesWithNulls = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            Map<String, String> row = data.get(i);
            if (row.get(column) == null || row.get(column).trim().isEmpty()) {
                indicesWithNulls.add(i);
            }
        }
        return indicesWithNulls;
    }

    /**
     * Retrieves rows with a specific unique value in the specified column.
     * @param column The column to check for the value.
     * @param value The value to look for.
     * @return A list of rows that contain the specified unique value.
     */
    public List<Map<String, String>> getRowsWithUniqueValue(String column, String value) {
        List<Map<String, String>> rowsWithValue = new ArrayList<>();
        for (Map<String, String> row : data) {
            if (value.equals(row.get(column))) {
                rowsWithValue.add(row);
            }
        }
        return rowsWithValue;
    }

    /**
     * Retrieves rows with duplicated values for a specific column.
     * @param column The name of the column to check for duplicates.
     * @return A list of rows that have duplicate values in the specified column.
     */
    public List<Map<String, String>> getDuplicateRows(String column) {
        Map<String, List<Map<String, String>>> duplicates = new HashMap<>();

        for (Map<String, String> row : data) {
            String value = row.get(column);
            if (value != null) {
                String normalizedValue = value.trim().toLowerCase(); // Normalize to lower case and trim spaces
                duplicates.putIfAbsent(normalizedValue, new ArrayList<>());
                duplicates.get(normalizedValue).add(row);
            }
        }

        // Return the list of duplicate values or simply count them
        return duplicates.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1) // Only keep duplicates
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());
    }
}
