<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>
<html>
<head>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">
	function init() {
		debugger
		var isEmpty=${isEmpty}
		if (isEmpty==1) {
			$("#message").append("没有依赖关系")
		}
	}
	


	$(function() {
		
		init()

	})
</script>
</head>
<body>
	<div id="message">${source_id } --- > ${hive_table} </div>
	<div id="s2e">
		依赖的源表为:<br />
		<div id="append"></div>
		<table border="1" id="s2e">
			<tr>
				<td>id</td>
				<td>hive_table</td>
			</tr>
			<c:forEach items="${source2etl }" var="item" varStatus="status">
				<tr>
				<!-- 
					<td >${item.etl_id }</td>
				 -->
					<td >${status.index+1 }</td>
					<td >${item.hive_table }</td>
				</tr>
			</c:forEach>
		</table>
	</div>

	<div id="e2e">
		依赖的中间表为:<br />
		<div id="append2"></div>
		<table border="1" id="tasktable">
			<tr>
				<td>front_id</td>
				<td>对应页签</td>
				
			</tr>
			<c:forEach items="${source2front }" var="item" varStatus="status">
				<tr>
					<td>${item.front_id }</td>
					<td>${item.label }</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</body>
</html>
