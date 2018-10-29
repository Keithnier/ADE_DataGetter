package DataCrawler.servlet;

import DataCrawler.WebService.CrawlerService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 停止爬虫的servlet
 */
public class StopCrawlerServlet extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        List<String> crawlerNames = new ArrayList<>();
        if(request.getParameter("Twitter").toLowerCase().equals("true")) crawlerNames.add("Twitter");
        if(request.getParameter("Flickr").toLowerCase().equals("true")) crawlerNames.add("Flickr");
        if(request.getParameter("Tumblr").toLowerCase().equals("true")) crawlerNames.add("Tumblr");
        if(request.getParameter("YouTube").toLowerCase().equals("true")) crawlerNames.add("Youtube");

        stopCrawler(crawlerNames);

        response.getWriter().print("true");
    }

    private void stopCrawler(List<String> crawlers) {
        for (String crawler : crawlers) {
            CrawlerService.stopCrawler(crawler);
        }
    }
}
