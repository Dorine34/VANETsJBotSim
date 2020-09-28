package com.company;

import io.jbotsim.core.Node;

import java.util.ArrayList;
import java.util.Vector;


public class HighwayCar extends Node {
    double speed = 0; // Random speed between 3 and 5
    public long tempsDebut;


    /*********************************
     * Algo de Double Selection
     *
     **********************************/
    ArrayList<HighwayTower> l_tourDispo = new ArrayList<HighwayTower>();
    HighwayTower tourElue;
    float debitDispo;
    int indMaxDeb;
    boolean debut = true;
    int nbPassage;
    int DELTA = 500;

    Integer source = -1;
    Vector<Prob> vectProb = new Vector<Prob>();
    Vector<Prob> vectElus = new Vector<Prob>();



    /*********************************
     * AODV
     *
     **********************************/
    Vector<Aodv> vectAodv = new Vector<Aodv>();


    /*********************************
     * Programmation Dynamique
     *
     **********************************/


    public Vector<Aodv> getVectAodv() {
        return vectAodv;
    }

    public void setVectAodv(Vector<Aodv> vectAodv) {
        this.vectAodv = vectAodv;
    }

    /******************
     * generateurs
     */
    public HighwayCar() {
        setDirection(0); // Eastward
        setIcon("/com/company/car.png");
        //setSize(10);
        setSensingRange(100);
    }

    public HighwayCar(int speed) {
        this.speed = speed;
        setDirection(0); // Eastward
        setIcon("/com/company/car.png");
        //setSize(10);
        setSensingRange(100);
        this.setLocation(Math.random() * 590, Math.random() * 390);
    }

    /*****************************
     * getteurs et setteurs
     *
     * @return
     */
    public int getNbPassage() {
        return nbPassage;
    }

