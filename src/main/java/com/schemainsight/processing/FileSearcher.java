package com.schemainsight.processing;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class FileSearcher {

    public List<Map<String, String>> searchInFileAsMap(String filePath, String searchTerm, int batchTerm) {
        List<Map<String, String>> matchedRows = new ArrayList<>();

        try (Reader reader = new FileReader(filePath);
             CSVParser parser = CSVFormat.DEFAULT
                     .withDelimiter(CSVImportConfig.getDelimiter()) // Assuming `CSVImportConfig` has a method to get delimiter
                     .withFirstRecordAsHeader()
                     .parse(reader)) {

            // Retrieve headers from the CSV file
            List<String> headers = parser.getHeaderNames();

            // Iterate over each record
            for (CSVRecord record : parser) {
                if (record.toString().contains(searchTerm)) {
                    Map<String, String> rowMap = new LinkedHashMap<>();

                    for (String header : headers) {
                        rowMap.put(header, record.get(header));
                    }
                    matchedRows.add(rowMap);

                    if (batchTerm > 0 && matchedRows.size() >= batchTerm) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return matchedRows;
    }
}
