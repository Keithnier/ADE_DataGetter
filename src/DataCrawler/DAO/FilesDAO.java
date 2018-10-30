package DataCrawler.DAO;

import DataCrawler.model.FilesModel;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import static CommonUtil.MongoDB.Read.getAllDataInManage;
import static CommonUtil.MongoDB.Delete.deleteDataInCollection;
import static CommonUtil.MongoDB.Read.dataManageDbName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Web显示爬到文件的辅助工具
 */
public class FilesDAO {


    /**
     * 获取文件信息
     * @return 所有爬到的文件
     */
    public List<FilesModel> readFilesModel(){

        Map<String, List<String>> results = getAllDataInManage();

        List<FilesModel> filesModels = new ArrayList<>();

        //在results的值中进行遍历
        for(List<String> filesStr : results.values()) {
            for(String fileStr : filesStr) {
                try{
                    filesModels.add(str2FilesModel(fileStr));
                } catch (Exception e) {
                    System.out.println("String to FilesModel failed. String: " + fileStr);
                }
            }
        }
        return filesModels;
    }

    /**
     * 把JSON字符串转化为文件对象
     * @param str
     * @return 文件对象
     * @throws JSONException
     */
    private FilesModel str2FilesModel(String str) throws JSONException {
        JSONObject jsonObject = new JSONObject(str);

        return new FilesModel(jsonObject.getString("fileName"), jsonObject.getString("startTime"),
                              jsonObject.getString("dataCount"), jsonObject.getString("dataSize"),
                              jsonObject.getString("crawler"),
                              jsonObject.getString("crawlerType"));
    }

    public void deleteFile(String fileName, String crawler){
        deleteDataInCollection(dataManageDbName, crawler + "_manage", "fileName", fileName);
    }
}
