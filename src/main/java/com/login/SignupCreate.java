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
import java.sql.ResultSet;


public class SignupCreate extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String url = "jdbc:mysql://localhost:3306/test";
	private String user = "root";
	private String password = "root";
	private String recCode;
	private String adCode;
 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String orgName = request.getParameter("orgname");
			String uname = request.getParameter("uname");
			Connection con = DriverManager.getConnection(url, user, password);
			if(!exist(con, orgName)) {
				if(!existuser(con, uname)) {
					PreparedStatement st = con.prepareStatement("insert into users values(?, ?, ?, ?, ?, ?, ?, ?)");
					recCode = getCode(6);
					adCode = getCode(6);
					st.setString(1, request.getParameter("fname"));
					st.setString(2, request.getParameter("lname"));
					st.setString(3, uname);
					st.setString(4, request.getParameter("pass"));		
					st.setString(5, orgName);
					st.setString(6, "SuperAdmin");
					st.setString(7, adCode);
					st.setString(8, recCode);
					st.executeUpdate();
					st = con.prepareStatement("insert into restrictions(OrgName) values(?)");
					st.setString(1, orgName);
					st.executeUpdate();
					st.close();
					con.close();
					
					HttpSession session = request.getSession();
					session.setAttribute("FirstName", request.getParameter("fname"));
					session.setAttribute("LastName", request.getParameter("lname"));
					session.setAttribute("username", request.getParameter("uname"));
					session.setAttribute("orgName", orgName);
					session.setAttribute("recCode", recCode);
					session.setAttribute("adCode", adCode);
					session.setAttribute("role", "SuperAdmin");
					session.setAttribute("errorcre", null);
					
					response.sendRedirect("welcome.jsp");
				}
				else {
					HttpSession session = request.getSession();
					session.setAttribute("errorcre", "UserName exists!!");
					response.sendRedirect("signupcreate.jsp");
				}
			}else {
				HttpSession session = request.getSession();
				session.setAttribute("errorcre", "OrgName exists!!");
				response.sendRedirect("signupcreate.jsp");
			}
		} catch (Exception e) {
			response.sendRedirect("signupcreate.jsp");
			e.printStackTrace();
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
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
	
	public boolean exist(Connection con, String orgName) {
		try {
			PreparedStatement st = con.prepareStatement("select OrgName from users where OrgName=?");
			st.setString(1, orgName);
			ResultSet rs = st.executeQuery();
			if(rs.next())
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean existuser(Connection con, String uname) {
		try {
			PreparedStatement st = con.prepareStatement("select OrgName from users where UserName=?");
			st.setString(1, uname);
			ResultSet rs = st.executeQuery();
			if(rs.next())
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


}
