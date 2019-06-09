package com.netent.game.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.netent.game.Game;
import com.netent.game.Player;
import com.netent.game.enums.RoundType;
import com.netent.game.enums.TestCheatModeType;
import com.netent.game.exceptions.GameRoundException;
import com.netent.game.exceptions.InsufficientFundsException;

public class TestGame {
	Player player;
	Game game;

	final static Logger log = Logger.getLogger(TestGame.class);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		player = new Player();
		game = new Game(player);
	}

	@After
	public void tearDown() throws Exception {
	}

	protected void playNumberOfTimes(int numTimes) throws InsufficientFundsException, GameRoundException {
		for (int i = 0; i < numTimes; i++)
			game.playRound();
	}

	@Test
	public void testPlayerStartsGameWith10kCoins() {

		assertEquals(10000, player.getInitialStake());
	}

	@Test
	public void testGameCounterUpdatesWithEachRound() throws Exception {
		playNumberOfTimes(2);
		assertEquals(2, game.getPlayedRoundCountForPlayer());
	}

	@Test
	public void testPlayerBets10CoinsForNormalRound() throws Exception {

		playNumberOfTimes(1);
		assertEquals("NORMAL", game.getActiveGameRound().getCurrentRoundMode().toString());
		assertEquals(10, game.getActiveGameRound().getCurrentBet());
	}

	@Test
	public void testPlayerGetsSomeResultAfterOneRound() throws Exception {
		playNumberOfTimes(1);

		assertNotNull("No gameRound result was returned", game.getActiveGameRound().getResult());
		assertNotNull("Coins cannot be null in a result", game.getActiveGameRound().getResult().getWinCoins());
		assertNotNull("Free rounds cannot be null in a result",
				game.getActiveGameRound().getResult().getNextRoundFree());
	}

	@Test
	public void testErrorIsThrownWhenPlayerHasInsufficientStakeToPlay()
			throws InsufficientFundsException, GameRoundException {
		thrown.expect(InsufficientFundsException.class);
		thrown.expectMessage("Insufficient stake to play");

		player.decrementCurrentStake(player.getCurrentStake() - 1);
		playNumberOfTimes(1);

	}

	@Test
	public void testPlayerGetsCoinsWhenChanceIsMoreThan30PC_TestCheatModeAsCoins() throws Exception {
		game.setTestCheatModeType(TestCheatModeType.COINS);
		playNumberOfTimes(1);
		assertEquals("Expected coin count does not match", 20, game.getActiveGameRound().getResult().getWinCoins());
	}

	@Test
	public void testCurrentStakeCannotBeLesserThanPreviousStakeForNormalRoundAndCoinsWin_TestCheatModeAsCoins()
			throws Exception {
		int playerPreviousStake = player.getCurrentStake();
		game.setTestCheatModeType(TestCheatModeType.COINS);
		playNumberOfTimes(1);
		log.debug("prev: " + playerPreviousStake + " curr: " + player.getCurrentStake() + "\n");
		// coins - yes : round - no
		assertTrue("Coin win count should be more than 0", game.getActiveGameRound().getResult().getWinCoins() > 0);
		assertTrue("No free round should be won", !game.getActiveGameRound().getResult().getNextRoundFree());
		assertTrue("Current stake cannot be lesser than previous stake for normal round and coins win",
				player.getCurrentStake() > playerPreviousStake);
	}

	@Test
	public void testPlayerGetsFreeRoundWhenChanceIsMoreThan10PC_TestCheatModeAsFreeRound() throws Exception {
		game.setTestCheatModeType(TestCheatModeType.FREE_ROUND);
		playNumberOfTimes(1);
		assertTrue("Player did not get a free round", player.getNextRoundFree());
	}

	@Test
	public void testPlayerIsAbleToUseFreeRoundWithZeroBet_TestCheatModeAsFreeRound() throws Exception {
		player.setWonFreeRound(true);

		int playerPreviousStake = player.getCurrentStake();
		playNumberOfTimes(1);
		assertEquals("Bet placed in a free round should be 0", 0, game.getActiveGameRound().getCurrentBet());
		assertEquals("This should have ben a free round", game.getActiveGameRound().getCurrentRoundMode(),
				RoundType.FREE);

		// assertEquals("Num of played round does not match", 2,
		// game.getPlayedRoundCountForPlayer());
		assertTrue("Current stake should be more than initial stake", player.getCurrentStake() >= playerPreviousStake);
		assertNotNull("Coins cannot be null in a result", game.getActiveGameRound().getResult().getWinCoins());
		assertNotNull("Free rounds cannot be null in a result",
				game.getActiveGameRound().getResult().getNextRoundFree());
	}

	@Test
	public void testCurrentStakeCannotBeDifferentThanPreviousStakeForFreeRoundAndNoCoinsWin_TestCheatModeAsFreeRound()
			throws Exception {
		player.setWonFreeRound(true);

		game.setTestCheatModeType(TestCheatModeType.FREE_ROUND);
		int playerPreviousStake = player.getCurrentStake();
		playNumberOfTimes(1);
		log.debug("prev: " + playerPreviousStake + " curr: " + player.getCurrentStake() + "\n");
		// coins - no : round - yes
		assertEquals("This should have ben a free round", game.getActiveGameRound().getCurrentRoundMode(),
				RoundType.FREE);
		assertTrue("Coin win count should be 0", game.getActiveGameRound().getResult().getWinCoins() == 0);
		assertTrue("Current stake cannot be different than previous stake for free round and no coins win",
				player.getCurrentStake() == playerPreviousStake);

	}

	@Test
	public void testCurrentStakeCannotBeGreaterThanPreviousStakeForNormalRoundAndNoCoinsWin_TestCheatModeAsNoCoinWinAndNoFreeRound()
			throws Exception {
		int playerPreviousStake = player.getCurrentStake();
		game.setTestCheatModeType(TestCheatModeType.NO_COINS_NO_FREE_ROUND);
		game.playRound();
		log.debug("prev: " + playerPreviousStake + " curr: " + player.getCurrentStake() + "\n");
		// coins - no : round - no
		assertTrue("Coin win count should be 0", game.getActiveGameRound().getResult().getWinCoins() == 0);
		assertTrue("No free round should be won", !game.getActiveGameRound().getResult().getNextRoundFree());
		assertTrue("Current stake cannot be greater than previous stake for normal round and no coins win",
				player.getCurrentStake() < playerPreviousStake);
	}

	@Test
	public void testCurrentStakeCannotBeLesserThanPreviousStakeForFreeRoundAndCoinsWin_TestCheatModeAsBothCoinWinAndFreeRound()
			throws Exception {
		int playerPreviousStake = player.getCurrentStake();
		game.setTestCheatModeType(TestCheatModeType.BOTH_COINS_AND_FREE_ROUND);
		game.playRound();
		log.debug("prev: " + playerPreviousStake + " curr: " + player.getCurrentStake() + "\n");
		// coins - yes : round - yes
		assertTrue("Coin win count should be more than 0", game.getActiveGameRound().getResult().getWinCoins() > 0);
		assertTrue("Free round should be won", game.getActiveGameRound().getResult().getNextRoundFree());
		assertTrue("Current stake cannot be lesser than previous stake for free round and coins win",
				player.getCurrentStake() > playerPreviousStake);

	}

	@Test
	public void testPlayerIsAbleToPlayEvenWithInsufficientFundsInAFreeRound() throws Exception {
		player.setWonFreeRound(true);
		player.decrementCurrentStake(player.getCurrentStake() - 1);

		int playerPreviousStake = player.getCurrentStake();
		playNumberOfTimes(1);
		log.debug("prev: " + playerPreviousStake + " curr: " + player.getCurrentStake() + "\n");

		assertEquals("This should have ben a free round", game.getActiveGameRound().getCurrentRoundMode(),
				RoundType.FREE);
	}
	
	@Test
	public void testMixedModeRunOfAGameGivesResult() throws InsufficientFundsException, GameRoundException {
		int previousStake = player.getCurrentStake();
		
		game.setTestCheatModeType(TestCheatModeType.COINS);
		game.playRound();

		game.setTestCheatModeType(TestCheatModeType.FREE_ROUND);
		game.playRound();
		
		game.setTestCheatModeType(TestCheatModeType.POSITIVE_BONUS_ROUND);
		game.playRound();
		
		player.setBonusBoxInput(4);
		game.playRound();
		assertTrue("Current stake should not be lesser than stake at the begining of round", player.getCurrentStake() >= previousStake );
	}

	@Test
	public void testRTPOnSimulationOf10000000RoundsIsNotNull() throws Exception {
		String returnToPlayer = game.getReturnToPlayer();
		assertNotNull("RTP is null", returnToPlayer );
		log.info(game.getPlayedRoundCountForPlayer());
		log.info("RTP: " + returnToPlayer);
	}

	@Test
	public void testBonusRoundIsNotWonWhenChanceIsLesserThanPresetValue_NegativeTestCheatModeAsBonusRound()
			throws Exception {
		player.setWonFreeRound(true);
		game.setTestCheatModeType(TestCheatModeType.NEGATIVE_BONUS_ROUND);
		playNumberOfTimes(1);
		assertFalse("Bonus round should NOT have been won", player.getBonusRoundWon());
	}

	@Test
	public void testBonusRoundIsWonWhenChanceIsGreaterThanPresetValue_PositiveTestCheatModeAsBonusRound()
			throws Exception {
		player.setWonFreeRound(true);
		game.setTestCheatModeType(TestCheatModeType.POSITIVE_BONUS_ROUND);
		playNumberOfTimes(1);
		assertTrue("Bonus round not won", player.getBonusRoundWon());
	}

	@Test
	public void testIfBonusRoundWonThenPlayedRoundWasFreeRound_TestCheatModeAsBonusRound() throws Exception {
		testBonusRoundIsWonWhenChanceIsGreaterThanPresetValue_PositiveTestCheatModeAsBonusRound();
		assertTrue("This round should have been a free round",
				game.getActiveGameRound().getCurrentRoundMode().equals(RoundType.FREE));
	}

	@Test
	public void testIfBonusRoundWonThenPlayedRoundWasNotANormalRound_TestCheatModeAsBonusRound() throws Exception {
		testBonusRoundIsWonWhenChanceIsGreaterThanPresetValue_PositiveTestCheatModeAsBonusRound();
		assertFalse("This round should NOT have been a normal round",
				game.getActiveGameRound().getCurrentRoundMode().equals(RoundType.NORMAL));
	}

	@Test
	public void testPreviousRoundWasFreeRoundIfCurrentRoundIsBonusRound() throws Exception {
		testBonusRoundIsWonWhenChanceIsGreaterThanPresetValue_PositiveTestCheatModeAsBonusRound();

		player.setBonusBoxInput(1);
		playNumberOfTimes(1);
		assertTrue("Previous round should have been a free round",
				game.getPreviousGameRound().getCurrentRoundMode().equals(RoundType.FREE));
		assertTrue("Current round should have been a bonus round",
				game.getActiveGameRound().getCurrentRoundMode().equals(RoundType.BONUS));
	}

}
