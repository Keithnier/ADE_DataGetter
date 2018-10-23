package WebService;

import Flickr.FlickrCrawler;
import Tumblr.TumblrCrawler;
import Twitter.TwitterCrawler;
import YouTube.YouTubeCrawler;
import model.Crawler;
import twitter4j.JSONObject;
import util.ConfigurationFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 爬虫服务中心。
 * 初始化爬虫。持有爬虫控制器。对所有爬虫进行统一管理。
 */
public class CrawlerService {
    //flickr param file path
    private static final String flickrParamFilePath = CrawlerService.class.getClassLoader().getResource("/paramfile/flickrparam.txt").getPath();

    //twitter rest param file path
    private static final String twitterRestParamFilePath = CrawlerService.class.getClassLoader().getResource("/paramfile/twitterrestparam.txt").getPath();

    private static final String tumblrParamFilePath = CrawlerService.class.getClassLoader().getResource("/paramfile/tumblrparam.txt").getPath();

    private static final String youtubeParamFilePath = CrawlerService.class.getClassLoader().getResource("/paramfile/youtubeparam.txt").getPath();

    private static ExecutorService executorService = Executors.newFixedThreadPool(4);

    private static Map<String, CrawlerControler> crawlerMap = new HashMap<>();

    private static Crawler twitterCrawler;

    private static Crawler flickrCrawler;

    private static Crawler tumblrCrawler;

    private static Crawler youtubeCrawler;


    public static void initCrawler() {
        System.out.println(twitterRestParamFilePath);
        try {
            twitterCrawler = new TwitterCrawler(
                    ConfigurationFactory.getRestParam(twitterRestParamFilePath).get(0)
                    , 200);

           /* flickrCrawler = new FlickrCrawler(ConfigurationFactory.getRestParam(flickrParamFilePath).get(0));*/

            tumblrCrawler = new TumblrCrawler(ConfigurationFactory.getRestParam(tumblrParamFilePath).get(0));

            youtubeCrawler = new YouTubeCrawler(ConfigurationFactory.getYouTubeRestParam(youtubeParamFilePath),50);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        crawlerMap.put("Twitter", new CrawlerControler(executorService, twitterCrawler, "Location", new HashMap<>()));
       /* crawlerMap.put("Flickr", new CrawlerControler(executorService, flickrCrawler, "Location", new HashMap<>()));*/
        crawlerMap.put("Tumblr", new CrawlerControler(executorService, tumblrCrawler, "Location", new HashMap<>()));
        crawlerMap.put("Youtube", new CrawlerControler(executorService, youtubeCrawler, "Location", new HashMap<>()));

        System.out.println("Crawler Init success");

    }

    public static void startCrawler(String crawler, String crawlerType, Map<String, Object> params) {

        crawlerMap.get(crawler).setCrawlerType(crawlerType);
        crawlerMap.get(crawler).setParams(params);

        crawlerMap.get(crawler).startCrawler();

    }

    public static String getCrawlerStatus(String crawler) {
        return new JSONObject(crawlerMap.get(crawler).getStatus()).toString();
    }

    public static void pauseCrawler(String crawler){
        crawlerMap.get(crawler).pauseCrawler();
    }

    public static void stopCrawler(String crawler) {
        crawlerMap.get(crawler).stopCrawler();
    }


    public static void destroyThreadPool(){
        executorService.shutdownNow();
    }

}
