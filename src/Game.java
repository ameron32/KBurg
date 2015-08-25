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

	Logger printer = Printer.get(); // TODO remove printer

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
		printer.log(enemyDeck.toString() + "\n"); // TODO wrong place for a review of enemy cards
		
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
		printer.log("New Game: " + players + " players");
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
		printer.log("Begin Year: " + year
				+ " ******************************************************"
				+ " ******************************************************");
		int numberOfPhases = PhaseHandler.getPhaseCount();
		for (int phase = 0; phase < numberOfPhases; phase++) {
			performPhase(round, phase);
		}
		printer.log("Year " + year + " over." + "\n\n");
	}

	// LOOP PHASES/SEASONS within A ROUND/YEAR
	private void performPhase(int round, int phase) {
		int truePhase = phase + 1;
		board.incrementPhase();
		printer.log("\n" + "Phase " + truePhase + " (" +
				PhaseHandler.getPhase(truePhase).getName() + ")" +
				" start. **************************");
		
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
			// roll dice and adjust turn order
			updateTurnOrder();
//			_displayRolls();

			for (int turn = 0; turn < players; turn++) {
				int playerAtTurnOrder = getPlayerAtTurnOrder(turn);
				// influence the King's Advisors
				determineKingsAdvisorsPerPlayerAndHandleRewards(round, phase, turn, playerAtTurnOrder);
			}
			
			chooseGoods(round, phase);			
			offerConstructBuildings(round, phase);
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
		
		printer.log("\n" + "Phase " + truePhase + " over." + "\n");
	}

	// LOOP TURNS within A PHASE/SEASON
	private void determineKingsAdvisorsPerPlayerAndHandleRewards(int round, int phase, int turn, int player) {
		// display the basic turn info
//		_displayGenericTurnInformation(round, phase, player);
		
		if (rolls == null || !isProductiveSeason(phase)) {
			return;
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
		
		printer.log("\n          roll || " + rolls[turn].toString());
		rememberRoundPhasePlayer(round, phase, player);
		getProxy(player).onAdvisorChoice(myRoll);
	}

	private void _displayGenericTurnInformation(int round, int phase, int player) {
		String phaseName = PhaseHandler.getPhase(phase + 1).getName();
		printer.log("\n" + printer.getPlayer(player) + " / " + printer.getRound(round) + " / "
				+ phaseName);
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
			printer.log("Player " + (player+1) + " has skipped the turn.");
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
			printer.log("Player " + (player+1) + " received the reward [" + rewardChoice.getHumanReadableReward() + "] from " + advisor.getName() + "(" + advisor.getOrdinal() + ").");
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
			printer.log("Player " + (player + 1) + " skipped the turn, unable to pay the gift cost.");
		}
		
		//TODO build a building
		printer.log(myStuff.toHumanReadableString());
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
					printer.log("Player " + (player+1) + " received the King's aid of 1 resource.");
					rememberRoundPhasePlayer(round, phase, player);
					playersStuff[player].gainUnchosenResources(1);
					complete = true;
				}
			}
			
			if (!complete) {
				// award to fewest resources
				int player = playersWithLeastBuildingsAndLeastResources.get(0).intValue();
				printer.log("Player " + (player+1) + " received the King's aid of 1 bonus die in Spring.");
				playersStuff[player].gainAid();
				complete = true;
			}
		}
		
		if (!complete) {
			// award to fewest buildings
			int player = playersWithLeastBuildings.get(0).intValue();
			printer.log("Player " + (player+1) + " received the King's aid of 1 bonus die in Spring.");
			playersStuff[player].gainAid();
			complete = true;
		}
	}
	
	void recallAid() {
		for (PlayerStuff p : playersStuff) {
			if (p.hasAid()) {
				p.useAid();
				printer.log("\nPlayer " + (p.getPlayerId()+1) + " returns the King's aid die.");
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
				printer.log("Player " + (p.getPlayerId()+1) + " receives the King's Reward of 1 victory point.");
				p.gainPoints(1);
			}
		}
	}
	
	void recallEnvoy() {
		for (PlayerStuff p : playersStuff) {
			if (p.hasEnvoy()) {
				p.useEnvoy();
				printer.log("Player " + (p.getPlayerId()+1) + " has not used the King's envoy. The envoy is returned to the board.");
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
				printer.log("Player " + (player+1) + " has received the King's envoy, with least resources.");
				playersStuff[player].gainEnvoy();
				complete = true;
			}
		}
		
		if (!complete) {
			// award to fewest buildings
			int player = playersWithLeastBuildings.get(0).intValue();
			printer.log("Player " + (player+1) + " has received the King's envoy.");
			playersStuff[player].gainEnvoy();
			complete = true;
		}
	}
	
	void updateTurnOrder() {
		// TODO add the "reserve advisors / 2-player only" rule
		rolls = rollDiceForAllPlayers();
		// TODO allow for Statue and/or Chapel
		updateTurnOrder(rolls);		
	}
	
	void chooseGoods(int round, int phase) {
		for (int player = 0; player < players; player++) {
			rememberRoundPhasePlayer(round, phase, player);
			getProxy(player).onChooseGoods(playersStuff[player].countUnchosenResources());
		}
	}
	
	@Override
	public void onGoodsSelected(RewardTotal total) {
		final int round = this.round;
		final int phase = this.phase;
		final int player = this.player;
		playersStuff[player].clearUnchosenResources();
		playersStuff[player].receiveReward(total);
	}
	
	void offerConstructBuildings(int round, int phase) {
		for (int player = 0; player < players; player++) {
			rememberRoundPhasePlayer(player, player, player);
			getProxy(player).onBuildOption(playersStuff[player]);
		}
	}
	
	@Override
	public void onBuild(List<ProvinceBuilding> buildings) {
		final int round = this.round;
		final int phase = this.phase;
		final int player = this.player;
		if (buildings == null || buildings.isEmpty()) {
			printer.log("Player " + (player+1) + " did not build a building.");
			return;
		}
		if (buildings.size() == 1) {
			ProvinceBuilding building = buildings.get(0);
			printer.log("Player " + (player+1) + " built a " + building.getName() + "!");
			playersStuff[player].gainPoints(building.getPoints());
			return;
		}
		if (buildings.size() > 1) {
			printer.log("Player " + (player+1) + " built:\n");
			for (ProvinceBuilding building : buildings) {
				printer.log("a " + building.getName() + "\n");
				playersStuff[player].gainPoints(building.getPoints());
			}
		}
	}
		
	void _displayRolls() {
		// TODO add the "reserve advisors / 2-player only" rule
		for (int turn = 0; turn < players; turn++) {
			if (rolls != null) {
				printer.log("\n          roll || " + rolls[turn].toString());
			} else {
				printer.log("\n          no-roll");
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
		printer.log("Player " + (player+1) + " recruited [" + count + "] soldier(s).");
		board.increaseSoldiers(player, count);
	}
	
	void rollKingsReinforcements() {
		int reinforcements = Roll.rollTheDice(0, 1, 6).getRoll();
		printer.log("The King sends (" + reinforcements + ") to fight with the cities.");
		board.addKingsReinforcements(reinforcements);
	}
	
	void performBattle(int round) {
		// TODO consider removing building bonuses and recalculating, to be safe
		int year = round + 1;
		EnemyCard enemy = enemyDeck.getCard(year);
		printer.log("Enemy Card revealed: " + enemy.toString());
		int strengthToBeat = enemy.getStrength();
		for (int player = 0; player < players; player++) {
			grantBuildingSoldierBoost(player);
			int soldiers = board.getSoldiersFor(player);
			if (enemy.isGoblin() &&	playersStuff[player].hasBarricade()) {
				printer.log("Player " + (player+1) + " has a Barricade against Goblins. +1");
				soldiers++;
			}
			if (enemy.isZombie() && playersStuff[player].hasPalisade()) {
				printer.log("Player " + (player+1) + " has a Palisade against Zombies. +1");
				soldiers++;
			}
			if (enemy.isDemon() && playersStuff[player].hasChurch()) {
				printer.log("Player " + (player+1) + " has a Church against Demons. +1");
				soldiers++;
			}
				
			if (soldiers > strengthToBeat) {
				printer.log("Player " + (player+1) + " won the fight against " + enemy.getName() + "(" + enemy.getStrength() + ") with " + soldiers + "!\n");
				battleVictory(player, enemy);
			} else if (soldiers == strengthToBeat) {
				if (playersStuff[player].hasStoneWall()) {
					printer.log("Player " + (player+1) + " stalemated the fight against " + enemy.getName() + "(" + enemy.getStrength() + ") with " + soldiers + ", but won because of having a Stone Wall!\n");
					battleVictory(player, enemy);
				} else {
					printer.log("Player " + (player+1) + " stalemated the fight against " + enemy.getName() + "(" + enemy.getStrength() + ") with " + soldiers + ".\n");
					battleDraw(player, enemy);
				}
			} else {
				printer.log("Player " + (player+1) + " lost the fight against " + enemy.getName() + "(" + enemy.getStrength() + ") with " + soldiers + ".\n");
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
		printer.log("Calculate scores: Game Over");
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
			printer.log("\nPlayer " + (player+1) + " has the King's aid. +1 bonus die");
			bonusDieCount++; 
		}
		if (playersStuff[player].hasFarms()) {
			printer.log("Player " + (player+1) + " has Farms. +1 bonus die");
			bonusDieCount++;
		}
		// roll 'em
		return Roll.rollTheDice(player, PlayerStuff.PLAYER_DICE_COUNT + bonusDieCount, PlayerStuff.PLAYER_DICE_SIDES);
	}
		
	private void grantBuildingSoldierBoost(int player) {
		PlayerStuff stuff = playersStuff[player];
		if (stuff.hasGuardTower()) {
			printer.log("Player " + (player+1) + " has a Guard Tower. +1");
			board.increaseSoldiers(player, 1);
		}
		if (stuff.hasBlacksmith()) {
			printer.log("Player " + (player+1) + " has a Blacksmith. +1");
			board.increaseSoldiers(player, 1);
		}
		if (stuff.hasPalisade()) {
			printer.log("Player " + (player+1) + " has a Palisade. +1");
			board.increaseSoldiers(player, 1);
		}
		if (stuff.hasStoneWall()) {
			printer.log("Player " + (player+1) + " has a Stone Wall. +1");
			board.increaseSoldiers(player, 1);
		}
		if (stuff.hasFortress()) {
			printer.log("Player " + (player+1) + " has a Fortress. +1");
			board.increaseSoldiers(player, 1);
		}
		if (stuff.hasChurch()) {
			printer.log("Player " + (player+1) + " has a Church. +0");
			board.increaseSoldiers(player, 0);
		}
		if (stuff.hasBarricade()) {
			printer.log("Player " + (player+1) + " has a Barricade. +0");
			board.increaseSoldiers(player, 0);
		}
		if (stuff.hasWizardsGuild()) {
			printer.log("Player " + (player+1) + " has a Wizards' Guild. +1");
			board.increaseSoldiers(player, 2);
		}
		if (stuff.hasFarms()) {
			printer.log("Player " + (player+1) + " has Farms. -1");
			board.increaseSoldiers(player, -1);
		}
	}
	
	private PlayerProxy getProxy(int player) {
		return proxies[player];
	}
}
