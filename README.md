# N-Gramm-Erkennung mithilfe menschlicher Gehirnfunktionen

Dieses Repository enthält den Quellcode zur Masterarbeit **„N-Gramm-Erkennung mithilfe menschlicher Gehirnfunktionen“**.

## Inhalt der Arbeit

Ziel der Arbeit ist es, ein Modell zu entwickeln, das N-Gramme aus Texten erkennt und verarbeitet, inspiriert von neurobiologischen Mechanismen des menschlichen Gehirns.

## Ressourcen

Für die Durchführung der Analyse muss im Hauptverzeichnis ein Ordner mit dem Namen `ressources` angelegt werden. In diesem Ordner können beliebige Textkorpora im `.txt`-Format abgelegt werden. Geeignete Kopora finden sich z.B. unter https://wortschatz.uni-leipzig.de/.

> **Hinweis:** Der Pfad zu den Korpora muss in der Datei `Constants.java` entsprechend angepasst werden.

Beim Ausführen der `IdentifyNGrams.RunAlgorithm.start();` Funktion kann der enthaltene Korpus eingelesen und die N-Gramme extrahiert. Die Parameter lassen sich in der Datei `Constants.java` einstellen. Anschließend werden die Sequenztabellen des LTM und STM im Ordner ressources/nods gespeichert.

Darauf folgend kann über die Klasse CreateGraph ein Graph, wie in der Arbeit beschrieben, aufgebaut werden. Die verschiedenen Parameter befinden sich ebenfalls in der `Constants.java`. Der erzeugte Graph wird abschließend als .txt-Datei gespeichert.

Um später Ausgaben zu erzeugen, muss die entsprechende Methode in der Klasse TraverseGraph aufgerufen werden. Falls ein direkter Aufruf gewünscht ist, kann der Graph-Parameter auf null gesetzt werden. Als erste Methodenvariable sollte in diesem Fall eine gewünschte Eingabesequenz angegeben werden, die im LTM enthalten ist. Andernfalls kann auch über die Klasse LiklihoodCheck mehrere Ausgaben erzeugt werden und auf ihre Einzigaritgkeit überprüft werden.
