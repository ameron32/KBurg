package com.ameron32.game.kingsburg.core;

import com.ameron32.game.kingsburg.core.state.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by klemeilleur on 6/7/2016.
 */
public class StageResolver {

    private final StateManager stateManager;
    private final Logger printer;
    private final PhaseHandler phaseHandler;
    private final BattleResolver battleResolver;
    private BattleResolver getBattleResolver() {
        return battleResolver;
    }

    // stateful
    private int round;
    private int phase;
    private Stage stage;
    private int truePhase;
    private Phase xPhase;

    private StateManager getStateManager() {
        return stateManager;
    }

    private PhaseHandler getPhaseHandler() {
        return phaseHandler;
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

    // most recent player rolls in order of player number
    private Roll[] rolls;
    private Roll[] getRolls() {
        return rolls;
    }
    private void setRolls(Roll[] rolls) {
        this.rolls = rolls;
    }



    public StageResolver(Logger printer, StateManager stateManager, PhaseHandler phaseHandler, BattleResolver battleResolver) {
        this.printer = printer;
        this.stateManager = stateManager;
        this.phaseHandler = phaseHandler;
        this.battleResolver = battleResolver;
    }

    public void setAccess(int round, int phase, Stage stage) {
        this.round = round;
        this.phase = phase;
        this.stage = stage;
        int truePhase = phase + 1;
        Phase xPhase = getPhaseHandler().getPhase(truePhase);
        setPhase(truePhase, xPhase);
    }

    private void setPhase(int truePhase, Phase xPhase) {
        this.truePhase = truePhase;
        this.xPhase = xPhase;
    }

    public void performStartPhase() {
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

        getStateManager().getBoard().incrementStage();
    }

    public void performRollAndReRoll() {
        // productive season
        if (isProductiveSeason(phase)) {
            for (PlayerStuff stuff : getStateManager().getAllPlayersStuff()) {
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

        getStateManager().getBoard().incrementStage();
    }

    public void performChooseAdvisors() {
        if (isProductiveSeason(phase)) {
            if (!playersHaveUnusedDice()) {
                getStateManager().getBoard().incrementStage();
                return;
            }
            int turn = getStateManager().getBoard().getCurrentTurn();
            int playerAtTurnOrder = getPlayerAtTurnOrder(turn);

            // influence the King's Advisors
            // PLAYER INTERACTION BREAK
            // SEQUENTIAL
            influenceOneAdvisor(round, phase, turn, playerAtTurnOrder);
            getStateManager().getBoard().incrementTurn();
        } else {
            getStateManager().getBoard().incrementStage();
        }
    }

    public void performSelectResourcesAndBuild() {
        if (isProductiveSeason(phase)) {
            // POSSIBLE PLAYER INTERACTION BREAK
            // CONCURRENT
            chooseGoods(round, phase);
            offerConstructBuildings(round, phase);
        }
        getStateManager().getBoard().incrementStage();
    }

    public void performTownhallOptionAndRecruitSoldiers() {
        if (isProductiveSeason(phase)) {
            if (getPhaseHandler().isSummer(xPhase)) {
                for (PlayerStuff stuff : getStateManager().getAllPlayersStuff()) {
                    int player = stuff.getPlayerId();
                    if (stuff.hasInn()) {
                        stuff.gainPlus2(1);
                        printer.log(player, "uses the Inn to gain a +2 token.");
                    }
                }
            }

            for (PlayerStuff stuff : getStateManager().getAllPlayersStuff()) {
                int player = stuff.getPlayerId();
                if (stuff.hasEmbassy()) {
                    stuff.gainPoints(1);
                    printer.log(player, "uses Embassy to gain +1 victory point!");
                }
            }

            for (PlayerStuff stuff : getStateManager().getAllPlayersStuff()) {
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
        getStateManager().getBoard().incrementStage();
    }

    public void performChooseDefeatLosses() {
        // battle stage
        if (isBattle(phase)) {
            rollKingsReinforcements();
            performBattle(round);
            resetSoldiers();

            // POSSIBLE PLAYER INTERACTION BREAK
            // CONCURRENT
            chooseLosses(round, phase);
        }
        getStateManager().getBoard().incrementStage();
    }

    public void performEndPhase() {
        // return the king's aid
        if (isLosesAid(phase)) {
            recallAid();
        }

        // at the end of a phase, restore all advisors to an unreserved state
        getStateManager().getBoard().resetAdvisors();

        printer.log("\n" + "Phase " + truePhase + " over." + "\n");
        getStateManager().getBoard().incrementPhase();
    }



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


    //<editor-fold desc="Determine and Recall">
    // USES IDENTICAL LOGIC TO determineEnvoy FOR MOST
    private void determineAid() {
        boolean complete = false;
        int leastBuildingsCount = 31000;
        List<Long> playersWithLeastBuildings = new ArrayList<>(5);
        for (PlayerStuff p : getStateManager().getAllPlayersStuff()) {
            int count = p.countBuildings();
            if (count < leastBuildingsCount) {
                leastBuildingsCount = count;
            }
        }

        for (PlayerStuff p : getStateManager().getAllPlayersStuff()) {
            if (p.countBuildings() == leastBuildingsCount) {
                playersWithLeastBuildings.add(new Long(p.getPlayerId()));
            }
        }

        if (playersWithLeastBuildings.size() > 1) {
            int leastBuildingsSize = playersWithLeastBuildings.size();
            int leastResourcesCount = 31000;
            List<Long> playersWithLeastResources = new ArrayList<>(leastBuildingsSize);
            for (int i = 0; i < leastBuildingsSize; i++) {
                PlayerStuff p = getStateManager().getPlayerStuff(playersWithLeastBuildings.get(i).intValue());
                int count = p.countResources();
                if (count < leastResourcesCount) {
                    leastResourcesCount = count;
                }
            }

            for (int i = 0; i < leastBuildingsSize; i++) {
                PlayerStuff p = getStateManager().getPlayerStuff(playersWithLeastBuildings.get(i).intValue());
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
                    getStateManager().getPlayerStuff(player).gainUnchosenResources(1);
                    complete = true;
                }
            }

            if (!complete) {
                // award to fewest resources
                int player = playersWithLeastBuildingsAndLeastResources.get(0).intValue();
                printer.log(player, "received the King's aid of 1 bonus die in Spring.");
                getStateManager().getPlayerStuff(player).gainAid();
                complete = true;
            }
        }

        if (!complete) {
            // award to fewest buildings
            int player = playersWithLeastBuildings.get(0).intValue();
            printer.log(player, "received the King's aid of 1 bonus die in Spring.");
            getStateManager().getPlayerStuff(player).gainAid();
            complete = true;
        }
    }

    private void recallAid() {
        for (PlayerStuff p : getStateManager().getAllPlayersStuff()) {
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
        for (PlayerStuff p : getStateManager().getAllPlayersStuff()) {
            int count = p.countBuildings();
            if (count > mostBuildingsCount) {
                mostBuildingsCount = count;
            }
        }

        for (PlayerStuff p : getStateManager().getAllPlayersStuff()) {
            if (p.countBuildings() == mostBuildingsCount) {
                playersWithMostBuildings.add(new Long(p.getPlayerId()));
                int player = p.getPlayerId();
                printer.log(player, "receives the King's Reward of 1 victory point.");
                p.gainPoints(1);
            }
        }
    }

    private void recallEnvoy() {
        for (PlayerStuff p : getStateManager().getAllPlayersStuff()) {
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
        for (PlayerStuff p : getStateManager().getAllPlayersStuff()) {
            int count = p.countBuildings();
            if (count < leastBuildingsCount) {
                leastBuildingsCount = count;
            }
        }

        for (PlayerStuff p : getStateManager().getAllPlayersStuff()) {
            if (p.countBuildings() == leastBuildingsCount) {
                playersWithLeastBuildings.add(new Long(p.getPlayerId()));
            }
        }

        if (playersWithLeastBuildings.size() > 1) {
            int leastBuildingsSize = playersWithLeastBuildings.size();
            int leastResourcesCount = 31000;
            List<Long> playersWithLeastResources = new ArrayList<>(leastBuildingsSize);
            for (int i = 0; i < leastBuildingsSize; i++) {
                PlayerStuff p = getStateManager().getPlayerStuff(playersWithLeastBuildings.get(i).intValue());
                int count = p.countResources();
                if (count < leastResourcesCount) {
                    leastResourcesCount = count;
                }
            }

            for (int i = 0; i < leastBuildingsSize; i++) {
                PlayerStuff p = getStateManager().getPlayerStuff(playersWithLeastBuildings.get(i).intValue());
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
                getStateManager().getPlayerStuff(player).gainEnvoy();
                complete = true;
            }
        }

        if (!complete) {
            // award to fewest buildings
            int player = playersWithLeastBuildings.get(0).intValue();
            printer.log(player, "has received the King's envoy.");
            getStateManager().getPlayerStuff(player).gainEnvoy();
            complete = true;
        }
    }
    //</editor-fold>


    //<editor-fold desc="Other">
    private Roll[] rollDiceForAllPlayers() {
        Roll[] roll = new Roll[getStateManager().getPlayers()]; // each players roll
        for (int player = 0; player < getStateManager().getPlayers(); player++) {
            Roll r = _rollOnePlayer(player);
            roll[player] = r;
        }
        return roll;
    }

    // modify to proper roll
    private Roll _rollOnePlayer(int player) {
        // determine number of dice
        int bonusDieCount = PlayerStuff.PLAYER_STARTING_BONUS_DIE_COUNT;
        if (getStateManager().getPlayerStuff(player).hasAid()) {
            printer.log(player, "has the King's aid. +1 bonus die");
            bonusDieCount++;
        }
        if (getStateManager().getPlayerStuff(player).hasFarms()) {
            printer.log(player, "has Farms. +1 bonus die");
            bonusDieCount++;
        }
        // roll 'em
        String randomRequestId = "rollOnePlayer" + getStateManager().getBoard().getCurrentStageAsString() + player;
        return Roll.rollTheDice(randomRequestId, player, PlayerStuff.PLAYER_DICE_COUNT + bonusDieCount, PlayerStuff.PLAYER_DICE_SIDES);
    }

    private void updateTurnOrder(Roll[] rolls) {
        // determine new rankings
        List<Roll> rollList = Arrays.asList(rolls);
        Collections.sort(rollList, new RollComparator(getTurnOrder()));

        // apply new turn order to master turn order
        for (int position = 0; position < getStateManager().getPlayers(); position++) {
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
            for (int i = 0; i < getStateManager().getPlayers(); i++) {
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
            PlayerStuff stuff = getStateManager().getPlayerStuff(player);
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
        PlayerStuff stuff = getStateManager().getPlayerStuff(player);
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
        String randomRequestId = "rollKingsReinforcements" + getStateManager().getBoard().getCurrentYear();
        int reinforcements = Roll.rollTheDice(randomRequestId, 0, 1, 6).getUnusedTotal();
        printer.log("The King sends (" + reinforcements + ") reinforcements to fight alongside your soldiers.");
        getStateManager().getBoard().addKingsReinforcements(reinforcements);
    }

    private void performBattle(int round) {
        // TODO consider removing building bonuses and recalculating, to be safe
        int year = round + 1;
        EnemyCard enemy = getEnemyDeck().getCard(year);
        printer.log("\nEnemy Card revealed: " + enemy.toString());
        int strengthToBeat = enemy.getStrength();
        int winners = 0;
        int[] cityForces = new int[5];
        for (int player = 0; player < getStateManager().getPlayers(); player++) {
            printer.log("");
            grantBuildingSoldierBoost(player);
            int soldiers = getStateManager().getBoard().getSoldiersFor(player);
            if (enemy.isGoblin() &&	getStateManager().getPlayerStuff(player).hasBarricade()) {
                printer.log(player, "has a Barricade against Goblins. +1");
                soldiers++;
            }
            if (enemy.isZombie() && getStateManager().getPlayerStuff(player).hasPalisade()) {
                printer.log(player, "has a Palisade against Zombies. +1");
                soldiers++;
            }
            if (enemy.isDemon() && getStateManager().getPlayerStuff(player).hasChurch()) {
                printer.log(player, "has a Church against Demons. +1");
                soldiers++;
            }

            cityForces[player] = soldiers;

            if (soldiers > strengthToBeat) {
                printer.log(player, "won the fight against " + enemy.getName() + "(" + enemy.getStrength() + ") with " + soldiers + "!");
                winners++;
                getBattleResolver().battleVictory(player, enemy);
            } else if (soldiers == strengthToBeat) {
                if (getStateManager().getPlayerStuff(player).hasStoneWall()) {
                    printer.log(player, "stalemated the fight against " + enemy.getName() + "(" + enemy.getStrength() + ") with " + soldiers + ", but won because of having a Stone Wall!");
                    winners++;
                    getBattleResolver().battleVictory(player, enemy);
                } else {
                    printer.log(player, "stalemated the fight against " + enemy.getName() + "(" + enemy.getStrength() + ") with " + soldiers + ".");
                    getBattleResolver().battleDraw(player, enemy);
                }
            } else {
                printer.log(player, "lost the fight against " + enemy.getName() + "(" + enemy.getStrength() + ") with " + soldiers + ".");
                getBattleResolver().battleLose(player, enemy);
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
                    PlayerStuff stuff = getStateManager().getPlayerStuff(player);
                    stuff.gainPoints(1);
                    printer.log(player, "gained +1 victory point for having the most soldiers!");
                }
            }
        }
    }

    private int getPlayerAtTurnOrder(int turn) {
        for (int i = 0; i < getStateManager().getPlayers(); i++) {
            if (getTurnOrder()[i] == turn) {
                return i;
            }
        }
        throw new IllegalStateException("player not found in turn order");
    }

    private void grantBuildingSoldierBoost(int player) {
        PlayerStuff stuff = getStateManager().getPlayerStuff(player);
        if (stuff.hasGuardTower()) {
            printer.log(player, "has a Guard Tower. +1");
            getStateManager().getBoard().increaseSoldiers(player, 1);
        }
        if (stuff.hasBlacksmith()) {
            printer.log(player, "has a Blacksmith. +1");
            getStateManager().getBoard().increaseSoldiers(player, 1);
        }
        if (stuff.hasPalisade()) {
            printer.log(player, "has a Palisade. +1");
            getStateManager().getBoard().increaseSoldiers(player, 1);
        }
        if (stuff.hasStoneWall()) {
            printer.log(player, "has a Stone Wall. +1");
            getStateManager().getBoard().increaseSoldiers(player, 1);
        }
        if (stuff.hasFortress()) {
            printer.log(player, "has a Fortress. +1");
            getStateManager().getBoard().increaseSoldiers(player, 1);
        }
        if (stuff.hasChurch()) {
            printer.log(player, "has a Church. +0");
            getStateManager().getBoard().increaseSoldiers(player, 0);
        }
        if (stuff.hasBarricade()) {
            printer.log(player, "has a Barricade. +0");
            getStateManager().getBoard().increaseSoldiers(player, 0);
        }
        if (stuff.hasWizardsGuild()) {
            printer.log(player, "has a Wizards' Guild. +1");
            getStateManager().getBoard().increaseSoldiers(player, 2);
        }
        if (stuff.hasFarms()) {
            printer.log(player, "has Farms. -1");
            getStateManager().getBoard().increaseSoldiers(player, -1);
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
        getStateManager().getBoard().resetSoldiers();
        // TODO add for buildings
    }
    //</editor-fold>





    //<editor-fold desc="PrepareToPromptUserOrBot">
    private void chooseAdvisor(int player, Roll myRoll) {
        // FIXME look for existing Decision.
        String decisionId = "chooseAdvisor(" + player + ")withRoll(" + myRoll.toString() + ")on(" + getBoard().getCurrentStageAsString() + ")";
        Decision decision = DecisionHandler.get().getDecisionAt(decisionId);
        if (decision == null) {
            // if no decision exists, prompt BOT or USER to make decision
            getProxy(player).onAdvisorChoice(myRoll, getStateManager().getBoard(), getStateManager().getPlayerStuff(player));
        }

        // now we have a decision
        decision = DecisionHandler.get().getDecisionAt(decisionId);

    }

    private void chooseGoods(int round, int phase) {
        // TODO look for existing Decision.

        // if no decision exists, prompt BOT or USER to make decision
        for (int player = 0; player < getStateManager().getPlayers(); player++) {
            getProxy(player).onGoodsChoice(getStateManager().getPlayerStuff(player).countUnchosenResources());
        }
    }

    private void chooseLosses(int round, int phase) {
        // TODO look for existing Decision.

        // if no decision exists, prompt BOT or USER to make decision
        for (int player = 0; player < getStateManager().getPlayers(); player++) {
            chooseLosses(round, phase, player);
        }
    }

    private void chooseLosses(int round, int phase, int player) {
        // TODO look for existing Decision.

        // if no decision exists, prompt BOT or USER to make decision
        int debts = getStateManager().getPlayerStuff(player).countUnpaidDebts();
        getProxy(player).onChooseSpentResources(debts, getStateManager().getPlayerStuff(player));
    }

    private void offerUseStatue(Roll roll, int player) {
        // TODO look for existing Decision.

        // if no decision exists, prompt BOT or USER to make decision
        getProxy(player).onOfferUseStatue(roll);
    }

    private void offerUseChapel(Roll roll, int player) {
        // TODO look for existing Decision.

        // if no decision exists, prompt BOT or USER to make decision
        getProxy(player).onOfferUseChapel(roll);
    }

    private void offerUseTownHall(PlayerStuff stuff, int player) {
        // TODO look for existing Decision.

        // if no decision exists, prompt BOT or USER to make decision
        getProxy(player).onOfferUseTownHall(stuff);
    }

    private void offerConstructBuildings(int round, int phase) {
        // TODO look for existing Decision.

        // if no decision exists, prompt BOT or USER to make decision
        printer.log("");
        for (int player = 0; player < getStateManager().getPlayers(); player++) {
            getProxy(player).onBuildOption(getStateManager().getPlayerStuff(player));
        }
    }

    private void offerRecruit(int round, int phase) {
        // TODO look for existing Decision.

        // if no decision exists, prompt BOT or USER to make decision
        for (PlayerStuff p : getStateManager().getAllPlayersStuff()) {
            int player = p.getPlayerId();
            getProxy(player).onRecruitOption(p);
        }
    }

    private void offerPeek(int player) {
        // TODO look for existing Decision.

        // if no decision exists, prompt BOT or USER to make decision
        // peek at soldiers
        getProxy(player).onPeek();
    }
    //</editor-fold>
}
