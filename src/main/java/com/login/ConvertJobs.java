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


public class ConvertJobs extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String url = "jdbc:mysql://localhost:3306/test";
	private String user = "root";
	private String password = "root";
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");	
			int jobID = Integer.parseInt(request.getParameter("jobID"));
			String action = request.getParameter("action");
			Connection con = DriverManager.getConnection(url, user, password);
			if(action.equalsIgnoreCase("Pending") || action.equalsIgnoreCase("Verified")) {
				PreparedStatement st = con.prepareStatement("update jobopening set Status=? where JobID=?");
				st.setString(1, action);
				st.setInt(2, jobID);
				st.executeUpdate();
				HttpSession session = request.getSession();
				session.setAttribute("displaymsg", "Changed Job Opening Status");

			}
			else if(action.equalsIgnoreCase("Delete")) {
				PreparedStatement st = con.prepareStatement("delete from jobopening where JobID=?");
				st.setInt(1, jobID);
				st.executeUpdate();
				HttpSession session = request.getSession();
				session.setAttribute("displaymsg", "Job Opening Deleted");
			}
		}catch (Exception e) {
			response.sendRedirect("login.jsp");
			e.printStackTrace();
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
