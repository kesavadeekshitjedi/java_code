<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Student Response</title>
</head>
<body>


The student is confirmed: ${param.firstName} ${param.lastName}
<br/><br/>

<br />
The students fav programming language are:
<br/><br/>

<br />

<!--  display the list of users favorite languages -->
<!--  This is also used as a comment -->
<ul>
<%
	String[] langs = request.getParameterValues("favLanguage");
if(langs!=null)
{
	for(String langStr: langs)
	{
		out.println("<li>"+langStr+"</li>");
	}
}
%>
</ul>
</body>
</html>