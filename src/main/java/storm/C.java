package storm;

import java.util.Random;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.metrics2.util.MBeans;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ClusterMetricsInfo;

public class C {

	public static void main(String[] args) {
//		FileSystem
		Random randomGenerator  = new Random();
		for(int i = 0 ;i< 1000 ;i++) {
			System.out.println(randomGenerator.nextInt(3));
			
			new ClusterMetricsInfo();
//			new DataXceiver();
//		}
	}

}
}
