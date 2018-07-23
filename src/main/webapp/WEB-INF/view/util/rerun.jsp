<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>
<html>
<head>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">
/**
 * 
	$(function(){
			$.ajax({
				url:"./util?op=view",
				type:"GET",
				dataType : "json",
				async : true, // 同步
				data : '',
				success:function(response){
					var lsinfo = response.lsinfo
					$("#message").append(lsinfo)
				}
			})		
	})	
 */
 	function cls(){
 		$("#message").empty();
	}
 
 	function upload(){
 		window.location="./util?op=upload"
 	}

 	function download(){
		var script=$("#script_name").val()
		var is_pdata=$('input[name="is_pdata"]:checked').val()
		window.location="./util?op=download&is_pdata="+is_pdata+"&script="+script;
	}

 
 	function createShell(){
 		window.location="./util?op=toCreateShell"
 	}
 	
 	function viewSQLContent(){
 		var script=$("#script_name").val()
		var is_pdata=$('input[name="is_pdata"]:checked').val()
 		$.ajax({
			url:"./util?op=viewSQLContent&script="+script+"&is_pdata="+is_pdata,
			type:"GET",
			dataType : "json",
			async : true, // 同步
			data : '',
			success:function(response){
				debugger
				var lsinfo = response.map.message
				$("#message").empty();
				$("#message").append(response.map.sort)
				$("#message").append(lsinfo)
			}
		})
 	}
 	
 	
	function viewSQL(){
		var is_pdata=$('input[name="is_pdata"]:checked').val()
			debugger
			$.ajax({
				url:"./util?op=viewSQL&is_pdata="+is_pdata,
				type:"GET",
				dataType : "json",
				async : true, // 同步
				data : '',
				success:function(response){
					debugger
					var lsinfo = response.map.message
					$("#message").empty();
					$("#message").append(response.map.sort)
					$("#message").append(lsinfo)
				}
			})		
	}
	
	function view(){
		var is_pdata=$('input[name="is_pdata"]:checked').val()
		$.ajax({
			url:"./util?op=view&is_pdata="+is_pdata,
			type:"GET",
			dataType : "json",
			async : true, // 同步
			data : '',
			success:function(response){
				debugger
				var lsinfo = response.map.message
				$("#message").empty();
				$("#message").append(response.map.sort)
				$("#message").append(lsinfo)
			}
		})		
}
	
	function viewCrontab(){
		$.ajax({
			url:"./util?op=viewCrontab",
			type:"GET",
			dataType : "json",
			async : true, // 同步
			data : '',
			success:function(response){
				debugger
				var lsinfo = response.map.message
				$("#message").empty();
				$("#message").append(response.map.sort)
				$("#message").append(lsinfo)
			}
		})	
	}
	
	
	
	
	function rerun_clock(){
		$("#clock").addClass('divcssblock');
		clock()
		rerun()
	}
	
	
	$(function(){
		$("#is_custom").hide();
		$("input[name='is_custom']").change(function (){
			var is_custom=$('input[name="is_custom"]:checked').val()
			console.log(is_custom)
			if(is_custom==1){
				$("#is_custom").show();
				$("#not_custom").hide();
			}else{
				$("#not_custom").show();
				$("#is_custom").hide();
			}
		})
	})
	
	function rerun(){
		var script=$("#script_name").val()
		var start_date=$("#start_date").val()
		var end_date=$("#end_date").val()
		var custom=$("#custom").val()
		var is_custom=$('input[name="is_custom"]:checked').val()
		
		$("#message").empty();
		$("#message").append("开始重跑,耗时比较长,耐心等待!<br/>")
		$("#message").append("<font color='red'>仅提供开始日期的日志,其他日期的日志可以自行修改url中的日期查看</font><br/>")
		$("#message").append("<a href='http://10.201.48.49/etl/"+start_date+"/"+script+".log'>详细日志查看</a>")
		$.ajax({
				url:"./util?op=rerun&script="+script+"&start_date="+start_date+"&end_date="+end_date+"&custom="+custom+"&is_custom="+is_custom,
				type:"GET",
				dataType : "html",
				async : true, // 同步
				data : '',
				success:function(response){
					debugger
					var message = response
					$("#message").append(message)
					//$("#clock").addClass('divcssnone');
					$("#clock").hide();
				}
			})
		/**
			*/
	}
	
	
	function clock()
	{
		var oClock = document.getElementById("clock");
		var aSpan = oClock.getElementsByTagName("span");
		setInterval(getTimes, 1000);
		getTimes();
		var time=0;
		function getTimes (){
				time=time+1
				aSpan[0].innerHTML = time 
			}
	}
	
	
</script>

<style>
.divcssnone{display:none}
.divcssblock{display:block}
</style>
</head>
<body>
	<label>script name:</label><input type="text" id="script_name"/>
	<br/>
	<input type="radio" name="is_custom" value="0" checked="checked" >
	<label>时间段选取</label>
	<input type="radio" name="is_custom" value="1" >
	<label>手动传参(周,月)</label>
	<br/>
	<input type="radio" name="is_pdata" value="0" checked="checked" >
	<label>非PDATA系列</label>
	<input type="radio" name="is_pdata" value="1" >
	<label>PDATA系列</label>
	<br/>
	<div id="not_custom">
		<label>between:</label><input type="text" id="start_date" value="${d }"/>
		<label>and</label><input type="text" id="end_date" value="${d }" />
	</div>
	<div id="is_custom">
		<textarea id="custom" rows="2" cols="100"></textarea>
	</div>	
	<br/>
	<button onclick="rerun_clock()">重跑</button>
	<br/>
	<button onclick="createShell()">创建shell&查看shell</button>
	<button onclick="upload()">去上传sql界面,上传数据到外部表</button>
	<button onclick="download()">下载sql脚本</button>
	<br />
	<button onclick="view()">查看shell脚本列表</button>
	<button onclick="viewSQL()">查看SQL列表</button>
	<button onclick="viewSQLContent()">查看SQL内容</button>
	<!-- -->
	<button onclick="viewCrontab()">查看调度</button>
	<button onclick="cls()">清屏</button>
	<br/>
	<div id="clock" class="divcssnone">
		已经用去<span></span>秒
	</div>
	<div id="message" style="color: green">
	</div>
</body>
</html>
