/**
 * ENUM to contain various values possible for a type of gameRound round
 */
package com.netent.game.enums;

public enum RoundType {

	NORMAL(10), FREE(0), BONUS(5);

	private int betValue;

	private RoundType(int value) {
		this.betValue = value;
	}

	public int getValue() {
		return betValue;
	}

}
