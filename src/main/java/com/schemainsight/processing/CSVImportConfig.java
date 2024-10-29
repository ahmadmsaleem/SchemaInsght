package com.schemainsight.processing;

public class CSVImportConfig {
    static char delimiter;
    static int skipRows;
    static String dateFormat;
    static String timestampFormat;
    static String encoding;
    static int batchSize;
    static boolean trimWhitespace;
    static boolean enableHeader;

    public CSVImportConfig(char delimiter,
                           int skipRows, String dateFormat, String timestampFormat, String encoding,
                           int batchSize, boolean trimWhitespace, boolean enableHeader) {
        CSVImportConfig.delimiter = delimiter;
        CSVImportConfig.skipRows = skipRows;
        CSVImportConfig.dateFormat = dateFormat;
        CSVImportConfig.timestampFormat = timestampFormat;
        CSVImportConfig.encoding = encoding;
        CSVImportConfig.batchSize = batchSize;
        CSVImportConfig.trimWhitespace = trimWhitespace;
        CSVImportConfig.enableHeader = enableHeader;
    }

    public static char getDelimiter() {
        return delimiter;
    }

    public static int getSkipRows() {
        return skipRows;
    }

    public static String getDateFormat() {
        return dateFormat;
    }

    public static String getTimestampFormat() {
        return timestampFormat;
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
