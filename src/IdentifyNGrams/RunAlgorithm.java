
package IdentifyNGrams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import Shared.AdjustingText;
import Shared.Constants;

public class RunAlgorithm {
    public static void start() {

        List<String> pdfTexte = new ArrayList<>();

        try {
            // Den gesamten Inhalt der Datei in einen String laden
            String dateiPfad = Constants.corpus; // Pfad zur Datei
            String inhalt = Files.readString(Paths.get(dateiPfad));
            pdfTexte.add(inhalt);

        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Datei: " + e.getMessage());
        }

        // Erstellung der primären Gedächnissstruktur
        Memory mainMemory = new Memory();
        List<Memory> threadsAVL = new ArrayList<>();
        for (int i = 0; i < pdfTexte.size(); i++) { // nur eine wird geladen
            List<String> inputSentences = AdjustingText.cleanSentences(pdfTexte.get(i));
            System.out.println("Anzahl untersuchter Sätze:" + inputSentences.size());
            if (Constants.AMOUNT_THREADS > inputSentences.size()) {
                System.out.println("Fehler: Threadanzahl übersteigt Satzanzahl.");
                System.exit(0);
            }
            // processSentencesList(inputSentences); // Befehl für Liste statt AVL-Baum

            List<List<String>> partitions = partitionList(inputSentences,
                    Constants.AMOUNT_THREADS);

            // CountdownLatch für Synchronisation erstellen benötigt damit alle Threads
            // aufeinander warten
            CountDownLatch latch = new CountDownLatch(Constants.AMOUNT_THREADS); //

            // Threads starten
            for (List<String> partition : partitions) {
                new Thread(() -> {
                    threadsAVL.add(processSentences(partition));
                    latch.countDown(); // Thread signalisiert dass er fertig ist
                }).start();
            }
            try {
                // Warten, bis alle Threads fertig sind
                latch.await();
            } catch (InterruptedException e) {
                System.out.println("Thread wurde unterbrochen: " + e.getMessage());
            }
        }
        if (Constants.AMOUNT_THREADS > 1) {
            AVLTree.merge(threadsAVL, mainMemory);
            AVLTree.transferSTMtoLTM(mainMemory); // Methode zum Sicherstellen dass alle ins LTM übertragen worden sind

            mainMemory.longTermMemory.printByCounter(true); // Ausgaben in .txt
            mainMemory.shortTermMemory.printByCounter(false);
        } else {
            mainMemory = threadsAVL.get(0);
            AVLTree.transferSTMtoLTM(mainMemory); // Methode zum Sicherstellen dass alle ins LTM übertragen worden sind
            mainMemory.longTermMemory.printByCounter(true); // Ausgaben in .txt
            mainMemory.shortTermMemory.printByCounter(false);
        }

    }

    // Aufteilung der Sätze in mehrere Einheiten damit alle Threads gleich Anzahl
    // bekommen
    private static List<List<String>> partitionList(List<String> list, int parts) {

        List<List<String>> partitions = new ArrayList<>();
        int partitionSize = (int) Math.ceil((double) list.size() / parts);

        for (int i = 0; i < list.size(); i += partitionSize) {
            partitions.add(new ArrayList<>(list.subList(i, Math.min(i + partitionSize, list.size()))));
        }

        return partitions;
    }

    // Avl-Baum Methodik
    private static Memory processSentences(List<String> sentences) {
        Memory memory = new Memory();
        for (String sentence : sentences) {
            List<String> nGramme = AdjustingText.generateNGramms(sentence);
            // Gehe alle nGramme durch
            for (String nGramm : nGramme) {
                if (!memory.longTermMemory.contains(nGramm, false, memory)) {
                    if (!memory.shortTermMemory.contains(nGramm, true, memory)) {
                        Sequenz seq = new Sequenz(nGramm);
                        memory.shortTermMemory.insert(seq, false);
                    }
                }
            }

            // Speicherüberprüfung
            HeapspaceController.checkHeapUsage(memory);

        }
        // System.out.println(sentenceCounter + " | " + memory.longTermMemory.size());
        memory.longTermMemory.updateTimeForAllNodes();
        memory.shortTermMemory.updateTimeForAllNodes();
        return memory;
    }

    // Methoden für Listen

    private static void processSentencesList(List<String> sentences) {
        List<Sequenz> shortTermList = new ArrayList<>();
        List<Sequenz> longTermList = new ArrayList<>();
        int numbersent = 0;
        for (String sentence : sentences) {
            List<String> nGramme = AdjustingText.generateNGramms(sentence);
            for (String nGramm : nGramme) {
                boolean found = false;

                // Prüfe in longTermList
                Iterator<Sequenz> longIter = longTermList.iterator();
                while (longIter.hasNext()) {
                    Sequenz seq = longIter.next();
                    if (seq.getnGrammString().equals(nGramm)) {
                        seq.setCounter(seq.getCounter() + 1);
                        found = true;
                        break;
                    }
                }
                if (found)
                    continue;

                // Prüfe in shortTermList
                Iterator<Sequenz> shortIter = shortTermList.iterator();
                while (shortIter.hasNext()) {
                    Sequenz seq = shortIter.next();
                    if (seq.getnGrammString().equals(nGramm)) {
                        seq.setCounter(seq.getCounter() + 1);
                        if (seq.getCounter() >= Constants.SHORTTOLONG_THRESHOLD) {
                            // Zähler explizit setzen (zur Sicherheit)
                            seq.setCounter(Constants.SHORTTOLONG_THRESHOLD);
                            longTermList.add(seq);
                            shortIter.remove();
                        }
                        found = true;
                        break;
                    }
                }
                if (found)
                    continue;

                // Neuen Sequenz hinzufügen
                shortTermList.add(new Sequenz(nGramm));
            }
            numbersent += 1;
            if (numbersent == 5000)
                break;
        }

        System.out.println("Short-Term-Größe: " + shortTermList.size());
        System.out.println("Long-Term-Größe: " + longTermList.size());
        shortTermList.clear();
        longTermList.clear();
    }
}
