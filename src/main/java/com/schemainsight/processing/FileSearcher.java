package com.schemainsight.processing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileSearcher {

    public Optional<List<String>> searchInFile(String filePath, String searchTerm, int limit) {
        List<String> foundLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                if (line.contains(searchTerm)) {
                    foundLines.add("Found at line " + lineNumber + ": " + line);
                    if (foundLines.size() >= limit) {
                        break;
                    }
                }
                lineNumber++;
            }

            return foundLines.isEmpty() ? Optional.empty() : Optional.of(foundLines);
        } catch (IOException e) {
            // You can use a logging framework here instead of printStackTrace
            System.err.println("Error reading file: " + e.getMessage());
            return Optional.empty();
        }
    }
}
