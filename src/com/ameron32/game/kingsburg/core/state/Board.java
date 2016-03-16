package com.ameron32.game.kingsburg.core.state;


/**
 * 
 * STATE of the game board. CONTAINS current game progress, as contained in a game board.
 *
 */
public abstract class Board {

	// hidden indicator of current stage in phase (not on physical board)
	private int currentStage;
	private int turn;

	// indicator of current phase
	private int currentPhase;
	private int phaseCount;
	
	// indicator of current year
	private int currentYear;
	
	// number of soldiers for player
	private int players;
	private int[] soldiers;
	
	boolean[] reservedAdvisors = new boolean[18];

	public Board() {
		super();
	}
	
	public void initialize(int players, int phaseCount) {
		this.players = players;
		this.phaseCount = phaseCount;
		soldiers = new int[players];
		currentPhase = 0;
		currentYear = 0;
		pushUpdate();
	}

	public void incrementTurn() {
		turn++;
		if (turn == players) {
			turn = 0;
		}
		pushUpdate();
	}

	public void incrementStage() {
		currentStage++;
		turn = 0;
		pushUpdate();
	}

	public void incrementYear() {
		currentYear++;
		currentPhase = 0;
		pushUpdate();
	}

	public void incrementPhase() {
		currentPhase++;
		currentStage = 0;
		if (currentPhase == phaseCount) {
			incrementYear();
		}
		pushUpdate();
	}
	
	public void addKingsReinforcements(int qty) {
		for (int i = 0; i < soldiers.length; i++) {
			soldiers[i] = soldiers[i] + qty;
		}
		pushUpdate();
	}
	
	public void increaseSoldiers(int player, int qty) {
		soldiers[player] = soldiers[player] + qty;
		pushUpdate();
	}
	
	public int getSoldiersFor(int player) {
		pullSynchronize();
		return soldiers[player];
	}
	
	public void resetSoldiers() {
		for (int i = 0; i < soldiers.length; i++) {
			soldiers[i] = 0;
		}
		pushUpdate();
	}
	
	public void reserveAdvisor(int ordinal) {
		int position = ordinal - 1;
		reservedAdvisors[position] = true;
		pushUpdate();
	}
	
	public boolean isAdvisorReserved(int ordinal) {
		pullSynchronize();
		int position = ordinal - 1;
		return reservedAdvisors[position];
	}

	public void resetAdvisors() {
		reservedAdvisors = new boolean[18];
		pushUpdate();
	}
	
	public abstract void pullSynchronize();
	public abstract void pushUpdate();

	public int getCurrentTurn() {
		return turn;
	}

	public int getCurrentStage() {
		return currentStage;
	}

	public int getCurrentPhase() {
		return currentPhase;
	}

	public int getCurrentYear() {
		return currentYear;
	}
}
