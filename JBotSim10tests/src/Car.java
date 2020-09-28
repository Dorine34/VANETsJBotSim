
/************************************************************************************/
/*Dans ce code : 																    */
/*	Les voitures ont : - la liste des tours candidates								*/
/*		-une seule tour, unique possibilit� pour emettre							*/
/*		-l'option Bluetooth : tour personnelle (emetteur que par 1)					*/
/*					reception par toute voiture a portee (broadcast)				*/
/*					limite 7 esclave												*/
/************************************************************************************/

import io.jbotsim.core.Message;
import io.jbotsim.core.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;


public class Car extends Node {
    ArrayList<Node> l_towerAvailable = new ArrayList<Node>();
    FileManager f = new FileManager();
    Node electedTower;
    float availableThroughput;

    float HYSTERESIS = 2;
    float ETA = (float) 1;
    float PARAM = (float) 0.5;

    float GAMMA = (float) 0.5;
    float TAU = (float) 1;

    int DELTA = 500;
    int source = -1;

    // ici cle= source vector qui pour chaque voisin v enregistre W+q+Y
    HashMap<Integer, Vector> RoutingTable = new HashMap<>();
    HashMap<Integer, Car> FatherTable = new HashMap<>();

    // utilisable dans le cas de AODV
    HashMap<Integer, Integer> AODVTable = new HashMap<>();

    // temps
    long TimeBegin;
    boolean bPassed = true;

    public Car() {
        this.TimeBegin = System.currentTimeMillis();
        setIcon("/car.png");
        //setSize(10);
        setSensingRange(100);
    }

    public Car(int s) {
        this.TimeBegin = System.currentTimeMillis();
        setIcon("/car.png");
        //setSize(10);
        setSensingRange(100);
        source = s;
        //this.setIntColor(s);

    }

    public Car(int s, double x, double y) {
        this.TimeBegin = System.currentTimeMillis();
        setIcon("/car.png");
        //setSize(10);
        setSensingRange(100);
        source = s;
        //
        // this.setIntColor(s);
        this.setLocation(x, y);
    }

    public Car(int s, double x, double y, FileManager file) {
        this.TimeBegin = System.currentTimeMillis();
        file = f;
        setIcon("/car.png");
        //setSize(10);
        setSensingRange(100);
        source = s;
        //this.setIntColor(s);
        this.setLocation(x, y);
        // f.writefos("--DEB "+isEquilibrium());
    }

    public Car(int s, FileManager file) {
        this.TimeBegin = System.currentTimeMillis();
        file = f;
        setIcon("/car.png");
        //setSize(10);
        setSensingRange(200);
        source = s;
        if (s > 0) {
            //this.setIntColor(s);
        }
        Random r = new Random();
        int x = 100 + r.nextInt(350);
        int y = r.nextInt(300);
        this.setLocation(x, y);
        f.writefInput("Class : Car",true);
        f.writefInput("Source : "+this.source,true);
        f.writefInput("Throughput : ",true);
        f.writefInput("Localisation X : "+this.getX(),true);
        f.writefInput("Localisation Y : "+this.getY(),true);
        f.writefInput("Sensing Range : ",true);
        f.writefInput("\t",true);
        // f.writefos("--DEB "+isEquilibrium());
    }

    public void onClock() {
        System.out.println("Bienvenue dans onclock () avec " + this.availableThroughput);

        if (this.electedTower == null) {
            electionTest();
            System.out.println("-------------Onclock de car " + this.getID() + " (" + this.availableThroughput + ")");
        } else {
            this.availableThroughput = ((Tower) this.electedTower).getThroughputAvailable();
            System.out.println("-------------Onclock de car " + this.getID() + " (" + this.availableThroughput
                    + ") avec tour" + this.electedTower.getID());
        }

        // election avec algoRAT
        // choix possibles :

        electionGen();
        // electionTest();
        // electionMax() ;

        // election du pere

        // messReceip();
        messAODV();

        messSent();
        // sortie
        OutAvailableTower();

        // verification

        boolean b = isEquilibrium();
        // f.writefos("--"+b+" au temps "+(System.currentTimeMillis()- this.TimeBegin));
        // boolean po = isParetoOptimal();

        if (b && bPassed) {
            // f.writefos("--"+b+" au temps "+(System.currentTimeMillis()- this.TimeBegin));
            bPassed = false;

        }
        System.out.println("equilibre de nash atteint ? " + b);
        // System.out.println("-------------Fin Onclock de car avec "
        // +this.availableThroughput);

    }

