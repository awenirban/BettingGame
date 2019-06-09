/**
 * This class represents a game that the player has signed up for. A game is made up of 2 {@link GameRound} references, one for the current one and one for the previous one.
 */
package com.netent.game;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;

import com.netent.game.constants.GameErrors;
import com.netent.game.enums.RoundType;
import com.netent.game.enums.TestCheatModeType;
import com.netent.game.exceptions.GameRoundException;
import com.netent.game.exceptions.InsufficientFundsException;

public class Game {

	protected Player player;
	private int playedRoundCountForPlayer;
	protected GameRound activeGameRound;
	private GameRound previousGameRound;
	private TestCheatModeType testCheatModeType = TestCheatModeType.OFF;
	private boolean simulationModeOn = false;
	final static Logger log = Logger.getLogger(Game.class);

	public Game(Player player) {
		this.setPlayer(player);
	}

	public GameRound getActiveGameRound() {
		return activeGameRound;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void playRound() throws InsufficientFundsException {
		if (this.activeGameRound != null) {
			this.previousGameRound = this.activeGameRound;
		}
		if (player.getBonusRoundWon()) {
			if (this.previousGameRound != null && this.previousGameRound.getCurrentRoundMode().equals(RoundType.BONUS))
				this.activeGameRound = this.previousGameRound;
			else
				this.activeGameRound = new BonusGameRound(player, this.simulationModeOn);
		} else {
			this.activeGameRound = new GameRound(player, this.simulationModeOn);
		}
		if (this.activeGameRound.getCurrentRoundMode().equals(RoundType.BONUS)) {
			try {
				activeGameRound.play();
			} catch (GameRoundException e) {
				if (e.getMessage().equals(GameErrors.ERROR_CANNOT_OPEN_ALREADY_OPENED_BOX)) {
					// Do nothing, retry in next round

				}
			}
		} else {
			if (player.getCurrentStake() - this.activeGameRound.getCurrentRoundMode().getValue() >= 0) {
				activeGameRound.setTestCheatModeType(this.testCheatModeType);
				try {
					activeGameRound.play();
				} catch (GameRoundException e) {
					// Do nothing, retry in next round
				}
				this.playedRoundCountForPlayer++;

			} else {
				throw new InsufficientFundsException("Insufficient stake to play");
			}
		}
	}

	public int getPlayedRoundCountForPlayer() {
		return this.playedRoundCountForPlayer;
	}

	public String getReturnToPlayer() throws InsufficientFundsException, GameRoundException {
		DecimalFormat df = new DecimalFormat("###.##");
		float returnToPlayer = 0;
		this.simulationModeOn = true;
		for (int i = 0; i < 10000000; i++) {
			this.playRound();
		}
		returnToPlayer = (((float) this.player.getCurrentStake() - (float) this.player.getInitialStake()) * 100
				/ (float) this.player.getTotalBet());
		log.info("Initial stake: " + this.player.getInitialStake());
		log.info("Current stake: " + this.player.getCurrentStake());
		log.info("Total bet: " + this.player.getTotalBet());
		resetSimulationModeDetails();
		return df.format(returnToPlayer);
	}

	private void resetSimulationModeDetails() {
		this.simulationModeOn = false;
		this.player = new Player();
	}

	public void setTestCheatModeType(TestCheatModeType cheatModeType) {
		this.testCheatModeType = cheatModeType;

	}

	public void setSimulationModeOn(boolean simulationModeON) {
		this.simulationModeOn = simulationModeON;
	}

	public GameRound getPreviousGameRound() {
		return this.previousGameRound;
	}

}
