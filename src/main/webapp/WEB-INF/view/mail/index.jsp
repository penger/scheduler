<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">
function send(){
	var urlPath=getParams();
	urlPath="./email?op=send&"+urlPath
	//urlPath="./email?op=send&"+encodeURI(urlPath)
	//window.location.href = "${basePath}/export/downfile?"+encodeURI("tempFilePath="+x.data+"&filename="+filename);
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
			host:$("#host").val(),
			from:$("#from").val(),
			username:$("#username").val(),
			password:$("#password").val(),
			to:$("#to").val(),
			cc:$("#cc").val(),
			subject:$("#subject").val(),
			content:$("#content").val(),
	}
	var params =$.param(item)
	return params
}

</script>
</head>
<body>
	<h4>测试发邮件</h4>
	<label>邮件服务器</label>:
	<input id="host" type="text" value="mail.bl.com"/><br/>
	<label>发件人</label>:
	<input id="from" type="email" value="Peng.Gong@bl.com"/><br/>
	<label>认证用户名</label>:
	<input id="username" type="text" value="GP39"/><br/>
	<label>认证密码</label>:
	<input id="password" type="password" value="syj125?"/><br/>
	<label>收件人</label>:
	<input id="to" type="email" value="gongpengllpp@sina.com"/><br/>
	<label>抄送</label>:
	<input id="cc" type="text" value="Peng.Gong@bl.com"/><br/>
	<label>主题</label>:
	<input id="subject" type="text"/><br/>
	<label>内容</label>:
	<textarea id="content"></textarea>
	
	<button onclick="send()">发送邮件</button>
</body>
</html>
