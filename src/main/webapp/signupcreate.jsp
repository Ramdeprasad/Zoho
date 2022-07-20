<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>Create Org</title>
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
      <a href="home.jsp">Home</a>
      <div class="home_underline"></div>
    </li>
    <li class="list">
      <a href="login.jsp">Login</a>
      <div class="home_underline"></div>
    </li>
    <li class="list">
      <a href="#">Create</a>
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
    <span class="notify-bubble" id="bubble" ></span>
    <button>Job Openings</button>
    </div></a>
  </ul>
  <label for="nav-toggle" class="icon-burger">
    <div class="line"></div>
    <div class="line"></div>
    <div class="line"></div>
  </label>
</nav>

<%	session.setAttribute("errorlog", null);
	session.setAttribute("errorjoi", null);
	session.setAttribute("errornoi", null);
	session.setAttribute("errorclog", null);
%>


<div class="header">
  <h1>
    Create Organization
  </h1>
  <div class="overlay">
	<form action="SignupCreate" method="post">
	<table>
  	<tr>
  		<td id="white">FirstName</td>
  		<td><input type="text" name="fname" autofocus required></td>
  	</tr>
  	<tr>
  		<td id="white">LastName</td>
  		<td><input type="text" name="lname" required></td>
  	</tr>
  	<tr>
  		<td id="white">UserName</td>
  		<td><input type="text" name="uname" required></td>
  	</tr>
  	<tr>
  		<td id="white">Password</td>
  		<td><input type="password" name="pass" required></td>
  	</tr>
  	<tr>
  		<td id="white">Organization Name</td>
  		<td><input type="text" name="orgname" required></td>
  	</tr>
  	<tr>
  		<td></td>
		<td><input id="login" type="submit" value="Sign Up"></td>
	</tr>
	<tr>
  		<td></td>
		<td><div id="err"> <% if (session.getAttribute("errorcre") != null) { 
       			out.println(session.getAttribute("errorcre")); 
     		}  %>
     	</div></td>
	</tr>
	</table>
	</form>
	</div>
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
function hideerr(){
	document.getElementById("err").style.visibility="hidden";
}
	setTimeout("hideerr()", 3000);
	
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