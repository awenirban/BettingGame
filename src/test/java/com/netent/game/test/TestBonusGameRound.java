package com.netent.game.test;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.netent.game.BonusGameRound;
import com.netent.game.BonusGameRound.Box;
import com.netent.game.Player;
import com.netent.game.constants.GameErrors;
import com.netent.game.enums.RoundType;
import com.netent.game.exceptions.GameRoundException;
import com.netent.util.RandomGenerator;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RandomGenerator.class)
public class TestBonusGameRound {

	BonusGameRound bonusGameRound;
	Player player;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		player = new Player();
		player.setWonBonusRound(true);
		bonusGameRound = new BonusGameRound(player, false);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIfPlayerGets5BoxesIfBonusRoundWon() {
		assertEquals("This should be a bonus round", RoundType.BONUS, bonusGameRound.getCurrentRoundMode());
		assertEquals("Box picking game not won even though bonus round was won", 5, bonusGameRound.getCountOfBoxes());
	}

	@Test
	public void testIfExactly4BoxesContain5CoinsEachInBonusRound() {
		int countOfBoxesHavingCoins = 0;
		Box[] boxes = bonusGameRound.getBoxes();
		for (Box box : boxes) {
			if (box.getCoins() > 0)
				countOfBoxesHavingCoins++;
		}
		assertEquals(4, countOfBoxesHavingCoins);
	}

	@Test
	public void testBonusGameEndingBoxEndsGameWhenPicked() throws GameRoundException {
		setBoxesContentForTest();
		player.setBonusBoxInput(3);
		bonusGameRound.play();
		assertEquals(3, bonusGameRound.getBoxPickingEndedAt());
	}

	@Test
	public void testUserCannotOpenMoreBoxAfterBonusGameHasEnded() throws GameRoundException {
		thrown.expect(GameRoundException.class);
		thrown.expectMessage(GameErrors.ERROR_BONUS_GAME_ENDED);

		setBoxesContentForTest();
		player.setBonusBoxInput(3);
		bonusGameRound.play();
		player.setBonusBoxInput(1);
		bonusGameRound.play();

	}

	@Test
	public void testBonusGameContinuesTillEndingBoxIsPicked() throws GameRoundException {
		setBoxesContentForTest();
		player.setBonusBoxInput(1);
		bonusGameRound.play();
		player.setBonusBoxInput(3);
		bonusGameRound.play();
		assertEquals(3, bonusGameRound.getBoxPickingEndedAt());
		assertEquals(2, bonusGameRound.getCountOfBoxesOpened());
	}

	@Test
	public void testUserCannotOpenAlreadyOpenedBox() throws GameRoundException {

		thrown.expect(GameRoundException.class);
		thrown.expectMessage(GameErrors.ERROR_CANNOT_OPEN_ALREADY_OPENED_BOX);

		setBoxesContentForTest();
		player.setBonusBoxInput(1);
		bonusGameRound.play();
		player.setBonusBoxInput(1);
		bonusGameRound.play();
	}

	@Test
	public void testBonusCoinsAddedToPlayerStakeWhenBonusGameEndingBoxGetsPicked() throws GameRoundException {
		int previousStake = player.getCurrentStake();
		testBonusGameContinuesTillEndingBoxIsPicked();

		assertEquals(5, player.getCurrentStake() - previousStake);
	}

	@Test
	public void testRandomGeneratorIsInvokedWhileAssigningValuesToBonusGameBoxes() {
		PowerMockito.mockStatic(RandomGenerator.class);
		Mockito.when(RandomGenerator.getNextInt()).thenReturn(2);
		new BonusGameRound(player, false);
		PowerMockito.verifyStatic(RandomGenerator.class);
		RandomGenerator.getNextInt();
	}

	private void setBoxesContentForTest() {
		bonusGameRound.setBoxes(new Box[] { bonusGameRound.new Box(5), bonusGameRound.new Box(5),
				bonusGameRound.new Box(0), bonusGameRound.new Box(5), bonusGameRound.new Box(5) });
	}

}
