package com.login;

import jakarta.servlet.RequestDispatcher;
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
import java.sql.SQLException;
import java.sql.*;
public class SignupJoin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String url = "jdbc:mysql://localhost:3306/test";
	private String user = "root";
	private String password = "root";
	private String role;
	private String orgName;
	private String recCode = null;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, user, password);
			String code = request.getParameter("invcode");
			if(getDetails(code, con)) {
				PreparedStatement st = con.prepareStatement("insert into users values(?, ?, ?, ?, ?, ?, ?, ?)");
				st.setString(1, request.getParameter("fname"));
				st.setString(2, request.getParameter("lname"));
				st.setString(3, request.getParameter("uname"));
				st.setString(4, request.getParameter("pass"));
				st.setString(5, orgName);
				st.setString(6, role);
				st.setString(7, null);
				st.setString(8, recCode);
				st.executeUpdate();
				st.close();
				con.close();
				
				HttpSession session = request.getSession();
				session.setAttribute("username", request.getParameter("uname"));
				session.setAttribute("FirstName", request.getParameter("fname"));
				session.setAttribute("LastName", request.getParameter("lname"));
				session.setAttribute("orgName", orgName);
				session.setAttribute("recCode", recCode);
				session.setAttribute("role", role);
				session.setAttribute("errorjoi", null);
				response.sendRedirect("welcome.jsp");
			}
			else {
				HttpSession session = request.getSession();
				session.setAttribute("errorjoi", "Invalid Invite Code");
				response.sendRedirect("signupjoin.jsp");
			}
		} catch (Exception e) {
			response.sendRedirect("signupjoin.jsp");
			e.printStackTrace();
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}
	
	public boolean getDetails(String code, Connection con) {
		String recCode = null;
		String adminCode = null;
		try {
			PreparedStatement st = con.prepareStatement("select OrgName, Admincode, Reccode from users where Admincode=? or Reccode=?");
			st.setString(1, code);
			st.setString(2, code);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				adminCode = rs.getString("Admincode");
				recCode = rs.getString("Reccode");
				orgName = rs.getString("OrgName");
				if(adminCode != null && adminCode.equals(code)) {
						String newAdcode = getCode(6);
						st = con.prepareStatement("update users set Admincode=? where Admincode=?");
						st.setString(1, newAdcode);
						st.setString(2, adminCode);
						st.executeUpdate();
						role = "Admin";
						this.recCode = recCode;
						return true;
				}else if(recCode != null && recCode.equals(code)) {
						String newReccode = getCode(6);
						st = con.prepareStatement("update users set Reccode=? where Reccode=?");
						st.setString(1, newReccode);
						st.setString(2, recCode);
						st.executeUpdate();
						role = "Recruiter";
						this.recCode = null;
						return true;
				}
				st.close();
				con.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	private String getCode(int n)
    {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);
  
        for (int i = 0; i < n; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
  
        return sb.toString();
    }
	

}
