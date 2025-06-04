import java.util.ArrayList;
import java.util.List;

public class AdjustingText {

    public static String cleanTextGer(String input) {
        // Bereinige Text das nur Buchstaben Leerzeichen Punkte Semikolons Fragezeichen
        // Ausrufezeichen übrig bleiben für deutsche Texte
        String cleanedText = input.replaceAll("[^a-zA-ZäöüßÄÖÜ .:?!]", "");
        cleanedText = cleanedText.toLowerCase();
        return cleanedText;
    }

    public static String cleanTextEng(String input) {
        // Textbereingung Englische Text + Zahlen
        // Korpus spezifische Löschungen
        input = input.replace("'", "");
        // Entfernt @@123 @@xyz <p> die im Korpus vorhanden sind
        input = input.replaceAll("@@\\S+", "");
        input = input.replaceAll("</?p>", "");

        // Nur Buchstaben Zahlen Leerzeichen und Satzzeichen zulassen
        input = input.replaceAll("[^a-zA-Z0-9 .:?!]", "");
        // Mehrere Leerzeichen zu einem reduzieren
        input = input.replaceAll("\\s+", " ").trim();
        return input.toLowerCase();
    }

    // Text in Sätze aufteilen
    public static List<String> splitIntoSentences(String text) {
        List<String> sentences = new ArrayList<>();
        // An Satzzeichen aufteilen
        String[] splitSentences = text.split("(?<=[.!?:])");

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

        int n = Constants.NGRAMM_SIZE;
        // Satzzeichen entfernen
        sentence = sentence.replaceAll("[.:?!]", "");

        // Satz in Wörter unterteilen
        String[] words = sentence.split("\\s+");

        // Wenn es weniger als n Wörter gibt, gibt es keine n-Gramme
        if (words.length < n) {
            return ngrams;
        }

        for (int i = 0; i <= words.length - n; i++) {
            // Das n-Gramm aus den n aufeinanderfolgenden Wörtern erstellen
            StringBuilder ngram = new StringBuilder();
            for (int j = 0; j < n; j++) {
                ngram.append(words[i + j]);
                if (j < n - 1) {
                    ngram.append(" "); // Leerzeichen zwischen den Wörtern hinzufügen
                }
            }
            ngrams.add(ngram.toString());
        }

        return ngrams;
    }

    public static List<String> cleanSentences(String txt) {
        // Möglichkeit zum deutschen Text zu wechseln
        String cleantxt = AdjustingText.cleanTextEng(txt);

        List<String> sentences = AdjustingText.splitIntoSentences(cleantxt);
        return sentences;
    }
}
