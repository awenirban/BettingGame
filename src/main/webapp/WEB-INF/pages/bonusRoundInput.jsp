<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<title></title>
</head>
<body>
	<form action="playBonusRound" method="POST">
		<c:if test="${bonusRoundEnded != true && noMoreActiveBonusRound != true }">
		Please select the box you wish to open! <select id="bonusBoxes"
				name="bonusBoxes">
				<option id="1" value="1" label="1" selected="selected"></option>
				<c:forEach var="i" begin="2" end="5">
					<option id="<c:out value = "${i}"/>"
						value="<c:out value = "${i}"/>" label="<c:out value = "${i}"/>"></option>
				</c:forEach>
			</select>
			<br>
			<div id="returns"></div>

			<input type="submit" id="getRTP" value="Click to open"
				style="height: 20px; width: 100px;">
			<c:if test="${winCoins > 0}">
				<p>You have won ${winCoins} coins! Play on to win more!!</p>
				<p>Your current stake is at ${currentStake } coins</p>
			</c:if>
		</c:if>
		<c:if test="${bonusRoundEnded == true }">
			<p>You have opened the game ending box!!</p>
			<input type="button" value="Click here to return to normal game"
				style="height: 20px; width: 300px;"
				onclick="location.href='<%=request.getContextPath()%>/playNormalRound';">
		</c:if>
		<c:if test="${noMoreActiveBonusRound == true }">
			<p>Sorry! You do not have any more bonus round to play!!</p>
			<input type="button" value="Click here to return to normal game"
				style="height: 20px; width: 300px;"
				onclick="location.href='<%=request.getContextPath()%>/playNormalRound';">
		</c:if>

	</form>
</body>
</html>