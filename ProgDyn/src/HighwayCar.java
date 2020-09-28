


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



    /*********************************
     * AODV
     *
     **********************************/
    Vector<ProgDyn> vectProgDyn = new Vector<ProgDyn>();
    public long dernierTempsPassage;

    /*********************************
     * Programmation Dynamique
     *
     **********************************/


    public Vector<ProgDyn> getVectProgDyn() {
        return vectProgDyn;
    }

    public void setVectProgDyn(Vector<ProgDyn> vectProgDyn) {
        this.vectProgDyn = vectProgDyn;
    }

    /******************
     * generateurs
     */
    public HighwayCar() {
        if (source>-1)
        {
            //this.setIntColor(source);
        }
        setDirection(0); // Eastward
        setIcon("/car.png");
        //setSize(10);
        setSensingRange(100);
    }

    public HighwayCar(int speed) {
        if (source>-1)
        {
            //this.setIntColor(source);
        }
        this.speed = speed;
        setDirection(0); // Eastward
        setIcon("/car.png");
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
        this.dernierTempsPassage= System.currentTimeMillis();
        // System.out.println("temps debut ="+tempsDebut);
        for (Node node : getSensedNodes()) {
            if (node instanceof HighwayTower) {
                addTourDispo((HighwayTower) node);
            }
        }

        if (this.source!=-1)
        {
            vectProgDyn.add(new ProgDyn( this.source, 0, null, System.currentTimeMillis()));
        }
        TempsParPassage tpp=new TempsParPassage(this.getID(),(long)0.0,0, 0.0,false);
        if (containstpp()==-1)
        {
            MainProgDyn.vectPassageTemps.add(tpp);
        }

    }
    public int containstpp() {
        for (int i=0; i<MainProgDyn.vectPassageTemps.size();i++)
        {
            if(MainProgDyn.vectPassageTemps.get(i).getId()==this.getID())
            {
                return i;
            }
        }
        return -1;

    }


    /************************
     * onPreClock() si car senti : -si abs c, abs d rajout dans vectProb+vectElus
     * -si abs c, et d rajout dans vectProb
     *
     */
    public void onPreClock() { // LOOK

        choixTour();
        System.out.println("**car num ="+this.getID()+" de debit="+this.getDebitDispo());

        System.out.println("tour elue = "+this.tourElue);
        //partie aodv
        if (this.debitDispo!=0)
        {
            for (Node node : getSensedNodes()) {

                boolean sourcePresence=false;
                if (node instanceof HighwayCar) {
                    for (int i=0; i<((HighwayCar) node).getVectProgDyn().size();i++)
                    {
                        sourcePresence=false;
                        for (int j=0; j<vectProgDyn.size();j++)
                        {
                            if (vectProgDyn.get(j).getSource()==((HighwayCar) node).getVectProgDyn().get(i).getSource())
                            {
                                sourcePresence=true;
                                if(vectProgDyn.get(j).getNbSauts()>((HighwayCar) node).getVectProgDyn().get(i).getNbSauts()+1 )
                                {
                                    vectProgDyn.get(j).setNbSauts(((HighwayCar) node).getVectProgDyn().get(i).getNbSauts()+1) ;
                                    vectProgDyn.get(j).setCarVoisin( (HighwayCar) node);
                                    vectProgDyn.get(j).setTourElue(((HighwayCar) node).getTourElue());
                                    if (((HighwayCar) node).getVectProgDyn().get(i).getDebit()==0)
                                    {
                                        vectProgDyn.get(j).setDebit(this.debitDispo);
                                    }
                                    else
                                        vectProgDyn.get(j).setDebit(Math.min(this.debitDispo, ((HighwayCar) node).getVectProgDyn().get(i).getDebit()));
                                    //vectAodv.get(j).setDebit(10000);

                                }
                            }
                        }
                        if (!sourcePresence)
                        {
                            long debitTour=(long) ((HighwayCar) node).getVectProgDyn().get(i).getDebit();
                            if (debitTour==0)
                            {
                                debitTour=(long) this.debitDispo;
                            }
                            vectProgDyn.add(new ProgDyn(
                                    ((HighwayCar) node).getVectProgDyn().get(i).getSource(),
                                    ((HighwayCar) node).getVectProgDyn().get(i).getNbSauts()+1,
                                    (HighwayCar) node,
                                    this.tourElue,
                                    Math.min(this.debitDispo,debitTour),
                                    System.currentTimeMillis()));
                        }
                    }
                }
                if (node instanceof HighwayTower) {
                    System.out.println(node.toString());
                    if (!l_tourDispo.contains((HighwayTower) node))
                    {
                        l_tourDispo.add((HighwayTower) node);
                        //System.out.println("RAJOUT "+l_tourDispo.size());
                    }
                }
            }
        }

        //elaguage();
        System.out.println("nb de changements = "+this.nbPassage+" avec "+toString());
    }


    public void choixTour() {
        System.out.println("---car num"+this.getID()+"----bienvenue dans choixtour"+ l_tourDispo.size());
        double debitj=0;
        for (int i = 0; i < l_tourDispo.size(); i++) {
            if (tourElue==null)
            {
                this.setTourElue(l_tourDispo.get(i));
                this.tourElue.addCar(this);
                this.debitDispo = (float) tourElue.getDebitDisponible();

                MainProgDyn.vectPassageTemps.get(containstpp()).setNbPassage(this.nbPassage);
                MainProgDyn.vectPassageTemps.get(containstpp()).setDebit(this.debitDispo);
                MainProgDyn.vectPassageTemps.get(containstpp()).setOptiPareto(isParetoOptimum());
                MainProgDyn.vectPassageTemps.get(containstpp()).setTemps(System.currentTimeMillis()-this.tempsDebut);
            }
            if ((tourElue!=null) &&

                    (System.currentTimeMillis()-this.dernierTempsPassage>DELTA)
                    &&(l_tourDispo.get(i).getID()!= tourElue.getID())
            ) {
                //System.out.println("**fin max, debit disponible = " + this.debitDispo);
                this.tourElue.extract(this);
                setTourElue( l_tourDispo.get(i));
                this.tourElue.addCar(this);
                this.debitDispo = (float) tourElue.getDebitDisponible();

                this.dernierTempsPassage=System.currentTimeMillis();
                System.out.println("-CHGMT "+tourElue.getID()+ "vers"+l_tourDispo.get(i).getID());
                this.nbPassage++;
                MainProgDyn.vectPassageTemps.get(containstpp()).setNbPassage(this.nbPassage);
                MainProgDyn.vectPassageTemps.get(containstpp()).setDebit(this.debitDispo);
                MainProgDyn.vectPassageTemps.get(containstpp()).setOptiPareto(isParetoOptimum());
                MainProgDyn.vectPassageTemps.get(containstpp()).setTemps(System.currentTimeMillis()-this.tempsDebut);

            }
        }
        if(this.tourElue==null)
        {
            System.out.println("--- car num"+this.getID()+"n'a pas choisi de tour");
        }
        else
        {
            System.out.println("--- car num"+this.getID()+"a choisi"+ this.tourElue.getID()+" debit disponible = " + this.debitDispo);
        }
    }

    public int containSource(int source)
    {
        for (int i=0; i<vectProgDyn.size();i++)
        {
            if (vectProgDyn.get(i).getSource()==source)
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
		  move(1); wrapLocation();
		 */
        move(speed);
        wrapLocation();
        System.out.println("Num "+this.getID()+" a pour debit "+this.debitDispo);
        System.out.println("car num"+this.getID()+ " : "+MainProgDyn.toStringvpt());
        System.out.println("debit total ="+MainProgDyn.debitTotal()+ " et nbOptimumPareto = "+nbOptiPareto());

        //System.out.println(this.toString());
    }
    public double nbDebitTotal()
    {
        double deb=0;
        for (int i=0; i<MainProgDyn.vectPassageTemps.size();i++)
        {
            deb+=MainProgDyn.vectPassageTemps.get(i).getDebit();
        }
        return deb;
    }
    public int nbOptiPareto()
    {
        int tot=0;
        for (int i=0; i<MainProgDyn.vectPassageTemps.size();i++)
        {
            if(MainProgDyn.vectPassageTemps.get(i).isOptiPareto())
            {
                tot++;
            }
        }
        return tot;
    }

    public boolean isParetoOptimum()
    {
        double debitMax=0;
        for (int i=0; i<this.l_tourDispo.size(); i++)
        {
            if (l_tourDispo.get(i).debitTotal>debitMax)
            {
                debitMax=l_tourDispo.get(i).getDebitTotal();
            }
        }
        return (this.debitDispo==debitMax);
    }

    @Override
    public String toString() {

        String str="";
        for (int i=0; i<this.vectProgDyn.size(); i++)
        {
            str=str+this.vectProgDyn.get(i).toString();
        }
        return "car num"+ this.getID() +" [" + str+ "]";
    }

    public void elaguage() {
        for (int i = 0; i < this.vectProgDyn.size(); i++) {
            if (System.currentTimeMillis() - vectProgDyn.get(i).getTemps() > DELTA) {
                // System.out.println("chgmt avec "+vectElus.get(i).getTempsReception());
                vectProgDyn.remove(i);
            }
        }
    }

    public void addTourDispo(HighwayTower tower) {
        if (!l_tourDispo.contains(tower)) {
            // System.out.println("ajout fait");
            this.l_tourDispo.add(tower);
        }
    }

}