package com.ameron32.game.kingsburg.core;
import com.ameron32.game.kingsburg.core.advisor.Advisor;
import com.ameron32.game.kingsburg.core.advisor.Cost;
import com.ameron32.game.kingsburg.core.advisor.Reward;
import com.ameron32.game.kingsburg.core.advisor.RewardChoice;
import com.ameron32.game.kingsburg.core.bot.PlayerProxy;
import com.ameron32.game.kingsburg.core.bot.PlayerProxyListener;
import com.ameron32.game.kingsburg.core.state.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * 
 * GAME loop.
 * REQUIRED lifecycle methods:
 * 		setup()
 * 		start()
 * 		complete()
 * 
 */
public class Game implements PlayerProxyListener {

	private Logger printer = Printer.get(); // TODO remove printer

	// player numbers are locked in place. turn order is variable.

	// number of players & their stuff
	private int players;
	private PlayerStuff[] playersStuff;
	
	// proxy interfaces to resolve user/player choices
	private PlayerProxy[] proxies;
	
	// phases
	private PhaseHandler phaseHandler;
	
	// number of rounds
	private int rounds;

	// current turn order of player
	private int[] turnOrder;

	// most recent player rolls in order of player number
	private Roll[] rolls;
	
	// storage of board-based information
	private Board board;
	
	// simple enemyDeck
	private EnemyDeck enemyDeck;
	
	// save state
	private int savedRound; 
	private int savedPhase; 
	private int savedPlayer;


	//
	//
	// **************************************************
	// GETTERS AND SETTERS
	//<editor-fold desc="GettersAndSetters">
	private PlayerProxy getProxy(int player) {
		getPlayerStuff(player).pullSynchronize();
		return proxies[player];
	}

	private PlayerStuff getPlayerStuff(int player) {
		playersStuff[player].pullSynchronize();
		return playersStuff[player];
	}
	//</editor-fold>



	//
	//
	// **************************************************
	// GAME LOOP / LIFECYCLE
	//<editor-fold desc="Lifecycle">
	public void setup(int players, int phases, int rounds) {
		this.enemyDeck = new EnemyDeck(rounds);
		this.players = players;
		this.playersStuff = new PlayerStuff[players];
		this.proxies = new PlayerProxy[players];
		this.phaseHandler = new PhaseHandler();
		this.rounds = rounds;
		turnOrder = new int[players];
		for (int i = 0; i < players; i++) {
			// set basic turn order by player # in case of tie on first
			// season
			turnOrder[i] = i;
		}
		printer.log("New Game: " + players + " players");
	}
	
	public void setPlayerProxy(int player, PlayerProxy proxy) {
		proxies[player] = proxy;
	}
	
	public void setPlayerStuff(int player, PlayerStuff stuff) {
		playersStuff[player] = stuff;
	}
	
	public void setBoard(Board board) {
		this.board = board;
		this.board.initialize(players);
	}
	
	public void start() {
		for (int round = 0; round < rounds; round++) {
			performRound(round);
		}
	}

	// LOOP ROUND/YEAR within A GAME
	private void performRound(int round) {
		int year = round + 1;
		board.incrementYear();
		printer.log("Begin Year: " + year
				+ " ******************************************************"
				+ " ******************************************************");
		int numberOfPhases = phaseHandler.getPhaseCount();
		for (int phase = 0; phase < numberOfPhases; phase++) {
			performPhase(round, phase);
		}
		printer.log("Year " + year + " over." + "\n\n");
	}

