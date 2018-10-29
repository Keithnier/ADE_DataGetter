package PreProcess.DataPreProcess.InfoExtra;

import PreProcess.DataPreProcess.Model.BoundingBox;
import PreProcess.DataPreProcess.Model.TwitterInfoModel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 解析Twitter原始数据类
 *
 */
public class TwitterExtract implements InfoExtract {
    @Override
    public TwitterInfoModel jsonInfoExtra(String jsonString) {
        JSONObject jsonObject = JSON.parseObject(jsonString);
        if(jsonObject.size() <= 3) {
            System.out.println("Error Info: " + jsonString);
            return null;
        }
        //获取地点
        if(jsonObject.getJSONObject("place") == null) {
            System.out.println("Error Info[ No place info ]: " + jsonString);
            return null;
        }
        String timestamp = jsonObject.getString("timestamp_ms");
        //获取原始数据，还未分词
        String text = jsonObject.getString("text");
        String id = jsonObject.getString("id_str");
        String location = jsonObject.getJSONObject("place").getString("full_name");
        JSONArray box = jsonObject.getJSONObject("place").getJSONObject("bounding_box").getJSONArray("coordinates");
        double west_long = box.getJSONArray(0).getJSONArray(0).getDoubleValue(0);
        double south_lat = box.getJSONArray(0).getJSONArray(0).getDoubleValue(1);
        double east_long = box.getJSONArray(0).getJSONArray(2).getDoubleValue(0);
        double north_lat = box.getJSONArray(0).getJSONArray(2).getDoubleValue(1);

        BoundingBox boundingBox = new BoundingBox(west_long, south_lat, east_long, north_lat);
        return new TwitterInfoModel("Twitter",id, text, location, boundingBox, timestamp);
    }


}
