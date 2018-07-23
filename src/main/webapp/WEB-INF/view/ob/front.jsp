<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>

<html>
<head>
	<link rel="stylesheet" href="css/demo.css" type="text/css">
	<link rel="stylesheet" href="css/zTreeStyle/zTreeStyle.css" type="text/css">
	<script type="text/javascript" src="js/jquery.min.js"></script>
	<script type="text/javascript" src="js/jquery.ztree.core.js"></script>
<script type="text/javascript">
	var setting = {
		view: {
			showIcon: showIconForTree
		},
		data: {
			simpleData: {
				enable: true
			}
		},
		callback:{
			onClick: onClick
		}

	};

	function onClick(event, treeId, treeNode, clickFlag) {
		var s = treeNode
		$("#item_id").text(treeNode.id)
		$("#item_url").text(treeNode.href)
		$("#item_name").text(treeNode.name)
		$("#hidden_id").val(treeNode.id)
		$("#append").empty()
		$("#tasktable").empty()
		$("#message").empty()
		$.ajax({
			url:"./ob?op=toUpdateFront&front_id="+treeNode.id ,
			type:"GET",
			dataType : "json",
			async : true, // 同步
			data : '',
			success:function(response){
				debugger
				for(var i =0 ;i<response.list.length;i++){
					var temp = response.list[i]
					var line="<tr id=tr_"+temp.etl_id+"><td>"+temp.etl_id+"</td><td>"+temp.front_name+"</td><td><a href='javascript:deleteRelation(3,"+temp.etl_id+")'>删除关系</a></td></tr>";
					$("#tasktable").append(line)
				}
			}
		})

	}

	function showIconForTree(treeId, treeNode) {
		return !treeNode.isParent;
	};

	$(document).ready(function(){
		$.ajax({
			url : "./ob?op=getZtree",
			data : '',
			dataType : "json",
			type : "GET",
			async : true, // 同步
			success : function(response) {
				var abc=response;
				debugger
				$.fn.zTree.init($("#treeDemo"), setting, response.list);
			}
		})


		$("#search").focusout(function(){
			$("#append").empty()
			var table=$("#search").val()
			$.ajax({
				url:"./relation?op=searchHive&tableName="+table,
				type:"GET",
				dataType : "json",
				async : true, // 同步
				data : '',
				success:function(response){
					for(var i=0;i<response.list.length;i++){
						var tn=response.list[i].hive_table
						var tid=response.list[i].etl_id;
						$("#append").append(tid+"-->"+tn+" <a href=javascript:addRelation(3,"+tid+",'"+tn+"')>add</a><br/>")
					}
				}
			})
		})



	})

	$("#search").focusin(function(){
		$("#append").empty()
	})

	//添加关系
	function addRelation(type,etl_id,etl_name){
		debugger
		var front_id=$("#hidden_id").val()
		$.ajax({
			url:"./relation?op=addRelation&type="+type+"&etl_id="+etl_id+"&front_id="+front_id ,
			type:"GET",
			dataType : "html",
			async : true, // 同步
			data : '',
			success:function(response){
				debugger
				$("#message").append("增加ETL: "+etl_id)
				var line="<tr id=tr_"+etl_id+"><td>"+etl_id+"</td><td>"+etl_name+"</td><td><a href='javascript:deleteRelation(3,"+etl_id+")'>删除关系</a></td></tr>";
				$("#tasktable").append(line)
			}
		})
	}

	//删除关系
	function deleteRelation(type,etl_id){

		var front_id=$("#hidden_id").val()
		$.ajax({
			url:"./relation?op=deleteRelation&type="+type+"&etl_id="+etl_id+"&front_id="+front_id ,
			type:"GET",
			dataType : "html",
			async : true, // 同步
			data : '',
			success:function(data){
				if(data!=0){
					$("#tr_"+etl_id).remove();
					$("#message").append("删除ETL: "+etl_id)
				}else{
					alert("删除失败")
				}

			}
		})

	}
</script>

</head>
<body>
	<div id="message"></div>
	<a href="./ob?op=source">源表</a>
	<a href="./ob?op=etl">ETL</a>
	<a href="./ob?op=front">前台</a>

	<div class="content_wrap">
		<div class="zTreeDemoBackground left">
			<ul id="treeDemo" class="ztree"></ul>
		</div>
		<div class="right">
			<ul class="info">
				<li id="item_id"></li>
				<input id="hidden_id" type="hidden" value="">
				<li id="item_url"></li>
				<li id="item_name"></li>
			</ul>
			<label>查找依赖的ETL:</label> <input id="search" TYPE="text" maxlength="150">
			<table border="1" id="tasktable">
				<tr>
					<td>etl_id</td>
					<td>hive_table</td>
					<td>操作</td>
				</tr>
			</table>
			<div id="append"></div>
		</div>
	</div>
</body>
</html>
