package DataCrawler.Flickr;

import com.github.scribejava.core.model.Response;
import DataCrawler.model.Crawler;
import DataCrawler.model.RestParam;
import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import DataCrawler.util.FileSystem;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 *
 * description： 运用RestApi 爬取Flickr用户发布的图片
 *
 * fields：  dbName  MongDB中Collection的名称
 *
 * methods:  getFlickrByLocation   根据地理位置信息获取Flickr内容
 *
 *
 */
public class FlickrCrawler implements Crawler{
    public final String dbName = "FlickrData";
    private String path;

    /**
     *
     * @param param 用户信息模版
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     */
    public FlickrCrawler(RestParam param) throws InterruptedException, ExecutionException, IOException {
        if(FlickrOAuth.isTokenInit() == false) {
            FlickrOAuth.initFlickrAccessToken(param);
        }
    }


    /**
     *
     * @param params 参数
     * @param startTime 开始时间
     * @param URL Api地址
     * @return results Map中存有信息个数，数据大小等信息
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     * @throws JSONException
     * @throws ParseException
     */

    public Map<String, Object> getFlickr(Map<String, String> params, String startTime, String URL) throws InterruptedException, ExecutionException, IOException, JSONException, ParseException {
//        String URL = "https://api.flickr.com/services/rest";
//        Map<String, String> params = new HashMap<>();
//        String locations = west_long + "," + south_lat + "," + east_long + "," + north_lat;
//        params.put("method", "flickr.photos.search");
//        params.put("min_upload_date", String.valueOf(min_time_upload));
//        params.put("max_upload_date", String.valueOf(max_time_upload));
//        params.put("bbox", locations);
//        params.put("extras", "geo,original_format,url_o");
//        params.put("format", "json");
//        params.put("nojsoncallback", "1");
//        params.put("per_page", String.valueOf(photo_count));

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        //构造文件名和存储路径
        String colname = "flickr_" + startTime + "_data";
        String filePath_data =  path + "outputfile/" + colname + ".txt";


        Map<String, Object> results = new HashMap<>();
        Response response = FlickrOAuth.getFlickrResponse(URL, params);
        //返回成功判断
        if(response.isSuccessful() == false) {
            System.out.println("response error.");
            return results;
        }
        //response body内容转json
        JSONObject jsonObject = new JSONObject(response.getBody());

        String stat = jsonObject.getString("stat");
        //数据状态正常与否的判断
        if(stat.equals("ok") == false) {
            System.out.println("http response wrong. Stat is not ok.");
            return results;
        }
        //Json嵌套
        JSONObject photosInfo = jsonObject.getJSONObject("photos");
        //理解为key-photo对应的是一个照片信息数组[{},{},{}]
        JSONArray photos = photosInfo.getJSONArray("photo");

        BufferedWriter bufferedWriter = null;
//        MongoCollection<Document> collection = MongoDB.getMongoCollection(this.dbName, colname);//mongoDB

        int count = 0;
        int file_size = 0;
        try{
            bufferedWriter = FileSystem.getBufferedWriterByPath(filePath_data);
            for(int i = 0; i < photos.length(); i++){
                //获取单个照片信息
                JSONObject photo = photos.getJSONObject(i);
                String url = null;
                //图片链接地址
                try{
                    url = photo.getString("url_o");
                }catch (Exception e) {
                    continue;
                }


                byte[] photoBinary = null;
                try{
                    //以二进制形式返回图片信息
                    photoBinary = getPicByUrl(url);
                }catch (Exception e) {
                    System.out.println("photo:" + photo.getString("title") + " cannot get. Skip.");
                    continue;
                }
                //id.jpg
                String pic_title = photo.getString("id") + "." + photo.getString("originalformat");
                //在对应路径存储照片，会自动新建文件
                FileOutputStream outputStream = new FileOutputStream(FileSystem.getFileByPath( path + "outputfile/" + pic_title));
                outputStream.write(photoBinary);
                outputStream.close();
                //仅存储照片信息
                bufferedWriter.write(photo.toString());
                bufferedWriter.newLine();

//                Document document = Document.parse(photo.toString());//mongoDB
//                document.put("imgURL", url);//mongoDB
//                collection.insertOne(document);//mongoDB
                //照片数目加一，file_size存储的是照片本身的大小
                count++;
                file_size += photoBinary.length;

            }
        }finally {
            bufferedWriter.close();
        }
        //两者大小之和
        long fileLength = FileSystem.getFileSizeByPath(filePath_data) + file_size;
        String endTime = df.format(new Date());
        String fileSize = 1.0 * fileLength/(1024*1024) + "MB";

        Date start = df.parse(startTime);
        Date end = df.parse(endTime);
        Double duration = (end.getTime() - start.getTime())/1000.0;

        results.put("dataCount", count);
        results.put("duration", duration);
        results.put("dataSize", fileSize);
        results.put("fileName", colname);

        System.out.println("dataCount:" + count + ", duration:" + duration + ", dataSize:" + fileSize + ", fileName:" + colname);

        return results;


    }


