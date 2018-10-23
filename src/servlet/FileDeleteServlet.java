package servlet;

import DAO.FilesDAO;
import Twitter.TwitterCrawler;
import util.FileSystem;
import util.MongoDB;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 删除文件的Servlet
 */
public class FileDeleteServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String fileName = request.getParameter("fileName");

        String crawlerName = request.getParameter("crawlerName");
        String filePath_data = TwitterCrawler.class.getClassLoader().getResource("/").getPath() + "outputfile/" + fileName + ".txt";


        File file = FileSystem.getFileByPath(filePath_data);
        file.delete();

        FilesDAO dao = new FilesDAO();
        dao.deleteFile(fileName, crawlerName);

        response.getWriter().print("ok");


    }
}