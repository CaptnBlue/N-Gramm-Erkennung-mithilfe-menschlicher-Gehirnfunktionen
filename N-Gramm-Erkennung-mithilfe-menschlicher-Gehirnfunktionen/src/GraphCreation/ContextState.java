package GraphCreation;

import java.util.List;

//Hilfsklasse für Kombination möglicher Kontexte bei Kantenbelegung
public class ContextState {

    int fromIndex;
    int collected;
    List<String> context;

    ContextState(int fromIndex, int collected, List<String> context) {
        this.fromIndex = fromIndex;
        this.collected = collected;
        this.context = context;
    }
}
