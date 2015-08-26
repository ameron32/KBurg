import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 
 * CONTAINS whether a series of ProvinceBuildings are owned by a player or not.
 * CONTAINS a static list of the 20 possible ProvinceBuildings.
 *
 */
public class ProvinceBoard {
	
	public static final int ROW_RELIGION = 0;
	public static final int ROW_MERCHANT = 1;
	public static final int ROW_COMBAT = 2;
	public static final int ROW_DEFENSE = 3;
	public static final int ROW_CITY = 4;
	public static final int TOTAL_ROWS = 5;
	private static final int MAX_COLUMN = 4;
	
	private static final List<ProvinceBuilding> buildings = new ArrayList<>(20);
	static {
		//religion
		buildings.add(new ProvinceBuilding("Statue", ROW_RELIGION, 1,    
				3, new int[] {2,0,0}, 
				"At the beginning of each productive season, if you have rolled the same number on all your dice, you may reroll one of them."));
		buildings.add(new ProvinceBuilding("Chapel", ROW_RELIGION, 2,
				5, new int[] {3,0,1}, 
				"At the beginning of each productive season, if the total of your dice is 7 or less, you may reroll all of them."));
		buildings.add(new ProvinceBuilding("Church", ROW_RELIGION, 3, 
				7, new int[] {3,1,2}, 
				"+0 in battle. (Becomes +1 in battle vs. Demons.)"));
		buildings.add(new ProvinceBuilding("Cathedral", ROW_RELIGION, 4,
				9, new int[] {5,0,3}, 
				"At the end of the game, score 1 extra [VP] for every 2 goods you own."));

		//merchant
		buildings.add(new ProvinceBuilding("Inn", ROW_MERCHANT, 1,
				0, new int[] {1,1,0}, 
				"At the end of Summer each year, you receive a [p2] token."));
		buildings.add(new ProvinceBuilding("Market", ROW_MERCHANT, 2,
				1, new int[] {2,2,0}, 
				"Once per season, you may influence an Advisor whose value is one point higher or lower than the sum of the dice you are using."));
		buildings.add(new ProvinceBuilding("Farms", ROW_MERCHANT, 3,
				2, new int[] {2,3,1}, 
				"During each productive season, you roll one extra (white) die. Penalty: -1 in battle."));
		buildings.add(new ProvinceBuilding("Merchants' Guild", ROW_MERCHANT, 4,
				4, new int[] {3,1,2}, 
				"At the beginning of each productive season, before rolling the dice, you get 1 [g]."));

		//combat
		buildings.add(new ProvinceBuilding("Guard Tower", ROW_COMBAT, 1,
				1, new int[] {1,0,1}, 
				"+1 in battle."));
		buildings.add(new ProvinceBuilding("Blacksmith", ROW_COMBAT, 2,
				2, new int[] {1,2,0}, 
				"+1 in battle."));
		buildings.add(new ProvinceBuilding("Barracks", ROW_COMBAT, 3,
				4, new int[] {2,2,1}, 
				"You pay only one good per soldier to recruit in phase 7."));
		buildings.add(new ProvinceBuilding("Wizards' Guild", ROW_COMBAT, 4,
				6, new int[] {3,2,2}, 
				"+2 in battle."));
		
		//defense
		buildings.add(new ProvinceBuilding("Palisade", ROW_DEFENSE, 1,
				0, new int[] {0,2,0}, 
				"+1 in battle. (Becomes +2 in battle vs. Zombies.)"));
		buildings.add(new ProvinceBuilding("Stable", ROW_DEFENSE, 2,
				2, new int[] {1,1,1}, 
				"+1 [sldr] when you influence an Advisor giving you at least 1 [sldr]."));
		buildings.add(new ProvinceBuilding("Stone Wall", ROW_DEFENSE, 3,
				2, new int[] {2,0,2}, 
				"+1 in battle. In addition, you win drawn battles."));
		buildings.add(new ProvinceBuilding("Fortress", ROW_DEFENSE, 4,
				4, new int[] {3,0,2}, 
				"+1 in battle. In addition, when you win a battle, you get 1 [VP] in addition to the normal rewards."));
		
		//city
		buildings.add(new ProvinceBuilding("Barricade", ROW_CITY, 1,
				0, new int[] {0,1,0}, 
				"+0 in battle. (Becomes +1 in battle vs. Goblins.)"));
		buildings.add(new ProvinceBuilding("Crane", ROW_CITY, 2,
				1, new int[] {0,1,1}, 
				"You pay one [g] less when you construct a building from column III or IV."));
		buildings.add(new ProvinceBuilding("Town Hall", ROW_CITY, 3,
				2, new int[] {2,1,1}, 
				"At the end of each productive season, you may pay [p2] or one good (of any kind) to get 1 [VP] (only once per season)."));
		buildings.add(new ProvinceBuilding("Embassy", ROW_CITY, 4,
				4, new int[] {2,2,2}, 
				"At the end of each productive season, you get 1 [VP]."));
	}
	static ProvinceBuilding getBuilding(int row, int column) {
		for (ProvinceBuilding building : buildings) {
			if (building.getRow() == row && building.getColumn() == column) {
				return building;
			}
		}
		return null;
	}
	static ProvinceBuilding getBuilding(String name) {
		for (ProvinceBuilding building : buildings) {
			if (building.getName().equalsIgnoreCase(name)) {
				return building;
			}
		}
		throw new IllegalArgumentException("name does not exist in ProvinceBuildings (insensitive case)");
	}
	
