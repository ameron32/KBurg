import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

	Logger p = Printer.get(); // TODO remove printer

	// player numbers are locked in place. turn order is variable.

	// number of players & their stuff
	int players;
	PlayerStuff[] playersStuff;
	
	// proxy interfaces to resolve user/player choices
	PlayerProxy[] proxies;
	
	// phases
	PhaseHandler phaseHandler;
	
	// number of rounds
	int rounds;

	// current turn order of player
	int[] turnOrder;

	// most recent player rolls in order of player number
	Roll[] rolls;
	
	// storage of board-based information
	Board board;
	
	// simple enemyDeck
	EnemyDeck enemyDeck;

	//
	//
	// **************************************************
	// GAME LOOP / LIFECYCLE

	void setup(int players, int phases, int rounds) {
		this.enemyDeck = new EnemyDeck(rounds);
		p.log(enemyDeck.toString() + "\n\n\n"); // TODO wrong place for a review of enemy cards
		
		this.players = players;
		this.playersStuff = new PlayerStuff[players];
		for (int i = 0; i < players; i++) {
			playersStuff[i] = new PlayerStuff(i);
		}
		this.proxies = new PlayerProxy[players];
		this.phaseHandler = new PhaseHandler();
		this.rounds = rounds;
		turnOrder = new int[players];
		for (int i = 0; i < players; i++) {
			// set basic turn order by player # in case of tie on first
			// season
			turnOrder[i] = i;
		}
		this.board = new Board(players);
		p.log("New Game: " + players + " players");
	}
	
	void setPlayerProxy(int player, PlayerProxy proxy) {
		proxies[player] = proxy;
	}
	
	void start() {
		for (int round = 0; round < rounds; round++) {
			performRound(round);
		}
	}

	// LOOP ROUND/YEAR within A GAME
	private void performRound(int round) {
		int year = round + 1;
		board.incrementYear();
		p.log("Begin Year: " + year
				+ " ******************************************************"
				+ " ******************************************************");
		int numberOfPhases = PhaseHandler.getPhaseCount();
		for (int phase = 0; phase < numberOfPhases; phase++) {
			performPhase(round, phase);
		}
		p.log("Year " + year + " over." + "\n\n");
	}

	// LOOP PHASES/SEASONS within A ROUND/YEAR
	private void performPhase(int round, int phase) {
		int truePhase = phase + 1;
		board.incrementPhase();
		p.log("\n" + "Phase " + truePhase
				+ " start. **************************");
		
		// aid from the king
		if (gainsAid(phase)) {
			determineAid();
		}
		
		// the king's reward
		if (isReward(phase)) {
			determineReward();
		}
		
		if (isEnvoy(phase)) {
			recallEnvoy();
			determineEnvoy();
		}
		
		// productive season
		if (isProductiveSeason(phase)) {
			performProductiveSeason();
		}
		
		if (isRecruit(phase)) {
			offerRecruit(round, phase);
		}
		
		if (isBattle(phase)) {
			rollKingsReinforcements();
			performBattle(round);
			resetSoldiers();
		}
		
		if (usesAid(phase)) {
			recallAid();
		}

		for (int turn = 0; turn < players; turn++) {
			int playerAtTurnOrder = getPlayerAtTurnOrder(turn);
			performTurn(round, phase, playerAtTurnOrder);
		}
		p.log("\n" + "Phase " + truePhase + " over." + "\n");
	}
	
	@Override
	public void onGoodsSelected(RewardTotal total) {
		final int round = this.round;
		final int phase = this.phase;
		final int player = this.player;
		playersStuff[player].receiveReward(total);
	}

	// LOOP TURNS within A PHASE/SEASON
	private void performTurn(int round, int phase, int player) {
		String phaseName = PhaseHandler.getPhase(phase + 1).getName();
		p.log("\n" + p.getPlayer(player) + "\n" + p.getRound(round) + "\n"
				+ phaseName);
		
		if (rolls == null || !isProductiveSeason(phase)) {
			return;
		}
		
		// 
		Roll myRoll = null;
		for (int i = 0; i < rolls.length; i++) {
			if (rolls[i].getPlayer() == player) {
				myRoll = rolls[i];
			}
		}
		if (myRoll == null) {
			return;
		}
		
		rememberRoundPhasePlayer(round, phase, player);
		getProxy(player).onAdvisorChoice(myRoll);
	}
	
	int round; int phase; int player;
	private void rememberRoundPhasePlayer(int round, int phase, int player) {
		this.round = round;
		this.phase = phase;
		this.player = player;
	}
	
	@Override
	public void onRewardChoiceSelected(Advisor advisor, RewardChoice rewardChoice) {
		final int round = this.round;
		final int phase = this.phase;
		final int player = this.player;
		if (rewardChoice == null) {
			//player has skipped the turn
			p.log("Player " + player + " has skipped the turn.");
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
			p.log("Player " + (player+1) + " received the reward [" + rewardChoice.getHumanReadableReward() + "] from " + advisor.getName() + "(" + advisor.getOrdinal() + ").");
			RewardTotal reward = rewardChoice.getReward();
			myStuff.receiveReward(reward);
			// TODO handle peek and soldiers
			if (reward.getSoldiers() > 0) {
				board.increaseSoldiers(player, reward.getSoldiers());
			}
			if (reward.isPeek()) {
				// TODO player can peek
			}
		} else {
			p.log("Player " + (player + 1) + " skipped the turn, unable to pay the gift cost.");
		}
		
		//TODO build a building
		p.log(myStuff.toString());
	}
	
	// USES IDENTICAL LOGIC TO determineEnvoy FOR MOST
	void determineAid() {
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
			int leastResourcesCount = 31000;
			List<Long> playersWithLeastResources = new ArrayList<>(5);
			for (PlayerStuff p : playersStuff) {
				int count = p.countResources();
				if (count < leastResourcesCount) {
					leastResourcesCount = count;
				}
			}
			
			for (PlayerStuff p : playersStuff) {
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
					rememberRoundPhasePlayer(round, phase, player);
					proxies[player].onChooseGoods(1);
					complete = true;
				}
			}
			
			if (!complete) {
				// award to fewest resources
				int player = playersWithLeastBuildingsAndLeastResources.get(0).intValue();
				playersStuff[player].gainAid();
				complete = true;
			}
		}
		
		if (!complete) {
			// award to fewest buildings
			int player = playersWithLeastBuildings.get(0).intValue();
			playersStuff[player].gainAid();
			complete = true;
		}
	}
	
	void recallAid() {
		for (PlayerStuff p : playersStuff) {
			if (p.hasAid()) {
				p.useAid();
			}
		}
	}
	
	void determineReward() {
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
				p.gainPoints(1);
			}
		}
	}
	
	void recallEnvoy() {
		for (PlayerStuff p : playersStuff) {
			if (p.hasEnvoy()) {
				p.useEnvoy();
			}
		}
	}
	
	// USES IDENTICAL LOGIC TO determineAid FOR MOST
	void determineEnvoy() {
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
			int leastResourcesCount = 31000;
			List<Long> playersWithLeastResources = new ArrayList<>(5);
			for (PlayerStuff p : playersStuff) {
				int count = p.countResources();
				if (count < leastResourcesCount) {
					leastResourcesCount = count;
				}
			}
			
			for (PlayerStuff p : playersStuff) {
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
			}
			
			if (!complete) {
				// award to fewest resources
				int player = playersWithLeastBuildingsAndLeastResources.get(0).intValue();
				playersStuff[player].gainEnvoy();
				complete = true;
			}
		}
		
		if (!complete) {
			// award to fewest buildings
			int player = playersWithLeastBuildings.get(0).intValue();
			playersStuff[player].gainEnvoy();
			complete = true;
		}
	}
	
	void performProductiveSeason() {
		// TODO add the "reserve advisors / 2-player only" rule
		rolls = rollDiceForAllPlayers();
		updateTurnOrder(rolls);
		for (int turn = 0; turn < players; turn++) {
			if (rolls != null) {
				p.log("          roll || " + rolls[turn].toString());
				p.log("");
			} else {
				p.log("          no-roll");
				p.log("");
			}
		}
	}
	
	void offerRecruit(int round, int phase) {
		for (PlayerStuff p : playersStuff) {
			int player = p.getPlayerId();
			rememberRoundPhasePlayer(round, phase, player);
			getProxy(player).onRecruitOption(p);
		}
	}
	
	@Override
	public void onSoldiersRecruited(int count) {
		final int round = this.round;
		final int phase = this.phase;
		final int player = this.player;
		board.increaseSoldiers(player, count);
	}
	
	void rollKingsReinforcements() {
		int reinforcements = Roll.rollTheDice(0, 1, 6).getRoll();
		p.log("The King sends (" + reinforcements + ") to fight with your city.");
		board.addKingsReinforcements(reinforcements);
	}
	
	void performBattle(int round) {
		// TODO consider removing building bonuses and recalculating, to be safe
		int year = round + 1;
		EnemyCard enemy = enemyDeck.getCard(year);
		int strengthToBeat = enemy.getStrength();
		for (int player = 0; player < players; player++) {
			int soldiers = board.getSoldiersFor(player);
			if (soldiers > strengthToBeat) {
				p.log("Player " + (player+1) + " won the fight against " + enemy.getName() + "(" + enemy.getStrength() + ") with " + soldiers + "!");
				battleVictory(player, enemy);
			} else if (soldiers == strengthToBeat) {
				p.log("Player " + (player+1) + " stalemated the fight against " + enemy.getName() + "(" + enemy.getStrength() + ") with " + soldiers + ".");
				battleDraw(player, enemy);
			} else {
				p.log("Player " + (player+1) + " lost the fight against " + enemy.getName() + "(" + enemy.getStrength() + ") with " + soldiers + ".");
				battleLose(player, enemy);
			}
		}
	}
	
	void battleVictory(int player, EnemyCard enemy) {
		// TODO method
	}
	
	void battleDraw(int player, EnemyCard enemy) {
		// TODO method	
	}
	
	void battleLose(int player, EnemyCard enemy) {
		// TODO method
	}
	
	void resetSoldiers() {
		board.resetSoldiers();
		// TODO add for buildings
	}

	void complete() {
		p.log("Calculate scores: Game Over");
	}

	//
	//
	// **************************************************
	// HUMAN UNDERSTANDABLE LOGIC METHODS

	private boolean isProductiveSeason(int phase) {
		phase++; // real phase count
		return PhaseHandler.getPhase(phase).isProductive();
	}
	
	private boolean gainsAid(int phase) {
		phase++; // real phase count
		return PhaseHandler.getPhase(phase).gainsAid();
	}
	
	private boolean usesAid(int phase) {
		phase++; // real phase count
		return PhaseHandler.getPhase(phase).usesAid();
	}

	private boolean isReward(int phase) {
		phase++; // real phase count
		return PhaseHandler.getPhase(phase).isReward();
	}
	
	private boolean isRecruit(int phase) {
		phase++; // real phase count
		return PhaseHandler.getPhase(phase).isRecruit();
	}
	
	private boolean isEnvoy(int phase) {
		phase++; // real phase count
		return PhaseHandler.getPhase(phase).isEnvoy();
	}
	
	private boolean isBattle(int phase) {
		phase++; // real phase count
		return PhaseHandler.getPhase(phase).isBattle();
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
		if (playersStuff[player].hasAid()) { 
			p.log("Player " + player + " has the King's aid. +1 bonus die");
			bonusDieCount++; 
		}
		if (playersStuff[player].hasFarms()) {
			p.log("Player " + player + " has Farms. +1 bonus die");
			bonusDieCount++;
		}
		// roll 'em
		return Roll.rollTheDice(player, PlayerStuff.PLAYER_DICE_COUNT + bonusDieCount, PlayerStuff.PLAYER_DICE_SIDES);
	}
	
	private PlayerProxy getProxy(int player) {
		return proxies[player];
	}
}
