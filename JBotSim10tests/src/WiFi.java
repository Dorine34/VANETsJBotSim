import io.jbotsim.core.Color;

public class WiFi extends Tower{

    int HYSTERESIS=3;
    public WiFi() {
        super();
        this.setColor(Color.yellow);
        // TODO Auto-generated constructor stub
    }

    public WiFi(float t, int r, FileManager f) {

        super(t, r,f);
        this.setColor(Color.yellow);
        // TODO Auto-generated constructor stub
    }

    public WiFi(float t, int r, float x, float y) {
        super(t, r, x, y);
        this.setColor(Color.yellow);

        // TODO Auto-generated constructor stub
    }

    public void onClock() {
        super.onClock();
        if (super.l_carUsed.size()>=1)
        {
            this.throughputAvailable=updateThroughputSize(super.l_carUsed.size());
        }
    }
    public float updateThroughputSize(int size)
    {

        //System.out.println("wifi");
        return this.throughputTotal/size;

    }
    public float updateThroughputSizePlusOne()
    {

        //System.out.println("wifi");
        return this.throughputTotal/(l_carUsed.size()+1);

    }
}