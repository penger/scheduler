<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">
	function allsource() {
		$.ajax({
					url : "./ob?op=viewsource",
					data : '',
					dataType : "json",
					type : "GET",
					async : true, // 同步
					success : function(response) {
						var list = response.list;
						showAddSource()
						var head = "<tr><th>序号</th><th>系统</th><th>hive table</th></tr>"
						var body = ""
						for (var i = 0; i < list.length; i++) {
							var tditem = ""
							var item = list[i]
							tditem += "<td>" + (i+1) + "</td>"
							tditem += "<td>" + item.system_id + "</td>"
							tditem += "<td>" + item.hive_table_name + "</td>"
							/**
							tditem += "<td>" + item.system_id + "</td>"
							tditem += "<td>" + item.db_ip + "</td>"
							tditem += "<td>" + item.db_port + "</td>"
							tditem += "<td>" + item.user_name + "</td>"
							tditem += "<td>" + item.password + "</td>"
							tditem += "<td>" + item.is_ince + "</td>"
							tditem += "<td>" + item.map_sign + "</td>"
							tditem += "<td>" + item.remark + "</td>"
							tditem += "<td>" + item.create_time + "</td>"
							*/
							tditem = "<tr>" + tditem + "</tr>"
							body = body + tditem
						}
						table = "<table id='sourceTable' >" + head
								+ body + "</table>"
						$("#content").empty()
						$("#content").append(table)
					}
				})
	}
	
	function allETL() {
		$.ajax({
					url : "./ob?op=viewETL",
					data : '',
					dataType : "json",
					type : "GET",
					async : true, // 同步
					success : function(response) {
						var list = response.list;
						showAddETL()
						var head = "<tr><th>序号</th><th>任务类型</th><th>脚本地址</th><th>调度信息</th><th>hive表名</th><th>创建人</th><th>备注</th><th>更新时间</th><th>操作</th></tr>"
						var body = ""
						for (var i = 0; i < list.length; i++) {
							var tditem = ""
							var item = list[i]
							tditem += "<td>" + (i+1) + "</td>"
							tditem += "<td>" + item.etl_type + "</td>"
							tditem += "<td>" + item.etl_script + "</td>"
							tditem += "<td>" + item.etl_crontab_info + "</td>"
							tditem += "<td>" + item.hive_table + "</td>"
							tditem += "<td>" + item.owner + "</td>"
							tditem += "<td>" + item.remark + "</td>"
							tditem += "<td>" + item.create_time + "</td>"
							tditem += "<td><a href='javascript:updateETL("+item.etl_id+")'>依赖管理</a></td>"
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
	function allfront() {
		$.ajax({
					url : "./ob?op=viewfront",
					data : '',
					dataType : "json",
					type : "GET",
					async : true, // 同步
					success : function(response) {
						var list = response.list;
						showAddFront()
						var head = "<tr><th>序号</th><th>前端标签</th><th>链接地址</th><th>创建人</th><th>备注</th><th>更新时间</th><th>操作</th></tr>"
						var body = ""
						for (var i = 0; i < list.length; i++) {
							var tditem = ""
							var item = list[i]
							tditem += "<td>" + item.front_id + "</td>"
							tditem += "<td>" + item.label + "</td>"
							tditem += "<td>" + item.url + "</td>"
							tditem += "<td>" + item.creator + "</td>"
							tditem += "<td>" + item.remark + "</td>"
							tditem += "<td>" + item.create_time + "</td>"
							tditem += "<td><a href='javascript:updatefront("+item.front_id+")'>依赖管理</a></td>"
							tditem = "<tr>" + tditem + "</tr>"
							body = body + tditem
						}
						table = "<table border='1' id='frontTable' >" + head
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
	
	
	function addfront(){
		$("#message").empty();
		var r=getRandomNum();
		var trContent="<tr>"+
		"<td></td>"+
		"<td><input id=label_"+r+" value=''/></td>"+
		"<td><input id=url_"+r+" value=''/></td>"+
		"<td><input id=creator_"+r+" value=''/></td>"+
		"<td><input id=remark_"+r+" value=''/></td>"+
		"<td><input></td>"+
		"<td><a href='javascript:createFront("+r+")'>创建流程</a></td>"+
		"</tr>"
		$("#frontTable").append(trContent)
	}
	
	
	function addETL(){
		$("#message").empty();
		var r=getRandomNum();
		var trContent="<tr>"+
		"<td></td>"+
		"<td><input id=etl_type_"+r+" value=''/></td>"+
		"<td><input id=etl_script_"+r+" value=''/></td>"+
		"<td><input id=etl_crontab_info_"+r+" value=''/></td>"+
		"<td><input id=hive_table_"+r+" value=''/></td>"+
		"<td><input id=owner_"+r+" value=''/></td>"+
		"<td><input id=remark_"+r+" value=''/></td>"+
		"<td><input></td>"+
		"<td><a href='javascript:createETL("+r+")'>创建流程</a></td>"+
		"</tr>"
		$("#ETLTable").append(trContent)
	}
	
	function addsource(){
		$("#message").empty();
		var r=getRandomNum();
		var trContent="<tr>"+
		"<td></td>"+
		"<td><input id=table_name_"+r+" value=''/></td>"+
		"<td><input id=hive_table_name_"+r+" value=''/></td>"+
		"<td><input id=system_id_"+r+" value=''/></td>"+
		"<td><input id=db_ip_"+r+" value=''/></td>"+
		"<td><input id=db_port_"+r+" value=''/></td>"+
		"<td><input id=user_name_"+r+" value=''/></td>"+
		"<td><input id=password_"+r+" value=''/></td>"+
		"<td><input id=is_inc_"+r+" value=''/></td>"+
		"<td><input id=map_sign_"+r+" value=''/></td>"+
		"<td><input id=remark_"+r+" value=''/></td>"+
		"<td><input></td>"+
		"<td><a href='javascript:createsource("+r+")'>创建流程</a></td>"+
		"</tr>"
		$("#sourceTable").append(trContent)
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
						$("#message").append("<font color='red'> 增加 "+data+" 条记录</font><br/>")
					}
					
				}
		})
	}
	
	
	function createFront(id){
		var front={
				label:$("#label_"+id).val(),
				url:$("#url_"+id).val(),
				creator:$("#creator_"+id).val(),
				remark:$("#remark_"+id).val()
		}
		var params =$.param(front)
		var urlPath="./ob?op=createfront&"+params
		create(urlPath)
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
	function createsource(id){
		var front={
				table_name:$("#table_name_"+id).val(),
				hive_table_name:$("#hive_table_name_"+id).val(),
				system_id:$("#system_id_"+id).val(),
				db_ip:$("#db_ip_"+id).val(),
				db_port:$("#db_port_"+id).val(),
				user_name:$("#user_name_"+id).val(),
				password:$("#password_"+id).val(),
				is_inc:$("#is_inc_"+id).val(),
				map_sign:$("#map_sign_"+id).val(),
				remark:$("#remark_"+id).val()
		}
		var params =$.param(front)
		var urlPath="./ob?op=createsource&"+params
		create(urlPath)
	}
	
	
	function hideAll(){
		document.getElementById("addsource").style.display="none";
		document.getElementById("addETL").style.display="none";
		document.getElementById("addfront").style.display="none";
	}
	
	function showAddSource(){
		hideAll();
		document.getElementById("addsource").style.display="block";
	}
	function showAddETL(){
		hideAll();
		document.getElementById("addETL").style.display="block";
	}
	function showAddFront(){
		hideAll();
		document.getElementById("addfront").style.display="block";
	}

	$(function() {
		allfront();
	});
	//查看及更新ETL的依赖关系
	function updateETL(id){
		debugger
		window.location="./ob?op=toUpdateETL&etl_id="+id	
	}
	//查看及更新前台的依赖关系
	function updatefront(id){
		window.location="./ob?op=toUpdateFront&front_id="+id	
	}
	
</script>
</head>
<body>
	<div id="message"></div>
	<a href="javascript:allsource()">源表</a>
	<a href="javascript:allETL()">ETL</a>
	<a href="javascript:allfront()">前台</a>

	<div id="addsource">
		<button onclick="addsource()">添加源表</button>
	</div>

	<div id="addETL">
		<button onclick="addETL()">添加ETL</button>
	</div>

	<div id="addfront">
		<button onclick="addfront()">添加前台页面显示</button>
	</div>

	<div id="content"></div>
</body>
</html>
