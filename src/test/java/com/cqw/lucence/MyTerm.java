package com.cqw.lucence;

public class MyTerm implements Comparable<MyTerm>{
	public String str = "";
	public int count =0;
	
	
	
	public MyTerm(String str, int count) {
		super();
		this.str = str;
		this.count = count;
	}



	@Override
	public String toString() {
		return "MyTerm [str=" + str + "    \t count=" + count + "]";
	}



	@Override
	public int compareTo(MyTerm o) {
		return this.count-o.count;
	}
}
