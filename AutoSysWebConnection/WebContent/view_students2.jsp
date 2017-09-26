<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Student Information</title>
</head>
<body>
<h1> The Student Information</h1>
<hr>
<br /><br />
<table border="1">


<tr>
	<th>First Name</th>
	<th>Last Name</th>
	<th>Email</th>
	
</tr>
<c:forEach var="studentname" items="${student_list4}">
<tr>
	<td>${studentname.firstName}</td>
	<td>${studentname.lastName}</td>
	<td>${studentname.emailAddress}</td>
</tr>
</c:forEach>
</table>

</body>
</html>