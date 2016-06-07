package com.ameron32.game.kingsburg.core;

import com.ameron32.game.kingsburg.core.advisor.Cost;
import com.ameron32.game.kingsburg.core.advisor.Reward;
import com.ameron32.game.kingsburg.core.state.EnemyCard;
import com.ameron32.game.kingsburg.core.state.PlayerStuff;
import com.ameron32.game.kingsburg.core.state.ProvinceBuilding;

/**
 * Created by klemeilleur on 6/7/2016.
 */
public class BattleResolver {

    private final StateManager stateManager;
    private final Logger printer;

    public BattleResolver(Logger printer, StateManager stateManager) {
        this.printer = printer;
        this.stateManager = stateManager;
    }

    //<editor-fold desc="WinterBattleResults">
    public void battleVictory(int player, EnemyCard enemy) {
        PlayerStuff stuff = stateManager.getPlayerStuff(player);
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
    public void battleDraw(int player, EnemyCard enemy) {
        // do nothing
    }

    public void battleLose(int player, EnemyCard enemy) {
        Cost cost = enemy.getDefeat().getCost();
        Reward reward = enemy.getDefeat().getReward();
        boolean loseABuilding = enemy.isDefeatLoseBuilding();
        printer.log(player, "pays the defeat cost of: " + cost.toString()
                + (loseABuilding ? " and loses a building!" : ""));
        stateManager.getPlayerStuff(player).payCost(cost);
        if (loseABuilding) {
            ProvinceBuilding buildingLost = stateManager.getPlayerStuff(player).loseABuilding();
            if (buildingLost != null) {
                printer.log(player, "loses a " + buildingLost.getName() + "(" + buildingLost.getPoints() + ").");
            } else {
                printer.log(player, "had no buildings. No buildings were lost.");
            }
        }
        stateManager.getPlayerStuff(player).receiveReward(reward); // shouldn't be a reward
    }
    //</editor-fold>
}
