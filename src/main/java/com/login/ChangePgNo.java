package com.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class ChangePgNo extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		System.out.println(request.getParameter("tableNo"));
//		System.out.println(request.getParameter("direction"));
//		System.out.println(request.getParameter("currPg"));
		
		String table = request.getParameter("tableNo");
		String direction = request.getParameter("direction");
		int pgNo = request.getParameter("currPg") == null? 0 : Integer.parseInt(request.getParameter("currPg"));
		HttpSession session = request.getSession();
		session.setAttribute("tableNo", table);
			
		if(direction.equalsIgnoreCase("Next"))
			session.setAttribute("pageNo", pgNo+1);
		else if(direction.equalsIgnoreCase("Prev"))
			if(pgNo <= 0)
				session.setAttribute("pageNo", 0);
			else
				session.setAttribute("pageNo", pgNo-1); 
		response.sendRedirect("welcome.jsp");
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
