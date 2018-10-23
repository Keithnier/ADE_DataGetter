package spatialindex.rtree;


/**
 * @description 该类是个辅助类，用来因此返回时空坐标
 * @author Pulin Xie
 */
class DataCoordinate {
    double time;
    double x1, y1, x2, y2;

    public DataCoordinate(double time, double x1, double y1, double x2, double y2) {
        this.time = time;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
}