package com.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class candidateLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String url = "jdbc:mysql://localhost:3306/test";
	private String user = "root";
	private String password = "root";
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uname = request.getParameter("uname");
		String pass = request.getParameter("pass");
		String fname = request.getParameter("fname");
		String lname = request.getParameter("lname");
		String jobID = request.getParameter("jobID");
		System.out.println("f"+fname);
		System.out.println("l"+lname);
		System.out.println("u"+uname);
		System.out.println("p"+pass);
		System.out.println("j"+jobID);
		Jedis jedis = new Jedis("localhost");
		
		if(usersexist(jedis, request, uname)) {
			response.sendRedirect("candidateLogin.jsp");
		}
		
		
		
		else if(!fname.isEmpty()) {
			System.out.println("fname empty");
			if(checkusername(jedis, uname)) {
				HttpSession session = request.getSession();
				session.setAttribute("errorclog", "UserName exist");
				response.sendRedirect("candidateLogin.jsp");
			}
			else {
				createcandidate(jedis, fname, lname, uname, pass);
				if(!jobID.equals("null")) {
					System.out.println("fname job");
					applyjob(uname, jobID, request, response);
				}
				HttpSession session = request.getSession();
				session.setAttribute("username", uname);
				session.setAttribute("FirstName", fname);
				session.setAttribute("LastName", lname);
				session.setAttribute("errorclog", null);
				response.sendRedirect("candidateWelcome.jsp");
			}
		}
		else if(verify(uname, pass)) {
			System.out.println("login");
				if(!jobID.equals("null")) {
					System.out.println("user job");
					applyjob(uname, jobID, request, response);
				}
				HttpSession session = request.getSession();
				session.setAttribute("username", uname);
				session.setAttribute("FirstName", fname);
				session.setAttribute("LastName", lname);
				session.setAttribute("role", "Candidate");
				session.setAttribute("errorclog", null);
				response.sendRedirect("candidateWelcome.jsp");
			}
			else {
				HttpSession session = request.getSession();
				session.setAttribute("errorclog", "Invalid Credentials");
				response.sendRedirect("candidateLogin.jsp");
				
			}	
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public boolean verify(String uname, String pass) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, user, password);
			PreparedStatement st = con.prepareStatement("select UserName from candidates where UserName=? and Pass_word=?");
			st.setString(1, uname);
			st.setString(2, pass);
			
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				return true;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	
	public boolean usersexist(Jedis jedis, HttpServletRequest request, String uname) {
		try {
			Date start = new Date();
		    long startms = start.getTime();
		    if(jedis.sismember("users", uname) || jedis.sismember("emp", uname)) {
		    	Date redis = new Date();
			    long redis_ms = redis.getTime();
			    System.out.println("Redis UserName Exist (ms):" + (redis_ms - startms));
				return true;
		    }			
			else {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection con = DriverManager.getConnection(url, user, password);
				PreparedStatement st = con.prepareStatement("select UserName from users where UserName=?");
				st.setString(1, uname);
				ResultSet rs = st.executeQuery();
				if(rs.next()) {
					HttpSession session = request.getSession();
					session.setAttribute("errorclog", "Use login form");
					jedis.sadd("users", uname);
					return true;
				}
				st = con.prepareStatement("select EUserName from emp where EUserName=?");
				st.setString(1, uname);
				rs = st.executeQuery();
				if(rs.next()) {
					HttpSession session = request.getSession();
					session.setAttribute("errorclog", "Your now Employee..Use Login form");
					jedis.sadd("emp", uname);
					return true;
				}
				Date db = new Date();
			    long db_ms = db.getTime();
			    System.out.println("DB UserName Exist (ms):" + (db_ms - startms));
			}
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	
	
	
	public boolean checkusername(Jedis jedis, String uname) {
		try {
			if(jedis.sismember("can", uname))
				return true;
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, user, password);
			PreparedStatement st = con.prepareStatement("select UserName from candidates where UserName=?");
			st.setString(1, uname);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				jedis.sadd("can", uname);
				return true;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	
	
	public void createcandidate(Jedis jedis, String fname, String lname, String uname, String pass) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, user, password);
			PreparedStatement st = con.prepareStatement("insert into candidates values(?, ?, ?, ?)");
			st.setString(1, uname);
			st.setString(2, fname);
			st.setString(3, lname);
			st.setString(4, pass);
			st.executeUpdate();
			Map<String, String> prop = new HashMap<>();
			prop.put("FirstName", fname);
			prop.put("LastName", lname);
			prop.put("UserName", uname);
			jedis.hmset(uname, prop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void applyjob(String userName, String jobID, HttpServletRequest request, HttpServletResponse response) {
		try {
			int applied=0, limit=5;
			int job = Integer.parseInt(jobID);
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, user, password);
			PreparedStatement st = con.prepareStatement("select Blacklist, RejectDate from blacklist where UserName=? and OrgName=(select Orgname from jobopening where JobID=?)");
			st.setString(1, userName);
			st.setInt(2, job);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				//blacklist
				if(((String)rs.getString("Blacklist")).equals("true")) {
					HttpSession session = request.getSession();
					session.setAttribute("errorcanwel", "You are BLACKLISTED");
					return;
				}
				
				st = con.prepareStatement("select JobID from candidateapply where UserName=? and JobID=?");
				st.setString(1, userName);
				st.setInt(2, job);
				rs = st.executeQuery();
				if(rs.next()) {
					HttpSession session = request.getSession();
					session.setAttribute("errorcanwel", "Already applied");
					return;
				}
				
				
				//restrict
				int restrictmonth = 0;
				st = con.prepareStatement("select Canrestrictlimit from restrictions where OrgName = (select OrgName from jobopening where JobID=?)");
				st.setInt(1, job);
				rs = st.executeQuery();
				if(rs.next())
					restrictmonth = rs.getInt("Canrestrictlimit");
				
				st = con.prepareStatement("select * from blacklist where datediff(curdate(),str_to_date(RejectDate,'%Y-%m-%d'))>? and UserName=? and OrgName=(select OrgName from jobopening where JobID=?)");
				st.setInt(1, restrictmonth*28);
				st.setString(2, userName);
				st.setInt(3, job);
				rs = st.executeQuery();
				if(!rs.next()) {
					HttpSession session = request.getSession();
					session.setAttribute("errorcanwel", "You are RESTRICTED");
					return;
				}
			}
			
			
			
			st = con.prepareStatement("select count(*) as count from candidateapply where UserName=?");
			st.setString(1, userName);
			rs = st.executeQuery();
			if(rs.next())
				applied = rs.getInt("count");
			
			st = con.prepareStatement("select B.Canapplylimit as climit from jobopening A, restrictions B where A.OrgName = B.OrgName and jobID=?");
			st.setInt(1, job);
			rs = st.executeQuery();
			if(rs.next())
				limit = rs.getInt("climit");
			
			if(applied < limit) {
				st = con.prepareStatement("insert into candidateapply values(?, ?, 'Pending')");
				st.setString(1, userName);
				st.setInt(2, job);
				st.executeUpdate();
			}
			else {
				HttpSession session = request.getSession();
				session.setAttribute("errorcanwel", "Max Threshold Reached");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
