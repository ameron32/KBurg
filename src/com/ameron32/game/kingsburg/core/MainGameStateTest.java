package com.ameron32.game.kingsburg.core;

import com.ameron32.game.kingsburg.core.bot.BasicBotPlayerProxy;
import com.ameron32.game.kingsburg.core.bot.PlayerProxy;
import com.ameron32.game.kingsburg.core.bot.PlayerProxyListener;
import com.ameron32.game.kingsburg.core.local.LocalBoard;
import com.ameron32.game.kingsburg.core.local.LocalPlayerStuff;
import com.ameron32.game.kingsburg.core.state.Board;
import com.ameron32.game.kingsburg.core.state.PlayerStuff;

import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;

public class MainGameStateTest {

	static final int NUMBER_OF_PLAYERS = 5;
	static final int NUMBER_OF_ROUNDS = 5;
	static final int NUMBER_OF_PHASES = 8;

	public static void main(String[] args) throws java.lang.Exception {
//		pingpongMain(args);

		// start
		Game game = new Game();
		BasicBotPlayerProxy botProxy = new BasicBotPlayerProxy();
		TestUserProxy proxy = new TestUserProxy(botProxy);
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

	// TODO test segments in between player interactions

	private static void testSegment() {
		// start
		// retrieve state
		// determine current segment (parameter or state)
		// perform segment
		// at next player interaction point, store state
		// stop
	}







	public static class TestUserProxy implements PlayerProxy {

		private final PlayerProxy proxy;

		public TestUserProxy(PlayerProxy proxy) {this.proxy = proxy;}

		@Override
		public void setListener(PlayerProxyListener listener) {

			proxy.setListener(listener);
		}

		@Override
		public void onAdvisorChoice(Roll roll, Board board, PlayerStuff stuff) {

			proxy.onAdvisorChoice(roll, board, stuff);
		}

		@Override
		public void onGoodsChoice(int unchosenResourcesCount) {

			proxy.onGoodsChoice(unchosenResourcesCount);
		}

		@Override
		public void onOfferUseStatue(Roll roll) {

			proxy.onOfferUseStatue(roll);
		}

		@Override
		public void onOfferUseChapel(Roll roll) {

			proxy.onOfferUseChapel(roll);
		}

		@Override
		public void onOfferUseTownHall(PlayerStuff stuff) {

			proxy.onOfferUseTownHall(stuff);
		}

		@Override
		public void onChooseSpentResources(int unchosenLossesCount, PlayerStuff stuff) {

			proxy.onChooseSpentResources(unchosenLossesCount, stuff);
		}

		@Override
		public void onRecruitOption(PlayerStuff stuff) {

			proxy.onRecruitOption(stuff);
		}

		@Override
		public void onBuildOption(PlayerStuff stuff) {

			proxy.onBuildOption(stuff);
		}

		@Override
		public void onPeek() {

			proxy.onPeek();
		}
	}





	static final Phaser p = new Phaser(1);
	static final AtomicInteger i = new AtomicInteger();
	public static void pingpongMain(String[] args) {
		t("ping");
		t("pong");
	}
	private static void t(final String msg) {
		Thread t = new Thread() { public void run() {
			while (i.get() < 10) {
				System.out.println(msg);
				i.incrementAndGet();
				p.awaitAdvance(p.arrive()+1);
			}
		}};
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					t.join();
					System.out.println("done");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		});

		t.start();
		t2.start();
	}
}
