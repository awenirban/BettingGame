/**
 * This class represents a bonus round that consists of several box picking sub-game rounds
 */
package com.netent.game;

import com.netent.game.constants.GameConstants;
import com.netent.game.constants.GameErrors;
import com.netent.game.enums.RoundType;
import com.netent.game.exceptions.GameRoundException;
import com.netent.util.RandomGenerator;

public class BonusGameRound extends GameRound {

	/* Inner class representing a box that contains coins or nothing */
	public class Box {

		private int coins = 0;
		private boolean isOpened = false;

		public Box(int coins) {
			this.coins = coins;
		}

		public int getCoins() {
			return this.coins;
		}

		public void setOpened(boolean choice) {
			this.isOpened = choice;
		}

		public boolean isOpened() {
			return isOpened;
		}

	}

	private int countOfBoxesInBonusRound = 0;
	private Box[] boxes;
	private int countOfBoxesOpened = 0;
	private int coinsWonFromBoxOpening;
	private boolean userHasOpenedGameEndBox = false;
	private int indexOfGameEndBox;

	public BonusGameRound(Player player, boolean simulationModeOn) {
		super(player, simulationModeOn);
		this.countOfBoxesInBonusRound = RoundType.BONUS.getValue();
		initializeRandomValuesToBoxes();
	}

	private void initializeRandomValuesToBoxes() {
		boxes = new Box[countOfBoxesInBonusRound];
		int indexOfrandomBoxToBeFilledWithNoCoins = RandomGenerator.getNextInt() % countOfBoxesInBonusRound;
		boxes[indexOfrandomBoxToBeFilledWithNoCoins] = new Box(0);
		for (int i = 0; i < countOfBoxesInBonusRound; i++) {
			if (i != indexOfrandomBoxToBeFilledWithNoCoins)
				boxes[i] = new Box(GameConstants.FREE_COINS_IN_BONUS_GAME_BOX);
		}
	}

	@Override
	public void play() throws GameRoundException {
		if (super.getSimulationModeOn()) {
			super.getCurrentPlayer().setBonusBoxInput((RandomGenerator.getNextInt() % countOfBoxesInBonusRound) + 1);
		}

		if (!hasUserOpenedGameEndBox()) {
			openBox(super.getCurrentPlayer().getBonusBoxInput());
		} else {
			throw new GameRoundException(GameErrors.ERROR_BONUS_GAME_ENDED);
		}

	}

	public boolean hasUserOpenedGameEndBox() {
		return this.userHasOpenedGameEndBox;
	}

	private void openBox(int bonusBoxInput) throws GameRoundException {
		Box box = boxes[bonusBoxInput - 1];
		if (!box.isOpened()) {
			box.setOpened(true);

			this.countOfBoxesOpened++;
			if (box.getCoins() == 0) {
				// This is the ending game box
				this.userHasOpenedGameEndBox = true;
				this.indexOfGameEndBox = bonusBoxInput - 1;
				super.getCurrentPlayer().setWonBonusRound(false);
			} else {
				this.coinsWonFromBoxOpening += box.getCoins();
				super.getResult().setWinCoins(box.getCoins());
				super.getCurrentPlayer().incrementCurrentStake(coinsWonFromBoxOpening);
			}
		} else {
			throw new GameRoundException(GameErrors.ERROR_CANNOT_OPEN_ALREADY_OPENED_BOX);
		}
	}

	public int getCountOfBoxes() {
		return this.countOfBoxesInBonusRound;
	}

	public Box[] getBoxes() {
		return this.boxes;
	}

	public int getBoxPickingEndedAt() {
		return this.indexOfGameEndBox + 1;
	}

	public void setBoxes(Box[] boxes) {
		this.boxes = boxes;
	}

	public int getCountOfBoxesOpened() {
		return this.countOfBoxesOpened;
	}

}
