package com.ameron32.game.kingsburg.core;
import java.util.Random;

/**
 * 
 * RANDOM PROXY to allow for future re-implementation of Random handling.
 *
 */
public class BasicRandomizer implements Randomizer {
	
	static BasicRandomizer randomizer;
	
	public static BasicRandomizer get() {
		if (randomizer == null) {
			randomizer = new BasicRandomizer();
		}
		return randomizer;
	}
	
	Random rand = new Random();

	private BasicRandomizer() { super(); }
	
	@Override
	public int nextInt(int max) {
		return rand.nextInt(max);
	}
}
