package algoAODV;
import java.util.ArrayList;
import jbotsim.Node;

public class HighwayTower extends Node {

	public static int SEUILCHGMT = 1;	
	public static int HYSTERESIS = 2;
	public static float PARAMRANDOM= (float) 0.5;
	public static float GAMMA= (float) 0.5;
	public static float TAU= (float) 1; 
	double portee = 100;
	double debitTotal = 0;
	ArrayList<HighwayCar> l_utilisateurs = new ArrayList<HighwayCar>();

	double debitDisponible = 0;
	int classe=0;
	
	

	/***************
	 * generateurs
	 * @return
	 */
	public HighwayTower(double portee, double debitTotal, double debitDisponible) {
		super();
		this.portee = portee;
		this.debitTotal = debitTotal;
		this.debitDisponible = debitDisponible;
		setSensingRange(this.portee);
	}

	public HighwayTower(double debitTotal) {
		super();
		this.debitDisponible = debitTotal;
		setIcon("/tower.png");
		setSize(14);
		this.debitTotal = debitTotal;
		setSensingRange(this.portee);
	}

	public HighwayTower() {
		setIcon("/tower.png");
		setSize(14);
		setSensingRange(this.portee);
	}

	/********************
	 * chrono
	 * @return
	 */

	public void onStart() {
	}
	/********************
	 * getteurs et setteurs
	 * @return
	 */
	
	public double getPortee() {
		return portee;
	}

	public void setPortee(double portee) {
		this.portee = portee;
	}

	public double getDebitTotal() {
		return debitTotal;
	}

	public void setDebitTotal(double debitTotal) {
		this.debitTotal = debitTotal;
	}

	public double getDebitDisponible() {
		return debitDisponible;
	}

	public void setDebitDisponible(double debitDisponible) {
		this.debitDisponible = debitDisponible;
	}

	public int getClasse() {
		return classe;
	}

	public void setClasse(int classe) {
		this.classe = classe;
	}
	@Override
	public String toString() {
		return "tour num " + this.getID() + "(" + debitDisponible + "/" + debitTotal + ") utilisé par "+this.l_utilisateurs.size();
	}
	
	/**************
	 * fonctions de regulation du debit
	 * @param c
	 */
	
	public void extract(HighwayCar c) {
		//System.out.println("hello donc extract");
		// System.out.println("-- tour num "+ this.getID()+" avant "+ c.getID() + " de taille "+this.l_utilisateurs.size());
		l_utilisateurs.remove(c);
		// System.out.println("-- tour num "+ this.getID()+" enleve "+ c.getID()+ " de taille "+this.l_utilisateurs.size());
		if (l_utilisateurs.size() == 0) {
			this.debitDisponible = this.debitTotal;
		} else {
			this.debitDisponible = this.debitTotal / l_utilisateurs.size();
		}
		System.out.println("++++++++++++++debit dispo tour"+this.getID()+" = "+this.debitDisponible);
	}



	public int getSizeL_utilisateurs() {
		return l_utilisateurs.size();
	}

	public void addCar(HighwayCar c) {
		System.out.println("-addcar--tour num "+this.getID()+" rajout "+c.getID()+ " de taille "+this.l_utilisateurs.size());
		this.l_utilisateurs.add(c);
		this.debitDisponible = this.debitTotal / l_utilisateurs.size();

		System.out.println("+++++++++++debit dispo tour"+this.getID()+" = "+this.debitDisponible);
	}

	public void onClock() {
		/*permet de calculer le debit total*/
		int tourUti=MainAODV.tourUtilisee(this.getID());
		if(l_utilisateurs.size()>0)
		{
			DebitParTour dpt=new DebitParTour(this.getID(), this.getDebitTotal());
			
			if (tourUti!=-1)
			{
				MainAODV.vectDebitTotal.remove(tourUti);
			}

			MainAODV.vectDebitTotal.add(dpt);	
		}
		else
		{
			if (tourUti!=-1)
			{
				MainAODV.vectDebitTotal.remove(tourUti);
			}
		}
		double res=0;
		for (int i=0; i<MainAODV.vectDebitTotal.size();i++)
		{
			res+=MainAODV.vectDebitTotal.get(i).getDebit();
				
		}
		System.out.println("Tour "+this.getID()+" : "+ l_utilisateurs.size()+" ; "+res);
	
	}
}