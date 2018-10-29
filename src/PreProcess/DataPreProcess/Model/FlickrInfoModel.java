package PreProcess.DataPreProcess.Model;

public class FlickrInfoModel extends InfoModel {
    private String title;
    private String latitude;
    private String longditude;

    /**
     * Flickr 的返回信息
     * @param id 记录的id
     * @param type 类型为Flickr
     * @param title 标题
     * @param latitude 图片的纬度
     * @param longitude 图片的经度
     */
    public FlickrInfoModel(String id, String type, String title, String latitude, String longitude) {
        this.id = id;
        this.type=type;
        this.title = title;
        this.latitude=latitude;
        this.longditude=longitude;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongditude() {
        return longditude;
    }

    public void setLongditude(String longditude) {
        this.longditude = longditude;
    }

    @Override
    public String toString() {
        return "--------" + type + "---------\n"
                + "ID: " + id + "\n"
                + "Title: " + title + "\n"
                + "Latitude: " + latitude + "\n"
                +"Longitude: " + longditude+"\n"
                +"=================================";
    }
}
