package IdentifyNGrams;
//Speicehrklasse für zusammengehörige LTMs und STMs

public class Memory {

    public AVLTree longTermMemory;
    public AVLTree shortTermMemory;

    public Memory() {
        this.longTermMemory = new AVLTree();
        this.shortTermMemory = new AVLTree();

    }

}
