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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.util.Properties;
import javax.mail.Authenticator;
//import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
import com.twilio.rest.api.v2010.account.Message;


public class ChangeApplyStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String url = "jdbc:mysql://localhost:3306/test";
	private String user = "root";
	private String password = "root";
	private final String ACCOUNT_SID="";
	private final String AUTH_TOKEN="";
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userName = request.getParameter("userName");
		int jobID = Integer.parseInt(request.getParameter("jobID"));
		String status = request.getParameter("status");
		String orgName = null;
		String recName = null;
		PreparedStatement st;
		try {
			Connection con = DriverManager.getConnection(url, user, password);
			if(status.equalsIgnoreCase("Pending")) {
				System.out.println(status);
				st = con.prepareStatement("update candidateapply set Status=? where UserName=? and JobID=?");
				st.setString(1, status);
				st.setString(2, userName);
				st.setInt(3, jobID);
				st.executeUpdate();
				HttpSession session = request.getSession();
				session.setAttribute("displaymsg", "Changed Candidate Apply Status");
			}
			else if(status.equalsIgnoreCase("Accept")) {
				System.out.println(status);
				int opening=0;
				st = con.prepareStatement("select Opening from jobopening where JobID=?");
				st.setInt(1, jobID);
				ResultSet rs = st.executeQuery();
				if(rs.next())
					opening = rs.getInt("Opening");
				
				if(opening > 0) {
					st = con.prepareStatement("update jobopening set Opening=? where JobID=?");
					st.setInt(1, opening-1);
					st.setInt(2, jobID);
					st.executeUpdate();
					
					st = con.prepareStatement("update candidateapply set Status=? where UserName=? and JobID=?");
					st.setString(1, status);
					st.setString(2, userName);
					st.setInt(3, jobID);
					st.executeUpdate();
					HttpSession session = request.getSession();
					session.setAttribute("displaymsg", "Candidate Application Accepted");
				}
				else {
					HttpSession session = request.getSession();
					session.setAttribute("displaymsg", "All Job Openings have been filled");
				}
			}
			else if(status.equalsIgnoreCase("Reject")) {
				System.out.println(status);
				st = con.prepareStatement("select Status from candidateapply where UserName=? and JobID=?");
				st.setString(1, userName);
				st.setInt(2, jobID);
				ResultSet rs =st.executeQuery();
				String oldStatus = null;
				boolean x = false;
				if(rs.next())
					oldStatus = rs.getString("Status");
								
				
				st = con.prepareStatement("delete from candidateapply where UserName=? and JobID=?");
				st.setString(1, userName);
				st.setInt(2, jobID);
				st.executeUpdate();
				
				
				st = con.prepareStatement("select OrgName from jobopening where JobID=?");
				st.setInt(1, jobID);
				rs = st.executeQuery();
				if(rs.next())
					orgName = rs.getString("OrgName");
				
				//add orgname
				st = con.prepareStatement("select UserName from blacklist where UserName=? and OrgName=?");
				st.setString(1, userName);
				st.setString(1, orgName);
				if(rs.next())
					x = true;
				
				if(x) {
					st = con.prepareStatement("update blacklist set RejectDate=curdate() where UserName=? and OrgName=?");
					st.setString(1, userName);
					st.setString(2, orgName);
					st.executeUpdate();
				}
				else {
					st = con.prepareStatement("insert into blacklist values(?, ?, 'false', curdate())");
					st.setString(1, userName);
					st.setString(2, orgName);
					st.executeUpdate();
				}
				
				if(oldStatus.equalsIgnoreCase("Accept")) {
					int opening=0;
					st = con.prepareStatement("select Opening from jobopening where JobID=?");
					st.setInt(1, jobID);
					rs = st.executeQuery();
					if(rs.next())
						opening = rs.getInt("Opening");

					st = con.prepareStatement("update jobopening set Opening=? where JobID=?");
					st.setInt(1, opening+1);
					st.setInt(2, jobID);
					st.executeUpdate();
					HttpSession session = request.getSession();
					session.setAttribute("displaymsg", "Candidate Application Rejected");
				}
			}
			else if(status.equalsIgnoreCase("Blacklist")) {
				System.out.println(status);
				freejobopening(con, jobID, userName);
				st = con.prepareStatement("select OrgName from jobopening where JobID=?");
				st.setInt(1, jobID);
				ResultSet rs = st.executeQuery();
				if(rs.next())
					orgName = rs.getString("OrgName");
				boolean x = false;
				st = con.prepareStatement("select UserName from blacklist where UserName=?");
				st.setString(1, userName);
				rs = st.executeQuery();
				if(rs.next())
					x = true;
				
				if(x) {
					st = con.prepareStatement("update blacklist set Blacklist='true' where UserName=? and OrgName=?");
					st.setString(1, userName);
					st.setString(2, orgName);
					st.executeUpdate();
				}
				else {
					st = con.prepareStatement("insert into blacklist values(?, ?, 'true', null)");
					st.setString(1, userName);
					st.setString(2, orgName);
					st.executeUpdate();
				}
								
				HttpSession session = request.getSession();
				session.setAttribute("displaymsg", "Candidate Blacklisted");
				
				
			}
			else if(status.equalsIgnoreCase("Employee")) {
				String jobName = null;
				st = con.prepareStatement("select * from candidates where UserName=?");
				st.setString(1, userName);
				ResultSet rs = st.executeQuery();
				String fname=null, lname=null, pass=null;
				if(rs.next()) {
					fname = rs.getString("FirstName");
					lname = rs.getString("LastName");
					pass = rs.getString("Pass_Word");
				}
				
				st = con.prepareStatement("select OrgName, JobName, RecUserName from jobopening where JobID=?");
				st.setInt(1, jobID);
				rs = st.executeQuery();
				if(rs.next()) {
					orgName = rs.getString("OrgName");
					jobName = rs.getString("JobName");
					recName = rs.getString("RecUserName");
				}
				
				st = con.prepareStatement("insert into emp values(?, ?, ?, ?, ?, ?, ?, ?)");
				st.setString(1, userName);
				st.setString(2, fname);
				st.setString(3, lname);
				st.setString(4, pass);
				st.setString(5, orgName);
				st.setInt(6, jobID);
				st.setString(7, jobName);
				st.setString(8, recName);
				st.executeUpdate();
				
				
				//Dont add count for this job alone
				st = con.prepareStatement("delete from candidateapply where UserName=? and jobID=?");
				st.setString(1, userName);
				st.setInt(2, jobID);
				st.executeUpdate();
				
				//inc count for other accepted job for same candidate in same org
				freejobopening(con, jobID, userName);
				
				
				//del jobs even in other organization
				st = con.prepareStatement("select JobID from candidateapply where UserName=? and Status='Accept'");
				st.setString(1, userName);
				rs = st.executeQuery();
				while(rs.next()) {
					int opening = 0;
					st = con.prepareStatement("select Opening from jobopening where JobID=?");
					st.setInt(1, rs.getInt(jobID));
					ResultSet rs1 = st.executeQuery();
					if(rs1.next()) {
						opening = rs1.getInt("Opening");
						st = con.prepareStatement("update jobopening set Opening=? where JobID=?");
						st.setInt(1, opening+1);
						st.setInt(1, rs.getInt(jobID));
					}	
				}
				
				st = con.prepareStatement("delete from candidateapply where UserName=?");
				st.setString(1, userName);
				st.executeUpdate();
				
				
				
				//del pending req for other emp if opening=0
				st = con.prepareStatement("select count(*) as count from candidateapply where JobID=? and Status='Accept'");
				st.setInt(1, jobID);
				rs = st.executeQuery();
				if(rs.next()) {
					int count = rs.getInt("count");
					System.out.println(count);
					if(count == 0) {
						st = con.prepareStatement("delete from candidateapply where JobID=? and Status='Pending'");
						st.setInt(1, jobID);
						st.executeUpdate();
						
						try (Jedis jedis = new Jedis("localhost")) {
							int update = Integer.parseInt(jedis.get("jobCount"))-1;
							jedis.set("jobCount", String.valueOf(update));
						}
					}
				}
				
				
				//sendmail("ramdeprasad@gmail.com", userName);
				
				sendMsg(userName, orgName);
				
				
				HttpSession session = request.getSession();
				session.setAttribute("displaymsg", "Candidate converted to Employee Mail sent!!");
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
	
//	public void sendmail(String recepient, String recuserName) {
//		Properties properties = new Properties();
//		properties.put("mail.smtp.auth", "true");	
//		properties.put("mail.smtp.starttls.enable", "true");
//		properties.put("mail.smtp.host", "smtp.gmail.com");
//		properties.put("mail.smtp.port", "587");
//		
//	    String sendermail = "sender@gmail.com";
//	    String senderpass = "senderpass";
//	    
//	    Session session = Session.getInstance(properties, new Authenticator() {
//	    	
//	    	@Override
//	    	protected PasswordAuthentication getPasswordAuthentication() {
//	    		return new PasswordAuthentication(sendermail, senderpass);
//	    	}
//	    });
//	    
//	    
//	    Message message = prepareMessage(session, sendermail, recepient, recuserName);
//	    try {
//			Transport.send(message);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	    System.out.println("Message sent");
//	}
	
//	private Message prepareMessage(Session session, String sendermail, String recepient, String recUserName) {
//		try {
//			Message message = new MimeMessage(session);
//			message.setFrom(new InternetAddress(sendermail));
//			message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
//			message.setSubject("Congrats on being converted to Employee");
//			message.setText("Hi"+recUserName+"\nYour pay is set at 10lpa fro joining the company");
//			return message;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	
	
	public void freejobopening(Connection con, int jobID, String userName) {
		String orgName = null;
		try {
			PreparedStatement st = con.prepareStatement("select OrgName from jobopening where JobID=?");
			st.setInt(1, jobID);
			ResultSet rs = st.executeQuery();
			if(rs.next())
				orgName = rs.getString("OrgName");
			
			List<Integer> jobids = new ArrayList<>();
			List<String> statuses = new ArrayList<>();
			st = con.prepareStatement("select JobID from jobopening where OrgName=?");
			st.setString(1, orgName);
			rs = st.executeQuery();
			while(rs.next()) {
				jobids.add(rs.getInt("JobID"));
			}
			
			
			for(int j : jobids) {
				st = con.prepareStatement("select Status from candidateapply where UserName=? and JobID=?");
				st.setString(1, userName);
				st.setInt(2, j);
				rs = st.executeQuery();
				if(rs.next())
					statuses.add(rs.getString("Status"));
				else
					statuses.add(null);
			}
			
			
			st = con.prepareStatement("delete from candidateapply where UserName=? and JobID in (select JobID from jobopening where OrgName=?)");
			st.setString(1, userName);
			st.setString(2, orgName);
			st.executeUpdate();
			
			
			for(int i=0; i<jobids.size(); i++) {
				if(statuses.get(i) != null && statuses.get(i).equalsIgnoreCase("Accept")) {
					st = con.prepareStatement("select Opening from jobopening where JobID=?");
					st.setInt(1, jobids.get(i));
					int opening = 0;
					if(rs.next())
						opening  = rs.getInt("Opening");
					st = con.prepareStatement("update jobopening set Opening=? where JobID=?");
					st.setInt(1, opening+1);
					st.setInt(2, jobids.get(i));
					st.executeUpdate();
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendMsg(String userName, String orgName) {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

	    Message message = Message.creator(
	    		new PhoneNumber("+918946036515"),
	    		new PhoneNumber("+14087067083"), 
	    		"A candidate with UserName "+userName+" has been converted to employee in "+orgName
	    		).create();

	    System.out.println(message.getSid());
	}

}