	// LOOP PHASES/SEASONS within A ROUND/YEAR
	private void performPhase(int round, int phase) {
		// TODO fracture performPhase method into stop & go state-saving methods
		int truePhase = phase + 1;
		Phase xPhase = phaseHandler.getPhase(truePhase);
		board.incrementPhase();
		printer.log("\n" + "Phase " + truePhase + " (" +
				xPhase.getName() + ")" +
				" start. **************************");
		
		// aid from the king
		if (isGainsAid(phase)) {
			determineAid();
		}
		
		// the king's reward
		if (isReward(phase)) {
			determineReward();
		}

		// distribute king's envoy
		if (isEnvoy(phase)) {
			recallEnvoy();
			determineEnvoy();
		}
		
		// productive season
		if (isProductiveSeason(phase)) {
			for (PlayerStuff stuff : playersStuff) {
				int player = stuff.getPlayerId();
				if (stuff.hasMerchantsGuild()) {
					stuff.gainGold(1);
					printer.log(player, "gains 1 gold from Merchants' Guild.");
				}
			}
			
			// roll dice and adjust turn order
			// NOTE: POSSIBLE PLAYER INTERACTION BREAK (CONCURRENT)
			rollForTurnOrder(round, phase);
//			_displayRolls();

			while (playersHaveUnusedDice()) {
				for (int turn = 0; turn < players; turn++) {
					int playerAtTurnOrder = getPlayerAtTurnOrder(turn);
					// influence the King's Advisors
					// NOTE: PLAYER INTERACTION BREAK (SEQUENTIAL)
					determineKingsAdvisorsPerPlayerAndHandleRewards(round, phase, turn, playerAtTurnOrder);
				}
			}

			/*
			 *  POSSIBLE PLAYER INTERACTION BREAK
			 *  CONCURRENT
			 */
			chooseGoods(round, phase);
			/*
			 *  PLAYER INTERACTION BREAK
			 *  CONCURRENT
			 */
			offerConstructBuildings(round, phase);
			
			if (phaseHandler.isSummer(xPhase)) {
				for (PlayerStuff stuff : playersStuff) {
					int player = stuff.getPlayerId();
					if (stuff.hasInn()) {
						stuff.gainPlus2(1);
						printer.log(player, "uses the Inn to gain a +2 token.");
					}
				}
			}
						
			for (PlayerStuff stuff : playersStuff) {
				int player = stuff.getPlayerId();
				if (stuff.hasTownHall()) {
					/*
					 *  POSSIBLE PLAYER INTERACTION BREAK
					 *  CONCURRENT
					 */
					rememberRoundPhasePlayer(round, phase, player);
					offerUseTownHall(stuff, player);
				}
			}
			
			for (PlayerStuff stuff : playersStuff) {
				int player = stuff.getPlayerId();
				if (stuff.hasEmbassy()) {
					stuff.gainPoints(1);
					printer.log(player, "uses Embassy to gain +1 victory point!");
				}
			}
		}

		/*
		 *  PLAYER INTERACTION BREAK
		 *  CONCURRENT
		 */
		// recruit soldiers stage
		if (isRecruit(phase)) {
			offerRecruit(round, phase);
		}

		// battle stage
		if (isBattle(phase)) {
			rollKingsReinforcements();
			performBattle(round);
			chooseLosses(round, phase);
			resetSoldiers();
		}

		// return the king's aid
		if (isLosesAid(phase)) {
			recallAid();
		}

		// at the end of a phase, restore all advisors to an unreserved state
		board.resetAdvisors();
		
		printer.log("\n" + "Phase " + truePhase + " over." + "\n");
	}

	public void complete() {
		for (PlayerStuff stuff : playersStuff) {
			int player = stuff.getPlayerId();
			if (stuff.hasCathedral()) {
				int resources = stuff.countResources();
				int points = resources / 2;
				stuff.gainPoints(points);
				printer.log(player, "used Cathedral to gain (" + points + ") points for (" + resources + ") resources.");
			}
		}

		printer.log("Game Over");
		printer.log("Calculate Scores");

		for (int player = 0; player < players; player++) {
			PlayerStuff stuff = playersStuff[player];
			int score = stuff.countPoints();
			printer.log("Player " + (player+1) + " scored " + score + " points.");
		}

		boolean complete = false;
		int mostPointsCount = 0;
		List<Long> playersWithMostPoints = new ArrayList<>(5);
		for (PlayerStuff p : playersStuff) {
			int count = p.countPoints();
			int playerId = p.getPlayerId();
			if (count > mostPointsCount) {
				mostPointsCount = count;
			}
		}
		for (PlayerStuff p : playersStuff) {
			if (p.countPoints() == mostPointsCount) {
				int playerId = p.getPlayerId();
				playersWithMostPoints.add(new Long(playerId));
			}
		}
		int playerCountMostPoints = playersWithMostPoints.size();

		if (playerCountMostPoints == 0) {
			printer.log("No one won the game.");
			complete = true;
		}
		if (playerCountMostPoints == 1 && !complete) {
			int playerId = playersWithMostPoints.get(0).intValue();
			playerWins(playerId, mostPointsCount);
			complete = true;
		}

		// multiple players with most points
		// most resources
		if (playerCountMostPoints > 1 && !complete) {
			int mostResourcesCount = 0;

			List<Long> playersWithMostPointsAndMostResources = new ArrayList<>(playerCountMostPoints);
			for (int i = 0; i < playerCountMostPoints; i++) {
				PlayerStuff p = playersStuff[playersWithMostPoints.get(i).intValue()];
				int count = p.countResources();
				if (count > mostResourcesCount) {
					mostResourcesCount = count;
				}
			}

			for (int i = 0; i < playerCountMostPoints; i++) {
				PlayerStuff p = playersStuff[playersWithMostPoints.get(i).intValue()];
				if (p.countResources() == mostResourcesCount) {
					playersWithMostPointsAndMostResources.add(new Long(p.getPlayerId()));
				}
			}

			int playerCountMostPointsAndMostResources = playersWithMostPointsAndMostResources.size();

			if (playerCountMostPointsAndMostResources == 0) {
				throw new IllegalStateException("can't happen");
			}

			if (playerCountMostPointsAndMostResources == 1) {
				int playerId = playersWithMostPointsAndMostResources.get(0).intValue();
				playerWins(playerId, mostPointsCount, mostResourcesCount);
				complete = true;
			}

			// multiple players with most points AND most resources
			// most buildings
			if (playerCountMostPointsAndMostResources > 1 && !complete) {
				int mostBuildingsCount = 0;

				List<Long> playersWithMostPointsAndMostResourcesAndMostBuildings = new ArrayList<>(playerCountMostPointsAndMostResources);
				for (int i = 0; i < playerCountMostPointsAndMostResources; i++) {
					PlayerStuff p = playersStuff[playersWithMostPointsAndMostResources.get(i).intValue()];
					int count = p.countBuildings();
					if (count > mostBuildingsCount) {
						mostBuildingsCount = count;
					}
				}

				for (int i = 0; i < playerCountMostPointsAndMostResources; i++) {
					PlayerStuff p = playersStuff[playersWithMostPointsAndMostResources.get(i).intValue()];
					if (p.countResources() == mostBuildingsCount) {
						playersWithMostPointsAndMostResourcesAndMostBuildings.add(new Long(p.getPlayerId()));
					}
				}

				int playerCountMostPointsAndMostResourcesAndMostBuildings = playersWithMostPointsAndMostResourcesAndMostBuildings.size();

				if (playerCountMostPointsAndMostResourcesAndMostBuildings == 1) {
					int playerId = playersWithMostPointsAndMostResourcesAndMostBuildings.get(0).intValue();
					playerWins(playerId, mostPointsCount, mostResourcesCount, mostBuildingsCount);
					complete = true;
				}

				// multiple players with most points and most resources AND most buildings
				// everyone wins/ties
				if (playerCountMostPointsAndMostResourcesAndMostBuildings > 1 && !complete) {
					// everyone wins
					for (Long playerId : playersWithMostPointsAndMostResourcesAndMostBuildings) {
						int player = playerId.intValue();
						playersWin(player, mostPointsCount, mostResourcesCount, mostBuildingsCount);
						complete = true;
					}
				}
			}
		}
	}
	//</editor-fold>



