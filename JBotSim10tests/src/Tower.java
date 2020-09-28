/************************************************************************************/
/*Dans ce code : 																    */
/*	Les tours ont la liste des voitures qui peuvent recevoir des informations	    */
/* 	Le débit fournit varie en fonction de la classe des techno de communication		*/
/************************************************************************************/

import io.jbotsim.core.Message;
import io.jbotsim.core.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


public abstract class Tower extends Node {

    ArrayList<HashMap<Car, Integer>> l_carUsed = new ArrayList<HashMap<Car, Integer>>();
    float throughputTotal;
    float throughputAvailable;
    int DELTA = 500;

    public ArrayList<HashMap<Car, Integer>> getL_carUsed() {
        return l_carUsed;
    }

    public void setL_carUsed(ArrayList<HashMap<Car, Integer>> l_carUsed) {
        this.l_carUsed = l_carUsed;
    }

    public abstract float updateThroughputSize(int size);
    public abstract float updateThroughputSizePlusOne();

    public void computThroughputAvailable() {
        System.out.println("Nombre de car : " + l_carUsed.size());
    }

    public Tower() {
        this.throughputAvailable = 10;
        this.throughputTotal = 10;
        setIcon("/tower.png");
        //setSize(14);
        setSensingRange(100);
    }

    public Tower(float t, int r, FileManager f) {
        this.throughputAvailable = t;
        this.throughputTotal = t;
        setIcon("/tower.png");
        //setSize(14);
        setSensingRange(r);
        if (this.getClass().equals(WiFi.class))
        {
            f.writefInput("Class : WiFi",true);
        }
        if (this.getClass().equals(Cellular.class))
        {
            f.writefInput("Class : Cellular",true);
        }
        f.writefInput("Source : ",true);
        f.writefInput("Throughput : "+this.throughputTotal,true);
        f.writefInput("Localisation X : "+this.getX(),true);
        f.writefInput("Localisation Y : "+this.getY(),true);
        f.writefInput("Sensing Range : "+r,true);
        f.writefInput("\t",true);
    }


    public Tower(float t, int r, float x, float y) {
        this.throughputAvailable = t;
        this.throughputTotal = t;
        setIcon("/tower.png");
        //setSize(14);
        setSensingRange(r);
        this.setLocation(x,y);
    }

    @Override
    public String toString() {
        return "Tower" + this.getID() + "(" + this.getClass() + ")" + " [" + throughputAvailable + "/" + throughputTotal
                + "]";
    }

    public void onClock() {

        System.out.println();
        System.out.println("*******OnClock de tour " + this.getID() + "****************************");
        System.out.println("-- Debit dispo " + this.throughputAvailable);

        float msgBody = updateThroughputSize(l_carUsed.size());
        sendAll(new Message(msgBody));

        for (Message msg : getMailbox()) {
            addCar(msg);
        }
        getMailbox().clear();
        //outCarListed();
        update();
        System.out.println("******* Fin OnClock de tour " + this.getID() + "****************************");
    }

    public void addCar(Message msg) {
        //System.out.println("temps actuel : " + System.currentTimeMillis());
        HashMap<Car, Integer> c = new HashMap<Car, Integer>();
        boolean carStored = false;
        if (msg.getContent().equals(this)) {
            c.put((Car) msg.getSender(), (int) System.currentTimeMillis());
            for (int i = 0; i < this.l_carUsed.size(); i++) {
                if (this.l_carUsed.get(i).containsKey((Car) msg.getSender())) {
                    this.l_carUsed.get(i).put((Car) msg.getSender(), (int) System.currentTimeMillis());
                    carStored = true;
                }
            }
            c.put((Car) msg.getSender(), (int) System.currentTimeMillis());
            if (!carStored) {
                this.l_carUsed.add(c);
            }
        }
        updateThroughputSize(this.l_carUsed.size());

        //System.out.println("Apres ajout car 1, nouveau debit dispo : "+ this.throughputAvailable);

    }

    public void addCar(Car car) {
        //System.out.println("temps actuel : " + System.currentTimeMillis());
        HashMap<Car, Integer> c = new HashMap<Car, Integer>();
        boolean carStored = false;
        c.put(car, (int) System.currentTimeMillis());
        for (int i = 0; i < this.l_carUsed.size(); i++) {
            if (this.l_carUsed.get(i).containsKey(car)) {
                carStored = true;
            }
        }
        c.put(car, (int) System.currentTimeMillis());
        if (!carStored) {
            this.l_carUsed.add(c);
        }
        updateThroughputSize(this.l_carUsed.size());
        System.out.println("Apres ajout car 2, nouveau debit dispo : "+ this.throughputAvailable);
    }

    public void update() {
        int time = (int) System.currentTimeMillis();
        for (int i = 0; i < this.l_carUsed.size(); i++) {
            HashMap<Car, Integer> carStored = this.l_carUsed.get(i);
            Collection<Integer> timeStored=carStored.values();

            int timePassed=(time- Integertoint(timeStored));
            //System.out.println ("temps passé ="+ timePassed);
            if (timePassed >DELTA)
            {
                this.l_carUsed.remove(i);
            }
        }
    }

    public void removeCar(Car car)
    {
        this.l_carUsed.remove(car);
    }

    public int Integertoint(Collection<Integer> timeStored) {
        int total=0;
        for (int i=1; i<timeStored.toString().length()-1; i++)
        {
            total=total*10+timeStored.toString().charAt(i)-48;
        }
        return total;

    }


    public void outCarListed() {
        System.out.println("Etat de la table de hachage :");
        for (int i = 0; i < this.l_carUsed.size(); i++) {
            System.out.println(this.l_carUsed.get(i).toString());
        }
    }

    public float getThroughputTotal() {
        return throughputTotal;
    }

    public void setThroughputTotal(float throughputTotal) {
        this.throughputTotal = throughputTotal;
    }

    public float getThroughputAvailable() {
        return throughputAvailable;
    }

    public void setThroughputAvailable(float throughputAvailable) {
        this.throughputAvailable = throughputAvailable;
    }

}