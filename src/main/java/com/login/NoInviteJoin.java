package com.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NoInviteJoin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String url = "jdbc:mysql://localhost:3306/test";
	String user = "root";
	String password = "root";
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, user, password);
			String orgName = request.getParameter("orgname");
			if(verify(orgName, con)) {
				PreparedStatement st = con.prepareStatement("insert into users values(?, ?, ?, ?, ?, ?, ?, ?)");
				st.setString(1, request.getParameter("fname"));
				st.setString(2, request.getParameter("lname"));
				st.setString(3, request.getParameter("uname"));
				st.setString(4, request.getParameter("pass"));		
				st.setString(5, orgName);
				st.setString(6, "Pending");
				st.setString(7, null);
				st.setString(8, null);
				st.executeUpdate();
				st.close();
				
				HttpSession session = request.getSession();
				session.setAttribute("username", request.getParameter("uname"));
				session.setAttribute("FirstName", request.getParameter("fname"));
				session.setAttribute("LastName", request.getParameter("lname"));
				session.setAttribute("orgName", orgName);
				session.setAttribute("role", "Pending");
				session.setAttribute("errornoi", null);
				response.sendRedirect("welcome.jsp");
			}
			else {
				HttpSession session = request.getSession();
				session.setAttribute("errornoi", "Invalid OrgName");
				response.sendRedirect("signupjoin.jsp");
			}
		} catch (Exception e) {
			response.sendRedirect("noinvite.jsp");
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public boolean verify(String org, Connection con) {
		try {
			PreparedStatement st = con.prepareStatement("select OrgName from users where OrgName=?");
			st.setString(1, org);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				st.close();
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
