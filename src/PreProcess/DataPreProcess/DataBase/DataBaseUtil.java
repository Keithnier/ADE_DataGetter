package PreProcess.DataPreProcess.DataBase;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static PreProcess.DataPreProcess.Util.FileSystemUtil.getFullDataPath;
import static PreProcess.DataPreProcess.Util.PropertyUtil.getValueByKey;

public class DataBaseUtil {
    private static MongoClient client = null;

    /**
     * 内部方法：连接数据库
     */
    public static MongoClient MongoDBConnect() {
        if (client == null) {
            String PropertiesPath = getFullDataPath("config.properties");
            String host = getValueByKey(PropertiesPath,"dbhost");
            String str_port = getValueByKey(PropertiesPath,"dbport");

            if(host==null || str_port == null)
                System.out.println("数据库打开失败！");
            else{
                int port = Integer.parseInt(str_port);
                // 连接到 mongodb 服务
                client = new MongoClient(host, port);
                System.out.println("mongodb服务连接成功");
            }
        }
        return client;
    }

    /**
     * 获取集合MongoCollection<Document>
     * @param dbName 数据库名称
     * @param colName 集合的名称
     * @return 集合
     */
    public static MongoCollection<Document> getMongoCollection(String dbName, String colName) {
        // 获取开始时间
        MongoCollection<Document> collection = null;
        try {
            // 连接到数据库
            DataBaseUtil.MongoDBConnect();
            MongoDatabase mongoDatabase = client.getDatabase(dbName);
            // 选择集合表格
            collection = mongoDatabase.getCollection(colName);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        return collection;
    }

    /**
     * 获取数据库下所有collection 名字
     *
     * @param DBName 数据库名称
     * @return list 返回collection名字的list
     */
    public static List<String> getColletcionNamesInDB(String DBName) {
        List <String>collectionNames = new ArrayList<>();
        try {
            DataBaseUtil.MongoDBConnect();
            for (String name : client.getDatabase(DBName).listCollectionNames()) {
                collectionNames.add(name);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        Collections.sort(collectionNames);
        return collectionNames;
    }


    /**
     * 结束连接，当关闭网页的时候执行该方法
     */
    public static void MongoDBDisConnect(){
        if(client!=null){
            client.close();
        }
    }

}
