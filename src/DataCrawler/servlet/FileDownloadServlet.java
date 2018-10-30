package DataCrawler.servlet;

import DataCrawler.util.FileSystem;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 下载文件的Servlet
 */
public class FileDownloadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = request.getParameter("fileName");

        String filePath_data = FileDownloadServlet.class.getClassLoader().getResource("/").getPath() + "DataCrawler/outputfile/" + fileName + ".txt";

        FileInputStream inputStream = new FileInputStream(FileSystem.getFileByPath(filePath_data));

        byte b[] = new byte[1024];
        int length;

        while((length = inputStream.read(b)) > 0) {
            response.getOutputStream().write(b, 0, length);
        }

        inputStream.close();
        response.getOutputStream().flush();

        response.getWriter().print("ok");

    }
}
