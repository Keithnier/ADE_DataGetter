package spatialindex.rtree;

class Location {
    private double coordinate[] = new double[2];
    Location(double x,double y){coordinate[0]=x; coordinate[1]=y; }

    double[] getCoordinate() {
        return coordinate;
    }
}
