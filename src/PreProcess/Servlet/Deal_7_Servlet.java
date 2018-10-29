package PreProcess.Servlet;

import PreProcess.DataPreProcess.Index.TwitterIndex;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;

public class Deal_7_Servlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        ServletContext sc = getServletContext();

        System.out.println(req.toString());//获得输出打印流
        PrintStream out = new PrintStream(resp.getOutputStream());
        String textType = req.getParameter("textType");
        if(textType.equals("Twitter")){
            TwitterIndex twitterIndex = new TwitterIndex();
            twitterIndex.createIndex();
            System.out.print("OK");
        }
    }
}
