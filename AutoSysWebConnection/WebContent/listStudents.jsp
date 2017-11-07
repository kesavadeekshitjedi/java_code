<%@ page import="java.util.*,com.udemy.model.*,com.udemy.student.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Student Tracker Application</title>
<link type="text/css" rel="stylesheet" href="css/styles.css">
</head>
<%
	// get the list of Students sent by the servlet
	List<Student> studList = (List<Student>)request.getAttribute("myStudent_List");
%>
<body>
<div id="wrapper">
	<div id="header">
	<h2>Some University</h2>
</div>
<div id="container">

	<div id="content">
	<input type="button" value="Add Student" 
		onclick="window.location.href='add-student.jsp';return false;"
		class="add-student-button"
	/>
	<table >
		<tr>
			<th>First Name</th>
			<th>Last Name</th>
			<th>Email Address</th>
			<th> Action </th>
		</tr>
		<% for(Student tempStudent:studList)
			{%>
			<tr>
			<td> <%= tempStudent.getFirstName() %> </td>
			<td> <%= tempStudent.getLastName() %> </td>
			<td> <%= tempStudent.getEmailAddress() %> </td>
			<td><a href="update-students2.jsp">Update</a>
			<%} %>
		
	</table>
</div>

</body>
</html>