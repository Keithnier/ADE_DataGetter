package servlet;

import WebService.CrawlerService;
import util.MongoDB;

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

        MongoDB.disconnectMongoDB();

        System.out.println("================MongoDB STOP=================");

        CrawlerService.destroyThreadPool();

        System.out.println("================ThreadPool STOP=================");




    }
}
