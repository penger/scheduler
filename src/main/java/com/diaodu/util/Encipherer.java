package com.diaodu.util;

/**
 * Created by GP39 on 2016/12/26.
 */


import com.diaodu.core.CenterConfig;
import com.diaodu.domain.Source;

/**
 * 加密解密,用于加密
 * 邮件密码,服务器密码,数据库密码
 * 2016-12-26 10:13:38
 */
public class Encipherer {

    //混淆字符串
    // 加密字符串

    //input  :xxxxxxxxxx
    //output :bDXc':hO4gdb[bXWbXWb0WaNQa`DT`bZgV`0J
    public static String mixCode(String x){
        StringBuffer sb = new StringBuffer();
        StringBuffer result = new StringBuffer();
        char[] chars = x.toCharArray();
        for (int i=0;i<chars.length;i++) {
            int j = chars[i];
            sb.append(1000-j-i);
//			System.out.println(1000-j-i);
        }
        char[] charArray = sb.toString().toCharArray();
        int mmm=0;
        for(int j=0;j<charArray.length;j++){
            int mm = Integer.parseInt(charArray[j]+"");
            if(j%2==1){
                mmm=mmm*10+mm+10;
                char xx= (char) mmm;
                result.append(xx);
            }else{
                mmm=mm;
            }
        }
        if(charArray.length%2!=0){
            result.append("@__@").append(charArray[charArray.length-1]);
        }
        return result.toString();
    }



    public static String hackCode(String x){
        String tail="";
        if(x.split("@__@").length >1){
            tail=x.split("@__@")[1];
            x=x.split("@__@")[0];
        }
        StringBuffer sb = new StringBuffer();
        StringBuffer result = new StringBuffer();
        char[] chars = x.toCharArray();
        for(int i=0;i<chars.length;i++){
            int mm= chars[i];
            if(mm<20){
                sb.append("0");
            }
            sb.append(mm-10);
        }
        sb.append(tail);
        char[] charArray = sb.toString().toCharArray();
        int mmm=0;
        for(int j=0;j<charArray.length;j++){
            int mm = Integer.parseInt(charArray[j] + "");
            mmm=mmm*10+mm;
            if(j%3==2){
                int t=1000-mmm-j/3;
                char xx= (char) t;
                result.append(xx);
                mmm=0;
            }
        }
        return result.toString();
    }


    public static void main(String[] args) {
        System.out.println(Encipherer.hackCode("10.201.48.49"));
        System.out.println(Encipherer.hackCode("hive"));
        System.out.println("10.201.48.26" + CenterConfig.POSTFIX_ACTOR);
        String actor = CenterConfig.getValueByKey("10.201.48.26" + CenterConfig.POSTFIX_ACTOR);
        System.out.println(actor);
        System.out.println(CenterConfig.getValueByKey("10.201.48.26_actor"));

        System.out.println(CenterConfig.getValueByKey("10.201.48.26_user_name"));
        System.out.println(CenterConfig.getValueByKey("local_ip"));
        System.out.println(CenterConfig.getValueByKey("local_user_name"));
        System.out.println(CenterConfig.getValueByKey("local_pass_word"));
    }
}
