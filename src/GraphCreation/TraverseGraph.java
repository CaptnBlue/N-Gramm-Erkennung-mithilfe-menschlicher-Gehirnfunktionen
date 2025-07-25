package GraphCreation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import Shared.Constants;

public class TraverseGraph {
    public static String generateSequence(String input, Map<String, Map<String, Edge>> graph) throws IOException {

        if (graph.isEmpty()) {
            graph = Output.loadFromFile(); // Graph laden falls nicht über Liklihoodcheck
        }

        // bereingung und splitten
        if (!Constants.WITH_PUNCTUATIONMARKS) {
            input = input.replaceAll("[.?!]", "");
        } else {
            input = input.replaceAll("([.?!])", " $1 ");
        }

        boolean isEndless = false;
        while (!input.matches(".*[.?!].*") && !isEndless) {
            // input = input.replaceAll("\\band\\b", "."); // and auch als mögliches
            // satzende
            if (countWordsWithEnding(input) > 200) { // abbrechen falls zu groß, wahrscheinlich endlois schleifen dann
                isEndless = true;
            }

            input = getNextToken(input, graph);
        }
        input = input.trim().replaceAll("\\s+([.!?])$", "$1"); // wichtig da sonst keine matches wenn Leerzeichen noch
                                                               // da
                                                               // sind
        return input;
    }

