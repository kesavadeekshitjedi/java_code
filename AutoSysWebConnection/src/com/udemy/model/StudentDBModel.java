package com.udemy.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.udemy.student.Student;

public class StudentDBModel 
{
	// Thsi uses the context.xml file for DS
	// Also uses the com.udemy.student.Student.java for the Student class
	
	private DataSource mySQLServerDS;
	
	public StudentDBModel(DataSource myDS)
	{
		mySQLServerDS=myDS;
	}
	
	public List<Student> getStudents() throws Exception
	{
		List<Student> myStudentList = new ArrayList<>();
		// Create the connection to the DB
		
		Connection dbConn = null;
		Statement dbStatement = null;
		ResultSet dbRS = null;
		try
		{
			dbConn=mySQLServerDS.getConnection();
			String sql="select * from StudentTable order by lastName";
			dbStatement = dbConn.createStatement();
			dbRS=dbStatement.executeQuery(sql);
			while(dbRS.next())
			{
				String firstName=dbRS.getString("FirstName");
				String lastName=dbRS.getString("LastName");
				String emailAddress = dbRS.getString("Email");
				
				Student myTempStudent = new Student(firstName,lastName,emailAddress);
				myStudentList.add(myTempStudent);
			}
		}
		finally
		{
			dbRS.close();
			dbStatement.close();
			dbConn.close();
		}
		return myStudentList;
	}

}
