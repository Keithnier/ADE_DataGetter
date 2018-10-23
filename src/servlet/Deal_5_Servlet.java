package servlet;

import DataPreProcess.Dictionary.GenerateDic;
import DataPreProcess.Segment.EnglishSegment;
import DataPreProcess.Segment.Segment;
import DataPreProcess.Util.PropertyUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class Deal_5_Servlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        ServletContext sc = getServletContext();

        System.out.println(req.toString());//获得输出打印流
        PrintStream out = new PrintStream(resp.getOutputStream());
        Segment seg = (EnglishSegment)sc.getAttribute("EnglishSegment");//获得分词器
        String textType = req.getParameter("textType");
        List<String> list = null;
        if(textType.equals("Twitter")){
            String dbname =PropertyUtil.getValueByKey("TwitterDataBaseName");
            list = GenerateDic.generateDict(dbname,seg);
        }
        if(list!=null)
            out.print("OK");
        else
            out.print("FALSE");
    }
}
