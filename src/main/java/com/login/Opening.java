package com.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.plaf.basic.BasicSplitPaneUI;

import java.io.PrintWriter;

public class Opening extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String url = "jdbc:mysql://localhost:3306/test";
	private String user = "root";
	private String password = "root";
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String query = request.getParameter("query");
		String domTable = "<table border = 1 background-color: '#D6EEEE' class='table'><thead><th>Orgaization Name</th><th>Job Name</th><th>Vacancies</th><th>Experience(Year)</th><th>Requirement</th><th>Apply</th></thead><tbody>";
		PrintWriter out = response.getWriter();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, user, password);
			PreparedStatement st;
			if(query.contains(":")) {
				String[] splitstr = query.split(":", 2);
				System.out.println(splitstr[0]);
				System.out.println(splitstr[1]);
				String sql = "select JobID, Opening, Experience, Requirement,OrgName, JobName from jobopening where Status='Verified' and Opening!=0 and "+splitstr[0]+"='"+splitstr[1]+"'";
//				System.out.println(sql);
				st = con.prepareStatement(sql);
//				st.setString(1, splitstr[0].strip());
//				st.setString(2, splitstr[1].strip());
			}
			else {
				st = con.prepareStatement("select JobID, Opening, Experience, Requirement,OrgName, JobName from jobopening where Status='Verified' and Opening!=0");
			}
			ResultSet rs = st.executeQuery();			
			while(rs.next()) {				
				domTable+="<tr>"
						+ "<td>"+rs.getString("OrgName")+"</td>"
						+ "<td>"+rs.getString("JobName")+"</td>"
						+ "<td>"+rs.getInt("Opening")+"</td>"
						+ "<td>"+rs.getInt("Experience")+"</td>"
						+ "<td>"+rs.getString("Requirement")+"</td>"
						+ "<td><form action='CandidateApply'><input hidden name='jobID' value="+rs.getInt("JobID")+" /><button type='submit' id='apply'>Apply</button></form></td>"
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
