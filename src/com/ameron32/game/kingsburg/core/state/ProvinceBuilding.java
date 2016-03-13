package com.ameron32.game.kingsburg.core.state;
import java.util.Arrays;


/**
 * 
 * TODO more unique building attributes need to be allocated.
 * RESOURCE POJO for a single building on the ProvinceBoard.
 *
 */
public class ProvinceBuilding {
	int row, column;
	String name;
	int points;
	int[] goldWoodStoneCost = new int[3];
	String description;
	
	public ProvinceBuilding(String name, int row, int column, 
			int points,
			int[] goldWoodStoneCost, String description) {
		super();
		this.row = row;
		this.column = column;
		this.name = name;
		this.points = points;
		this.goldWoodStoneCost = goldWoodStoneCost;
		this.description = description;
	}

	@Override
	public String toString() {
		return "ProvinceBuilding [name=" + name + ", points=" + points
				+ ", goldWoodStoneCost="
				+ Arrays.toString(goldWoodStoneCost) + ", description="
				+ description + "]";
	}
	
	// TODO fancy handling like Statue dice re-roll mechanisms
	
	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public int getPoints() {
		return points;
	}

	public int getGoldCost() {
		return goldWoodStoneCost[0];
	}

	public int getWoodCost() {
		return goldWoodStoneCost[1];
	}

	public int getStoneCost() {
		return goldWoodStoneCost[2];
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public boolean is(String name) {
		return (name.equalsIgnoreCase(getName()));
	}
}
