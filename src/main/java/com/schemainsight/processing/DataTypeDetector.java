package com.schemainsight.processing;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataTypeDetector {

    public static Map<String, String> detectDataTypes(String filePath, char delimiter) {
        Map<String, String> columnDataTypes = new LinkedHashMap<>();
        List<Map<String, String>> data = CSVProcessor.readCSVFile(filePath, delimiter, 500);

        if (data.isEmpty()) {
            return columnDataTypes;
        }

        String[] headers = data.getFirst().keySet().toArray(new String[0]);
        for (int rowCount = 0; rowCount < Math.min(data.size(), 500); rowCount++) {
            for (String header : headers) {
                String value = data.get(rowCount).get(header);
                String detectedType = updateDataType(columnDataTypes.get(header), value);
                columnDataTypes.put(header, detectedType);
            }
        }

        return columnDataTypes;
    }



    private static String updateDataType(String existingType, String value) {
        if (value == null || value.trim().isEmpty()) {
            return existingType != null ? existingType : "VARCHAR";
        }

        String newType = determineDataType(value);
        return resolveTypeConflict(existingType, newType);
    }

    private static String determineDataType(String value) {
        if (isBoolean(value)) return "BOOLEAN";
        if (isFloat(value)) return "FLOAT";
        if (isDate(value)) return "DATE";
        if (isTimestamp(value)) return "TIMESTAMP";
        return "VARCHAR";
    }

    private static String resolveTypeConflict(String existingType, String newType) {
        if (existingType == null) return newType;

        if (existingType.equals(newType)) return existingType;

        // Allow upgrading to more complex types, fallback to VARCHAR if there's a conflict
        if (existingType.equals("VARCHAR") || newType.equals("VARCHAR")) return "VARCHAR";
        if (existingType.equals("FLOAT") && newType.equals("DOUBLE")) return "FLOAT";
        if (existingType.equals("BOOLEAN") || newType.equals("BOOLEAN")) return "BOOLEAN";
        if (existingType.equals("DATE") || newType.equals("DATE")) return "DATE";
        if (existingType.equals("TIMESTAMP") || newType.equals("TIMESTAMP")) return "TIMESTAMP";

        return existingType;
    }

    private static boolean isBoolean(String value) {
        return "true".equalsIgnoreCase(value.trim()) || "false".equalsIgnoreCase(value.trim());
    }

    private static boolean isFloat(String value) {
        try {
            Float.parseFloat(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDate(String value) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                    CSVImportConfig.getDateFormat() != null ? CSVImportConfig.getDateFormat() : "yyyy-MM-dd"
            );
            LocalDate.parse(value.trim(), formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static boolean isTimestamp(String value) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                    CSVImportConfig.getTimestampFormat() != null ? CSVImportConfig.getTimestampFormat() : "yyyy-MM-dd HH:mm:ss"
            );
            LocalDateTime.parse(value.trim(), formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
