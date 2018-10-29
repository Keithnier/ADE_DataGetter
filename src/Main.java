import DataCrawler.Flickr.FlickrCrawler;
import DataCrawler.Twitter.TwitterCrawler;
import DataCrawler.YouTube.YouTubeCrawler;
import DataCrawler.util.ConfigurationFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
//
public class Main {
    //flickr param file path
    private static final String flickrParamFilePath = "paramfile/flickrparam.txt";
    //twitter param file path
    private static final String twitterParamFilePath = "paramfile/twitterparam.txt";
    //twitter rest param file path
    private static final String twitterRestParamFilePath = "paramfile/twitterrestparam.txt";
    //youtube param file path
    private static final String youtubeParamFilePath = "paramfile/youtubeparam.txt";

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the platform and time(minutes) you want the crawlers execute:");
        String duration = scanner.nextLine();

        float time2last = Float.parseFloat(duration) * 60 * 1000;

        //1.get twitter data by rest
//        getTwitterByRest(time2last);//1
        //2.get flickr data by rest
        getFlickrByRest(time2last);//2
        //3.get youtube data by rest
//        getYoutubeByRest(time2last);//3


    }

    /**
     * get twitter data by rest API
     * @param time2last
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     */
    public static void getTwitterByRest(float time2last) throws InterruptedException, ExecutionException, IOException {

        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        int round = 1;
        while((endTime - startTime) < time2last) {

            try {
                TwitterCrawler twitterCrawler = new TwitterCrawler(ConfigurationFactory.getRestParam(twitterRestParamFilePath).get(0), 1000);
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                String start = df.format(new Date());
                Map<String, Object> params = new HashMap<>();
                params.put("startTime", start);
                twitterCrawler.getDataByLocation(-180, -90, 180, 90, params,round);
                round++;
            } catch (Exception e) {
                e.printStackTrace();
            }
            endTime = System.currentTimeMillis();
        }

        System.out.println("===== End! ======");
    }

    /**
     * get flickr data by rest API
     * @param time2last
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     */
    public static void getFlickrByRest(float time2last) throws InterruptedException, ExecutionException, IOException {
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        while((endTime - startTime) < time2last) {

            try {
                FlickrCrawler flickrCrawler = new FlickrCrawler(ConfigurationFactory.getRestParam(flickrParamFilePath).get(0));
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                String start = df.format(new Date());
                Map<String, Object> params = new HashMap<>();
                params.put("startTime", start);
                flickrCrawler.getDataByLocation(-180, -90, 180, 90, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
            endTime = System.currentTimeMillis();
        }
    }

    /**
     * get youtube data by rest API
     * @param time2last
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     */
    public static void getYoutubeByRest(float time2last) throws InterruptedException, ExecutionException, IOException {
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        int round = 1;
        while((endTime - startTime) < time2last) {

            try {
                YouTubeCrawler youtubeCrawler = new YouTubeCrawler(ConfigurationFactory.getYouTubeRestParam(youtubeParamFilePath),50);
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                String start = df.format(new Date());
                Map<String, Object> params = new HashMap<>();
                params.put("startTime", start);
                youtubeCrawler.getDataByLocation(-180, -90, 180, 90, params);
                round++;
            } catch (Exception e) {
                e.printStackTrace();
            }
            endTime = System.currentTimeMillis();
        }

        System.out.println("===== End! ======");
    }

}
