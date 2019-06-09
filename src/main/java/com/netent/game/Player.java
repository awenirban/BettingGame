/**
 * This class represents a player
 */
package com.netent.game;

import com.netent.game.constants.GameConstants;

public class Player {

	private int initialStake;

	private int currentStake;

	private boolean getNextRoundFree = false;

	private int totalBet;

	private boolean bonusRoundOn = false;

	private int bonuxBoxInput;

	public Player() {
		this.currentStake = this.initialStake = GameConstants.INITIAL_STAKE;
	}

	public int getInitialStake() {
		return this.initialStake;
	}

	public int getCurrentStake() {
		return this.currentStake;
	}

	public void decrementCurrentStake(int currentBet) {
		this.currentStake = this.currentStake - currentBet;
	}

	public void incrementCurrentStake(int winCoinsPerGame) {
		this.currentStake = this.currentStake + winCoinsPerGame;

	}

	public boolean getNextRoundFree() {
		return getNextRoundFree;
	}

	public void setWonFreeRound(boolean wonFreeRound) {
		this.getNextRoundFree = wonFreeRound;
	}

	public void updateTotalBet(int currentBet) {
		this.setTotalBet(this.getTotalBet() + currentBet);
	}

	public int getTotalBet() {
		return totalBet;
	}

	public void setTotalBet(int totalBet) {
		this.totalBet = totalBet;
	}

	public boolean getBonusRoundWon() {
		return this.bonusRoundOn;
	}

	public void setWonBonusRound(boolean bonusRoundWon) {
		this.bonusRoundOn = bonusRoundWon;
	}

	public void setBonusBoxInput(int input) {
		this.bonuxBoxInput = input;
	}

	public int getBonusBoxInput() {
		return this.bonuxBoxInput;
	}

}
