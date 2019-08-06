<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<c:url value="/styles/main.css"/>"  type="text/css" rel="stylesheet" />
<title>配置参数管理</title>
</head>
<body>
    <div class="main">
        <h2 class="title"><span>配置参数管理</span></h2>
        <form action="deleteUsers" method="post">
        <table border="1" width="100%" class="tab">
            <tr>
                <th><input type="checkbox" id="chkAll"></th>
                <th>配置名称</th>
                <th>配饰详情说明</th>
                <th>配置参数</th>
                <th>配置类型</th>
                <th>操作</th>
            </tr>
            <c:forEach var="entity" items="${paramconfigList}">
                <tr>      
                    <th><input type="checkbox" name="config_name" value="${entity.value.config_name}"></th>
                    <td>${entity.value.config_name}</td>
                    <td>${entity.value.config_desc}</td>
                    <td>${entity.value.config_value}</td>
                    <td>${entity.value.config_index}</td>
                    <td>
                    <a href="edit/${entity.value.config_name}" class="abtn">编辑</a>
                    <a href="deleteParamConfigByConfigName/${entity.value.config_name}" class="abtn">删除</a>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <div id="pager"></div>
        <p>
            <a href="add" class="abtn out">添加</a>
            <input type="submit"  value="批量删除" class="btn out" onclick="return submitForm();"/>
        </p>
        <p style="color: red">${message}</p>
        
     
    </form>
    
    </div>
</body>
</html>