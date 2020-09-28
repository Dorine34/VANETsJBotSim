package algoAODV;


public class Aodv {
	int source;
	int nbSauts;
	HighwayCar carVoisin;
	HighwayTower tourElue;
	double debit;
	long temps;
	
	
	public Aodv(int source, int nbSauts, HighwayCar carVoisin, HighwayTower tourElue, double debit, long temps) {
		super();
		this.source = source;
		this.nbSauts = nbSauts;
		this.carVoisin = carVoisin;
		this.tourElue = tourElue;
		this.debit = debit;
		this.temps = temps;
	}
	public HighwayTower getTourElue() {
		return tourElue;
	}
	public void setTourElue(HighwayTower tourElue) {
		this.tourElue = tourElue;
	}
	public double getDebit() {
		return debit;
	}
	public void setDebit(double debit) {
		this.debit = debit;
	}
	
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
		if ((carVoisin!=null)&&(tourElue!=null))
		{
			return " [source=" + source + ", nbSauts=" + nbSauts + ", carVoisin :"+ this.carVoisin.getID()+", tourElue :"+this.tourElue.getID()+", debit=" +
					debit + ", temps=" + temps + "]";
		}
		if (carVoisin!=null)
		{
			return " [source=" + source + ", nbSauts=" + nbSauts + ", carVoisin"+this.carVoisin.getID()+", PB pas de tour Elue, debit=" +
					debit + ", temps=" + temps + "]";
		}
		if (tourElue!=null)
		{
			return " [source=" + source + ", nbSauts=" + nbSauts + ", PB pas de carVoisin, tourElue"+this.tourElue.getID()+", debit=" +
					debit + ", temps=" + temps + "]";
		}
		return " [source=" + source + ", nbSauts=" + nbSauts + ", PB *2 : pas de tour elue et de car voisn, debit=" +
		debit + ", temps=" + temps + "]";
		
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