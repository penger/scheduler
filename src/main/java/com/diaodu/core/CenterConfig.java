package com.diaodu.core;

import com.diaodu.domain.Source;
import com.diaodu.util.Encipherer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by GP39
 * 解密工具类通过读取配置中的对应key获取明码
 * update: 2016-12-26 10:35:17
 */
public class CenterConfig {
    public static String ORACLE_JDBC_URL="oracle_connect";
    public static String ORACLE_USER_NAME="oracle_username";
    public static String ORACLE_PASSWORD="oracle_password";

    public static String MYSQL_JDBC_URL="mysql_connect";
    public static String MYSQL_USER_NAME="mysql_username";
    public static String MYSQL_PASSWORD="mysql_password";


    public static String LOCAL_IP="local_ip";
    public static String LOCAL_USERNAME="local_user_name";
    public static String LOCAL_PASSWORD="local_pass_word";
    public static String LOCAL_ACTOR="local_actor";


    //非固定IP的前缀
    public static String POSTFIX_IP="_ip";
    public static String POSTFIX_USERNAME="_user_name";
    public static String POSTFIX_PASSWORD="_pass_word";
    public static String POSTFIX_ACTOR="_actor";



    //如果非加密的字符串前缀
    public static String DECODE_PREFIX="x_";

    public static String PRE_CLIENT_IP="pre_host";
    public static String PRE_ENABLE="pre_enable";


    private static Map<String,String> configMap=null;

    private static void initMap(){
        if(configMap==null || configMap.isEmpty()){
            configMap=new HashMap<>();
            Properties properties = new Properties();
            try {
                properties.load(CenterConfig.class.getClassLoader().getResourceAsStream("config.properties"));
                Set<Map.Entry<Object, Object>> entries = properties.entrySet();
                Iterator<Map.Entry<Object, Object>> iterator = entries.iterator();
                while (iterator.hasNext()){
                    Map.Entry<Object, Object> next = iterator.next();
                    String key = (String)next.getKey();
                    //对没有前缀的进行解密
                    System.out.println(key + "  ==================================");
                    if(key.startsWith(CenterConfig.DECODE_PREFIX)){
                        configMap.put(key,(String)next.getValue());
                        System.out.println(next.getValue());
                    }else{
                        configMap.put((String)next.getKey(),Encipherer.hackCode((String)next.getValue()));
                    }

                }
                System.out.println("reading config from config.properties with "+ configMap.size()+ " records !");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            throw new RuntimeException("check config file: config.properties");
        }
    }

    public static String getValueByKey(String key){
        if(configMap==null){
            initMap();
        }
        return configMap.get(key);
    }

    public static String getValueByKeyWithoutDecode(String key){
        key = CenterConfig.DECODE_PREFIX+key;
        if(configMap==null){
            initMap();
        }
        return configMap.get(key);
    }



    public static void main(String[] args) {
//        testReadFromConfig();
//        System.out.println(CenterConfig.getValueByKey(CenterConfig.MYSQL_PASSWORD));
//        CenterConfig.getValueByKey(CenterConfig.MYSQL_PASSWORD);
        System.out.println("-------------------" +CenterConfig.PRE_CLIENT_IP);
        String x = CenterConfig.getValueByKeyWithoutDecode(CenterConfig.PRE_CLIENT_IP);
        System.out.println("!!!!!!!!!!!!!!!!!!"+x);
    }




    public static void testReadFromConfig(){
        Properties properties = new Properties();
        try {
            properties.load(CenterConfig.class.getClassLoader().getResourceAsStream("config.properties"));
            Set<Map.Entry<Object, Object>> entries = properties.entrySet();
            Iterator<Map.Entry<Object, Object>> iterator = entries.iterator();
            while (iterator.hasNext()){
                Map.Entry<Object, Object> next = iterator.next();
                String key = (String) next.getKey();
                System.out.println(key+" ------- > "+CenterConfig.getValueByKey(key));
            }
//            System.out.println(Encipherer.hackCode((String) properties.get(ORACLE_JDBC_URL)));
//            System.out.println(Encipherer.hackCode((String) properties.get(ORACLE_USER_NAME)));
//            System.out.println(Encipherer.hackCode((String) properties.get(ORACLE_PASSWORD)));
//            System.out.println(Encipherer.hackCode((String) properties.get(MYSQL_JDBC_URL)));
//            System.out.println(Encipherer.hackCode((String) properties.get(MYSQL_USER_NAME)));
//            System.out.println(Encipherer.hackCode((String) properties.get(MYSQL_PASSWORD)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