    public void messReceip() {
        for (Message msg : getMailbox()) {
            if (!msg.getSender().getClass().equals(Car.class)) {
                if ((!msg.getSender().getClass().equals(BlueTooth.class))) {
                    AddAvailableTower((Tower) msg.getSender());
                } else {
                    AddAvailableTower((BlueTooth) msg.getSender());
                }
            } else {
                Node towerElectedofSender = ((Car) msg.getSender()).getElectedTower();
                if (this.l_towerAvailable.contains(towerElectedofSender)) {// communication possible
                    if (msg.getContent().getClass().equals(Integer.class)) {// source
                        int key = (int) msg.getContent();
                        System.out.println("OK " + RoutingTable.containsKey(key));
                        if (RoutingTable.containsKey(key)) {
                            updateKey(key, msg);
                            electionFather(key);
                            System.out.println("liste des peres " + FatherTable.entrySet());
                            System.out.println("liste des donnees " + RoutingTable.entrySet());
                        } else {
                            inserNewKey(key, msg);
                            System.out.println("liste des peres " + FatherTable.entrySet());
                            System.out.println("liste des donnees " + RoutingTable.entrySet());
                        }
                    }
                } else {
                    System.out.println("NON OK");
                }
                outMess(msg);

            }

            updateThroughput(msg);
        }
        getMailbox().clear();
    }

    public void messAODV() {
        for (Message msg : getMailbox()) {
            if (!msg.getSender().getClass().equals(Car.class)) {
                if ((!msg.getSender().getClass().equals(BlueTooth.class))) {
                    AddAvailableTower((Tower) msg.getSender());
                } else {
                    AddAvailableTower((BlueTooth) msg.getSender());
                }
            } else {
                Node towerElectedofSender = ((Car) msg.getSender()).getElectedTower();
                if (this.l_towerAvailable.contains(towerElectedofSender)) {// communication possible
                    if (msg.getContent().getClass().equals(Integer.class)) {// source
                        int key = (int) msg.getContent();
                        System.out.println(" " + AODVTable.containsKey(key));
                        if (AODVTable.containsKey(key)) {
                            updateKey(key, msg);

                            if (((Car) msg.getSender()).getSource() == key) {
                                AODVTable.put(key, 1);
                                FatherTable.put(key, (Car) msg.getSender());
                            } else {
                                Car c = (Car) msg.getSender();
                                int num = c.getAODVTable().get(key);
                                if ((num + 1) < this.getAODVTable().get(key)) {
                                    AODVTable.put(key, num + 1);
                                    FatherTable.put(key, (Car) msg.getSender());
                                }
                            }
                        }
                    }

                }

            }

            updateThroughput(msg);
        }
        getMailbox().clear();
    }

    public HashMap<Integer, Integer> getAODVTable() {
        return AODVTable;
    }

    public void setAODVTable(HashMap<Integer, Integer> aODVTable) {
        AODVTable = aODVTable;
    }

