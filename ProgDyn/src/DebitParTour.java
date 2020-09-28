

public class DebitParTour {

    Integer tourID ;
    double debit ;


    public Integer getTourID() {
        return tourID;
    }
    public void setTourID(Integer tourID) {
        this.tourID = tourID;
    }
    public double getDebit() {
        return debit;
    }
    public void setDebit(Integer debit) {
        this.debit = debit;
    }
    @Override
    public String toString() {
        return "DebitParTour [tourID=" + tourID + ", debit=" + debit + "]";
    }
    public DebitParTour(Integer tourID, double d) {
        super();
        this.tourID = tourID;
        this.debit = d;
    }


}