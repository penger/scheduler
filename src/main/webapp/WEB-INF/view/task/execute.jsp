<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page isELIgnored="false"%>
<html>
<head>
	<link href="css/base.css"rel="stylesheet"type="text/css"/>
<style>

body {
  font: 10px sans-serif;
}

.bar rect {
  fill: steelblue;
}

.bar text.value {
  fill: white;
}

.axis {
  shape-rendering: crispEdges;
}

.axis path {
  fill: none;
}

.x.axis line {
  stroke: #fff;
  stroke-opacity: .8;
}

.y.axis path {
  stroke: black;
}

.MyText {
	fill: black;
	//text-anchor: left;
}
</style>

<script src="./js/d3.v4.js" charset="utf-8"></script>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">
/**
 * 
 */
	$(function(){
		var rectHeight=25;
		var width=1200;
		var json=${jsontasks}
		var tasks= json.tasks;
		var height=rectHeight*tasks.length+100;

		fillTable(tasks);


		var svg=d3.select("body")
				.append("svg")
				.attr("width",width)
				.attr("height",height)
		//定义域,值域
		//留下200像素作为表名
		var linear = d3.scale.linear()
					.domain([0,d3.max(tasks,function(tasks){
						console.log(tasks.avaragetime)
						return tasks.avaragetime;
					})])
					.range([0,800])
				
		var rectHeight=25;
		
		svg.selectAll("rect")
		   .data(tasks)
		   .enter()
		   .append("rect")
		   .attr("x",300)
		   .attr("y",function(d,i){
			   return (i+1)*rectHeight;
		   })
		   .attr("width",function(d,i){
			   var r=linear(d.avaragetime);
			   return r;
		   })
		   .attr("height",rectHeight-2)
		   .attr("fill","steelblue");
		
		var texts = svg.selectAll(".MyText")
					.data(tasks)
					.enter()
					.append('text')
					.attr('class','MyText')
					.attr('transform','translate()')
					.attr('x',function(d,i){
						return 100;
					})
					.attr('y',function(d,i){
						return rectHeight*(i+1)+rectHeight/2;
					})
					.attr('dx',function(d){
						return 0;
					})
					.attr('dy',function(d){
						return 0;
					})
					.text(function(d){
						return d.command;
					})
		
		var xaxis=d3.svg.axis()
				.scale(linear)
				.orient("bottom")
				.ticks(8);
		
		
		
		svg.append("g")
			.attr("class","axis")
			.attr("transform","translate(300,0)")
			.call(xaxis);
	
	
		
		
	})
	
 	//单独执行task 同步,当任务执行成功后变
	function executeTask(id,atpre,sync){
		if(sync!=false){
			sync=true;
		}
		var urlPath="./task?op=exec&id="+id+"&atpre="+atpre
		$.ajax({
			url : urlPath,
			data: "",
			dataType : "html",
			type : "GET",
			async : true, // 同步
			beforeSend : function(){
				$("#id_"+id).css("bgcolor","yellow");
				$("#message").append(" task " +id+" is running ...<br/>");
			},
			success : function(data) {
					var obj =$.parseJSON(data)
					if(obj.exit_code=="0"){
						$("#id_"+id).css("bgcolor","green")
					}else{
						$("#id_"+id).css("bgcolor","red")
					}
					$("#message").append("message :"+obj.message+"<br/>");
					$("#message").append("exit code:"+obj.exit_code+"<br/>");
					$("#message").append("time :"+obj.spend_time+" ms<br/>");
				}
		})
	}


	function fillTable( tasks ) {
		var whole_content="";
		for(var i=0;i<tasks.length;i++ ){
			var time="";
			var t=tasks[i];
			if(t.avaragetime >60000){
				time=(t.avaragetime/60000).toFixed(2);
				time=time+"min";
			}else{
				time=(t.avaragetime/1000).toFixed(2);
				time=time+"s";
			}
			var log_log=t.command.replace(".sh",".log")
			var log_path=t.logpath+""+${one_day_before}+"/"+log_log
			var log_path_pre=log_path.replace("48.49","48.2")

			var itemline="";
			itemline+="<td id='id_'"+t.id+">"+t.id+"</td>";
			itemline+="<td id='seq_'"+t.id+">"+t.seq+"</td>";
			itemline+="<td id='tname_'"+t.id+">"+t.tname+"</td>";
			itemline+="<td id='tdesc_'"+t.id+">"+t.tdesc+"</td>";
			itemline+="<td id='taskhost_'"+t.id+">"+t.taskhost+"</td>";
			itemline+="<td id='tasktype_'"+t.actor+">"+t.actor+"</td>";
			itemline+="<td id='commandpath_'"+t.id+">"+t.commandpath+"</td>";
			itemline+="<td id='command_'"+t.id+">"+t.command+"</td>";
			itemline+="<td id='args_'"+t.id+">"+t.args+"</td>";
			itemline+="<td bgcolor="+t.color+"> "+t.status+"</td>";
			itemline+="<td id='avaragetime_'"+t.id+">"+time+"</td>";
			itemline+="<td> " +
					"<a href='javascript:executeTask("+t.id+",0,true)'> 执行</a>&nbsp;" +
					" <a href='"+log_path+"'> 日志</a>&nbsp;" +
					"</td>";
			whole_content=whole_content+"<tr>"+itemline+"</tr>";
		}
		$("#t_tbody").append(whole_content);
	}

</script>
</head>
<body>
	
	<br />
	<!--  考虑并发,已经废弃此方法 
	<button onclick="javascript:executeAll(${id})">执行全部</button><br/>
	 -->
	<button onclick="javascript:clearMessage()">清空</button><br/>
	<table>
		<tr>
			<td bgcolor="gray">任务无效</td>
			<td bgcolor="green">已经完成</td>
            <td bgcolor="yellow">正在运行</td>
            <td bgcolor="red">运行失败</td>
			<td bgcolor="#d2691e">再重试验</td>
            <td bgcolor="#7fffd4">待运行</td>
            <td bgcolor="blue">异常状态</td>
		</tr>
	</table>
	<table border="1" id="tasktable">
		<tr>
			<th>id</th>
			<th>seq</th>
			<th>tname</th>
			<th>thesc</th>
			<th>宿主机</th>
			<th>执行用户</th>
			<th>commandpath</th>
			<th>command</th>
			<th>args</th>
			<th>状态</th>
			<th>平均执行时间</th>
			<th>action</th>
		</tr>
		<tbody id="t_tbody">

		</tbody>
	</table>
	<div id="message" style="color:red"></div>
	<div class="someclass">
		<div id="bar-chart"></div>
	</div>
	
</body>
</html>
