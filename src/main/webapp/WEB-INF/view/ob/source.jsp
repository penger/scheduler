<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head>
<style>
.table_class{
	float:left;
	width:40%;
}
.map_class{
	float:left;
	width:60%;
}
.node circle {
  fill: #fff;
  stroke: steelblue;
  stroke-width: 1.5px;
}

.node {
  font: 12px sans-serif;
}

.link {
  fill: none;
  stroke: #ccc;
  stroke-width: 1.5px;
}
</style>

<script src="./js/d3.v4.js" charset="utf-8"></script>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">
	function allsource(in_use) {
		$.ajax({
					url : "./ob?op=viewsource&in_use="+in_use,
					data : '',
					dataType : "json",
					type : "GET",
					async : true, // 同步
					success : function(response) {
						//先画表,后画图
						debugger
						var list = response.list;
						var head = "<tr><th>序号</th><th>系统</th><th>hive table</th><th>上层依赖</th></tr>"
						var body = ""
						for (var i = 0; i < list.length; i++) {
							var tditem = ""
							var item = list[i]
							console.log(item)
							tditem += "<td>" + (i+1) + "</td>"
							tditem += "<td>" + item.system_id + "</td>"
							tditem += "<td>" + item.hive_table_name + "</td>"
							tditem += "<td><a href=javascript:searchRelation('"+item.hive_table_name+"',"+item.source_id+")>查看上层</a></td>"
							//tditem += "<td><a href=javascript:searchRelation2('"+item.hive_table_name+"')>查看上层</a></td>"
							tditem = "<tr>"+tditem+"</tr>"
							body = body + tditem
						}
						table = "<table id='sourceTable' >" + head
								+ body + "</table>"
						$("#content").empty()
						$("#content").append(table)
						
						drawMap(list);
						//drawMap2(response.tree);
					}
				})
	}
	
	/**
	*FIXME 可能会产生随机数碰撞
	* 产生参数为 1000~9999
	*/
	function getRandomNum(){
		var r=Math.random()*9000+1000;
		r=Math.floor(r)
		return r;
	}
	
	function searchRelation(hive_table_name,id){
		window.location="./ob?op=searchSource&source_id="+id+"&hive_table="+hive_table_name	
	}
	function searchRelation2(id){
		alert(id)
	}
	
	
	function addsource(){
		$("#message").empty();
		var r=getRandomNum();
		var trContent="<tr>"+
		"<td></td>"+
		"<td><input id=system_id_"+r+" value=''/></td>"+
		"<td><input id=table_name_"+r+" value=''/></td>"+
		"<td><input id=hive_table_name_"+r+" value=''/></td>"+
		"<td><a href='javascript:createsource("+r+")'>创建流程</a></td>"+
		"</tr>"
		$("#sourceTable").append(trContent)
	}
	
	
	
	function create(urlPath){
		$.ajax({
			url : urlPath,
			data: '',
			dataType : "html",
			type : "GET",
			async : true, // 同步
			success : function(data) {
					debugger
					if(data==1){
						alert("源表已经添加")
					}
					
				}
		})
	}
	
	
	function createFront(id){
		var front={
				label:$("#label_"+id).val(),
				url:$("#url_"+id).val(),
				creator:$("#creator_"+id).val(),
				remark:$("#remark_"+id).val()
		}
		var params =$.param(front)
		var urlPath="./ob?op=createfront&"+params
		create(urlPath)
	}
	

	function createsource(id){
		var front={
				table_name:$("#table_name_"+id).val(),
				hive_table_name:$("#hive_table_name_"+id).val(),
				system_id:$("#system_id_"+id).val()
		}
		var params =$.param(front)
		var urlPath="./ob?op=createsource&"+params
		create(urlPath)
	}
	

	$(function() {
		allsource("");
	});
	
	function in_use(in_use){
		allsource(in_use)
	}
	
	function drawMap(list){
		var nodes=list;
		var svg=d3.select("#svg1"),
			width=svg.attr("width"),
			height=svg.attr("height");
		var force=d3.layout.force()
					.nodes(list)
					.size([width,height])
					.linkDistance(100)
					.charge(-100);
		force.start();
		var color=d3.scale.category20();
		//添加节点
		var svg_nodes=svg.selectAll("circle").data(nodes)
							.enter()
							.append("circle")
							.attr("r",5)
							.style("fill",function(d,i){
								debugger;
								var system_id=d.system_id;
								console.log(system_id);
								if(system_id==undefined){
									return "black";
								}
								var color_index=1;
								if(system_id.indexOf("s0")){
									color_index = system_id.substring(2,system_id.length);
								}else{
									color_index = system_id.substring(1,system_id.length);
								}
								return color(color_index);
							})
							.call(force.drag);
		
		var svg_texts=svg.selectAll("text")
						.data(nodes)
						.enter()
						.append("text")
						.text(function(d){
							var label=d.hive_table_name;
							label = label.substring(4,100);
							return label;
						});
		
	force.on("tick",function(){
		svg_nodes.attr("cx",function(d){return d.x})
				.attr("cy",function(d){return d.y});
		svg_texts.attr("x",function(d){return d.x})
				.attr("y",function(d){return d.y});
	});						
		
	}
	
	
	function drawMap2(map){
		console.log(map);
		
		var svg=d3.select("#svg2"),
		width=svg.attr("width"),
		height=svg.attr("height");
		
		svg.attr("width",width)
			.attr("height",height)
			.append("g")
			.attr("transform","translate(40,0)");

		var tree = d3.layout.tree().size([width,height-200])
					.separation(function(a,b){ return (a,parent == b.parent ? 1:2)});
		
		var diagonal = d3.svg.diagonal()
						.projection(function(d){return [d.y,d.x]});
		
		
		d3.json(map,function(error,root){
			var nodes =tree.nodes(root);
			var links =tree.links(nodes);
			
			
			console.log(nodes);
			console.log(links);
			
			var link = svg.selectAll(".link")
			  .data(links)
			  .enter()
			  .append("path")
			  .attr("class", "link")
			  .attr("d", diagonal);
			
			var node = svg.selectAll(".node")
			  .data(nodes)
			  .enter()
			  .append("g")
			  .attr("class", "node")
			  .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; })
			
			node.append("circle")
			  .attr("r", 4.5);
			
			node.append("text")
			  .attr("dx", function(d) { return d.children ? -8 : 8; })
			  .attr("dy", 3)
			  .style("text-anchor", function(d) { return d.children ? "end" : "start"; })
			  .text(function(d) { return d.name; });
			
		});
		
	}
	
</script>
</head>
<body>
	<div id="message"></div>
	<a href="./ob?op=source">源表</a>
	<a href="./ob?op=etl">ETL</a>
	<a href="./ob?op=front">前台</a>

	<div id="addsource">
		<button onclick="addsource()">添加源表</button>
		<button onclick="in_use('no')">没用的源表</button>		
		<button onclick="in_use('yes')">在用的源表</button>		
	</div>
	<div>
		<div id="content" class="table_class"></div>
		<div id="map" class="map_class" >
			<svg id="svg1" width="860" height="800"></svg>
		</div>
		<div id="map2" class="map_class" >
			<svg id="svg2" width="860" height="1000"></svg>
		</div>
	</div>
</body>
</html>
