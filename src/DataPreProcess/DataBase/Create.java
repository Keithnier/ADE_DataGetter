package DataPreProcess.DataBase;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class Create {

    public static boolean CreateCollection(String dataBaseName, String collection){
        try{
            MongoClient client = DataBaseUtil.MongoDBConnect();
            MongoDatabase mongoDatabase = client.getDatabase(dataBaseName);
            mongoDatabase.createCollection(collection);
            System.out.println("在"+dataBaseName+"中创建"+collection+"数据表成功！");
            return true;
        }catch (Exception e){
            System.out.println("创建数据表失败！");
            return false;
        }
    }
}
