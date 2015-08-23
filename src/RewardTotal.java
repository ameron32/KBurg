
/**
 * 
 * POJO for wrapping rewards with labels.
 * 
 */
public class RewardTotal {
	private int gold, wood, stone;
	private int points = 0;
	private int choose = 0;
	private int soldiers = 0;
	private int plus2 = 0;
	private boolean peek;
	
	RewardTotal(int gold, int wood, int stone) {
		this.gold = gold; 
		this.wood = wood; 
		this.stone = stone;
	}
	
	RewardTotal(int gold, int wood, int stone, int points, int choose) {
		this(gold, wood, stone);
		this.points = points;
		this.choose = choose;
	}
	
	RewardTotal(int gold, int wood, int stone, int points, int choose, int soldiers, int plus2, int peek) {
		this(gold, wood, stone, points, choose);
		this.soldiers = soldiers;
		this.plus2 = plus2;
		this.peek = (peek == 1);
	}

	public int getGold() {
		return gold;
	}

	public int getWood() {
		return wood;
	}

	public int getStone() {
		return stone;
	}

	public int getPoints() {
		return points;
	}

	public int getChoose() {
		return choose;
	}

	public int getSoldiers() {
		return soldiers;
	}

	public int getPlus2() {
		return plus2;
	}

	public boolean isPeek() {
		return peek;
	}	
}