	//because you can only own the NEXT building (not any building) in the chain, the chain is represented as "farthest owned building."
	int[] buildingTracker = new int[TOTAL_ROWS];
	
	//
	//
	// BUYING A BUILDING
	//
	boolean canAffordNextBuilding(int row, int playerGold, int playerWood, int playerStone, boolean hasCrane) {
		int nextBuildingInRow = getNextBuildingColumn(row);
		if (!doesBuildingExist(row, nextBuildingInRow)) {
			//no more buildings in row, so you can't afford the next one
			return false;
		}
		ProvinceBuilding buildingBlueprint = getBuilding(row, nextBuildingInRow);
		Cost nextBuildingCost = getCostOfBuilding(buildingBlueprint, hasCrane);
		if (playerGold >= nextBuildingCost.getGold() && playerWood >= nextBuildingCost.getWood() && playerStone >= nextBuildingCost.getStone()) {
			return true;
		}
		return false;
	}
	
	Cost getCostOfNextBuilding(int row, boolean hasCrane) {
		int nextBuildingInRow = getNextBuildingColumn(row);
		if (!doesBuildingExist(row, nextBuildingInRow)) {
			//no more buildings in row, so you can't afford the next one
			return null;
		}
		ProvinceBuilding buildingBlueprint = getBuilding(row, nextBuildingInRow);
		return getCostOfBuilding(buildingBlueprint, hasCrane);
	}
		
	// RETURNS QUANTITY OF VICTORY POINTS EARNED
	ProvinceBuilding buyNextBuilding(int row, boolean hasCrane) {
		int nextBuilding = getNextBuildingColumn(row);
		buildingTracker[row] = nextBuilding;
		// return the points earned
		return getBuilding(row, nextBuilding);
	}
	
	//
	//
	// QUERY OWNERSHIP OF A BUILDING
	//
	boolean hasBuilding(String name) {
		ProvinceBuilding building = getBuilding(name);
		int row = building.getRow();
		int column = building.getColumn();
		return hasBuilding(row, column);		
	}
	
	int countBuildings() {
		int count = 0;
		for (int buildingCount : buildingTracker) {
			count += buildingCount;
		}
		return count;
	}
	
	//
	//
	// LOSE A BUILDING TO ATTACKERS
	//
	// RETURN COST TO LOSE
	ProvinceBuilding loseBestBuilding() {
		//count of buildings in the farthest right column
		int highestNumber = 0;
		for (int i = 0; i < TOTAL_ROWS; i++) {
			if (buildingTracker[i] > highestNumber) {
				highestNumber = buildingTracker[i];
			}
		}
		int count = 0;
		for (int i = 0; i < TOTAL_ROWS; i++) {
			if (buildingTracker[i] == highestNumber) {
				count++;
			}
		}
		if (highestNumber == 0) {
			// no buildings. lose nothing.
			return null;
		}
		//determine FIRST building, top to bottom
		for (int i = 0; i < TOTAL_ROWS; i++) {
			if (buildingTracker[i] == highestNumber) {
				// set building to previous
				ProvinceBuilding lostBuilding = getBuilding(i, highestNumber);
				int row = lostBuilding.getRow();
				int previousBuilding = getPreviousBuildingColumn(row);
				buildingTracker[row] = previousBuilding;
				// return building that was lost
				return lostBuilding;
			}
		}
		throw new IllegalStateException("something happened. I don't know how.");
	}
	
	
	
	private int getNextBuildingColumn(int row) {
		int currentBuildingInRow = buildingTracker[row];
		int nextBuildingInRow = currentBuildingInRow + 1;
		return nextBuildingInRow;
	}
	
	private int getPreviousBuildingColumn(int row) {
		int currentBuildingInRow = buildingTracker[row];
		int nextBuildingInRow = currentBuildingInRow - 1;
		return nextBuildingInRow;
	}
	
	private boolean doesBuildingExist(int row, int column) {
		ProvinceBuilding buildingBlueprint = getBuilding(row, column);
		if (buildingBlueprint == null) {
			//could not find building
			return false;
		}
		return true;
	}

	private Cost getCostOfBuilding(ProvinceBuilding buildingBlueprint, boolean hasCrane) {
		int goldCost = buildingBlueprint.getGoldCost();
		if (hasCrane && buildingBlueprint.getColumn() > 2) {
			goldCost -= 1;
		}
		int woodCost = buildingBlueprint.getWoodCost();
		int stoneCost = buildingBlueprint.getStoneCost();
		return new Cost(goldCost, woodCost, stoneCost);
	}
	
	private boolean hasBuilding(int row, int column) {
		int currentBuilding = buildingTracker[row];
		return (currentBuilding >= column);
	}
	
	@Override
	public String toString() {
		return "ProvinceBoard [buildingTracker=" + Arrays.toString(buildingTracker) + "]";
	}
}
