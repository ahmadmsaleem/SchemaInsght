package com.schemainsight.processing;

public class CSVImportConfig {
    static char delimiter;
    static int columnCount;
    static int skipRows;
    private static String filePath;
    static String dateFormat;
    static String encoding;
    static int batchSize;
    static boolean trimWhitespace;
    static boolean enableHeader;

    public CSVImportConfig(char delimiter, int columnCount, int skipRows, String filePath, String dateFormat, String encoding, int batchSize, boolean trimWhitespace, boolean enableHeader) {
        CSVImportConfig.delimiter = delimiter;
        CSVImportConfig.columnCount = columnCount;
        CSVImportConfig.skipRows = skipRows;
        CSVImportConfig.filePath = filePath;
        CSVImportConfig.dateFormat = dateFormat;
        CSVImportConfig.encoding = encoding;
        CSVImportConfig.batchSize = batchSize;
        CSVImportConfig.trimWhitespace = trimWhitespace;
        CSVImportConfig.enableHeader = enableHeader;
    }

    public static char getDelimiter() {
        return delimiter;
    }

    public static int getColumnCount() {
        return columnCount;
    }

    public static int getSkipRows() {
        return skipRows;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static String getDateFormat() {
        return dateFormat;
    }

    public static String getEncoding() {
        return encoding;
    }

    public static int getBatchSize() {
        return batchSize;
    }

    public static boolean isTrimWhitespace() {
        return trimWhitespace;
    }

    public static boolean isEnableHeader() {
        return enableHeader;
    }
}
