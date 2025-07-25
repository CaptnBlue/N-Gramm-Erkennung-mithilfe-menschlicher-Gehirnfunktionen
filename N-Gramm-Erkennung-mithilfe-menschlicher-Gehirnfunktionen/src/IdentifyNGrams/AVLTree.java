package IdentifyNGrams;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import Shared.Constants;

public class AVLTree {

    private AVLNode root;

    public AVLTree() {
        this.root = null;
    }

    public void insert(Sequenz data, boolean isLtm) {
        root = insert(root, data, isLtm);
    }

    // Höhe eines Knotens berechen
    private int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }

    // Balance herausfinden
    private int getBalance(AVLNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    // Einfügemethode mit Balancierung
    private AVLNode insert(AVLNode node, Sequenz data, boolean isLtm) {
        if (isLtm && node == null) {
            AVLNode first = new AVLNode(data);
            first.seqData.setCounter(Constants.SHORTTOLONG_THRESHOLD);
            return first;
        }

        if (node == null) {
            AVLNode first = new AVLNode(data);

            first.seqData.setCounter(1);
            return first;

        }

        // String vergleich um gleiche Sequenz zu finden
        if (data.getnGrammString().compareTo(node.seqData.getnGrammString()) < 0) {
            node.left = insert(node.left, data, isLtm);
        } else if (data.getnGrammString().compareTo(node.seqData.getnGrammString()) > 0) {
            node.right = insert(node.right, data, isLtm);
        } else {
            return node; // verhindert doppelte Werte
        }

        // Baum balancieren
        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && data.getnGrammString().compareTo(node.left.seqData.getnGrammString()) < 0) {
            return rightRotate(node);
        }

        if (balance < -1 && data.getnGrammString().compareTo(node.right.seqData.getnGrammString()) > 0) {
            return leftRotate(node);
        }

        if (balance > 1 && data.getnGrammString().compareTo(node.left.seqData.getnGrammString()) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && data.getnGrammString().compareTo(node.right.seqData.getnGrammString()) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    // Suchen ob NGramm bereits vorhanden ist wenn ja coutner erhöhen
    public boolean contains(String nGramm, boolean isStm, Memory memory) {
        return contains(root, nGramm, isStm, memory);
    }

    private boolean contains(AVLNode node, String nGramm, boolean isStm, Memory memory) {
        if (node == null) {
            return false; // Basisfall Baum ist leer oder Knoten wurde nicht gefunden
        }

        int cmp = nGramm.compareTo(node.seqData.getnGrammString());

        if (cmp < 0) {
            return contains(node.left, nGramm, isStm, memory);
        } else if (cmp > 0) {
            return contains(node.right, nGramm, isStm, memory);
        } else {
            // Prüfen und erhöhen des Counters
            node.seqData.setCounter(node.seqData.getCounter() + 1);

            // Übertragung ins LTM wenn Schwelle erreicht
            if (node.seqData.getCounter() >= Constants.SHORTTOLONG_THRESHOLD && isStm) {
                memory.longTermMemory.insert(node.seqData, true);
                memory.shortTermMemory.delete(node.seqData.getnGrammString());
            }
            return true;
        }
    }

    public void delete(String nGrammString) {
        root = deleteRec(root, nGrammString);
    }

    private AVLNode deleteRec(AVLNode root, String nGrammString) {
        if (root == null) {
            return root;
        }

        if (nGrammString.compareTo(root.seqData.getnGrammString()) < 0) {
            root.left = deleteRec(root.left, nGrammString);
        } else if (nGrammString.compareTo(root.seqData.getnGrammString()) > 0) {
            root.right = deleteRec(root.right, nGrammString);
        } else {
            if (root.left == null || root.right == null) {
                AVLNode temp = root.left != null ? root.left : root.right;

                if (temp == null) {
                    root = null;
                } else {
                    root = temp;
                }
            } else {
                AVLNode temp = minValueNode(root.right);

                root.seqData = temp.seqData;

                root.right = deleteRec(root.right, temp.seqData.getnGrammString());
            }
        }

        if (root == null) {
            return root;
        }

        root.height = Math.max(height(root.left), height(root.right)) + 1;

        int balance = getBalance(root);

        if (balance > 1 && getBalance(root.left) >= 0) {
            return rightRotate(root);
        }

        if (balance < -1 && getBalance(root.right) <= 0) {
            return leftRotate(root);
        }

        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }

        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }

    // Methode zum Finden des kleinsten Werts im rechten Subbaum
    private AVLNode minValueNode(AVLNode node) {
        AVLNode current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    // LTM und/oder STM in .txt übertragen
    public void printByCounter(boolean isLtm) {

        List<AVLNode> nodeList = new ArrayList<>();
        collectNodes(root, nodeList);
        String filename;
        // Sortiere die Liste der Knoten nach counter absteigend
        nodeList.sort((node1, node2) -> Integer.compare(node2.seqData.getCounter(), node1.seqData.getCounter()));
        if (isLtm) {
            filename = "output/nods/ltm.txt";
        } else {
            filename = "output/nods/stm.txt";
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Schreiben des Headers
            if ((isLtm)) {
                writer.write("Inhalt des LongTermMemory:");
            } else {
                writer.write("Inhalt des ShortTermMemory:");
            }
            writer.newLine();

            // Ausgabe der Knoten
            int nr = 0;
            for (AVLNode node : nodeList) {

                nr += 1;

                writer.write(
                        nr + " "
                                + " Sequenz: " + node.seqData.getnGrammString()
                                + ", Time: " + (node.time)
                                + ", Counter: " + node.seqData.getCounter());
                writer.newLine();
            }

        } catch (IOException e) {

            System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
        }
    }

    // Hilfsmethode zum Sammeln aller Knoten im Baum
    private void collectNodes(AVLNode node, List<AVLNode> nodeList) {
        if (node != null) {
            nodeList.add(node);
            collectNodes(node.left, nodeList);
            collectNodes(node.right, nodeList);
        }
    }

    public void updateTimeForAllNodes() {
        updateTimeRec(root);
    }

    private void updateTimeRec(AVLNode node) {
        if (node == null) {
            return;
        }

        node.updateTime();

        // Linken und rechten Teilbaum rekursiv besuchen
        updateTimeRec(node.left);
        updateTimeRec(node.right);
    }

    public static void merge(List<Memory> all, Memory target) {
        // Iteriere über alle Gruppen von AVL-Bäumen
        for (Memory memory : all) {
            mergeTree(memory.longTermMemory, target, false);
            mergeTree(memory.shortTermMemory, target, true);

        }

    }

    private static void mergeTree(AVLTree source, Memory target, boolean isStm) {
        if (source == null || target == null) {
            return;
        }
        // Rekursiv alle Knoten des Quellbaums in den Zielbaum einfügen
        mergeTreeRec(source.root, target, isStm);
    }

    private static void mergeTreeRec(AVLNode node, Memory target, boolean isStm) {
        if (node == null) {
            return;
        }

        // Prüfen ob die Sequenz bereits existiert
        if (!target.longTermMemory.containsMerge(node, isStm, target)) {
            if (!target.shortTermMemory.containsMerge(node, isStm, target)) {
                if (node.seqData.getCounter() < Constants.SHORTTOLONG_THRESHOLD) {
                    target.shortTermMemory.insertMerge(node, !isStm);
                } else {
                    target.longTermMemory.insertMerge(node, !isStm);
                }
            }

        }

        mergeTreeRec(node.left, target, isStm);
        mergeTreeRec(node.right, target, isStm);
    }

    public boolean containsMerge(AVLNode node, boolean isStm, Memory memory) {
        return containsMerge(root, node, isStm, memory);
    }

    private boolean containsMerge(AVLNode node, AVLNode nodeCheck, boolean isStm, Memory memory) {
        if (node == null) {
            return false; // Basisfall
        }

        int cmp = nodeCheck.seqData.getnGrammString().compareTo(node.seqData.getnGrammString()); // String vergleich

        if (cmp < 0) {
            return containsMerge(node.left, nodeCheck, isStm, memory); // Suche im linken Teilbaum
        } else if (cmp > 0) {
            return containsMerge(node.right, nodeCheck, isStm, memory); // Suche im rechten Teilbaum
        } else {
            // Prüfen und erhöhen des Counters

            node.seqData.setCounter(node.seqData.getCounter() + nodeCheck.seqData.getCounter());
            if (node.time < nodeCheck.time) {
                node.time = nodeCheck.time;
            }

            if (node.seqData.getCounter() >= Constants.SHORTTOLONG_THRESHOLD && isStm) {
                memory.longTermMemory.insertMerge(node, true);
                memory.shortTermMemory.delete(node.seqData.getnGrammString());
            }
            return true;
        }
    }

    public void insertMerge(AVLNode node, boolean isLtm) {
        root = insertMerge(root, node, isLtm);
    }

    private AVLNode insertMerge(AVLNode node, AVLNode data, boolean isLtm) {
        if (isLtm && node == null) {
            AVLNode first = new AVLNode(data.seqData);
            first.seqData.setCounter(data.seqData.getCounter());
            first.time = data.time;
            return first;
        }

        if (node == null) {
            AVLNode first = new AVLNode(data.seqData);

            first.seqData.setCounter(data.seqData.getCounter());
            first.time = data.time;
            return first;

        }

        if (data.seqData.getnGrammString().compareTo(node.seqData.getnGrammString()) < 0) {
            node.left = insertMerge(node.left, data, isLtm);
        } else if (data.seqData.getnGrammString().compareTo(node.seqData.getnGrammString()) > 0) {
            node.right = insertMerge(node.right, data, isLtm);
        } else {
            return node; // keine doppelten Werte
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && data.seqData.getnGrammString().compareTo(node.left.seqData.getnGrammString()) < 0) {
            return rightRotate(node);
        }

        if (balance < -1 && data.seqData.getnGrammString().compareTo(node.right.seqData.getnGrammString()) > 0) {
            return leftRotate(node);
        }

        if (balance > 1 && data.seqData.getnGrammString().compareTo(node.left.seqData.getnGrammString()) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && data.seqData.getnGrammString().compareTo(node.right.seqData.getnGrammString()) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    // Durchgeht solange das STM bis wirklich alle Sequnezen übertragen worden sind
    public static void transferSTMtoLTM(Memory memory) {
        boolean found;
        do {
            found = transferSTMtoLTMRec(memory.shortTermMemory.root, memory);
        } while (found);
    }

    private static boolean transferSTMtoLTMRec(AVLNode root, Memory memory) {
        if (root == null)
            return false;

        boolean transferred = false;

        // Linken Teilbaum prüfen
        if (transferSTMtoLTMRec(root.left, memory)) {
            transferred = true;
        }

        // Rechten Teilbaum prüfen
        if (transferSTMtoLTMRec(root.right, memory)) {
            transferred = true;
        }

        // Schwellenwert prüfen und ggf. verschieben
        if (root.seqData.getCounter() >= Constants.SHORTTOLONG_THRESHOLD) {
            memory.longTermMemory.insertMerge(root, true);
            memory.shortTermMemory.delete(root.seqData.getnGrammString());
            transferred = true;
        }

        return transferred;
    }

    // Methode zur Vergesslichkeitskomponente
    public void cleanStm() {
        List<AVLNode> nodeList = new ArrayList<>();
        collectNodes(root, nodeList);

        // Sortiere die Liste nach (time / counter) absteigend
        nodeList.sort(Comparator.comparingDouble(node -> -(node.time / (double) node.seqData.getCounter())));

        // Berechne die Anzahl der zu löschenden Elemente
        int deleteCount = (int) (nodeList.size() * Constants.DELETESTM_AMOUNT);

        // Entferne die obersten deleteCount Elemente
        for (int i = 0; i < deleteCount; i++) {
            this.delete(nodeList.get(i).seqData.getnGrammString());
        }
    }

    public int size() {
        return size(root);
    }

    private int size(AVLNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + size(node.left) + size(node.right);
    }

    public int totalCounter() {
        return totalCounter(root);
    }

    private int totalCounter(AVLNode node) {
        if (node == null) {
            return 0;
        }
        int current = node.seqData != null ? node.seqData.getCounter() : 0;
        return current + totalCounter(node.left) + totalCounter(node.right);
    }

}
