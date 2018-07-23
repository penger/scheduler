<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>
<html>
<head>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">

	//同步表
	function migrate() {
		var db = $("#db_name").val();
		var table = $("#table_name").val();
		var platform = $("#platform").val();
		var url="./migrate?db="+db+"&table="+table+"&platform="+platform;
		$.ajax({
			url:url,
			type:"GET",
			dataType:"json",
			async:false,
			data:"",
			beforeSend : function(){
				$("#message").append(db+"."+table+" 开始同步请耐心等待 ... ")
			},
			success:function (response) {
				debugger
				$("#message").empty()
				var message = response.message
				$("#message").append(message)
			}

		})
	}
	
</script>
</head>
<body>

	<font color="red">访问地址实际格式为:<br/> http://10.201.48.49:8080/z_bigdata/migrate?db=dbname&table=tablename&platform=pre</font><br/>
	<font color="green">example: http://10.201.48.49:8080/z_bigdata/migrate?db=sourcedata&table=s03_oms_order&platform=pre</font><br/>
	<font color="green">默认平台为pre(10.201.48.1),默认为 10.201.48.101</font><br/>
	<label>库名:</label>
		<input type="text" id="db_name"/>
	<label>表名:</label>
		<input type="text" id="table_name"/>
	<label>平台:</label>
		<input type="text" placeholder="pre" id="platform"/>

	<button onclick="migrate()">同步数据</button>

	<div id="message">	
	</div>
</body>
</html>
