package com.rmt.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTest {

    public static void main(String[] args) throws ParseException {

    	SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/mm/yyyy");
        String date1 = "30/11/2016";
        String date2 = "08/15/2016";
        
        Date newDate1 = sdf.parse(date1);
        Date newDate2 = sdf2.parse(date2);
        System.out.println(sdf.format(newDate1));
        System.out.println(sdf2.format(newDate2));

    }

}