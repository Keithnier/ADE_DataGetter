package DataPreProcess.DataBase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.*;

import static DataPreProcess.DataBase.DataBaseUtil.MongoDBConnect;
import static DataPreProcess.DataBase.DataBaseUtil.getColletcionNamesInDB;
import static DataPreProcess.DataBase.DataBaseUtil.getMongoCollection;

public class Read {
    private static MongoCursor<Document> mongoCursor;//迭代器锚点
    private static FindIterable<Document> findIterable;//数据表的迭代器

    /**
     * 获取数据库下所有数据
     *
     * @param DBName 数据库名称
     * @return Map<String, List<String>> key值为collection name, data值为存放数据的List 数据格式为json
     */
    public static Map<String, List<String>> getAllDataInDB(String DBName) {
        Map<String, List<String>> result = new HashMap<>();

        try {
            MongoClient client = MongoDBConnect();
            MongoDatabase db = client.getDatabase(DBName);

            for (String collectionName : db.listCollectionNames()) {
                MongoCollection<Document> collection = db.getCollection(collectionName);
                FindIterable<Document> findIterable = collection.find();
                MongoCursor<Document> mongoCursor = findIterable.iterator();
                List<String> collectionData = new ArrayList<>();
                while (mongoCursor.hasNext()) {
                    collectionData.add(mongoCursor.next().toJson());
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
     * @param dbName 数据库名称
     * @param collectionName 集合名称
     * @return List<String> 数据格式为Json
     */
    public static List<String> getAllDataInCollection(String dbName, String collectionName) {
        List<String> result = new ArrayList<>();
        try {
            MongoClient client = MongoDBConnect();

            FindIterable<Document> findIterable = client.getDatabase(dbName).getCollection(collectionName).find();
            MongoCursor<Document> mongoCursor = findIterable.iterator();

            while (mongoCursor.hasNext()) {
                result.add(mongoCursor.next().toJson());
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取指定数据库下指定collection中的最新count条数据
     *
     * @param dbName 数据库名称
     * @param collectionName 集合名称
     * @param count 最新的条数
     * @return List<String> 数据格式为Json
     */
    public static List<String> getCountDataInCollection(String dbName, String collectionName, int count) {
        List<String> result = new ArrayList<>();
        try {
            MongoClient client = MongoDBConnect();

            FindIterable<Document> findIterable = client.getDatabase(dbName).getCollection(collectionName).find().sort(new BasicDBObject("_id",-1)).limit(count);

            for (Document aFindIterable : findIterable) {
                result.add(aFindIterable.toJson());
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        Collections.reverse(result); // 取出时为倒序，这里转为正序。
        return result;
    }

    /**
     * 在数据库中的数据表中查找相应的数据
     * 符合EntryName=value的数据被选择出来
     * @param DataBaseName 数据库名称
     * @param CollectionName 集合名称
     * @param EntryName 项目的名称
     * @param value 项目的值
     * @return 结果的Json字符串
     */
    public static List<String> findEntry(String DataBaseName, String CollectionName, String EntryName, String value){
        List<String> strList = new ArrayList<>();
        MongoCollection<Document> coll = getMongoCollection(DataBaseName, CollectionName);
        FindIterable<Document> findIterable = coll.find();
        for (Document doc : findIterable) {
            JSONObject data = JSON.parseObject(doc.toJson());
            String result = data.getString(EntryName);
            if(result==null) continue;
            if(result.equals(value))
                strList.add(doc.toJson());
        }
        return strList;
    }

    public static List<String> findEntry(String DataBaseName, String EntryName, String value){
        List<String> strList = new ArrayList<>();
        List<String> collnames = getColletcionNamesInDB(DataBaseName);
        for(String collname:collnames){
            List<String> str_ = findEntry(DataBaseName,collname,EntryName,value);
            strList.addAll(str_);
        }
        return strList;
    }
    /**
     * 利用迭代_iterator方式获取下一个元素
     * @return Json字符串
     */
    public static String nextElem(String DbName, String Collection){
        String result;//结果字符串
        //第一次迭代时获取锚点
        if(mongoCursor==null){
            MongoCollection<Document> coll = getMongoCollection(DbName, Collection);
            assert coll != null;
            findIterable = coll.find();
            mongoCursor = findIterable.iterator();
        }
        //正常情况
        if(mongoCursor.hasNext()){
            Document doc = mongoCursor.next();
            result = doc.toJson();
        }
        else{
            mongoCursor = null;
            findIterable = null;
            result = null;
        }
        return result;
    }

    public static boolean hasNextElem(){
        return mongoCursor.hasNext();
    }


}
