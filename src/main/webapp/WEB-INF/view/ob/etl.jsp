<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head>
	<link href="css/base.css"rel="stylesheet"type="text/css"/>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">
	
	
	function allETL(hive_table,owner,etl_type,remark,etl_crontab_info) {
		$.ajax({
					url : "./ob?op=viewETL&hive_table="+hive_table+"&owner="+owner+"&etl_type="+etl_type+"&remark="+remark+"&etl_crontab_info="+etl_crontab_info,
					data : '',
					dataType : "json",
					type : "GET",
					async : true, // 同步
					success : function(response) {
						var list = response.list;
						debugger;
						var one=response.one_day_before;
						var head = "<tr><th>序号</th><th>任务类型</th><th>脚本地址</th><th>调度信息</th><th>hive表名</th><th>创建人</th><th>备注</th><th>更新时间</th><th>操作</th></tr>"
						var body = ""
						for (var i = 0; i < list.length; i++) {
							var tditem = ""
							var item = list[i];
							log=item.etl_script.replace('.sh','.log')
							tditem += "<td bgcolor="+item.etl_color+">" + (i+1) + "</td>"
							tditem += "<td>" + item.etl_type_string + "</td>"
							tditem += "<td>" + item.etl_script + "</td>"
							tditem += "<td bgcolor="+item.crontab_info_color+">" + item.etl_crontab_info + "</td>"
							tditem += "<td>" + item.hive_table + "</td>"
							tditem += "<td><input type='text' id="+item.etl_id+"owner value='" + item.owner + "' /></td>"
							tditem += "<td><input type='text' id="+item.etl_id+"remark value='" + item.remark + "' /></td>"
							tditem += "<td>" + item.create_time + "</td>"
							tditem += "<td bgcolor="+item.scheduling_color+"><a href=./ob?op=toUpdateETL&etl_id="+item.etl_id+"&hive_table="+item.hive_table+" target='_blank'>依赖管理</a>"
								   +"&nbsp;<a href=javascript:editTask('"+item.etl_id+"')>调度管理</a>"
								   +"&nbsp;<a href=http://10.201.48.26/etl/"+one+"/"+log+" >日志</a>"
								   +"&nbsp;<a href=javascript:updateETL('"+item.etl_id+"')>更新</a>"
								   +"</td>"
							tditem = "<tr>" + tditem + "</tr>"
							body = body + tditem
						}
						table = "<table border='1' id='ETLTable' >" + head
								+ body + "</table>"
						$("#content").empty()
						$("#content").append(table)
					}
				})
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

	function updateETL(etl_id){
		
		var front={
				etl_id:etl_id,
				owner:$("#"+etl_id+"owner").val(),
				remark:$("#"+etl_id+"remark").val()
		}
		var params =$.param(front)
		var urlPath = "ob?op=updateETL&"+params;
		alert(urlPath)
		$.ajax({
			url : urlPath,
			data: '',
			dataType : "html",
			type : "GET",
			async : true, // 同步
			success : function(data) {
					debugger
					if(data==1){
						alert("修改了 "+data+" 条记录");
					}else{
						alert("修改失败!")
					}
					
				}
		})
	}
	
	
	function addETL(){
		$("#message").empty();
		var r=getRandomNum();
		var trContent="<tr>"+
		"<td></td>"+
		"<td><input id=etl_type_"+r+" value='1'/ disabled='true '></td>"+
		"<td><input id=etl_script_"+r+" value=''/></td>"+
		"<td><input id=etl_crontab_info_"+r+" value=''/></td>"+
		"<td><input id=hive_table_"+r+" value=''/></td>"+
		"<td><input id=owner_"+r+" value=''/></td>"+
		"<td><input id=remark_"+r+" value='功能描述<修改>'/></td>"+
		"<td></td>"+
		"<td><a href='javascript:createETL("+r+")'>创建流程</a></td>"+
		"</tr>"
		$("#ETLTable").append(trContent)
	}
	

	function create(urlPath){
		$.ajax({
			url : urlPath,
			data: '',
			dataType : "html",
			type : "GET",
			async : true, // 同步
			success : function(data) {
					debugger
					if(data==1){
						alert(" 增加 "+data+" 条记录")
					}else{
						alert("新增失败!")
					}
					
				}
		})
	}
	
	
	
	function createETL(id){
		debugger
		var front={
				etl_script:$("#etl_script_"+id).val(),
				etl_type:$("#etl_type_"+id).val(),
				etl_crontab_info:$("#etl_crontab_info_"+id).val(),
				hive_table:$("#hive_table_"+id).val(),
				owner:$("#owner_"+id).val(),
				remark:$("#remark_"+id).val()
		}
		var params =$.param(front)
		var urlPath="./ob?op=createETL&"+params
		create(urlPath)
	}

	$(function() {
		//allETL("","","","","");\
		search();
	});
	
	
	//ETL关联task
	function editTask(id){
		debugger
		window.location="./ob?op=toEditTask&etl_id="+id	
	}
	
	function search(){
		var etl_type=$("#etl_type").val()
		var hive_table=$("#hive_table").val()
		var owner=$("#owner").val()
		var remark=$("#remark").val()
		var etl_crontab_info=$("#etl_crontab_info").val()
		allETL(hive_table,owner,etl_type,remark,etl_crontab_info)
	}
	

</script>
</head>
<body>
	<div id="message"></div>
	<a href="./ob?op=source">源表</a>
	<a href="./ob?op=etl">ETL</a>
	<a href="./ob?op=front">前台</a>
	<div id="addETL">
		<button onclick="addETL()">添加ETL</button>
	</div>
	<div>
		<label>任务类型:</label><input id="etl_type" type="text">
		<label>hive表名:</label><input id="hive_table" type="text">
		<label>创建人:</label><input id="owner" type="text">
		<label>备注:</label><input id="remark" type="text">
		<label>调度类型:</label><input id="etl_crontab_info" type="text">
		<button onclick="search()">搜索</button>
	</div>
	<div id="content">
	</div>
	
</body>
</html>
