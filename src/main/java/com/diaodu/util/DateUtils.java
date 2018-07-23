package com.diaodu.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DateUtils {

	public static void main(String[] args) throws ParseException {
		Date dateCalculate = getDateCalculate(-1);
		System.out.println(dateCalculate);
	}
	
	
	public static Date getDateCalculate(int d){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, d);
		return calendar.getTime();
	}
	
	

	public static List<String> getTaskList(String start_date, String end_date) throws ParseException {
		List<String> dates=new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date endDate = sdf.parse(end_date);
		Date startDate = sdf.parse(start_date);
		Calendar calendar = Calendar.getInstance();
		if(endDate.before(startDate)){
			Date tempDate=endDate;
			endDate=startDate;
			startDate=tempDate;
		}
		System.out.println("startDate:"+sdf.format(startDate));
		System.out.println("endDate:"+sdf.format(endDate));
		calendar.setTime(startDate);
		while(calendar.getTime().before(endDate)){
			dates.add(sdf.format(calendar.getTime()));
			calendar.add(Calendar.DATE, 1);
		}
		dates.add(end_date);
		return dates;
	}
	
	

}
