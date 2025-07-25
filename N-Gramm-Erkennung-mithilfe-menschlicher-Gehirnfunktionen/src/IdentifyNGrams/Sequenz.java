
package IdentifyNGrams;

//Basisdatenstruktur für AVL-Baum
public class Sequenz {

    private String nGrammString;
    private int counter = 0;

    public Sequenz(String nGramm) {
        this.setnGrammString(nGramm);

    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getnGrammString() {
        return nGrammString;
    }

    public void setnGrammString(String nGrammString) {
        this.nGrammString = nGrammString;
    }

}
