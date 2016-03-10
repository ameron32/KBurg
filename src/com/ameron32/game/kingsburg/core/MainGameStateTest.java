package com.ameron32.game.kingsburg.core;

public class MainGameStateTest {

	static final int NUMBER_OF_PLAYERS = 5;
	static final int NUMBER_OF_ROUNDS = 5;
	static final int NUMBER_OF_PHASES = 8;

	public static void main(String[] args) throws java.lang.Exception {
		// start
		Game game = new Game();
		BasicBotPlayerProxy proxy = new BasicBotPlayerProxy();
		proxy.setListener(game); // TODO separate listener from Game
		game.setup(NUMBER_OF_PLAYERS, NUMBER_OF_PHASES, NUMBER_OF_ROUNDS);
		game.setBoard(new LocalBoard());
		for (int player = 0; player < NUMBER_OF_PLAYERS; player++) {
			game.setPlayerProxy(player, proxy);
			game.setPlayerStuff(player, new LocalPlayerStuff(player));
		}
		// loop rounds
		game.start();
		// finish
		game.complete();
	}
}
