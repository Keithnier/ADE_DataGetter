<%@ page import="PreProcess.DataPreProcess.DataBase.DataBaseUtil" %>
<%--
  Created by IntelliJ IDEA.
  User: wml
  Date: 2018/4/23
  Time: 14:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Index Create</title>

    <script src="../js/jquery.min.js"></script>
    <script src="../js/jquery.form.js"></script>
    <script src = "../js/buttonclick.js"></script>
    <link href="css/dataPreprocess.css" rel="stylesheet" type="text/css"/>
</head>

<body>

<header>
    <span>Index Create</span>
</header>
<aside >
    <ul class="menu">
        <li><img class="icon" src="../image/left/home.png"><a href="index.jsp" >Back Home</a></li>
    </ul>
</aside>
<section id="main">

    <div>
        <select id = "select4">
            <option value ="twitter">Twitter</option>
            <option value ="youtube">Youtube</option>
            <option value="flicker">Flickr</option>
            <option value="tumblr">Tumblr</option>
        </select>
                 <input type = "button" value="建立Lucene索引" onclick="reSetLucene()"/> &nbsp;&nbsp;
                 <br/><br/>
                 <div>
                     <label class = "input_label">Lucene索引</label><br/>
                     <select id = "select3">
                         <option value ="twitter">Twitter</option>
                         <option value ="youtube">Youtube</option>
                         <option value="flicker">Flickr</option>
                         <option value="tumblr">Tumblr</option>
                     </select>
                     <label>请输入关键字：</label>

                     <textarea name="textarea4" id="textarea4" cols="30" rows="10" title="输入索引的关键字"></textarea>
                     <input type="button" value="确定" onclick="luceneSearch()" />
                 </div>
                 <br/><br/>
                 <div>
                    <textarea name="result" id="result" cols="30" rows="10" title = "输出结果框" readonly></textarea>
                </div>

    </div>
</section>

</body>
</html>
