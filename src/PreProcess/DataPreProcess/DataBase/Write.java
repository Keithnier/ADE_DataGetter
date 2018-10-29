package PreProcess.DataPreProcess.DataBase;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static PreProcess.DataPreProcess.DataBase.Create.CreateCollection;
import static PreProcess.DataPreProcess.DataBase.DataBaseUtil.getColletcionNamesInDB;

public class Write {
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
}
