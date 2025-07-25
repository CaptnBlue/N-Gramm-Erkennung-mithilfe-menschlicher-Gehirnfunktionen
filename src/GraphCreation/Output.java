package GraphCreation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Output {

    public static void outputAdjacencyList(Map<String, Map<String, ?>> adjacencyList) {
        // Adjazenzliste in Datei schreiben
        File outputDir = new File("output/graph");
        outputDir.mkdirs();
        File outputFile = new File(outputDir, "adjacency_list.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (String from : adjacencyList.keySet()) {
                Map<String, Integer> neighbors = (Map<String, Integer>) adjacencyList.get(from);
                if (!neighbors.isEmpty()) {
                    writer.write(from + " -> ");
                    List<String> neighborStrings = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry : neighbors.entrySet()) {
                        neighborStrings.add(entry.getKey() + "(" + entry.getValue() + ")");
                    }
                    writer.write(String.join(", ", neighborStrings));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
        }
    }

    public static void saveToFile(Map<String, Map<String, Edge>> map) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output/graph/adjacency_list.txt"))) {
            for (Map.Entry<String, Map<String, Edge>> outer : map.entrySet()) {
                String from = outer.getKey();
                for (Map.Entry<String, Edge> inner : outer.getValue().entrySet()) {
                    Edge edge = inner.getValue();
                    writer.write(from + ";;" + edge.getTo() + ";;" + String.join(",", edge.getContextHashes()));
                    writer.newLine();
                }
            }
        }
    }

    public static Map<String, Map<String, Edge>> loadFromFile() throws IOException {
        Map<String, Map<String, Edge>> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("output/graph/adjacency_list.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";;");
                if (parts.length < 3) {
                    continue;
                }
                String from = parts[0];
                String to = parts[1];
                Set<String> hashes = new HashSet<>(Arrays.asList(parts[2].split(",")));

                Edge edge = new Edge(from, to);
                edge.setContextHashes(hashes);

                map.computeIfAbsent(from, k -> new HashMap<>()).put(to, edge);
            }
        }
        return map;
    }

}
