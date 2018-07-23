package storm;

import java.util.Random;

import org.apache.hadoop.fs.FileSystem;

public class C {

	public static void main(String[] args) {
//		FileSystem
		Random randomGenerator  = new Random();
		for(int i = 0 ;i< 1000 ;i++) {
			System.out.println(randomGenerator.nextInt(3));
		}
	}

}
