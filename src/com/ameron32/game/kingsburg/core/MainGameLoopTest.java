package com.ameron32.game.kingsburg.core;
/* package whatever; // don't place package name! */

import com.ameron32.game.kingsburg.core.bot.BasicBotPlayerProxy;
import com.ameron32.game.kingsburg.core.local.LocalBoard;
import com.ameron32.game.kingsburg.core.local.LocalPlayerStuff;

import java.lang.*;


/* Name of the class has to be "Main" only if the class is public. */
public class MainGameLoopTest {

	static final int NUMBER_OF_PLAYERS = 5;
	static final int NUMBER_OF_ROUNDS = 5;
	static final int NUMBER_OF_PHASES = 8;

	public static void main(String[] args) throws java.lang.Exception {
		// start
		Game game = new Game();
		BasicBotPlayerProxy[] bots = new BasicBotPlayerProxy[NUMBER_OF_PLAYERS];
		for (int player = 0; player < NUMBER_OF_PLAYERS; player++) {
			BasicBotPlayerProxy proxy = new BasicBotPlayerProxy(player);
			proxy.setListener(game.getPlayerProxyListener()); // TODO separate listener from Game
			bots[player] = proxy;
		}
		game.setup(NUMBER_OF_PLAYERS, NUMBER_OF_PHASES, NUMBER_OF_ROUNDS,
				new LocalBoard(new LocalBoard.BoardListener() {

			int year, phase, stage, turn;
			@Override
			public void currentState(int year, int phase, int stage, int turn) {
				if (this.year == year && this.phase == phase && this.stage == stage && this.turn == turn) {
					return;
				}

				this.year = year;
				this.phase = phase;
				this.stage = stage;
				this.turn = turn;

				Printer.get().log("  [ " +String.join(", ", Integer.toString(year), Integer.toString(phase),
						Integer.toString(stage), Integer.toString(turn)) + " ]");
			}
		}));
		for (int player = 0; player < NUMBER_OF_PLAYERS; player++) {
			game.setPlayerProxy(player, bots[player]);
			game.setPlayerStuff(player, new LocalPlayerStuff(player));
		}
		// loop rounds
		game.start();
		// finish
		game.complete();
	}
}
