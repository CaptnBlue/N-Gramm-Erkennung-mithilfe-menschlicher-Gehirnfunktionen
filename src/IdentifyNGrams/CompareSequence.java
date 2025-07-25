package IdentifyNGrams;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

import javax.swing.JFrame;

public class CompareSequence {

    // Vergleichmethoden für LTM
    public static void compareSequences(String path1, String path2) throws IOException {
        String gemeinsameDatei = "gemeinsameSequenzen.txt";
        String unterschiedlicheDatei = "nichtGemeinsameSequenzen.txt";

        Map<String, String> map1 = new HashMap<>();
        Map<String, String> map2 = new HashMap<>();
        Pattern sequenzPattern = Pattern.compile("Sequenz: (.*?),");
        Pattern leadNumPattern = Pattern.compile("^(\\d+)");
        Pattern tagNumPattern = Pattern.compile("^\\[Datei[12]\\]\\s*(\\d+)");

        // Einlesen beider LTMs in map1 / map2
        for (String line : Files.readAllLines(Paths.get(path1))) {
            Matcher m = sequenzPattern.matcher(line);
            if (m.find()) {
                map1.put(m.group(1).trim(), line);
            }
        }
        for (String line : Files.readAllLines(Paths.get(path2))) {
            Matcher m = sequenzPattern.matcher(line);
            if (m.find()) {
                map2.put(m.group(1).trim(), line);
            }
        }

        // Gemeinsame und unterschiedliche Zeilen sammeln
        List<String> gemeinsameZeilen = new ArrayList<>();
        List<String> unterschiedlicheZeilen = new ArrayList<>();
        Set<String> alleSequenzen = new HashSet<>();
        alleSequenzen.addAll(map1.keySet());
        alleSequenzen.addAll(map2.keySet());

        for (String seq : alleSequenzen) {
            boolean in1 = map1.containsKey(seq);
            boolean in2 = map2.containsKey(seq);
            if (in1 && in2) {
                gemeinsameZeilen.add(map1.get(seq));
            } else {
                if (in1)
                    unterschiedlicheZeilen.add("[Datei1] " + map1.get(seq));
                if (in2)
                    unterschiedlicheZeilen.add("[Datei2] " + map2.get(seq));
            }
        }

        // Durchschnitt berechnen
        List<Integer> keys = new ArrayList<>();
        for (String line : unterschiedlicheZeilen) {
            Matcher mTag = tagNumPattern.matcher(line);
            if (mTag.find()) {
                keys.add(Integer.parseInt(mTag.group(1)));
            } else {
                Matcher mLead = leadNumPattern.matcher(line);
                if (mLead.find()) {
                    keys.add(Integer.parseInt(mLead.group(1)));
                }
            }
        }
        double average = keys.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        // Sortieren
        Comparator<String> bySecondNumber = Comparator.comparingInt(line -> {
            Matcher mTag = tagNumPattern.matcher(line);
            if (mTag.find()) {
                return Integer.parseInt(mTag.group(1));
            }
            Matcher mLead = leadNumPattern.matcher(line);
            if (mLead.find()) {
                return Integer.parseInt(mLead.group(1));
            }
            return Integer.MAX_VALUE;
        });
        unterschiedlicheZeilen.sort(bySecondNumber);

        // In Ausgabedatei schreiben
        try (
                BufferedWriter gw = Files.newBufferedWriter(Paths.get(gemeinsameDatei));
                BufferedWriter uw = Files.newBufferedWriter(Paths.get(unterschiedlicheDatei))) {
            // Gemeinsame Sequenzen (bleibt unverändert, nur Größe + Zeilen)
            gw.write("Anzahl gemeinsamer Sequenzen: " + gemeinsameZeilen.size());
            gw.newLine();
            for (String l : gemeinsameZeilen) {
                gw.write(l);
                gw.newLine();
            }

            // Unterschiedliche Sequenzen mit Durchschnitt
            uw.write("Anzahl unterschiedlicher Sequenzen: "
                    + unterschiedlicheZeilen.size()
                    + ", Durchschnitt der Zahlen: "
                    + average);
            uw.newLine();
            for (String l : unterschiedlicheZeilen) {
                uw.write(l);
                uw.newLine();
            }
        }

        System.out.println("Ergebnisse in:");
        System.out.println(gemeinsameDatei);
        System.out.println(unterschiedlicheDatei);
    }

