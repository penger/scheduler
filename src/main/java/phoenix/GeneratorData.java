package phoenix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;




/**
 * 测试生成详单数据
 * @author gongp
 * 用户电话号码,省份代码,使用时间开始,使用时间结束,使用流量值
 * 计划生成一千万用户名
 * 计划生成31个省份
 * 计划生成20条
 * 计划生成50天 
 * 
 * 1000 * 31 * 1000 = 32000w
 *
 */
public class GeneratorData {
	
	public static String path = "d://one.txt";

	public static void main(String[] args) throws IOException {
		if(args.length ==1 ) {
			path = args[0];
		}else {
			System.out.println("use default path : " + path);
		}
		//号码段
		String[] head = new String[] {"139","138","177","176","131","159","132"};
		Random random = new Random();
		int length = head.length;
		//省份代码
		Map<String, String> provinces = getProvinces();
		Set<String> keySet = provinces.keySet();
		int size = keySet.size();
		String[] codes = new String[size];
		int index = 0 ;
		for (String key : keySet) {
			codes[index]=key;
			index++;
		}
		int codesLength = codes.length;
		//日期
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		List<String > dates = new ArrayList<>();
		for(int i =0 ;i <100;i++) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(calendar.DATE, i);
			Date time = calendar.getTime();
			String d = dateFormat.format(time);
			dates.add(d);
		}
		//时间段
		List<String> timelist = new ArrayList<>();
		
		for(int i=2;i<22;i++) {
			String num = i<10?"0"+i:""+i;
			timelist.add(num+"1234");
			timelist.add(num+"2046");
			timelist.add(num+"3456");
			timelist.add(num+"5612");
			timelist.add(num+"4444");
		}
		System.out.println(timelist.size());
		
		
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path));
		
		//1000w 电话号码
//		for(int i =2400*10000 ;i< 2500*10000; i++) {
		for(int i =2000*100 ;i< 2001*100; i++) {
			String num = head[random.nextInt(length)]+i;
			for(int j=0;j<31;j++) {
				String code = codes[j];
				for(int k=0;k<50;k++) {
					String date = dates.get(k);
					int last =0 ;
					for(int t=0;t<2;t++) {
						if(last ==0 ) {
							last = random.nextInt(50);
						}else {
							last = 50+last;
						}
						String start = timelist.get(last);
						String line = num + ":"+code + ":"+ date + ":"+ start+","+random.nextInt(1000*60);
						bufferedWriter.write(line);
						bufferedWriter.newLine();
					}
				}
				
			}
			
		}
		
		bufferedWriter.close();
		
	}
	
	
	public static Map<String, String> getProvinces() throws IOException{
		Map<String,String> map = new HashMap();
		File file = new File("E:\\javawork\\scheduler\\src\\main\\resources\\province");
		System.out.println(file.getAbsolutePath());
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = "";
		while((line= reader.readLine())!=null) {
			System.out.println(line);
			if(line.length()>0) {
				String[] split = line.split(":");
				map.put(split[1].trim(), split[0].trim());
			}
		}
		reader.close();
		return map;
	}

}
