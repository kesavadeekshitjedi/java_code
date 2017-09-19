<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Uses the Header and Footer files</title>
</head>
<body>
<jsp:include page="myHeader.html" />
placeholder here

String "TEST" to lower is <%= com.udemy.jspDemo.JSPDemoUtils.makeItLower("TEST")%>

<jsp:include page="myFooter.jsp" />
</body>
</html>