    public void electionFather(int key) {
        float rand = (float) Math.random();
        float Qd = 0;
        float QdTotal = 0;
        for (int i = 0; i < RoutingTable.get(key).size(); i++) {
            Vector vectNode = (Vector) RoutingTable.get(key).get(i);
            Qd = (float) vectNode.get(3);
            QdTotal += Qd;
            if (rand < QdTotal) {
                int keyElected = Math.round((float) vectNode.get(0));
                rand = 2;
                Node towerElected = this.getElectedTower();
                // System.out.println("++++ELECTION " + vectNode.get(0) + " " +
                // towerElected.getClass().equals(Tower.class)
                // + " :" + towerElected.getClass() + " " + Tower.class);

                for (int j = 0; j < this.getInNeighbors().size(); j++) {
                    if (this.getInNeighbors().get(j).getID() == keyElected) {
                        FatherTable.put(key, (Car) this.getInNeighbors().get(j));
                    }
                }

            }
        }
    }

    public void inserNewKey(int key, Message msg) {

        FatherTable.put(key, (Car) msg.getSender());

        float Idv = (float) (((Car) msg.getSender()).getID());
        float Wdv = (float) (((Car) msg.getSender()).getAvailableThroughput() - 0.1);
        float Ydv = 1;
        float qdv = 1;
        Vector<Float> vectInfoV = new Vector<Float>();
        vectInfoV.add(Idv);
        vectInfoV.add(Wdv);
        vectInfoV.add(Ydv);
        vectInfoV.add(qdv);

        Vector<Vector<Float>> vectV = new Vector<Vector<Float>>();
        vectV.add(vectInfoV);
        RoutingTable.put(key, vectV);
    }

    public void updateKey(int key, Message msg) {
        float idSender = msg.getSender().getID();
        for (int i = 0; i < RoutingTable.get(key).size(); i++) {
            Vector vectNode = (Vector) RoutingTable.get(key).get(i);
            float idNode = (float) vectNode.get(0);
            if (idNode == idSender) {
                updatedv(msg, key, i);
            }
        }
    }

    public void updatedv(Message msg, int key, int v) {
        Vector vectNode = (Vector) RoutingTable.get(key).get(v);

        float idv = (float) vectNode.get(0);
        float Wdv = (float) (((Car) msg.getSender()).getAvailableThroughput() - 0.1);
        float Ydv = (float) vectNode.get(2);
        float Qdv = (float) vectNode.get(3);

        Ydv = Ydv + GAMMA * (Wdv - TAU * Ydv) / Qdv;
        Ydv = Math.min(Ydv, this.availableThroughput);
        // System.out.println("Ydv ="+Ydv+"( "+Ydv+" + "+GAMMA+" *( "+Wdv+" - "+TAU+ " *
        // "+Ydv+" )/"+ Qdv);

        float sumY = 0;
        float Ydu;
        for (int i = 0; i < RoutingTable.get(key).size(); i++) {
            if (i == v) {
                sumY += Math.exp(Ydv);
            } else {
                Vector vectu = (Vector) RoutingTable.get(key).get(i);
                Ydu = (float) vectu.get(2);
                sumY += Math.exp(Ydu);
            }
        }
        Qdv = (float) (Math.exp(Ydv) / sumY);

        Vector<Float> vectNewInfoV = new Vector<Float>();
        vectNewInfoV.add(idv);
        vectNewInfoV.add(Wdv);
        vectNewInfoV.add(Ydv);
        vectNewInfoV.add(Qdv);

        RoutingTable.get(key).remove(v);
        RoutingTable.get(key).add(vectNewInfoV);

    }

    public void outMess(Message msg) {
        System.out.println("Message venant de " + msg.getSender() + " contenant " + msg.getContent());
    }

    public void messSent() {
        // message reservé aux tours
        if (this.electedTower != null) {
            sendAll(new Message(electedTower));
        }

        if (this.source > 0) {
            sendAll(new Message(source));
        }

    }

    public ArrayList<Node> getL_towerAvailable() {
        return l_towerAvailable;
    }

    public void setL_towerAvailable(ArrayList<Node> l_towerAvailable) {
        this.l_towerAvailable = l_towerAvailable;
    }

    public Node getElectedTower() {
        return electedTower;
    }

