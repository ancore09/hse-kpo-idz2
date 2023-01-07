package org.example;

import java.io.*;
import java.util.Collection;

public class FileProcessor {
    private final String directoryPath;
    public FileProcessor(String directory) {
        this.directoryPath = directory;
    }

    /**
     * @return true if all files exist, false otherwise
     */
    public boolean validateExistance(Collection<String> filePaths) {
        for (String filePath : filePaths) {
            File file = new File(directoryPath + '/' + filePath);
            if (!file.exists()) {
                return false;
            }
        }
        return true;
    }

    // concatenate files contents in the order they are in the list
    /**
     * Concatenate files contents in the order they are in the list
     * @param filePaths list of files to concatenate
     * @return concatenated files contents
     */
    public String concatFiles(Collection<String> filePaths) {
        StringBuilder content = new StringBuilder();
        for (String filePath : filePaths) {
            try (BufferedReader br = new BufferedReader(new FileReader(directoryPath + '/' + filePath))) {
                String line;
                content.append(filePath).append("\n");
                while ((line = br.readLine()) != null) {
                    content.append(line).append('\n');
                }
                content.append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content.toString();
    }

    // write to file
    /**
     * Write the content to the file
     * @param filePath path to the desired file
     * @param content content to write
     * @throws IOException if an error occurs while writing to the file
     */
    public void writeToFile(String filePath, String content) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write(content);
        writer.close();
    }

}
