package com.udemy.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.udemy.model.StudentDBModel;
import com.udemy.student.Student;

/**
 * Servlet implementation class StudentControllerServlet
 */
@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
   
	private StudentDBModel studentDBModel;
	@Resource(name="jdbc/StudentInfo")
	private DataSource dataSource;
   
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		try
		{
			studentDBModel=new StudentDBModel(dataSource);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{
			// Currently just listing students. not reading the command.
			
			// to accomodate for additional CRUD operations from the jsp to the database, we need to read the command
			// based on the command, route to the proper method.
			
			// read command usinig request.getParameter. This corresponds back to the hidden field in the jsp which has the command
			
			String myCommand=request.getParameter("command");
			// if there's no command, just list the students
			
			if(myCommand==null)
			{
				myCommand="LIST";
			}
			
			switch(myCommand)
			{
			case "LIST":
				listStudents(request,response);
				break;
			case "ADD":
				addStudents(request,response);
				break;
			default: 
				listStudents(request, response);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void addStudents(HttpServletRequest request, HttpServletResponse response) throws SQLException 
	{
		// get the firstname from the form
		// get the last name from the form
		String firstName=request.getParameter("firstName");
		String lastName=request.getParameter("lastName");
		String emailAddr = request.getParameter("email");
		
		// create a new StudentModel object
		Student studentInfo = new Student(firstName,lastName,emailAddr);
		
		// Add to the database table
		studentDBModel.addStudent(studentInfo);
		// send back to listStudents.jsp to view newly added student. (optional?)
		try {
			listStudents(request,response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
	}

	private void listStudents(HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		// get student info from DB
		// add the students to the reqeust object (request.addAttribute)
		// send to JSP for view
		
		List<Student> studentList= studentDBModel.getStudents();
		request.setAttribute("myStudent_List", studentList);
		
		RequestDispatcher dispatcher=request.getRequestDispatcher("/listStudents.jsp");
		dispatcher.forward(request, response);
		
	}

	

}
