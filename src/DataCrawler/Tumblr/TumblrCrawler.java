package DataCrawler.Tumblr;

import com.mongodb.client.MongoCollection;
import DataCrawler.model.Crawler;
import DataCrawler.model.RestParam;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.bson.Document;
import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import DataCrawler.util.ConfigurationFactory;
import DataCrawler.util.FileSystem;
import static CommonUtil.MongoDB.DataBaseUtil.getMongoCollection;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 获取Tumblr上数据的爬虫
 * 没有按地区爬去功能
 */
public class TumblrCrawler implements Crawler{
    public final String dbName = "TumblrData";
    private String path;

    public TumblrCrawler(RestParam param) throws InterruptedException, ExecutionException, IOException {
        if(TumblrOAuth.isTokenInit() == false) {
            TumblrOAuth.initTumblrAccessToken(param);
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException, JSONException, ParseException {
        TumblrCrawler crawler = new TumblrCrawler(ConfigurationFactory.getRestParam("paramfile/tumblrparam.txt").get(0));
        crawler.getTumblrByTag("gif", "19920404144444");
    }

    /**
     * 按标签获取数据
     * @param tag
     * @param startTime
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     * @throws JSONException
     * @throws ParseException
     */
    public Map<String, Object> getTumblrByTag(String tag, String startTime) throws InterruptedException, ExecutionException, IOException, JSONException, ParseException {
        Map<String, String> params = new HashMap<>();
        params.put("tag", tag);

        String url = new String("https://api.tumblr.com/v2/tagged");

        HttpResponse response = TumblrOAuth.getTumblrResponse(url, params);

//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH：mm：ss");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//        String startTime = df.format(new Date());
        String colname = "tumblr_" + startTime + "_data";
        String filePath_data = path + "outputfile/" + colname + ".txt";
//        String filePath_test = TumblrCrawler.class.getClassLoader().getResource("/").getPath() + "outputfile/tumblr_" + "test_" + startTime + ".txt";

        HttpEntity entity = response.getEntity();

        Map<String, Object> results = new HashMap<>();
        if(response.getStatusLine().getStatusCode() != 200) {
            System.out.println("response error.");
            return results;
        }
        InputStreamReader streamReader = new InputStreamReader(entity.getContent());
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        StringBuffer buffer = new StringBuffer();
        String data = null;
        while((data = bufferedReader.readLine()) != null) {
            buffer.append(data);
        }


        JSONObject jsonObject = new JSONObject(buffer.toString());

        String stat = jsonObject.getJSONObject("meta").getString("msg");


        if(stat.toLowerCase().equals("ok") == false) {
            System.out.println("http response wrong. Stat is not ok.");
            return results;
        }

        JSONArray tumblrs = jsonObject.getJSONArray("response");

        BufferedWriter bufferedWriter = null;
        MongoCollection<Document> collection = getMongoCollection(this.dbName, colname);

        int count = 0;
        int file_size = 0;
        try{
            bufferedWriter = FileSystem.getBufferedWriterByPath(filePath_data);
            for(int i = 0; i < tumblrs.length(); i++){
                JSONObject tumblr = tumblrs.getJSONObject(i);
                JSONArray photos = tumblr.getJSONArray("photos");
                String url_photo = null;

                bufferedWriter.write(tumblr.toString());
                bufferedWriter.newLine();

                Document document = Document.parse(tumblr.toString());
                for(int j = 0; j < photos.length(); j++) {
                    try{
                        url_photo = photos.getJSONObject(j).getJSONObject("original_size").getString("url");
                    }catch (Exception e) {
                        continue;
                    }
                    byte[] photoBinary = null;

                    try{
                        photoBinary = getPicByUrl(url_photo);
                    }catch (Exception e) {
                        System.out.println("photo:" + tumblr.getString("blog_name") + " cannot get. Skip.");
                        continue;
                    }

                    String[] splits = url_photo.split("\\.");
                    String pic_title = tumblr.getString("id") + "." + splits[splits.length - 1];

                    FileOutputStream outputStream = new FileOutputStream(FileSystem.getFileByPath(path + "outputfile/" + pic_title));
                    outputStream.write(photoBinary);
                    outputStream.close();

                    document.put("img" + j + "_byte", url_photo);

                    count++;
                    file_size += photoBinary.length;
                }



                collection.insertOne(document);

                count++;

            }
        }finally {
            bufferedWriter.close();
        }

        long fileLength = FileSystem.getFileSizeByPath(filePath_data) + file_size;
        String endTime = df.format(new Date());
        String fileSize = 1.0 * fileLength/(1024*1024) + "MB";

        Date start = df.parse(startTime);
        Date end = df.parse(endTime);
        Double duration = (end.getTime() - start.getTime())/1000.0;
//        params.clear();
//        params.put("startTime", startTime);
//        params.put("endTime", endTime);
//        params.put("duration", duration.toString());
//        params.put("get Tumblr Number:", count + "");
//        params.put("get file Size:", fileSize);
//
//        FileSystem.writeParamsToFile(filePath_test, params);
        results.put("dataCount", count);
        results.put("duration", duration);
        results.put("dataSize", fileSize);
        results.put("fileName", colname);

        return results;


    }

    /**
     *
     * @param url 图片所在url
     * @return    图片二进制数据
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     */
    public byte[] getPicByUrl(String url) throws InterruptedException, ExecutionException, IOException {
        HttpResponse response = TumblrOAuth.getTumblrResponse(url, new HashMap<>());
        HttpEntity entity = response.getEntity();
        if(response.getStatusLine().getStatusCode() != 200) throw new IOException();

        int count = (int)entity.getContentLength();
        if(count == 0) throw new IOException();


        BufferedInputStream bufferedInputStream = new BufferedInputStream(entity.getContent());
        byte[] buffer = new byte[count];

        int readCount = 0;
        while(readCount <= count) {
            if(readCount == count) break;
            readCount += bufferedInputStream.read(buffer, readCount, count - readCount);
        }
        return buffer;
    }

    //测试。 TODO
    @Override
    public Map<String, Object> getDataByLocation(int west_long, int south_lat, int east_long, int north_lat, Map<String, Object> others) throws Exception {
        if (others.containsKey("path")) {
            path = (String)others.get("path");
        } else {
            path = "";
        }
        return getTumblrByTag((String)others.get("tag"), (String)others.get("startTime"));
    }

    @Override
    public String getCrawlerName() {
        return "Tumblr";
    }

    @Override
    public Map<String, Object> getDataByKeyword(String keyword, Map<String, Object> others) throws Exception {
        if (others.containsKey("path")) {
            path = (String)others.get("path");
        } else {
            path = "";
        }
        return getTumblrByTag((String) others.get("tag"), (String) others.get("startTime"));

    }


}

