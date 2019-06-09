/**
 * This class represents a typical result from a gameRound
 */
package com.netent.game;

public class GameRoundResult {

	private int winCoins;
	private boolean gotNextRoundFree;
	private boolean wonBonusRound;

	public GameRoundResult() {
		winCoins = 0;
		gotNextRoundFree = false;
	}

	public int getWinCoins() {
		return winCoins;
	}

	public boolean getNextRoundFree() {
		return gotNextRoundFree;
	}

	public void setWinCoins(int winCoinsPerGame) {
		this.winCoins = winCoinsPerGame;
	}

	public void setWonFreeRound(boolean gotNextRoundFree) {
		this.gotNextRoundFree = gotNextRoundFree;
	}

	public void setWonBonusRound(boolean wonBonusRound) {
		this.wonBonusRound = wonBonusRound;
	}

	public boolean getWonBonusRound() {
		return this.wonBonusRound;
	}

}
