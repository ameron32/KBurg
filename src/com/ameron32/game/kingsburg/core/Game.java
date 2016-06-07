package com.ameron32.game.kingsburg.core;
import com.ameron32.game.kingsburg.core.bot.PlayerProxyListener;
import com.ameron32.game.kingsburg.core.next.GameLoop;
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
public class Game implements GameLoop {

	private Logger printer = Printer.get(); // TODO remove printer

	//
	//
	// **************************************************
	// GETTERS AND SETTERS
	//<editor-fold desc="GettersAndSetters">
	// proxy interfaces to resolve user/player choices
	public PlayerStuff getPlayerStuff(int player) {
		return stateManager.getPlayerStuff(player);
	}

	public Board getBoard() {
		return stateManager.getBoard();
	}

	private PlayerProxy[] proxies;
	private PlayerProxy getProxy(int player) {
		getPlayerStuff(player).pullSynchronize();
		return proxies[player];
	}
	public void setPlayerProxy(int player, PlayerProxy proxy) {
		proxies[player] = proxy;
	}

	private StateManager stateManager;
	private StateManager getStateManager() {
		return stateManager;
	}

	// simple enemyDeck
	private EnemyDeck enemyDeck;
	private EnemyDeck getEnemyDeck() {
		return enemyDeck;
	}

	// battle resolver
	private BattleResolver battleResolver;
	private BattleResolver getBattleResolver() {
		return battleResolver;
	}

	// game resolver
	private GameFinisher gameFinisher;
	private GameFinisher getGameFinisher() {
		return gameFinisher;
	}

	// stage resolver
	private StageResolver stageResolver;
	private StageResolver getStageResolver() {
		return stageResolver;
	}

	// phases
	private PhaseHandler phaseHandler;
	private PhaseHandler getPhaseHandler() {
		return phaseHandler;
	}

	// number of rounds
	private int rounds;
	private int getRounds() {
		return rounds;
	}
	private void setRounds(int rounds) {
		this.rounds = rounds;
	}

	public PlayerProxyListener playerProxyListener = new PlayerProxyListener() {
		@Override
		public void onAdvisorGiftSelection(int player, Advisor advisor, RewardChoice rewardChoice) {
			// TODO store the decision

			// perform the decision
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
			// TODO store the decision

			// perform the decision
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
			// TODO store the decision

			// perform the decision
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
			// TODO store the decision

			// perform the decision
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
			// TODO store the decision

			// perform the decision
			if (!total.isEmpty()) {
				printer.log(player, "converted unchosen resources into goods.");
			}
			getPlayerStuff(player).clearUnchosenResources();
			getPlayerStuff(player).receiveReward(total);
			getPlayerStuff(player).pushUpdate();
		}

		@Override
		public void onLossesSelection(int player, Cost total) {
			// TODO store the decision

			// perform the decision
			if (!total.isEmpty()) {
				printer.log(player, "paid unchosen losses from goods.");
			}
			getPlayerStuff(player).clearUnpaidDebts();
			getPlayerStuff(player).payCost(total);
			getPlayerStuff(player).pushUpdate();
		}

		@Override
		public void onBuild(int player, List<ProvinceBuilding> buildings) {
			// TODO store the decision

			// perform the decision
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
			// TODO store the decision

			// perform the decision
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
	};
	public PlayerProxyListener getPlayerProxyListener() {
		return playerProxyListener;
	}
	//</editor-fold>



	//
	//
	// **************************************************
	// GAME LOOP / LIFECYCLE
	//<editor-fold desc="Lifecycle">
	public void setup(int players, int phases, int rounds, Board board) {
		this.stateManager = new StateManager();
		stateManager.setPlayers(players);
		this.proxies = new PlayerProxy[players];
		this.phaseHandler = new PhaseHandler();
		this.battleResolver = new BattleResolver(printer, stateManager);
		this.gameFinisher = new GameFinisher(printer, stateManager);
		setRounds(rounds);
		setTurnOrder(new int[players]);
		stateManager.setBoard(board);

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
		getStageResolver().setAccess(round, phase, stage);
		switch (stage) {
			case START_PHASE:
				getStageResolver().performStartPhase();
				break;

			case ROLL_AND_REROLL:
				getStageResolver().performRollAndReRoll();
				break;

			case CHOOSE_ADVISORS:
				getStageResolver().performChooseAdvisors();
				break;

			case SELECT_RESOURCES_AND_BUILD:
				getStageResolver().performSelectResourcesAndBuild();
				break;

			case TOWNHALL_OPTION_AND_RECRUIT_SOLDIERS:
				getStageResolver().performTownhallOptionAndRecruitSoldiers();
				break;

			case CHOOSE_DEFEAT_LOSSES:
				getStageResolver().performChooseDefeatLosses();
				break;

			case END_PHASE:
				getStageResolver().performEndPhase();
				break;
		}
	}

	public void complete() {
		for (PlayerStuff stuff : getStateManager().getAllPlayersStuff()) {
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

		for (int player = 0; player < getStateManager().getPlayers(); player++) {
			PlayerStuff stuff = getPlayerStuff(player);
			int score = stuff.countPoints();
			printer.log("Player " + (player+1) + " scored " + score + " points.");
		}

		boolean complete = false;
		int mostPointsCount = 0;
		List<Long> playersWithMostPoints = new ArrayList<>(5);
		for (PlayerStuff p : getStateManager().getAllPlayersStuff()) {
			int count = p.countPoints();
			int playerId = p.getPlayerId();
			if (count > mostPointsCount) {
				mostPointsCount = count;
			}
		}
		for (PlayerStuff p : getStateManager().getAllPlayersStuff()) {
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
			getGameFinisher().playerWins(playerId, mostPointsCount);
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
				getGameFinisher().playerWins(playerId, mostPointsCount, mostResourcesCount);
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
					getGameFinisher().playerWins(playerId, mostPointsCount, mostResourcesCount, mostBuildingsCount);
					complete = true;
				}

				// multiple players with most points and most resources AND most buildings
				// everyone wins/ties
				if (playerCountMostPointsAndMostResourcesAndMostBuildings > 1 && !complete) {
					// everyone wins
					for (Long playerId : playersWithMostPointsAndMostResourcesAndMostBuildings) {
						int player = playerId.intValue();
						getGameFinisher().playersWin(player, mostPointsCount, mostResourcesCount, mostBuildingsCount);
						complete = true;
					}
				}
			}
		}
	}
	//</editor-fold>

}
