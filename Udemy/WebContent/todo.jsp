<%@ page import="java.util.*" language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
Item entered: <%= request.getParameter("itemName") %>
<%
	// Get the todo items from the session
	// if item doesnt exist in the list, then add to list
	
	// Getting item from the session
	List<String> itemList = (List<String>)session.getAttribute("myTodoList");
	if(itemList==null)
	{
		itemList=new ArrayList<String>();
		session.setAttribute("myTodoList", itemList);
	}
	String formData = request.getParameter("itemName");
	if(!itemList.contains(formData) && formData!=null)
	{
		itemList.add(formData);
	}
	
	
%>
<br /><br />
<b> To Do List Items: </b>
<ol>
<%
	for(String listItem:itemList)
	{
		out.println("<li>"+listItem+"</li>");
	}
%>

</ol>
</body>
</html>