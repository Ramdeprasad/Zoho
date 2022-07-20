<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.io.*, java.util.*, java.sql.*"%>
<%@ page import="jakarta.servlet.http.*, jakarta.servlet.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<title>Welcome</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
<style>
body{
  margin-top: 20px;
  font-family: Montserrat, sans-serif;
}

img {
  display:block;
  margin-left: auto;
  margin-right: auto;
  height: 50%;
  width: 50%;
  margin-top: 15px;
}
.panel-footer {
  padding: 5px;
  height: 40px;
}
#req{
  margin-top:10px;
  text-align:center;
}

</style>
</head>
<body>
<% 

if(session.getAttribute("username")==null){
	response.sendRedirect("home.jsp");
	
}


response.setHeader("Cache-control","no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setHeader("Expires", "0");
%>
<div class="container" >
  <div class="panel panel-info" >
      <div class="panel-heading" >
        <h3 class="panel-title">Candidate</h3>
      </div> 
      <div class="panel-body" >
        <div class="row" >
          <div class="col-xs-12 col-md-3 col-lg-3" align="center">
            <img src="https://www.seekpng.com/png/detail/133-1334822_default-staff-image-person-icon.png" class="img-responsive img-rounded" alt="User image">
          </div>
          <div class="col-xs-12 col-md-5 col-lg-5" >  
            <h3 class="text-uppercase">${FirstName } ${LastName }</h3>
            <h4>@${username }</h4>
            <div class="table-responsive">
              <table class="table table-responsive table-user-information">
                <tbody>
                  <tr>
                    <td>
                      <strong>
                        <span class="glyphicon glyphicon-home"></span>
                      </strong>
                    </td>
                    <td class="text-primary">
                      India
                    </td>
                  </tr>
                  <tr>
                     <td>
                      <strong>
                        <span class="glyphicon glyphicon-envelope"></span>
                      </strong>
                    </td>
                    <td class="text-primary">
                      ${username }@zcorpcandidate.com
                    </td>
                  </tr>
                               
                </tbody>
              </table>
              </div>

  <div class="button-group center">
      <button class="btn" id="showtog" onclick="tog()">Show Applied Jobs</button>   
  </div>
  
<p hidden id="user">${username }</p>
<div id="table"></div>


            
          </div>
        </div>
      </div>
      <div class="panel-footer">
        <h5 class="pull-left">&copy; A Moments of Technology</h5>
        <div id="err" style="text-align:center;margin-left:27%;width:200px;display:inline-block;"> <% if (session.getAttribute("errorcanwel") != null) { 
       			out.println(session.getAttribute("errorcanwel")); 
     		}  %>
     	</div>
        <div class="pull-right">
        	<form action="Logout" method="get">
        	<a href="#" data-original-title="Logout" data-toggle="tooltip"><button type="submit" class="btn btn-default btn-sm">
            	<span class="glyphicon glyphicon-log-out"></span> Log out</button>
            </a>
            </form>
        </div>
      </div>
    </div>
</div>






<script>

window.onload = displaytable;
	function displaytable(){
		console.log("hoi");
		var uname = document.getElementById("user").innerHTML;
		console.log(uname);
	    var url = "http://localhost:8080/Zoho/GetAppliedRole"
	   	var params = "uname="+uname;
	    var xhr = new XMLHttpRequest();
	    xhr.open('GET', url+"?"+params, true);
	    xhr.onload = function(){
	      if(xhr.status == 200){
	      	document.getElementById("table").innerHTML = this.responseText;
	      }
	    }
	    xhr.send();
	}
	
	function tog(){
		var x = document.getElementById('req');
		if(document.getElementsByTagName("tr").length <= 3){
			x.style.display = 'none';
		}
		else{
			if (x.style.display === "none"){
			    x.style.display = "table";
			} 
			else{
			    x.style.display = "none";
			}
		}
	}
	
	
	function hideerr(){
		document.getElementById("err").style.display="none";
	}
		setTimeout("hideerr()", 3000);
	
</script>

</body>
</html>