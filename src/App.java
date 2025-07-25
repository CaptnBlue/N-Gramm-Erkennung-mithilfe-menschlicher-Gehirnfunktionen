import GraphCreation.*;
import IdentifyNGrams.*;

/* Wichtige FUnktionen IdentifyNGrams.RunAlgorithm.start(); Baut LTM auf; CreateGraph.buildGraph(); erstellt Graphen aus LTM und Korpus ; 
LiklihoodCheck.checkProbability(); geht verschiedene Knoten durch und erzeugt Ausgaben */
public class App {

    public static void main(String[] args) throws Exception {
        IdentifyNGrams.RunAlgorithm.start();
        CreateGraph.buildGraph();
        LiklihoodCheck.checkProbability();

    }
}
