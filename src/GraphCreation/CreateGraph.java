package GraphCreation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

import Shared.AdjustingText;
import Shared.Constants;

public class CreateGraph {

    public static void buildGraph() throws IOException {
        if (Constants.WITH_ADJACENCYLIST) {
            buildGraphAdjacencyList();
        } else {
            buildGraphAdjacencyMatrix();
        }
    }

    public static void buildGraphAdjacencyMatrix() throws IOException {

        List<String> nodes = new ArrayList<>();

        // Einlesen ltm.txt
        try (BufferedReader reader = new BufferedReader(
                new FileReader("output\\nods\\ltm.txt"))) {
            // erste Zeile ignorieren "Inhalt des Long..."
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String nodeLabel = extractSequence(line);
                if (!nodes.contains(nodeLabel)) {
                    nodes.add(nodeLabel);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Datei nicht gefunden: " + e.getMessage());
            return;
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der Datei: " + e.getMessage());
            return;
        }

        List<String> inputSentences = new ArrayList<>();
        int[][] adjacencyMatrix = new int[nodes.size()][nodes.size()];
        try {
            // Den gesamten Inhalt der Datei in einen String laden
            String dateiPfad = Constants.corpus; // Pfad zur Datei
            String corpusString = Files.readString(Paths.get(dateiPfad));
            inputSentences = AdjustingText.cleanSentences(corpusString);
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Datei: " + e.getMessage());
        }

        for (String sentence : inputSentences) {
            if (!Constants.WITH_PUNCTUATIONMARKS) {
                sentence = sentence.replaceAll("[.?!]", "");
            } else {
                sentence = sentence.replaceAll("([.?!])", " $1 ");
            }
            String[] words = sentence.split("[\\s']+");

            for (int n1 = Constants.NGRAMM_SIZE_FROM; n1 <= Constants.NGRAMM_SIZE_TO; n1++) {
                for (int n2 = Constants.NGRAMM_SIZE_FROM; n2 <= Constants.NGRAMM_SIZE_TO; n2++) {
                    for (int i = 0; i <= words.length - (n1 + n2); i++) {
                        // FROM-N-Gramm
                        StringBuilder firstNgram = new StringBuilder();
                        for (int j = 0; j < n1; j++) {
                            firstNgram.append(words[i + j]);
                            if (j < n1 - 1)
                                firstNgram.append(" ");
                        }
                        String from = firstNgram.toString();
                        if (!nodes.contains(from))
                            continue;

                        // TO-N-Gramm
                        StringBuilder secondNgram = new StringBuilder();
                        for (int j = 0; j < n2; j++) {
                            secondNgram.append(words[i + n1 + j]);
                            if (j < n2 - 1)
                                secondNgram.append(" ");
                        }
                        String to = secondNgram.toString();
                        if (!nodes.contains(to))
                            continue;

                        int fromIndex = nodes.indexOf(from);
                        int toIndex = nodes.indexOf(to);
                        adjacencyMatrix[fromIndex][toIndex]++;
                    }
                }
            }
        }

        // Ausgabe der Matrix
        // Maximale Breite aller Knotennamen bestimmen
        int maxNodeLength = 0;
        for (String node : nodes) {
            maxNodeLength = Math.max(maxNodeLength, node.length());
        }
        int cellWidth = Math.max(maxNodeLength, 5) + 2; // +2 mehr abstand

        // Matrix in Datei schreiben
        File outputDir = new File("output/graph");
        outputDir.mkdirs(); // Ordner erstellen, falls nicht vorhanden
        File outputFile = new File(outputDir, "adjacency_matrix.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Kopfzeile
            writer.write(String.format("%" + cellWidth + "s", ""));
            for (String node : nodes) {
                writer.write(String.format("%" + cellWidth + "s", node));
            }
            writer.newLine();

            // Matrixzeilen
            for (int i = 0; i < nodes.size(); i++) {
                writer.write(String.format("%" + cellWidth + "s", nodes.get(i))); // Zeilenname
                for (int j = 0; j < nodes.size(); j++) {
                    writer.write(String.format("%" + cellWidth + "d", adjacencyMatrix[i][j]));
                }
                writer.newLine();
            }

            System.out.println("Matrix wurde erfolgreich gespeichert unter: " + outputFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
        }
    }

    private static void buildGraphAdjacencyList() throws IOException {

        List<String> nodes = new ArrayList<>();
        // Einlesen ltm.txt
        try (BufferedReader reader = new BufferedReader(new FileReader("output/nods/ltm.txt"))) {
            reader.readLine(); // Erste Zeile ignorieren
            String line;
            while ((line = reader.readLine()) != null) {
                String nodeLabel = extractSequence(line);
                if (!nodes.contains(nodeLabel)) {
                    nodes.add(nodeLabel);
                }
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der Datei: " + e.getMessage());
            return;
        }

        List<String> inputSentences = new ArrayList<>();
        try {
            String corpusString = Files.readString(Paths.get(Constants.corpus));
            inputSentences = AdjustingText.cleanSentences(corpusString);
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Datei: " + e.getMessage());
        }

        // Adjazenzliste initialisieren
        Map<String, Map<String, ?>> adjacencyList = new HashMap<>();
        if (!Constants.WITH_HASHEDEDGES) {
            for (String node : nodes) {
                adjacencyList.put(node, new HashMap<String, Integer>());
            }
        } else {
            for (String node : nodes) {
                adjacencyList.put(node, new HashMap<String, Edge>());
            }
        }
        nodes.clear(); // nodes nicht mehr benötigit

        for (String sentence : inputSentences) {
            if (!Constants.WITH_PUNCTUATIONMARKS) {
                sentence = sentence.replaceAll("[.?!]", "");
            } else {
                sentence = sentence.replaceAll("([.?!])", " $1 ");
            }
            String[] words = sentence.split("[\\s']+");

            for (int n1 = Constants.NGRAMM_SIZE_FROM; n1 <= Constants.NGRAMM_SIZE_TO; n1++) {
                for (int n2 = Constants.NGRAMM_SIZE_FROM; n2 <= Constants.NGRAMM_SIZE_TO; n2++) {
                    for (int i = 0; i <= words.length - (n1 + n2); i++) {

                        // FROM-N-Gramm
                        StringBuilder firstNgram = new StringBuilder();
                        for (int j = 0; j < n1; j++) {
                            firstNgram.append(words[i + j]);
                            if (j < n1 - 1) {
                                firstNgram.append(" ");
                            }
                        }
                        String from = firstNgram.toString();
                        if (!adjacencyList.containsKey(from)) {
                            continue;
                        }

                        // TO-N-Gramm
                        StringBuilder secondNgram = new StringBuilder();
                        for (int j = 0; j < n2; j++) {
                            secondNgram.append(words[i + n1 + j]);
                            if (j < n2 - 1) {
                                secondNgram.append(" ");
                            }
                        }
                        String to = secondNgram.toString();
                        if (!adjacencyList.containsKey(to)) {
                            continue;
                        }

                        if (!Constants.WITH_HASHEDEDGES) {
                            // Gewicht erhöhen
                            ((Map<String, Integer>) adjacencyList.get(from)).merge(to, 1, Integer::sum);
                        } else {
                            List<List<String>> allContext = new ArrayList<>();

                            // Sammle alle möglichen Kontexte mit einer Queue
                            Queue<ContextState> queue = new LinkedList<>();

                            // start knoten setzen
                            List<String> initial = new ArrayList<>();
                            initial.add(from);
                            // hilfsklasse um mögliche kombinationen durchzugehen
                            queue.add(new ContextState(i - 1, 0, initial));

                            while (!queue.isEmpty()) {
                                ContextState state = queue.poll();

                                int fromIndex = state.fromIndex;
                                int collected = state.collected;
                                List<String> context = new ArrayList<>(state.context);

                                // Abbruchbedingung
                                if (collected == Constants.HISTORY || fromIndex < 0) {
                                    while (context.size() < Constants.HISTORY + 1) {
                                        context.add("*");
                                    }
                                    if (!allContext.contains(context)) {
                                        allContext.add(context);
                                    }
                                    continue;
                                }

                                boolean found = false;

                                // Alle N-Gramm-Größen testen
                                for (int size = Constants.NGRAMM_SIZE_TO; size >= Constants.NGRAMM_SIZE_FROM; size--) {
                                    if (fromIndex - size + 1 < 0) {
                                        continue;
                                    }
                                    // N-Gramm bauen
                                    StringBuilder ngramBuilder = new StringBuilder();
                                    for (int j = 0; j < size; j++) {
                                        ngramBuilder.append(words[fromIndex - size + 1 + j]);
                                        if (j < size - 1) {
                                            ngramBuilder.append(" ");
                                        }
                                    }
                                    String candidate = ngramBuilder.toString();

                                    if (adjacencyList.containsKey(candidate)) {
                                        List<String> nextContext = new ArrayList<>(context);
                                        nextContext.add(candidate);
                                        queue.add(new ContextState(fromIndex - size, collected + 1, nextContext));
                                        found = true;
                                    }
                                }

                                // Wenn nichts gefunden wildcard
                                if (!found) {
                                    List<String> nextContext = new ArrayList<>(context);
                                    nextContext.add("*");
                                    queue.add(new ContextState(fromIndex - 1, collected + 1, nextContext));
                                }
                            }

                            for (List<String> context : allContext) {
                                Collections.reverse(context); // Liste spiegeln da falsche reihenfolge hierdrin
                                // hashen und speichern aller kontexte mit verkürzung

                                for (int k = 0; k < Constants.HISTORY; k++) {
                                    Edge newEdge;

                                    Map<String, Edge> innerMap = (Map<String, Edge>) adjacencyList.get(from);
                                    if (k == 0) {
                                        String hash = Edge.computeSHA256(context);
                                        if (adjacencyList.get(from).get(to) == null) {
                                            newEdge = new Edge(from, to);
                                            innerMap.put(to, newEdge);
                                        } else {
                                            newEdge = innerMap.get(to); // auf bestehende kante zugreifen
                                        }
                                        newEdge.addHash(hash);
                                        newEdge = null;
                                    }

                                    context.set(k, "*");

                                    String hash = Edge.computeSHA256(context);

                                    // Falls verbindung noch nicht besteht neue Kante bilden
                                    if (adjacencyList.get(from).get(to) == null) {
                                        newEdge = new Edge(from, to);
                                        innerMap.put(to, newEdge);
                                    } else {
                                        newEdge = innerMap.get(to); // auf bestehende kante zugreifen
                                    }
                                    newEdge.addHash(hash);

                                }
                            }
                        }
                    }
                }
            }
        }
        if (!Constants.WITH_HASHEDEDGES) {
            Output.outputAdjacencyList(adjacencyList);
        } else {

            Map<String, Map<String, Edge>> castedMap = (Map<String, Map<String, Edge>>) (Map<?, ?>) adjacencyList; // cast
                                                                                                                   // anwenden
                                                                                                                   // um
                                                                                                                   // gerneic
                                                                                                                   // ?
                                                                                                                   // zu
                                                                                                                   // umgehen
            castedMap.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isEmpty()); // leeren
                                                                                                            // unnötiger
                                                                                                            // knoten
                                                                                                            // ohne
                                                                                                            // kanten

            Output.saveToFile(castedMap); // Graphen speichern

        }

    }

    // Extrahiere den Text nach "Sequenz:" bis zum nächsten Komma
    private static String extractSequence(String line) {
        int start = line.indexOf("Sequenz:") + "Sequenz:".length();
        int end = line.indexOf(",", start);
        if (start < 0 || end < 0 || start >= end) {
            throw new IllegalArgumentException("Zeile hat kein gültiges Sequenz-Format: " + line);
        }
        return line.substring(start, end).trim();
    }

}
