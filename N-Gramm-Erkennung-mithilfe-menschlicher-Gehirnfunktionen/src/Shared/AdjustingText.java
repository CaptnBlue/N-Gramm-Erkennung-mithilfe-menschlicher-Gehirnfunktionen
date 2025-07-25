package Shared;

import java.util.ArrayList;
import java.util.List;

public class AdjustingText {

    public static String cleanTextGer(String input) {
        // Bereinige Text das nur Buchstaben Leerzeichen Punkte Semikolons Fragezeichen
        // Ausrufezeichen übrig bleiben für deutsche Texte
        String cleanedText = input.replaceAll("[^a-zA-ZäöüßÄÖÜ .?!]", "");
        cleanedText = cleanedText.toLowerCase();
        return cleanedText;
    }

    public static String cleanTextEng(String input) {
        // Tabs durch Leerzeichen ersetzen
        input = input.replace("\t", " ");
        // Textbereingung Englische Text + Zahlen
        // Nur Buchstaben Zahlen Leerzeichen und Satzzeichen zulassen
        input = input.replaceAll("[^a-zA-Z0-9 .?!]", "");
        // Mehrere Leerzeichen zu einem reduzieren
        input = input.replaceAll("\\s+", " ").trim();
        return input.toLowerCase();
    }

    // Text in Sätze aufteilen
    public static List<String> splitIntoSentences(String text) {
        List<String> sentences = new ArrayList<>();
        // An Satzzeichen aufteilen
        String[] splitSentences = text.split("(?<=[.!?])");

        for (String sentence : splitSentences) {
            // Satz trimmen und nur hinzufügen wenn nicht leer
            sentence = sentence.trim();
            if (!sentence.isEmpty()) {
                sentences.add(sentence);
            }
        }

        return sentences;
    }

    // NGramm Erstelung
    public static List<String> generateNGramms(String sentence) {
        List<String> ngrams = new ArrayList<>();

        // Satz ggf. vorbereiten
        if (!Constants.WITH_PUNCTUATIONMARKS) {
            // Satzzeichen entfernen
            sentence = sentence.replaceAll("[.?!]", "");
        } else {
            // Satzzeichen von Wörtern trennen damit sie eigene Token werden
            sentence = sentence.replaceAll("([.?!])", " $1 ");
        }

        // In Wörter/Satzzeichen aufteilen
        String[] words = sentence.trim().split("[\\s']+");

        for (int n = Constants.NGRAMM_SIZE_FROM; n <= Constants.NGRAMM_SIZE_TO; n++) {
            if (words.length < n) {
                continue;
            }

            for (int i = 0; i <= words.length - n; i++) {
                // N-Gramm zusammensetzen
                StringBuilder ngram = new StringBuilder();
                for (int j = 0; j < n; j++) {
                    ngram.append(words[i + j]);
                    if (j < n - 1) {
                        ngram.append(" ");
                    }
                }
                ngrams.add(ngram.toString());
            }
        }

        return ngrams;
    }

    public static List<String> cleanSentences(String txt) {
        // Möglichkeit zum deutschen Text zu wechseln/ wurde im weiteren verlauf nicht
        // getestet oder weiter verfolgt
        String cleantxt = AdjustingText.cleanTextEng(txt);

        List<String> sentences = AdjustingText.splitIntoSentences(cleantxt);
        if (sentences.size() > 10) {// wichtig für lazyload im liklihoodchecker da einezlne sätze nur noch geladen
                                    // werden
            // sentences = sentences.subList(0, 50000); // Satzbegrenzung möglich
        }
        return sentences;
    }
}
