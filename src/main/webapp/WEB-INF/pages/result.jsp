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
	<form action="playNormalRound" method="POST">

		<c:choose>
			<c:when test="${winCoins > 0}">
				<p>Congratulations! You have won ${winCoins} coins!! Keep
					playing!!</p>
			</c:when>
			<c:when test="${freeRound == true}">
				<p>Congratulations! You have won a free round! Play again to
					make the most of it!
				<p>
			</c:when>
			<c:when test="${bonusRound == true}">
				<p>
					Congratulations! You have won a Bonus round! <a
						href="<%=request.getContextPath()%>/showBonusRoundInput">Click
						here</a> to pick your boxes!
				<p>
			</c:when>
			<c:otherwise>
				<p>Better luck next time! Play again to try your luck!</p>
			</c:otherwise>
		</c:choose>
		<br>
		<p>Your current stake is ${sessionScope.currentPlayer.getCurrentStake()} coins.</p>

		<c:choose>
			<c:when test="${sessionScope.currentPlayer.getCurrentStake() > 0}">
				<input type="submit" id="play" value="Play Again!" style="height: 20px; width: 100px;">
			</c:when>
		</c:choose>
	</form>
</body>
</html>