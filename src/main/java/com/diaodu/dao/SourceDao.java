package com.diaodu.dao;

import java.sql.SQLException;
import java.util.List;

import com.diaodu.domain.Source;

public interface SourceDao {
	public List<Source> getAllSources() throws SQLException;

	public int addSource(Source s) throws SQLException;

	public Source getSourceByID(int sourceId) throws SQLException;

	public List<Source> getSourceByName(String sourceName) throws SQLException;

	public Source getOnlySourceByName(String sourceName) throws SQLException;

	public List<Source> getSourcesInUse() throws SQLException;

	public List<Source> getSourcesNotInUse() throws SQLException;
}
