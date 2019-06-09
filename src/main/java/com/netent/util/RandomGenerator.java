/**
 * This is a framework class to generate a random number
 */
package com.netent.util;

import java.util.Random;

public class RandomGenerator {

	private static Random rand = new Random();

	public static float getNextFloat() {
		return rand.nextFloat();
	}

	public static int getNextInt() {
		return Math.abs(rand.nextInt());

	}

}
