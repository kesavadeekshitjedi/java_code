package com.udemy.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

	public void deleteStudent(Student studentInfo) throws SQLException
	{
		
	}
	public void updateStudent(Student studentInfo) throws SQLException
	{
		Connection dbConn=null;
		PreparedStatement dbStatement=null;
		
		try
		{
			dbConn=mySQLServerDS.getConnection();
			String sql="update StudentTable";
		}
		finally
		{
			dbStatement.close();
			dbConn.close();
		}
	}
	public void addStudent(Student studentInfo) throws SQLException 
	{
		Connection dbConn = null;
		PreparedStatement dbStatement = null;
		String sql="insert into StudentTable (FirstName, LastName, Email) values(?,?,?)";
		
		try
		{
			dbConn=mySQLServerDS.getConnection();
			dbStatement=dbConn.prepareStatement(sql);
			dbStatement.setString(1,studentInfo.getFirstName());
			dbStatement.setString(2,studentInfo.getLastName());
			dbStatement.setString(3, studentInfo.getEmailAddress());
			dbStatement.execute();
		}
		finally
		{
			dbStatement.close();
			dbConn.close();
		}
		// TODO Auto-generated method stub
		
	}

}
