

/**
 * 
 * POJO for wrapping costs with labels.
 * 
 */
public class Cost {
	private int gold, wood, stone;
	private int points = 0;
	private int choose = 0;
	
	Cost(int gold, int wood, int stone) {
		this.gold = gold; 
		this.wood = wood; 
		this.stone = stone;
	}
	
	Cost(int gold, int wood, int stone, int points, int choose) {
		this(gold, wood, stone);
		this.points = points;
		this.choose = choose;
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
	
	public boolean isEmpty() {
		return (getGold() == 0 && getWood() == 0 && getStone() == 0 && 
				getPoints() == 0 && getChoose() == 0);
	}

	@Override
	public String toString() {
//		return "Cost [gold=" + gold + ", wood=" + wood + ", stone=" + stone + ", points=" + points + ", choose="
//				+ choose + "]";
		return toHumanReadableString();
	}
	
	private String toHumanReadableString() {
		return "Cost: gold/wood/stone ["+gold+"/"+wood+"/"+stone+"]"
				+ ((choose != 0) ? " choose: " + choose : "")
				+ ((points != 0) ? " points: " + points : "");
	}
}
