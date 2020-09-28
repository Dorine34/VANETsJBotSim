package com.company;

public class Prob {
    Integer demande ;
    HighwayCar car;
    double q;
    double y;
    long tempsReception;


    public Prob(Integer demande, HighwayCar car, float q, float y, long tempsReception) {
        super();
        this.demande = demande;
        this.car = car;
        this.q = q;
        this.y = y;
        this.tempsReception=tempsReception;
    }
    @Override
    public String toString() {
        return "Prob [demande=" + demande + ", car=" + car +
                ", q=" + q + ", y=" + y + " temps = "+this.tempsReception +"]";
    }
    public HighwayCar getCar() {
        return car;
    }
    public long getTempsReception() {
        return tempsReception;
    }
    public void setTempsReception(long tempsReception) {
        this.tempsReception = tempsReception;
    }
    public void setCar(HighwayCar car) {
        this.car = car;
    }
    public Integer getDemande() {
        return demande;
    }
    public void setDemande(Integer demande) {
        this.demande = demande;
    }
    public double getQ() {
        return q;
    }
    public void setQ(double d) {
        this.q = d;
    }
    public double getY() {
        return y;
    }
    public void setY(double d) {
        this.y = d;
    }

}