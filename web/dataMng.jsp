<%@ page import="model.FilesModel" %>
<%@ page import="java.util.List" %>
<%@ page import="DAO.FilesDAO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Multi-platform Data Crawler</title>
    <link href="css/dataCrawl.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript">
    </script>
  </head>
  <body>
  <header>
    <span>Multi-platform Data Crawler</span>
    <%--<div class="green_button" style="position:fixed;top:40px;left:0px;">--%>
      <%--<a href="#" onclick="window.location.href='http://localhost:5000'">网页爬取</a>--%>
      <%--<a href="#" onclick="window.location.href='http://localhost:8080'">Api爬取</a>--%>
    <%--</div>--%>
  </header>
  <aside>
    <ul class="menu">
      <li><img class="icon" src="../image/left/dataGet.png"><a href="bytime.jsp" >Crawl By Time</a></li>
      <li><img class="icon" src="../image/left/dataPreProcess.png"><a href="bykeyword.jsp">Crawl By Keyword</a></li>
      <li><img class="icon" src="../image/left/dataIndex.png"><a href="byregion.jsp">Crawl By Region</a></li>
      <li class="active"><img class="icon" src="../image/left/dataQuery.png"><a href="dataMng.jsp">Data Manage</a></li>
      <li></li>
      <li><img class="icon" src="../image/left/home.png"><a href="index.jsp" onclick="return isStop()">Back Home</a></li>
    </ul>
  </aside>
  <section id="main">
    <div id="con">
      <table id="table-7"> <!-- Replace "table-1" with any of the design numbers -->
        <thead>
        <th>Id</th>
        <th>FileName</th>
        <th>StartTime</th>
        <th>DataNum</th>
        <th>DataSize</th>
        <th>Operation</th>
        </thead>
        <tbody>
        <%
          FilesDAO dao=new FilesDAO();
          int id = 1;
          List<FilesModel> list =dao.readFilesModel();
          for(FilesModel tl:list) {
              String deleteUrl = "/FileDeleteServlet?fileName=" + tl.getFileName() + "&crawlerName=" + tl.getCrawler();
              String downloadUrl = "/FileDownloadServlet?fileName=" + tl.getFileName();
        %>
        <tr>
          <td><%=id++%></td>
          <td><%=tl.getFileName()%></td>
          <td><%=tl.getStartTime() %>></td>
          <td><%=tl.getDataNum() %></td>
          <td><%=tl.getDataSize() %></td>
          <td><a href=<%=deleteUrl%>>Delete</a>&nbsp;&nbsp;&nbsp;<a href=<%=downloadUrl%>>Detail</a></td>
        </tr>
        <%}
        %>
        </tbody>
      </table>
    </div>
  </section>
  </body>
</html>
