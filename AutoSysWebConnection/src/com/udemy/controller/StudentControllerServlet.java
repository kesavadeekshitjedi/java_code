package com.udemy.controller;

import java.io.IOException;
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
			listStudents(request,response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
