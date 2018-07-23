package com.diaodu.dao;

import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.List;

import com.diaodu.domain.History;

public interface HistoryDao {

	List<History> getHistoryPage(int start,int end)  throws SQLException ;
	int getTotalRecodes() throws SQLException;

	List<History> getHistoryPageByConditon(int start,int end,String where_order)  throws SQLException ;
	int getTotalRecodesByConditon(String where) throws SQLException;

	int getAvarageTimeByScript(String script) throws SQLException;

	int deleteHistoryNdaysBefore(int n) throws SQLException;


}
