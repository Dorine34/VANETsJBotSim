
public class BlueTooth extends Car{
    /* Partie option blueTooth */

    boolean blueTooth = false;
    float blueToothThroughput=0;

    public BlueTooth(int s, float t) {
        super(s);
        blueTooth = true;
        blueToothThroughput = t;
        setIcon("/car.png");
        //setSize(10);
        setSensingRange(100);
    }


    public BlueTooth(int s, float f, float x, float y) {
        super(s, x, y);
        blueTooth = true;
        blueToothThroughput = f;
        setIcon("/car.png");
        //setSize(10);
        setSensingRange(100);
    }
    public BlueTooth(int s, float f, float x, float y, FileManager file) {
        super(s, x, y, file);
        blueTooth = true;
        blueToothThroughput = f;
        setIcon("/car.png");
        //setSize(10);
        setSensingRange(100);
    }

    public BlueTooth() {
        super();
        blueTooth = true;
        blueToothThroughput = 1;
        setIcon("/car.png");
        //setSize(10);
        setSensingRange(100);
    }

    public boolean isBlueTooth() {
        return blueTooth;
    }

    public void setBlueTooth(boolean blueTooth) {
        this.blueTooth = blueTooth;
    }

    public float getBlueToothThroughput() {
        return blueToothThroughput;
    }

    public void setBlueToothThroughput(float blueToothThroughput) {
        this.blueToothThroughput = blueToothThroughput;
    }

}