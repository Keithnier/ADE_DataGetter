package IRTree.servlet;

import IRTree.spatialindex.rtree.DataGetter_IRTree;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IRtreeServlet extends javax.servlet.http.HttpServlet{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
        request.setCharacterEncoding("utf-8");
        String DBname = request.getParameter("filepath");
        boolean state = false;
        try {
            //TODO:可以选择创建一段指定时间的IRTree
            //TODO:btreename建议跟DBname关联起来，否则会覆盖之前的文件
            state = DataGetter_IRTree.build(DBname,"testbtree",100,4096,true);

        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Fail to build IRtree!");
            response.getWriter().print("Fail to build IRTree!");
            System.exit(-1);
        }
        if (state) response.getWriter().print("Success to build IRTree!");
        else response.getWriter().print("Fail to build IRTree!");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }
}
