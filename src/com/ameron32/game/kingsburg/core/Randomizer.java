package com.ameron32.game.kingsburg.core;

public interface Randomizer {

	public static Randomizer get() {
		return BasicRandomizer.get();
	}

	/**
	 *
	 * @param requestId Should guarantee the same roll where requestId is called in the future. Should guarantee new roll where requestId is used for the first time.
	 * @param max Integer between 1 and X;
     * @return New random number where requestId is original OR same number as previously returned where requestId is a copy.
     */
	int nextInt(String requestId, int max);
}
