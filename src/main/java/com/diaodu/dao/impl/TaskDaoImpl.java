package com.diaodu.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.diaodu.dao.TaskDao;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.History;
import com.diaodu.domain.Task;

public class TaskDaoImpl implements TaskDao {

	@Override
	public List<Task> getAllTask() throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		List<Task> list = queryRunner.query(connection,"select * from task order by id asc", new BeanListHandler<>(Task.class));
		return list;
	}

	@Override
	public List<Task> getTaskListByBatchID(int batchid) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		List<Task> list = queryRunner.query(connection,"select * from task where batchid = ? order by seq", new BeanListHandler<>(Task.class),batchid);
		return list;
	}

	@Override
	public Task getTaskByID(int id) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		Task task = queryRunner.query(connection,"select * from task where id = ?", new BeanHandler<>(Task.class),id);
		return task;
	}

	@Override
	public int deleteTaskByID(int id) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		return queryRunner.update(connection,"delete from task where id = ?",id);
	}

	@Override
	public int addTask(Task task) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		String sql="insert into task(seq,tname,tdesc,batchid,tasktype,commandpath,command,args,actor)"+
		" values (?,?,?,?,?,?,?,?,?)";
		Object[] params={task.getSeq(),task.getTname(),task.getTdesc(),
				task.getBatchid(),task.getTasktype(),task.getCommandpath(),
				task.getCommand(),task.getArgs(),task.getActor()};
		return queryRunner.update(connection, sql, params);
		
	}
	
	

	@Override
	public int addHistory(History history) throws SQLException {
		//日志长度防御
		String message = history.getMessage();
		if(message.length()>200){
			String new_message=message.substring(0, 100);
			new_message=new_message+".......";
			new_message=new_message+message.substring(message.length()-100,message.length());
			//减去中间的输出
			history.setMessage(new_message);
		}
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		String sql="insert into history(start_date,task_date,cmd,exit_code,spend_time,message,script)"+
		" values (?,?,?,?,?,?,?)";
		Object[] params={history.getStart_date(),history.getTask_date(),history.getCmd(),
				history.getExit_code(),history.getSpend_time(),history.getMessage(),history.getScript()};
		return queryRunner.update(connection, sql, params);
		
	}
	

	@Override
	public int updateTask(Task t) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner runner = new QueryRunner();
		String sql="update task set seq=? ,tname = ?,tdesc= ?,batchid= ?,tasktype= ?,commandpath= ?"+
		" ,command= ?,args= ? ,flag = ?, avaragetime = ?, usedtime = ? where id= ?";
		Object[] params={t.getSeq(),t.getTname(),t.getTdesc(),t.getBatchid(),t.getTasktype(),
				t.getCommandpath(),	t.getCommand(),t.getArgs(),t.getFlag(),t.getAvaragetime(),
				t.getUsedtime(),
				t.getId()};
		return runner.update(connection, sql, params);
		
	}

	@Override
	public Task getTaskByScript(String etl_script) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		Task task = queryRunner.query(connection,"select * from task where command = ?", new BeanHandler<>(Task.class),etl_script);
		return task;
	}

	/**
	 * 只修改task状态为已经运行成功的为待运行状态
	 * @param flag
	 * @param batchid
	 * @return
	 * @throws SQLException
	 */
	@Override
	public int updateTaskFlag(int flag,int batchid) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		return queryRunner.update(connection,"update task as a join(select id from task where batchid = ? and flag = 1 ) as b on a.id = b.id set flag= ?", batchid , flag);
		//return queryRunner.update(connection,"update task as a set flag= ? where batchid= ?", flag,batchid);
	}

    @Override
    public List<Task> getFailTasksByBatchID(Integer batchid) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		String sql = "select * from task where flag=3 and batchid=? ";
		List<Task> tasks = queryRunner.query(connection, sql, new BeanListHandler<>(Task.class),batchid);
		return tasks;
	}

	@Override
	public int updateNotVainTask2Free(int batchid) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		return queryRunner.update(connection,"update task as a join(select id from task where batchid=? and flag != 0 and flag != 1 ) as b on a.id = b.id set flag=1 ",batchid);

	}

}
