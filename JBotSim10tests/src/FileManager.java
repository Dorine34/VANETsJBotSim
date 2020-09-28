import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class FileManager {
    /************************************************************************/
    Vector vectNode = new Vector();// comprend les données relatives aux objets utilisés
    int size = 0;

    public int vectNodeSize() {
        return size;
    }

    // dans input.txt : tri les objets et les places dans un tableau
    public void creationVector() {

        File f = new File("src/Input.txt");
        FileInputStream fis = null;
        String str = null;

        String tab[] = new String[6];
        int tabnum = 0;

        try {
            fis = new FileInputStream(new File("src/Input.txt"));

            byte[] buf = new byte[8];
            int n = 0;
            while ((n = fis.read(buf)) >= 0) {
                for (byte bit : buf) {
                    if (bit == 10) {
                        str = "";
                    }
                    if (bit == 13) {
                        if (str != null) {
                            int l = str.length();
                            tab[tabnum] = str.substring(1, l);
                        }
                        str = "";
                        if (tabnum == 5) {
                            tabnum = 0;
                            for (int i = 0; i < 6; i++) {

                                System.out.print(tab[i] + " ");
                            }
                            vectNode.add(tab);
                            size++;
                        }
                    }
                    if (bit == 58) {
                        str = "";
                    }

                    if (contain(str) >= 0) {
                        tabnum = contain(str);
                    }
                    str += (char) bit;
                }
                buf = new byte[8];// reinitialization
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    // dans input.txt : permet d'accéder directement aux données d'un objet
    public String rechVector(int numb, int pos) {

        File f = new File("src/Input.txt");
        FileInputStream fis = null;
        String str = null;

        String tab[] = new String[6];
        int tabnum = 0;

        try {
            fis = new FileInputStream(new File("src/Input.txt"));
            byte[] buf = new byte[8];
            int n = 0;
            String stri;
            while ((n = fis.read(buf)) >= 0) {
                for (byte bit : buf) {
                    if (bit == 10) {

                        // str = "";
                    }
                    if (bit == 13) {
                        if (str != null) {
                            int l = str.length();
                            tab[tabnum] = str.substring(1, l);

                        }
                        str = "";
                        if (tabnum == 5) {
                            // System.out.println();
                            // System.out.println("Tableau=");
                            tabnum = 0;
                            for (int i = 0; i < 6; i++) {
                                // System.out.print(tab[i] + " ");

                            }
                            if (numb == 0) {
                                stri = "";
                                stri = tab[pos];
                                return stri;
                            }

                            vectNode.add(tab);
                            numb--;
                        }
                    }
                    if (bit == 58) {
                        str = "";
                    }

                    if (contain(str) >= 0) {
                        tabnum = contain(str);
                    }

                    str += (char) bit;
                }
                buf = new byte[8];// reinitialization

            }

        } catch (FileNotFoundException e) {
            // Cette exception est levée si l'objet FileInputStream ne trouve
            // aucun fichier
            e.printStackTrace();
        } catch (IOException e) {
            // Celle-ci se produit lors d'une erreur d'écriture ou de lecture
            e.printStackTrace();
        } finally {
            // On ferme nos flux de données dans un bloc finally pour s'assurer
            // que ces instructions seront exécutées dans tous les cas même si
            // une exception est levée !
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tab[0];

    }

    public Vector getVectNode() {
        return vectNode;
    }

    public void setVectNode(Vector vectNode) {
        this.vectNode = vectNode;
    }

    public int contain(String str) {
        if (str.contains("Class")) {
            return 0;
        }
        if (str.contains("Source")) {
            return 1;
        }
        if (str.contains("Throughput")) {
            return 2;
        }
        if (str.contains("Localisation X")) {
            return 3;
        }
        if (str.contains("Localisation Y")) {
            return 4;
        }
        if (str.contains("Sensing Range")) {
            return 5;
        }
        return -1;
    }

    public void copy() {

        // Création de l'objet File
        File f = new File("src/Input.txt");
        // Packages à importer afin d'utiliser les objets

        // Nous déclarons nos objets en dehors du bloc try/catch
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            // On instancie nos objets :
            // fis va lire le fichier
            // fos va écrire dans le nouveau !
            fis = new FileInputStream(new File("src/Input.txt"));
            fos = new FileOutputStream(new File("src/Output.txt"));

            // On crée un tableau de byte pour indiquer le nombre de bytes lus à
            // chaque tour de boucle
            byte[] buf = new byte[8];

            // On crée une variable de type int pour y affecter le résultat de
            // la lecture
            // Vaut -1 quand c'est fini
            int n = 0;

            // Tant que l'affectation dans la variable est possible, on boucle
            // Lorsque la lecture du fichier est terminée l'affectation n'est
            // plus possible !
            // On sort donc de la boucle
            while ((n = fis.read(buf)) >= 0) {
                // On écrit dans notre deuxième fichier avec l'objet adéquat
                fos.write(buf);
                // On affiche ce qu'a lu notre boucle au format byte et au
                // format char
                for (byte bit : buf) {
                    System.out.print("\t" + bit + "(" + (char) bit + ")");
                }
                System.out.println("");
                // Nous réinitialisons le buffer à vide
                // au cas où les derniers byte lus ne soient pas un multiple de 8
                // Ceci permet d'avoir un buffer vierge à chaque lecture et ne pas avoir de
                // doublon en fin de fichier
                buf = new byte[8];

            }
            System.out.println("Copie terminée !");

        } catch (FileNotFoundException e) {
            // Cette exception est levée si l'objet FileInputStream ne trouve
            // aucun fichier
            e.printStackTrace();
        } catch (IOException e) {
            // Celle-ci se produit lors d'une erreur d'écriture ou de lecture
            e.printStackTrace();
        } finally {
            // On ferme nos flux de données dans un bloc finally pour s'assurer
            // que ces instructions seront exécutées dans tous les cas même si
            // une exception est levée !
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writefos(String str) {
        FileOutputStream fos = null;
        try (FileWriter fw = new FileWriter("src/Output.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(str);
        } catch (IOException e) {
            // Gestion des exceptions en cas de problème d'accès au fichier
        }
    }

    public void writefTot(String str) {
        FileOutputStream fos = null;
        try (FileWriter fw = new FileWriter("src/Total.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(str);
        } catch (IOException e) {
            // Gestion des exceptions en cas de problème d'accès au fichier
        }
    }

    public void writefInput(String str, boolean writeAfter) {
        FileOutputStream fos = null;
        try (FileWriter fw = new FileWriter("src/Input.txt", writeAfter);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(str);
        } catch (IOException e) {
            // Gestion des exceptions en cas de problème d'accès au fichier
        }
    }
    // a partir de output.txt : tri l'objet et l'insere dans total.txt
    public void count(FileInputStream f) {
        // compteur du changement
        Integer[] tab = new Integer[20];

        int finalCountNE = 0;
        try {
            byte[] buf = new byte[8];
            int n = 0;
            String str = null;
            String nashStr = null;
            String wordStr = null;
            int countNE = 0;

            for (int i = 0; i < tab.length; i++) {
                tab[i] = 0;
            }
            boolean begPos = true;
            boolean begNE = false;
            while ((n = f.read(buf)) >= 0) {
                for (byte bit : buf) {
                    // System.out.print("\t" + bit + "(" + (char) bit + ")");
                    if (bit == 32) {
                        wordStr = "";
                    }
                    wordStr += (char) bit;
                    if (wordStr.contains("Data")) {
                        if (countNE==12)
                        {
                            finalCountNE++;

                        }
                        System.out.println("TOTAL= " + countNE);
                        countNE = 0;
                    }
                    if (begNE) {
                        nashStr += (char) bit;
                    }
                    if (bit == 45) {
                        begNE = true;
                        countNE++;
                    }
                    if ((begPos) && (bit == 32)) {
                        int test = strToInt(str);
                        str = "";
                        if (test > 0) {
                            int number = test / 50;
                            if (number < tab.length) {
                                tab[number]++;
                            }
                        }
                    }
                    if (bit == 10) {
                        begPos = true;
                        // System.out.println(nashStr+" contient "+nashStr.contains("eq :true"));
                        begNE = false;
                        nashStr = "";
                    }
                    if (bit == 32) {

                        begPos = false;
                    }
                    if (begPos) {
                        str += (char) bit;
                    }
                }
                buf = new byte[8];
            }
            System.out.println("Terminée !");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (f != null)
                    f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream ftot = null;
        String s = null;
        try (FileWriter fw = new FileWriter("src/Total.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println("Test");

            for (int i = 1; i < tab.length; i++) {
                if (tab[i] != 0) {
                    s = "Entre " + i * 50 + " et " + (i + 1) * 50 + ", il y a " + tab[i] + " changements";
                    out.println(s);
                }
                s = "";
            }


            out.println("Equilibre de Nash atteint "+(finalCountNE+1)+" fois");


        } catch (IOException e) {
            // Gestion des exceptions en cas de problème d'accès au fichier
        }

    }

    public int strToInt(String sourceStr) {
        int sourceInt = 0;
        for (int i = 0; i < sourceStr.length(); i++) {
            if (((int) sourceStr.charAt(i) >= 48) && ((int) sourceStr.charAt(i) <= 57)) {

                sourceInt = sourceInt * 10 + sourceStr.charAt(i) - 48;
            }
        }
        return sourceInt;
    }

}