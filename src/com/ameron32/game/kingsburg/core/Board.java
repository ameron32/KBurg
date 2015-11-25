package com.ameron32.game.kingsburg.core;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 
 * STATE of the game board. CONTAINS current game progress, as contained in a game board.
 *
 */
public abstract class Board {

	// indicator of current phase
	private int currentPhase;
	
	// indicator of current year
	private int currentYear;
	
	// number of soldiers for player
	private int[] soldiers;
	
	boolean[] reservedAdvisors = new boolean[18];

	public Board() {
		super();
	}
	
	void initialize(int players) {
		soldiers = new int[players];
		currentPhase = 0;
		currentYear = 0;
		pushUpdate();
	}

	void incrementYear() {
		currentYear++;
		pushUpdate();
	}

	void incrementPhase() {
		currentPhase++;
		pushUpdate();
	}
	
	void addKingsReinforcements(int qty) {
		for (int i = 0; i < soldiers.length; i++) {
			soldiers[i] = soldiers[i] + qty;
		}
		pushUpdate();
	}
	
	void increaseSoldiers(int player, int qty) {
		soldiers[player] = soldiers[player] + qty;
		pushUpdate();
	}
	
	int getSoldiersFor(int player) {
		pullSynchronize();
		return soldiers[player];
	}
	
	void resetSoldiers() {
		for (int i = 0; i < soldiers.length; i++) {
			soldiers[i] = 0;
		}
		pushUpdate();
	}
	
	void reserveAdvisor(int ordinal) {
		int position = ordinal - 1;
		reservedAdvisors[position] = true;
		pushUpdate();
	}
	
	boolean isAdvisorReserved(int ordinal) {
		pullSynchronize();
		int position = ordinal - 1;
		return reservedAdvisors[position];
	}

	void resetAdvisors() {
		reservedAdvisors = new boolean[18];
		pushUpdate();
	}
	
	public abstract void pullSynchronize();
	public abstract void pushUpdate();
}
