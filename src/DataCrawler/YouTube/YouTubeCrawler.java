package DataCrawler.YouTube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.Joiner;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.mongodb.client.MongoCollection;
import DataCrawler.model.Crawler;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.bson.Document;
import DataCrawler.util.FileSystem;
import static CommonUtil.MongoDB.DataBaseUtil.getMongoCollection;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Youtube 爬虫
 * 使用官方提供的jar包
 * 储存视频相关信息，爬取视频的封面。
 */
public class YouTubeCrawler implements Crawler{


    private String apiKey;
    private long youtubeCount;
    public static final String dbName = "YoutubeData";
    private static YouTube youtube;
    private static String path;

    /**
     * 构造函数
     */
    public YouTubeCrawler(String apiKey, int youtubeCount) throws InterruptedException, ExecutionException, IOException {

        this.apiKey = apiKey;
        this.youtubeCount = youtubeCount;

    }


    public  Map<String, Object> getYoutubeByLocation(String location, String locationRadius, String queryTerm, String startTime) throws IOException {

//        System.out.println("api_key:" + properties.getProperty("youtube.apikey"));

        List<String> scopes = new ArrayList();

        scopes.add("https://www.googleapis.com/auth/youtube");

//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH：mm：ss");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String colname = "youtube_" + startTime + "_data";
        String filePath_data = path + "DataCrawler/outputfile/" + colname + ".txt";

//        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        MongoCollection<Document> collection;

        Map<String, Object> results = new HashMap<>();



        try {

            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            Credential credential = YouTubeOAuth.authorize(scopes, "videolist", path);
            youtube = new YouTube.Builder(YouTubeOAuth.HTTP_TRANSPORT, YouTubeOAuth.JSON_FACTORY, credential).setApplicationName("DataGetter").build();

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the {{ Google Cloud Console }} for
            // non-authenticated requests. See:
            // {{ https://cloud.google.com/console }}
            search.setKey(apiKey);
            search.setQ(queryTerm);
            if(location.equals("") == false) {
                search.setLocation(location);
                search.setLocationRadius(locationRadius);
            }

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // As a best practice, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/videoId)");
            search.setMaxResults(youtubeCount);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            List<String> videoIds = new ArrayList<>();

            if (searchResultList != null) {

                // Merge video IDs
                for (SearchResult searchResult : searchResultList) {
//                    System.out.println(searchResult.getId().getVideoId());
                    videoIds.add(searchResult.getId().getVideoId());

                }
                Joiner stringJoiner = Joiner.on(',');
                String videoId = stringJoiner.join(videoIds);

                // Call the YouTube Data API's youtube.videos.list method to
                // retrieve the resources that represent the specified videos.
                YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet, recordingDetails").setId(videoId);
                VideoListResponse listResponse = listVideosRequest.execute();

                List<Video> videoList = listResponse.getItems();

                bufferedWriter = FileSystem.getBufferedWriterByPath(filePath_data);
                collection = getMongoCollection(YouTubeCrawler.dbName, colname);

                int file_size = 0;
                int dataCount = 0;

                if (videoList != null) {

                    Iterator<Video> videos = videoList.iterator();
                    while(videos.hasNext()) {
                        Video video = videos.next();
                        String thumbnailUrl = video.getSnippet().getThumbnails().getHigh().getUrl();
                        GeoPoint locationResult = video.getRecordingDetails().getLocation();
                        byte[] photoBinary = null;
                        try{
                            photoBinary = getPicByUrl(thumbnailUrl);
                        }catch (Exception e) {
                            System.out.println("thumbnail:" +videos.next().getSnippet().getTitle()  + " cannot get. Skip.");
                            continue;
                        }

                        String[] splits = thumbnailUrl.split("\\.");
                        String pic_title = "youtube_" + video.getId() + "." + splits[splits.length - 1];
                        file_size += photoBinary.length;

                        FileOutputStream outputStream = new FileOutputStream(FileSystem.getFileByPath(path + "DataCrawler/outputfile/" + pic_title));
                        outputStream.write(photoBinary);
                        outputStream.close();

                        bufferedWriter.write(video.toPrettyString());
                        bufferedWriter.newLine();

                        Document document = Document.parse(video.toPrettyString());
                        document.put("img", thumbnailUrl);
                        if (locationResult != null){
                            document.put("location", locationResult.getLongitude() + ", " + locationResult.getLatitude());
                        }
                        collection.insertOne(document);
                        dataCount++;

                    }
                }

                long fileLength = FileSystem.getFileSizeByPath(filePath_data) + file_size;
                String endTime = df.format(new Date());
                String fileSize = 1.0 * fileLength/(1024*1024) + "MB";

                Date start = df.parse(startTime);
                Date end = df.parse(endTime);
                Double duration = (end.getTime() - start.getTime())/1000.0;
//                Map<String, String> params = new HashMap<>();
//                params.put("startTime", startTime);
//                params.put("endTime", endTime);
//                params.put("duration", duration.toString());
//                params.put("get Youtube Number:", NUMBER_OF_VIDEOS_RETURNED + "");
//                params.put("get file Size:", fileSize);
//
//                FileSystem.writeParamsToFile(filePath_test, params);

                results.put("dataCount", dataCount);
                results.put("duration", duration);
                results.put("dataSize", fileSize);
                results.put("fileName", colname);

                System.out.println("dataCount:" + dataCount + ", duration:" + duration + ", dataSize:" + fileSize + ", fileName:" + colname);

                return results;

            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
        }

        return results;
    }

    public byte[] getPicByUrl(String url) throws InterruptedException, ExecutionException, IOException {
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(httpGet);
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

//    public static void main(String[] args) throws IOException {
//        new YouTubeCrawler().getYoutubeByLocation("37,-122", "100km, 8mi", "cat", "19920404144444");
//    }


    //TODO

    @Override
    public Map<String, Object> getDataByLocation(int west_long, int south_lat, int east_long, int north_lat, Map<String, Object> others) throws Exception {
        if (others.containsKey("path")) {
            path = (String)others.get("path");
        } else {
            path = "";
        }
        int location_long = (west_long + east_long) / 2;
        int location_lat = (south_lat + north_lat) / 2;
        int location_Radius = (int) getDistance(south_lat, west_long, location_lat, location_long) / 1000;

        if(location_long == 0 || location_lat == 0){
            return getYoutubeByLocation("37.42307,-122.08427", "5km", "Youtube", (String)others.get("startTime"));
        }
        return getYoutubeByLocation(location_long + "," + location_lat, location_Radius + "km", "Youtube", (String)others.get("startTime"));
    }

    @Override
    public String getCrawlerName() {
        return "Youtube";
    }

    @Override
    public Map<String, Object> getDataByKeyword(String keyword, Map<String, Object> others) throws Exception {
        if (others.containsKey("path")) {
            path = (String)others.get("path");
        } else {
            path = "";
        }
        keyword = keyword.replaceAll(",", " ")
                .replaceAll("，", " ");
        return getYoutubeByLocation("37.42307,-122.08427", "5km", keyword, (String)others.get("startTime"));

    }


    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 通过经纬度获取距离(单位：米)
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s*1000;
        return s;
    }
}
