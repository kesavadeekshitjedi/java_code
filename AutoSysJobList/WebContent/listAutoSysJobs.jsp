<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>AutoSys Job List</title>
<%-- <%
	// Get the list of job names from the request object (sent by servlet = RetrieveJobController)
	
	List<AEJobInfo> jobList = (List<AEJobInfo>) request.getAttribute("AE_JOB_LIST");
	
%> --%>

</head>
<body>
<div id="wrapper">
	<div id="header">
	<h2>AutoSys Job List</h2>
</div>
<div id="container">
	<div id="content">
	<table >
		<tr>
			<th>Job Name</th>
			<th>Job Type</th>
			
		</tr>
		<%-- <% for(AEJobInfo tempJobName: jobList)
			{%>
				<tr>
				<td> <%= tempJobName.getJobName() %> </td>
				<td> <%= tempJobName.getJobType() %> </td>
				
				
				</tr>
			<%} 
		%> --%>
		
		<c:forEach var="tempJobName" items="${AE_JOB_LIST}">
		<tr>
				<td> ${tempJobName.jobName} </td>
				<td> ${tempJobName.jobType} </td>
				
				
				</tr>
		</c:forEach>
		
	</table>
</div>


</body>
</html>