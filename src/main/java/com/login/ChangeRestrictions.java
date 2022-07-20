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
import java.sql.SQLException;

public class ChangeRestrictions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String url = "jdbc:mysql://localhost:3306/test";
	private String user = "root";
	private String password = "root";
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String orgName = request.getParameter("orgName");
		int limit = Integer.parseInt(request.getParameter("limit"));
		String type = request.getParameter("type");
		PreparedStatement st;
		try {
			try (Jedis jedis = new Jedis("localhost")) {
				Connection con = DriverManager.getConnection(url, user, password);
				if(type.equalsIgnoreCase("Reclimit")) {
					st = con.prepareStatement("update restrictions set Reclimit=? where OrgName=?");
					st.setInt(1, limit);
					st.setString(2, orgName);
					st.executeUpdate();
					HttpSession session = request.getSession();
					session.setAttribute("displaymsg", "Changed Reclimit");
					if(jedis.exists(orgName))
						jedis.hset(orgName, type, String.valueOf(limit));
				}
				else if(type.equalsIgnoreCase("Canapplylimit")) {
					st = con.prepareStatement("update restrictions set Canapplylimit=? where OrgName=?");
					st.setInt(1, limit);
					st.setString(2, orgName);
					st.executeUpdate();
					HttpSession session = request.getSession();
					session.setAttribute("displaymsg", "Changed Candidate Apply Limit");
					if(jedis.exists(orgName))
						jedis.hset(orgName, "Canlimit", String.valueOf(limit));
				}
				else if(type.equalsIgnoreCase("Canrestrictlimit")) {
					st = con.prepareStatement("update restrictions set Canrestrictlimit=? where OrgName=?");
					st.setInt(1, limit);
					st.setString(2, orgName);
					st.executeUpdate();
					HttpSession session = request.getSession();
					session.setAttribute("displaymsg", "Changed Candidate Restrict Limit");
					if(jedis.exists(orgName))
						jedis.hset(orgName, "Restrictlimit", String.valueOf(limit));
				}
			}
		} catch (Exception e) {
			response.sendRedirect("login.jsp");
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
