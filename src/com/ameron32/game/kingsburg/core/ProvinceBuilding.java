package com.ameron32.game.kingsburg.core;
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
	
	int getRow() {
		return row;
	}

	int getColumn() {
		return column;
	}

	int getPoints() {
		return points;
	}

	int getGoldCost() {
		return goldWoodStoneCost[0];
	}

	int getWoodCost() {
		return goldWoodStoneCost[1];
	}

	int getStoneCost() {
		return goldWoodStoneCost[2];
	}

	String getName() {
		return name;
	}

	String getDescription() {
		return description;
	}
	
	boolean is(String name) {
		return (name.equalsIgnoreCase(getName()));
	}
}
