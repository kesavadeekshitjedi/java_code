package com.rmt.autosys.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ca.autosys.services.AsApi;
import com.ca.autosys.services.AsConstants;
import com.rmt.autosys.dao.AEJobInfo;
import com.rmt.autosys.model.RetrieveJobsModel;

/**
 * Servlet implementation class RetrieveJobController
 */
@WebServlet("/RetrieveJobController")
public class RetrieveJobController extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private RetrieveJobsModel jobModel;
	private AsApi appServer;
	
       
    @Override
	public void init() throws ServletException 
    {
		// TODO Auto-generated method stub
		//super.init();
		appServer = new AsApi("rhel-ae-dev",9000,AsConstants.ENCRYPTION_TYPE_DEFAULT);
		appServer.authenticateWithPassword("daddepalli", "rhel-ae-dev.rmt.com", "Deek5581");
		System.out.println("Connection created!");
		jobModel=new RetrieveJobsModel();
	}
    
   

	

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	
		
		// List the jobs from AutoSys - connection made in the init method
		
		listAEJobs(request,response);
		
	}



	private void listAEJobs(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// Get List of jobs from AE - connection made in init()
		List<AEJobInfo> aeJobNames = jobModel.getJobNames();
		// add to request
		request.setAttribute("AE_JOB_LIST", aeJobNames); // adding the list retrieved in 64 to a variable called AE_JOB_LIST
		// send to JSP page (not created yet)
		RequestDispatcher dispatcher = request.getRequestDispatcher("/listAutoSysJobs.jsp");
		dispatcher.forward(request, response);
	}

	

}
