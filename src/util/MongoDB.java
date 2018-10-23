package util;


import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;

import java.util.*;
import java.util.List;

/**
 * 工具类，将数据存入数据库
 */
public class MongoDB {

    private static MongoClient client = null;

    public static String dataManageDbName = "DataManage";

    public static void MongoDBConnect() {
        if (client == null) {
            // 连接到 mongodb 服务
            client = new MongoClient("localhost", 27017);
            System.out.println("mongodb服务连接成功");
        }

    }

    static {

    }

    // 传入参数：文件路径，数据库名称，集合表格名称

    /**
     * 获取集合
     *
     * @param dbName
     * @param colName
     * @return
     */
    public static MongoCollection<Document> getMongoCollection(String dbName, String colName) {
        // 获取开始时间
        long start = System.currentTimeMillis();
        MongoCollection<Document> collection = null;
        try {
            MongoDB.MongoDBConnect();

            // 连接到数据库
            MongoDatabase mongoDatabase = client.getDatabase(dbName);
//            System.out.println("数据库连接成功");

            // 选择集合表格
            collection = mongoDatabase.getCollection(colName);
//            System.out.println("集合表格选择成功");
//
//            // 读取文件存入字符串
//            BufferedReader br = new BufferedReader(new FileReader(dataPath));
//            String jsonData = null;
//            while ((jsonData = br.readLine()) != null)
//            {
//                // 过滤较大字符串
//                if (jsonData.length() < 15000000)
//                {
//                    // 数据库操作
//                    Document document = Document.parse(jsonData);
//                    collection.insertOne(document);
//                    i++;
//                    if (i % 1000 == 0)
//                        System.out.println("已成功插入" + i + "份数据！");
//                }
//            }
//            // 关闭文件流
//            br.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return collection;
        }

//        // 获取结束时间
//        long end = System.currentTimeMillis();
//        // 输出运行时间
//        System.out.println("数据库写入成功，耗时："+(end - start) / 1000.0 +"m");
        return collection;
    }

    /**
     * 把Json字符传写入集合
     *
     * @param collection
     * @param jsonData
     */
    public static void writeJson2Collection(MongoCollection<Document> collection, String jsonData) {
        Document document = Document.parse(jsonData);
        collection.insertOne(document);
    }

    /**
     * 获取数据库下所有collection 名字
     *
     * @param DBName
     * @return list 存collection名
     */
    public static List<String> getColletcionNamesInDB(String DBName) {
        List collectionNames = new ArrayList<String>();
        try {
            MongoDB.MongoDBConnect();

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
     * 获取数据库下所有数据
     *
     * @param DBName
     * @return Map<String, List<String>> key值为collection name, data值为存放数据的List 数据格式为json
     */
    public static Map<String, List<String>> getAllDataInDB(String DBName) {
        Map<String, List<String>> result = new HashMap<>();

        try {
            MongoDB.MongoDBConnect();
            MongoDatabase db = client.getDatabase(DBName);

            for (String collectionName : db.listCollectionNames()) {
                MongoCollection collection = db.getCollection(collectionName);
                FindIterable<Document> findIterable = collection.find();
                MongoCursor<Document> mongoCursor = findIterable.iterator();
                List<String> collectionData = new ArrayList<>();
                while (mongoCursor.hasNext()) {
                    collectionData.add(JSON.serialize(mongoCursor.next()));
                }

                result.put(collectionName, collectionData);
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return result;
    }

    /**
     * 获取指定数据库下指定collection中的所有值
     *
     * @param dbName
     * @param collectionName
     * @return List<String> 数据格式为Json
     */
    public static List<String> getAllDataInCollection(String dbName, String collectionName) {
        List<String> result = new ArrayList<>();
        try {
            MongoDB.MongoDBConnect();

            FindIterable<Document> findIterable = client.getDatabase(dbName).getCollection(collectionName).find();
            MongoCursor<Document> mongoCursor = findIterable.iterator();

            while (mongoCursor.hasNext()) {
                result.add(JSON.serialize(mongoCursor.next()));
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return result;
    }


    /**
     * 获取指定数据库下指定collection中的最新count条数据
     *
     * @param dbName
     * @param collectionName
     * @param count
     * @return List<String> 数据格式为Json
     */
    public static List<String> getCountDataInCollection(String dbName, String collectionName, int count) {
        List<String> result = new ArrayList<>();
        try {
            MongoDB.MongoDBConnect();

            FindIterable<Document> findIterable = client.getDatabase(dbName).getCollection(collectionName).find().sort(new BasicDBObject("_id",-1)).limit(count);
            MongoCursor<Document> mongoCursor = findIterable.iterator();

            while (mongoCursor.hasNext()) {
                result.add(JSON.serialize(mongoCursor.next()));
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        Collections.reverse(result); // 取出时为倒序，这里转为正序。
        return result;
    }

    public static void main(String[] args) {
//        MongoCollection<Document> collection = MongoDB.getMongoCollection("test", "testCrawler");
//        MongoDB.writeJson2Collection(collection, "{\"test\": \"test1\"}");


//        testGetAllDataInDB("DataManage");
//        testGetCollectionNamesInDB("TwitterData");
//        testGetAllDataInCollection("TwitterData", "twitter_20171129144625_data");

        List<String> list = getColletcionNamesInDB("TwitterData");
        for (String str:list){
            System.out.println(str);
        }

    }

    private static void testGetAllDataInDB(String dbName) {
        Map<String, List<String>> result = MongoDB.getAllDataInDB(dbName);

        for (String key : result.keySet()) {
            List<String> data = result.get(key);
            System.out.println("collection name:  " + key);
            for (String str : data) {
                System.out.println("data:  " + str);
            }
        }
    }

    private static void testGetCollectionNamesInDB(String dbName) {
        List<String> result = MongoDB.getColletcionNamesInDB(dbName);

        for (String str : result) {
            System.out.println("collection name:   " + str);
        }
    }

    private static String testGetAllDataInCollection(String dbName, String collectionName) {
        List<String> collection = MongoDB.getAllDataInCollection(dbName, collectionName);
        String result = "";
        for (String str : collection) {
            result += str;
        }
        return result;
    }

    /**
     * 把Json字符传写入数据管理集合
     *
     * @param json
     * @param colName
     */
    public static void write2DataManage(String json, String colName) {
        MongoCollection<Document> collection = getMongoCollection(MongoDB.dataManageDbName, colName);
        Document document = Document.parse(json);
        collection.insertOne(document);
    }

    public static Map<String, List<String>> getAllDataInManage() {
        return getAllDataInDB(dataManageDbName);
    }


    /**
     * 删除集合中的某一条数据
     *
     * @param dbName
     * @param collectionName
     * @param key
     * @param value
     */
    public static void deleteDataInCollection(String dbName, String collectionName, String key, String value) {
        MongoCollection collection = getMongoCollection(dbName, collectionName);

        Document document = new Document();
        document.put(key, value);

        collection.deleteOne(document);

    }

    public static void disconnectMongoDB(){
        client.close();
    }

}
