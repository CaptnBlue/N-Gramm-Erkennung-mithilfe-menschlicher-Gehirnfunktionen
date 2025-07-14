import GraphCreation.*;
import Shared.Constants;

public class App {

    public static void main(String[] args) throws Exception {
        IdentifyNGrams.RunAlgorithm.start();
        while (Constants.HISTORY < 3) {
            CreateGraph.buildGraph();
            LiklihoodCheck.checkProbability();
            Constants.HISTORY++;
        }

    }
}
