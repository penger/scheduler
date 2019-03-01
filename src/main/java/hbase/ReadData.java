package hbase;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.DecoderException;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.tephra.TxConstants.HBase;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class ReadData {

	public static void main(String[] args) throws IOException {
		
		test();
		
//		File file = new File("d://x.txt");
//		String line = Files.readFirstLine(file, Charsets.US_ASCII);
//		System.out.println(line);
		
//		new String
		
		
//		String me= "我爱习近平";
//		byte[] bytes = me.getBytes();
//		for (byte b : bytes) {
//			System.out.println(b);
//		}
//		String you = new String(bytes,"utf8");
//		System.out.println(you);
		
	}
	
	
	
	private static void test() {
		
		try {
			System.out.println("e4b8ade69687".toCharArray());
			System.out.println(new String(org.apache.commons.codec.binary.Hex.decodeHex("07d40002".toCharArray()),"utf8"));
		} catch (UnsupportedEncodingException | DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		HTable
//		String str ="我是谁";
//		byte[] bytes = Bytes.toBytes(str);
//		for (byte b : bytes) {
//			System.out.println(b);
//		}
//		String new_str = new String(bytes);
//		System.out.println(new_str);
	}

}