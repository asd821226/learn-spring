<%@ page language="java" contentType="text/html; charset=utf8"
	pageEncoding="utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>editUser</title>
<script>
function updateUser(form){
	form.action ="/user/" + document.getElementById("userId").value;
}
</script>
</head>
<body>
    <form method="post" onsubmit="updateUser(this)">
        userId:<input type="text" name="userId" id="userId"><br/>
        userName:<input type="text" name="userName"><br/>
        userAge:<input type="text" name="userAge"><br/>
        <input type="submit">
    </form>
</body>
</html>