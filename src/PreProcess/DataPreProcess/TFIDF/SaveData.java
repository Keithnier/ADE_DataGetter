package PreProcess.DataPreProcess.TFIDF;

import PreProcess.DataPreProcess.Util.FileSystemUtil;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static CommonUtil.MongoDB.Write.writeJson2Collection;
import static PreProcess.DataPreProcess.Util.PropertyUtil.getValueByKey;

public class SaveData {

    public static final int SAVE_DATABASE = 0;
    public static final int SAVE_FILE = 1;//未完成

    public static void Save2DB(int TypeCode){
        switch (TypeCode){
            case PreCut.TWITTER:
                __twitterSave__(SAVE_DATABASE);
                break;
            case PreCut.YOUTUBE:
                break;
            case PreCut.FLICKR:
                break;
            case PreCut.TUMBLR:
                break;
            default:
                break;
        }
    }

    /**
     * 增加保存固定的表
     * @param TypeCode
     */
    public static void Save2DB(int TypeCode , String Collection){
        switch (TypeCode){
            case PreCut.TWITTER:
                __twitterSave__(SAVE_DATABASE, Collection);
                break;
            case PreCut.YOUTUBE:
                break;
            case PreCut.FLICKR:
                break;
            case PreCut.TUMBLR:
                break;
            default:
                break;
        }
    }

    /**
     * 保存twitter的权重计算结果
     * @param where
     */
    public static void __twitterSave__(int where){
        BufferedReader br_id;
        BufferedReader br_wordweight;
        BufferedReader br_time;
        BufferedReader br_location;
        BufferedReader br_bounding;
        String cachePath = FileSystemUtil.class.getClassLoader().
                getResource("PreProcess/Cache/").getPath();

        String WeightPath = getValueByKey("WeightBaseName");
        String TwitterColl = getValueByKey("TwitterWightCollection");
        try{
            //获取各个输入流
            br_id = new BufferedReader(new FileReader(cachePath+"__preCutTwitterId__"));
            br_wordweight = new BufferedReader(new FileReader(cachePath+"__TF_IDF__"));
            br_time = new BufferedReader(new FileReader(cachePath+"__preCutTwitterTime__"));
            br_location = new BufferedReader(new FileReader(cachePath+"__preCutTwitterLocal__"));
            br_bounding = new BufferedReader(new FileReader(cachePath+"__preCutTwitterBounding__"));

            String JSONStr;
            while((JSONStr=getJSONData(br_id, br_wordweight,br_time,br_location,br_bounding))!=null){
                switch (where){
                    case SAVE_DATABASE:
                        writeJson2Collection(WeightPath,
                                TwitterColl ,JSONStr);
                        break;
                    default:
                        break;
                }
            }
            br_id.close();
            br_bounding.close();
            br_location.close();
            br_time.close();
            br_wordweight.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("数据计算失败！");
        }
    }
    public static void __twitterSave__(int where, String Collection){
        BufferedReader br_id;
        BufferedReader br_wordweight;
        BufferedReader br_time;
        BufferedReader br_bounding;
        BufferedReader br_location;
        String cachePath = FileSystemUtil.class.getClassLoader().
                getResource("PreProcess/Cache/").getPath();
        String WeightPath = getValueByKey("TwitterWeightBaseName"); //新建数据库保存
        try{
            //获取各个输入流
            br_id = new BufferedReader(new FileReader(cachePath+"__preCutTwitterId__"));
            br_wordweight = new BufferedReader(new FileReader(cachePath+"__TF_IDF__"));
            br_location = new BufferedReader(new FileReader(cachePath+"__preCutTwitterLocal__"));
            br_time = new BufferedReader(new FileReader(cachePath+"__preCutTwitterTime__"));
            br_bounding = new BufferedReader(new FileReader(cachePath+"__preCutTwitterBounding__"));

            String JSONStr;
            while((JSONStr=getJSONData(br_id, br_wordweight,br_time,br_location,br_bounding))!=null){
                switch (where){
                    case SAVE_DATABASE:
                        writeJson2Collection(WeightPath,
                                Collection ,JSONStr);
                        break;
                    default:
                        break;
                }
            }
            br_id.close();
            br_bounding.close();
            br_location.close();
            br_time.close();
            br_wordweight.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("数据计算失败！");
        }
    }
    /**
     * 获得JSONData
     * 五个输入流
     * @param br_id
     * @param br_wordweight
     * @param br_time
     * @param br_location
     * @param br_bounding
     * @return //JSONData
     */
    public static String getJSONData(BufferedReader br_id, BufferedReader br_wordweight,BufferedReader br_time,
                                  BufferedReader br_location,BufferedReader br_bounding){

        JSONObject jsonObject;
        try{
            String id_str = br_id.readLine();
            String weight_str = br_wordweight.readLine();
            String time_str = br_time.readLine();
            String local_str = br_location.readLine();
            String bounding_str = br_bounding.readLine();

            if(id_str==null||weight_str==null||time_str==null||local_str==null||bounding_str==null)
                return null;
            jsonObject = new JSONObject();
            //处理id
            jsonObject.put("twitterid",id_str.replace("\n","").replace("\r",""));

            //处理权重
            Map<String, String> map = TransferWeightMap(weight_str);
            jsonObject.put("weight",map);

            //处理时间
            jsonObject.put("timestamp",time_str.replace("\n","").replace("\r",""));

            //处理地点
            jsonObject.put("location",local_str.replace("\n","").replace("\r",""));

            //处理经纬度
            bounding_str = bounding_str.replace("\n","").replace("\r","");
            String [] bounding = bounding_str.split(":");
            jsonObject.put("locationX",bounding[0]);
            jsonObject.put("locationY",bounding[1]);
            return jsonObject.toJSONString();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String,String> TransferWeightMap(String str){
        Map<String, String> map = new HashMap<>();

        str = str.replace("\n","").replace("\r","");
        if(str.equals("")) return null;
        String[] entry = str.split(",");
        for(String ent:entry){
            String[] kv = ent.split(":");
            map.put(kv[0],kv[1]);
        }
        return map;
    }
}
