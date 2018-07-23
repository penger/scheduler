<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<html>
<head>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">

 
	function addBatch(){
		var urlPath=getParams();
		urlPath="./batch?op=c&"+encodeURI(urlPath)
		$.ajax({
			url : urlPath,
			data: '',
			dataType : "html",
			type : "GET",
			async : true, // 同步
			success : function(data) {
					$("#message").append("<font color='red'>"+data+"</font><br/>")
				}
		})
	}
	
	
	function getParams(){
		
		var item = {
				bname:$("#bname").val(),
				bdesc:$("#bdesc").val(),
				beforeid:$("#beforeid").val(),
				afterid:$("#afterid").val(),
				flag:$("#flag").val(),
				nextexecutetime:$("#nextexecutetime").val(),
                cronexpression:$("#cronexpression").val(),
                cronexplain:$("#cronexplain").val()
		}
		var params =$.param(item)
		return params
	}

</script>
</head>
<body>
<a href="./batch">创建批次</a>
<br/>
<div id="message"></div>
<label>批次名称</label>
<input id="bname" type="text" value="批次名称"/><br/>
<label>描述</label>
<input id="bdesc" type="text" value="批次描述"/><br/>
<label>前置批次id</label>
<input id="beforeid" type="text" value="-1"/>前一个批次的id(-1表示没有)<br/>
<label>后续批次id</label>
<input id="afterid" type="text" value="-1"/>后一个批次的id(-1表示没有)<br/>
<label>任务执行日期</label>
<input id="flag" type="text" value="0"/>0(失效)1(完成)2(正在运行)3(运行失败)<br/>
<label>下次运行时间</label>
<input id="nextexecutetime" type="text" value="${nextexecute}"/>批次下次运行时间<br/>
<a target="_blank" href="http://www.quartz-scheduler.org/documentation/quartz-2.1.x/tutorials/tutorial-lesson-06.html">表达式参考</a>
<br/>
<label>调度 cron 表达式</label>
<input id="cronexpression" type="text" value="0 0 8 * * ?"/>批次下次运行时间<br/>
<label>调度解释</label>
<input id="cronexplain" type="text" value="每天早上八点运行"/>批次下次运行时间<br/>
<button onclick="addBatch()">增加批次</button>
<div id="content"></div>

</body>
</html>
