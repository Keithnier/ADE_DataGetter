package PreProcess.DataPreProcess.Model;

public class TwitterInfoModel extends InfoModel {
    private String text;
    private String locationName;
    private BoundingBox boundingBox;
    private String timestamp;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * TwitterInfoModel构造方法
     * @param id Twitter的ID
     * @param text Twitter的内容
     * @param locationName twitter的地点
     * @param boundingBox twitter的boundingBox
     * @param timestamp twitter的timestamp
     * @param type twitter的类型
     */

    public TwitterInfoModel(String type, String id, String text, String locationName, BoundingBox boundingBox, String timestamp) {
        this.text = text;
        this.locationName = locationName;
        this.boundingBox = boundingBox;
        this.timestamp = timestamp;
        this.id = id;
        this.type = type;
    }

    public TwitterInfoModel(){
        super();
        this.type = "Twitter";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * 将之前的PrettyPrint修改成toString方法
     * @return string输出字符串
     */
    public String toString(){
        return "--------" + type + "---------\n"
                + "ID: " + id + "\n"
                + "Text: " + text + "\n"
                + "Location: " + locationName + "\n"
                + "Bounding" + boundingBox.toString()+"\n"
                +"=================================";
    }
}
