package com.ameron32.game.kingsburg.core;

import com.ameron32.game.kingsburg.core.state.Board;
import com.ameron32.game.kingsburg.core.state.PlayerStuff;

import java.util.Arrays;
import java.util.List;

/**
 * Created by klemeilleur on 6/7/2016.
 */
public class StateManager {

    private int players;

    // player numbers are locked in place. turn order is variable.
    // number of players & their stuff
    private PlayerStuff[] playersStuff;

    // storage of board-based information
    private Board board;

    public StateManager() {

    }

    public void setPlayers(int players) {
        this.players = players;
        this.playersStuff = new PlayerStuff[players];
    }

    public PlayerStuff getPlayerStuff(int player) {
        playersStuff[player].pullSynchronize();
        return playersStuff[player];
    }

    private void setPlayerStuff(int player, PlayerStuff stuff) {
        playersStuff[player] = stuff;
    }

    List<PlayerStuff> getAllPlayersStuff() {
        return Arrays.asList(playersStuff);
    }

    public Board getBoard() {
        return board;
    }

    void setBoard(Board board) {
        this.board = board;
    }

    public int getPlayers() {
        return players;
    }
}
