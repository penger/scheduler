package phoenix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


import com.ibm.icu.text.SimpleDateFormat;


public class NNN {

	public static void main(String[] args) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader("G://k.txt"));
		String line = null;
		List <String> list = new ArrayList();
		while((line = bufferedReader.readLine())!=null) {
			String new_line = line.split(",")[1];
//			System.out.println(new_line);
			String dd = new_line.split("\\.")[0] ;
//			testLong(dd);
			list.add(dd);
					
		}
		Collections.sort(list);
		for (String str : list) {
			testLong(str);
		}
	}

	
	
	public static void testLong(String str) {
		long l = Long.parseLong(str);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(l);
		Date date = calendar.getTime();
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
	}
}
