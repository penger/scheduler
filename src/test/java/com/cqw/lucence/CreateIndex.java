package com.cqw.lucence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class CreateIndex {
    
    /**
     * 创建索引
     * @throws IOException
     */
    private static void createIndex() throws IOException {
        // 指定analyzer，在查询时必须使用同样的analyzer
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
        
        // 指定index存储位置
        File indexDir = new File("indexDir");
        if(indexDir.exists()) {
        	boolean delete = indexDir.delete();
        	System.err.println(delete);
        }
        
        
        Directory dir = FSDirectory.open(indexDir);
        
        // 创建index
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);
        IndexWriter w = new IndexWriter(dir, config);


        File file = new File("f:\\shakespeare_6.0.json");
    	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
    	String line = null;
    	int i =0 ;
    	while((line = bufferedReader.readLine())!=null) {
    		if(line.contains("_index")) {
    			continue;
    		}
    		i=i++;
    		String content =line.split(",")[6];
    		String[] split = content.split("\"");
    		addDoc(w, "doc"+i, split[3]);
    	}
        
        
        w.close(); //必须close()，非常重要，不然数据不会写入到index中
    }
    
    /**
     * 创建Doucment对象，并添加到索引中
     * @param w ： IndexWriter对象
     * @param id：document的ID
     * @param title ：document的title
     * @param content ： document的content
     * @throws IOException
     * {"type":"line",
     * "line_id":536,
     * "play_name":"Henry IV",
     * "speech_number":26,
     * "line_number":"1.3.206",
     * "speaker":"HOTSPUR",
     * "text_entry":"By heaven, methinks it were an easy leap,"}
     */
    
    
    private static void beforeAdd() throws IOException {
    	
    }
    
    
    private static void addDoc(IndexWriter w, String id, String content) throws IOException {
        // 创建document对象
        Document doc = new Document();
        // 文档中的field, StringField中的内容不能用于检索，TextField中的内容才能用于检索
        doc.add(new StringField("id", id, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.YES));
        w.addDocument(doc);
    }

    public static void main(String[] args) throws IOException {
        createIndex();
        System.out.println("Create Index Success!!!");
    }
}