package hbase;
 
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import clojure.instant__init;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
 
/**
 * Created by Administrator on 2016-11-23.
 */
public class HBaseUtils {
    static Configuration cfg;
    static Connection connection;
 
    static {
        cfg = HBaseConfiguration.create();
        try {
            connection = ConnectionFactory.createConnection(cfg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(cfg.get("hbase.master"));
    }
 
    //新建表
    public static boolean create(String tableName, String columnFamily)
            throws Exception {
        HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();
 
        if (admin.tableExists(tableName)) {
            System.out.println(tableName + " exists!");
            return false;
        } else {
            String[] columnFamilyArray = columnFamily.split(",");
            HColumnDescriptor[] hColumnDescriptor = new HColumnDescriptor[columnFamilyArray.length];
            for (int i = 0; i < hColumnDescriptor.length; i++) {
                hColumnDescriptor[i] = new HColumnDescriptor(columnFamilyArray[i]);
            }
            HTableDescriptor familyDesc = new HTableDescriptor(TableName.valueOf(tableName));
            for (HColumnDescriptor columnDescriptor : hColumnDescriptor) {
                familyDesc.addFamily(columnDescriptor);
            }
//            HTableDescriptor tableDesc = new HTableDescriptor(familyDesc);
 
            admin.createTable(familyDesc);
            System.out.println(tableName + " create successfully!");
            return true;
        }
    }
 
    //插入数据
    public static boolean put(String tablename, String row, String columnFamily,
                              String qualifier, String data) throws Exception {
        Table table = connection.getTable(TableName.valueOf(tablename));
        Put put = new Put(Bytes.toBytes(row));
        put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(qualifier),
                Bytes.toBytes(data));
        table.put(put);
        System.out.println("put '" + row + "', '" + columnFamily + ":" + qualifier
                + "', '" + data + "'");
        return true;
    }
 
    //把result转换成map，方便返回json数据
    public static Map<String, Object> resultToMap(Result result) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        List<Cell> listCell = result.listCells();
        Map<String, Object> tempMap = new HashMap<String, Object>();
        String rowname = "";
        List<String> familynamelist = new ArrayList<String>();
        for (Cell cell : listCell) {
            byte[] rowArray = cell.getRowArray();
            byte[] familyArray = cell.getFamilyArray();
            byte[] qualifierArray = cell.getQualifierArray();
            byte[] valueArray = cell.getValueArray();
            int rowoffset = cell.getRowOffset();
            int familyoffset = cell.getFamilyOffset();
            int qualifieroffset = cell.getQualifierOffset();
            int valueoffset = cell.getValueOffset();
            int rowlength = cell.getRowLength();
            int familylength = cell.getFamilyLength();
            int qualifierlength = cell.getQualifierLength();
            int valuelength = cell.getValueLength();
 
            byte[] temprowarray = new byte[rowlength];
            System.arraycopy(rowArray, rowoffset, temprowarray, 0, rowlength);
            String temprow= Bytes.toString(temprowarray);
//            System.out.println(Bytes.toString(temprowarray));
 
            byte[] tempqulifierarray = new byte[qualifierlength];
            System.arraycopy(qualifierArray, qualifieroffset, tempqulifierarray, 0, qualifierlength);
            String tempqulifier= Bytes.toString(tempqulifierarray);
//            System.out.println(Bytes.toString(tempqulifierarray));
 
            byte[] tempfamilyarray = new byte[familylength];
            System.arraycopy(familyArray, familyoffset, tempfamilyarray, 0, familylength);
            String tempfamily= Bytes.toString(tempfamilyarray);
//            System.out.println(Bytes.toString(tempfamilyarray));
 
            byte[] tempvaluearray = new byte[valuelength];
            System.arraycopy(valueArray, valueoffset, tempvaluearray, 0, valuelength);
            String tempvalue= Bytes.toString(tempvaluearray);
//            System.out.println(Bytes.toString(tempvaluearray));
 
 
            tempMap.put(tempfamily + ":" + tempqulifier, tempvalue);
//            long t= cell.getTimestamp();
//            tempMap.put("timestamp",t);
            rowname = temprow;
            String familyname = tempfamily;
            if (familynamelist.indexOf(familyname) < 0) {
                familynamelist.add(familyname);
            }
        }
        resMap.put("rowname", rowname);
        for (String familyname : familynamelist) {
            HashMap<String,Object> tempFilterMap = new HashMap<String,Object>();
            for (String key : tempMap.keySet()) {
                String[] keyArray = key.split(":");
                if(keyArray[0].equals(familyname)){
                    tempFilterMap.put(keyArray[1],tempMap.get(key));
                }
            }
            resMap.put(familyname, tempFilterMap);
        }
 
        return resMap;
    }
 
    //查看某行
    public static String get(String tablename, String row) throws Exception {
        Table table = connection.getTable(TableName.valueOf(tablename));
        Get get = new Get(Bytes.toBytes(row));
        Result result = table.get(get);
        System.out.println("Get: " + result);
        return resultToMap(result).toString();
    }
 
    //查看全表
    public static String scan(String tablename) throws Exception {
        Table table = connection.getTable(TableName.valueOf(tablename));
        Scan s = new Scan();
        ResultScanner rs = table.getScanner(s);
 
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
        for (Result r : rs) {
            Map<String, Object> tempmap = resultToMap(r);
            resList.add(tempmap);
        }
        return resList.toString();
    }
 
    //删除表
    public static boolean delete(String tableName) throws IOException {
        HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();
        if (admin.tableExists(tableName)) {
            try {
                admin.disableTable(tableName);
                admin.deleteTable(tableName);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
 
    //删除ColumnFamily
    public static boolean deleteColumnFamily(String tableName,String columnFamilyName) throws IOException {
        HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();
        if (admin.tableExists(tableName)) {
            try {
                admin.deleteColumn(tableName,columnFamilyName);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    //删除row
    public static boolean deleteRow(String tableName,String rowName) throws IOException {
        HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();
        Table table = connection.getTable(TableName.valueOf(tableName));
        if (admin.tableExists(tableName)) {
            try {
                Delete delete = new Delete(rowName.getBytes());
                table.delete(delete);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
 
    //删除qualifier
    public static boolean deleteQualifier(String tableName,String rowName,String columnFamilyName,String qualifierName) throws IOException {
        HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();
        Table table = connection.getTable(TableName.valueOf(tableName));
        if (admin.tableExists(tableName)) {
            try {
                Delete delete = new Delete(rowName.getBytes());
                delete.addColumns(columnFamilyName.getBytes(),qualifierName.getBytes());
                table.delete(delete);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    
    
    public static void main(String[] args) throws Exception {
		HBaseUtils baseUtils = new HBaseUtils();
		int i=0;
		//写入表
//		while(true) {
//			i=i++;
//			Thread.sleep(1000*2);
//			baseUtils.put("test", UUID.randomUUID().toString(), "cf", "name", "gongpeng"+i);
//			System.out.println(" put at :  "+ new Date());
//		}
		//创建表
		for(int j=0;j<1000;j++) {
			Thread.sleep(1000*30);
			baseUtils.create("test_table_"+j, "cf");
		}
		
	}
}
