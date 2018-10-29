package PreProcess.DataPreProcess.InfoExtra;

import PreProcess.DataPreProcess.Model.BoundingBox;
import PreProcess.DataPreProcess.Model.FlickrInfoModel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 从json原始数据中提取有效信息
 */
public class FlickrExtract implements InfoExtract {
    @Override
    public FlickrInfoModel jsonInfoExtra(String jsonString) {
        JSONObject jsonObject = JSON.parseObject(jsonString);
        if(jsonObject.size() <= 3) {
            System.out.println("Error Info: " + jsonString);
            return null;
        }
        if(jsonObject.getJSONObject("id") == null) {
            System.out.println("Error Info[ No id info ]: " + jsonString);
            return null;
        }

        String timestamp = jsonObject.getString("timestamp_ms");
        String text = jsonObject.getString("text");
        String id = jsonObject.getString("id_str");
        String location = jsonObject.getJSONObject("place").getString("full_name");
        JSONArray box = jsonObject.getJSONObject("place").getJSONObject("bounding_box").getJSONArray("coordinates");
        double west_long = box.getJSONArray(0).getJSONArray(0).getDoubleValue(0);
        double south_lat = box.getJSONArray(0).getJSONArray(0).getDoubleValue(1);
        double east_long = box.getJSONArray(0).getJSONArray(2).getDoubleValue(0);
        double north_lat = box.getJSONArray(0).getJSONArray(2).getDoubleValue(1);

        BoundingBox boundingBox = new BoundingBox(west_long, south_lat, east_long, north_lat);
        return new FlickrInfoModel(id,"Flickr", "","","");
    }

}
