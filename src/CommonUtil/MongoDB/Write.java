package CommonUtil.MongoDB;

import CommonUtil.MongoDB.DataBaseUtil;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static CommonUtil.MongoDB.Create.CreateCollection;
import static CommonUtil.MongoDB.DataBaseUtil.getColletcionNamesInDB;
import static CommonUtil.MongoDB.DataBaseUtil.getMongoCollection;

public class Write {
    public static String dataManageDbName = "DataManage";
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
    public static void writeJson2Collection(String databaseName,String collection, String jsonData) {
        MongoClient client = DataBaseUtil.MongoDBConnect();
        MongoDatabase md = client.getDatabase(databaseName);
        MongoCollection<Document> mc;
        if(!getColletcionNamesInDB(databaseName).contains(collection))
            CreateCollection(databaseName,collection);
        mc = md.getCollection(collection);
        Document document = Document.parse(jsonData);
        mc.insertOne(document);
    }
    /**
     * 把Json字符传写入数据管理集合
     *
     * @param json
     * @param colName
     */
    public static void write2DataManage(String json, String colName) {
        MongoCollection<Document> collection = getMongoCollection(dataManageDbName, colName);
        Document document = Document.parse(json);
        collection.insertOne(document);
    }
}
