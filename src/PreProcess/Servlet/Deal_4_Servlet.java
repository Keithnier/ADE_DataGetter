package PreProcess.Servlet;

import PreProcess.DataPreProcess.Index.TwitterIndex;
import PreProcess.DataPreProcess.Model.TwitterInfoModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class Deal_4_Servlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        PrintStream out = new PrintStream(resp.getOutputStream());
        String textType = req.getParameter("textType");
        if(textType.equals("Twitter")){
            String text = req.getParameter("text");
            List<TwitterInfoModel> list = new TwitterIndex().searchByText(text);
            for(TwitterInfoModel tif:list){
                out.println(tif+"\n");
            }
            System.out.print("OK");
        }
    }
}
