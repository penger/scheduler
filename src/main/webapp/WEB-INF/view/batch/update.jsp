<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<html>
<head>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">

 
	function updateBatch(){
		var urlPath=getParams();
		
		urlPath="./batch?op=u&"+encodeURI(urlPath)
		$.ajax({
			url : urlPath,
			data: '',
			dataType : "html",
			type : "GET",
			async : true, // 同步
			success : function(data) {
					if(data==1){
						alert("修改成功")
					}else{
						alert("修改失败")
					}
				}
		})
	}
	
	
	function getParams(){
		
		var item = {
				id:$("#id").val(),
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
<a href="./batch">list batch</a>
<br/>
<div id="message"></div>
<label>batch id</label>
<input id="id" type="text" value="${batch.id }"  readonly="readonly"/><br/>
<label>名称</label>
<input id="bname" type="text" value="${batch.bname }"/><br/>
<label>描述</label>
<input id="bdesc" type="text" value="${batch.bdesc }"/><br/>
<label>前置id</label>
<input id="beforeid" type="text" value="${batch.beforeid }"/><br/>
<label>后置id</label>
<input id="afterid" type="text" value="${batch.afterid }"/><br/>
<label>标志位</label>
<input id="flag" type="text" value="${batch.flag }"/><br/>
<label>下次执行时间:</label>
<input id="nextexecutetime" type="text" value="${batch.nextexecutetime }"/><br/>
<label>调度cron 表达式:</label>
<input id="cronexpression" type="text" value="${batch.cronexpression }"/><br/>
<label>调度解释:</label>
<input id="cronexplain" type="text" value="${batch.cronexplain }"/><br/>

<button onclick="updateBatch()">update batch</button>
<div id="content"></div>

</body>
</html>
