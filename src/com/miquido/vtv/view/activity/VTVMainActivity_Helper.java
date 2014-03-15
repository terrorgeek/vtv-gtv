package com.miquido.vtv.view.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class VTVMainActivity_Helper {
	public static String Get_Current_Hour()
    {
    	Date current_date=new Date();
    	return current_date.getHours()+"";
    }
	
    public static ArrayList<String> Get_Time_Line_List()
    {
    	String current_date=Get_Current_Hour();
    	ArrayList<String> resultSet=new ArrayList<String>();
    	Calendar cal = Calendar.getInstance();
    	if(Integer.parseInt(current_date)<12)
			resultSet.add(current_date+":00 AM");
		else
			resultSet.add(current_date+":00 PM");
    	for(int i=0;i<24;i++)
    	{
    		cal.add(Calendar.HOUR_OF_DAY, +1);
    		String result = new SimpleDateFormat("HH").format(cal.getTime());
    		if(Integer.parseInt(result)<12)
    			resultSet.add(result+":00 AM");
    		else
    			resultSet.add(result+":00 PM");
    	}
    	return resultSet;
    }
}
