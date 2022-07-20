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
        <h3 class="panel-title">${orgName}</h3>
      </div> 
      <div class="panel-body" >
        <div class="row" >
          <div class="col-xs-12 col-md-3 col-lg-3" align="center">
            <img src="https://www.seekpng.com/png/detail/133-1334822_default-staff-image-person-icon.png" class="img-responsive img-rounded" alt="User image">
          </div>
          <div class="col-xs-12 col-md-5 col-lg-8" >  
            <h3 class="text-uppercase">${FirstName} ${LastName}</h3>
            <h4>@${username}    ${ role }</h4>
            <div class="table-responsive" >
              <table class="table table-responsive table-user-information">
                <tbody>
                  <tr>
                    <td>
                      <strong>
                        <span class="glyphicon glyphicon-home"></span>
                      </strong>
                    </td>
                    <td class="text-primary">
                      ${orgName} India
                    </td>
                  </tr>
                  <tr>
                     <td>
                      <strong>
                        <span class="glyphicon glyphicon-envelope"></span>
                      </strong>
                    </td>
                    <td class="text-primary">
                      ${username}@zcorp.com
                    </td>
                  </tr>
                               
                </tbody>
              </table>
              </div>
              
              
<% 

Class.forName("com.mysql.cj.jdbc.Driver");
String url = "jdbc:mysql://localhost:3306/test";
String user = "root";
String password = "root";
Connection con = DriverManager.getConnection(url, user, password); 
PreparedStatement st = con.prepareStatement("select FirstName, LastName, UserName, Roles from users where OrgName=? and Roles in (?, ?)");
st.setString(1, (String)session.getAttribute("orgName"));
st.setString(2, "Pending");
st.setString(3, "Recruiter");
ResultSet rs = st.executeQuery();


