package com.ameron32.game.kingsburg.core;

import com.ameron32.game.kingsburg.core.advisor.Cost;
import com.ameron32.game.kingsburg.core.advisor.Reward;
import com.ameron32.game.kingsburg.core.state.EnemyCard;
import com.ameron32.game.kingsburg.core.state.PlayerStuff;
import com.ameron32.game.kingsburg.core.state.ProvinceBuilding;

/**
 * Created by klemeilleur on 6/7/2016.
 */
public class GameFinisher {

    private final StateManager stateManager;
    private final Logger printer;

    public GameFinisher(Logger printer, StateManager stateManager) {
        this.printer = printer;
        this.stateManager = stateManager;
    }

    //<editor-fold desc="DeclareGameWinner(s)">
    public void playerWins(int player, int score) {
        printer.log("");
        printer.log("Player " + (player+1)
                + " wins with " + score + " points!");
    }

    public void playerWins(int player, int score, int totalResources) {
        printer.log("");
        printer.log("Player " + (player+1)
                + " ties with " + score + " points, but wins with "
                + totalResources + " resources!");
    }

    public void playerWins(int player, int score, int totalResources, int totalBuildings) {
        printer.log("");
        printer.log("Player " + (player+1)
                + " ties with " + score + " points and "
                + totalResources + " resources, but wins with "
                + totalBuildings + " buildings!");
    }



    public void playersWin(int player, int score, int totalResources, int totalBuildings) {
        printer.log("");
        printer.log("Player " + (player+1)
                + " ties with " + score + " points and "
                + totalResources + " resources and "
                + totalBuildings + " buildings!");
    }
    //</editor-fold>
}
