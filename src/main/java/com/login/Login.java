package com.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String url = "jdbc:mysql://localhost:3306/test";
	private String user = "root";
	private String password = "root";
	private boolean temp = false;
	private String userScript;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uname = request.getParameter("uname");
		String pass = request.getParameter("pass");
		if(verify(uname, pass)) {
			setSession(request, uname);
			response.sendRedirect("welcome.jsp");
		}
		else if(empverify(request, uname, pass)) {
			response.sendRedirect("welcome.jsp");
		}
		else {
//			request.setAttribute("error", "Invalid Credentials");
//			RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
//			rd.forward(request, response);
			HttpSession session = request.getSession();
			session.setAttribute("errorlog", "Invalid Credentials");
			response.sendRedirect("login.jsp");
		}
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public boolean verify(String uname, String pass) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, user, password);
			PreparedStatement st = con.prepareStatement("select Roles from users where UserName=? and Pass_word=?");
			st.setString(1, uname);
			st.setString(2, pass);
			
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				if(rs.getString("Roles").equals("Pending"))
					temp = true;
				return true;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return false;
	}
	public void setSession(HttpServletRequest request, String uname) {
		try {
			Date start = new Date();
		    long startms = start.getTime();
			
			
			try (Jedis jedis = new Jedis("localhost")) {
				if(!jedis.exists("UserProp")) {
					Map<String, Double> userProp = new HashMap<>();
					userProp.put("FirstName", 1.0);
					userProp.put("LastName", 2.0);
					userProp.put("UserName", 3.0);
					userProp.put("OrgName", 4.0);
					userProp.put("Roles", 5.0);
					userProp.put("Admincode", 6.0);
					userProp.put("Reccode", 7.0);
					jedis.zadd("UserProp", userProp);
//					System.out.println("userProp added!!");
				}
				
				
				if(userScript == null || !jedis.scriptExists(userScript)) {
					userScript = jedis.scriptLoad("local p = redis.call('zrange', KEYS[1], 0, -1); return redis.call('hmget', KEYS[2], unpack(p))");
//					System.out.println("Script created");
				}
				if(jedis.exists(uname)) {	
					fetchFromRedis(jedis, request, uname);
					Date redis = new Date();
				    long redis_ms = redis.getTime();
				    System.out.println("Redis Login Fetch (ms):" + (redis_ms - startms));
					return;	
				}
				else {
					Class.forName("com.mysql.cj.jdbc.Driver");
					Connection con = DriverManager.getConnection(url, user, password);
					PreparedStatement st = con.prepareStatement("select FirstName,LastName,OrgName,Admincode,Reccode,Roles from users where username=?");
					st.setString(1, uname);
					ResultSet rs = st.executeQuery();
					HttpSession session = request.getSession();
					if(rs.next()) {
						Map<String, String> prop = new HashMap<>();
						session.setAttribute("username", uname);
						prop.put("UserName", uname);
						
						session.setAttribute("FirstName", rs.getString("FirstName"));
						prop.put("FirstName", rs.getString("FirstName"));
						
						session.setAttribute("LastName", rs.getString("LastName"));
						prop.put("LastName", rs.getString("LastName"));
						
						session.setAttribute("orgName", rs.getString("OrgName"));
						prop.put("OrgName", rs.getString("OrgName"));
						
						session.setAttribute("recCode", rs.getString("Reccode"));
						if(rs.getString("Reccode") != null)
							prop.put("Reccode", rs.getString("Reccode"));
						
						session.setAttribute("adCode", rs.getString("Admincode"));
						if(rs.getString("Admincode") != null)
							prop.put("Admincode", rs.getString("Admincode"));
						
						session.setAttribute("errorlog", null);
						if(temp) {
							session.setAttribute("role", "Pending");
							prop.put("Roles", "Pending");
						}else {
							session.setAttribute("role", rs.getString("Roles"));
							prop.put("Roles", rs.getString("Roles"));
						}
						jedis.sadd("users", uname);
						jedis.hmset(uname, prop);
					}
//					System.out.println("UserName added!!");
					Date db = new Date();
				    long db_ms = db.getTime();
				    System.out.println("DB Login Fetch (ms):" + (db_ms - startms));
				}
			}

				
				
//			if(userScript == null) {
//				userScript = jedis.scriptLoad("local p = redis.call('zrange', KEYS[1], 0, -1); return redis.call('hmget', KEYS[2], unpack(p))");	
//			}
//			List<String> prop = new ArrayList<>();
//			prop = (List<String>) jedis.evalsha(userScript, 2, new String[] {"UserProp", "ram"});

//			Class.forName("com.mysql.cj.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, password);
//			PreparedStatement st = con.prepareStatement("select FirstName,LastName,OrgName,Admincode,Reccode,Roles from users where username=?");
//			st.setString(1, uname);
//			ResultSet rs = st.executeQuery();
//			HttpSession session = request.getSession();
//			if(rs.next()) {
//				session.setAttribute("username", uname);
//				session.setAttribute("FirstName", rs.getString("FirstName"));
//				session.setAttribute("LastName", rs.getString("LastName"));
//				session.setAttribute("orgName", rs.getString("OrgName"));
//				session.setAttribute("recCode", rs.getString("Reccode"));
//				session.setAttribute("adCode", rs.getString("Admincode"));
//				session.setAttribute("errorlog", null);
//				if(temp) {
//					session.setAttribute("role", "Pending");
//				}else {
//					session.setAttribute("role", rs.getString("Roles"));
//				}
//			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean empverify(HttpServletRequest request, String uname, String pass) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, user, password);
			PreparedStatement st = con.prepareStatement("select * from emp where EUserName=? and Pass_word=?");
			st.setString(1, uname);
			st.setString(2, pass);
			
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				HttpSession session = request.getSession();
				session.setAttribute("username", uname);
				session.setAttribute("FirstName", rs.getString("FirstName"));
				session.setAttribute("LastName", rs.getString("LastName"));
				session.setAttribute("orgName", rs.getString("OrgName"));
				session.setAttribute("role", "Employee");
				session.setAttribute("errorlog", null);
				return true;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public void fetchFromRedis(Jedis jedis, HttpServletRequest request, String uname) {
//		System.out.println("UserName Hit");
		List<String> prop = new ArrayList<>();
		prop = (List<String>) jedis.evalsha(userScript, 2, new String[] {"UserProp", uname});
//		for(String i: prop) {
//			System.out.println(i);
//		}
		HttpSession session = request.getSession();
		session.setAttribute("username", uname);
		session.setAttribute("FirstName", prop.get(0));
		session.setAttribute("LastName", prop.get(1));
		session.setAttribute("orgName", prop.get(3));
		session.setAttribute("recCode", prop.get(6));
		session.setAttribute("adCode", prop.get(5));
		session.setAttribute("role", prop.get(4));
		session.setAttribute("errorlog", null);
	}

}