    public static void findTimeAndLocationNgrams() {
        Set<String> locationSet = new HashSet<>(Arrays.asList(
                // Räumliche Präpositionen/Indikatoren
                "into", "in", "on", "at", "from", "around", "inside", "outside",
                "above", "below", "under", "over", "between", "among", "through",
                "across", "along", "beyond", "near", "far", "within", "without",
                "towards", "off", "out"));

        Set<String> timeSet = new HashSet<>(Arrays.asList(
                // Zeitliche Präpositionen/Indikatoren
                "before", "at", "on", "in", "after", "during", "until", "since", "for", "by", "within",
                "past", "throughout", "about", "around", "ago", "early", "late",
                "soon", "then", "when", "while", "always", "never", "often", "rarely",
                "sometimes", "usually", "already", "yet", "still", "once", "twice",
                "thrice", "first", "last", "next", "previous",

                // Wochentage
                "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday",

                // Monate
                "january", "february", "march", "april", "may", "june", "july", "august",
                "september", "october", "november", "december",

                // Tageszeiten
                "morning", "afternoon", "evening", "night", "midnight", "noon", "sunrise", "sunset",

                // Allgemeine Zeitangaben
                "today", "yesterday", "tomorrow", "tonight", "now", "soon", "later", "early", "late",
                "before", "after", "next", "last", "past", "future", "present", "meanwhile",

                // Zeitspannen & Perioden
                "second", "minute", "hour", "day", "week", "month", "year", "decade", "century", "era",
                "moment", "instant", "interval", "duration", "period", "timeline", "cycle",

                // Häufigkeit
                "daily", "weekly", "monthly", "annually", "hourly", "frequently", "regularly",
                "rarely", "sometimes", "often", "always", "never", "occasionally",

                // Jahreszeiten
                "spring", "summer", "autumn", "fall", "winter",

                // Kalenderbegriffe
                "weekend", "weekday", "workday", "holiday", "season"));

        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get("output\\nods\\ltm.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int totalProcessed = 0;
        int locationCounter = 0;
        int timeCounter = 0;

        if (lines != null && !lines.isEmpty()) {
            for (String line : lines) {
                Matcher m = Pattern.compile("Sequenz: (.*?),").matcher(line);
                if (m.find()) {
                    totalProcessed++;
                    String ngram = m.group(1).toLowerCase();

                    boolean foundLocation = false;
                    boolean foundTime = false;

                    for (String word : ngram.split(" ")) {
                        if (!foundLocation && locationSet.contains(word)) {
                            locationCounter++;
                            foundLocation = true;
                        }
                        if (!foundTime && timeSet.contains(word)) {
                            timeCounter++;
                            foundTime = true;
                        }
                        if (foundLocation && foundTime) {
                            break; // Wenn beide gefunden raus aus der Schleife
                        }
                    }

                    if (totalProcessed % 50 == 0) {
                        System.out.println("Processed: " + totalProcessed +
                                " | Location matches: " + locationCounter +
                                " | Time matches: " + timeCounter);
                    }
                }
            }
            System.out.println("FINAL COUNT >>> Processed: " + totalProcessed +
                    " | Location matches: " + locationCounter +
                    " | Time matches: " + timeCounter);
        }
    }

    public static void compareLtms() throws IOException {

        // Dateien einlesen
        List<String> text1 = Files
                .readAllLines(Paths.get("resources\\compareCorpora\\eng_news_2024_1M-sentencesltm.txt"));
        List<String> text2 = Files
                .readAllLines(Paths.get("resources\\compareCorpora\\eng_wikipedia_2016_1M-sentencesltm.txt"));
        List<String> text3 = Files
                .readAllLines(Paths.get("resources\\compareCorpora\\eng-com_web-public_2018_1M-sentencesltm.txt"));

        // Sequenzen extrahieren
        Set<String> sequenzen1 = new HashSet<>();
        Set<String> sequenzen2 = new HashSet<>();
        Set<String> sequenzen3 = new HashSet<>();

        for (String line : text1) {
            int start = line.indexOf("Sequenz:");
            int end = line.indexOf(", Time:");
            if (start != -1 && end != -1) {
                sequenzen1.add(line.substring(start + 8, end).trim());
            }
        }

        for (String line : text2) {
            int start = line.indexOf("Sequenz:");
            int end = line.indexOf(", Time:");
            if (start != -1 && end != -1) {
                sequenzen2.add(line.substring(start + 8, end).trim());
            }
        }

        for (String line : text3) {
            int start = line.indexOf("Sequenz:");
            int end = line.indexOf(", Time:");
            if (start != -1 && end != -1) {
                sequenzen3.add(line.substring(start + 8, end).trim());
            }
        }

        // Schnittmenge aller drei Sets berechnen
        sequenzen1.retainAll(sequenzen2); // Gemeinsame von 1 und 2
        sequenzen1.retainAll(sequenzen3); // & 3

        // Ergebnis in Datei schreiben
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources\\compareCorpora\\sameNGramms"))) {
            for (String sequenz : sequenzen1) {
                writer.write(sequenz);
                writer.newLine();
            }
        }

        System.out.println("Gemeinsame Sequenzen in allen drei Dateien: " + sequenzen1.size());
    }

