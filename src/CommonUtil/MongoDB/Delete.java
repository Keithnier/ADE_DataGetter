package CommonUtil.MongoDB;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import static CommonUtil.MongoDB.DataBaseUtil.getMongoCollection;

public class Delete {
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
}
