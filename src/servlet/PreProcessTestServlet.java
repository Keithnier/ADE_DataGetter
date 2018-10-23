package servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Scanner;

/**
 * 测试类
 */
public class PreProcessTestServlet extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int num = Integer.parseInt(req.getParameter("num"));
        String filePath = this.getServletContext().getRealPath("res/after1.txt");
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < 10; i++){
            result.append(getString(filePath, num+i));
        }
        resp.getWriter().print(result.toString());
    }

    private String getString(String filePath, int num) throws IOException {
        int line = (num -1) * 9 + 1;

        File file = new File(filePath);
        if (file.exists() == false) {
            return "";
        }

        FileReader fileReader = new FileReader(file);
        LineNumberReader lineNumberReader = new LineNumberReader(fileReader);

        StringBuilder buffer = new StringBuilder();
        String temp = lineNumberReader.readLine();

        while(lineNumberReader.getLineNumber() != line) {
            temp = lineNumberReader.readLine();
        }

        for(int i = 0; i < 9; i++) {
            buffer.append(temp);
            buffer.append('\n');
            temp = lineNumberReader.readLine();
        }

        return buffer.toString();

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    public static void main(String[] args) throws IOException {
//        Scanner scanner = new Scanner(System.in);
//
//        String filePath = scanner.nextLine();
//        int num = scanner.nextInt();
//
//        System.out.println(getString(filePath, num));
    }
}
