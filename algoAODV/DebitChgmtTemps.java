package algoAODV;

public class DebitChgmtTemps {

	public double debit;
	public int nbchgmt;
	public long temps;
	public int nbOptiPareto;
	
	public DebitChgmtTemps(double debit, int nbchgmt, long temps, int nbOptiPareto) {
		super();
		this.debit = debit;
		this.nbchgmt = nbchgmt;
		this.temps = temps;
		this.nbOptiPareto = nbOptiPareto;
	}
	public int getNbOptiPareto() {
		return nbOptiPareto;
	}
	public void setNbOptiPareto(int nbOptiPareto) {
		this.nbOptiPareto = nbOptiPareto;
	}
	public DebitChgmtTemps(double debit, int nbchgmt, long temps) {
		super();
		this.debit = debit;
		this.nbchgmt = nbchgmt;
		this.temps = temps;
	}
	@Override
	public String toString() {
		return "DebitChgmtTemps [debit=" + debit + ", nbchgmt=" + nbchgmt + ", temps=" + temps + ", nbOptiPareto="
				+ nbOptiPareto + "]";
	}
	public double getDebit() {
		return debit;
	}
	public void setDebit(double debit) {
		this.debit = debit;
	}
	public int getNbchgmt() {
		return nbchgmt;
	}
	public void setNbchgmt(int nbchgmt) {
		this.nbchgmt = nbchgmt;
	}
	public long getTemps() {
		return temps;
	}
	public void setTemps(long temps) {
		this.temps = temps;
	}
	
}
