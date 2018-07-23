package com.diaodu.dao;

import java.sql.SQLException;
import java.util.List;

import com.diaodu.domain.Batch;
import com.diaodu.domain.History;
import com.diaodu.domain.Task;

public interface TaskDao {

	List<Task> getAllTask() throws SQLException;
	
	List<Task> getTaskListByBatchID(int batchid) throws SQLException;
	Task getTaskByID(int id) throws SQLException;
	int deleteTaskByID(int id) throws SQLException;
	int addTask(Task task) throws SQLException;
	int updateTask(Task t) throws SQLException;

	int addHistory(History history) throws SQLException;

	Task getTaskByScript(String etl_script) throws SQLException;

    int updateTaskFlag(int flag,int batchid) throws SQLException;

    List getFailTasksByBatchID(Integer batchid) throws SQLException;

	//更新未执行成功的task为正常执行
	int updateNotVainTask2Free(int batchid) throws SQLException;


}