    private static String getNextToken(String input, Map<String, Map<String, Edge>> graph) {

        String[] words = input.split("[\\s']+");
        List<List<String>> allContext = new ArrayList<>();

        Queue<ContextState> queue = new LinkedList<>();

        // Start: leerer Kontext, von letztem Wort, 0 gesammelt
        queue.add(new ContextState(words.length - 1, 0, new ArrayList<>()));

        while (!queue.isEmpty()) {
            ContextState state = queue.poll();

            int fromIndex = state.fromIndex;
            int collected = state.collected;
            List<String> context = new ArrayList<>(state.context);

            // Abbruchbedingung: HISTORY erreicht oder Anfang erreicht
            if (collected == Constants.HISTORY + 1 || fromIndex < 0) {
                while (context.size() < Constants.HISTORY + 1) {
                    context.add("*");
                }
                if (!allContext.contains(context)) {
                    allContext.add(context);
                }
                continue;
            }

            boolean found = false;

            for (int size = Constants.NGRAMM_SIZE_TO; size >= Constants.NGRAMM_SIZE_FROM; size--) {
                if (fromIndex - size + 1 < 0)
                    continue;

                StringBuilder ngramBuilder = new StringBuilder();
                for (int j = 0; j < size; j++) {
                    ngramBuilder.append(words[fromIndex - size + 1 + j]);
                    if (j < size - 1)
                        ngramBuilder.append(" ");
                }
                String candidate = ngramBuilder.toString();

                if (graph.containsKey(candidate)) {
                    List<String> nextContext = new ArrayList<>(context);
                    nextContext.add(candidate);
                    queue.add(new ContextState(fromIndex - size, collected + 1, nextContext));
                    found = true;
                }
            }

            // Wildcard hinzufügen, wenn kein N-Gramm gefunden wurde
            if (!found) {
                while (context.size() < Constants.HISTORY + 1) {
                    context.add("*");
                }
                if (!allContext.contains(context)) {
                    allContext.add(context);
                }
            }
        }

        // Zwischenspeicher für Treffer mit diesem Hash
        List<Edge> matchesForThisHash = new ArrayList<>();
        // Zwischenspeicher für mögliche tokens
        Set<String> possibleNext = new HashSet<>();

        for (List<String> context : allContext) {
            Collections.reverse(context); // Liste spiegeln da falsche reihenfolge hierdrin
            // hashen und speichern aller kontexte mit verkürzung

            for (int k = 0; k < Constants.HISTORY; k++) {
                Map<String, Edge> innerMap = graph.get(context.get(context.size() - 1));
                if (innerMap == null) {
                    break;
                }
                if (k == 0) {
                    String hash = Edge.computeSHA256(context);

                    for (Edge edge : innerMap.values()) {
                        if (edge.getContextHashes().contains(hash)) {
                            matchesForThisHash.add(edge);
                        }
                    }

                    // Falls Treffer gefunden speichere String und raus aus for
                    if (!matchesForThisHash.isEmpty()) {
                        for (Edge e : matchesForThisHash) {
                            possibleNext.add(e.getTo());
                        }
                        break;
                    }
                }

                context.set(k, "*");

                String hash = Edge.computeSHA256(context);

                for (Edge edge : innerMap.values()) {
                    if (edge.getContextHashes().contains(hash)) {
                        matchesForThisHash.add(edge);
                    }
                }

                // Falls Treffer gefunden speichere String und raus aus for
                if (!matchesForThisHash.isEmpty()) {
                    for (Edge e : matchesForThisHash) {
                        possibleNext.add(e.getTo());
                    }
                    break;
                }

            }
        }

        // Ab hier Entscheidungsstrategien
        if (!matchesForThisHash.isEmpty()) {
            // Entscheidung des nächsten Tokens
            if (Constants.OUTPUTMODE == 1) {

                // longestNgramm
                List<String> longestEntries = new ArrayList<>();
                int maxWordCount = 0;

                for (String entry : possibleNext) {
                    // Spliten
                    String[] words2 = entry.trim().split("\\s+");
                    int wordCount = words2.length;

                    if (wordCount > maxWordCount) {
                        longestEntries.clear();
                        longestEntries.add(entry);
                        maxWordCount = wordCount;
                    } else if (wordCount == maxWordCount) {
                        longestEntries.add(entry);
                    }
                }

                // Zuffalsauswahl des nächsten tokens
                Random random = new Random();

                if (!longestEntries.isEmpty()) {
                    // Generiere einen zufälligen Index innerhalb der Größe der Liste
                    int randomIndex = random.nextInt(longestEntries.size());

                    // Ziehe den Wert an diesem zufälligen Index
                    String randomToken = longestEntries.get(randomIndex);

                    input = input + " " + randomToken;
                }
            } else if (Constants.OUTPUTMODE == 2) {
                List<Edge> edgesWithMostHashes = new ArrayList<>();
                int maxHashCount = 0;
                String randomToken = null;

                for (Edge edge : matchesForThisHash) {
                    int currentHashCount = edge.getContextHashes().size();
                    if (currentHashCount > maxHashCount) {
                        maxHashCount = currentHashCount;
                        edgesWithMostHashes.clear();
                        edgesWithMostHashes.add(edge);
                    } else if (currentHashCount == maxHashCount) {
                        edgesWithMostHashes.add(edge);
                    }

                }

                // randomMode
                Random random = new Random();

                // Generiere einen zufälligen Index innerhalb der Größe der Liste
                int randomIndex = random.nextInt(edgesWithMostHashes.size());

                // Ziehe den Wert an diesem zufälligen Index
                randomToken = edgesWithMostHashes.get(randomIndex).getTo();

                input = input + " " + randomToken;
            } else if (Constants.OUTPUTMODE == 3) {
                List<String> tokensWithMostIncHashes = new ArrayList<>();
                int maxHashCount = 0;
                String randomToken = null;

                for (String token : possibleNext) {
                    // Finde alle Kanten, die zu diesem Token führen (also alle Einträge im Graph,
                    // deren Ziel 'token' ist) nicht genutzt
                    int totalHashes = 0;

                    for (Map<String, Edge> edges : graph.values()) {
                        Edge edge = edges.get(token);
                        if (edge != null) {
                            totalHashes += edge.getContextHashes().size();
                        }
                    }

                    // Vergleiche mit dem bisherigen Maximum
                    if (totalHashes > maxHashCount) {
                        tokensWithMostIncHashes.clear();
                        tokensWithMostIncHashes.add(token);
                        maxHashCount = totalHashes;
                    } else if (totalHashes == maxHashCount) {
                        tokensWithMostIncHashes.add(token);
                    }
                }

                Random random = new Random();
                int randomIndex = random.nextInt(tokensWithMostIncHashes.size());
                randomToken = tokensWithMostIncHashes.get(randomIndex);

                input = input + " " + randomToken;

            } else if (Constants.OUTPUTMODE == 4) {
                // Liste aller Kanten aus matchesForThisHash – Gewichtung über Hash-Anzahl
                List<Edge> weightedEdges = new ArrayList<>();

                for (Edge edge : matchesForThisHash) {
                    int weight = edge.getContextHashes().size();
                    // Füge Kante so oft hinzu, wie sie Gewicht hat (zb. bei 3 Hashes 3 mal)
                    for (int i = 0; i < weight; i++) {
                        weightedEdges.add(edge);
                    }
                }

                // Gewichtete Zufallsauswahl je mehr Hashes, desto höhere Chance
                Random random = new Random();
                int randomIndex = random.nextInt(weightedEdges.size());

                // Ziehe die zufällige Kante anhand des gewichteten Index
                Edge selectedEdge = weightedEdges.get(randomIndex);

                // Ziel-Token extrahieren
                String randomToken = selectedEdge.getTo();

                // Token anhängen
                input = input + " " + randomToken;
            }

            else if (Constants.OUTPUTMODE == 5) { // nie benutzt war gedacht um meisten kanten zu berücksichtigen
                List<String> tokensWithMostIncEdges = new ArrayList<>();
                int maxInEdgeCount = 0;
                String randomToken = null;

                for (String token : possibleNext) {
                    int incomingEdgeCount = 0;

                    for (Map<String, Edge> edges : graph.values()) {
                        if (edges.containsKey(token)) {
                            incomingEdgeCount++; // Nur Anzahl der Kanten, nicht Hashes
                        }
                    }

                    // Vergleiche mit bisherigem Maximum
                    if (incomingEdgeCount > maxInEdgeCount) {
                        tokensWithMostIncEdges.clear();
                        tokensWithMostIncEdges.add(token);
                        maxInEdgeCount = incomingEdgeCount;
                    } else if (incomingEdgeCount == maxInEdgeCount) {
                        tokensWithMostIncEdges.add(token);
                    }
                }

                Random random = new Random();
                int randomIndex = random.nextInt(tokensWithMostIncEdges.size());
                randomToken = tokensWithMostIncEdges.get(randomIndex);

                input = input + " " + randomToken;

            }
            if (Constants.OUTPUTMODE == 6) { // kombi erst längsten dann meisten hashes
                List<Edge> longestEdges = new ArrayList<>();
                int maxWordCount = 0;

                // longest
                for (Edge edge : matchesForThisHash) {
                    String to = edge.getTo();
                    int wordCount = to.trim().split("\\s+").length;

                    if (wordCount > maxWordCount) {
                        longestEdges.clear();
                        longestEdges.add(edge);
                        maxWordCount = wordCount;
                    } else if (wordCount == maxWordCount) {
                        longestEdges.add(edge);
                    }
                }

                // Hashes
                List<Edge> mostHashEdges = new ArrayList<>();
                int maxHashCount = 0;

                for (Edge edge : longestEdges) {
                    int currentHashCount = edge.getContextHashes().size();

                    if (currentHashCount > maxHashCount) {
                        mostHashEdges.clear();
                        mostHashEdges.add(edge);
                        maxHashCount = currentHashCount;
                    } else if (currentHashCount == maxHashCount) {
                        mostHashEdges.add(edge);
                    }
                }

                // random
                Random random = new Random();
                int randomIndex = random.nextInt(mostHashEdges.size());
                String randomToken = mostHashEdges.get(randomIndex).getTo();

                input = input + " " + randomToken;
            } else {
                // Hash-Set in Liste
                List<String> possibleNextList = new ArrayList<>(possibleNext);

                // randomMode
                Random random = new Random();

                // Generiere einen zufälligen Index innerhalb der Größe der Liste
                int randomIndex = random.nextInt(possibleNext.size());

                // Ziehe den Wert an diesem zufälligen Index
                String randomToken = possibleNextList.get(randomIndex);

                input = input + " " + randomToken;

            }
        }

        // Modi falls keine Kante gefunden worden ist
        else {
            int contextLength = Constants.NGRAMM_SIZE_FROM - 1;
            List<String> possibleNextList = new ArrayList<>();

            while (contextLength > 0) {
                if (words.length >= contextLength) {
                    // Kontext der letzten Wörter erstellen
                    StringBuilder contextBuilder = new StringBuilder();
                    for (int i = words.length - contextLength; i < words.length; i++) {
                        contextBuilder.append(words[i]);
                        if (i < words.length - 1) {
                            contextBuilder.append(" ");
                        }
                    }
                    String context = contextBuilder.toString();
                    for (Map.Entry<String, Map<String, Edge>> outerEntry : graph.entrySet()) {
                        String token = outerEntry.getKey();
                        int index = token.indexOf(context);

                        if (index != -1 &&
                                (index == 0 || token.charAt(index - 1) == ' ') &&
                                (index + context.length() < token.length()
                                        && token.charAt(index + context.length()) == ' ')) {
                            String rest = token.substring(index + context.length()).trim();

                            // Wörter im rest splitten
                            String[] outerWords = rest.split("\\s+");

                            if (!outerWords[0].isEmpty()) {
                                possibleNext.add(outerWords[0]);
                            }
                        }
                        Map<String, Edge> innerMap = outerEntry.getValue();

                        for (Map.Entry<String, Edge> innerEntry : innerMap.entrySet()) {

                            String nextToken = innerEntry.getKey();
                            int indexInner = nextToken.indexOf(context);

                            if (indexInner != -1 &&
                                    (indexInner == 0 || nextToken.charAt(indexInner - 1) == ' ') &&
                                    (indexInner + context.length() < nextToken.length()
                                            && nextToken.charAt(indexInner + context.length()) == ' ')) {
                                String restInner = nextToken.substring(indexInner + context.length()).trim();

                                String[] innerWords = restInner.split("\\s+");

                                if (!innerWords[0].isEmpty()) {

                                    possibleNext.add(innerWords[0]);
                                }
                            }

                        }
                    }

                }
                contextLength--;
            }
            if (possibleNext.isEmpty()) {
                return input + ".";
            }
            possibleNextList = new ArrayList<>(possibleNext);
            // randomMode
            Random random = new Random();

            // Generiere einen zufälligen Index innerhalb der Größe der Liste
            int randomIndex = random.nextInt(possibleNextList.size());

            // Ziehe den Wert an diesem zufälligen Index
            String randomToken = possibleNextList.get(randomIndex);

            input = input + " " + randomToken;
        }

        return input;
    }

    private static int countWordsWithEnding(String sentence) {
        if (sentence == null || sentence.isBlank())
            return 0;

        String[] words = sentence.trim().split("\\s+");
        int count = words.length;

        // . ? oder ! zählt als wort
        if (sentence.matches(".*[.!?]$")) {
            count++;
        }
        return count;
    }
}
