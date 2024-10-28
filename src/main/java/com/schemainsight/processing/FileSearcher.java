package com.schemainsight.processing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public class FileSearcher {

    public Optional<String> searchInFile(String filePath, String searchTerm) {
        StringBuilder foundLines = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            boolean found = false;

            while ((line = br.readLine()) != null) {
                if (line.contains(searchTerm)) {
                    foundLines.append("Found at line ").append(lineNumber).append(": ").append(line).append("\n");
                    found = true;
                }
                lineNumber++;
            }

            return found ? Optional.of(foundLines.toString()) : Optional.empty();
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
