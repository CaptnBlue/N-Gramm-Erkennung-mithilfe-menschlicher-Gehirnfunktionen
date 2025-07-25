package IdentifyNGrams;

import Shared.Constants;

public class AVLNode {

    Sequenz seqData;
    AVLNode left, right;
    int height;
    long time = 0;
    long timeToAdd;

    AVLNode(Sequenz data) {
        this.seqData = data;
        this.height = 1;
        this.timeToAdd = System.currentTimeMillis();
    }

    // Methode zur Zeitakutalisierung der Sequenz
    public void updateTime() {
        long time = System.currentTimeMillis() - this.timeToAdd;
        this.timeToAdd = Constants.currentTime;
        this.time = (this.time + time);
    }

}
