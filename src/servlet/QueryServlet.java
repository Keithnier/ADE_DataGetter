package servlet;

import IRTree.spatialindex.rtree.DataGetter_IRTree;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class QueryServlet extends javax.servlet.http.HttpServlet{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String time = request.getParameter("time");
        String location = request.getParameter("location");
        String keys = request.getParameter("keys");
        ArrayList<String> result = null;
        try {
            result = DataGetter_IRTree.query(time,location,keys,10);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO:此处再加上通过result找到数据库中的原始文件，然后返回
        response.getWriter().print(result);
    }
}
