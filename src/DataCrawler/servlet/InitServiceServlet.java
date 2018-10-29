package DataCrawler.servlet;

import DataCrawler.WebService.CrawlerService;

import javax.servlet.http.HttpServlet;

/**
 * 初始化Servlet.因为初始化爬虫需要较长时间，因此放在这里，开始爬取以前提前初始化。
 */
public class InitServiceServlet extends HttpServlet {

    public  void init(){
        System.out.println("Init service");
        CrawlerService.initCrawler();
    }
}