    public void setElectedTower(Node electedTower) {
        this.electedTower = electedTower;
    }

    public float getAvailableThroughput() {
        return availableThroughput;
    }

    public void setAvailableThroughput(float availableThroughput) {
        this.availableThroughput = availableThroughput;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public void electionTest() {
        if ((this.electedTower == null) && (this.l_towerAvailable.size() > 0)) {

            // update dans tower
            ((Tower) l_towerAvailable.get(0)).addCar(this);

            // update dans car
            this.electedTower = this.l_towerAvailable.get(0);
            this.availableThroughput = ((Tower) l_towerAvailable.get(0)).getThroughputAvailable();

        }
        System.out.println("--Election Normale : debit k =" + this.availableThroughput);
    }

    public void electionGen() {
        System.out.println("--Election Generique : debit k =" + this.availableThroughput);
        for (int i = 0; i < this.l_towerAvailable.size(); i++) {
            System.out.println(this.l_towerAvailable.get(i).getClass());
            if (!this.l_towerAvailable.get(i).getClass().equals(Car.class)) {
                if (testETA(i) && testPARAM()) {
                    if (testCLASS(i)) {
                        System.out.println("  changement de tour pour " + this.l_towerAvailable.get(i).getID());

                        newTower(i);

                    } else {
                        if (testHYSTERESIS(i)) {
                            System.out.println("  changement de tour pour " + this.l_towerAvailable.get(i).getID());

                            newTower(i);
                        }
                    }
                }

            }
        }
        System.out.println("--Fin Election Generique avec " + this.availableThroughput);
    }

    public void electionMax() {
        System.out.println("--Election Max : debit k =" + this.availableThroughput);

        int max = 0;
        for (int i = 0; i < this.l_towerAvailable.size(); i++) {
            if ((!this.l_towerAvailable.get(i).getClass().equals(Car.class))
                    && (!this.l_towerAvailable.get(i).getClass().equals(BlueTooth.class))) {
                if (((Tower) this.l_towerAvailable.get(i))
                        .getThroughputAvailable() > ((Tower) this.l_towerAvailable.get(max)).getThroughputAvailable()) {
                    max = i;
                    newTower(i);
                }
            }
        }

        System.out.println("--Fin Election Generique avec " + this.availableThroughput);
    }

    /*********************************************************************************/
    /*
     *
     * Patie changement de station de base
     *
     *
     */
    /**********************************************************************************/

    public void newTower(int i) {
        long timePassed = System.currentTimeMillis() - this.TimeBegin;
        if (!this.l_towerAvailable.get(i).getClass().equals(BlueTooth.class)) {

            System.out.println("CHANGEMENT TOUR vers " + (Tower) this.l_towerAvailable.get(i));

            ((Tower) this.electedTower).removeCar(this);
            this.electedTower = (Tower) this.l_towerAvailable.get(i);

            //int size = this.l_towerAvailable.get(i).getSize();

            int size = 10;
            this.availableThroughput = ((Tower) this.l_towerAvailable.get(i)).updateThroughputSize(size);
            if (this.f != null) {
                String str = timePassed + " Car " + this.getID() + " chg Tower ";

                this.f.writefos(str);
            }
        }
    }

    public boolean testHYSTERESIS(int i) {

        if (!this.l_towerAvailable.get(i).getClass().equals(BlueTooth.class)) {
            int s = ((Tower) this.l_towerAvailable.get(i)).getL_carUsed().size();
            float throughputTest = ((Tower) this.l_towerAvailable.get(i)).updateThroughputSize(s + 1);
            // System.out.println(" débit k' : " + throughputTest + " avec s=" + s + 1);
            // System.out.println(" hysteresis : " + this.HYSTERESIS);
            return throughputTest > this.HYSTERESIS;
        } else {
            // System.out.println(" bluetooth 1 " + this.l_towerAvailable.get(i));

            float throughputTest = ((BlueTooth) this.l_towerAvailable.get(i)).getBlueToothThroughput();
            // System.out.println(" hysteresis : " + this.HYSTERESIS);
            return throughputTest > this.HYSTERESIS;
        }

    }

    public boolean testCLASS(int i) {

        /*
         * System.out.println(" class k': " + this.l_towerAvailable.get(i).getClass() +
         * " comparaison avec " + this.getElectedTower().getClass() + " donne " +
         * this.l_towerAvailable.get(i).getClass().equals(this.getElectedTower().
         * getClass()));
         */
        return this.l_towerAvailable.get(i).getClass().equals(this.getElectedTower().getClass());
    }

    public boolean testETA(int i) {

        if (!this.l_towerAvailable.get(i).getClass().equals(BlueTooth.class)) {
            int s = ((Tower) this.l_towerAvailable.get(i)).getL_carUsed().size();
            float throughputTest = ((Tower) this.l_towerAvailable.get(i)).updateThroughputSize(s + 1);
            // System.out.println(" débit k' : "+throughputTest);
            float frac = throughputTest / this.availableThroughput;
            // System.out.println(" débit k'/k : " + frac + " comparaison avec " + ETA + "
            // donne " + (frac > ETA));
            return frac > ETA;
        } else// bluetooth
        {
            // System.out.println(" bluetooth 1 " + this.l_towerAvailable.get(i));

            float throughputTest = ((BlueTooth) this.l_towerAvailable.get(i)).getBlueToothThroughput();
            float frac = throughputTest / this.availableThroughput;
            // System.out.println(" débit k'/k (bt): " + frac + " comparaison avec " + ETA +
            // " donne " + (frac > ETA));
            return frac > ETA;
        }
    }

    public boolean testPARAM() {

        float rand = (float) Math.random();
        // System.out.println(" rand: " + rand + " comparaison avec " + PARAM + " donne
        // " + (rand < PARAM));

        return rand < PARAM;
    }

    public void updateThroughput(Message msg) {
        if (msg.getSender().equals(electedTower)) {
            this.availableThroughput = (float) ((Tower) msg.getSender()).getThroughputAvailable();
        }
    }

    public void OutAvailableTower() {
        System.out.println("Tours disponibles avec " + this.availableThroughput);
        for (int i = 0; i < this.l_towerAvailable.size(); i++) {
            System.out.println(l_towerAvailable.get(i));
        }

        if (this.electedTower != null) {
            System.out.println("Tour elue :" + this.electedTower.getID());
        }

    }

    public void AddAvailableTower(Node t) {

        if (!this.l_towerAvailable.contains(t)) {
            this.l_towerAvailable.add(t);
            // System.out.println("RAJOUT " + this.l_towerAvailable.size());
        }

    }
    /*
     * public boolean isEquilibrium() {
     *
     * System.out.println("+++++++++++++Bienvenue dans equilibre ");
     *
     * System.out.println(this.getID() + " a une table " + FatherTable.entrySet());
     * boolean isPere = false; float throughputFatherElectedi = -1;// throughput
     * Actuel pour la cle i
     *
     * for (int i = 0; i < 100; i++) { // avoir le throughput elu Car fatherElectedi
     * = this.FatherTable.get(i);// car elu pour la cle i isPere = false; if
     * (this.RoutingTable.containsKey(i)) { System.out.println("=====" + i +
     * "===== " + this.RoutingTable.get(i)); for (int k = 0; k <
     * this.RoutingTable.get(i).size(); k++) {
     *
     * Vector vectRoutTableik = (Vector) this.RoutingTable.get(i).get(k); float
     * idRoutTableik = (float) vectRoutTableik.get(0); int idPereik =
     * Math.round(idRoutTableik);
     *
     * isPere = (idPereik == fatherElectedi.getID()); System.out.println("pere ? " +
     * isPere); if (isPere) { throughputFatherElectedi = (float)
     * vectRoutTableik.get(1); } System.out .println("=====" + i + "===== " +
     * ((Vector<Node>) this.RoutingTable.get(i).get(k)).get(1)); }
     *
     * // comparaison vis a vis des autres debits for (int k = 0; k <
     * this.RoutingTable.get(i).size(); k++) { Vector vectRoutingTableik = (Vector)
     * this.RoutingTable.get(i).get(k); float throughputRoutingTableik = (float)
     * vectRoutingTableik.get(1); if (throughputFatherElectedi >
     * throughputRoutingTableik) { return false; } } } if (
     * this.RoutingTable.size()==0) { return false; }
     *
     *
     * } System.out.println("+++++++++++++Fin de equilibre ");
     *
     * return true; }
     */

    public boolean isEquilibrium() {

        System.out.println("+++++++++++++Bienvenue dans equilibre ");

        System.out.println(this.getID() + " a une table " + FatherTable.entrySet());
        boolean isPere = false;
        float throughputFatherElectedi = -1;// throughput Actuel pour la cle i

        for (int i = 0; i < 100; i++) {
            // avoir le throughput elu
            Car fatherElectedi = this.FatherTable.get(i);// car elu pour la cle i
            isPere = false;
            if (this.RoutingTable.containsKey(i)) {
                System.out.println("=====" + i + "===== " + this.RoutingTable.get(i));
                for (int k = 0; k < this.RoutingTable.get(i).size(); k++) {

                    Vector vectRoutTableik = (Vector) this.RoutingTable.get(i).get(k);
                    int idPereik = Math.round((float) vectRoutTableik.get(0));
                    isPere = (idPereik == fatherElectedi.getID());
                    System.out.println("pere ? " + isPere);
                    if (isPere) {
                        throughputFatherElectedi = (float) vectRoutTableik.get(1);
                    }
                    //System.out.println("=====" + i + "===== " + ((Vector<Node>) this.RoutingTable.get(i).get(k)).get(1));
                }

                // comparaison vis a vis des autres debits
            }
        }

        for (int i = 0; i < 100; i++) {

            if (this.RoutingTable.containsKey(i)) {
                for (int k = 0; k < this.RoutingTable.get(i).size(); k++) {
                    Vector vectRoutingTableik = (Vector) this.RoutingTable.get(i).get(k);

                    if (throughputFatherElectedi > (float) vectRoutingTableik.get(1)) {

                        //this.f.writefos("--Car "+this.getID()+" is not eq");
                        System.out.println("++++++EQ+Non+Atteint++++ ");
                        return false;
                    }
                }
            }
        }
        //this.f.writefos("Car "+this.getID()+" is eq");
        System.out.println("++++++EQ+++++Atteint++++ ");

        return true;
    }

    public boolean isParetoOptimal() {

        System.out.println("+++++++++++++Bienvenue dans PO ");

        System.out.println(this.getID() + " a une table " + FatherTable.entrySet());
        boolean isPere = false;
        float throughputFatherElectedi = -1;// throughput Actuel pour la cle i

        float throughputTotal = 0;
        int nbKeys = 0;
        int firstkey = -1;
        for (int i = 0; i < 100; i++) {
            // avoir le throughput elu
            Car fatherElectedi = this.FatherTable.get(i);// car elu pour la cle i
            isPere = false;

            // System.out.println("0 ");
            if (this.RoutingTable.containsKey(i)) {

                System.out.println("1 ");
                if (firstkey < 0) {
                    firstkey = i;
                }
                nbKeys++;
                System.out.println("=====" + i + "===== " + this.RoutingTable.get(i));
                System.out.println("=====" + i + "===== " + this.RoutingTable.get(i).size());
                for (int k = 0; k < this.RoutingTable.get(i).size(); k++) {

                    Vector vectRoutTableik = (Vector) this.RoutingTable.get(i).get(k);
                    int idPereik = Math.round((float) vectRoutTableik.get(0));

                    if (idPereik == fatherElectedi.getID()) {
                        throughputFatherElectedi = (float) vectRoutTableik.get(1);
                    }
                    System.out
                            .println("=====" + i + "===== " + ((Vector<Node>) this.RoutingTable.get(i).get(k)).get(1));
                }

                throughputTotal += throughputFatherElectedi;
            }
        }
        System.out.println("2 " + this.RoutingTable.keySet());

        if (firstkey > -1) {
            int nbNeighbors = this.RoutingTable.get(firstkey).size();
            System.out.println("3 " + nbNeighbors);
            Tower tabTowerNeighbors[] = new Tower[nbNeighbors];
            int tabnbTowerUsedperNeighbors[] = new int[nbNeighbors];
            float tabthroughputTowerperNeighbors[] = new float[nbNeighbors];

            // tabtower regroupe les tours elues par les voisins
            // on teste les nbkeys cas de figure (max = 1 )
            // max suivant =2 etc...
            // on compare avec la valeur totale obtenue
            int tabint = 0;
            for (int k = 0; k < this.RoutingTable.get(firstkey).size(); k++) {
                System.out.println("vector" + this.RoutingTable.get(firstkey).get(k));
                Vector vectRoutTableik = (Vector) this.RoutingTable.get(firstkey).get(k);
                float idRoutTableik = (float) vectRoutTableik.get(0);
                int identik = Math.round(idRoutTableik);
                for (int j = 0; j < this.getInNeighbors().size(); j++) {
                    if (this.getInNeighbors().get(j).getID() == identik) {
                        Tower t = (Tower) ((Car) this.getInNeighbors().get(j)).getElectedTower();
                        tabTowerNeighbors[tabint] = t;
                        tabnbTowerUsedperNeighbors[tabint] = t.getL_carUsed().size();
                        tabthroughputTowerperNeighbors[tabint] = t.throughputAvailable;
                        tabint++;
                    }
                }
            }

            for (int i = 0; i < 100; i++) {
                if (FatherTable.containsKey(i)) {
                    System.out.println(FatherTable.get(i).getElectedTower());
                    for (int k = 0; k < nbNeighbors; k++) {
                        if (tabTowerNeighbors[k].equals(FatherTable.get(i).getElectedTower())) {
                            tabnbTowerUsedperNeighbors[k]--;
                        }
                    }
                }
            }
            for (int k = 0; k < nbNeighbors; k++) {
                tabthroughputTowerperNeighbors[k] = tabTowerNeighbors[k]
                        .updateThroughputSize(tabnbTowerUsedperNeighbors[k]);
            }
            float throuMax = 0;
            for (int i = 0; i < nbKeys; i++) {
                int m = maxThroughput(tabthroughputTowerperNeighbors);
                throuMax += tabthroughputTowerperNeighbors[m];
                tabnbTowerUsedperNeighbors[m]--;
                tabthroughputTowerperNeighbors[m] = tabTowerNeighbors[m]
                        .updateThroughputSize(tabnbTowerUsedperNeighbors[m]);

            }

            System.out.println("+++++++++++++Fin de PO++++++ " + (throughputTotal > throuMax) + "(" + throughputTotal
                    + " > " + throuMax + ")");
            return throughputTotal > throuMax;
        }



        return true;

    }

    public int maxThroughput(float tab[]) {
        int max = 0;
        for (int i = 0; i < tab.length; i++) {
            if (tab[max] < tab[i]) {
                max = i;
            }
        }
        return max;

    }

    public HashMap<Integer, Vector> getRoutingTable() {
        return RoutingTable;
    }

    public void setRoutingTable(HashMap<Integer, Vector> routingTable) {
        RoutingTable = routingTable;
    }

    public HashMap<Integer, Car> getFatherTable() {
        return FatherTable;
    }

    public void setFatherTable(HashMap<Integer, Car> fatherTable) {
        FatherTable = fatherTable;
    }

}