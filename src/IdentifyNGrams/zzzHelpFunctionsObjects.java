
//Ausgabe für größen
// public class zzzHelpFunctionsObjects {
//      System.out.println("Größe Long-Term Memory (LTM): " + mainMemory.longTermMemory.size());
//     System.out.println("Größe Short-Term Memory (STM): " + mainMemory.shortTermMemory.size());
//     System.out.println("Gesamt gefundene N-Gramme: " + 
// (mainMemory.longTermMemory.size() + mainMemory.shortTermMemory.size()));
// System.out.println("Gesamte Zählung Long-Term Memory (LTM): " + mainMemory.longTermMemory.totalCounter());
// System.out.println("Gesamte Zählung Short-Term Memory (STM): " + mainMemory.shortTermMemory.totalCounter());
// System.out.println("Gesamt-Zählung aller N-Gramme: " + 
// (mainMemory.longTermMemory.totalCounter() + mainMemory.shortTermMemory.totalCounter()));  

//Ausgabe für Zeiten
    // public static void main(String[] args) throws Exception {
    //     long allTime = 0;

    //     for (int x = 0; x < 1; x++) {
    //         RunAlgorithm.start();
    //         long start = new Date().getTime(); // Startzeit pro Durchlauf

    //         long runningTime = new Date().getTime() - start; // Zeit für diesen Durchlauf
    //         allTime += runningTime;
    //         System.out.println("Durchlauf " + x + " dauerte: " + runningTime + " ms");
    //     }

    //     System.out.println("Gesamtlaufzeit: " + allTime + " ms");
    //     System.out.printf("Maximale beobachtete Auslastung: %.2f%%\n", HeapspaceController.maxUsage * 100);
    //     System.exit(0);

    // }


    // Auslastung Durchschnitt
//     double[] maxUsages = new double[30];
//     double total = 0;

//     for (int i = 0; i < 30; i++) {
//         // Reset maxUsage vor jedem Durchlauf
//         HeapspaceController.maxUsage = 0.0;

//         // Algorithmus starten
//         RunAlgorithm.start();

//         // Nach Algorithmuslauf maxUsage speichern
//         maxUsages[i] = HeapspaceController.maxUsage;
//         total += maxUsages[i];

//         System.out.printf("Maximale Auslastung bei Lauf %d: %.2f%%\n", i + 1, HeapspaceController.maxUsage * 100);
//     }

//     double averageMaxUsage = total / 30;
//     System.out.printf("\nDurchschnitt der maximalen Speicherauslastung über 30 Läufe: %.2f%%\n", averageMaxUsage * 100);
//     System.exit(0);
// }


//Durchlaufen für unterschiedliche Auslastung des Heapspace


// for (int i = 0; i < 30; i++) {
//     System.out.printf("Durchlauf %d – Aktueller Heapspace-Threshold: %.2f%%\n", i + 1, Constants.HEAPSPACE_THRESHOLD * 100);


//     // Algorithmus starten
//     RunAlgorithm.start();

//     Constants.HEAPSPACE_THRESHOLD +=0.05;
// }

// System.exit(0);

// Ausgabe für Vergesslichkeitsmechanismus
// for (int i = 0; i < 10; i++) {
//     System.out.printf("Äußerer Durchlauf %d\n", i + 1);
//     Constants.DELETESTM_AMOUNT += 0.1;
//     Constants.HEAPSPACE_THRESHOLD = 0;

//     for (int j = 0; j <= 7; j++) {
//         Constants.HEAPSPACE_THRESHOLD += 0.05;

//         System.out.printf(
//             "  Innerer Durchlauf %d – DELETESTM_AMOUNT: %.2f, HEAPSPACE_THRESHOLD: %.2f\n",
//             j, Constants.DELETESTM_AMOUNT, Constants.HEAPSPACE_THRESHOLD
//         );

//         // Algorithmus starten
//         RunAlgorithm.start();
//     }

//     System.out.println(); // Leerzeile zur besseren Lesbarkeit
// }

// System.exit(0);

//Vergleiche LTM
// CompareSequence.compareSequences("ltmall.txt", "ltm.txt");



// Ausgabe des Vergessensmechanismus
// long allTime = 0;
    
// for (int i = 0; i < 10; i++) {
//     System.out.printf("==== Äußerer Durchlauf %d ====\n", i + 1);

//     Constants.DELETESTM_AMOUNT += 0.1;
//     Constants.HEAPSPACE_THRESHOLD = 1;

//     for (int j = 0; j <= 7; j++) {
//         Constants.HEAPSPACE_THRESHOLD += 0.05;

//         System.out.printf(
//             "Innerer Durchlauf %d  DELETESTM_AMOUNT: %.2f, HEAPSPACE_THRESHOLD: %.2f\n",
//             j, Constants.DELETESTM_AMOUNT, Constants.HEAPSPACE_THRESHOLD
//         );

//         long start = System.currentTimeMillis(); // Startzeit messen

//         // Algorithmus starten
//         RunAlgorithm.start();

//         long runningTime = System.currentTimeMillis() - start; // Dauer messen
//         allTime += runningTime;

//         System.out.printf("Laufzeit dieses Durchlaufs: %d ms\n", runningTime);
//     }

//     System.out.println(); // Leerzeile zur besseren Lesbarkeit
// }

// System.out.println("==== Gesamtlaufzeit aller Durchläufe: " + allTime + " ms ====");
// System.exit(0);
// }