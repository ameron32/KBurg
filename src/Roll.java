import java.util.Arrays;
import java.util.Random;

/**
 * 
 * ACTION CONTAINER to roll dice in the typical manner.
 * PRIMARY USAGE: Roll.rollTheDice(0, 3, 6)
 * 
 */
public class Roll {

	public static final int MAXIMUM_PLAYER_DICE = 3;
	
	private static final int NO_PLAYER = -1;
		
	public static Roll rollTheDice(int player, int numOfDice, int dieSides) {
		return new Roll(player, numOfDice, dieSides);
	}
	
	private int player = NO_PLAYER;
	private int roll;
	private int[] standardDice;
	private int[] bonusDice;

	public int getPlayer() {
		return player;
	}

	public int getRoll() {
		return roll;
	}

	public int[] getDice() {
		return standardDice;
	}

	private Roll(int player, int numOfDice, int dieSides) {
		super();
		this.player = player;
		setDiceSlots(numOfDice);
		fillDiceSlots(numOfDice, dieSides);
	}
	
	private void setDiceSlots(int numOfDice) {
		if (numOfDice < 0) {
			numOfDice = 0;
		}
		if (numOfDice <= MAXIMUM_PLAYER_DICE) {
			this.standardDice = new int[numOfDice];
			this.bonusDice = new int[0];
		} else {
			this.standardDice = new int[MAXIMUM_PLAYER_DICE];
			this.bonusDice = new int[numOfDice - MAXIMUM_PLAYER_DICE];
		}
	}
	
	private void fillDiceSlots(int numOfDice, int dieSides) {
		if (numOfDice <= 0) {
			return;
		}
		
		if (numOfDice <= MAXIMUM_PLAYER_DICE) {
			for (int i = 0; i < numOfDice; i++) {
				int oneRoll = _rollOneDie(dieSides);
				this.roll += oneRoll;
				this.standardDice[i] = oneRoll;
			}
		} else {
			for (int i = 0; i < MAXIMUM_PLAYER_DICE; i++) {
				int oneRoll = _rollOneDie(dieSides);
				this.roll += oneRoll;
				this.standardDice[i] = oneRoll;
			}
			int numOfBonusDice = numOfDice - MAXIMUM_PLAYER_DICE; 
			for (int i = 0; i < numOfBonusDice; i++) {
				int oneRoll = _rollOneDie(dieSides);
				this.roll += oneRoll;
				this.bonusDice[i] = oneRoll;
			}
		}
	}
	
	private int _rollOneDie(int dieSides) {
		BasicRandomizer rand = BasicRandomizer.get();
		// TODO consider checking rolls
		return rand.nextInt(dieSides) + 1;
	}

	@Override
	public String toString() {
		return "Roll [player=" + player + ", roll=" + roll + ", dice="
				+ Arrays.toString(standardDice) + ", bonusDice="
				+ Arrays.toString(bonusDice) + "]";
	}
}
