package com.diaodu.dao;

import java.sql.SQLException;
import java.util.List;

import com.diaodu.domain.Batch;

public interface BatchDao {

	public List<Batch> getAllBatch()  throws SQLException ;
	public Batch getBatchByID(String id) throws SQLException;
	public Integer addBatch(Batch b) throws SQLException;
	public int deleteBatch(int id) throws SQLException;
	public int updateBatch(Batch b) throws SQLException;
}
