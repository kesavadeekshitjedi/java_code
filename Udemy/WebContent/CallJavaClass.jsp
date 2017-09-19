<%@page import="com.udemy.jspDemo.JSPDemoUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Calls the JSPDemoUtils class file</title>
</head>
<body>
<h2> This JSP calls the JSPDemoUtils class to get the upper or lower case version of the string.</h2>

This converts "TEST" to lowercase: <%= JSPDemoUtils.makeItLower("TEST")%>
This converts "test" to uppercase: <%= JSPDemoUtils.makeItUpper("test")%>
</body>
</html>