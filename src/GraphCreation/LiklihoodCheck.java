package GraphCreation;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import Shared.AdjustingText;
import Shared.Constants;

public class LiklihoodCheck {
    // Sequnece length beachtung hier
    public static void checkProbability() throws IOException {
        System.out.println("History: " + Constants.HISTORY);
        int minPerLength = 40;
        int maxLength = 20;

        Map<String, Map<String, Edge>> graph = Output.loadFromFile();
        List<String> graphKeys = new ArrayList<>(graph.keySet());
        Random random = new Random();

        // Map: Wortanzahl
        Map<Integer, int[]> stats = new HashMap<>();

        while (true) {
            String randomInput = graphKeys.get(random.nextInt(graphKeys.size()));
            String sent = TraverseGraph.generateSequence(randomInput, graph);
            if (sent == null || sent.isBlank()) {
                continue;
            }
            String generated = sent; // ausgelagerte funktion nach traverse graph um Satzzeichen hinten anzufügen
            int wordCount = countWordsWithEnding(generated);

            if (wordCount > maxLength || wordCount <= 0) {
                continue;
            }
            boolean isDuplicate = isInCorpus(generated, Paths.get(Constants.corpus)); // lazylode damit arbeitsspeicher
                                                                                      // gespart wird
            // System.out.println(randomInput + " | " + generated + (isDuplicate ? //Ausgabe
            // mit zusatz Duplikat oder nicht
            // "(DUPLIKAT)" : ""));

            stats.putIfAbsent(wordCount, new int[2]);
            stats.get(wordCount)[0]++; // total
            if (isDuplicate) {
                stats.get(wordCount)[1]++; // duplicates
            }
            // Abbruchbedingung prüfen
            boolean allMet = true;
            for (int i = 2; i <= maxLength; i++) { // bei veränderung der ngramm größen i auf kleinstmögliches ändern
                int total = stats.getOrDefault(i, new int[2])[0];
                if (total < minPerLength) {
                    allMet = false;
                    break;
                }
            }

            if (allMet) {
                break;
            }
        }

        System.out.println("\nErgebnisse:");
        AtomicInteger totalSentences = new AtomicInteger(0); // atomic damit innerhalb stream bearbeiten möglich
        AtomicInteger totalWords = new AtomicInteger(0);

        stats.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    int length = entry.getKey();
                    int total = entry.getValue()[0];
                    int duplicates = entry.getValue()[1];
                    double uniqueness = (double) (total - duplicates) / total * 100;
                    System.out.printf(Locale.US, "(%d, %.2f)", length, uniqueness); // us für . statt , für latex
                    totalSentences.addAndGet(total);
                    totalWords.addAndGet(length * total);
                });

        System.out.println();// Ausgabe in Latex Format
        if (totalSentences.get() > 0) {
            double averageLength = (double) totalWords.get() / totalSentences.get();
            System.out.printf(Locale.US, "Durchschnittliche Satzlänge: %.2f Wörter%n", averageLength);
        } else {
            System.out.println("Keine Sätze generiert.");
        }
    }

    public static void checkProbability2() throws IOException {
        System.out.println("History: " + Constants.HISTORY);
        int iterations = 100;
        Map<String, Map<String, Edge>> graph = Output.loadFromFile();

        List<String> inputSentences = new ArrayList<>();
        try {
            String corpusPath = Constants.corpus;
            String corpusString = Files.readString(Paths.get(corpusPath));
            inputSentences = AdjustingText.cleanSentences(corpusString);
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Datei: " + e.getMessage());
            return;
        }

        // Wähle zufälligen Start-Input aus den ersten Keys im Graph
        List<String> graphKeys = new ArrayList<>(graph.keySet());
        int limit = graphKeys.size();
        Random random = new Random();

        int duplicateCount = 0;

        for (int i = 0; i < iterations; i++) {
            String randomInput = graphKeys.get(random.nextInt(limit));

            // Generiere Satz
            String sent = TraverseGraph.generateSequence(randomInput, graph);
            if (sent == null || sent.isBlank()) {
                continue;
            }
            final String generated = sent.trim().replaceAll("\\s+([.!?])$", "$1");

            // Vergleich mit Korpus
            boolean isDuplicate = inputSentences.stream()
                    .anyMatch(sentence -> sentence.contains(generated));
            if (isDuplicate) {
                duplicateCount++;
            }
            System.out.println("[" + (i + 1) + "] " + generated + (isDuplicate ? "(DUPLIKAT)" : ""));
        }
        double duplicateProbability = (double) duplicateCount / iterations;
        System.out.printf("Wahrscheinlichkeit für nicht-einzigartige Sätze: %.2f%%%n", duplicateProbability * 100);
    }

    private static int countWordsWithEnding(String sentence) {
        if (sentence == null || sentence.isBlank()) {
            return 0;
        }
        String[] words = sentence.trim().split("\\s+");
        int count = words.length;

        // Prüfe, ob Satz mit . ? oder ! endet (zählt als weiteres „Wort“)
        if (sentence.matches(".*[.!?]$")) {
            count++;
        }
        return count;
    }

    private static boolean isInCorpus(String sentence, Path corpusPath) {
        try (BufferedReader reader = Files.newBufferedReader(corpusPath)) {
            String line;
            int lineCount = 0;

            while ((line = reader.readLine()) != null && lineCount < 50000) { // Begrenzung für stäze falls nicht
                                                                              // benutzt
                                                                              // lineCOunt auskommentieren
                // lineCount++;
                List<String> sentences = AdjustingText.cleanSentences(line);
                for (String s : sentences) {
                    if (s.contains(sentence)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen des Korpus: " + e.getMessage());
        }
        return false;
    }
}
