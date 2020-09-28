import io.jbotsim.core.Color;

public class Cellular extends Tower{

    int HYSTERESIS=5;
    public Cellular() {
        super();
        this.setColor(Color.orange);
        // TODO Auto-generated constructor stub
    }

    public Cellular(float t, int r,  FileManager f) {
        super( t,r,f);
        this.setColor(Color.orange);
        // TODO Auto-generated constructor stub
    }

    public Cellular(float t, int r, float x, float y) {
        super(t, r,x,y);
        this.setColor(Color.orange);
        // TODO Auto-generated constructor stub

    }

    public void onClock() {
        super.onClock();
        this.throughputAvailable=updateThroughputSize(this.l_carUsed.size());

    }
    public float updateThroughputSize(int size)
    {

        //System.out.println("cellulaire");
        float fading=0;
        for (int i=0; i<l_carUsed.size();i++)
        {
            fading+=1/(i+1);
        }
        //System.out.println("le fading est de "+fading);
        if (size>=1)
        {

            return this.throughputTotal/size*fading;
        }
        return this.throughputTotal ;
    }
    public float updateThroughputSizePlusOne()
    {
        float fading=0;
        for (int i=0; i<(l_carUsed.size()+1);i++)
        {
            fading+=1/(i+1);
        }
        //System.out.println("le fading est de "+fading);


        //System.out.println("wifi");
        return this.throughputTotal/(l_carUsed.size()+1);

    }


}