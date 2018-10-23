package DataPreProcess.InfoExtra;

import DataPreProcess.Model.InfoModel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TumblrExtract implements InfoExtract {

    @Override
    public InfoModel jsonInfoExtra(String jsonString) {
        JSONObject jsonObject = JSON.parseObject(jsonString);
        return null;
    }
}
