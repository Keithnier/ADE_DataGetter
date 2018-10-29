package DataCrawler.servlet;

import DataCrawler.WebService.CrawlerService;
import twitter4j.JSONException;
import twitter4j.JSONObject;

import java.io.IOException;
import java.util.*;


/**
 * 开始爬取的Servlet
 */
public class DataGetServlet extends javax.servlet.http.HttpServlet {

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        String result="";

        String option=request.getParameter("option");
        String time=request.getParameter("time");
        String crawlerType = request.getParameter("crawlerType");
        String paramString = request.getParameter("params");
        System.out.println(paramString);
        JSONObject paramJson = null;
        try {
            paramJson = new JSONObject(paramString);
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("param is not json");
            response.getWriter().print("false");
            return;
        }

        Map<String, Object> params = new HashMap<>();
        Iterator iterator = paramJson.keys();
        while(iterator.hasNext()) {
            String key = (String)iterator.next();
            try {
                params.put(key, paramJson.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("parse key[" + key + "] exception.");
            }
        }

        List<String> crawlerNames = new ArrayList<>();
        if(request.getParameter("Twitter").toLowerCase().equals("true")) crawlerNames.add("Twitter");
        if(request.getParameter("Flickr").toLowerCase().equals("true")) crawlerNames.add("Flickr");
        if(request.getParameter("Tumblr").toLowerCase().equals("true")) crawlerNames.add("Tumblr");
        if(request.getParameter("YouTube").toLowerCase().equals("true")) crawlerNames.add("Youtube");



        System.out.println("option："+option);
        System.out.println("time："+time);
        System.out.println("crawlerType: " + crawlerType);
        for(String crawlerName : crawlerNames) {
            System.out.println("Crawler："+crawlerName);
        }

        if(option.equals("start")){
            //开始抓取
            System.out.println("start");
            startCrawler(crawlerNames, crawlerType, time, params);
            response.getWriter().print("true");
        }
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }

    private void startCrawler(List<String> crawlers, String crawlerType, String time, Map<String, Object> params) {
        for(String crawlerName : crawlers) {
            CrawlerService.startCrawler(crawlerName, crawlerType, params);
        }
    }





}