	//
	//
	// **************************************************
	// HUMAN UNDERSTANDABLE LOGIC METHODS

	private boolean isProductiveSeason(int phase) {
		phase++; // real phase count
		return phaseHandler.getPhase(phase).isProductive();
	}
	
	private boolean isGainsAid(int phase) {
		phase++; // real phase count
		return phaseHandler.getPhase(phase).isGainsAid();
	}
	
	private boolean isLosesAid(int phase) {
		phase++; // real phase count
		return phaseHandler.getPhase(phase).isLosesAid();
	}

	private boolean isReward(int phase) {
		phase++; // real phase count
		return phaseHandler.getPhase(phase).isReward();
	}
	
	private boolean isRecruit(int phase) {
		phase++; // real phase count
		return phaseHandler.getPhase(phase).isRecruit();
	}
	
	private boolean isEnvoy(int phase) {
		phase++; // real phase count
		return phaseHandler.getPhase(phase).isEnvoy();
	}
	
	private boolean isBattle(int phase) {
		phase++; // real phase count
		return phaseHandler.getPhase(phase).isBattle();
	}

	private Roll[] rollDiceForAllPlayers() {
		Roll[] roll = new Roll[players]; // each players roll
		for (int player = 0; player < players; player++) {
			Roll r = _rollOnePlayer(player);
			roll[player] = r;
		}
		return roll;
	}

	private void updateTurnOrder(Roll[] rolls) {
		// determine new rankings
		List<Roll> rollList = Arrays.asList(rolls);
		Collections.sort(rollList, new RollComparator(turnOrder));

		// apply new turn order to master turn order
		for (int position = 0; position < players; position++) {
			Roll r = rollList.get(position);
			turnOrder[r.getPlayer()] = position;
		}
	}


	// LOOP TURNS within A PHASE/SEASON
	private void determineKingsAdvisorsPerPlayerAndHandleRewards(int round, int phase, int turn, int player) {
		// display the basic turn info
//		_displayGenericTurnInformation(round, phase, player);
		
		if (rolls == null || !isProductiveSeason(phase)) {
			return;
		}
		
		for (Roll roll : rolls) {
			for (int i = 0; i < players; i++) {
				if (roll.getPlayer() == player) {
					if (!roll.hasUsableDice()) {
						return;
					}
				}
			}
		}
		
		influenceOneAdvisor(round, phase, turn, player);
	}
	
	private void influenceOneAdvisor(int round, int phase, int turn, int player) {
		Roll myRoll = null;
		for (int i = 0; i < rolls.length; i++) {
			if (rolls[i].getPlayer() == player) {
				myRoll = rolls[i];
			}
		}
		if (myRoll == null) {
			return;
		}
		
		printer.log(player, rolls[turn].toString());

		/*
		 *  PLAYER INTERACTION BREAK
		 *  SEQUENTIAL
		 */
		rememberRoundPhasePlayer(round, phase, player);
		chooseAdvisor(player, myRoll);
	}

	private void rememberRoundPhase(int round, int phase) {
		this.savedRound = round;
		this.savedPhase = phase;
	}

	private void rememberRoundPhasePlayer(int round, int phase, int player) {
		this.savedRound = round;
		this.savedPhase = phase;
		this.savedPlayer = player;
	}