    public void setNbPassage(int nbPassage) {
        this.nbPassage = nbPassage;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public float getDebitDispo() {
        return debitDispo;
    }

    public void setDebitDispo(float debitDispo) {
        this.debitDispo = debitDispo;
    }

    public HighwayTower getTourElue() {
        return tourElue;
    }

    public void setTourElue(HighwayTower tourElue) {
        this.tourElue = tourElue;
    }

    public void outTourDispo() {
        /*
         * System.out.println("bienvenue dans output a temps"+
         * System.currentTimeMillis() + ", dispo :" + this.debitDispo+
         * " nb passage ="+this.nbPassage);
         *
         * for (int i = 0; i < l_tourDispo.size(); i++) { System.out.println("  -" +
         * l_tourDispo.get(i).toString()); }
         *
         * System.out.println(" car num" + this.getID() + " a comme vectProb : ");
         *
         * for (int i = 0; i < vectProb.size(); i++) { System.out.println("  -" +
         * vectProb.get(i).toString()); }
         */
        System.out.println(" car num " + this.getID() + " a comme vectElus : ");
        for (int i = 0; i < vectElus.size(); i++) {
            System.out.println("  -" + vectElus.get(i).toString());
        }

        outVectTpp();
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    /********************************
     * TEMPS
     *****************************/

    /* onStart() liste les tours proximité */

    public void onStart() {
        // this.speed=0.5;
        this.tempsDebut = System.currentTimeMillis();


        // System.out.println("temps debut ="+tempsDebut);
        for (Node node : getSensedNodes()) {
            if (node instanceof HighwayTower) {
                addTourDispo((HighwayTower) node);
            }
        }

        if (this.source!=-1)
        {
            vectAodv.add(new Aodv( this.source, 0, null, System.currentTimeMillis()));
        }

    }

    /************************
     * onPreClock() si car senti : -si abs c, abs d rajout dans vectProb+vectElus
     * -si abs c, et d rajout dans vectProb
     *
     */
    public void onPreClock() { // LOOK

        // System.out.println("Bienvenue dans onPreClock");
        for (Node node : getSensedNodes()) {
            if (node instanceof HighwayTower) {
                // System.out.println("car " + this.getID() + " rencontre tour num " +
                // node.getID());
                addTourDispo((HighwayTower) node);
            }
            if ((node instanceof HighwayCar)) {
                // System.out.println("car " + this.getID() + " rencontre car num " +
                // node.getID());
                if (((HighwayCar) node).getSource() > -1) {
                    Prob p = new Prob(((HighwayCar) node).getSource(), ((HighwayCar) node), 1, 1,
                            System.currentTimeMillis());

                    // si c et d sont enregistres, pas de rajout
                    if (contientVectProb(((HighwayCar) node).getSource(), (HighwayCar) node) == 2) {

                    }
                    // si c n'est pas enregistree mais d enregistree, rajout dans le vecteur
                    // vectprob
                    if (contientVectProb(((HighwayCar) node).getSource(), (HighwayCar) node) == 1) {
                        vectProb.add(p);
                    }
                    // si d n'est pas enregistree, rajout dans les deux vecteurs
                    if (contientVectProb(((HighwayCar) node).getSource(), (HighwayCar) node) == 0) {
                        vectProb.add(p);
                        vectElus.add(p);
                    }
                } else {
                    for (int i = 0; i < ((HighwayCar) node).vectElus.size(); i++) {
                        System.out.println(((HighwayCar) node).vectElus.get(i).toString());
                        if (contientVectProb(((HighwayCar) node).vectElus.get(i).getDemande(),
                                ((HighwayCar) node).vectElus.get(i).getCar()) == 1) {
                            vectProb.add(((HighwayCar) node).vectElus.get(i));
                        }
                        if (contientVectProb(((HighwayCar) node).vectElus.get(i).getDemande(),
                                ((HighwayCar) node).vectElus.get(i).getCar()) == 0) {
                            vectProb.add(((HighwayCar) node).vectElus.get(i));
                            vectElus.add(((HighwayCar) node).vectElus.get(i));
                        }
                    }
                }
            }
        }
        elaguage();

        //partie aodv
        for (Node node : getSensedNodes()) {
            boolean sourcePresence=false;
            if (node instanceof HighwayCar) {
                for (int i=0; i<((HighwayCar) node).getVectAodv().size();i++)
                {
                    sourcePresence=false;
                    for (int j=0; j<vectAodv.size();j++)
                    {
                        if (vectAodv.get(j).getSource()==((HighwayCar) node).getVectAodv().get(i).getSource())
                        {
                            sourcePresence=true;
                            if(vectAodv.get(j).getNbSauts()>((HighwayCar) node).getVectAodv().get(i).getNbSauts()+1 )
                            {
                                vectAodv.get(j).setNbSauts(((HighwayCar) node).getVectAodv().get(i).getNbSauts()+1) ;
                                vectAodv.get(j).setCarVoisin( (HighwayCar) node);
                            }
                        }
                    }
                    if (!sourcePresence)
                    {
                        vectAodv.add(new Aodv(((HighwayCar) node).getVectAodv().get(i).getSource(), 1, (HighwayCar) node, System.currentTimeMillis()));
                    }
                }
            }
        }

    }

    public int containSource(int source)
    {
        for (int i=0; i<vectAodv.size();i++)
        {
            if (vectAodv.get(i).getSource()==source)
            {
                return i;
            }
        }
        return -1;
    }

    /***************
     * utilisé dans onClock
     *
     * @param d demande
     * @param c car
     * @return 0, 1(contient d ), 2 (contient c,d)
     */
    public void onClock() {

        /*
         * move(1); wrapLocation();
         */

        // System.out.println("Bienvenue dans onClock de "+this.getID()+" avec horaire
        // "+this.tempsDebut +2000);
        /*
         * for(int i=0; i<this.getMailbox().size(); i++) {
         * System.out.println(this.getMailbox().get(i).toString());
         *
         * }
         */
        // System.out.println("temps actuel : "+ System.currentTimeMillis()/1000);

        move(speed);
        wrapLocation();
        testTechnoComm();
        if (debut) {
            this.debut = false;

            if (maxDebitfrac()) {
                tourElue = l_tourDispo.get(indMaxDeb);
                tourElue.addCar(this);
            }
        }
        for (int i = 0; i < vectElus.size(); i++) {
            selection(vectElus.get(i).getDemande());
        }

        elaguage();
        outTourDispo();
        /*
         * System.out.println("RESTE : "); for (int i = 0; i < vectElus.size(); i++) {
         * System.out.println(vectElus.get(i).toString()); }
         */
    }


    public int contientVectProb(Integer d, HighwayCar c) {

        int res = 0;
        for (int i = 0; i < vectElus.size(); i++) {
            if (vectElus.get(i).getDemande() == c.getSource()) {
                res = 1;
                if (vectElus.get(i).getCar().equals(c)) {
                    return 2;
                }
            }
            vectElus.get(i).toString();
        }
        return res;
    }
    /**
     * renvoie indice de la tour de debit max
     ******************************************/

    public boolean optimum() {
        double opti = 0;
        for (int i = 0; i < l_tourDispo.size(); i++) {
            if (l_tourDispo.get(i).getDebitTotal() > opti) {
                opti = l_tourDispo.get(i).getDebitTotal();
            }
        }

        if (opti == this.getDebitDispo()) {
            return true;
        }
        return false;
    }

    public void elaguage() {
        // System.out.println(this.vectProb.size() +"test avec
        // "+System.currentTimeMillis());

        for (int i = 0; i < this.vectProb.size(); i++) {
            if (System.currentTimeMillis() - vectProb.get(i).getTempsReception() > DELTA) {
                if (vectProb.get(i).getCar().getTourElue() != null) {
                    this.vectProb.get(i).getCar().getTourElue().extract(this);
                    vectProb.remove(i);
                } else {
                    vectProb.remove(i);
                }
            }
        }
        for (int i = 0; i < this.vectElus.size(); i++) {
            if (System.currentTimeMillis() - vectElus.get(i).getTempsReception() > DELTA) {
                // System.out.println("chgmt avec "+vectElus.get(i).getTempsReception());
                vectElus.remove(i);
            }
        }
        for (int i = 0; i < this.vectAodv.size(); i++) {
            if (System.currentTimeMillis() - vectAodv.get(i).getTemps() > DELTA) {
                // System.out.println("chgmt avec "+vectElus.get(i).getTempsReception());
                vectAodv.remove(i);
            }
        }
    }

    public void testTechnoComm() {
        boolean chgmt = maxDebitfrac();
        float rand = (float) Math.random();
        if (chgmt && (l_tourDispo.size() > 0)) {
            if (tourElue == null) {
                this.setTourElue(l_tourDispo.get(0));
            }
            if (l_tourDispo.get(indMaxDeb).getClasse() == tourElue.getClasse()) {
                if (rand < Math.pow(HighwayTower.PARAMRANDOM, this.nbPassage + 1)) {
                    chgmtTechnoComm();
                }
            } else {
                if ((l_tourDispo.get(indMaxDeb).getDebitDisponible() > HighwayTower.HYSTERESIS)
                        && (rand < Math.pow(HighwayTower.PARAMRANDOM, this.nbPassage + 1))) {
                    chgmtTechnoComm();
                    HighwayTower.HYSTERESIS++;
                }
            }
        }
    }

    public void chgmtTechnoComm() {
        long tempsChgmt = System.currentTimeMillis();
/*
		if ((tourElue != null) &&
				(l_tourDispo.get(indMaxDeb).compareTo(tourElue) != 0)&&
				(System.currentTimeMillis()-this.tempsDernierMessage>DELTA)
				) {

		}*/
        //System.out.println("*****CHGMT "+this.tempsDernierMessage+"/"+System.currentTimeMillis());
        if ((tourElue != null) &&
                (l_tourDispo.get(indMaxDeb).compareTo(tourElue) != 0)
        ) {
            tourElue.extract(this);
            nbPassage++;

            HelloWorld.CHGMT++;
            TempsParPassage tpp = new TempsParPassage(tempsChgmt - tempsDebut, HelloWorld.CHGMT,
                    HelloWorld.debitTotal(), optimum());
            HelloWorld.vectPassageTemps.add(tpp);
            /*
             * System.out.println("***************************changement = "+HelloWorld.
             * CHGMT); System.out.println(); System.out.println();
             */

        }
        tourElue = l_tourDispo.get(indMaxDeb);

        if (!tourElue.l_utilisateurs.contains(this)) {
            tourElue.addCar(this);
        }
    }

    public int nbOpti() {
        int total = 0;
        for (int i = 0; i < HelloWorld.vectPassageTemps.size(); i++) {
            if (HelloWorld.vectPassageTemps.get(i).isOptiPareto()) {
                total++;
            }
        }
        return total;
    }

    public void outVectTpp() {

        System.out.println("Nombre total de changement = " + HelloWorld.CHGMT + "(dont optimum de Pareto " + nbOpti()
                + ") avec débit de " + HelloWorld.debitTotal());

        // System.out.println("nombre de changement =");
        String strg = "";
        for (int i = 0; i < HelloWorld.vectPassageTemps.size(); i++) {
            strg = strg + HelloWorld.vectPassageTemps.get(i).toString();
        }
        System.out.println(strg);
        System.out.println();

    }

    public int maxDebit() {
        int indiceDebMax = 0;

        for (int i = 0; i < l_tourDispo.size(); i++) {
            if (l_tourDispo.get(indiceDebMax).getDebitDisponible() < l_tourDispo.get(i).getDebitDisponible())
                indiceDebMax = i;
        }
        return indiceDebMax;
    }

    public boolean maxDebitfrac() {

        // System.out.println("debut max, debit disponible = " + this.debitDispo);
        indMaxDeb = 0;
        boolean chgmt = false;
        for (int i = 0; i < l_tourDispo.size(); i++) {
            /*
             * System.out.println("test avec  = " + this.l_tourDispo.get(i) + " cad " +
             * l_tourDispo.get(i).getDebitDisponible() + " / " +
             * l_tourDispo.get(indMaxDeb).getDebitDisponible() + "(" +
             * l_tourDispo.get(i).getDebitDisponible() /
             * l_tourDispo.get(indMaxDeb).getDebitDisponible() + ") >" +
             * HighwayTower.SEUILCHGMT);
             */
            if (l_tourDispo.get(i).getDebitDisponible()
                    / l_tourDispo.get(indMaxDeb).getDebitDisponible() > HighwayTower.SEUILCHGMT) {
                indMaxDeb = i;
                chgmt = true;
            }
        }
        if (chgmt) {
            this.debitDispo = (float) l_tourDispo.get(indMaxDeb).getDebitDisponible();

        }
        // System.out.println("fin max, debit disponible = " + this.debitDispo);
        return chgmt;
    }

    /* selection du voisin selon demande q */
    public void selection(Integer d) {
        // System.out.println("Bienvenue dans selection");
        if (this.debitDispo > 0) {
            double randQTotal = 0;
            for (int i = 0; i < vectProb.size(); i++) {
                if (vectProb.get(i).getDemande() == d) {
                    randQTotal += vectProb.get(i).getQ();
                }
            }
            double randQ = Math.random() * randQTotal;

            for (int i = 0; i < vectProb.size(); i++) {
                if (vectProb.get(i).getDemande() == d) {
                    randQ -= vectProb.get(i).getQ();
                    if (randQ < 0) {
                        extractCar(d);
                        calculY(d, vectProb.get(i).getCar());
                        vectElus.add(vectProb.get(i));
                        tourElue.addCar(vectProb.get(i).getCar());
                        return;
                    }
                }
            }
            updateQ(d);
        }

    }

    public double SumExp(Integer d) {
        double res = 0;
        for (int i = 0; i < vectProb.size(); i++) {
            if (vectProb.get(i).getDemande() == d) {
                res += Math.exp(vectProb.get(i).getY());
            }
        }
        return res;
    }

    public void updateQ(Integer d) {
        double sum = SumExp(d);
        double q = 0;
        for (int i = 0; i < vectProb.size(); i++) {
            if (vectProb.get(i).getDemande() == d) {
                q = vectProb.get(i).getQ();
                vectProb.get(i).setQ(q / sum);
            }
        }
    }

    public void extractCar(Integer d) {
        for (int i = 0; i < vectElus.size(); i++) {
            if (vectElus.get(i).getDemande() == d) {
                vectElus.remove(i);
            }
        }
    }

    public void calculY(Integer d, HighwayCar c) {
        // System.out.println("Dans calcul y, le debit disponible est " +
        // this.debitDispo);
        double resY = 0;
        if (rechercheProb(d, c) != -1) {
            resY = HighwayTower.GAMMA
                    * (c.getDebitDispo() - HighwayTower.TAU * vectProb.get(rechercheProb(d, c)).getY())
                    / vectProb.get(rechercheProb(d, c)).getQ();
            /*
             * System.out.println("Le y stade 1 est " + HighwayTower.GAMMA + " * (" +
             * c.getDebitDispo() + " - " + HighwayTower.TAU + "*" +
             * vectProb.get(rechercheProb(d, c)).getY() + " )/ " +
             * vectProb.get(rechercheProb(d, c)).getQ() + " = " + resY);
             */
            resY += vectProb.get(rechercheProb(d, c)).getY();
            /*
             * System.out.println( "Le y stade 2 avec rajout de  " +
             * vectProb.get(rechercheProb(d, c)).getY() + " = " + resY);
             * vectProb.get(rechercheProb(d, c)).setY(Math.min(resY, this.debitDispo));
             *
             * System.out.println(("nouveau Y de " + c.getID() + " avec demande " + d +
             * " = " + vectProb.get(rechercheProb(d, c)).getY()));
             */
        }
        // System.out.println("---Le y est " + resY);
    }

    public int rechercheProb(Integer d, HighwayCar c) {
        for (int i = 0; i < vectProb.size(); i++) {
            if ((vectProb.get(i).getDemande() == d) && (vectProb.get(i).getCar().compareTo(c) == 0)) {
                return i;
            }
        }
        return -1;
    }

    public void addTourDispo(HighwayTower tower) {
        if (!l_tourDispo.contains(tower)) {
            // System.out.println("ajout fait");
            this.l_tourDispo.add(tower);
        }
    }

}