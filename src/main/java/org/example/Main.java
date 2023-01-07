package org.example;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String root;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the path to the directory: ");
        root = scanner.nextLine();

        FileProcessor processor = new FileProcessor(root);
        if (!processor.validateDirectory(root)) {
            System.out.println("Invalid directory");
            return;
        }

        DependencyGraph graph = new DependencyGraph(root);

        var cycle = graph.getCycle();

        if (cycle != null) {
            System.out.println("Cycle found");
            for (String s : cycle) {
                System.out.println(s);
            }
            return;
        }

        if (!processor.validateExistance(graph.getFiles())) {
            System.out.println("Some files don't exist");
            return;
        }

        List<String> files = graph.getFilesInOrder();
        for (String file : files) {
            System.out.println(file);
        }

        String content = processor.concatFiles(files);
        try {
            processor.writeToFile("output.txt", content);
            System.out.println("\nOutput written to output.txt:");
            System.out.println(content);
        } catch (Exception e) {
            System.out.println("Error writing to file");
        }
    }
}