	// USES IDENTICAL LOGIC TO determineEnvoy FOR MOST
	private void determineAid() {
		boolean complete = false;
		int leastBuildingsCount = 31000;
		List<Long> playersWithLeastBuildings = new ArrayList<>(5);
		for (PlayerStuff p : playersStuff) {
			int count = p.countBuildings();
			if (count < leastBuildingsCount) {
				leastBuildingsCount = count;
			}
		}

		for (PlayerStuff p : playersStuff) {
			if (p.countBuildings() == leastBuildingsCount) {
				playersWithLeastBuildings.add(new Long(p.getPlayerId()));
			}
		}

		if (playersWithLeastBuildings.size() > 1) {
			int leastBuildingsSize = playersWithLeastBuildings.size();
			int leastResourcesCount = 31000;
			List<Long> playersWithLeastResources = new ArrayList<>(leastBuildingsSize);
			for (int i = 0; i < leastBuildingsSize; i++) {
				PlayerStuff p = playersStuff[playersWithLeastBuildings.get(i).intValue()];
				int count = p.countResources();
				if (count < leastResourcesCount) {
					leastResourcesCount = count;
				}
			}

			for (int i = 0; i < leastBuildingsSize; i++) {
				PlayerStuff p = playersStuff[playersWithLeastBuildings.get(i).intValue()];
				if (p.countResources() == leastResourcesCount) {
					playersWithLeastResources.add(new Long(p.getPlayerId()));
				}
			}

			int playerCountLeastBuildings = playersWithLeastBuildings.size();
			int playerCountLeastResources = playersWithLeastResources.size();
			List<Long> playersWithLeastBuildingsAndLeastResources = new ArrayList<>(5);
			for (int i = 0; i < playerCountLeastBuildings; i++) {
				for (int j = 0; j < playerCountLeastResources; j++) {
					int playerId = playersWithLeastBuildings.get(i).intValue();
					if (playersWithLeastBuildings.get(i).intValue() == playersWithLeastResources.get(j).intValue()) {
						playersWithLeastBuildingsAndLeastResources.add(new Long(playerId));
					}
				}
			}

			if (playersWithLeastBuildingsAndLeastResources.size() > 1) {
				// multiple players get minimal help
				for (Long playerId : playersWithLeastBuildingsAndLeastResources) {
					int player = playerId.intValue();
					printer.log(player, "received the King's aid of 1 resource.");
					rememberRoundPhasePlayer(savedRound, savedPhase, player);
					getPlayerStuff(player).gainUnchosenResources(1);
					complete = true;
				}
			}

			if (!complete) {
				// award to fewest resources
				int player = playersWithLeastBuildingsAndLeastResources.get(0).intValue();
				printer.log(player, "received the King's aid of 1 bonus die in Spring.");
				getPlayerStuff(player).gainAid();
				complete = true;
			}
		}

		if (!complete) {
			// award to fewest buildings
			int player = playersWithLeastBuildings.get(0).intValue();
			printer.log(player, "received the King's aid of 1 bonus die in Spring.");
			getPlayerStuff(player).gainAid();
			complete = true;
		}
	}

	private void recallAid() {
		for (PlayerStuff p : playersStuff) {
			if (p.hasAid()) {
				p.useAid();
				int player = p.getPlayerId();
				printer.log(player, "returns the King's aid die.");
			}
		}
	}

	private void determineReward() {
		int mostBuildingsCount = 0;
		List<Long> playersWithMostBuildings = new ArrayList<>(5);
		for (PlayerStuff p : playersStuff) {
			int count = p.countBuildings();
			if (count > mostBuildingsCount) {
				mostBuildingsCount = count;
			}
		}

		for (PlayerStuff p : playersStuff) {
			if (p.countBuildings() == mostBuildingsCount) {
				playersWithMostBuildings.add(new Long(p.getPlayerId()));
				int player = p.getPlayerId();
				printer.log(player, "receives the King's Reward of 1 victory point.");
				p.gainPoints(1);
			}
		}
	}

	private void recallEnvoy() {
		for (PlayerStuff p : playersStuff) {
			if (p.hasEnvoy()) {
				p.useEnvoy();
				int player = p.getPlayerId();
				printer.log(player, "has not used the King's envoy. The envoy is returned to the board.");
			}
		}
	}

