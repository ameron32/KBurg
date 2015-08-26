import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	private int total;
	private int[] standardDice;
	private int[] bonusDice;
	private boolean isStatueUsed = false;
	private boolean isChapelUsed = false;
	
	private final List<Long> unusedStandardDice = new ArrayList<>(3);
	private final List<Long> unusedBonusDice = new ArrayList<>();

	private int numOfDice;
	private int dieSides;
	
	private Roll(int player, int numOfDice, int dieSides) {
		super();
		this.player = player;
		this.numOfDice = numOfDice;
		this.dieSides = dieSides;
		rollAllDice();
	}
	
	private void rollAllDice() {
		clearUnusedDice();
		setDiceSlots(numOfDice);
		fillDiceSlots(numOfDice, dieSides);
		setUnusedSlots(numOfDice);
	}
	
	private void clearUnusedDice() {
		unusedStandardDice.clear();
		unusedBonusDice.clear();
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
		
//		if (numOfDice <= MAXIMUM_PLAYER_DICE) {
//			for (int i = 0; i < numOfDice; i++) {
//				int oneRoll = _rollOneDie(dieSides);
//				this.standardDice[i] = oneRoll;
//			}
//		} else {
//			for (int i = 0; i < MAXIMUM_PLAYER_DICE; i++) {
//				int oneRoll = _rollOneDie(dieSides);
//				this.standardDice[i] = oneRoll;
//			}
//			int numOfBonusDice = numOfDice - MAXIMUM_PLAYER_DICE; 
//			for (int i = 0; i < numOfBonusDice; i++) {
//				int oneRoll = _rollOneDie(dieSides);
//				this.bonusDice[i] = oneRoll;
//			}
//		}
		for (int i = 0; i < numOfDice; i++) {
			fillDieSlot(i, dieSides);
		}
		recalculateDiceTotals();
	}
	
	private void fillDieSlot(int slot, int dieSides) {
		int oneRoll = _rollOneDie(dieSides);
		if (slot < MAXIMUM_PLAYER_DICE) {
			this.standardDice[slot] = oneRoll;
		} else {
			this.bonusDice[slot - MAXIMUM_PLAYER_DICE] = oneRoll;
		}
		// TODO could do this LESS often
		recalculateDiceTotals();
	}
	
	private void recalculateDiceTotals() {
		this.total = 0;
		for (int i = 0; i < standardDice.length; i++) {
			this.total += standardDice[i];
		}
		for (int i = 0; i < bonusDice.length; i++) {
			this.total += bonusDice[i];
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

	private int getTotal() {
		return total;
	}
	
	public int getUnusedTotal() {
		int total = 0;
		for (Long die : getAllUnusedDice()) {
			total += die.intValue();
		}
		return total;
	}
	
	public List<Long> getAllUnusedDice() {
		List<Long> allDice = new ArrayList<>();
		allDice.addAll(getUnusedStandardDice());
		allDice.addAll(getUnusedBonusDice());
		return allDice;
	}
	
	public List<Long> getUnusedStandardDice() {
		return unusedStandardDice;
	}
	
	public List<Long> getUnusedBonusDice() {
		return unusedBonusDice;
	}
	
	public boolean hasUsableDice() {
		return !(getUnusedStandardDice().isEmpty());
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
	
	public void useStandardDice(Long dice) {
		for (int position = 0; position < unusedStandardDice.size(); position++) {
			Long d2 = unusedStandardDice.get(position);
			if (d2 == dice) {
				unusedStandardDice.remove(dice);
				return;
			}
		}
	}
	
	public void useBonusDice(Long dice) {
		for (int position = 0; position < unusedBonusDice.size(); position++) {
			Long d2 = unusedBonusDice.get(position);
			if (d2 == dice) {
				unusedBonusDice.remove(dice);
				return;
			}
		}
	}
	
	public boolean isStatueEligable() {
		if (isStatueUsed) {
			return false;
		}
		int[] allDice = new int[standardDice.length + bonusDice.length];
		System.arraycopy(standardDice, 0, allDice, 0, standardDice.length);
		System.arraycopy(bonusDice, 0, allDice, standardDice.length, bonusDice.length);
		int startNumber = allDice[0];
		for (int die : allDice) {
			if (die != startNumber) {
				return false;
			}
		}
		return true;
	}
		
	public boolean isChapelEligable() {
		if (isChapelUsed) {
			return false;
		}
		return (getTotal() < 8);
	}
	
	public void useStatue(int diePosition) {
		isStatueUsed = true;
		clearUnusedDice();
		fillDieSlot(diePosition, dieSides);
		setUnusedSlots(numOfDice);
	}
	
	public void useChapel() {
		isChapelUsed = true;
		rollAllDice();
	}

	
	
	@Override
	public String toString() {
//		return "Roll [player=" + (player+1) + ", roll=" + roll + ", dice="
//				+ Arrays.toString(standardDice) + ", bonusDice="
//				+ Arrays.toString(bonusDice) + "]";
		return toHumanReadableString();
	}
	
	private String toHumanReadableString() {
		return "Roll: (" + getUnusedTotal() + "). Made of " + getUnusedStandardDice()
				+ ((getUnusedBonusDice().size() > 0) ? " and bonus of " + getUnusedBonusDice() : "")
				+ ".";
	}
}
