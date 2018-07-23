<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page isELIgnored="false"%>
<html>
<head>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">
	function appendtr(type) {
		var id=${id}
		var commandtype=""
		var commandpath="/home/bl/ETL/"
		var command="a.sh" 
		if("shell"==type){
			commandtype="shell"
		}else if("cleanoracle"==type){
			commandtype="oracle_table_clean"
			commandpath="/home/hdfs/scheduler/oracle_table_clean.sh"
			arg_demo="";
		}else if("checkhdfs"==type){
			commandtype="hdfs_check"
			commandpath="/home/hdfs/scheduler/hdfs_check.sh"
			arg_demo="idmdata.db test_hive_name dt 20161201";
		}else if("cleanhdfs"==type){
			commandtype="hdfs_clean"
			commandpath="/home/hdfs/scheduler/hdfs_clean.sh"
			arg_demo="idmdata.db test_hive_name dt 20161201";
		}else if("exechive"==type){
			commandtype="hive_sql_exec"
			commandpath="/home/hdfs/scheduler/hive_sql_exec.sh"
		}else if("hive2oracle"==type){
			commandtype="sqoop_hive2oracle"
			commandpath="/home/hdfs/scheduler/sqoop_hive2oracle.sh"
			arg_demo=""
		}else if("oracle2hive"==type){
			commandtype="sqoop_oracle2hive"
			commandpath="/home/hdfs/scheduler/sqoop_oracle2hive.sh"
		}else{
			alert("不支持的任务类型")
			return
		}
		//伪随机id
		var r=getRandomNum();
		var trContent="<tr>"+
		"<td></td>"+
		"<td><input size=5  id=seq_"+r+" value=''/></td>"+
		"<td><input id=tname_"+r+" value=''/></td>"+
		"<td><input id=tdesc_"+r+" value=''/></td>"+
		"<td><input  id=batchid_"+r+" value='"+id+"'/></td>"+
		"<td><input id=tasktype_"+r+" value='"+commandtype+"'/></td>"+
		"<td><input id=commandpath_"+r+" value='"+commandpath+"'/></td>"+
		"<td><input id=command_"+r+" value='"+command+"'/></td>"+
		"<td><input id=args_"+r+" value=''/></td>"+
		"<td><a href='javascript:createTask("+r+")'>创建流程</a></td>"+
		"</tr>"
		$("#tasktable").append(trContent)
	}
	/**
	*FIXME 可能会产生随机数碰撞
	* 产生参数为 1000~9999
	*/
	function getRandomNum(){
		var r=Math.random()*9000+1000;
		r=Math.floor(r)
		return r;
	}
	
	
	//获取当前元素的值
	function getParams(index){
		var item={
				id:$("#id_"+index).val(),
				seq:$("#seq_"+index).val(),
				tname:$("#tname_"+index).val(),
				tdesc:$("#tdesc_"+index).val(),
				batchid:$("#batchid_"+index).val(),
				tasktype:$("#tasktype_"+index).val(),
				commandpath:$("#commandpath_"+index).val(),
				command:$("#command_"+index).val(),
				args:$("#args_"+index).val()
				
		};
		var params=$.param(item)
		alert(params)
		return params
	}
	//创建task
	function createTask(index){
		var urlPath=getParams(index)
		urlPath="./task?op=c&"+encodeURI(urlPath)
		$.ajax({
			url : urlPath,
			data: '',
			dataType : "html",
			type : "GET",
			async : true, // 同步
			success : function(data) {
					$("#message").append(data)
				}
		})
	}
	
	
	//更新task
	function updateTask(index){
		var urlPath=getParams(index)
		urlPath="./task?op=u&"+encodeURI(urlPath)
		$.ajax({
			url : urlPath,
			data: '',
			dataType : "html",
			type : "GET",
			async : true, // 同步
			success : function(data) {
					$("#message").append(data)
				}
		})
	}
	//删除task
	function deleteTask(index){
		var id=$("#id_"+index).val()
		urlPath="./task?op=d&id="+id
		$.ajax({
			url : urlPath,
			data: '',
			dataType : "html",
			type : "GET",
			async : true, // 同步
			success : function(data) {
					if(data==1){
						$("#message").append(id+"删除成功")
						$("#tr_"+index).remove();
					}else{
						alert("删除失败")
					}
				}
		})
	}
	
</script>
</head>
<body>
	<button onclick="appendtr('shell')">shell</button>
	<button onclick="appendtr('cleanoracle')">cleanoracle</button>
	<button onclick="appendtr('checkhdfs')">checkhdfs</button>
	<button onclick="appendtr('cleanhdfs')">cleanhdfs</button>
	<button onclick="appendtr('exechive')">exechive</button>
	<button onclick="appendtr('hive2oracle')">hive2oracle</button>
	<button onclick="appendtr('oracle2hive')">oracle2hive</button>
	
	<br />
	目前共${length }步
	<br />
	<div id="message" style="color:red"></div>
	<table border="1" id="tasktable">
		<tr>
			<td>id</td>
			<td>seq</td>
			<td>tname</td>
			<td>tdesc</td>
			<td>batchid</td>
			<td>taskType</td>
			<td>scriptpath</td>
			<td>script</td>
			<td>args</td>
			<td>action</td>
		</tr>
		<c:forEach items="${tasks }" var="item" varStatus="status">
			<tr id="tr_${item.id }">
				<td> <input size=5 id="id_${item.id }" value="${item.id }"/></td>
				<td> <input size=5 id="seq_${item.id }" value="${item.seq }"/></td>
				<td> <input id="tname_${item.id }" value="${item.tname }"/></td>
				<td> <input id="tdesc_${item.id }" value="${item.tdesc }"/></td>
				<td> <input id="batchid_${item.id }" value="${item.batchid }"/></td>
				<td> <input id="tasktype_${item.id }" value="${item.tasktype }"/></td>
				<td> <input id="commandpath_${item.id }" value="${item.commandpath }"/></td>
				<td> <input id="command_${item.id }" value="${item.command }"/></td>
				<td> <input id="args_${item.id }" value="${item.args }"/></td>
				<td><a href="javascript:updateTask(${item.id })">update</a>&nbsp;<a href="javascript:deleteTask(${item.id })">delete</a></td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>
