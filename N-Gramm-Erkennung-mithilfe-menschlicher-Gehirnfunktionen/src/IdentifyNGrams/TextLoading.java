
package IdentifyNGrams;
//Klasse um mit pdfs zu arbeiten wird nicht mehr benötigit

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class TextLoading {

    // Gibt alle PDFs im Ornder resources zurück
    public static List<String> pdfLoader() {

        String pdfFolderPath = "resources"; // Relativer Pfad zum Ordner

        // Hole alle PDF-Dateien im Ordner
        File folder = new File(pdfFolderPath);
        File[] pdfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        List<String> pdfTexte = new ArrayList<>();
        if (pdfFiles != null) {
            for (File pdfFile : pdfFiles) {
                try {

                    PDDocument document = Loader.loadPDF(pdfFile);

                    PDFTextStripper pdfStripper = new PDFTextStripper();

                    // Extrahiere den Text
                    String pdfText = pdfStripper.getText(document);
                    pdfTexte.add(pdfText);

                    // Schließe das Dokument
                    document.close();
                } catch (IOException e) {
                    System.err.println("Fehler beim Verarbeiten der Datei: " + pdfFile.getName());
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Keine PDF-Dateien im angegebenen Ordner gefunden.");
        }
        return pdfTexte;
    }
}