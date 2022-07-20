package com.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.*;
public class Convert extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String url = "jdbc:mysql://localhost:3306/test";
	private String user = "root";
	private String password = "root";
	private String role;
	private String recCode = null;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");	
			role = (String)request.getParameter("role");
			String userName = request.getParameter("uname");
			Connection con = DriverManager.getConnection(url, user, password);
			if(role.equalsIgnoreCase("Admin")) {
				PreparedStatement st = con.prepareStatement("select OrgName from users where UserName=?");
				st.setString(1, userName);
				ResultSet rs = st.executeQuery();
				if(rs.next()) {
					String org = rs.getString("OrgName");
					recCode = getrecCode(con, org);
				}
			}
			PreparedStatement st = con.prepareStatement("update users set Roles=?, Reccode=? where UserName=?");
			st.setString(1, role);
			st.setString(2, recCode);
			st.setString(3, userName);
			st.executeUpdate();
			HttpSession session = request.getSession();
			session.setAttribute("displaymsg", "Convert position successful");
			
		}catch (Exception e) {
			response.sendRedirect("login.jsp");
			e.printStackTrace();
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public String getrecCode(Connection con, String orgname) {
		try {
			PreparedStatement st = con.prepareStatement("select Reccode from users where OrgName=? and Roles='SuperAdmin'");
			st.setString(1, orgname);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				return rs.getString("Reccode");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
