<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head>
	<link href="css/base.css"rel="stylesheet"type="text/css"/>
<script src="./js/d3.v4.js" charset="utf-8"></script>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">

	$(function(){
		list();
	})

	function update(id){
		window.location="./batch?op=toUpdatePage&id="+id
	}
	
	//执行 batch 的子任务
	function executeSubTask(id,taskdate){
		window.location="./batch?op=toExecutePage&id="+id+"&taskdate="+taskdate
	}
	
	//添加子任务
	function editSubTask(id){
		window.location="./batch?op=toEditTaskPage&id="+id
	}
	
	function del(id){
		urlPath="./batch?op=d&id="+id
		$.ajax({
			url : urlPath,
			data: '',
			dataType : "html",
			type : "GET",
			async : true, // 同步
			success : function(data) {
					if(data==1){
						$("#message").append(id+"删除成功")
						$("#tr_"+id).remove();
					}else{
						alert("删除失败")
					}
				}
		})
	}

 
	
	function list(){
		$.ajax({
			url : "./batch?op=r",
			data: '',
			dataType : "json",
			type : "GET",
			async : true, // 同步
			success : function(response) {
					var color= new Array("gray","green","yellow","red","blue")
					var list = response.list;
					var head="<tr><th>批次</th><th>名称</th>"+
							"<th>描述</th>"+
							"<th>cron表达式</th>"+
							"<th>调度解释</th>"+
							"<th>最大并发数量</th>"+
							"<th>下次执行时间</th>"+
							"<th>操作</th></tr>"
					var body=""
					for(var i=0;i<list.length;i++){
						var tditem=""
						var item=list[i]
						
						tditem+="<td>"+item.id+"</td>"
						tditem+="<td  bgcolor='"+color[item.flag]+"'  >"+item.bname+"</td>"
						tditem+="<td>"+item.bdesc+"</td>"
						tditem+="<td>"+item.cronexpression+"</td>"
						tditem+="<td>"+item.cronexplain+"</td>"
						tditem+="<td>"+item.threadcount+"</td>"
						tditem+="<td>"+item.readabletime+"</td>"
						tditem=tditem+"<td><a href='javascript:update("+item.id+")'>更新</a>"+"&nbsp;<a href='javascript:executeSubTask("+item.id+","+item.taskdate+")'>查看子任务</a></td>"
						tditem="<tr id=tr_"+item.id+">"+tditem+"</tr>";
						body=body+tditem
					}
					table="<table border='1'>"+head+body+"</table>"
					$("#content").empty()
					$("#content").append(table)
					
					draw(list,color);
					
				}
		})
	}
	
	
	function draw(list,color){
		
		var edges=new Array();
		console.log(list.length)
		for(var i=0;i<list.length;i++){
			var item=list[i];
			console.log(item.afterid)
			if(item.afterid!=-1){
				var o =new Object();
				o.source=i;
				o.target=i+1;
				console.log(o+"------"+i+"    "+(i+1))
				edges.push(o);
				debugger
			}
		}
		
		console.log(edges)
		
		var height=900;
		var width=1000;
		var svg=d3.select("svg")
			.attr("width",width)
			.attr("height",height);
		var force=d3.layout.force()
					.nodes(list)
					.links(edges)
					.size([width,height])
					.linkDistance(70)
					.charge(-700);
		
		force.start();
		
		console.log(color)
		
		
		
		var svg_texts=svg.selectAll("text")
						.data(list)
						.enter()
						.append("text")
						.attr("dx",20)
						.attr("dy",0)
						.text(function(d){
							debugger
							return d.bdesc;
						});
		
		var svg_edges=svg.selectAll("line")
						.data(edges)
						.enter()
						.append("line")
						.style("stroke","#999")
						.style("stroke-width",2);
		
		var svg_nodes=svg.selectAll("circle")
		.data(list)
		.enter()
		.append("circle")
		.attr("r",15)
		.style("fill",function(d,i){
			console.log(d.flag)
			var colorx=color[d.flag]
			return colorx;
		})
		.call(force.drag);
		
		
		force.on("tick",function(){
			svg_nodes.attr("cx",function(d){return d.x})
					.attr("cy",function(d){return d.y});
			svg_texts.attr("x",function(d){return d.x})
					.attr("y",function(d){return d.y});
			svg_edges.attr("x1",function(d){return d.source.x;})
					.attr("y1",function(d){return d.source.y;})
					.attr("x2",function(d){return d.target.x;})
					.attr("y2",function(d){return d.target.y;});
			/**
			*/
		})	
		
	}
	

</script>
</head>
<body>
<font color="red"></font>
<a href="javascript:list()">batch list</a>
<a href="./batch?op=toCreatePage">create batch</a>
<table>
	<tr>
		<td bgcolor="gray">任务无效</td>
		<td bgcolor="green">已经完成</td>
		<td bgcolor="yellow">正在运行</td>
		<td bgcolor="red">运行失败</td>
		<td bgcolor="blue">异常状态</td>
		<td bgcolor="#7fffd4">待运行</td>
		<td bgcolor="#d2691e">再重试验</td>
	</tr>
</table>
<div id="content"></div>
<svg></svg>
<div id="message" style="color:red"></div>
</body>
</html>