	// USES IDENTICAL LOGIC TO determineAid FOR MOST
	private void determineEnvoy() {
		boolean complete = false;
		int leastBuildingsCount = 31000;
		List<Long> playersWithLeastBuildings = new ArrayList<>(5);
		for (PlayerStuff p : playersStuff) {
			int count = p.countBuildings();
			if (count < leastBuildingsCount) {
				leastBuildingsCount = count;
			}
		}

		for (PlayerStuff p : playersStuff) {
			if (p.countBuildings() == leastBuildingsCount) {
				playersWithLeastBuildings.add(new Long(p.getPlayerId()));
			}
		}

		if (playersWithLeastBuildings.size() > 1) {
			int leastBuildingsSize = playersWithLeastBuildings.size();
			int leastResourcesCount = 31000;
			List<Long> playersWithLeastResources = new ArrayList<>(leastBuildingsSize);
			for (int i = 0; i < leastBuildingsSize; i++) {
				PlayerStuff p = playersStuff[playersWithLeastBuildings.get(i).intValue()];
				int count = p.countResources();
				if (count < leastResourcesCount) {
					leastResourcesCount = count;
				}
			}

			for (int i = 0; i < leastBuildingsSize; i++) {
				PlayerStuff p = playersStuff[playersWithLeastBuildings.get(i).intValue()];
				if (p.countResources() == leastResourcesCount) {
					playersWithLeastResources.add(new Long(p.getPlayerId()));
				}
			}

			int playerCountLeastBuildings = playersWithLeastBuildings.size();
			int playerCountLeastResources = playersWithLeastResources.size();
			List<Long> playersWithLeastBuildingsAndLeastResources = new ArrayList<>(5);
			for (int i = 0; i < playerCountLeastBuildings; i++) {
				for (int j = 0; j < playerCountLeastResources; j++) {
					int playerId = playersWithLeastBuildings.get(i).intValue();
					if (playersWithLeastBuildings.get(i).intValue() == playersWithLeastResources.get(j).intValue()) {
						playersWithLeastBuildingsAndLeastResources.add(new Long(playerId));
					}
				}
			}

			if (playersWithLeastBuildingsAndLeastResources.size() > 1) {
				// no players get help
				complete = true;
				printer.log("Several players were tied for weakest governor. No one receives the King's envoy.");
			}

			if (!complete) {
				// award to fewest resources
				int player = playersWithLeastBuildingsAndLeastResources.get(0).intValue();
				printer.log(player, "has received the King's envoy, with least resources.");
				getPlayerStuff(player).gainEnvoy();
				complete = true;
			}
		}

		if (!complete) {
			// award to fewest buildings
			int player = playersWithLeastBuildings.get(0).intValue();
			printer.log(player, "has received the King's envoy.");
			getPlayerStuff(player).gainEnvoy();
			complete = true;
		}
	}

	private void rollForTurnOrder(int round, int phase) {
		// TODO add the "reserve advisors / 2-player only" rule

		rolls = rollDiceForAllPlayers();

		// offer Statue and Chapel before updating turn order
		// TODO needs concurrent Statue/Chapel offer
		if (isAnyStatuesOrChapelsEligable()) {
			printer.log("Looks like someone can use a Statue or Chapel. Let's see if the rolls are changing.");

			/*
			 *  POSSIBLE PLAYER INTERACTION BREAK
			 *  CONCURRENT
			 */
			rememberRoundPhase(round, phase);
			pauseToOfferStatueOrChapel();

			updateTurnOrder(rolls);
		}
	}

	private boolean isAnyStatuesOrChapelsEligable() {
		for (Roll roll : rolls) {
			int player = roll.getPlayer();
			PlayerStuff stuff = playersStuff[player];
			if (stuff.hasStatue() && roll.isStatueEligable()) {
				return true;
			}
			if (stuff.hasChapel() && roll.isChapelEligable()) {
				return true;
			}
		}
		return false;
	}

