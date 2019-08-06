<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<link href="<c:url value="/styles/main.css" />" type="text/css" rel="stylesheet" />
<script language="javascript" type="text/javascript" 
    src="<c:url value="/scripts/My97DatePicker/WdatePicker.js" />"></script>
<title>编辑配置参数</title>
<base href="<%=basePath %>" />
</head>
<body>
    <div class="main">
        <h2 class="title"><span>编辑配置参数</span></h2>
        <form:form action="paramconfig/editSave" method="post" modelAttribute="paramconfig">
        <fieldset>
            <legend>配置参数</legend>
            <table cellpadding="5" cellspacing="8"> 
                <tr>
                    <td><label for="config_name">参数名称：</label></td>
                    <td><form:input path="config_name" size="40"/></td>
                    <td><form:errors path="config_name" cssClass="error"></form:errors></td>
                </tr>
                <tr>
                    <td><label for="config_desc">参数详情：</label></td>
                    <td><form:input path="config_desc" size="40"/></td>
                    <td><form:errors path="config_desc" cssClass="error"></form:errors></td>
                </tr>
                <tr>
                    <td><label for="config_value">参数值：</label></td>
                    <td><form:input path="config_value" size="40" class="Wdate" onClick="WdatePicker()"/></td>
                    <td><form:errors path="config_value" cssClass="error"></form:errors></td>
                </tr>
                <tr>
                    <td><label for="config_index">参数类型：</label></td>
                    <td><form:input path="config_index" size="40"/></td>
                    <td><form:errors path="config_index" cssClass="error"></form:errors></td>
                </tr>
            </table>
            <p>
                <input type="submit" value="保存" class="btn out">
            </p>
        </fieldset>
        <!--<form:errors path="*"></form:errors> -->
        </form:form>
        <p style="color: red">${message}</p>
        <p>
            <a href="<c:url value="/paramconfig/list" />"  class="abtn out">返回列表</a>
        </p>
    </div>
</body>
</html>