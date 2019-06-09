package com.netent.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.netent.game.BonusGameRound;
import com.netent.game.Game;
import com.netent.game.GameRoundResult;
import com.netent.game.Player;
import com.netent.game.exceptions.GameRoundException;
import com.netent.game.exceptions.InsufficientFundsException;
import com.netent.web.constants.GameWebConstants;

/**
 * Servlet implementation class PlayGameServlet
 */
@WebServlet(asyncSupported = true, urlPatterns = { GameWebConstants.URI_PLAY_NORMAL_ROUND,
		GameWebConstants.URI_PLAY_BONUS_ROUND, GameWebConstants.URI_GET_RTP,
		GameWebConstants.URI_SHOW_BONUS_ROUND_PAGE })
public class PlayGameServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	final static Logger log = Logger.getLogger(PlayGameServlet.class);

	/**
	 * Default constructor.
	 */
	public PlayGameServlet() {
		super();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("Inside doPost method");
		String requestedBy = request.getServletPath();
		Player player;
		Game game = null;
		GameRoundResult result = null;
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/pages/Error.jsp");
		HttpSession session = request.getSession();
		if (session.getAttribute("currentPlayer") == null) {
			log.debug("New player");
			player = new Player();
			session.setAttribute("currentPlayer", player);
		} else {
			log.debug("Existing player");
			player = (Player) session.getAttribute("currentPlayer");
		}

		if (session.getAttribute("currentGame") == null) {
			log.debug("New game");
			game = new Game(player);
			session.setAttribute("currentGame", game);
		} else {
			log.debug("Existing gameRound");
			game = (Game) session.getAttribute("currentGame");
		}
		if (requestedBy.equals(GameWebConstants.URI_PLAY_NORMAL_ROUND)) {

			try {
				game.playRound();
			} catch (InsufficientFundsException e) {
				log.error(e.getMessage());
			}
			result = game.getActiveGameRound().getResult();
			rd = request.getRequestDispatcher("/WEB-INF/pages/result.jsp");
		} else if (requestedBy.equals(GameWebConstants.URI_PLAY_BONUS_ROUND)) {
			if (player.getBonusRoundWon()) {
				String userChosenBox = request.getParameter("bonusBoxes");
				player.setBonusBoxInput(Integer.parseInt(userChosenBox));
				try {
					game.playRound();
				} catch (InsufficientFundsException e) {
					log.error(e.getMessage());
				}
				result = game.getActiveGameRound().getResult();
				if (((BonusGameRound) game.getActiveGameRound()).hasUserOpenedGameEndBox()) {
					request.setAttribute("bonusRoundEnded", true);
					player.setWonBonusRound(false);
				} else {
					request.setAttribute("bonusRoundEnded", false);
				}
				request.setAttribute("openedBox", userChosenBox);
			} else {
				request.setAttribute("noMoreActiveBonusRound", true);
			}
			rd = request.getRequestDispatcher("/WEB-INF/pages/bonusRoundInput.jsp");
		} else if (requestedBy.equals(GameWebConstants.URI_GET_RTP)) {
			Player simulationPlayer = new Player();
			Game simulationGame = new Game(simulationPlayer);
			try {
				request.setAttribute("returnToPlayer", simulationGame.getReturnToPlayer());
				rd = request.getRequestDispatcher("/WEB-INF/pages/returnToPlayer.jsp");
			} catch (InsufficientFundsException e) {
				log.error(e.getMessage());
			} catch (GameRoundException e) {
				log.error(e.getMessage());
			} finally {
				simulationGame = null;
				simulationPlayer = null;
			}
		}
		if (result != null) {
			request.setAttribute("winCoins", result.getWinCoins());
			request.setAttribute("freeRound", result.getNextRoundFree());
			request.setAttribute("bonusRound", result.getWonBonusRound());
		}
		session.setAttribute("currentPlayer", player);
		rd.forward(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("Inside doGet method");
		String requestedBy = request.getServletPath();
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/pages/Error.jsp");
		HttpSession session = request.getSession();
		Player player;
		if (session.getAttribute("currentPlayer") == null) {
			log.debug("New player");
			player = new Player();
			session.setAttribute("currentPlayer", player);
		} else {
			log.debug("Existing player");
			player = (Player) session.getAttribute("currentPlayer");
		}
		if (requestedBy.equals(GameWebConstants.URI_SHOW_BONUS_ROUND_PAGE)) {
			player.setWonBonusRound(true);
			rd = request.getRequestDispatcher("/WEB-INF/pages/bonusRoundInput.jsp");
		} else if (requestedBy.equals(GameWebConstants.URI_PLAY_NORMAL_ROUND)) {
			rd = request.getRequestDispatcher("/WEB-INF/pages/result.jsp");
		}
		session.setAttribute("currentPlayer", player);
		rd.forward(request, response);
	}

}
