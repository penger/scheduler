package com.cqw.lucence;

import java.io.Console;
import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class MyQuery2 {
    
	public static void main(String[] args) throws IOException {  
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);   
        // 指定index存储位置
        File indexDir = new File("indexDir");
        Directory dir = FSDirectory.open(indexDir);
        // 创建检索器
        IndexReader reader = DirectoryReader.open(dir);
//        int docCount = reader.getDocCount("content");
//        System.out.println(docCount);
		
        int numDocs = reader.numDocs();
        System.out.println("文档总数："+numDocs);
        long sumDocFreq = reader.getSumDocFreq("content");
        System.out.println("term 总数： "+sumDocFreq);
        
//		TermEnum termEnum = reader.Terms();
//		while (termEnum.Next())
//		{
//			Console.WriteLine(termEnum.Term());
//			Console.WriteLine("DocFreq="+termEnum.DocFreq());
//			
//			TermDocs termDocs = reader.TermDocs(termEnum.Term());
//			while (termDocs.Next())
//			{
//				Console.WriteLine("DocNo:   "+termDocs.Doc()+"  Freq:   "+termDocs.Freq());
//			}
//		}
	}
	
}