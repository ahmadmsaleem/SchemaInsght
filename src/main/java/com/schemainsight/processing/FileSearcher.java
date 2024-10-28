package com.schemainsight.processing;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class FileSearcher {

    public List<Map<String, String>> searchInFileAsMap(String filePath, String searchTerm, int batchTerm) {
        List<Map<String, String>> matchedRows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            if (line == null) return matchedRows;

            String[] headers = line.split(String.valueOf(CSVImportConfig.getDelimiter()));

            while ((line = br.readLine()) != null) {
                if (line.contains(searchTerm)) {
                    String[] values = line.split(String.valueOf(CSVImportConfig.getDelimiter()));
                    Map<String, String> rowMap = new LinkedHashMap<>();
                    for (int i = 0; i < headers.length; i++) {
                        rowMap.put(headers[i], i < values.length ? values[i] : "");
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
