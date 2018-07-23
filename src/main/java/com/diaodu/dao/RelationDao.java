package com.diaodu.dao;

import java.sql.SQLException;
import java.util.List;

import com.diaodu.domain.Relation;

public interface RelationDao {

	public List<Relation> getETLRelation(int etl_id)  throws SQLException ;
	public List<Relation> getFrontRelation(String front_id)  throws SQLException ;
	public List<Relation> getSourceRelation(int source_id) throws SQLException;
	public int addRelation(Relation relation)  throws SQLException ;
	public int deleteRelation(Relation relation) throws SQLException;
}
