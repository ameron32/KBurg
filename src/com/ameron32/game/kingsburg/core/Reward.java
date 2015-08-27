package com.ameron32.game.kingsburg.core;

/**
 * 
 * POJO for wrapping rewards with labels.
 * 
 */
public class Reward {
	private int gold, wood, stone;
	private int points = 0;
	private int choose = 0;
	private int soldiers = 0;
	private int plus2 = 0;
	private boolean peek;
	
	Reward(int gold, int wood, int stone) {
		this.gold = gold; 
		this.wood = wood; 
		this.stone = stone;
	}
	
	Reward(int gold, int wood, int stone, int points, int choose) {
		this(gold, wood, stone);
		this.points = points;
		this.choose = choose;
	}
	
	Reward(int gold, int wood, int stone, int points, int choose, int soldiers, int plus2, int peek) {
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
	
	public boolean isEmpty() {
		return (getGold() == 0 && getWood() == 0 && getStone() == 0 && 
				getPoints() == 0 && getChoose() == 0 && 
				getSoldiers() == 0 && getPlus2() == 0 && !isPeek());
	}

	@Override
	public String toString() {
//		return "Reward [gold=" + gold + ", wood=" + wood + ", stone=" + stone + ", points=" + points + ", choose="
//				+ choose + ", soldiers=" + soldiers + ", plus2=" + plus2 + ", peek=" + peek + "]";
		return toHumanReadableString();
	}
	
	private String toHumanReadableString() {
		return "Reward: gold/wood/stone ["+gold+"/"+wood+"/"+stone+"]"
				+ ((choose != 0) ? " choose: " + choose : "")
				+ ((points != 0) ? " points: " + points : "");
	}
}