    /**
     * 准备按区域爬数据的参数
     * @param west_long 西经
     * @param south_lat 南纬
     * @param east_long 东经
     * @param north_lat 北纬
     * @param min_time_upload 最早上传时间
     * @param max_time_upload 最晚上传时间
     * @param photo_count 一次爬照片最大个数
     * @return 参数
     */
    private Map<String, String> prepareParamsForRegion(int west_long, int south_lat, int east_long, int north_lat, long min_time_upload, long max_time_upload, int photo_count) {
        String URL = "https://api.flickr.com/services/rest";
        Map<String, String> params = new HashMap<>();
        String locations = west_long + "," + south_lat + "," + east_long + "," + north_lat;
        params.put("method", "flickr.photos.search");
        params.put("min_upload_date", String.valueOf(min_time_upload));
        params.put("max_upload_date", String.valueOf(max_time_upload));
        params.put("bbox", locations);
        params.put("extras", "geo,original_format,url_o,date_upload,date_taken,last_update");
        params.put("format", "json");
        params.put("nojsoncallback", "1");
        params.put("per_page", String.valueOf(photo_count));
        params.put("URL", URL);
        return params;
    }

    /**
     * 准备按关键字爬数据的参数
     * @param min_time_upload 最早上传时间
     * @param max_time_upload 最晚上传时间
     * @param photo_count 一次爬照片最大个数
     * @param keyword 关键字
     * @return
     */
    private Map<String, String> prepareParamsForKeyword(long min_time_upload, long max_time_upload, int photo_count, String keyword){
        Map<String, String> params = prepareParamsForRegion(-180,-90,180,90, min_time_upload, max_time_upload, photo_count);
        params.put("text", keyword);

        return params;

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
        Response response = FlickrOAuth.getFlickrResponse(url, new HashMap<>());
        if(response.isSuccessful() == false) throw new IOException();

        int count = Integer.parseInt(response.getHeader("Content-Length"));
        if(count == 0) throw new IOException();

        BufferedInputStream bufferedInputStream = new BufferedInputStream(response.getStream());
        byte[] buffer = new byte[count];

        int readCount = 0;
        while(readCount <= count) {
            if(readCount == count) break;
            readCount += bufferedInputStream.read(buffer, readCount, count - readCount);
        }
        return buffer;
    }

    @Override
    public Map<String, Object> getDataByLocation(int west_long, int south_lat, int east_long, int north_lat, Map<String, Object> others) throws Exception{
        if (others.containsKey("path")) {
            path = (String)others.get("path");
        } else {
            path = "";
        }
        long max_time_upload = System.currentTimeMillis();
        long min_time_upload = max_time_upload - 3600000;
        int photo_count = 1000;

        Map<String, String> params = prepareParamsForRegion(west_long, south_lat, east_long, north_lat, min_time_upload, max_time_upload, photo_count);
        return getFlickr(params, (String)others.get("startTime"), params.get("URL"));
    }

    @Override
    public String getCrawlerName() {
        return "Flickr";
    }

    @Override
    public Map<String, Object> getDataByKeyword(String keyword, Map<String, Object> others) throws Exception{
        if (others.containsKey("path")) {
            path = (String)others.get("path");
        } else {
            path = "";
        }
        long max_time_upload = System.currentTimeMillis();
        long min_time_upload = max_time_upload - 600000;
        int photo_count = 1000;

        Map<String, String> params = prepareParamsForKeyword(min_time_upload, max_time_upload, photo_count, keyword);
        return getFlickr(params, (String)others.get("startTime"), params.get("URL"));
    }
}
