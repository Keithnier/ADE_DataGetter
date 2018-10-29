package DataCrawler.Twitter;

import com.github.scribejava.core.model.Response;
import com.mongodb.client.MongoCollection;
import DataCrawler.model.Crawler;
import DataCrawler.model.RestParam;
import org.bson.Document;
import DataCrawler.util.FileSystem;
import DataCrawler.util.MongoDB;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 *
 * description： 1.运用RestApi 爬取tweets
 *
 * fields：  tweetsCount 单次请求获取Tweets的条数
 *
 * methods:  getTweetsByLocation          根据地理位置信息获取实时Tweets
 *
 *
 */
public class TwitterCrawler implements Crawler{

    public int tweetsCount = 0;
    public final String dbName = "TwitterData";

    private RestParam param;
    private static String path;

    /**
     * 构造函数
     * @param param         Twitter账户信息模版， 包含使用Twitter Rest Api 需要的信息
     * @param tweetsCount   单次请求获取的Tweets条数
     */
    public TwitterCrawler(RestParam param, int tweetsCount) throws InterruptedException, ExecutionException, IOException {

        if(TwitterOAuth.isTokenInit() == false) {
            TwitterOAuth.initTwitterAccessToken(param);
        }
        this.param = param;
        this.tweetsCount = tweetsCount;

    }


    /**
     * 获取Tweets
     * @param params 参数
     * @param startTime 开始时间
     * @param url Api
     * @return 爬去数据的条数、大小等信息，用于返回给前端
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     * @throws ParseException
     */
    public Map<String, Object> getTweets(Map<String, String> params, String startTime, String url) throws InterruptedException, ExecutionException, IOException, ParseException {
//        String url = "https://stream.twitter.com/1.1/statuses/filter.json";
//        String locations = west_long + "," + south_lat + "," + east_long + "," + north_lat;
//        Map<String, String> params = new HashMap<>();
//        params.put("language","en");
//        params.put("locations", locations);

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

        String colname = "twitter_" + startTime + "_data";
        String filePath_data = path + "outputfile/" + colname + ".txt";

        Response response = TwitterOAuth.getTwitterResponse(url, params);

        Map<String, Object> results = new HashMap<>();
        if(response.isSuccessful() == false) {
            System.out.println("TwitterRestApi Error:[url:" + url + "] [responseCode:" + response.getCode() + "]");
            if(response.getCode() == 420) {
                System.out.println("Error: 420");
                Thread.sleep(10000);
            }else if(response.getCode() == 401) {
                System.out.println("Twitter OAuth verify again.");
                TwitterOAuth.initTwitterAccessToken(param);
            }else if (response.getCode() == 408) {
                System.out.println("Twitter API Time out.");
            }

            return results;
        }else if (response.isSuccessful() == true){
            System.out.println("Connect Successful!");
        }

//        long fileLength = FileSystem.writeTextStreamToFile(filePath_data, response.getStream(), tweetsCount);


        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        MongoCollection<Document> collection;//mongodb

        int counts = tweetsCount;
        try {
            bufferedReader = getBufferedReaderByStream(response.getStream());
            bufferedWriter = FileSystem.getBufferedWriterByPath(filePath_data);
            collection = MongoDB.getMongoCollection(this.dbName, colname);//mongodb

            while(counts > 0) {
                String json = bufferedReader.readLine();

                if(json == null) {
                    break;
                }

                if(json.equals("")) continue;

                if(json.length() > 15000000) continue;

                bufferedWriter.write(json);
                bufferedWriter.newLine();

                MongoDB.writeJson2Collection(collection, json);//mongodb
                if (counts % 10 == 0){
                    System.out.print("|" + counts);
                }
                counts--;
            }
        } finally {
            bufferedReader.close();
            bufferedWriter.close();
        }

        long fileLength = FileSystem.getFileSizeByPath(filePath_data);
        String endTime = df.format(new Date());

        Date start = df.parse(startTime);
        Date end = df.parse(endTime);
        Double duration = (end.getTime() - start.getTime())/1000.0;

        String fileSize = 1.0 * fileLength/(1024*1024) + "MB";
//        params.clear();
//        params.put("startTime", startTime);
//        params.put("endTime", endTime);
//        params.put("duration", duration.toString());
////        params.put("get Tweets Number:", tweetsCount + "");
//        params.put("get file Size:", fileSize);
//
//        FileSystem.writeParamsToFile(filePath_test, params);

        results.put("dataCount", tweetsCount - counts);
        results.put("duration", duration);
        results.put("dataSize", fileSize);
        results.put("fileName", colname);

        //释放内存
        response = null;

//        System.out.println("dataCount: " + (tweetsCount - counts) + ", duration: " + duration + ", dataSize: " + fileSize + ", fileName: " + colname);

        return results;

    }


    private BufferedReader getBufferedReaderByStream(InputStream stream) {
        InputStreamReader inputStreamReader = new InputStreamReader(stream);
        return new BufferedReader(inputStreamReader);
    }

    /**
     * 准备按区域爬取的参数
     * @param west_long
     * @param south_lat
     * @param east_long
     * @param north_lat
     * @return
     */
    private Map<String, String> prepareParamsForRegion(int west_long,int south_lat, int east_long, int north_lat) {
        String url = "https://stream.twitter.com/1.1/statuses/filter.json";
        String locations = west_long + "," + south_lat + "," + east_long + "," + north_lat;
        Map<String, String> params = new HashMap<>();
        params.put("language","en");
        params.put("locations", locations);
        params.put("URL", url);

        return params;
    }

    /**
     * 准备按关键字爬取的参数
     * @param keyword
     * @return
     */
    private Map<String, String> prepareParamsForKeyword(String keyword) {
        Map<String, String> params = prepareParamsForRegion(-180, -90, 180, 90);
        keyword = keyword.replaceAll(",", " ") //英文,
                .replaceAll("，", " ") //中文,
                .replaceAll("\\|", ",");
        params.put("track", keyword);
        params.remove("locations");

        return params;
    }



    @Override
    public Map<String, Object> getDataByLocation(int west_long, int south_lat, int east_long, int north_lat, Map<String, Object> others) throws Exception {
        if (others.containsKey("path")) {
            path = (String)others.get("path");
        } else {
            path = "";
        }

        Map<String, String> params = prepareParamsForRegion(west_long, south_lat, east_long, north_lat);

        return getTweets(params, (String) others.get("startTime"), params.get("URL"));
    }

    public void getDataByLocation(int west_long, int south_lat, int east_long, int north_lat, Map<String, Object> others, int round) throws Exception {
        Map<String, Object> results = new HashMap<>();
        results = getDataByLocation(-180, -90, 180, 90, others);
        System.out.println("Round :" + round + ", dataCount: " + results.get("dataCount") + ", duration: " + results.get("duration") + ", dataSize: " + results.get("dataSize") + ", fileName: " + results.get("fileName"));
    }

    @Override
    public String getCrawlerName() {
        return "Twitter";
    }

    @Override
    public Map<String, Object> getDataByKeyword(String keyword, Map<String, Object> others) throws Exception {
        if (others.containsKey("path")) {
            path = (String)others.get("path");
        } else {
            path = "";
        }

        Map<String, String> params = prepareParamsForKeyword(keyword);

        return getTweets(params, (String) others.get("startTime"), params.get("URL"));
    }


}