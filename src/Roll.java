import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	
	private final List<Long> unusedStandardDice = new ArrayList<>(3);
	private final List<Long> unusedBonusDice = new ArrayList<>();


	private Roll(int player, int numOfDice, int dieSides) {
		super();
		this.player = player;
		setDiceSlots(numOfDice);
		fillDiceSlots(numOfDice, dieSides);
		setUnusedSlots(numOfDice);
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
	
	private void setUnusedSlots(int numOfDice) {
		if (numOfDice <= 0) {
			return;
		}
		
		for (int i = 0; i < standardDice.length; i++) {
			unusedStandardDice.add(new Long(standardDice[i]));
		}
		for (int i = 0; i < bonusDice.length; i++) {
			unusedBonusDice.add(new Long(bonusDice[i]));
		}
	}
	
	private int _rollOneDie(int dieSides) {
		BasicRandomizer rand = BasicRandomizer.get();
		// TODO consider checking rolls
		return rand.nextInt(dieSides) + 1;
	}
	
	

	public int getPlayer() {
		return player;
	}

	public int getRoll() {
		return roll;
	}

	private int[] getStandardDice() {
		return standardDice;
	}
	
	private int[] getBonusDice() {
		return bonusDice;
	}
	
	public List<Long> getUnusedStandardDice() {
		return unusedStandardDice;
	}
	
	public List<Long> getUnusedBonusDice() {
		return unusedBonusDice;
	}
	
	public boolean hasUsableDice() {
		return !getUnusedStandardDice().isEmpty();
	}
	
	public void useAllDice() {
		List<Long> toUse = new ArrayList<>();
		for (Long die : getUnusedStandardDice()) {
			toUse.add(die);
		}
		for (Long use : toUse) {
			useStandardDice(use);
		}
		if (getUnusedBonusDice().size() == 0) {
			return;
		}
		toUse.clear();
		for (Long die : getUnusedBonusDice()) {
			toUse.add(die);
		}
		for (Long use : toUse) {
			useBonusDice(use);
		}
	}
	
	public void useStandardDice(Long... dice) {
		List<Long> toRemove = new ArrayList<>();
		for (Long die : dice) {
			boolean found = false;
			for (int position = 0; position < unusedStandardDice.size(); position++) {
				Long d2 = unusedStandardDice.get(position);
				if (d2.intValue() == die.intValue()) {
					if (!found) {
						toRemove.add(d2);
						found = true;
					}
				}
			}
		}
		unusedStandardDice.removeAll(toRemove);
	}
	
	public void useBonusDice(Long... dice) {
		List<Long> toRemove = new ArrayList<>();
		for (Long die : dice) {
			boolean found = false;
			for (int position = 0; position < unusedBonusDice.size(); position++) {
				Long d2 = unusedBonusDice.get(position);
				if (d2.intValue() == die.intValue()) {
					if (!found) {
						toRemove.add(d2);
						found = true;
					}
				}
			}
		}
		unusedBonusDice.removeAll(toRemove);
	}

	
	
	@Override
	public String toString() {
//		return "Roll [player=" + (player+1) + ", roll=" + roll + ", dice="
//				+ Arrays.toString(standardDice) + ", bonusDice="
//				+ Arrays.toString(bonusDice) + "]";
		return toHumanReadableString();
	}
	
	private String toHumanReadableString() {
		return "Roll: (" + roll + "). Made of " + Arrays.toString(standardDice)
				+ ((bonusDice.length > 0) ? " and bonus of " + Arrays.toString(bonusDice) : "")
				+ ".";
	}
}
