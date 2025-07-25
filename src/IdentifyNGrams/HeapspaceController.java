package IdentifyNGrams;

import Shared.Constants;

public class HeapspaceController {
    public static double maxUsage = 0.0;

    public static void checkHeapUsage(Memory memory) {
        Runtime runtime = Runtime.getRuntime();

        // Aktuelle Speicher-Auslastung berechen
        long maxMemory = runtime.maxMemory(); // Maximal verfügbarer Heap-Speicher
        long usedMemory = runtime.totalMemory() - runtime.freeMemory(); // Belegter Speicher
        double usage = (double) usedMemory / maxMemory; // Auslastung als Prozentsatz

        if (usage > maxUsage) {
            maxUsage = usage;
        }
        // System.out.printf("Speicherauslastung: %.2f%%\n", usage * 100);

        // Überprüfe ob der Schwellenwert überschritten
        if (usage >= Constants.HEAPSPACE_THRESHOLD) {
            Constants.currentTime = System.currentTimeMillis();
            memory.shortTermMemory.updateTimeForAllNodes();

            memory.shortTermMemory.cleanStm();

            System.gc(); // GC manuell aufurufen um Heapspace zu leeren
            System.out.printf("Speicherauslastung: %.2f%%\n", usage * 100);
            // System.out.println("Achtung: Speicherauslastung überschreitet den
            // Schwellenwert!");

        }
    }
}
