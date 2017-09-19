package com.udemy.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.udemy.utils.CreateDBConnection;

/**
 * Servlet implementation class DBConnection
 */
@WebServlet("/DBConnection")
public class DBConnection extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DBConnection() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		CreateDBConnection dbC = new CreateDBConnection();
		String dbHostName=request.getParameter("dbHost");
		String dbPort=request.getParameter("dbPort");
		PrintWriter out = response.getWriter();
		
		// Getting parameter information from web.xml
		
		ServletContext scContext = getServletContext();
		String customerName=scContext.getInitParameter("conversion-customer-name");
		String conversionPhase = scContext.getInitParameter("conversion-phase-name");
		
		try 
		{
			Connection oraConnection=dbC.connect2Oracle(dbHostName, dbPort, "aedbadmin", "Test1234", "SP3");
			DatabaseMetaData meta = oraConnection.getMetaData();
			
			out.println("<html><body>");
			out.println("<h1>"+customerName+"</h1>");
			out.println("<br />");
			out.println("<h2>"+conversionPhase+"</h2");
			out.println("<br />");
			out.println("<h3> Connected to: "+dbHostName+"</h3>");
			out.println("<br />");
			out.println("Driver Information: "+meta.getDriverName());
			out.println("<br />");
			out.println("Vendor Name: "+meta.getDatabaseProductName());
			out.println("<br />");
			out.println("Database Version: "+meta.getDatabaseProductVersion());
			out.println("<br />");
			out.println("</body></html>");
		} 
		catch (ClassNotFoundException | SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
