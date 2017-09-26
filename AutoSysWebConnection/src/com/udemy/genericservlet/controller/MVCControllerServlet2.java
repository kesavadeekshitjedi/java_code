package com.udemy.genericservlet.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.udemy.model.StudentDataUtil;
import com.udemy.student.Student;

/**
 * Servlet implementation class MVCDemoServlet2
 */
@WebServlet("/MVCControllerServlet2")
public class MVCControllerServlet2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MVCControllerServlet2() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		/*
		 * Step 1. Get the information from the model 
		 * Step 2. Add Student info to the request object
		 * Step 3. Create the dispatcher
		 * Step 4. Forward the dispatcher to the JSP
		 */
		
		List<Student> myStudents = StudentDataUtil.getStudents();
		request.setAttribute("student_list4", myStudents);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/view_students2.jsp");
		dispatcher.forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
