<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">


	function search(pageNum){
		debugger
		var history={
				start_date:$("#start_date").val(),	
				cmd:$("#cmd").val(),
				exit_code:$("exit_code").val(),
				time:$('input[name="time"]:checked').val(),
				start:$('input[name="start"]:checked').val()
		}
		var params=$.param(history)
		var url="./history?op=query&pageNum="+pageNum+"&"+params;
		getHistory(pageNum,url);
	}





	function getHistory(pageNum,url) {
		$.ajax({
					url : url,
					data : '',
					dataType : "json",
					type : 'GET',
					async : true,
					success : function(response) {
						debugger
						var list = response.list
						var pageLength=response.pageLength
						var length=response.length
						var body = ""
						for (var i = 0; i < list.length; i++) {
							debugger
							var tditem = ""
							var item = list[i]
							var time="";
							if(item.spend_time<60000){
								time=((item.spend_time)/1000).toFixed(2);
								time=time+"s"
							}else{
								time=((item.spend_time)/60000).toFixed(2);
								time=time+"min"
							}
							tditem += "<td>" + item.start_date + "</td>"
							tditem += "<td>" + item.task_date + "</td>"
							tditem += "<td>" + item.cmd + "</td>"
							tditem += "<td>" + item.exit_code + "</td>"
							tditem += "<td>" + time + "</td>"
							//tditem += "<td>" + item.spend_time + "</td>"
							tditem += "<td>" + item.message + "</td>"
							tditem = "<tr>" + tditem + "</tr>"
							body = body + tditem
						}
						debugger
						
						$("#content").empty()
						$("#content").html(body)
						var p=""
						for(var i=1;i<=pageLength;i++){
							if(i==pageNum){
								p+="<font size='6'>"+i+"</font>&nbsp;"
							}else{
								p+="<a href='javascript:search("+i+")'>"+i+"</a>&nbsp;"
							}
							if(i%15==0){
								p+="<br/>"
							}
						}
						$("#page").empty()		
						$("#page").append("跳转到:	"+p)
						
						
						
					}
				})
	}

	$(function() {
		search(1);
	});
</script>
</head>
<body>
	<div>
		<label>任务日期:</label><input id="start_date" type="text">
		<label>命令关键词:</label><input id="cmd" type="text">
		<label>返回状态:</label><input id="exit_code" type="text">
		<br/>
		<label>排序规则:</label>
		<br>
		<label>耗时</label>
		<input type="radio" name="time" value="long"/>长 
		<input type="radio" name="time" value="short"/>短
		<label>开始时间</label>
		<input type="radio" name="start" value="early"/>早 
		<input type="radio" name="start" value="late"/>晚
		
		<button onclick="search(1)">搜索</button>
	</div>
	
	<table  border='1'  >
		<tr>
			<th>start_date</th>
			<th>task_date</th>
			<th>cmd</th>
			<th>exit_code</th>
			<th>use_time</th>
			<th>message</th>
		</tr>
		<tbody id="content">
		
		</tbody>
	</table>
	<div id="page"></div>

</body>
</html>
