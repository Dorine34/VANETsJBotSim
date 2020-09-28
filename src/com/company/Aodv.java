package com.company;

public class Aodv {
    int source;
    int nbSauts;
    HighwayCar carVoisin;
    long temps;

    public int getSource() {
        return source;
    }
    public void setSource(int source) {
        this.source = source;
    }
    public int getNbSauts() {
        return nbSauts;
    }
    public void setNbSauts(int nbSauts) {
        this.nbSauts = nbSauts;
    }
    public HighwayCar getCarVoisin() {
        return carVoisin;
    }
    public void setCarVoisin(HighwayCar carVoisin) {
        this.carVoisin = carVoisin;
    }
    @Override
    public String toString() {
        return "aodv [source=" + source + ", nbSauts=" + nbSauts + ", carVoisin=" + carVoisin + "]";
    }

    public Aodv(int source, int nbSauts, HighwayCar carVoisin, long temps) {
        super();
        this.source = source;
        this.nbSauts = nbSauts;
        this.carVoisin = carVoisin;
        this.temps=temps;
    }
    public long getTemps() {
        return temps;
    }
    public void setTemps(long temps) {
        this.temps = temps;
    }
}