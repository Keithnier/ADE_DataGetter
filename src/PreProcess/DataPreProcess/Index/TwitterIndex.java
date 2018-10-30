package PreProcess.DataPreProcess.Index;

import CommonUtil.MongoDB.DataBaseUtil;
import PreProcess.DataPreProcess.InfoExtra.TwitterExtract;
import PreProcess.DataPreProcess.Model.BoundingBox;
import PreProcess.DataPreProcess.Model.TwitterInfoModel;
import PreProcess.DataPreProcess.Util.FileSystemUtil;
import PreProcess.DataPreProcess.Util.PropertyUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static CommonUtil.MongoDB.Read.nextElem;

public class TwitterIndex implements Index<TwitterInfoModel> {

    /**
     * 创建索引
     */

    public void createIndex() {
        String rootPath = FileSystemUtil.getFullDataPath("LuceneIndexDir/");
        deleteAll(new File(rootPath+"Twitter/"));//先删除原有文件
        createDir(rootPath+"Twitter/");

        Analyzer analyzer = new StandardAnalyzer();
        //获得数据库名称
        String dbname = PropertyUtil.getValueByKey("TwitterDataBaseName");
        List<String> colls = DataBaseUtil.getColletcionNamesInDB(dbname);
        try{
            //文件系统总是出错，直接改成windows的路径了，这里不知道怎么用更好的办法解决
            //确定是在Linux上应该无法直接运行（没有试过）
            String strPath = FileSystemUtil.getFullDataPath("LuceneIndexDir/Twitter/");
            String [] strArr = strPath.split("/");
            StringBuilder sb = new StringBuilder();
            for(String str:strArr)
                if(!str.equals(""))
                    sb.append(str).append("//");
            Path path = Paths.get(sb.toString());
            Directory directory = FSDirectory.open(path);
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter iwriter = new IndexWriter(directory, config);
            for(String col:colls){
                String jsonStr;
                while((jsonStr=nextElem(dbname,col))!=null){
                    TwitterInfoModel tim = new TwitterExtract().jsonInfoExtra(jsonStr);
                    if(tim==null) continue;
                    //获得数据
                    String text = tim.getText();
                    if(text==null || text.equals("")) continue;//如果Twitter内容为空，则丢弃该条twitter
                    String id = tim.getId();//获得ID
                    if(id==null) id = "";
                    //处理地点
                    String location = tim.getLocationName();
                    if(location==null) location="";
                    String time = tim.getTimestamp();
                    String bounding = tim.getBoundingBox().toString();
                    Document doc = new Document();
                    doc.add(new TextField("type",tim.getType(),Field.Store.YES));
                    doc.add(new TextField("id",id,Field.Store.YES));
                    doc.add(new TextField("text",text,Field.Store.YES));
                    doc.add(new TextField("locationName",location,Field.Store.YES));
                    doc.add(new TextField("bounding",bounding,Field.Store.YES));
                    doc.add(new TextField("timestamp",time,Field.Store.YES));
                    iwriter.addDocument(doc);
                }
            }
            iwriter.close();
            directory.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 查找内容
     * @param text 搜索关键词
     * @return 查找结果的列表
     */
    @Override
    public List<TwitterInfoModel> searchByText(String text) {
        List<TwitterInfoModel> lucenesEntities = new ArrayList<>();
        try{
            String strPath = FileSystemUtil.getFullDataPath("LuceneIndexDir/Twitter/");
            String [] strArr = strPath.split("/");
            StringBuilder sb = new StringBuilder();
            for(String str:strArr)
                if(!str.equals(""))
                    sb.append(str).append("//");
            Path path = Paths.get(sb.toString());
            Directory directory = FSDirectory.open(path);
            DirectoryReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
            QueryParser parser = new QueryParser("text", new StandardAnalyzer());
            Query query = parser.parse(text);
            ScoreDoc[] hits = isearcher.search(query, 5).scoreDocs;
            //assertEquals(1, hits.length);
            // Iterate through the results:
            for (ScoreDoc hit : hits) {
                Document hitDoc = isearcher.doc(hit.doc);
                lucenesEntities.add(new TwitterInfoModel("Twitter",hitDoc.get("id"),
                        hitDoc.get("text"),hitDoc.get("locationName"),new BoundingBox(hitDoc.get("bounding")),hitDoc.get("timestamp")));
            }
            ireader.close();
            directory.close();
        }catch (IOException e){
            e.printStackTrace();
        }catch (ParseException e2){
            e2.printStackTrace();
            System.out.println("解析失败！");
            return lucenesEntities;
        }
        return lucenesEntities;
    }

    /**
     * 删除文件夹
     * @param file 文件
     */
    @Override
    public void deleteAll(File file) {
        if (file.isFile()) {
            if(!file.delete())
                System.out.println("错误！旧索引没有删除成功");
        }
        else {
            File[] files = file.listFiles();
            if(files == null) return;
            for (File file1 : files) {
                deleteAll(file1);
                if(!file1.delete()) System.out.println("文件没有删除成功");
            }
            if (file.exists()) // 如果文件本身就是目录 ，就要删除目录
                if(!file.delete()) System.out.println("文件没有删除成功");
        }
    }

    /**
     * 创建文件
     * @param destDirName 目标目录名称
     * @return 是否创建成功
     */
    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            System.out.println("创建目录" + destDirName + "成功！");
            return true;
        } else {
            System.out.println("创建目录" + destDirName + "失败！");
            return false;
        }
    }
    public static void main(String[] args){
        TwitterIndex ti = new TwitterIndex();
        ti.createIndex();
        List<TwitterInfoModel> list = ti.searchByText("came home to eat the calories lost hate myself");
        for(TwitterInfoModel tim:list){
            System.out.println(tim.toString());
        }
    }
}
