package PreProcess.Servlet;

import PreProcess.DataPreProcess.InfoExtra.TwitterExtract;
import PreProcess.DataPreProcess.Segment.EnglishSegment;
import PreProcess.DataPreProcess.Segment.Segment;
import PreProcess.WebProcess.BySentence;
import PreProcess.WebProcess.TransferInstruction;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static CommonUtil.MongoDB.Read.findEntry;
import static PreProcess.WebProcess.BySentence.ProcessSentence;

//第二个按钮
public class Deal_2_Servlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        ServletContext sc = getServletContext();
        Segment seg = (EnglishSegment)sc.getAttribute("EnglishSegment");//获得分词器
        List<String> dic = (List<String>)sc.getAttribute("Dictionary");

        System.out.println(req.toString());//获得输出打印流
        PrintStream out = new PrintStream(resp.getOutputStream());
        //输入的英文单词
        String id = req.getParameter("text");
        String textType= req.getParameter("textType");
        System.out.println(id);
        List<String> list;
        if(textType.equals("Twitter")) {
            list = findEntry("TwitterData", "id_str", id);
        } else{
            list = new LinkedList<>();
        }
        if(list.isEmpty()) {
            out.println("No Twitter found!");
            return;
        }
        //获得分词、去除停用词、过滤表情、过滤http、过滤语言、过滤主题、过滤使用者,
        // 分词后词语在词典中的位置的列表
        String text = new TwitterExtract().jsonInfoExtra(list.get(0)).getText();
        if(text==null) text = "";

        Map<String, BySentence.FILTER> map = TransferInstruction.Check2Map(req);
        List<String> result = ProcessSentence(text, seg,dic, map);

        for(String str:result){
            out.print(str);
        }
        out.println("\n");
    }
}
