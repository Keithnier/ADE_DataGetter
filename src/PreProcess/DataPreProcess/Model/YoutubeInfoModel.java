package PreProcess.DataPreProcess.Model;

public class YoutubeInfoModel extends InfoModel {
    private String content;

    public YoutubeInfoModel(String id, String type, String content){
        this.id = id;
        this.type = type;
        this.content = content;
    }

    @Override
    public String toString() {
        return "--------" + type + "---------\n"
                + "ID: " + id + "\n"
                + "Content: " + content + "\n"
                +"=================================";
    }
}
