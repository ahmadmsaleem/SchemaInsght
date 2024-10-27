package com.schemainsight.processing;

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

        String[] headers = data.get(0).keySet().toArray(new String[0]);

        for (int rowCount = 0; rowCount < Math.min(data.size(), 500); rowCount++) {
            for (String header : headers) {
                String value = data.get(rowCount).get(header);
                columnDataTypes.put(header, updateDataType(columnDataTypes.get(header), value));
            }
        }

        printDataTypes(columnDataTypes);
        return columnDataTypes;
    }

    private static void printDataTypes(Map<String, String> dataTypes) {
        System.out.println("Detected Data Types:");
        for (Map.Entry<String, String> entry : dataTypes.entrySet()) {
            System.out.printf("Column: %s, Data Type: %s%n", entry.getKey(), entry.getValue());
        }
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
        if (isNormalInt(value)) return "INTEGER (NORMAL)"; // Check for NORMAL INTEGER
        if (isSmallInt(value)) return "INTEGER (SMALL)"; // Check for SMALL INTEGER
        if (isBigInt(value)) return "BIGINT";
        if (isFloat(value)) return "FLOAT";
        if (isDouble(value)) return "DOUBLE";
        if (isDate(value)) return "DATE";
        if (isTimestamp(value)) return "TIMESTAMP";
        return "VARCHAR";
    }

    private static String resolveTypeConflict(String existingType, String newType) {
        if (existingType == null) return newType;

        if (existingType.equals(newType)) return existingType;

        // Allow upgrading to more complex types, fallback to VARCHAR if there's a conflict
        if (existingType.equals("VARCHAR") || newType.equals("VARCHAR")) return "VARCHAR";
        if (existingType.startsWith("INTEGER") && newType.startsWith("INTEGER")) return "INTEGER (NORMAL)";
        if (existingType.equals("BIGINT") || newType.equals("BIGINT")) return "BIGINT";
        if (existingType.equals("FLOAT") && (newType.equals("FLOAT") || newType.equals("DOUBLE"))) return "FLOAT";
        if (existingType.equals("DOUBLE") || newType.equals("DOUBLE")) return "DOUBLE";
        if (existingType.equals("BOOLEAN") || newType.equals("BOOLEAN")) return "BOOLEAN";
        if (existingType.equals("DATE") || newType.equals("DATE")) return "DATE";
        if (existingType.equals("TIMESTAMP") || newType.equals("TIMESTAMP")) return "TIMESTAMP";

        return existingType; // Fallback to the most complex type
    }

    private static boolean isBoolean(String value) {
        return "true".equalsIgnoreCase(value.trim()) || "false".equalsIgnoreCase(value.trim());
    }

    private static boolean isNormalInt(String value) {
        try {
            int intValue = Integer.parseInt(value.trim());
            return intValue >= Byte.MIN_VALUE && intValue <= Byte.MAX_VALUE; // Check for SMALL
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isSmallInt(String value) {
        try {
            int intValue = Integer.parseInt(value.trim());
            return intValue >= Short.MIN_VALUE && intValue <= Short.MAX_VALUE; // Check for SMALL INT
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isBigInt(String value) {
        try {
            Long.parseLong(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isFloat(String value) {
        try {
            Float.parseFloat(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDouble(String value) {
        try {
            Double.parseDouble(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDate(String value) {
        try {
            java.time.LocalDate.parse(value.trim());
            return true;
        } catch (java.time.format.DateTimeParseException e) {
            return false;
        }
    }

    private static boolean isTimestamp(String value) {
        try {
            java.time.LocalDateTime.parse(value.trim());
            return true;
        } catch (java.time.format.DateTimeParseException e) {
            return false;
        }
    }
}
