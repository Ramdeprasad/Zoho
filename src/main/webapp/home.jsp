<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>Home Page</title>
<link rel="stylesheet" href="home.css">
<style>
.notify-container {
  position: relative;
	display: inline-block;
  margin-top: 10px;
  z-index:-100;
}

  .notify-bubble {
    position: absolute;
    top: -8px;
    right: -11px;
    padding: 2px 5px 2px 6px;
    background-color: white;
    color: blue;
    font-size: 0.65em;
    border-radius: 50%;
    box-shadow: 1px 1px 1px gray;
    opacity:75%;
    width:15px;
    height:15px;
    text-align: center;
    font-size:12px;
  }
</style>
</head>
<body>

<nav class="container">
  <input id="nav-toggle" type="checkbox" />
  <div class="logo">
    Z<strong style="color: #153b66; font-weight: 800">Cliq</strong>
    <br><p style="font-size:15px;">A Moments of Technology</p>
  </div>
  <ul class="links">
  	<li class="list">
      <a href="#">Home</a>
      <div class="home_underline"></div>
    </li>
    <li class="list">
      <a href="login.jsp">Login</a>
      <div class="home_underline"></div>
    </li>
    <li class="list">
      <a href="signupcreate.jsp">Create</a>
      <div class="home_underline"></div>
    </li>
    <li class="list">
      <a href="signupjoin.jsp">Join</a>
      <div class="home_underline"></div>
    </li>
    <li class="list">
      <a href="candidateLogin.jsp">Candidates</a>
      <div class="home_underline"></div>
    </li>
    <a href="opening.html"><div class="notify-container">
    <span class="notify-bubble" id="bubble"></span>
    <button>Job Openings</button>
    </div></a>
  </ul>
  
  <label for="nav-toggle" class="icon-burger">
    <div class="line"></div>
    <div class="line"></div>
    <div class="line"></div>
  </label>
</nav>

<%	session.setAttribute("errorcre", null);
	session.setAttribute("errorjoi", null);
	session.setAttribute("errorlog", null);
	session.setAttribute("errornoi", null);
	session.setAttribute("errorclog", null);
%>


<div class="header">
  <h1>
    The best and Effective way to manage your Organizations.
  </h1>
  <div class="hero">
    <div class="circles"></div>
    <div class="circles"></div>
    <div class="circles"></div>
    <div class="circles"></div>
    <div class="circles"></div>
    <div class="circles"></div>
  </div>
</div>

<script>
	window.onload = bubble;
		function bubble(){
			var url = "http://localhost:8080/Zoho/GetBubbleCount";
	        var xhr = new XMLHttpRequest();
	        xhr.open('GET', url, true);
	        xhr.onload = function(){
	          if(xhr.status == 200){
	        	  document.getElementById("bubble").innerHTML = this.responseText;
	          }
	        }
	        xhr.send();
	}
</script>

</body>
</html>