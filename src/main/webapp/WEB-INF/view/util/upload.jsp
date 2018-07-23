<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>
<html>
<head>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">
	function to_rerun(){
		window.location="./util?op=toRerun"
	}

	function cp(is_padata) {
		var filename=$("#filename").val()
		var tablename=$("#table_name").val()
		if(is_padata=="3" && tablename.length==0){
			alert("需要传入表名,朋友")
		}
		window.location="./util?op=cp&filename="+filename+"&is_pdata="+is_padata+"&table_name="+tablename
	}
</script>
</head>
<body>
	<button onclick="to_rerun()">返回重跑页面</button>
	<br/>
	<font color="red">分两步 : 1. 提交文件  2. 上传到目录 3.如果创建外部表需要填写表名<br/></font>
	<font color="red">.q文件默认同时上传到pre集群<br/></font>
	<br/>
	<form action="./util" enctype="multipart/form-data" method="post">
		<input type="file" name="file"/><br/>
		<input type="submit">
	</form>
	<input id="filename" type="hidden" value="${filename}">
	<input id="table_name" type="text">
	<br>
	<button onclick="cp(1)">上传到PDATA脚本目录</button>
	<button onclick="cp(0)" > ETL SQL 目录 </button>
	<button onclick="cp(3)" >上传文件到外部表/user/hive/external/</button>

	<div id="message" style="color: green">
		${message }<br/>
		${cpinfo }<br/>
		文件名为:${filename }
	</div>
<div style="color: #A60000">
	外部表创建示例:<br/>
	create external table if not exists test_external_table(name string,age int) row format delimited fields terminated by '\t' location '/user/hive/external/test_external_table';
</div>
</body>
</html>
