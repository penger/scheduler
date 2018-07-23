package com.diaodu.dao;

import java.sql.SQLException;
import java.util.List;

import com.diaodu.domain.ETL;

public interface ETLDao {
	public List<ETL> getAllETL() throws SQLException;

	public int addETL(ETL etl) throws SQLException;

	public ETL getETLByID(int etlId) throws SQLException;

	public List<ETL> getETLsByName(String hiveName) throws SQLException;

	ETL getETLByName(String hiveName) throws SQLException;

	public List<ETL> getETLsByCondition(ETL e) throws SQLException;
	
	public int updateETL(ETL etl) throws SQLException;

	public int updateSchedulingByID(int etl_id,int is_scheduling)throws SQLException;

	int updateETL(int etl_id, String owner, String remark) throws SQLException;
	
}
