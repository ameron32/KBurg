package com.ameron32.game.kingsburg.core;
import sun.rmi.runtime.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 
 * RANDOM PROXY to allow for future re-implementation of Random handling.
 *
 */
public class BasicRandomizer implements Randomizer {

	static HashMap<String, Integer> cachedRandoms;
	
	static BasicRandomizer randomizer;
	
	public static BasicRandomizer get() {
		if (randomizer == null) {
			randomizer = new BasicRandomizer();
		}
		return randomizer;
	}
	
	Random rand = new Random();

	private BasicRandomizer() {
		super();
		cachedRandoms = new HashMap<>();
	}
	
	@Override
	public int nextInt(String requestId, int max) {
		// generate a random value incase needed
		int random = rand.nextInt(max);
		// pull the cached value, if it exists,
		// otherwise, default to the new random value
		int value = cachedRandoms.getOrDefault(requestId, random);
		Printer.get().log("          {"+requestId+"}    random: " + value);
		cachedRandoms.put(requestId, value);
		return value;
	}
}
