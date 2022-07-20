<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.io.*, java.util.*, java.sql.*"%>
<%@ page import="jakarta.servlet.http.*, jakarta.servlet.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<!DOCTYPE html>
<html>
<head>
<title>Login Page</title>
<link rel="stylesheet" href="home.css">
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
      <a href="signupcreate.jsp">Create</a>
      <div class="home_underline"></div>
    </li>
    <li class="list">
      <a href="#">Join</a>
      <div class="home_underline"></div>
    </li>
    <li class="list">
      <a href="candidateLogin.jsp">Candidates</a>
      <div class="home_underline"></div>
    </li>
    <a href="opening.html"><button>Job Openings</button></a>
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
%>


<sql:setDataSource var="snapshot" driver="com.mysql.jdbc.Driver" url="jdbc:mysql://localhost:3306/test" user="root" password="root"/>

<sql:query dataSource="${snapshot}" var="result">
SELECT OrgName from users;
</sql:query>



<div class="header">
  <h1>
    Join Organization
  </h1>
  <div class="overlay">
	<table>
	<form action="NoInviteJoin" method="post">
  	<tr>
  		<td id="white">FirstName</td>
  		<td><input type="text" name="fname" autofocus></td>
  	</tr>
	<tr>
  		<td id="white">LastName</td>
  		<td><input type="text" name="lname"></td>
  	</tr>
  	<tr>
  		<td id="white">UserName</td>
  		<td><input type="text" name="uname" autofocus></td>
  	</tr>
  	<tr>
  		<td id="white">Password</td>
  		<td><input type="password" name="pass"></td>
  	</tr>
  	<tr>
  		<td id="white">Organisation Name</td>
  		<td><input type="text" name="orgname"></td>
  	</tr>
  	<tr>
  		<td><button id="login" type="submit">Sign Up</button></td>
  		</form>
		<td><a href="http://localhost:8080/Zoho/signupjoin.jsp"><button id="invite">Have Invite Code?</button></a></td>
	</tr>
	<% if (session.getAttribute("errornoi") != null) { %>
	<tr>
  		<td></td>
		<td>
			<div id="err"><%out.println(session.getAttribute("error")); %></div>
		</td>
	</tr>
     <%} %>
	<c:if test="${empty result.rows}">
	<tr>
  		<td></td>
		<td>
			<div id="err">No Org present</div>
		</td>
	</tr>
	</c:if>
	</table>
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
<div class="space" style="height: 200px; background-color: rgb(36, 36, 36)"></div>

<script>
	document.getElementById('invite').addEventListener('click', redirect);
	
	
	function redirect(){
		windows.location.href = "http://localhost:8080/Zoho/signupjoin.jsp";
	}
	</script>
</body>
</html>