/*
 * package Archiv;
 * import java.math.BigInteger;
 * import java.util.ArrayList;
 * import java.util.Arrays;
 * import java.util.HashMap;
 * import java.util.HashSet;
 * import java.util.List;
 * import java.util.Set;
 * 
 * 
 * 
 * public class Encoder {
 * 
 * private int[] sdr = new int[Constants.SDR_SIZE*Constants.NGRAMM_SIZE]; // Für
 * sequenz
 * 
 * public static HashMap<BigInteger, String> map = new HashMap<>();
 * 
 * public static int[] encodeToSDR(String text) {
 * int[] sdr = new int[Constants.SDR_SIZE * Constants.NGRAMM_SIZE]; // Leerer
 * SDR-Vektor
 * List<String> words = tokenizeNGram(text);
 * // Set für alle möglichen Teilzeichenfolgen
 * for (int i = 0; i < words.size(); i++) {
 * Set<String> substrings = getSubstrings(words.get(i));
 * // Hashing der Teilzeichenfolgen und Aktivierung der SDR-Bits
 * for (String sub : substrings) {
 * // System.out.println(sub.hashCode());
 * int hash = Math.abs(sub.hashCode() % Constants.SDR_SIZE); // Hash berechnen
 * und modulieren
 * sdr[hash +
 * (i * Constants.SDR_SIZE)] = 1; // Setze entsprechendes Bit auf 1
 * }
 * 
 * }
 * for (int i = 0; i < Constants.NGRAMM_SIZE; i++) {
 * 
 * BigInteger hashKey = createKey(Arrays.copyOfRange(sdr, i *
 * Constants.SDR_SIZE,
 * Constants.SDR_SIZE * (i + 1)));
 * if (!map.containsKey(hashKey)) {
 * map.put(hashKey, words.get(i));
 * }
 * }
 * 
 * return sdr;
 * }
 * 
 * // Hilfsfunktion, um alle möglichen Teilzeichenfolgen eines Wortes zu
 * erhalten
 * private static Set<String> getSubstrings(String text) {
 * Set<String> substrings = new HashSet<>();
 * int len = text.length();
 * 
 * for (int i = 0; i < len; i++) {
 * for (int j = i + 1; j <= len; j++) {
 * substrings.add(text.substring(i, j));
 * }
 * }
 * return substrings;
 * }
 * 
 * public static List<String> tokenizeNGram(String ngram) {
 * // Teile den N-Gramm-String in Wörter auf
 * String[] words = ngram.split("\\s+"); // Trenne nach Leerzeichen
 * List<String> tokens = new ArrayList<>();
 * for (String word : words) {
 * tokens.add(word);
 * }
 * return tokens;
 * }
 * 
 * // Hilfsfunktion, Keybestimmung für Hashmap
 * private static BigInteger createKey(int[] bitArray) {
 * StringBuilder binaryString = new StringBuilder();
 * 
 * // Füge jedes Bit zum StringBuilder hinzu
 * for (int i = 0; i < bitArray.length; i++) {
 * 
 * if (bitArray[i] == 1) {
 * binaryString.append(i); // Füge das Bit hinzu, wenn starter true ist
 * }
 * }
 * 
 * // Wenn binaryString leer ist, bedeutet dies, dass nur Nullen vorhanden waren
 * // In diesem Fall geben wir 0 zurück
 * String sol = binaryString.toString();
 * // Konvertiere den String in einen Integer
 * return new BigInteger(sol);
 * }
 * 
 * public static String decodeSdr(int[] sdr) {
 * StringBuilder returnString = new StringBuilder();
 * for (int i = 0; i < Constants.NGRAMM_SIZE; i++) {
 * StringBuilder binaryString = new StringBuilder();
 * for (int j = i * Constants.SDR_SIZE; j < Constants.SDR_SIZE * (i + 1); j++) {
 * 
 * if (sdr[j] == 1) {
 * binaryString.append(j % Constants.SDR_SIZE);
 * }
 * 
 * }
 * String index = binaryString.toString();
 * 
 * returnString.append(map.get(new BigInteger(index)));
 * returnString.append(" ");
 * 
 * }
 * String sol = returnString.toString();
 * return sol;
 * }
 * }
 * 
 * 
 * //Weiter sachen aus Sequenz
 * 
 * private int activeBits = 0;
 * public void calculateActiveBits(){
 * for (int i = 0; i < this.sdr.length; i++) {
 * 
 * if (sdr[i] == 1) {
 * this.activeBits++;
 * }
 * }
 * }
 * 
 * public int calculateOverlap(int[] sdrToCompare){
 * int overlapCount = 0;
 * 
 * // Überprüfen jedes Bits in den SDR-Vektoren
 * for (int i = 0; i < Constants.SDR_SIZE*Constants.NGRAMM_SIZE-1; i++) {
 * // Zählen der Positionen, an denen beide Bits auf 1 gesetzt sind
 * if (this.getSdr()[i] == 1 && sdrToCompare[i] == 1) {
 * overlapCount++;
 * }
 * }
 * 
 * return overlapCount;
 * }
 * 
 * // Ausgabe der HashMap wo SDRs strings speichern
 * public static void outputHashMap(){
 * System.out.println("Inhalt der HashMap:");
 * for (Map.Entry<BigInteger, String> entry : Encoder.map.entrySet()) {
 * System.out.println("Schlüssel: " + entry.getKey() + ", Wert: " +
 * entry.getValue());
 * }
 * }
 */