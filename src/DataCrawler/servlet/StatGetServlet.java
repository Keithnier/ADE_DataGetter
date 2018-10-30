package DataCrawler.servlet;

import DataCrawler.WebService.CrawlerService;
import twitter4j.JSONObject;
import static CommonUtil.MongoDB.DataBaseUtil.getColletcionNamesInDB;
import static CommonUtil.MongoDB.Read.getCountDataInCollection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取爬虫状态的servlet
 */
public class StatGetServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        List<String> crawlerNames = new ArrayList<>();
        if (request.getParameter("Twitter").toLowerCase().equals("true")) crawlerNames.add("Twitter");
        if (request.getParameter("Flickr").toLowerCase().equals("true")) crawlerNames.add("Flickr");
        if (request.getParameter("Tumblr").toLowerCase().equals("true")) crawlerNames.add("Tumblr");
        if (request.getParameter("YouTube").toLowerCase().equals("true")) crawlerNames.add("Youtube");

        //显示爬取数据状态
//         response.getWriter().print(getCrawlerStatus(crawlerNames));

        //显示爬取数据内容
        String result = getCrawlerData(crawlerNames);

        response.getWriter().print(result);
    }

    private String getCrawlerStatus(List<String> crawlers) {
        Map<String, Object> results = new HashMap<>();

        for(String crawlerName : crawlers) {
            results.put(crawlerName, CrawlerService.getCrawlerStatus(crawlerName));
        }

        return new JSONObject(results).toString();
    }

    private String getCrawlerData(List<String> crawlerNames){
        String result = "";
        if (crawlerNames.contains("Twitter")) {
            List<String> collectionName = getColletcionNamesInDB("TwitterData");
            if(collectionName.size()!=0){
                List<String> twitterData  = getCountDataInCollection("TwitterData",collectionName.get(collectionName.size()-1),1);
                if (twitterData.size()!=0) result = result + twitterData.toString() + "\r\n";
            }
        }
        if (crawlerNames.contains("Flickr")) {
            List<String> collectionName = getColletcionNamesInDB("FlickrData");
            if(collectionName.size()!=0){
                List<String> flickrData  = getCountDataInCollection("FlickrData",collectionName.get(collectionName.size()-1),1);
                if (flickrData.size()!=0) result = result + flickrData.toString() + "\r\n";
            }
        }
        if (crawlerNames.contains("Youtube")) {
            List<String> collectionName = getColletcionNamesInDB("YoutubeData");
            if(collectionName.size()!=0){
                List<String> youtubeData  = getCountDataInCollection("YoutubeData",collectionName.get(collectionName.size()-1),1);
                if (youtubeData.size()!=0) result = result + youtubeData.toString() + "\r\n";
            }
        }
        if (crawlerNames.contains("Tumblr")) {
            List<String> collectionName = getColletcionNamesInDB("TumblrData");
            if(collectionName.size()!=0){
                List<String> tumblrData  = getCountDataInCollection("TumblrData",collectionName.get(collectionName.size()-1),1);
                if (tumblrData.size()!=0) result = result + tumblrData.toString() + "\r\n";
            }
        }
        return result;
    }

}
