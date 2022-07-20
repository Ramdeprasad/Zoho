package com.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Servlet implementation class GetAppliedRole
 */
public class GetAppliedRole extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String url = "jdbc:mysql://localhost:3306/test";
	private String user = "root";
	private String password = "root";  
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String domTable = "<table id='req' class='table table-responsive table-hover' style='display:none;'><thead><th>Orgaization Name</th><th>Job Type</th><th>Status</th></thead><tbody>";
		PrintWriter out = response.getWriter();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, user, password);
			String uname = request.getParameter("uname");
			PreparedStatement st = con.prepareStatement("select A.JobName,A.OrgName, B.Status from jobopening A, candidateapply B where A.JobID = B.JobID and B.UserName=?");
			st.setString(1, uname);
			ResultSet rs = st.executeQuery();			
			while(rs.next()) {				
				domTable+="<tr>"
						+ "<td>"+rs.getString("A.OrgName")+"</td>"
						+ "<td>"+rs.getString("A.JobName")+"</td>"
						+ "<td>"+rs.getString("B.Status")+"</td>"
						+ "</tr>";	
			}
			domTable+="</tbody></table>";
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.println(domTable);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
