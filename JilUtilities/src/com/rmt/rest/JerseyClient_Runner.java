package com.rmt.rest;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class JerseyClient_Runner {

	public static void main(String[] args) {


		JerseyGetRequests jr = new JerseyGetRequests();
		JerseyPostRequest jp = new JerseyPostRequest();
		try 
		{
			JerseyGetRequests.getJobDetails("LUMOS","job1");
			JerseyGetRequests.getAllJobs("LUMOS");
			JerseyGetRequests.getAllJobRunInfo("LUMOS");
		} 
		catch (KeyManagementException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
