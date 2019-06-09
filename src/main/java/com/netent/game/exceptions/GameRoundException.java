/**
 * This represents a general exception scenario returned from a game round
 */
package com.netent.game.exceptions;

public class GameRoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public GameRoundException(String message) {
		super(message);
	}

}
