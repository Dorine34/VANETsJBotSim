/************************************************************************************/
/*Dans ce code : 																    */
/*	Les tours ont la liste des voitures qui peuvent recevoir des informations	    */
/*	Les voitures ont : - la liste des tours candidates								*/
/*		-une seule tour, unique possibilité pour emettre							*/
/* 																					*/
/* 	A ameliorer : rapport par rapport à la distance, a la vitesse, buffer à agrandir
 *
 * 	Possibilité de générer aléatoirement la position des véhicules
 * 	Possibilité de charger la position des véhicules grâce à Input.txt
 *
 * 	Sortie output.txt : temps des changements
 * 	Sortie total.txt : temps des changements classés																				*/
/************************************************************************************/

import io.jbotsim.core.Color;
import io.jbotsim.core.Link;
import io.jbotsim.core.Topology;
import io.jbotsim.ui.JViewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HelloWorld {

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {

        FileManager f = new FileManager();

        f.creationVector();

        //Topology tp = new Topology();
        FileInputStream fOutput = null;
        String str = null;

        String tab[] = new String[6];
        int tabnum = 0;


        Topology tp = new Topology();

/************Fichier à partir de Input*******************************/

        for (int i = 0; i < f.vectNodeSize(); i++) {
            String sourceStr = f.rechVector(i, 1);
            int sourceInt = 0;
            sourceInt = strToInt(sourceStr);

            String throuhgputStr = f.rechVector(i, 2);
            int throuhgputInt = strToInt(throuhgputStr);

            String locXStr = f.rechVector(i, 3);
            int locXint = strToInt(locXStr);

            String locYStr = f.rechVector(i, 4);
            int locYint = strToInt(locYStr);

            String rangeStr = f.rechVector(i, 5);
            int rangeInt = strToInt(rangeStr);


            if (f.rechVector(i, 0).contains("BlueTooth")) {
                tp.addNode(new BlueTooth(sourceInt, throuhgputInt, locXint, locYint, f));
            }
            if (f.rechVector(i, 0).contains("WiFi")) {
                tp.addNode(new WiFi(throuhgputInt, throuhgputInt, locXint, locYint));
            }
            if (f.rechVector(i, 0).contains("Cellular")) {
                tp.addNode(new Cellular(throuhgputInt, throuhgputInt, locXint, locYint));
            }
            if (f.rechVector(i, 0).contains("Car")) {
                tp.addNode(new Car(sourceInt, locXint, locYint, f));
            }

        }

/************Fichier implémenté à partir de Random*******************************/
        int testNB = 10;//nombre de test à réaliser

        Car tableauCar[] = new Car[10];

        while (testNB > 0) {
            f.writefInput("", false);
            f.writefos("Data " + testNB);
            //f.writefos("TEMPS "+System.currentTimeMillis()+" NUM "+testNB);
            System.out.println("TEMPS" + tp.getTime());

            //tp.getRefreshMode();
            tp.clear();

            //Car source
            Car c0 = new Car(1, f);
            tp.addNode(c0);

            Car c1 = new Car(2, f);
            tp.addNode(c1);

            //10 nombre de voitures transitrices
            for (int i = 0; i < 10; i++) {
                tableauCar[i] = new Car(-1, f);
                tp.addNode(tableauCar[i]);
            }

            //4 technologies de communication

            WiFi t1 = new WiFi(3, 100, f);
            tp.addNode(310, 200, t1);

            WiFi t0 = new WiFi(5, 100, f);
            tp.addNode(210, 140, t0);

            Cellular t2 = new Cellular(7, 100, f);
            tp.addNode(310, 80, t2);

            Cellular t3 = new Cellular(10, 100, f);
            tp.addNode(210, 80, t3);


            new JViewer(tp);
            tp.start();
            Thread.sleep(5000);//durée d'un test
            f.writefos("-Car " + c0.getID() + " eq :" + c0.isEquilibrium());
            f.writefos("-Car " + c1.getID() + " eq :" + c1.isEquilibrium());
            for (int i = 0; i < 10; i++) {
                f.writefos("-Car " + tableauCar[i].getID() + " eq :" + tableauCar[i].isEquilibrium());

            }
            f.writefTot("-Car PO :" + c0.isParetoOptimal());
            testNB--;

            tp.clear();

        }

        tp.clear();


/************Résultat sortie *******************************/

        try {
            // On instancie nos objets :
            // fis va lire le fichier
            fOutput = new FileInputStream(new File("src/Output.txt"));

            f.count(fOutput);
        } finally {
            // On ferme nos flux de données dans un bloc finally pour s'assurer
            // que ces instructions seront exécutées dans tous les cas même si
            // une exception est levée !
            try {
                if (fOutput != null)
                    fOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public static int strToInt(String sourceStr) {
        int sourceInt = 0;
        for (int i = 0; i < sourceStr.length(); i++) {
            if (((int) sourceStr.charAt(i) >= 48) && ((int) sourceStr.charAt(i) <= 57)) {

                sourceInt = sourceInt * 10 + sourceStr.charAt(i) - 48;
            }
        }
        return sourceInt;
    }

    public static Link addLinks(Car c0, Car b1) {
        System.out.println(c0.FatherTable.keySet());

        Link l = new Link(c0, b1);
        l.setColor(Color.yellow);
        l.setWidth(10);
        return l;
    }

}