	Thread[] threads;
	CountDownLatch latch;
	private void pauseToOfferStatueOrChapel() {
		// TODO remove threads... only demonstrating race handling
		threads = new Thread[rolls.length];
		latch = new CountDownLatch(rolls.length);
		for (int i = 0, rollsLength = rolls.length; i < rollsLength; i++) {
			final Roll roll = rolls[i];
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					int player = roll.getPlayer();
					try {
						Thread.sleep((new Random().nextInt(5)+1) * 100 + 1000);

						PlayerStuff stuff = playersStuff[player];
						if (stuff.hasStatue() && roll.isStatueEligable()) {
							rememberRoundPhasePlayer(Game.this.savedRound, Game.this.savedPhase, player);
							offerUseStatue(roll, player);
						}
						if (stuff.hasChapel() && roll.isChapelEligable()) {
							rememberRoundPhasePlayer(Game.this.savedRound, Game.this.savedPhase, player);
							offerUseChapel(roll, player);
						}
						if (stuff.hasStatue() && roll.isStatueEligable()) {
							rememberRoundPhasePlayer(Game.this.savedRound, Game.this.savedPhase, player);
							offerUseStatue(roll, player);
						}

						latch.countDown();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {}
				}
			});
			threads[i].start();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}



	private void _displayRolls() {
		// TODO add the "reserve advisors / 2-player only" rule
		for (int turn = 0; turn < players; turn++) {
			if (rolls != null) {
				Roll roll = rolls[turn];
				final int player = roll.getPlayer();
				printer.log(player, "\n          roll || " + rolls[turn].toString());
			} else {
				printer.log("\n          roll missing for turn " + turn);
			}
		}
	}

	private void rollKingsReinforcements() {
		int reinforcements = Roll.rollTheDice(0, 1, 6).getUnusedTotal();
		printer.log("The King sends (" + reinforcements + ") reinforcements to fight alongside your soldiers.");
		board.addKingsReinforcements(reinforcements);
	}

	private void performBattle(int round) {
		// TODO consider removing building bonuses and recalculating, to be safe
		int year = round + 1;
		EnemyCard enemy = enemyDeck.getCard(year);
		printer.log("\nEnemy Card revealed: " + enemy.toString());
		int strengthToBeat = enemy.getStrength();
		int winners = 0;
		int[] cityForces = new int[5];
		for (int player = 0; player < players; player++) {
			printer.log("");
			grantBuildingSoldierBoost(player);
			int soldiers = board.getSoldiersFor(player);
			if (enemy.isGoblin() &&	getPlayerStuff(player).hasBarricade()) {
				printer.log(player, "has a Barricade against Goblins. +1");
				soldiers++;
			}
			if (enemy.isZombie() && getPlayerStuff(player).hasPalisade()) {
				printer.log(player, "has a Palisade against Zombies. +1");
				soldiers++;
			}
			if (enemy.isDemon() && getPlayerStuff(player).hasChurch()) {
				printer.log(player, "has a Church against Demons. +1");
				soldiers++;
			}

			cityForces[player] = soldiers;

			if (soldiers > strengthToBeat) {
				printer.log(player, "won the fight against " + enemy.getName() + "(" + enemy.getStrength() + ") with " + soldiers + "!");
				winners++;
				battleVictory(player, enemy);
			} else if (soldiers == strengthToBeat) {
				if (getPlayerStuff(player).hasStoneWall()) {
					printer.log(player, "stalemated the fight against " + enemy.getName() + "(" + enemy.getStrength() + ") with " + soldiers + ", but won because of having a Stone Wall!");
					winners++;
					battleVictory(player, enemy);
				} else {
					printer.log(player, "stalemated the fight against " + enemy.getName() + "(" + enemy.getStrength() + ") with " + soldiers + ".");
					battleDraw(player, enemy);
				}
			} else {
				printer.log(player, "lost the fight against " + enemy.getName() + "(" + enemy.getStrength() + ") with " + soldiers + ".");
				battleLose(player, enemy);
			}
		}

		printer.log("");
		if (winners > 0) {
			int highestStrength = 0;
			for (int player = 0; player < cityForces.length; player++) {
				if (cityForces[player] > highestStrength) {
					highestStrength = cityForces[player];
				}
			}
			for (int player = 0; player < cityForces.length; player++) {
				if (cityForces[player] == highestStrength) {
					PlayerStuff stuff = playersStuff[player];
					stuff.gainPoints(1);
					printer.log(player, "gained +1 victory point for having the most soldiers!");
				}
			}
		}
	}

	private int getPlayerAtTurnOrder(int turn) {
		for (int i = 0; i < players; i++) {
			if (turnOrder[i] == turn) {
				return i;
			}
		}
		throw new IllegalStateException("player not found in turn order");
	}

	// modify to proper roll
	private Roll _rollOnePlayer(int player) {
		// determine number of dice
		int bonusDieCount = PlayerStuff.PLAYER_STARTING_BONUS_DIE_COUNT;
		if (getPlayerStuff(player).hasAid()) {
			printer.log(player, "has the King's aid. +1 bonus die");
			bonusDieCount++;
		}
		if (getPlayerStuff(player).hasFarms()) {
			printer.log(player, "has Farms. +1 bonus die");
			bonusDieCount++;
		}
		// roll 'em
		return Roll.rollTheDice(player, PlayerStuff.PLAYER_DICE_COUNT + bonusDieCount, PlayerStuff.PLAYER_DICE_SIDES);
	}

	private void grantBuildingSoldierBoost(int player) {
		PlayerStuff stuff = playersStuff[player];
		if (stuff.hasGuardTower()) {
			printer.log(player, "has a Guard Tower. +1");
			board.increaseSoldiers(player, 1);
		}
		if (stuff.hasBlacksmith()) {
			printer.log(player, "has a Blacksmith. +1");
			board.increaseSoldiers(player, 1);
		}
		if (stuff.hasPalisade()) {
			printer.log(player, "has a Palisade. +1");
			board.increaseSoldiers(player, 1);
		}
		if (stuff.hasStoneWall()) {
			printer.log(player, "has a Stone Wall. +1");
			board.increaseSoldiers(player, 1);
		}
		if (stuff.hasFortress()) {
			printer.log(player, "has a Fortress. +1");
			board.increaseSoldiers(player, 1);
		}
		if (stuff.hasChurch()) {
			printer.log(player, "has a Church. +0");
			board.increaseSoldiers(player, 0);
		}
		if (stuff.hasBarricade()) {
			printer.log(player, "has a Barricade. +0");
			board.increaseSoldiers(player, 0);
		}
		if (stuff.hasWizardsGuild()) {
			printer.log(player, "has a Wizards' Guild. +1");
			board.increaseSoldiers(player, 2);
		}
		if (stuff.hasFarms()) {
			printer.log(player, "has Farms. -1");
			board.increaseSoldiers(player, -1);
		}
	}

	private boolean playersHaveUnusedDice() {
		for (Roll roll : rolls) {
			if (roll.hasUsableDice()) {
				return true;
			}
		}
		return false;
	}

	private void resetSoldiers() {
		board.resetSoldiers();
		// TODO add for buildings
	}

	//<editor-fold desc="WinterBattleResults">
	private void battleVictory(int player, EnemyCard enemy) {
		PlayerStuff stuff = playersStuff[player];
		Cost cost = enemy.getVictory().getCost();
		Reward reward = enemy.getVictory().getReward();
		printer.log(player, "collects the victory reward of: " + reward.toString());
		stuff.payCost(cost); // shouldn't be a cost
		stuff.receiveReward(reward);
		if (stuff.hasFortress()) {
			stuff.gainPoints(1);
			printer.log(player, "uses Fortress and gains an additional +1 victory point!");
		}
	}

	// THIS IS A REAL DRAW. STONE WALL DRAW-WINS ARE ALREADY HANDLED
	private void battleDraw(int player, EnemyCard enemy) {
		// do nothing
	}

	private void battleLose(int player, EnemyCard enemy) {
		Cost cost = enemy.getDefeat().getCost();
		Reward reward = enemy.getDefeat().getReward();
		boolean loseABuilding = enemy.isDefeatLoseBuilding();
		printer.log(player, "pays the defeat cost of: " + cost.toString()
			+ (loseABuilding ? " and loses a building!" : ""));
		getPlayerStuff(player).payCost(cost);
		if (loseABuilding) {
			ProvinceBuilding buildingLost = getPlayerStuff(player).loseABuilding();
			if (buildingLost != null) {
				printer.log(player, "loses a " + buildingLost.getName() + "(" + buildingLost.getPoints() + ").");
			} else {
				printer.log(player, "had no buildings. No buildings were lost.");
			}
		}
		getPlayerStuff(player).receiveReward(reward); // shouldn't be a reward
	}
	//</editor-fold>


	//<editor-fold desc="DeclareGameWinner(s)">
	private void playerWins(int player, int score) {
		printer.log("");
		printer.log("Player " + (player+1)
				+ " wins with " + score + " points!");
	}

	private void playerWins(int player, int score, int totalResources) {
		printer.log("");
		printer.log("Player " + (player+1)
				+ " ties with " + score + " points, but wins with "
				+ totalResources + " resources!");
	}

	private void playerWins(int player, int score, int totalResources, int totalBuildings) {
		printer.log("");
		printer.log("Player " + (player+1)
				+ " ties with " + score + " points and "
				+ totalResources + " resources, but wins with "
				+ totalBuildings + " buildings!");
	}



	private void playersWin(int player, int score, int totalResources, int totalBuildings) {
		printer.log("");
		printer.log("Player " + (player+1)
				+ " ties with " + score + " points and "
				+ totalResources + " resources and "
				+ totalBuildings + " buildings!");
	}
	//</editor-fold>


	//<editor-fold desc="PrepareToPromptUserOrBot">
	private void chooseAdvisor(int player, Roll myRoll) {
		getProxy(player).onAdvisorChoice(myRoll, board, playersStuff[player]);
	}

	private void chooseGoods(int round, int phase) {
		for (int player = 0; player < players; player++) {
			rememberRoundPhasePlayer(round, phase, player);
			getProxy(player).onGoodsChoice(getPlayerStuff(player).countUnchosenResources());
		}
	}

	private void chooseLosses(int round, int phase) {
		for (int player = 0; player < players; player++) {
			chooseLosses(round, phase, player);
		}
	}

	private void chooseLosses(int round, int phase, int player) {
		rememberRoundPhasePlayer(round, phase, player);
		getProxy(player).onChooseSpentResources(getPlayerStuff(player).countUnpaidDebts(), playersStuff[player]);
	}

	private void offerUseStatue(Roll roll, int player) {
		getProxy(player).onOfferUseStatue(roll);
	}

	private void offerUseChapel(Roll roll, int player) {
		getProxy(player).onOfferUseChapel(roll);
	}

	private void offerUseTownHall(PlayerStuff stuff, int player) {
		getProxy(player).onOfferUseTownHall(stuff);
	}

	private void offerConstructBuildings(int round, int phase) {
		printer.log("");
		for (int player = 0; player < players; player++) {
			rememberRoundPhasePlayer(player, player, player);
			getProxy(player).onBuildOption(playersStuff[player]);
		}
	}

	private void offerRecruit(int round, int phase) {
		for (PlayerStuff p : playersStuff) {
			int player = p.getPlayerId();
			rememberRoundPhasePlayer(round, phase, player);
			getProxy(player).onRecruitOption(p);
		}
	}

	private void offerPeek(int player) {
		// peek at soldiers
		getProxy(player).onPeek();
	}
	//</editor-fold>


	//<editor-fold desc="ListenerCallbacks">
	@Override
	public void onAdvisorGiftSelection(Advisor advisor, RewardChoice rewardChoice) {
		final int round = this.savedRound;
		final int phase = this.savedPhase;
		final int player = this.savedPlayer;

		if (advisor != null) {
			board.reserveAdvisor(advisor.getOrdinal());
		}

		if (rewardChoice == null) {
			//player has skipped the turn
			printer.log(player, "has skipped the turn.");
			return;
		}

		PlayerStuff myStuff = playersStuff[player];
		// pay the cost for the advisor (or skip the reward)
		boolean skip = false;
		if (rewardChoice.hasAnyCost()) {
			Cost cost = rewardChoice.getCost();
			if (myStuff.canPayCost(cost)) {
				myStuff.payCost(cost);
			} else {
				skip = true;
			}
		}
		if (!skip) {
			printer.log(player, "received the reward [" + rewardChoice.getHumanReadableReward() + "] from " + advisor.getName() + "(" + advisor.getOrdinal() + ").");
			Reward reward = rewardChoice.getReward();
			myStuff.receiveReward(reward);

			// handle soldiers
			int soldiers = reward.getSoldiers();
			if (soldiers > 0) {
				if (myStuff.hasStable()) {
					soldiers++;
					printer.log(player, "uses Stables to increase soldiers by 1.");
				}
				board.increaseSoldiers(player, soldiers);
			}
			if (reward.isPeek()) {
				offerPeek(player);

			}
		} else {
			printer.log(player, "skipped the turn, unable to pay the gift cost.");
		}

		// display stuff
		printer.log(player, myStuff.toHumanReadableString());
		getPlayerStuff(player).pushUpdate();
	}

	@Override
	public void onUseStatueResponse(boolean useStatue, Roll roll, int diePosition) {
		final int round = this.savedRound;
		final int phase = this.savedPhase;
		final int player = this.savedPlayer;
		printer.log(player, ((useStatue) ? "did" : "did not") + " use the Statue.");
		if (useStatue) {
			printer.log(player, "roll was " + roll.toString());
			roll.useStatue(diePosition);
			printer.log(player, "roll is now " + roll.toString());
		}
		getPlayerStuff(player).pushUpdate();
	}

	@Override
	public void onUseChapelResponse(boolean useChapel, Roll roll) {
		final int round = this.savedRound;
		final int phase = this.savedPhase;
		final int player = this.savedPlayer;
		printer.log(player, ((useChapel) ? "did" : "did not") + " use the Chapel.");
		if (useChapel) {
			printer.log(player, "roll was " + roll.toString());
			roll.useChapel();
			printer.log(player, "roll is now " + roll.toString());
		}
		getPlayerStuff(player).pushUpdate();
	}

	@Override
	public void onUseTownHallResponse(boolean useTownHall) {
		final int round = this.savedRound;
		final int phase = this.savedPhase;
		final int player = this.savedPlayer;
		if (useTownHall) {
			PlayerStuff stuff = playersStuff[player];
			stuff.gainPoints(1);
			stuff.spendUnpaidDebt(1);
			chooseLosses(round, phase, player);
			printer.log(player, "uses Town Hall to gain +1 victory point in exchange for 1 resource.");
		}
		getPlayerStuff(player).pushUpdate();
	}

	@Override
	public void onGoodsSelection(Reward total) {
		final int round = this.savedRound;
		final int phase = this.savedPhase;
		final int player = this.savedPlayer;
		if (!total.isEmpty()) {
			printer.log(player, "converted unchosen resources into goods.");
		}
		getPlayerStuff(player).clearUnchosenResources();
		getPlayerStuff(player).receiveReward(total);
		getPlayerStuff(player).pushUpdate();
	}

	@Override
	public void onLossesSelection(Cost total) {
		final int round = this.savedRound;
		final int phase = this.savedPhase;
		final int player = this.savedPlayer;
		if (!total.isEmpty()) {
			printer.log(player, "paid unchosen losses from goods.");
		}
		getPlayerStuff(player).clearUnpaidDebts();
		getPlayerStuff(player).payCost(total);
		getPlayerStuff(player).pushUpdate();
	}

	@Override
	public void onBuild(List<ProvinceBuilding> buildings) {
		final int round = this.savedRound;
		final int phase = this.savedPhase;
		final int player = this.savedPlayer;
		if (buildings == null || buildings.isEmpty()) {
			printer.log(player, "did not build a building.");
			return;
		}
		if (buildings.size() == 1) {
			ProvinceBuilding building = buildings.get(0);
			printer.log(player, "built a " + building.getName() + "!");
			getPlayerStuff(player).gainPoints(building.getPoints());
			return;
		}
		if (buildings.size() > 1) {
			printer.log(player, "built:\n");
			for (ProvinceBuilding building : buildings) {
				printer.log(player, "a " + building.getName() + "\n");
				getPlayerStuff(player).gainPoints(building.getPoints());
			}
		}
		getPlayerStuff(player).pushUpdate();
	}

	@Override
	public void onChooseRecruitQuantity(int count) {
		final int round = this.savedRound;
		final int phase = this.savedPhase;
		final int player = this.savedPlayer;
		PlayerStuff stuff = playersStuff[player];
		int recruitQty = count / 2;
		if (stuff.hasBarracks()) {
			recruitQty = count;
		}
		getProxy(player).onChooseSpentResources(recruitQty, stuff);
		printer.log(player, "recruited [" + count + "] soldier(s).");
		board.increaseSoldiers(player, count);
		getPlayerStuff(player).pushUpdate();
	}
	//</editor-fold>


}
