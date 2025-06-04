# N-Gramm-Erkennung mithilfe menschlicher Gehirnfunktionen

Dieses Repository enthält den Quellcode zur Masterarbeit **„N-Gramm-Erkennung mithilfe menschlicher Gehirnfunktionen“**.

## Inhalt der Arbeit

Ziel der Arbeit ist es, ein Modell zu entwickeln, das N-Gramme aus Texten erkennt und verarbeitet, inspiriert von neurobiologischen Mechanismen des menschlichen Gehirns.

## Ressourcen

Für die Durchführung der Analyse muss im Hauptverzeichnis ein Ordner mit dem Namen `ressources` angelegt werden. In diesem Ordner können beliebige Textkorpora im `.txt`-Format abgelegt werden.

> **Hinweis:** Der Pfad zu den Korpora muss in der Datei `RunAlgorithm.java` entsprechend angepasst werden.

Die integrierte Textbereinigung ist speziell auf die Korpora abgestimmt, die unter https://www.corpusdata.org/intro.asp verfügbar sind.

Beim Ausführen des Programms werden die enthaltenen Texte automatisch eingelesen und die N-Gramme extrahiert. Die Parameter lassen sich in der `Constants.java` einstellen. Abschließend finden sich die Sequenztabellen des LTMs und STMs im Ordner `ressources`.
