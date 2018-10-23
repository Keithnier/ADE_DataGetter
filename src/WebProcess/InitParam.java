package WebProcess;

import DataPreProcess.DataBase.DataBaseUtil;
import DataPreProcess.Dictionary.LoadDictionary;
import DataPreProcess.Segment.EnglishSegment;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * 初始化监听器
 * 初始时需要连接数据库并且声明分词器
 * 销毁时需要断开数据库（分词器会由java的垃圾回收机制销毁）
 *
 */
public class InitParam implements ServletContextListener {

    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
        DataBaseUtil.MongoDBDisConnect();
    }

    public void contextInitialized(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
        System.out.println("==========系统初始化==========");
        arg0.getServletContext().setAttribute("EnglishSegment", new EnglishSegment());
        arg0.getServletContext().setAttribute("Dictionary", LoadDictionary.loadDictionary());
        DataBaseUtil.MongoDBConnect();

    }

}