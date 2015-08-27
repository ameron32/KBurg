package com.ameron32.game.kingsburg.core;
/* package whatever; // don't place package name! */

import java.util.*;
import java.lang.*;
import java.io.*;



/* Name of the class has to be "Main" only if the class is public. */
public class MainGameLoopTest {

	static final int NUMBER_OF_PLAYERS = 5;
	static final int NUMBER_OF_ROUNDS = 5;
	static final int NUMBER_OF_PHASES = 8;

	public static void main(String[] args) throws java.lang.Exception {
		// start
		Game game = new Game();
		BasicBotPlayerProxy proxy = new BasicBotPlayerProxy();
		proxy.setListener(game); // TODO separate listener from Game
		game.setup(NUMBER_OF_PLAYERS, NUMBER_OF_PHASES, NUMBER_OF_ROUNDS);
		for (int player = 0; player < NUMBER_OF_PLAYERS; player++) {
			game.setPlayerProxy(player, proxy);
		}
		// loop rounds
		game.start();
		// finish
		game.complete();
	}
}
