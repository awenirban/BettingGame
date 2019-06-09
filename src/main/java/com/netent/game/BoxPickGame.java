/**
 * This class represents a box picking game that is playable when a player wins a bonus round. 
 */
package com.netent.game;

import com.netent.game.exceptions.InsufficientFundsException;

public class BoxPickGame extends Game {

	public BoxPickGame(Player player) {
		super(player);
	}

	@Override
	public void playRound() throws InsufficientFundsException {
		this.activeGameRound = new BonusGameRound(player, false);
		
	}

}
