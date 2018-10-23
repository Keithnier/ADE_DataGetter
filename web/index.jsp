<%--
  Created by IntelliJ IDEA.
  User: dzm
  Date: 2017/11/24
  Time: 19:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Cross-platform Spatio-temporal Text Object Search Engine</title>
    <link href="css/index.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript">
        window.onload = function(){
            var obj1 = document.getElementById("p1");
            var obj2 = document.getElementById("p2");
            var obj3 = document.getElementById("p3");
            var obj4 = document.getElementById("p4");
            obj1.onclick = function(){
                window.location.href="bytime.jsp"
            }
            obj2.onclick = function(){
                window.location.href = "preprocess.jsp";
            }
            obj3.onclick = function(){
                window.location.href = "CreateIndex.jsp";
            }
            obj4.onclick = function(){
                window.location.href = "IRTree.jsp";
            }
        }
    </script>
  </head>
  <body>
  <header>
    <span>Cross-platform Spatio-temporal Text Object Search Engine</span>
  </header>
  <section id="main" >
    <div class="p1" id="p1">
      <img src="image/home/p1.png">
      <span>Data Crawl</span>
    </div>
    <div class="p2" id="p2">
      <img src="image/home/p2.png">
      <span>Data Preprocess</span>
    </div>
    <div class="p3" id="p3">
      <img src="image/home/p3.png">
      <span>Index Create</span>
    </div>
    <div class="p4" id="p4">
      <img src="image/home/p4.png">
      <span>Data Search</span>
    </div>
  </section>
  </body>
</html>
