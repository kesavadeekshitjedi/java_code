<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Expression Tests in JSP</title>
</head>
<body>
Converting a string to upper case: <%= new String("hello world").toUpperCase() %>
is 75 < 69? <%= 75 < 69 %>
</body>
</html>