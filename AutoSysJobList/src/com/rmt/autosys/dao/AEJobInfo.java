package com.rmt.autosys.dao;

public class AEJobInfo 
{
	private String jobName;
	private int jobType;
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public int getJobType() {
		return jobType;
	}
	public void setJobType(int jobType) {
		this.jobType = jobType;
	}
	public AEJobInfo(String jobName, int jobType) {
		
		this.jobName = jobName;
		this.jobType = jobType;
	}
	@Override
	public String toString() {
		return "AEJobInfo [jobName=" + jobName + ", jobType=" + jobType + "]";
	}
	
	

}
