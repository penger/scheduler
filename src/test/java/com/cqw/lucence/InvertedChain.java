package com.cqw.lucence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class InvertedChain {
	
	private List<MyTerm> terms;
	private int length;
	private Map<String, List<MyTerm>> map;
	
	public void change2Map() {
		List<MyTerm> tempList = new ArrayList<>();
		for(int i=0;i<terms.size();i++) {
			tempList.add(terms.get(i));
			if(terms.size()%length==0) {
				map.put(terms.size()/length+"", tempList);
				tempList.clear();
			}
		}
	}
	

	public List<MyTerm> search(String word) {
		List<MyTerm>  list  = new ArrayList<>();
		if (map.isEmpty()) {
			for (MyTerm myTerm : terms) {
				if (myTerm.str.contains(word)) {
					list.add(myTerm);
				}
			}
		}else {
			Set<String> keySet = map.keySet();
			for (String key : keySet) {
				List<MyTerm> keylist = map.get(key);
				new Thread(new Runnable() {
					@Override
					public void run() {
						for (MyTerm myTerm : keylist) {
							if(myTerm.str.contains(word)) {
								list.add(myTerm);
							}
						}
						
					}
				}).start();
			}
		}
		return list;
	}

}
