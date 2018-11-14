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
    <link rel="stylesheet" href="http://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="http://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
    <script src="http://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>

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
  <div style="height: 6%;font-size: 18px;background: black;color:grey;">
    <span style="margin-left: 110px;float: left;margin-top: 7px;">Cross-platform Search Engine</span>
    <span style="margin-left: 30px;float: left;margin-top: 7px;">Contact Us</span>
  </div>
  <div id="myCarousel" class="carousel slide">
    <!-- 轮播（Carousel）指标 -->
    <ol class="carousel-indicators">
      <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
      <li data-target="#myCarousel" data-slide-to="1"></li>
      <li data-target="#myCarousel" data-slide-to="2"></li>
      <li data-target="#myCarousel" data-slide-to="3"></li>
    </ol>

    <!-- 轮播（Carousel）项目 -->
    <div class="carousel-inner">
      <div class="item active" id="p1">
        <img src="image/home/bg1.jpg" style="height:94%;width: 100%;" alt="">
        <div class="carousel-caption">
          <p style="margin-bottom: 23%;font-size: 70px;">Data Crawler</p>
          <span style="font-size: 25px;">Get what you want from Twitter,Youtube and so on</span>
        </div>
      </div>
      <div class="item" id="p2">
        <img src="image/home/bg1.jpg" style="height:94%;width: 100%;" alt="">
        <div class="carousel-caption">
          <p style="margin-bottom: 23%;font-size: 70px;">Data Preprocess</p>
          <span style="font-size: 25px;">Make the data normalized and easy to handle</span>
        </div>
      </div>
      <div class="item" id="p3">
        <img src="image/home/bg1.jpg" style="height:94%;width: 100%;" alt="">
        <div class="carousel-caption">
          <p style="margin-bottom: 23%;font-size: 70px;">Index Create</p>
          <span style="font-size: 25px;">To improve the data query rate</span>
        </div>
      </div>
      <div class="item" id="p4">
        <img src="image/home/bg1.jpg" style="height:94%;width: 100%;" alt="">
        <div class="carousel-caption">
          <p style="margin-bottom: 23%;font-size: 70px;">IRTree</p>
          <span style="font-size: 25px;">Build the IRTree</span>
        </div>
      </div>
    </div>
    <!-- 轮播（Carousel）导航 -->
    <a class="left carousel-control" href="#myCarousel" role="button" data-slide="prev">
      <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
      <span class="sr-only">Previous</span>
    </a>
    <a class="right carousel-control" href="#myCarousel" role="button" data-slide="next">
      <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
      <span class="sr-only">Next</span>
    </a>
  </div>
  </body>
</html>
