
public class EnemyCard {
	String name;
	int year;
	int strength;
	RewardChoice victory;
	RewardChoice defeat;
	boolean defeatLoseBuilding;
	
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
		return "\nEnemyCard [name=" + name + ", year=" + year + ", strength="
				+ strength + ", \n  victory=" + victory + ", \n  defeat=" + defeat
				+ ", defeatLoseBuilding=" + defeatLoseBuilding + "]";
	}
}
