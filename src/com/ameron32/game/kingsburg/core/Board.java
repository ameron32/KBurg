package com.ameron32.game.kingsburg.core;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 
 * STATE of the game board. CONTAINS current game progress, as contained in a game board.
 *
 */
public class Board {

	// indicator of current phase
	private int currentPhase;
	
	// indicator of current year
	private int currentYear;
	
	// number of soldiers for player
	private int[] soldiers;
	
	boolean[] reservedAdvisors = new boolean[18];

	public Board(int players) {
		super();
		soldiers = new int[players];
		currentPhase = 0;
		currentYear = 0;
	}

	void incrementYear() {
		currentYear++;
	}

	void incrementPhase() {
		currentPhase++;
	}
	
	void addKingsReinforcements(int qty) {
		for (int i = 0; i < soldiers.length; i++) {
			soldiers[i] = soldiers[i] + qty;
		}
	}
	
	void increaseSoldiers(int player, int qty) {
		soldiers[player] = soldiers[player] + qty;
	}
	
	int getSoldiersFor(int player) {
		return soldiers[player];
	}
	
	void resetSoldiers() {
		for (int i = 0; i < soldiers.length; i++) {
			soldiers[i] = 0;
		}
	}
	
	void reserveAdvisor(int ordinal) {
		int position = ordinal - 1;
		reservedAdvisors[position] = true;
	}
	
	boolean isAdvisorReserved(int ordinal) {
		int position = ordinal - 1;
		return reservedAdvisors[position];
	}

	void resetAdvisors() {
		reservedAdvisors = new boolean[18];
	}
}
