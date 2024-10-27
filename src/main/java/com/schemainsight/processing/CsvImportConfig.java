package com.schemainsight.processing;

public class CsvImportConfig {
    static char delimiter;
    static int columnCount;
    static int skipRows;
    private static String filePath;
    static String dateFormat;
    static String encoding;
    static int batchSize;
    static boolean trimWhitespace;
    static boolean enableHeader;

    public CsvImportConfig(char delimiter, int columnCount, int skipRows, String filePath, String dateFormat, String encoding, int batchSize, boolean trimWhitespace, boolean enableHeader) {
        CsvImportConfig.delimiter = delimiter;
        CsvImportConfig.columnCount = columnCount;
        CsvImportConfig.skipRows = skipRows;
        CsvImportConfig.filePath = filePath;
        CsvImportConfig.dateFormat = dateFormat;
        CsvImportConfig.encoding = encoding;
        CsvImportConfig.batchSize = batchSize;
        CsvImportConfig.trimWhitespace = trimWhitespace;
        CsvImportConfig.enableHeader = enableHeader;
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
