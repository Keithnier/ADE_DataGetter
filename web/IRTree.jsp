<%@ page import="DataPreProcess.DataBase.DataBaseUtil" %>
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
    <title>Data Search</title>

    <script src="../js/jquery.min.js"></script>
    <script src="../js/jquery.form.js"></script>
    <script src = "../js/buttonclick.js"></script>
    <link href="css/dataPreprocess.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript">
        function buildIRtree() {
            // var filepath = $("#filepath").val();
            var Twitter = document.getElementById("Twitter").checked;
            var Flickr = document.getElementById("Flickr").checked;
            var Tumblr = document.getElementById("Tumblr").checked;
            var YouTube = document.getElementById("YouTube").checked;
            var filepath;
            if (Twitter) filepath = "src\\test\\data.txt"; //这只是测试路径，例如："src\\test\\data.txt"和"Weight"
            /*if (Twitter) filepath = "E:\\data.txt";*/
            else filepath = null;
            $.ajax({
                url: "servlet/IRtreeServlet",
                data: {filepath: filepath},
                async: true,   //是否为异步请求
                cache: false,  //是否缓存结果
                type: "POST", //请求方式为POST
                dataType: "text",  //服务器返回的数据是什么类型
                success: function (result) {  //这个方法会在服务器执行成功是被调用 ，参数result就是服务器返回的值(现在是json类型)
                    if (result) {
                        var obj = document.getElementById("result");
                        obj.scrollTop = obj.scrollHeight; // good
                        obj.innerHTML = result;
                    }
                }
            })
        }
    </script>

    <script type="text/javascript">
        window.onload = function () {
            var obj = document.getElementById("query");
            obj.onclick = function () {
                window.location.href = "query.jsp";
            }
        }
    </script>
</head>

<body>

<header>
    <span>IRTree Build</span>
</header>
<aside >
    <ul class="menu">
        <li><img class="icon" src="../image/left/home.png"><a href="index.jsp" >Back Home</a></li>
    </ul>
</aside>
<section id="main">
    <section id="con">
        <div class="content">
            <label>
                Platform：&nbsp;&nbsp;
                <input id="Twitter" type="checkbox" value="Twitter"/>&nbsp;&nbsp;Twitter&nbsp;&nbsp;
                <input id="Flickr" type="checkbox" value="Flickr" disabled="disabled"
                       style="TEXT-DECORATION: line-through" title="Does not support real-time acquisition"/><s>&nbsp;&nbsp;Flickr&nbsp;&nbsp;</s>
                <input id="Tumblr" type="checkbox" value="Tumblr" disabled="disabled"
                       style="TEXT-DECORATION: line-through" title="Does not support real-time acquisition"/><s>&nbsp;&nbsp;Tumblr&nbsp;&nbsp;</s>
                <input id="YouTube" type="checkbox" value="YouTube" disabled="disabled"
                       style="TEXT-DECORATION: line-through" title="Does not support real-time acquisition"/><s>&nbsp;&nbsp;YouTube&nbsp;&nbsp;</s>
            </label>
        </div>
        <div class="content">
            <label><input id="build" class="green_button" type="button" value="Build" style="height:40px;width:80px;"
                          onclick="buildIRtree();"/> </label>
        </div>
        <div class="content">
            <textarea id="result" rows="19" cols="52" readonly></textarea>
        </div>
        <div class="content">
            <label><input id="query" class="green_button" type="button" value="query" style="height:40px;width:80px;"/>
            </label>
        </div>
    </section>

</section>

</body>
</html>
