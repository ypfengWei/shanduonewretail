<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<form id = "upload" action="file/upload" method="post" enctype="multipart/form-data">
<input type="text" name="token" value="token"/><br>
<input type="file" name= "file"><br>
<input type="submit" value = "点击上传">
</form>
</body>
</html>