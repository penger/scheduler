package zk;

import com.sun.research.ws.wadl.HTTPMethods;
import org.apache.commons.httpclient.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import sun.applet.Main;

import java.io.IOException;

/**
 * Created by gongp on 2017/12/7.
 */
public class TestUrl {
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
            //2.生成一个get请求
            for(int i=0;i<1000;i++){
                System.out.println(i);
                HttpGet httpget = new HttpGet("http://node1:8080/");
                //3.执行get请求并返回结果
                CloseableHttpResponse response = httpclient.execute(httpget);
                try {
                    //4.处理结果
                } finally {
                    response.close();
            }
        }
    }
}
