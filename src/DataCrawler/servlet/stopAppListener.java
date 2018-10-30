package DataCrawler.servlet;

import DataCrawler.WebService.CrawlerService;
import static CommonUtil.MongoDB.DataBaseUtil.MongoDBDisConnect;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class stopAppListener implements ServletContextListener{
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("================App START=================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("================App STOP=================");

        MongoDBDisConnect();

        System.out.println("================MongoDB STOP=================");

        CrawlerService.destroyThreadPool();

        System.out.println("================ThreadPool STOP=================");




    }
}
