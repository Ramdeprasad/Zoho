package com.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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


public class CreateJob extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String url = "jdbc:mysql://localhost:3306/test";
	private String user = "root";
	private String password = "root";   

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jobName = request.getParameter("jobName");
		int vacancy = Integer.parseInt(request.getParameter("vacancy"));
		int experience = Integer.parseInt(request.getParameter("exp"));
		String require = request.getParameter("require");
		String orgName = request.getParameter("orgName");
		String rec = request.getParameter("rec");
		int limit=5, used=0;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, user, password);
			PreparedStatement st = con.prepareStatement("select Reclimit from restrictions where OrgName=?");
			st.setString(1, orgName);
			ResultSet rs = st.executeQuery();
			if(rs.next())
				limit = rs.getInt("Reclimit");
			
			st = con.prepareStatement("select sum(Opening) as sum from jobopening where RecUserName=?");
			st.setString(1, rec);
			rs = st.executeQuery();
			if(rs.next())
				used = rs.getInt("sum");
			
			//System.out.println(vacancy+" "+used+" "+limit);
			if(vacancy+used <= limit) {
				st = con.prepareStatement("insert into jobopening(OrgName, Opening, Experience, Requirement, JobName, RecUserName, Status) values(?, ?, ?, ?, ?, ?, 'Pending')");
				st.setString(1, orgName);
				st.setInt(2, vacancy);
				st.setInt(3, experience);
				st.setString(4, require);
				st.setString(5, jobName);
				st.setString(6, rec);
				st.executeUpdate();
				HttpSession session = request.getSession();
				session.setAttribute("displaymsg", "Job Opening Request Created");
				
				try (Jedis jedis = new Jedis("localhost")) {
					int update = Integer.parseInt(jedis.get("jobCount"))+1;
					jedis.set("jobCount", String.valueOf(update));
				}
			}
			else {
				HttpSession session = request.getSession();
				session.setAttribute("displaymsg", "Job Opening Request Limit Reached");
			}
			response.sendRedirect("welcome.jsp");
		}catch(Exception e) {
			e.printStackTrace();
			HttpSession session = request.getSession();
			session.invalidate();
			response.sendRedirect("home.jsp");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
