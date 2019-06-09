<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<title></title>
<script>
function calculate() {
  var elem = document.getElementById("myBar");   
  elem.style.width = ${returnToPlayer} + '%'; 
  
}
document.addEventListener('DOMContentLoaded', calculate, false);
</script>
</head>
<style>
#myProgress {
	width: 60%;
	background-color: #ddd;
	border-style: double;
}

#myBar {
	width: 1%;
	height: 30px;
	background-color: #4CAF50;
}
</style>
<body>
	<form action="getRTP" method="POST">

		<br>
		<div id="returns">
			<c:if test="${returnToPlayer != null}">
				Your chances of returns from this game is at : ${returnToPlayer}%
			</c:if>
		</div>
		<div id="myProgress">
			<div id="myBar"></div>
		</div>
		<input type="submit" id="getRTP" value="Re-assess!"
			style="height: 20px; width: 100px;"> <input type="button"
			value="Click here for Home page" style="height: 20px; width: 250px;"
			onclick="location.href='<%=request.getContextPath()%>/Game.html';">
	</form>
</body>
</html>