    public static void calculateStats() throws IOException {
        Map<String, Integer> ngramToCounter = new HashMap<>();
        List<String> ngramList = new ArrayList<>();

        // LTM einlesen
        try (BufferedReader reader = new BufferedReader(
                new FileReader("resources\\compareCorpora\\eng-com_web-public_2018_1M-sentencesltm.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("Sequenz: |, Time: |, Counter: ");
                if (parts.length >= 4) {
                    String ngram = parts[1].trim();
                    int counter = Integer.parseInt(parts[3].trim());
                    ngramToCounter.put(ngram, counter);
                    ngramList.add(ngram);
                }
            }
        }

        // N-Gramme nach Counter sortieren
        List<String> sortedNgrams = ngramToCounter.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        int totalNGrams = sortedNgrams.size();
        double averageFreq = ngramToCounter.values().stream().mapToInt(i -> i).average().orElse(0);

        // Schnittmenge einlesen
        List<String> schnittmenge = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("resources\\compareCorpora\\sameNGramms"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                schnittmenge.add(line.trim());
            }
        }

        // Schnittmengen Werte sammeln
        List<Integer> schnittCounter = new ArrayList<>();
        List<Integer> schnittRang = new ArrayList<>();

        for (String s : schnittmenge) {
            if (ngramToCounter.containsKey(s)) {
                int counter = ngramToCounter.get(s);
                int rang = sortedNgrams.indexOf(s) + 1; // Rang ab 1
                schnittCounter.add(counter);
                schnittRang.add(rang);
            }
        }

        // Statistiken berechnen
        double schnittAvgFreq = schnittCounter.stream().mapToInt(i -> i).average().orElse(0);
        double schnittAvgRang = schnittRang.stream().mapToInt(i -> i).average().orElse(0);

        Collections.sort(schnittRang);
        int medianRang = schnittRang.isEmpty() ? 0
                : (schnittRang.size() % 2 == 0)
                        ? (schnittRang.get(schnittRang.size() / 2 - 1) + schnittRang.get(schnittRang.size() / 2)) / 2
                        : schnittRang.get(schnittRang.size() / 2);

        if (!schnittRang.isEmpty()) {
            int minRang = schnittRang.get(0);
            int maxRang = schnittRang.get(schnittRang.size() - 1);
            System.out.println("Range der Schnittmenge:\tvon Rang " + minRang + " bis Rang " + maxRang);
        } else {
            System.out.println("Range der Schnittmenge:\tKeine Schnittmenge gefunden.");
        }

        // Ausgabe
        System.out.println("\teng-news-2024");
        System.out.println("Gesamtanzahl N-Gramme:\t" + totalNGrams);
        System.out.println("Durschnittliche Häufigkeit:\t" + averageFreq);
        System.out.println("Durchschnittsrang der Schnittmenge:\t" + schnittAvgRang);
        System.out.println("Median der Schnittmenge:\t" + medianRang);
        System.out.println("Durchschnittliche Häufigkeit der Schnittmenge:\t" + schnittAvgFreq);
    }
}
