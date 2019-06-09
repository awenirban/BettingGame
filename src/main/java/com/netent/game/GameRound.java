/**
 * This class represents an actual game round being played.
 */
package com.netent.game;

import org.apache.log4j.Logger;

import com.netent.game.constants.GameConstants;
import com.netent.game.enums.RoundType;
import com.netent.game.enums.TestCheatModeType;
import com.netent.game.exceptions.GameRoundException;
import com.netent.util.RandomGenerator;

public class GameRound {

	private Player player;
	protected RoundType currentRoundMode;
	private int currentBet;
	private GameRoundResult gameRoundResult;
	private TestCheatModeType testCheatModeType;
	private float randomChanceForCoinWin;
	private float randomChanceForFreeRoundWin;
	private float randomChanceForBonusRoundWin;
	private boolean simulationModeOn;

	final static Logger log = Logger.getLogger(GameRound.class);

	protected GameRound(Player player, boolean simulationModeOn) {
		this.player = player;
		this.simulationModeOn = simulationModeOn;
		if (player.getNextRoundFree()) {
			this.currentRoundMode = RoundType.FREE;
			this.player.setWonFreeRound(false);
		} else if (player.getBonusRoundWon()) {
			this.currentRoundMode = RoundType.BONUS;
			//this.player.setWonBonusRound(false);
		} else
			this.currentRoundMode = RoundType.NORMAL;
		this.gameRoundResult = new GameRoundResult();
	}

	public RoundType getCurrentRoundMode() {
		return this.currentRoundMode;
	}

	public void play() throws GameRoundException {
		// assumption of "infinite" number of coins in simulation mode is that the stake
		// never decrements and is hence always sufficient to continue the rounds. If
		// simulation mode is not on, normal decrement happens.
		if (!this.simulationModeOn)
			this.player.decrementCurrentStake(getCurrentBet());
		this.player.updateTotalBet(getCurrentBet());
		determineResultForCurrentRound();
	}

	private void determineResultForCurrentRound() {
		this.randomChanceForCoinWin = RandomGenerator.getNextFloat();
		this.randomChanceForFreeRoundWin = RandomGenerator.getNextFloat();
		this.randomChanceForBonusRoundWin = RandomGenerator.getNextFloat();
		switch (testCheatModeType) {

		case COINS:
			this.randomChanceForCoinWin = GameConstants.FREE_OR_NORMAL_COIN_WIN_CHANCE - 0.1f;
			this.randomChanceForFreeRoundWin = GameConstants.FREE_OR_NORMAL_FREE_ROUND_WIN_CHANCE + 0.1f;
			updateWinDetails();
			break;
		case FREE_ROUND:
			this.randomChanceForCoinWin = GameConstants.FREE_OR_NORMAL_COIN_WIN_CHANCE + 0.1f;
			this.randomChanceForFreeRoundWin = GameConstants.FREE_OR_NORMAL_FREE_ROUND_WIN_CHANCE - 0.1f;
			updateWinDetails();
			break;
		case BOTH_COINS_AND_FREE_ROUND:
			this.randomChanceForCoinWin = GameConstants.FREE_OR_NORMAL_COIN_WIN_CHANCE - 0.1f;
			this.randomChanceForFreeRoundWin = GameConstants.FREE_OR_NORMAL_FREE_ROUND_WIN_CHANCE - 0.1f;
			updateWinDetails();
			break;
		case NO_COINS_NO_FREE_ROUND:
			this.randomChanceForCoinWin = GameConstants.FREE_OR_NORMAL_COIN_WIN_CHANCE + 0.1f;
			this.randomChanceForFreeRoundWin = GameConstants.FREE_OR_NORMAL_FREE_ROUND_WIN_CHANCE + 0.1f;
			updateWinDetails();
			break;
		case POSITIVE_BONUS_ROUND:
			this.randomChanceForBonusRoundWin = GameConstants.FREE_BONUS_ROUND_WIN_CHANCE - 0.1f;
			updateWinDetails();
			break;
		case NEGATIVE_BONUS_ROUND:
			this.randomChanceForBonusRoundWin = GameConstants.FREE_BONUS_ROUND_WIN_CHANCE + 0.1f;
			updateWinDetails();
			break;
		default:
			updateWinDetails();
			break;
		}
		log.debug("Result from RoundType: " + this.currentRoundMode.toString() + " Coins won: "
				+ this.gameRoundResult.getWinCoins() + " Free round: " + this.gameRoundResult.getNextRoundFree()
				+ " Bonus round: " + this.gameRoundResult.getWonBonusRound() + " current stake: "
				+ this.player.getCurrentStake());
	}

	private void updateWinDetails() {
		if (this.randomChanceForCoinWin <= GameConstants.FREE_OR_NORMAL_COIN_WIN_CHANCE) {
			updateCoinsWin();
		}
		if (this.randomChanceForFreeRoundWin <= GameConstants.FREE_OR_NORMAL_FREE_ROUND_WIN_CHANCE) {
			updateFreeRoundWin();
		}
		if (this.currentRoundMode.equals(RoundType.FREE)
				&& this.randomChanceForBonusRoundWin <= GameConstants.FREE_BONUS_ROUND_WIN_CHANCE) {
			updateBonusRoundWin();
		}
	}

	private void updateBonusRoundWin() {
		gameRoundResult.setWonBonusRound(true);
		this.player.setWonBonusRound(true);

	}

	private void updateFreeRoundWin() {
		gameRoundResult.setWonFreeRound(true);
		this.player.setWonFreeRound(true);
	}

	private void updateCoinsWin() {
		gameRoundResult.setWinCoins(GameConstants.WIN_COINS_PER_GAME);
		this.player.incrementCurrentStake(GameConstants.WIN_COINS_PER_GAME);
	}

	public int getCurrentBet() {
		currentBet = currentRoundMode.getValue();
		return this.currentBet;
	}

	public GameRoundResult getResult() {
		return gameRoundResult;
	}

	public void setTestCheatModeType(TestCheatModeType testCheatModeType) {
		this.testCheatModeType = testCheatModeType;
	}

	public Player getCurrentPlayer() {
		return this.player;
	}

	public boolean getSimulationModeOn() {
		return this.simulationModeOn;
	}

}
