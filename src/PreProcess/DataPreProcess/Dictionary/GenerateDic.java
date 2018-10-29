package PreProcess.DataPreProcess.Dictionary;

import PreProcess.DataPreProcess.InfoExtra.TwitterExtract;
import PreProcess.DataPreProcess.Model.TwitterInfoModel;
import PreProcess.DataPreProcess.Segment.EnglishSegment;
import PreProcess.DataPreProcess.Segment.Segment;
import PreProcess.DataPreProcess.Util.FileSystemUtil;
import PreProcess.DataPreProcess.Util.PropertyUtil;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static PreProcess.DataPreProcess.DataBase.DataBaseUtil.getColletcionNamesInDB;
import static PreProcess.DataPreProcess.DataBase.DataBaseUtil.getMongoCollection;

public class GenerateDic {
    /**
     * 生成用户词典，首先从数据库中读入所有的text，然后将每一个text进行分词、过滤后加入到
     * set集合中，最后把set集合转成List形成用户词典
     */
    private static final String DicPath = FileSystemUtil.getFullDataPath("Dictionary.txt");


    public static List<String> generateDict(String DBname, Segment seg) {
        HashSet<String> dic_set = new HashSet<>();//词语先放到set中，然后转成list
        List<String> collnames = getColletcionNamesInDB(DBname);
        String stopwprdpath = FileSystemUtil.getFullDataPath(PropertyUtil.getValueByKey("stopwordpath"));
        //每张表
        for(String collname:collnames){
            MongoCollection<Document> mgc = getMongoCollection(DBname,collname);
            //每个元素
            FindIterable<Document> findIterable = mgc.find();
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            while (mongoCursor.hasNext()) {
                //获得一个数据元素
                Document doc = mongoCursor.next();
                //获得text字段
                TwitterInfoModel tf = new TwitterExtract().jsonInfoExtra(doc.toJson());
                if (tf == null) continue;
                String text = tf.getText();
                if (text == null) continue;
                //进行分词
                List<String> words = seg.doAll(text, stopwprdpath);
                //将词语加入到set中
                dic_set.addAll(words);
            }
        }
        //生成词典
        List<String> dic_list = new ArrayList<>(dic_set);
        dic_list.sort(Comparator.naturalOrder());
        //将词典写入到文件中
        try{
            String filepath = FileSystemUtil.getFullDataPath("Dictionary.txt");
            PrintStream br = new PrintStream(new FileOutputStream(filepath));
            for (String word:dic_list){
                br.println(word);
            }
            br.close();
            System.out.println("字典生成成功！");


        }catch (IOException e1){
            e1.printStackTrace();
        }
        return dic_list;
    }

    /**
     * 在一个Collection中生成字典，该方法用于测试，一般情况下字典要对整个数据集进行操作
     * @param DBname
     * @param seg
     * @param Collection
     * @return
     */
    public static List<String> generateDict(String DBname, Segment seg, String Collection) {
        HashSet<String> dic_set = new HashSet<>();//词语先放到set中，然后转成list
        String stopwprdpath = FileSystemUtil.getFullDataPath(PropertyUtil.getValueByKey("stopwordpath"));
        //每张表
        MongoCollection<Document> mgc = getMongoCollection(DBname,Collection);
        //每个元素
        FindIterable<Document> findIterable = mgc.find();
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            //获得一个数据元素
            Document doc = mongoCursor.next();
            //获得text字段
            String string = doc.toJson();
            TwitterInfoModel tf = new TwitterExtract().jsonInfoExtra(string);
            if (tf == null) continue;
            String text = tf.getText();
            if (text == null) continue;
                //进行分词
            List<String> words = seg.doAll(text, stopwprdpath);
            //将词语加入到set中
            dic_set.addAll(words);
        }
        //生成词典
        List<String> dic_list = new ArrayList<>(dic_set);
        dic_list.sort(Comparator.naturalOrder());
        //将词典写入到文件中
        try{
            String filepath = FileSystemUtil.getFullDataPath("Dictionary.txt");
            FileOutputStream fo =  new FileOutputStream(filepath);
            PrintStream br = new PrintStream(fo);
            for (String word:dic_list){
                br.println(word);
            }
            br.close();
            System.out.println("字典生成成功！");
        }catch (IOException e1){
            e1.printStackTrace();
        }
        return dic_list;
    }
    public static void main(String[] args){
        generateDict("TwitterData",new EnglishSegment(), "twitter_20171212132632_data");
    }
}
