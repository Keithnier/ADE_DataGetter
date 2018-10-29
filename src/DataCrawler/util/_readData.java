package DataCrawler.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static DataCrawler.util.MongoDB.getAllDataInDB;

public class _readData implements readData {
    private  BufferedReader in;
    private String file;
    public String line;
    private  Map<String, List<String>> DBresult;
    private  String key;
    private  List<String> data;
    private  Iterator<String> it_key;
    private  Iterator<String> it_data;
    private  boolean flag;
    public static Map<Integer,String> idMap = new HashMap<>();
    private static int id = 0;//文档id

    public _readData(){

    }

    public _readData(String filePath)throws IOException {
        //初始化
        file = filePath;
        if (filePath.endsWith(".txt"))
            in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        else{
            MongoDB.MongoDBConnect();
            DBresult = getAllDataInDB(filePath);//此时filePath = DBname 如“YoutubeData”等
            it_key = DBresult.keySet().iterator();
            key = it_key.next();
            data = DBresult.get(key);
            it_data = data.iterator();
            flag = (DBresult.size() == 1); //标记是否为最后一次的数据，若不是则为false，若是则为true
        }
    }

    public String myreadline(String filePath){
        return filePath.endsWith(".txt")? readBytxt():readByDB();
    }

    public String readBytxt(){
        try {
            line = in.readLine();
        }catch (IOException e){
            System.err.println("fail to read data! " );
            System.exit(-1);
        }
        if (line==null) return null;
        else return Json2Result(line);
    }

    public String readByDB(){
        String result = null;
        if (it_key.hasNext() || flag){
            if(it_data.hasNext()){
                result = it_data.next();
                //将JSON格式的result转换为所需的格式
                result = Json2Result(result);
            }
            else if (!flag) {
                key = it_key.next();
                data = DBresult.get(key);
                it_data = data.iterator();
                result = it_data.next();
                result = Json2Result(result);
                flag = true;
            }
        }
        return result;
    }

    public String Json2Result(String str){
        String result = "";
        try {
            JsonObject json = new JsonParser().parse(str).getAsJsonObject();
            if (! json.has("weight")) return "false format";
            //TODO:每个平台id名称不同，不好统一写，比如现在是"twitterid"，但是之后可能是“youtubeid”等
            //TODO:所以建议整合时修改一下预处理的结果，统一名称为“id”之类的
            idMap.put(id,json.get("twitterid").getAsString());
            result += id + ",";
            result += json.get("timestamp").getAsString() + ",";
            result += json.get("locationX").getAsString() + ",";
            result += json.get("locationY").getAsString() + ",";
            result += json.get("locationX").getAsString() + ",";
            result += json.get("locationY").getAsString() + ",";
            JsonObject subwordWeight = json.getAsJsonObject("weight");
            for (String wordid : subwordWeight.keySet()) {
                result += wordid + " ";
                result += subwordWeight.get(wordid).getAsString() + ",";
            }
        }catch (IllegalStateException e){
            System.out.println("错误的Json格式！");
            return null;
        }
        id += 1;
        return result.substring(0,result.length()-1);//去除最后一个","
    }


    public void close()throws IOException {
        if (file.endsWith(".txt"))
            in.close();
        else MongoDB.disconnectMongoDB();
        id = 0;
    }

    public static void main(String[] args)throws IOException{
        _readData read = new _readData("G:\\laboratory\\IRTree\\test\\data.txt");
        String str;
        while ((str=read.myreadline("G:\\laboratory\\IRTree\\test\\data.txt"))!=null)
            System.out.println(str);
        read.close();
    }
}
