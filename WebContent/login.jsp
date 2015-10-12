<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
    <h3>${msg }</h3>  
    <form action="/SpringShiro/shiro/login.do" method="post">  
        <br />用户帐号： <input type="text" name="username" id="username" value="" />  
        <br />登录密码： <input type="password" name="password" id="password"  value="" /> <br />  
        <input value="登录" type="submit">  
    </form>  
</body>
</html>