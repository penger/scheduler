import com.diaodu.domain.Source;
import org.apache.hadoop.mapred.SshFenceByTcpPort;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 客户端
 */
public class A {
    public static void main(String[] args) throws IOException, SQLException {

        //二进制
        byte nByte = (byte)0b0001;
        short nShort = (short)0B0010;
        int nInt = 0b0011;
        long nLong = 0b0100L;

        int a=10_0000_10;
        long b = 0xffffff___fffl;
        byte c = 0b1_______1000;



        System.out.println(c);
        //switch 支持string字符串
        switchString("one");
        //map 泛型推断
        Map<String,List<String>> map= new HashMap<>();


        try {
            testthrows();
        } catch (SQLException|IOException e) {
            throw e;
        }
        tryWithResource();
    }

    private static void switchString(String str){
        switch (str){
            case "one":
                System.out.println("ont");
                break;
            case "two":
                System.out.println("tow");
                break;
            default:
                System.out.println("hap");
        }

    }
    public static <T> void addToList(List<T>listx,T...args){
        for (T x: args) {
            listx.add(x);
        }
    }


    public static void testthrows() throws IOException,SQLException{

    }


    public static void tryWithResource() throws IOException {
    }
}

//  try {
//          Thread.sleep(1000000000l);
//          //1.创建客户端Socket，指定服务器地址和端口
//          Socket socket=new Socket("192.168.211.142", Integer.parseInt("2181"));
//          //2.获取输出流，向服务器端发送信息
//          OutputStream os=socket.getOutputStream();//字节输出流
//          PrintWriter pw=new PrintWriter(os);//将输出流包装为打印流
//          pw.write("conf");
//          pw.flush();
//          socket.shutdownOutput();//关闭输出流
//          //3.获取输入流，并读取服务器端的响应信息
//          InputStream is=socket.getInputStream();
//          BufferedReader br=new BufferedReader(new InputStreamReader(is));
//          String info=null;
//          while((info=br.readLine())!=null){
//          System.out.println("我是客户端，服务器说："+info);
//          }
//          //4.关闭资源
//          br.close();
//          is.close();
//          pw.close();
//          os.close();
//          socket.close();
//          } catch (UnknownHostException e) {
//          e.printStackTrace();
//          } catch (IOException e) {
//          e.printStackTrace();
//          } catch (InterruptedException e) {
//          e.printStackTrace();
//          }