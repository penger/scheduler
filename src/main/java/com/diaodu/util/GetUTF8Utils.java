package com.diaodu.util;

import java.io.UnsupportedEncodingException;

public class GetUTF8Utils {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	public static String getUTF8(String s){
		if( s==null ||s.equals("")){
			return "";
		}
		String ss="";
		try {
			ss =new String(s.getBytes("ISO-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ss;
	}

}
