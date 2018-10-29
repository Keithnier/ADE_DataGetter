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
    <title>Data Preprocess</title>

    <script src="../js/jquery.min.js"></script>
    <script src="../js/jquery.form.js"></script>
    <script src = "../js/buttonclick.js"></script>
    <link href="css/dataPreprocess.css" rel="stylesheet" type="text/css"/>
</head>

<body>

    <header>
    <span>Data Preprocess</span>
    </header>
    <aside >
        <ul class="menu">
            <li><img class="icon" src="../image/left/home.png"><a href="index.jsp" >Back Home</a></li>
        </ul>
    </aside>
    <section id="main">

         <div>
           <%-- <p align="center"><h1>数据预处理</h1></p>--%>
            <div>
                <p><label>选择处理方式：</label>
                    <input type="checkbox" id = "removeStop" value="removeStop"/> &nbsp;&nbsp;去除停用词&nbsp;&nbsp;
                    <input type="checkbox" id = "filterEmoji" value="filterEmoji"/> &nbsp;&nbsp;过滤emoji&nbsp;&nbsp;
                    <input type="checkbox" id = "filterHttp" value="filterHttp"/> &nbsp;&nbsp;过滤http&nbsp;&nbsp;
                    <input type="checkbox" id = "filterChinese" value="filterChinese"/> &nbsp;&nbsp;过滤中文&nbsp;&nbsp;
                    <input type="checkbox" id = "filterTheme" value="filterTheme"/> &nbsp;&nbsp;过滤主题&nbsp;&nbsp;
                    <input type="checkbox" id = "filterUser" value="filterUser"/> &nbsp;&nbsp;过滤用户名&nbsp;&nbsp;


                </p>
                <label class = "input_label">请输入一段英文句子：</label>
                <textarea id="textarea1" name="textarea1" title="输入一句英文"></textarea>
                <input type = "button" id="button1" value="确定" onclick="button1_click()"/>
                <br/>
                <label class = "input_label">选择类型，并输入相应的id</label>
                <select id = "select1">
                    <option value ="twitter">Twitter</option>
                    <option value ="youtube">Youtube</option>
                    <option value="flicker">Flickr</option>
                    <option value="tumblr">Tumblr</option>
                </select>
                <textarea id="textarea2" title="输入id"></textarea>
                <input type = "button" value="确定" onclick="button2_click()"/>
                <br/>
                <!--从数据库中找出五条数据演示处理-->
                <label class = "input_label">从数据库中找出五条记录进行处理</label>
                <select id = "select2">
                    <option value ="twitter">Twitter</option>
                    <option value ="youtube">Youtube</option>
                    <option value="flicker">Flickr</option>
                    <option value="tumblr">Tumblr</option>
                </select>
                <input type = "button" value="确定" onclick="button3_click()"/>
                <%--<div>
                    <label class = "input_label">Lucene索引部分</label><br/>
                    <label>请输入关键字：</label>
                    <select id = "select3">
                        <option value ="twitter">Twitter</option>
                        <option value ="youtube">Youtube</option>
                        <option value="flicker">Flickr</option>
                        <option value="tumblr">Tumblr</option>
                    </select>
                    <textarea name="textarea4" id="textarea4" cols="30" rows="10" title="输入索引的关键字"></textarea>
                    <input type="button" value="确定" onclick="luceneSearch()" />
                </div>--%>
            </div>
            <div>
                <textarea name="result" id="result" cols="30" rows="10" title = "输出结果框" readonly></textarea>
            </div>
            <div>
                <br/>
                <br/>
                <select id = "select4">
                    <option value ="twitter">Twitter</option>
                    <option value ="youtube">Youtube</option>
                    <option value="flicker">Flickr</option>
                    <option value="tumblr">Tumblr</option>
                </select>
                <input type = "button" value="重新生成用户词典" onclick="reSetDictionary()"/> &nbsp;&nbsp;
                <input type = "button" value="重新计算TF-IDF" onclick="reSetTFIDF()"/> &nbsp;&nbsp;
                <%--<input type = "button" value="重新建立Lucene索引" onclick="reSetLucene()"/> &nbsp;&nbsp;--%>
            </div>
          </div>
        </section>

</body>
</html>
