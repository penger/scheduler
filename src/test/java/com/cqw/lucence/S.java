package com.cqw.lucence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.impl.conn.ConnectionShutdownException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class S {

	public static void main(String[] args) throws IOException {
		Directory indexDirectory = FSDirectory.open(new File("indexDir"));

        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        
        int numDocs = indexReader.numDocs();
        
        System.out.println(numDocs);
        List<MyTerm> list = new ArrayList<>();
        Fields fields = MultiFields.getFields(indexReader );
        Iterator<String> fieldsIterator = fields.iterator();
        while(fieldsIterator.hasNext()){
            String field = fieldsIterator.next();
            Terms terms = fields.terms(field);
            TermsEnum termsEnum = terms.iterator(null);
            BytesRef byteRef = null;
            System.out.println("field : "+ field);
            while((byteRef = termsEnum.next()) != null) {
                String term = new String(byteRef.bytes, byteRef.offset, byteRef.length);
                Term term2 = new Term(field, term);
                int docFreq = indexReader.docFreq(term2);
                System.out.println("term is : " + term + "freq is : "+docFreq);
                list.add(new MyTerm(term,docFreq));
            }
        }
        Collections.sort(list);
        int count =0;
        for (MyTerm my : list) {
			System.out.println(my);
			count=count+my.count;
		}
        System.out.println("terms list size is "+list.size());
        System.out.println("terms total length is :"+ count);
	}

}
