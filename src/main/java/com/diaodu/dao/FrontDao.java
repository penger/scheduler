package com.diaodu.dao;

import java.sql.SQLException;
import java.util.List;

import com.diaodu.domain.Front;

public interface FrontDao {
	public List<Front> getAllFront() throws SQLException;
	//public int addFront(Front front) throws SQLException;
	public Front getFrontByID(String front_id) throws SQLException;
	//public List<Front> getFrontsByCondition(Front f)  throws SQLException;

}
