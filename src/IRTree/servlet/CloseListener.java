package IRTree.servlet;

import IRTree.spatialindex.rtree.DataGetter_IRTree;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class CloseListener implements ServletContextListener
        {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("ServletContext对象创建");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("ServletContext对象销毁");
        try {
            DataGetter_IRTree.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
