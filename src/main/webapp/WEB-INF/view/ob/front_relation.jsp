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
	
	function addRelation(type,source_id){
		debugger
		var front_id=${front_id}
		//跳到添加关系的页面,再冲添加关系的页面返回到当前页面
		$.ajax({
			url:"./relation?op=addRelation&type="+type+"&source_id="+source_id+"&front_id="+front_id ,
			type:"GET",
			dataType : "html",
			async : true, // 同步
			data : '',
			success:function(response){
				$("#message").append("增加"+response+"条记录 ! 刷新查看")
			}
		})
			
	}
	
	
	

	$(function() {
		
		init()
		
		$("#search_from_source").focusout(function(){
			var sourceName=$("#search_from_source").val()
			$.ajax({
				url:"./relation?op=searchSource&tableName="+sourceName,
				type:"GET",
				dataType : "json",
				async : true, // 同步
				data : '',
				success:function(response){
					for(var i=0;i<response.list.length;i++){
						var tn=response.list[i].hive_table_name
						var tid=response.list[i].source_id
						$("#append").append(tid+"-->"+tn+"<a href='javascript:addRelation(2,"+tid+")'>增加</a><br/>")
					}
				}
			})
		})
		$("#search_from_source").focusin(function(){
			$("#append").empty()
		})
		
		
		$("#search_from_hive").focusout(function(){
			var tableName=$("#search_from_hive").val()
			$.ajax({
				url:"./relation?op=searchHive&tableName="+tableName,
				type:"GET",
				dataType : "json",
				async : true, // 同步
				data : '',
				success:function(response){
					for(var i=0;i<response.list.length;i++){
						var tn=response.list[i].hive_table
						var tid=response.list[i].etl_id
						$("#append2").append(tid+"-->"+tn+" <a href='javascript:addRelation(3,"+tid+")'>add</a><br/>")
					}
				}
			})
		})
		$("#search_from_hive").focusin(function(){
			$("#append2").empty()
		})
		
	})
	function updateETL(hive_table,id){
		debugger
		window.location="./ob?op=toUpdateETL&etl_id="+id+"&hive_table="+hive_table	
	}
</script>
</head>
<body>
	<div id="message">${etl_id }</div>
	<div id="s2e">
		依赖的源表为:<br />
		查找源表:<input id="search_from_source" type="text" maxlength=150 />
		<div id="append"></div>
		<table border="1" id="s2e">
			<tr>
				<td>id</td>
				<td>hive_table_name</td>
			</tr>
			<c:forEach items="${source2front }" var="item" varStatus="status">
				<tr>
					<td >${item.source_id }</td>
					<td >${item.hive_table_name }</td>
				</tr>
			</c:forEach>
		</table>
	</div>

	<div id="e2e">
		依赖的中间表为:<br />
		查找hive表:<input id="search_from_hive" type="text" maxlength=100 />
		<div id="append2"></div>
		<table border="1" id="tasktable">
			<tr>
				<td>etl_id</td>
				<td>hive_table</td>
			</tr>
			<c:forEach items="${etl2front }" var="item" varStatus="status">
				<tr>
					<td>${item.etl_id }</td>
					<td><a href=javascript:updateETL('${item.hive_table }',${item.etl_id })>${item.hive_table }</a></td>
				</tr>
			</c:forEach>
		</table>
	</div>
</body>
</html>
