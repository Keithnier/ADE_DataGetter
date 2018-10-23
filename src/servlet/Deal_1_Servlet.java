package servlet;

import DataPreProcess.Segment.EnglishSegment;
import DataPreProcess.Segment.Segment;
import WebProcess.BySentence;
import WebProcess.TransferInstruction;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static WebProcess.BySentence.ProcessSentence;

//第一个按钮
public class Deal_1_Servlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.toString());
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        ServletContext sc = getServletContext();
        Segment seg = (EnglishSegment)sc.getAttribute("EnglishSegment");//获得分词器
        List<String> dic = (List<String>)sc.getAttribute("Dictionary");

        //输入的英文单词
        String text = req.getParameter("text");
        System.out.println(text);
        //获得分词、去除停用词、过滤表情、过滤http、过滤语言、过滤主题、过滤使用者,
        // 分词后词语在词典中的位置的列表
        Map<String, BySentence.FILTER> map = TransferInstruction.Check2Map(req);

        List<String> result = ProcessSentence(text, seg,dic, map);
        System.out.println(req.toString());
        PrintStream out = new PrintStream(resp.getOutputStream());
        for(String str:result){
            out.print(str);
        }
        out.println("\n");
    }

}
