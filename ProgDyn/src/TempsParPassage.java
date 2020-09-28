


public class TempsParPassage {
    Integer id;
    Integer nbPassage ;
    long temps;
    double debit;
    boolean optiPareto;


    public boolean isOptiPareto() {
        return optiPareto;
    }
    public void setOptiPareto(boolean optiPareto) {
        this.optiPareto = optiPareto;
    }
    public Integer getNbPassage() {
        return nbPassage;
    }
    public void setNbPassage(Integer nbPassage) {
        this.nbPassage = nbPassage;
    }
    public long getTemps() {
        return temps;
    }
    public void setTemps(long temps) {
        this.temps = temps;
    }
    @Override
    public String toString() {
        return "["+nbPassage + "," + temps +","+this.debit+","+optiPareto+ "]";
    }

    public TempsParPassage(Integer id,long temps,Integer nbPassage, double debitT, boolean optiPareto) {
        super();
        this.nbPassage = nbPassage;
        this.temps = temps;
        this.debit=debitT;
        this.optiPareto=optiPareto;
        this.id=id;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public double getDebit() {
        return debit;
    }
    public void setDebit(double debit) {
        this.debit = debit;
    }


}