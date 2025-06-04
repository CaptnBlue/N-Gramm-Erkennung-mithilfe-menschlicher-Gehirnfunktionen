import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class CompareSequence {

    // Vergleichmethoden von LTMs oder STMs
    public static void compareSequences(String path1, String path2) throws IOException {
        String gemeinsameDatei = "gemeinsameSequenzen.txt";
        String unterschiedlicheDatei = "nichtGemeinsameSequenzen.txt";

        Map<String, String> map1 = new HashMap<>();
        Map<String, String> map2 = new HashMap<>();
        Pattern sequenzPattern = Pattern.compile("Sequenz: (.*?),");
        Pattern leadNumPattern = Pattern.compile("^(\\d+)");
        Pattern tagNumPattern = Pattern.compile("^\\[Datei[12]\\]\\s*(\\d+)");

        // Einlesen beider Dateien in map1 / map2
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

        System.out.println("Fertig! Ergebnisse in:");
        System.out.println(gemeinsameDatei);
        System.out.println(unterschiedlicheDatei);
    }
}