if(session.getAttribute("role") != null && session.getAttribute("role").equals("Pending")){ %>
  <div class="button-group center">
      <a href="#" data-original-title="Pending" data-toggle="tooltip"><button class="btn">Show Status</button> </a>   
  </div>


<% } 
else if(session.getAttribute("recCode")==null){ %>
  <div class="button-group center"> 
      <button class="btn" id="showtog" onclick="togrec1()">Create Job</button>
      <button class="btn" id="showtog" onclick="togrec2()">View Job Opening</button>
      <button class="btn" id="showtog" onclick="togrec3()">View Candidates</button>   
  </div>  
  <div>         
  <table id="rec1" class="table table-responsive table-user-information" style="display:none;">
	<form action="CreateJob" method="post">
	<tbody>
  	<tr>
  		<td id="white">JobName</td>
  		<td><input type="text" name="jobName" autofocus></td>
  	</tr>
	<tr>
  		<td id="white">Vacancy</td>
  		<td><input type="number" name="vacancy"></td>
  	</tr>
  	<tr>
  		<td id="white">Experience(Year)</td>
  		<td><input type="number" name="exp"></td>
  	</tr>
  	<tr>
  		<td id="white">Requirement</td>
  		<td><input type="text" name="require"></td>
  	</tr>
  	<tr>
  		<td><input hidden type="text" name="orgName" value= ${ orgName }><input hidden type="text" name="rec" value= ${ username }></td>
  		<td><button id="createjobbtn" type="submit">Create Job</button></td>
	</tr>
	</tbody>
	</form>
	</table> 
	
	
	
<%
	st = con.prepareStatement("select JobName, Opening, Experience, Requirement from jobopening where OrgName=? and RecUserName=? and Opening!=0");
	st.setString(1, (String)session.getAttribute("orgName"));
	st.setString(2, (String)session.getAttribute("username"));
	rs = st.executeQuery();

%>
	
	
<table  id="rec2" class="table table-responsive table-user-information" style="display:none;">
  <thead>
	<tr>
		<th>JobName</th>
		<th>Vacancy</th>
		<th>Experience</th>
		<th>Requirement</th>
	</tr>
	</thead>
	<tbody>
<%	



while(rs.next()){ %>
		<tr>
			<td><%= rs.getString("JobName") %></td>
			<td><%= rs.getString("Opening") %></td>
			<td><%= rs.getString("Experience") %></td>
			<td><%= rs.getString("Requirement") %></td>
		</tr>
<% } %>
</tbody>
</table>





<%
	st = con.prepareStatement("select A.FirstName, A.LastName, A.UserName, B.JobName, B.JobID, C.Status from candidates A, jobopening B, candidateapply C where A.UserName = C.UserName and B.JobID = C.JobID and B.RecUserName = ?");
	st.setString(1, (String)session.getAttribute("username"));
	rs = st.executeQuery();

%>
	
	
<table  id="rec3" class="table table-responsive table-user-information" style="display:none;">
  <thead>
	<tr>
		<th>FirstName</th>
		<th>LastName</th>
		<th>UserName</th>
		<th>JobName</th>
		<th>Action</th>
	</tr>
	</thead>
	<tbody>
<%	



while(rs.next()){ %>
		<tr>
			<td><%= rs.getString("UserName") %></td>
			<td><%= rs.getString("FirstName") %></td>
			<td><%= rs.getString("LastName") %></td>
			<td><%= rs.getString("JobName") %></td>
			<td><% String action = rs.getString("Status");%>
			<select id="sel" onchange="changecanapplystatus(this)">
			<% if(action.equalsIgnoreCase("Pending")){ %>
					<option value=<%= rs.getString("UserName") %> id=<%= rs.getString("JobID") %> selected>Pending</option>
					<option value=<%= rs.getString("UserName") %> id=<%= rs.getString("JobID") %> >Accept</option>
					<option value=<%= rs.getString("UserName") %> id=<%= rs.getString("JobID") %> >Reject</option>
					<option value=<%= rs.getString("UserName") %> id=<%= rs.getString("JobID") %> >Blacklist</option>
				<%}else if(action.equalsIgnoreCase("Accept")){ %>
					<option value=<%= rs.getString("UserName") %> id=<%= rs.getString("JobID") %> selected>Accept</option>
					<option value=<%= rs.getString("UserName") %> id=<%= rs.getString("JobID") %> >Reject</option>
					<option value=<%= rs.getString("UserName") %> id=<%= rs.getString("JobID") %> >Blacklist</option>
				<%} %>
			  	</select>
			</td>	
		</tr>
<% } %>
</tbody>
</table>


	
</div>  
       
<% }
else if(session.getAttribute("adCode")==null){ %>         
  <div class="button-group center">
      <button class="btn" id="showtog" onclick="togad1()">Show Requests</button>
      <button class="btn" id="showtog" onclick="togad2()">Job Openings</button>
      <button class="btn" id="showtog" onclick="togad3()">Convert Candidate</button>
      <button class="btn">Recruiter code:${recCode}</button>     
  </div> 
  <div>        
  <table  id="ad1" class="table table-responsive table-user-information" style='display:none'>
  <thead>
	<tr>
		<th>FirstName</th>
		<th>LastName</th>
		<th>UserName</th>
		<th>OrgName</th>
		<th>Roles</th>
	</tr>
	</thead>
	<tbody>
<%	



while(rs.next()){ %>
		<tr>
			<td><%= rs.getString("FirstName") %></td>
			<td><%= rs.getString("LastName") %></td>
			<td><%= rs.getString("UserName") %></td>
			<td>${ orgName }</td>
			<td><% String role = rs.getString("Roles");%>
			<select id="sel" onchange="selectval(this)">
			<% if(role.equals("Pending")){ %>
					<option value=<%= rs.getString("UserName") %> selected>Pending</option>
					<option value=<%= rs.getString("UserName") %> >Recruiter</option>
				<%}else if(role.equals("Recruiter")){ %>
					<option value=<%= rs.getString("UserName") %> >Pending</option>
					<option value=<%= rs.getString("UserName") %> selected>Recruiter</option>
				<%} %>
			  	</select>
			</td>	
		</tr>
<% } %>
</tbody>
</table>

<%
st = con.prepareStatement("select * from jobopening where OrgName=?");
st.setString(1, (String)session.getAttribute("orgName"));
rs = st.executeQuery();

%>



<table  id="ad2" class="table table-responsive table-user-information" style='display:none;'>
  <thead>
	<tr>
		<th>JobName</th>
		<th>Vacancy</th>
		<th>Experience</th>
		<th>Requirement</th>
		<th>Rec_ID</th>
		<th>Action</th>
	</tr>
	</thead>
	<tbody>
<%	



while(rs.next()){ %>
		<tr>
			<td><%= rs.getString("JobName") %></td>
			<td><%= rs.getString("Opening") %></td>
			<td><%= rs.getString("Experience") %></td>
			<td><%= rs.getString("Requirement") %></td>
			<td><%= rs.getString("RecUserName") %></td>
			<td><% String action = rs.getString("Status");%>
			<select id="sel" onchange="changejobreq(this)">
			<% if(action.equals("Pending")){ %>
					<option value=<%= rs.getString("JobID") %> selected>Pending</option>
					<option value=<%= rs.getString("JobID") %> >Verified</option>
					<option value=<%= rs.getString("JobID") %> >Delete</option>
				<%}else if(action.equals("Verified")){ %>
					<option value=<%= rs.getString("JobID") %> >Pending</option>
					<option value=<%= rs.getString("JobID") %> selected>Verified</option>
					<option value=<%= rs.getString("JobID") %> >Delete</option>
				<%} %>
			  	</select>
			</td>	
		</tr>
<% } %>
</tbody>
</table>


<%
	st = con.prepareStatement("select A.FirstName, A.LastName, A.UserName, B.JobName, B.JobID, C.Status from candidates A, jobopening B, candidateapply C where A.UserName = C.UserName and B.JobID = C.JobID and B.OrgName=?");
	st.setString(1, (String)session.getAttribute("orgName"));
	rs = st.executeQuery();

%>





<table  id="ad3" class="table table-responsive table-user-information" style="display:none;">
  <thead>
	<tr>
		<th>FirstName</th>
		<th>LastName</th>
		<th>UserName</th>
		<th>JobName</th>
		<th>Action</th>
	</tr>
	</thead>
	<tbody>
<%	



while(rs.next()){ %>
		<% String action = rs.getString("Status");%>
		<% if(action.equalsIgnoreCase("Accept")){ %>
		<tr>
			<td><%= rs.getString("UserName") %></td>
			<td><%= rs.getString("FirstName") %></td>
			<td><%= rs.getString("LastName") %></td>
			<td><%= rs.getString("JobName") %></td>
			<td>
				<select id="sel" onchange="changecanapplystatus(this)">
					<option value=<%= rs.getString("UserName") %> id=<%= rs.getString("JobID") %> selected>Accept</option>
					<option value=<%= rs.getString("UserName") %> id=<%= rs.getString("JobID") %> >Reject</option>
					<option value=<%= rs.getString("UserName") %> id=<%= rs.getString("JobID") %> >Blacklist</option>
					<option value=<%= rs.getString("UserName") %> id=<%= rs.getString("JobID") %> >Employee</option>
				<%} %>
			  	</select>
			</td>	
		</tr>
<% } %>
</tbody>
</table>



</div>

<% } else { 


st = con.prepareStatement("select * from restrictions where OrgName=?");
st.setString(1, (String)session.getAttribute("orgName"));
rs = st.executeQuery();
%>
  <div class="button-group" ">
      <button class="btn" id="showtog" onclick="togsa1()">Show Requests</button>
      <button class="btn">Ad:${adCode}</button> 
      <button class="btn">Rec:${recCode}</button> 
      <%if(rs.next()){ %>
      Rec Limit<input type="number" onchange="changeres(this)" class=${orgName} id="reclimit" style="width:40px" value="<%= rs.getInt("Reclimit") %>" />
      Can limit<input type="number" onchange="changeres(this)" class=${orgName} id="canapplylimit" style="width:40px" value="<%= rs.getInt("Canapplylimit") %>"/> 
      Rest(months)<input type="number" onchange="changeres(this)" class=${orgName} id="canrestrictlimit" style="width:40px" value="<%= rs.getInt("Canrestrictlimit") %>"/> 
      <%} %>   
  </div>
  
  
  <%
  
  st = con.prepareStatement("select FirstName, LastName, UserName, Roles from users where OrgName=? and Roles in (?, ?, ?)");
  st.setString(1, (String)session.getAttribute("orgName"));
  st.setString(2, "Pending");
  st.setString(3, "Recruiter");
  st.setString(4, "Admin");
  rs = st.executeQuery();
  
  %>
<table id='sa1' class='table table-responsive table-hover' style='display:none;'>
	<thead>
	<tr>
		<th>FirstName</th>
		<th>LastName</th>
		<th>UserName</th>
		<th>OrgName</th>
		<th>Roles</th>
	</tr>
	</thead>
	<tbody>
<%	while(rs.next()){ %>
		<tr>
			<td ><%= rs.getString("FirstName") %></td>
			<td><%= rs.getString("LastName") %></td>
			<td><%= rs.getString("UserName") %></td>
			<td>${ orgName }</td>
			<td><% String role = rs.getString("Roles");%>
			<select id="sel" onchange="selectval(this)">
			<% if(role.equals("Pending")){ %>
					<option value=<%= rs.getString("UserName") %> selected>Pending</option>
					<option value=<%= rs.getString("UserName") %> >Admin</option>
					<option value=<%= rs.getString("UserName") %> >Recruiter</option>
				<%}else if(role.equals("Recruiter")){ %>
					<option value=<%= rs.getString("UserName") %> >Pending</option>
					<option value=<%= rs.getString("UserName") %> >Admin</option>
					<option value=<%= rs.getString("UserName") %> selected>Recruiter</option>
				<%}else if(role.equals("Admin")){ %>
					<option value=<%= rs.getString("UserName") %> >Pending</option>
					<option value=<%= rs.getString("UserName") %> selected>Admin</option>
					<option value=<%= rs.getString("UserName") %> >Recruiter</option>
				<%} %>
			  	</select>
			</td>	
		</tr>
<% } %>
</tbody>
</table>

<% } %>

            
          </div>
        </div>
      </div>
      <div class="panel-footer">
        <h5 class="pull-left">&copy; A Moments of Technology</h5>
        <div id="err" style="text-align:center;margin-left:26%;width:300px;display:inline-block;"> <% if (session.getAttribute("displaymsg") != null) { 
       			out.println(session.getAttribute("displaymsg")); 
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
	
	
	
	
	function togrec1(){
		
		
		
		
		var rec1 = document.getElementById('rec1').style;
			if (rec1.display == "none"){
				$('#rec2 thead').hide();
				$('#rec2 tbody').hide();
				$('#rec3 thead').hide();
				$('#rec3 tbody').hide();

				rec1.display="table";
			} 
			else{
				rec1.display = "none";
			}
		}

	
	function togrec2(){
		var rec2 = document.getElementById('rec2').style;
		if (rec2.display == "none"){
			$('#rec1 thead').hide();
			$('#rec1 tbody').hide();
			$('#rec2 thead').hide();
			$('#rec2 tbody').hide();

			rec2.display="table";
		} 
		else{
			rec2.display = "none";
		}
	}
	
	function togrec3(){
		console.log("rec3");
		if ($("#rec3").style.display == "none"){
			$("#rec1").style.display = "table";
		} 
		else{
			$("#rec1").style.display = "none";
		}
	}
	
	function togad1(){
		var ad1 = document.getElementById('ad1').style;
		var ad2 = document.getElementById('ad2').style;
		var ad3 = document.getElementById('ad3').style;
		if(ad2.display != 'none' || ad3.display != 'none'){
			ad2.display = 'none';
			ad3.display = 'none';
		}
		if(document.getElementsByTagName("tr").length <= 3){
			ad2.display = 'none';
			ad3.display = 'none';
		}
		else{
			if (ad1.display == "none"){
				ad1.display = "table";
			} 
			else{
				ad1.display = "none";
			}
		}
	}
	
	function togad2(){
		var ad1 = document.getElementById('ad1').style;
		var ad2 = document.getElementById('ad2').style;
		var ad3 = document.getElementById('ad3').style;
		if(ad1.display != 'none' || ad3.display != 'none'){
			ad1.display = 'none';
			ad3.display = 'none';
		}
		if(document.getElementsByTagName("tr").length <= 3){
			ad1.display = 'none';
			ad3.display = 'none';
		}
		else{
			if (ad2.display == "none"){
				ad2.display = "table";
			} 
			else{
				ad2.display = "none";
			}
		}
	}	
	
	function togad3(){
		var ad1 = document.getElementById('ad1').style;
		var ad2 = document.getElementById('ad2').style;
		var ad3 = document.getElementById('ad3').style;
		if(ad1.display != 'none' || ad2.display != 'none'){
			ad1.display = 'none';
			ad2.display = 'none';
		}
		if(document.getElementsByTagName("tr").length <= 3){
			ad1.display = 'none';
			ad2.display = 'none';
		}
		else{
			if (ad3.display == "none"){
				ad3.display = "table";
			} 
			else{
				ad3.display = "none";
			}
		}
	}
	
	function togsa1(){
		var sa1 = document.getElementById('sa1').style;
		if(document.getElementsByTagName("tr").length <= 3){
			sa1.display = 'none';
		}
		else{
			if (sa1.display == "none"){
				sa1.display = "table";
			} 
			else{
				sa1.display = "none";
			}
		}
	}
	
	

	
	$(document).ready(function(){
		  $('[data-toggle="tooltip"]').tooltip();
		});
	function refresh () {
		location.reload();
		}
	function selectval(element){
		console.log(element.value);
		console.log(element.options[element.selectedIndex].text);
		var uname = element.value;
		var role = element.options[element.selectedIndex].text;
	      var url = "http://localhost:8080/Zoho/Convert"
	      var params = "uname="+uname+"&role="+role;
	      var xhr = new XMLHttpRequest();
	      xhr.addEventListener("load", refresh);
	      xhr.open('POST', url+"?"+params, true);
	      xhr.onload = function(){
	        if(xhr.status == 200){
	        	//document.getElementById("success").innerHTML = this.responseText;
	        }
	      }
	      xhr.send();
	}
	
	function changecanapplystatus(element){
		//console.log(element.value);
		//console.log(element.options[element.selectedIndex].text);
		//console.log(element.options[element.selectedIndex].id);
		var userName = element.value;
		var status = element.options[element.selectedIndex].text;
		var jobID = element.options[element.selectedIndex].id;
		var url = "http://localhost:8080/Zoho/ChangeApplyStatus";
        var params = "userName="+userName+"&status="+status+"&jobID="+jobID;
        var xhr = new XMLHttpRequest();
        xhr.addEventListener("load", refresh);
        xhr.open('POST', url+"?"+params, true);
        xhr.onload = function(){
          if(xhr.status == 200){
        	  //document.getElementById("success").innerHTML = this.responseText;
          }
        }
        xhr.send();
	}
	
	
	
	function changejobreq(element){
		//console.log(element.value);
		//console.log(element.options[element.selectedIndex].text);
		var jobID = element.value;
		var action = element.options[element.selectedIndex].text;
        var url = "http://localhost:8080/Zoho/ConvertJobs"
        var params = "jobID="+jobID+"&action="+action;
        var xhr = new XMLHttpRequest();
        xhr.addEventListener("load", refresh);
        xhr.open('POST', url+"?"+params, true);
        xhr.onload = function(){
          if(xhr.status == 200){
        	  //document.getElementById("success").innerHTML = this.responseText;
          }
        }
        xhr.send();
	}
	
	
	
	function changeres(element){
		//console.log(element.value);
		//console.log(element.id);
		//console.log(element.className);
		var limit = element.value;
		var type = element.id;
		var orgName = element.className;
        var url = "http://localhost:8080/Zoho/ChangeRestrictions"
        var params = "limit="+limit+"&type="+type+"&orgName="+orgName;
        var xhr = new XMLHttpRequest();
        xhr.addEventListener("load", refresh);
        xhr.open('POST', url+"?"+params, true);
        xhr.onload = function(){
          if(xhr.status == 200){
        	  //document.getElementById("success").innerHTML = this.responseText;
          }
        }
        xhr.send();
	}
	
	function hideerr(){
		document.getElementById("err").style.visibility="hidden";
	}
		setTimeout("hideerr()", 3000);
	


	
</script>
</body>
</html>