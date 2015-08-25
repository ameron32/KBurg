
public class EnemyCard {
	String name;
	int year;
	int strength;
	RewardChoice victory;
	RewardChoice defeat;
	boolean defeatLoseBuilding;
	boolean isGoblin, isZombie, isDemon;
	
	public EnemyCard(String name, int year, int strength, 
			RewardChoice victory,
			RewardChoice defeat, 
			boolean defeatLoseBuilding) {
		super();
		this.name = name;
		this.year = year;
		this.strength = strength;
		this.victory = victory;
		this.defeat = defeat;
		this.defeatLoseBuilding = defeatLoseBuilding;
	}
	
	public boolean isGoblin() {
		return isGoblin;
	}

	public void setGoblin(boolean isGoblin) {
		this.isGoblin = isGoblin;
	}

	public boolean isZombie() {
		return isZombie;
	}

	public void setZombie(boolean isZombie) {
		this.isZombie = isZombie;
	}

	public boolean isDemon() {
		return isDemon;
	}

	public void setDemon(boolean isDemon) {
		this.isDemon = isDemon;
	}

	public String getName() {
		return name;
	}

	public int getYear() {
		return year;
	}

	public int getStrength() {
		return strength;
	}

	public RewardChoice getVictory() {
		return victory;
	}

	public RewardChoice getDefeat() {
		return defeat;
	}

	public boolean isDefeatLoseBuilding() {
		return defeatLoseBuilding;
	}

	@Override
	public String toString() {
//		return "\nEnemyCard [name=" + name + ", year=" + year + ", strength="
//				+ strength + ", \n  victory=" + victory + ", \n  defeat=" + defeat
//				+ ", defeatLoseBuilding=" + defeatLoseBuilding + "]";
		return toHumanReadableString();
	}
	
	public String toHumanReadableString() {
		return "\n" + name + "(" + strength + ") on year " + year + "\n"
				+ "defeat: " + defeat.toString() + "\n"
				+ ((defeatLoseBuilding) ? "        +1 building\n" : "")
				+ "victory: " + victory.toString() + "\n";
	}
}
