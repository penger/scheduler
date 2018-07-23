package com.cqw.lucence;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class MyQuery {
    
    /**
     * 执行查询，返回前returnNum个结果
     * @param query_str ：待查询的字符串
     * @param returnNum ：指定返回前returnNum个结果
     * @throws ParseException
     * @throws IOException
     */
    private static void query(String query_str, int returnNum) throws ParseException, IOException {
        // 指定与CreateIndex一样的analyzer
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
        
        // 指定index存储位置
        File indexDir = new File("indexDir");
        Directory dir = FSDirectory.open(indexDir);
        
        // 创建检索器
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        
        // 解析输入，并指定检索content的内容
        QueryParser queryParser = new QueryParser(Version.LUCENE_43, "content", analyzer);
        Query query = queryParser.parse(query_str);
        
        // 执行查询
        TopScoreDocCollector collector = TopScoreDocCollector.create(returnNum, true);
        searcher.search(query, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        show(searcher, hits);
    }
    
    /**
     * 打印查询结果
     * @param searcher ：IndexSearcher对象
     * @param hits ：查询结果
     * @throws IOException
     */
    private static void show(IndexSearcher searcher, ScoreDoc[] hits) throws IOException {
        for (int i = 0; i < hits.length; i++) {
            int docId = hits[i].doc;
            Document doc = searcher.doc(docId); //提取查询到的文档
//            System.out.println(doc.get("id") + ". " + doc.get("title") + "\t" + doc.get("content"));
        }
    }
    
    public static void main(String[] args) throws ParseException, IOException {
        int returnNum = 10; //指定返回前10个文档
        String query_str = args.length > 0 ? args[0] : "have";
        int count=1000;
        long currentTimeMillis = System.currentTimeMillis();
        for(int i=0;i<count;i++) {
        	try {
				Thread.sleep(3);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	query(query_str, returnNum);        	
        }
        long time_used= System.currentTimeMillis()-currentTimeMillis;
        System.out.println("检索命中的Top："+returnNum);
        System.out.println("共索引文档(次数)："+count);
        System.out.println("共使用时长为(ms)："+time_used);
        System.out.println("平均使用时长(ms)："+(time_used)/count);
    }
}