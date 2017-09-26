package com.udemy.model;

import java.util.ArrayList;
import java.util.List;

import com.udemy.student.Student;

public class StudentDataUtil 
{
	// This is only the model that returns the data. 
	//typically this would be something that makes a call to a DB or AutoSys or something on the backend which returns info
	public static List<Student> getStudents()
	{
		List<Student> studentList = new ArrayList<>();
		studentList.add(new Student("Deekshit","Addepalli","daddepalli@robert.com"));
		studentList.add(new Student("Deekshit2","Addepalli3","daddepalli5@robert.com"));
		studentList.add(new Student("Deekshit3","Addepalli4","daddepalli6@robert.com"));
		
		return studentList;
	}

}
