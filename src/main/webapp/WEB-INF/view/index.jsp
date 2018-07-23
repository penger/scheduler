<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<html>
<head>
	<link href="css/base.css"rel="stylesheet"type="text/css"/>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">


	function allBatch() {
		$.ajax({
					url : "./index?op=batchlist",
					data : '',
					dataType : "json",
					type : "GET",
					async : true, // 同步
					success : function(response) {
						var list = response.list;
						var head = "<tr><th>批次序号</th><th>批次名称</th><th>批次描述</th><th>前批次</th><th>后批次</th>"
								+ "<th>批次状态</th></tr>"
						var body = ""
						for (var i = 0; i < list.length; i++) {
							var tditem = ""
							var item = list[i]
							tditem += "<td>" + i + "</td>"
							tditem += "<td>" + item.bname + "</td>"
							tditem += "<td>" + item.bdesc + "</td>"
							tditem += "<td>" + item.beforeid + "</td>"
							tditem += "<td>" + item.afterid + "</td>"
							tditem += "<td>" + item.status + "</td>"
							body = body + tditem
						}
						table = "<table border='1'  class='data_list' >" + head
								+ body + "</table>"
						$("#content").empty()
						$("#content").append(table)

					}
				})
	}


	$(function() {
		allBatch();
	});
</script>
</head>
<body>
	<!--
	<a href="javascript:allBatch()">batch 的配置信息</a>
	<br/>
	<a href="javascript:allTask()">task 的配置信息</a>
	<br/>
	-->
	<a href="./batch">批次管理页面</a> &nbsp;&nbsp;&nbsp;
	<a href="./history">历史信息</a>

	<!--
	<a href="./monitor">监控</a>&nbsp;&nbsp;&nbsp;
	<br/>
	<a href="./util?op=toRerun">ETL脚本上传及重跑页面</a>&nbsp;&nbsp;&nbsp;
	<a href="./ob?op=etl">ETL</a>
	<a href="./migrate">PRD hive 同步到PRE</a>
	<br/>
	<a href="./ob?op=front">前端对应后台</a>
	<br/>
	<a href="./ob?op=source">添加的源表</a>
    <br/>
    <a href='http://10.201.48.26/etl/${yesterday}/error.log'>etl error log</a>
    <br/>
    <a href='http://10.201.48.26/pdatalog/${yesterday}/error.log'>pdata error log </a>
    <br/>
	<a href="javascript:refreshMap()">刷新内存map</a>
	<br/>
    <a href='./monitor?op=refreshFront'>修改前端页面后点我</a>
    <br/>
    <a href='./monitor?op=oracleCount2Mysql'>获取oracle中的数据量 </a>
	<br/>
	<a href='./email?op=getTaskInfo'>发送批次运行状态 </a>
	<br/>
	-->
	<div id="content"></div>

</body>
</html>
