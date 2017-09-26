package com.database.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class DBConnectionTest
 */
@WebServlet("/DBConnectionTest")
public class DBConnectionTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
	// Define the datasource for resource injection
	@Resource(name="jdbc/StudentInfo") // This gives me the resource from context.xml
	
	private DataSource mySQLServerDS;
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		/*
		 * Step 1. Create printwriter
		 * Step 2. Get Database Connection
		 * Step 3. Create SQL Statment
		 * Step 4. Run sql statement
		 * Step 5. Process output (resultset)
		 */
		
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		
		Connection myDBConnection;
		Statement myStatement;
		ResultSet myResultSet;
		try
		{
			myDBConnection=mySQLServerDS.getConnection();
			String sql="select * from StudentTable";
			myStatement = myDBConnection.createStatement();
			myResultSet=myStatement.executeQuery(sql);
			while(myResultSet.next())
			{
				String name=myResultSet.getString("FirstName");
				String email=myResultSet.getString("Email");
				out.println("Name: "+name+" Email: "+email);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	

}
