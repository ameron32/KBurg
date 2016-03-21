package com.ameron32.game.kingsburg.core;
import com.ameron32.game.kingsburg.core.bot.PlayerProxyListener;
import com.ameron32.game.kingsburg.core.state.Stage;
import com.ameron32.game.kingsburg.core.advisor.Advisor;
import com.ameron32.game.kingsburg.core.advisor.Cost;
import com.ameron32.game.kingsburg.core.advisor.Reward;
import com.ameron32.game.kingsburg.core.advisor.RewardChoice;
import com.ameron32.game.kingsburg.core.bot.PlayerProxy;
import com.ameron32.game.kingsburg.core.state.*;

import java.util.*;

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

	//
	//
	// **************************************************
	// GETTERS AND SETTERS
	//<editor-fold desc="GettersAndSetters">
	// proxy interfaces to resolve user/player choices
	private PlayerProxy[] proxies;
	private PlayerProxy getProxy(int player) {
		getPlayerStuff(player).pullSynchronize();
		return proxies[player];
	}
	public void setPlayerProxy(int player, PlayerProxy proxy) {
		proxies[player] = proxy;
	}

	// player numbers are locked in place. turn order is variable.
	// number of players & their stuff
	private int players;
	private PlayerStuff[] playersStuff;
	private PlayerStuff getPlayerStuff(int player) {
		playersStuff[player].pullSynchronize();
		return playersStuff[player];
	}
	public void setPlayerStuff(int player, PlayerStuff stuff) {
		playersStuff[player] = stuff;
	}

	private List<PlayerStuff> getAllPlayersStuff() {
		return Arrays.asList(playersStuff);
	}

	// simple enemyDeck
	private EnemyDeck enemyDeck;
	private EnemyDeck getEnemyDeck() {
		return enemyDeck;
	}

	// phases
	private PhaseHandler phaseHandler;
	private PhaseHandler getPhaseHandler() {
		return phaseHandler;
	}

	// storage of board-based information
	private Board board;
	private Board getBoard() {
		return board;
	}
	private void setBoard(Board board) {
		this.board = board;
	}

	// most recent player rolls in order of player number
	private Roll[] rolls;
	private Roll[] getRolls() {
		return rolls;
	}
	private void setRolls(Roll[] rolls) {
		this.rolls = rolls;
	}

	// current turn order of player
	private int[] turnOrder;
	private int[] getTurnOrder() {
		return turnOrder;
	}
	private void setTurnOrder(int[] turnOrder) {
		this.turnOrder = turnOrder;
	}
	private void setTurnOrder(int player, int position) {
		this.turnOrder[player] = position;
	}

	// number of rounds
	private int rounds;
	private int getRounds() {
		return rounds;
	}
	private void setRounds(int rounds) {
		this.rounds = rounds;
	}
	//</editor-fold>



	//
	//
	// **************************************************
	// GAME LOOP / LIFECYCLE
	//<editor-fold desc="Lifecycle">
	public void setup(int players, int phases, int rounds, Board board) {
		this.players = players;
		this.playersStuff = new PlayerStuff[players];
		this.proxies = new PlayerProxy[players];
		this.phaseHandler = new PhaseHandler();
		setRounds(rounds);
		setTurnOrder(new int[players]);
		setBoard(board);

		getBoard().initialize(players, getPhaseHandler().getPhaseCount());
		this.enemyDeck = new EnemyDeck(getRounds());
		for (int i = 0; i < players; i++) {
			// set basic turn order by player # in case of tie on first season
			setTurnOrder(i, i);
		}
		printer.log("New Game: " + players + " players");
	}
	
	@Deprecated
	public void start() {
		while(getBoard().getCurrentYear() < getRounds()) {
			int round = getBoard().getCurrentYear();
			int phase = getBoard().getCurrentPhase();
			Stage stage = Stage.values()[getBoard().getCurrentStage()];
			performStage(round, phase, stage);
		}
	}

	// LOOP PHASES/SEASONS within A ROUND/YEAR
	// ADDED STAGE: the fragment of a PHASE that runs from User-Input to User-Input
	public void performStage(int round, int phase, Stage stage) {
		int truePhase = phase + 1;
		Phase xPhase = getPhaseHandler().getPhase(truePhase);
		switch (stage) {
			case START_PHASE:
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

				getBoard().incrementStage();
				break;

			case ROLL_AND_REROLL:
				// productive season
				if (isProductiveSeason(phase)) {
					for (PlayerStuff stuff : getAllPlayersStuff()) {
						int player = stuff.getPlayerId();
						if (stuff.hasMerchantsGuild()) {
							stuff.gainGold(1);
							printer.log(player, "gains 1 gold from Merchants' Guild.");
						}
					}

					// roll dice and adjust turn order
					// NOTE: POSSIBLE PLAYER INTERACTION BREAK (CONCURRENT)
					rollForTurnOrder(round, phase);
				}

				getBoard().incrementStage();
				break;

			case CHOOSE_ADVISORS:
				if (isProductiveSeason(phase)) {
					if (!playersHaveUnusedDice()) {
						getBoard().incrementStage();
						return;
					}
					int turn = getBoard().getCurrentTurn();
					int playerAtTurnOrder = getPlayerAtTurnOrder(turn);

					// influence the King's Advisors
					// PLAYER INTERACTION BREAK
					// SEQUENTIAL
					influenceOneAdvisor(round, phase, turn, playerAtTurnOrder);
					getBoard().incrementTurn();
				} else {
					getBoard().incrementStage();
				}
				break;

			case SELECT_RESOURCES_AND_BUILD:
				if (isProductiveSeason(phase)) {
					// POSSIBLE PLAYER INTERACTION BREAK
					// CONCURRENT
					chooseGoods(round, phase);
					offerConstructBuildings(round, phase);
				}
				getBoard().incrementStage();
				break;

			case TOWNHALL_OPTION_AND_RECRUIT_SOLDIERS:
				if (isProductiveSeason(phase)) {
					if (getPhaseHandler().isSummer(xPhase)) {
						for (PlayerStuff stuff : getAllPlayersStuff()) {
							int player = stuff.getPlayerId();
							if (stuff.hasInn()) {
								stuff.gainPlus2(1);
								printer.log(player, "uses the Inn to gain a +2 token.");
							}
						}
					}

					for (PlayerStuff stuff : getAllPlayersStuff()) {
						int player = stuff.getPlayerId();
						if (stuff.hasEmbassy()) {
							stuff.gainPoints(1);
							printer.log(player, "uses Embassy to gain +1 victory point!");
						}
					}

					for (PlayerStuff stuff : getAllPlayersStuff()) {
						int player = stuff.getPlayerId();
						if (stuff.hasTownHall()) {
							// POSSIBLE PLAYER INTERACTION BREAK
							// CONCURRENT
							offerUseTownHall(stuff, player);
						}
					}
				}

				// recruit soldiers stage
				if (isRecruit(phase)) {
					// POSSIBLE PLAYER INTERACTION BREAK
					// CONCURRENT
					offerRecruit(round, phase);
				}
				getBoard().incrementStage();
				break;

			case CHOOSE_DEFEAT_LOSSES:
				// battle stage
				if (isBattle(phase)) {
					rollKingsReinforcements();
					performBattle(round);
					resetSoldiers();

					// POSSIBLE PLAYER INTERACTION BREAK
					// CONCURRENT
					chooseLosses(round, phase);
				}
				getBoard().incrementStage();
				break;

			case END_PHASE:
				// return the king's aid
				if (isLosesAid(phase)) {
					recallAid();
				}

				// at the end of a phase, restore all advisors to an unreserved state
				getBoard().resetAdvisors();

				printer.log("\n" + "Phase " + truePhase + " over." + "\n");
				getBoard().incrementPhase();
				break;
		}
	}

	public void complete() {
		for (PlayerStuff stuff : getAllPlayersStuff()) {
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
			PlayerStuff stuff = getPlayerStuff(player);
			int score = stuff.countPoints();
			printer.log("Player " + (player+1) + " scored " + score + " points.");
		}

		boolean complete = false;
		int mostPointsCount = 0;
		List<Long> playersWithMostPoints = new ArrayList<>(5);
		for (PlayerStuff p : getAllPlayersStuff()) {
			int count = p.countPoints();
			int playerId = p.getPlayerId();
			if (count > mostPointsCount) {
				mostPointsCount = count;
			}
		}
		for (PlayerStuff p : getAllPlayersStuff()) {
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
				PlayerStuff p = getPlayerStuff(playersWithMostPoints.get(i).intValue());
				int count = p.countResources();
				if (count > mostResourcesCount) {
					mostResourcesCount = count;
				}
			}

			for (int i = 0; i < playerCountMostPoints; i++) {
				PlayerStuff p = getPlayerStuff(playersWithMostPoints.get(i).intValue());
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
					PlayerStuff p = getPlayerStuff(playersWithMostPointsAndMostResources.get(i).intValue());
					int count = p.countBuildings();
					if (count > mostBuildingsCount) {
						mostBuildingsCount = count;
					}
				}

				for (int i = 0; i < playerCountMostPointsAndMostResources; i++) {
					PlayerStuff p = getPlayerStuff(playersWithMostPointsAndMostResources.get(i).intValue());
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
//<editor-fold desc="PhaseHandler Tests">
	private boolean isProductiveSeason(int phase) {
		phase++; // real phase count
		return getPhaseHandler().getPhase(phase).isProductive();
	}
	
	private boolean isGainsAid(int phase) {
		phase++; // real phase count
		return getPhaseHandler().getPhase(phase).isGainsAid();
	}
	
	private boolean isLosesAid(int phase) {
		phase++; // real phase count
		return getPhaseHandler().getPhase(phase).isLosesAid();
	}

	private boolean isReward(int phase) {
		phase++; // real phase count
		return getPhaseHandler().getPhase(phase).isReward();
	}
	
	private boolean isRecruit(int phase) {
		phase++; // real phase count
		return getPhaseHandler().getPhase(phase).isRecruit();
	}
	
	private boolean isEnvoy(int phase) {
		phase++; // real phase count
		return getPhaseHandler().getPhase(phase).isEnvoy();
	}
	
	private boolean isBattle(int phase) {
		phase++; // real phase count
		return getPhaseHandler().getPhase(phase).isBattle();
	}
	//</editor-fold>


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
		Collections.sort(rollList, new RollComparator(getTurnOrder()));

		// apply new turn order to master turn order
		for (int position = 0; position < players; position++) {
			Roll r = rollList.get(position);
			setTurnOrder(r.getPlayer(), position);;
		}
	}


	// LOOP TURNS within A PHASE/SEASON
	private void influenceOneAdvisor(int round, int phase, int turn, int player) {

		if (getRolls() == null || !isProductiveSeason(phase)) {
			return;
		}
		
		for (Roll roll : getRolls()) {
			for (int i = 0; i < players; i++) {
				if (roll.getPlayer() == player) {
					if (!roll.hasUsableDice()) {
						return;
					}
				}
			}
		}
		
		// influence one advisor
		Roll myRoll = null;
		for (int i = 0; i < getRolls().length; i++) {
			if (getRolls()[i].getPlayer() == player) {
				myRoll = getRolls()[i];
			}
		}
		if (myRoll == null) {
			return;
		}
		
		printer.log(player, getRolls()[turn].toString());

		// PLAYER INTERACTION BREAK
		// SEQUENTIAL
		chooseAdvisor(player, myRoll);
	}

	// USES IDENTICAL LOGIC TO determineEnvoy FOR MOST
	private void determineAid() {
		boolean complete = false;
		int leastBuildingsCount = 31000;
		List<Long> playersWithLeastBuildings = new ArrayList<>(5);
		for (PlayerStuff p : getAllPlayersStuff()) {
			int count = p.countBuildings();
			if (count < leastBuildingsCount) {
				leastBuildingsCount = count;
			}
		}

		for (PlayerStuff p : getAllPlayersStuff()) {
			if (p.countBuildings() == leastBuildingsCount) {
				playersWithLeastBuildings.add(new Long(p.getPlayerId()));
			}
		}

		if (playersWithLeastBuildings.size() > 1) {
			int leastBuildingsSize = playersWithLeastBuildings.size();
			int leastResourcesCount = 31000;
			List<Long> playersWithLeastResources = new ArrayList<>(leastBuildingsSize);
			for (int i = 0; i < leastBuildingsSize; i++) {
				PlayerStuff p = getPlayerStuff(playersWithLeastBuildings.get(i).intValue());
				int count = p.countResources();
				if (count < leastResourcesCount) {
					leastResourcesCount = count;
				}
			}

			for (int i = 0; i < leastBuildingsSize; i++) {
				PlayerStuff p = getPlayerStuff(playersWithLeastBuildings.get(i).intValue());
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
		for (PlayerStuff p : getAllPlayersStuff()) {
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
		for (PlayerStuff p : getAllPlayersStuff()) {
			int count = p.countBuildings();
			if (count > mostBuildingsCount) {
				mostBuildingsCount = count;
			}
		}

		for (PlayerStuff p : getAllPlayersStuff()) {
			if (p.countBuildings() == mostBuildingsCount) {
				playersWithMostBuildings.add(new Long(p.getPlayerId()));
				int player = p.getPlayerId();
				printer.log(player, "receives the King's Reward of 1 victory point.");
				p.gainPoints(1);
			}
		}
	}

	private void recallEnvoy() {
		for (PlayerStuff p : getAllPlayersStuff()) {
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
		for (PlayerStuff p : getAllPlayersStuff()) {
			int count = p.countBuildings();
			if (count < leastBuildingsCount) {
				leastBuildingsCount = count;
			}
		}

		for (PlayerStuff p : getAllPlayersStuff()) {
			if (p.countBuildings() == leastBuildingsCount) {
				playersWithLeastBuildings.add(new Long(p.getPlayerId()));
			}
		}

		if (playersWithLeastBuildings.size() > 1) {
			int leastBuildingsSize = playersWithLeastBuildings.size();
			int leastResourcesCount = 31000;
			List<Long> playersWithLeastResources = new ArrayList<>(leastBuildingsSize);
			for (int i = 0; i < leastBuildingsSize; i++) {
				PlayerStuff p = getPlayerStuff(playersWithLeastBuildings.get(i).intValue());
				int count = p.countResources();
				if (count < leastResourcesCount) {
					leastResourcesCount = count;
				}
			}

			for (int i = 0; i < leastBuildingsSize; i++) {
				PlayerStuff p = getPlayerStuff(playersWithLeastBuildings.get(i).intValue());
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

		setRolls(rollDiceForAllPlayers());

		// offer Statue and Chapel before updating turn order
		// TODO needs concurrent Statue/Chapel offer
		if (isAnyStatuesOrChapelsEligable()) {
			printer.log("Looks like someone can use a Statue or Chapel. Let's see if the rolls are changing.");

			// POSSIBLE PLAYER INTERACTION BREAK
			// CONCURRENT
			pauseToOfferStatueOrChapel();
		}

		updateTurnOrder(getRolls());
	}

	private boolean isAnyStatuesOrChapelsEligable() {
		for (Roll roll : getRolls()) {
			int player = roll.getPlayer();
			PlayerStuff stuff = getPlayerStuff(player);
			if (stuff.hasStatue() && roll.isStatueEligable()) {
				return true;
			}
			if (stuff.hasChapel() && roll.isChapelEligable()) {
				return true;
			}
		}
		return false;
	}

	private void pauseToOfferStatueOrChapel() {
		for (int i = 0, rollsLength = getRolls().length; i < rollsLength; i++) {
			final Roll roll = getRolls()[i];
			int player = roll.getPlayer();
			handleReroll(roll, player);
		}
	}

	private void handleReroll(Roll roll, int player) {
		PlayerStuff stuff = getPlayerStuff(player);
		if (stuff.hasStatue() && roll.isStatueEligable()) {
			offerUseStatue(roll, player);
		}
		if (stuff.hasChapel() && roll.isChapelEligable()) {
			offerUseChapel(roll, player);
		}
		if (stuff.hasStatue() && roll.isStatueEligable()) {
			offerUseStatue(roll, player);
		}
	}

	private void rollKingsReinforcements() {
		int reinforcements = Roll.rollTheDice(0, 1, 6).getUnusedTotal();
		printer.log("The King sends (" + reinforcements + ") reinforcements to fight alongside your soldiers.");
		getBoard().addKingsReinforcements(reinforcements);
	}

	private void performBattle(int round) {
		// TODO consider removing building bonuses and recalculating, to be safe
		int year = round + 1;
		EnemyCard enemy = getEnemyDeck().getCard(year);
		printer.log("\nEnemy Card revealed: " + enemy.toString());
		int strengthToBeat = enemy.getStrength();
		int winners = 0;
		int[] cityForces = new int[5];
		for (int player = 0; player < players; player++) {
			printer.log("");
			grantBuildingSoldierBoost(player);
			int soldiers = getBoard().getSoldiersFor(player);
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
					PlayerStuff stuff = getPlayerStuff(player);
					stuff.gainPoints(1);
					printer.log(player, "gained +1 victory point for having the most soldiers!");
				}
			}
		}
	}

	private int getPlayerAtTurnOrder(int turn) {
		for (int i = 0; i < players; i++) {
			if (getTurnOrder()[i] == turn) {
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
		PlayerStuff stuff = getPlayerStuff(player);
		if (stuff.hasGuardTower()) {
			printer.log(player, "has a Guard Tower. +1");
			getBoard().increaseSoldiers(player, 1);
		}
		if (stuff.hasBlacksmith()) {
			printer.log(player, "has a Blacksmith. +1");
			getBoard().increaseSoldiers(player, 1);
		}
		if (stuff.hasPalisade()) {
			printer.log(player, "has a Palisade. +1");
			getBoard().increaseSoldiers(player, 1);
		}
		if (stuff.hasStoneWall()) {
			printer.log(player, "has a Stone Wall. +1");
			getBoard().increaseSoldiers(player, 1);
		}
		if (stuff.hasFortress()) {
			printer.log(player, "has a Fortress. +1");
			getBoard().increaseSoldiers(player, 1);
		}
		if (stuff.hasChurch()) {
			printer.log(player, "has a Church. +0");
			getBoard().increaseSoldiers(player, 0);
		}
		if (stuff.hasBarricade()) {
			printer.log(player, "has a Barricade. +0");
			getBoard().increaseSoldiers(player, 0);
		}
		if (stuff.hasWizardsGuild()) {
			printer.log(player, "has a Wizards' Guild. +1");
			getBoard().increaseSoldiers(player, 2);
		}
		if (stuff.hasFarms()) {
			printer.log(player, "has Farms. -1");
			getBoard().increaseSoldiers(player, -1);
		}
	}

	private boolean playersHaveUnusedDice() {
		for (Roll roll : getRolls()) {
			if (roll.hasUsableDice()) {
				return true;
			}
		}
		return false;
	}

	private void resetSoldiers() {
		getBoard().resetSoldiers();
		// TODO add for buildings
	}

	//<editor-fold desc="WinterBattleResults">
	private void battleVictory(int player, EnemyCard enemy) {
		PlayerStuff stuff = getPlayerStuff(player);
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
		getProxy(player).onAdvisorChoice(myRoll, getBoard(), getPlayerStuff(player));
	}

	private void chooseGoods(int round, int phase) {
		for (int player = 0; player < players; player++) {
			getProxy(player).onGoodsChoice(getPlayerStuff(player).countUnchosenResources());
		}
	}

	private void chooseLosses(int round, int phase) {
		for (int player = 0; player < players; player++) {
			chooseLosses(round, phase, player);
		}
	}

	private void chooseLosses(int round, int phase, int player) {
		int debts = getPlayerStuff(player).countUnpaidDebts();
		getProxy(player).onChooseSpentResources(debts, getPlayerStuff(player));
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
			getProxy(player).onBuildOption(getPlayerStuff(player));
		}
	}

	private void offerRecruit(int round, int phase) {
		for (PlayerStuff p : getAllPlayersStuff()) {
			int player = p.getPlayerId();
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
	public void onAdvisorGiftSelection(int player, Advisor advisor, RewardChoice rewardChoice) {
		if (advisor != null) {
			getBoard().reserveAdvisor(advisor.getOrdinal());
		}

		if (rewardChoice == null) {
			//player has skipped the turn
			printer.log(player, "has skipped the turn.");
			return;
		}

		PlayerStuff myStuff = getPlayerStuff(player);
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
				getBoard().increaseSoldiers(player, soldiers);
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
	public void onUseStatueResponse(int player, boolean useStatue, Roll roll, int diePosition) {
		printer.log(player, ((useStatue) ? "did" : "did not") + " use the Statue.");
		if (useStatue) {
			printer.log(player, "roll was " + roll.toString());
			roll.useStatue(diePosition);
			printer.log(player, "roll is now " + roll.toString());
		}
		getPlayerStuff(player).pushUpdate();
	}

	@Override
	public void onUseChapelResponse(int player, boolean useChapel, Roll roll) {
		printer.log(player, ((useChapel) ? "did" : "did not") + " use the Chapel.");
		if (useChapel) {
			printer.log(player, "roll was " + roll.toString());
			roll.useChapel();
			printer.log(player, "roll is now " + roll.toString());
		}
		getPlayerStuff(player).pushUpdate();
	}

	@Override
	public void onUseTownHallResponse(int player, boolean useTownHall) {
		final int round = getBoard().getCurrentYear();
		final int phase = getBoard().getCurrentPhase();
		if (useTownHall) {
			PlayerStuff stuff = getPlayerStuff(player);
			stuff.gainPoints(1);
			stuff.spendUnpaidDebt(1);
			chooseLosses(round, phase, player);
			printer.log(player, "uses Town Hall to gain +1 victory point in exchange for 1 resource.");
		}
		getPlayerStuff(player).pushUpdate();
	}

	@Override
	public void onGoodsSelection(int player, Reward total) {
		if (!total.isEmpty()) {
			printer.log(player, "converted unchosen resources into goods.");
		}
		getPlayerStuff(player).clearUnchosenResources();
		getPlayerStuff(player).receiveReward(total);
		getPlayerStuff(player).pushUpdate();
	}

	@Override
	public void onLossesSelection(int player, Cost total) {
		if (!total.isEmpty()) {
			printer.log(player, "paid unchosen losses from goods.");
		}
		getPlayerStuff(player).clearUnpaidDebts();
		getPlayerStuff(player).payCost(total);
		getPlayerStuff(player).pushUpdate();
	}

	@Override
	public void onBuild(int player, List<ProvinceBuilding> buildings) {
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
	public void onChooseRecruitQuantity(int player, int count) {
		final int round = getBoard().getCurrentYear();
		final int phase = getBoard().getCurrentPhase();

		PlayerStuff stuff = getPlayerStuff(player);
		int recruitQty = count / 2;
		if (stuff.hasBarracks()) {
			recruitQty = count;
		}
		getProxy(player).onChooseSpentResources(recruitQty, stuff);
		printer.log(player, "recruited [" + count + "] soldier(s).");
		getBoard().increaseSoldiers(player, count);
		getPlayerStuff(player).pushUpdate();
	}
	//</editor-fold>


}
