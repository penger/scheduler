<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>
<html>
<head>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">
	function init() {
		//batch list
		debugger;
		var json=jQuery.parseJSON('${json}');
		var batchs=json.batchs;
		var task=json.task;
		var map=json.map;
		var etl=json.etl;
		var etl_info="ETL info:<br/>"+etl.etl_script+"<br/>添加到批次:<br/>"
		var etl_desc=etl.remark
		var head = "<tr><th>批次id</th><th>批次描述</th><th>子序列</th></tr>"
		var body = "";
		for(var i=0;i<batchs.length;i++){
			var item=batchs[i];
			var id=item.id;
			var bdesc=item.bdesc;
			var seqlist=map[id];
			var tdcontent="";
			for(var j=0;j<seqlist.length;j++){
				iitem=id+"__"+seqlist[j];
				if(task!= undefined && task.seq==seqlist[j] && id==task.batchid){
					tdcontent=tdcontent+"<input type='radio' name='seq' value='"+iitem+"' checked='checked'/>"+seqlist[j];
				}else{
					if(j==seqlist.length-1){
						tdcontent=tdcontent+"<input type='radio' name='seq' value='"+iitem+"'/><font color='red'>"+seqlist[j]+"</font>";
					}else{
						tdcontent=tdcontent+"<input type='radio' name='seq' value='"+iitem+"'/>"+seqlist[j];
					}
				}
			}
			body += "<tr><td><a href='./batch?op=toExecutePage&id="+id+"'>"+id+"</a></td><td>"+bdesc+"</td><td>"+tdcontent+"</td></tr>";
		}
		var tname="task任务名称:<input id='tname' type='text' value='"+etl_desc+"'/><br/>";
		var tdesc="task任务描述:<input id='tdesc' type='text' value='执行hive语句导入数据到oracle中'/><br/>";
		var command="task脚本名称:<input id='command' type='text' value='"+etl.etl_script+"' /><br/>";
		var commandpath="task脚本路径:<input id='commandpath' type='text' value='/home/bl/ETL/'/><br/>";
		var tasktype="task任务类型:<input id='tasktype' type='text' value='shell'/><br/>";
		var args="task任务参数:<input id='args' type='text' /><br/>";
		var button="<br/><button onclick='javascript:updateTask()'>添加</button>"
		if(task!=null){
			tname="task任务名称:<input id='tname' type='text' value='"+task.tname+"'/><br/>";
			tdesc="task任务描述:<input id='tdesc' type='text' value='"+task.tdesc+"'/><br/>";
			command="task脚本名称:<input id='command' type='text' value='"+etl.etl_script+"' /><br/>";
			commandpath="task脚本路径:<input id='commandpath' type='text' value='"+task.commandpath+"'/><br/>";
			tasktype="task任务类型:<input id='tasktype' type='text' value='shell'/><br/>";
			args="task任务参数:<input id='args' type='"+task.args+"' /><br/>";
			button="<br/><button onclick='javascript:updateTask("+task.id+")'>更新</button>"
		}
		
		$("#content").append(etl_info);
		$("#content").append(head+body);
		$("#content").append(tname);
		$("#content").append(tdesc);
		$("#content").append(command);
		$("#content").append(commandpath);
		$("#content").append(tasktype);
		$("#content").append(args);
		$("#content").append("<input type='hidden' id='etl_id' value='"+etl.etl_id+"' />");
		$("#content").append(button);
		
		if(task!=null){
			$("#message").append("已加入批次:"+task.batchid);
		}else{
			$("#message").append("<font color='green'>从未添加过</font>");
		}
	}
	

	$(function() {
		
		init()
	})
	
	//如果taskid存在那么本任务已经添加到调度中,如果不存在,那么添加本task
	//update at : 2017-2-9 16:04:45   默认界面添加的任务 执行者都是 hive 用户 by gp
	function updateTask(taskid){
		var whole = $('input[name="seq"]:checked').val();
		var whole = whole.split("__");
		var task={
				id:taskid,
				batchid:whole[0],
				seq:whole[1],
				tname:$("#tname").val(),
				tdesc:$("#tdesc").val(),
				tasktype:$("#tasktype").val(),
				commandpath:$("#commandpath").val(),
				command:$("#command").val(),
				args:$("#args").val(),
				etl_id:$("#etl_id").val(),
				actor:"hive"

		}
		
		var task=$.param(task);
		urlPath="./task?op=c&"+encodeURI(task);
		console.log(urlPath);
//		alert(urlPath);
		$.ajax({
			url:urlPath,
			data:'',
			dataType:"html",
			type:"GET",
			async:true,
			success:function(data){
				$("#message").append(data)
			}
		})	
		/**
		*/

		alert("任务加入调度批次中,但是处于无效状态\n需要在批次页面点击测试跑批成功后加入定时调度")
	}
	
		
</script>
</head>
	<body>
		<div id="message"></div>
		<div id="content">
			
		</div>
	</body>
</html>
