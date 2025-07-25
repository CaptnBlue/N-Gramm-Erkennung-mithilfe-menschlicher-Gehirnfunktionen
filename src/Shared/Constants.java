package Shared;

public class Constants {
    // Einstellbare Parameter um LTM aufzubauen
    public final static int NGRAMM_SIZE_FROM = 3;
    public final static int NGRAMM_SIZE_TO = 5;
    public final static int SHORTTOLONG_THRESHOLD = 3; // 0 benutzen um alle aufzunehmen
    public static double HEAPSPACE_THRESHOLD = 0.9;
    public static double DELETESTM_AMOUNT = 0.5;
    public final static int AMOUNT_THREADS = 1;
    // Hilfsvariablen LTM aufbauen
    public static long currentTime;

    // Einstellbare Parameter für beide Mechanismen
    public static String corpus = "resources\\leipzig\\eng_news_2024_1M-sentences.txt";
    public final static boolean WITH_PUNCTUATIONMARKS = true;

    // Einstellbare Parameter für Graphen
    public final static boolean WITH_ADJACENCYLIST = true; // false baut auf matrix auf, basiert nur auf häufigkeiten,
                                                           // ansatz wurde nicht weiterverfolgt

    public final static boolean WITH_HASHEDEDGES = true; // true -> HashedEdges; false -> absolute Häufigkeiten (nicht
                                                         // weiterverfolgt)
    public static int HISTORY = 2; // gibt anzahl an wie viele ngramme zurück angeschaut werden
    public static int OUTPUTMODE = 6; // 0=default randomMode, 1 longestNgramm, 2 mostHashes, 3 most
                                      // incominghashes, 4 weigthed Random, 5 most edges, 6 kombi methode aus
                                      // longestngramm dann most hashes und abschließend random

}
