package org.example;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DependencyGraph {
    // Map to store the dependencies for each file
    private final Map<String, List<String>> dependencies;

    private final String directoryPath;

    public DependencyGraph(String directory) {
        dependencies = new HashMap<>();
        directoryPath = directory;
        readFiles(directory);
    }

    /**
     * @return all files in the graph (directory)
     *
     */
    public Set<String> getFiles() {
        var files = new HashSet<String>();
        for (var entry : dependencies.entrySet()) {
            files.add(entry.getKey());
            files.addAll(entry.getValue());
        }
        return files;
    }


    // Recursive function to read files in a directory and its subdirectories
    /**
     * Read all files in the directory and store the dependencies in the map
     * @param directory path to the directory
     */
    private void readFiles(String directory) {
        File dir = new File(directory);

        // Get the list of files in the directory
        File[] files = dir.listFiles();
        if (files != null) {
            // Iterate over the files
            for (File file : files) {
                // If the file is a directory, recursively read its files
                if (file.isDirectory()) {
                    readFiles(file.getAbsolutePath());
                } else {
                    // If the file is a regular file, read it and add its dependencies to the map
                    readFile(file);
                }
            }
        }
    }

    // Read a file and add its dependencies to the map
    /**
     * Read a file and add its dependencies to the map
     * @param file file to read
     */
    private void readFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            List<String> fileDependencies = new ArrayList<>();

            // Read the file line by line
            while ((line = br.readLine()) != null) {
                // Check if the line starts with "require"
                if (line.startsWith("require '")) {
                    // Extract the dependency file name
                    String dependency = line.substring("require '".length()).trim();
                    fileDependencies.add(dependency.substring(0, dependency.length() - 1));
                }
            }

            // Add the dependencies to the map
            Path dirPath = Paths.get(this.directoryPath);
            Path filePath = Paths.get(file.getAbsolutePath());
            Path relativePath = dirPath.relativize(filePath);
            dependencies.put(relativePath.toString(), fileDependencies);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get the list of files in the required order
    /**
     * @return list of files in the topological order
     */
    public List<String> getFilesInOrder() {
        // Set to store the visited files
        Set<String> visited = new HashSet<>();

        // List to store the files in the required order
        List<String> files = new ArrayList<>();

        // Iterate over the dependencies map
        for (Map.Entry<String, List<String>> entry : dependencies.entrySet()) {
            String file = entry.getKey();
            List<String> fileDependencies = entry.getValue();

            // Recursively get the dependencies of the file
            getDependencies(file, fileDependencies, visited, files);
        }

        return files;
    }

    // Recursive function to get the dependencies of a file
    /**
     * Recursively get the dependencies of a file
     * @param file file to get the dependencies for
     * @param dependencies list of dependencies for the file
     * @param visited set of visited files
     * @param files list of files in the required order
     */
    private void getDependencies(String file, List<String> dependencies, Set<String> visited, List<String> files) {
        // If the file has already been visited, return
        if (visited.contains(file)) {
            return;
        }

        // Mark the file as visited
        visited.add(file);

        // Recursively get the dependencies of the file
        for (String dependency : dependencies) {
            getDependencies(dependency, this.dependencies.get(dependency), visited, files);
        }

        // Add the file to the list
        files.add(file);
    }

    // Get the cycle in the graph
    /**
     * @return set of files in the cycle
     */
    public Set<String> getCycle() {
        // Iterate over the dependencies map
        for (Map.Entry<String, List<String>> entry : dependencies.entrySet()) {
            String file = entry.getKey();

            // Set to store the visited files
            Set<String> visited = new HashSet<>();
            // Recursively check if the file has a cycle in its dependencies
            if (getCycle(file, visited) != null) {
                return visited;
            }
        }

        return null;
    }

    // Recursive function to check if a file has a cycle in its dependencies
    /**
     * Recursively check if a file has a cycle in its dependencies
     * @param file file to check
     * @param visited set of visited files (potential cycle)
     */
    private Set<String> getCycle(String file, Set<String> visited) {
        // If the file has already been visited, return true
        if (visited.contains(file)) {
            return visited;
        }

        // Mark the file as visited
        visited.add(file);

        // Recursively check the dependencies of the file
        List<String> fileDependencies = dependencies.get(file);
        if (fileDependencies != null) {
            for (String dependency : fileDependencies) {
                if (getCycle(dependency, visited) != null) {
                    return visited;
                }
                visited.clear();
                visited.add(file);
            }
        }

        // If the file has no cycles in its dependencies, return false
        return null;
    }

}
