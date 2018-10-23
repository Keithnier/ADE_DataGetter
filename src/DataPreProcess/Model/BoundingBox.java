package DataPreProcess.Model;

/**
 * 待填写
 */
public class BoundingBox {
    private double west_long;
    private double south_lat;
    private double east_long;
    private double north_lat;

    public BoundingBox(double west_long, double south_lat, double east_long, double north_lat) {
        this.west_long = west_long;
        this.south_lat = south_lat;
        this.east_long = east_long;
        this.north_lat = north_lat;
    }

    public BoundingBox(String bound) {
        String [] bounds = bound.split(":");
        if(bounds.length!=4) {
            this.west_long=this.south_lat=this.east_long=this.north_lat = 0.0;
        }
        else{
            this.west_long = Double.parseDouble(bounds[0]);
            this.south_lat = Double.parseDouble(bounds[1]);
            this.east_long = Double.parseDouble(bounds[2]);
            this.north_lat = Double.parseDouble(bounds[3]);
        }
    }

    public double getWest_long() {
        return west_long;
    }

    public void setWest_long(double west_long) {
        this.west_long = west_long;
    }

    public double getEast_long() {
        return east_long;
    }

    public void setEast_long(double east_long) {
        this.east_long = east_long;
    }

    public double getNorth_lat() {
        return north_lat;
    }

    public void setNorth_lat(double north_lat) {
        this.north_lat = north_lat;
    }

    public double getSouth_lat() {
        return south_lat;
    }

    public void setSouth_lat(double south_lat) {
        this.south_lat = south_lat;
    }

    @Override
    public String toString() {
        return getWest_long()+":"+getSouth_lat()+":"+getEast_long()+":"+getNorth_lat();
    }

    public double getX(){
        return (this.west_long+this.east_long)/2;
    }
    public double getY(){
        return(this.north_lat+this.south_lat)/2;
    }

}
