<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">


	function search(){
		var history={
				start_date:$("#start_date").val(),
				end_date:$("#end_date").val(),
				table_name:$("#table_name").val(),
				type:$("#type").val()
		}
		var params=$.param(history)
		var url="./monitor?op=query&"+params;
		getHistory(url);
	}





	function getHistory(url) {
		$.ajax({
					url : url,
					data : '',
					dataType : "json",
					type : 'GET',
					async : true,
					success : function(response) {
						debugger
						var list = response.list;
						var body = ""
						for (var i = 0; i < list.length; i++) {
							debugger
							var tditem = ""
							var item = list[i]

							tditem += "<td>" + item.etl_id + "</td>"
							tditem += "<td>" + item.table_name + "</td>"
							tditem += "<td>" + item.type + "</td>"
							tditem += "<td>" + item.count + "</td>"
							tditem += "<td>" + item.count_date + "</td>"
							tditem += "<td>" + item.insert_date + "</td>"
							tditem = "<tr>" + tditem + "</tr>"
							body = body + tditem
						}
						$("#content").empty()
						$("#content").html(body)
					}
				})
	}

	$(function() {
		search();
	});
</script>
</head>
<body>
	<div>
		<label>开始日期:</label><input id="start_date" type="text">
		<label>结束日期:</label><input id="end_date" type="text">
		<label>表名:</label><input id="table_name" type="text">
		<label>类型:</label><input id="type" type="text">
		<br/>


		
		<button onclick="search(1)">搜索</button>
	</div>
	
	<table  border='1'  >
		<tr>
			<th>etl_id</th>
			<th>table_name</th>
			<th>type</th>
			<th>count</th>
			<th>count_date</th>
			<th>update_time</th>
		</tr>
		<tbody id="content">
		
		</tbody>
	</table>

</body>
</html>
