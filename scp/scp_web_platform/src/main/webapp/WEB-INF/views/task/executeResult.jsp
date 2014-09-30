<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="shortcut icon" href="${ctx}/static/images/favicon.png">
<title>Execute result</title>
<link href="${ctx}/static/css/bootstrap.css" rel="stylesheet">
<link href="${ctx}/static/css/navbar.css" rel="stylesheet">
<link href="${ctx}/static/css/jquery-ui.css" rel="stylesheet">
</head>
<body>
	<div class="container">
		<div class="navbar navbar-default">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">SCP Web Management</a>
			</div>
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav navbar-right">
					<li class="active"><a href="#"><img
							src="${ctx}/static/images/setting.png" />Change Pwd</a></li>
					<li><a href="#"><img
							src="${ctx}/static/images/help.png" />Help</a></li>
					<li><a href="<%=request.getContextPath()%>/logout"><img
							src="${ctx}/static/images/logout.png" />Exit</a></li>
				</ul>
				<ul class="nav navbar-nav">
					<li class="active"><a href="${ctx}/task/tasklist">Task
							Manage</a></li>
					<li><a href="${ctx}/algorithm/algorithmlist">Algorithm
							Manage</a></li>
					<li><a href="${ctx}/source/sourcelist">Source
							Manage</a></li>
					<li><a href="${ctx}/inter/interlist">Interface
							Manage</a></li>
					<li><a href="${ctx}/config/configlist">Config
							Manage</a></li>
					<li><a href="${ctx}/user/userlist">User Manager</a></li>
				</ul>
			</div>
			<!--/.nav-collapse -->
		</div>
	</div>
	<div class="container">
		<div class="row">
			<div class="col-md-2">
				<div class="panel panel-default" style="min-height: 400px;">
					<div class="panel-body">
						<ul class="nav nav-pills nav-stacked">
							<li><a href="${ctx}/task/tasklist">Task List</a></li>
							<li><a href="${ctx}/task/mininglist">Mining List</a></li>
							<li class="active"><a href="${ctx}/task/executeresult">Execute Result</a></li>
							<li><a href="${ctx}/task/miningresult">Mining Result</a></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="col-md-10">
				<div class="panel panel-default" id="content-panel">
					<div class="panel-heading">
						<div class="alert alert-info" style="margin-bottom:0px;">Execute Result</div>
					</div>
					<div class="panel-body">
						<form:form action="${ctx}/task/executeresult" method="get" modelAttribute="task" role="form" id="taskForm">
							<div class="form-group">
								<label class="col-sm-2 control-label" style="text-align:right;">Task Name：</label>
								<div class="col-sm-7">
									<form:input path="taskName" id="taskName" class="form-control" value="${taskName}"/>
								</div>
								<div class="col-sm-3">
									<button type="submit" class="btn btn-default">Search</button>
								</div>
							</div>
						</form:form>
					</div>
					<c:if test="${not empty tasks}">
					<table id="taskTable" class="table table-bordered">
						<tr>
							<th>TaskName</th>
							<th>StartTime</th>
							<th>TriggerType</th>
							<th>Status</th>
							<th>Operation</th>
						</tr>
						<c:forEach items="${tasks}" var="task">
							<tr class="active">
								<td><input type="hidden" value="${task.rowKey}">${task.taskName}</td>
								<td><fmt:formatDate value="${task.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
								<td>${task.triggerType}</td>
								<td>
									<c:choose>
										<c:when test="${task.status eq 0}">
											ready
										</c:when>
										<c:when test="${task.status eq 1}">
											running
										</c:when>
										<c:when test="${task.status eq 2}">
											interrupt
										</c:when>
										<c:when test="${task.status eq 3}">
											finished
										</c:when>
									</c:choose>
								</td>
								<td>
								    <c:if test="${task.status eq 3}">
								    <button type="button" class="btn btn-info" onclick="viewtaskresult(this)">view</button>
									<button type="button" class="btn btn-info" onclick="runTask(this)">download</button>
									</c:if>
								</td>
							</tr>
						</c:forEach>
					</table>
					</c:if>
				</div>
			</div>
		</div>
	</div>
	<div id="dialogDiv" class="container">
		<table class="table table-bordered"></table>
	</div>
	<script src="${ctx}/static/js/jquery-1.10.2.min.js"></script>
	<script src="${ctx}/static/js/jquery-ui.js"></script>
	<script src="${ctx}/static/js/bootstrap.min.js"></script>
	<script type="text/javascript">		
		$(document).ready(function() {
			$("#dialogDiv").dialog({
				autoOpen : false,
				height : 380,
				width : 800,
				modal : true,
				title : 'Add Action',
				buttons : {
					"OK" : function() {
						$(this).dialog("close");
					}
				}
			});
		});
		
		function viewtaskresult(k) {
			var rowKey = $(k).parent().parent().find("input[type='hidden']")
					.val();
			if (rowKey == undefined) {
				return false;
			}
			$.ajax({
				type : 'post',
				url : '${ctx}/task/viewtaskresult',
				dataType : 'json',
				data : {
					"rowKey" : rowKey
				},
				success : function(data) {
					var e = eval(data);
					if (e.msg == "success") {
						var html = "";
						var columns = e.columns;
						if (columns != undefined && columns != null) {
							html = html + "<tr class='success'>";
							$.each(columns, function(i, item) {
								html = html + "<th>" + item + "</th>";
							});
							html = html + "</tr>";
							var list = e.list;
							if (list != undefined && list != null) {
								$.each(list, function(i, items) {
									if (items != undefined || items != null) {
										html = html + "<tr>";
										$.each(items, function(i, item) {
											html = html + "<td>" + item
													+ "</td>";
										});
										html = html + "</tr>";
									}
								});
							}
						}
						$("#dialogDiv").find("table:eq(0)").html(html);
						$("#dialogDiv").dialog("open");
					} else if (e.msg == "failure") {
						alert("retrieve data failure!!!");
					} else {
						alert(e.msg);
					}
				}
			});
		}

		function runTask(k) {
			var rowKey = $(k).parent().parent().find("input[type='hidden']")
					.val();
			if (rowKey == undefined) {
				return false;
			}
// 			if (confirm("are you ensure download these datas")) {

// 			}
			document.location.href = "${ctx}/download/"+rowKey;
		}
	</script>
</body>
</html>