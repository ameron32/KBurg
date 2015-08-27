package com.ameron32.game.kingsburg.core;


/**
 * 
 * CONTAINS one player's state, including the buildings owned, resource supply, and special assistance from the king.
 *
 */
public class PlayerStuff {
	
	public static final int PLAYER_DICE_COUNT = 3;
	public static final int PLAYER_DICE_SIDES = 6;
	public static final int PLAYER_STARTING_BONUS_DIE_COUNT = 0;
	
	private int playerId;
	private int wood, stone, gold, plus2;
	// unchosen resources are green bags received but unconverted yet. conversion should take place a little later.
	// unpaid resources represent LOST resources during lost battles where green bags are lost.	
	private int unchosenResources, unpaidResources;
	private int points;
	private boolean hasEnvoy;
	private boolean hasAid;
	private ProvinceBoard province;
	
	PlayerStuff(int playerId) {
		this.playerId = playerId;
		province = new ProvinceBoard();
	}
	
	int getPlayerId() { return playerId; }
	
	boolean hasAid() { return hasAid; }	
	boolean hasEnvoy() { return hasEnvoy; }
	
	void gainEnvoy() { hasEnvoy = true; }
	void gainAid() { hasAid = true; }
	void useEnvoy() { hasEnvoy = false; }
	void useAid() { hasAid = false; }
	
	int countUnchosenResources() { return unchosenResources; }
	int countUnpaidDebts() { return unpaidResources; }
	int countWood() { return wood; }
	int countGold() { return gold; }
	int countStone() { return stone; }
	int countPlus2() { return plus2; }
	int countPoints() { return points; }
	
	void spendUnpaidDebt(int qty) { unpaidResources = unpaidResources + qty; }
	void spendPoints(int qty) { points = points - qty; }
	void spendWood(int qty) { 
		wood = wood - qty;
		if (wood < 0) { wood = 0; }
	}
	void spendGold(int qty) { 
		gold = gold - qty; 
		if (gold < 0) { gold = 0; }
	}
	void spendStone(int qty) { 
		stone = stone - qty;
		if (stone < 0) { stone = 0; }
	}
	void usePlus2() { plus2--; }
	
	void clearUnchosenResources() { unchosenResources = 0; }
	void clearUnpaidDebts() { unpaidResources = 0; }
	
	void gainUnchosenResources(int qty) { unchosenResources = unchosenResources + qty; }
	void gainPoints(int qty) { points = points + qty; }
	void gainWood(int qty) { wood = wood + qty; }
	void gainGold(int qty) { gold = gold + qty; }
	void gainStone(int qty) { stone = stone + qty; }
	void gainPlus2(int qty) { plus2 = plus2 + qty; }
	
	boolean canPayCost(Cost cost) {
		// can have negative points!
		int gold = cost.getGold();
		int stone = cost.getStone();
		int wood = cost.getWood();
		if (countGold() < gold || countStone() < stone || countWood() < wood) {
			return false;
		}
		//check for extra resources to pay 'choose' cost
		int goldExtra = countGold() - cost.getGold();
		int stoneExtra = countStone() - cost.getStone();
		int woodExtra = countWood() - cost.getWood();
		if (cost.getChoose() > goldExtra + stoneExtra + woodExtra) {
			return false;
		}
		return true;
	}
	
	void payCost(Cost cost) {
		spendWood(cost.getWood());
		spendGold(cost.getGold());
		spendStone(cost.getStone());
		spendPoints(cost.getPoints());
	}

	void receiveReward(Reward reward) {
		gainWood(reward.getWood());
		gainGold(reward.getGold());
		gainStone(reward.getStone());
		gainPoints(reward.getPoints());
		gainPlus2(reward.getPlus2());
		gainUnchosenResources(reward.getChoose());
	}

	// DOES NOT INCLUDE UNCHOSEN RESOURCES OR UNPAID RESOURCES
	int countResources() {
		int count = 0;
		count += countWood();
		count += countGold();
		count += countStone();
		count += countUnchosenResources();
		return count;
	}
	
	
	//
	//
	// PROVINCE BOARD PROXY
	//
	boolean hasStatue() {
		return province.hasBuilding("Statue");
	}
	
	boolean hasChapel() {
		return province.hasBuilding("Chapel");
	}
	
	boolean hasChurch() {
		return province.hasBuilding("Church");
	}
	
	boolean hasCathedral() {
		return province.hasBuilding("Cathedral");
	}
	
	boolean hasInn() {
		return province.hasBuilding("Inn");
	}
	
	boolean hasMarket() {
		return province.hasBuilding("Market");
	}
	
	boolean hasFarms() {
		return province.hasBuilding("Farms");
	}
	
	boolean hasMerchantsGuild() {
		return province.hasBuilding("Merchants' Guild");
	}
	
	boolean hasGuardTower() {
		return province.hasBuilding("Guard Tower");
	}
	
	boolean hasBlacksmith() {
		return province.hasBuilding("Blacksmith");
	}
	
	boolean hasBarracks() {
		return province.hasBuilding("Barracks");
	}
	
	boolean hasWizardsGuild() {
		return province.hasBuilding("Wizards' Guild");
	}
	
	boolean hasPalisade() {
		return province.hasBuilding("Palisade");
	}
	
	boolean hasStable() {
		return province.hasBuilding("Stable");
	}
	
	boolean hasStoneWall() {
		return province.hasBuilding("Stone Wall");
	}
	
	boolean hasFortress() {
		return province.hasBuilding("Fortress");
	}
	
	boolean hasBarricade() {
		return province.hasBuilding("Barricade");
	}
	
	boolean hasCrane() {
		return province.hasBuilding("Crane");
	}
	
	boolean hasTownHall() {
		return province.hasBuilding("Town Hall");
	}
	
	boolean hasEmbassy() {
		return province.hasBuilding("Embassy");
	}
	
	int countBuildings() {
		return province.countBuildings();
	}
	
	boolean canAffordNextBuilding(int row) {
		return province.canAffordNextBuilding(row, countGold(), countWood(), countStone(), hasCrane());
	}
	
	ProvinceBuilding buyNextBuilding(int row) {
		Cost cost = province.getCostOfNextBuilding(row, hasCrane());
		spendGold(cost.getGold());
		spendWood(cost.getWood());
		spendStone(cost.getStone());
		return province.buyNextBuilding(row, hasCrane());
	}
	
	ProvinceBuilding loseABuilding() {
		return province.loseBestBuilding();
	}

	@Override
	public String toString() {
//		return "PlayerStuff [wood=" + wood + ", stone=" + stone + ", gold=" + gold + ", plus2=" + plus2
//				+ ", unchosenResources=" + unchosenResources + ", unpaidResources=" + unpaidResources + ", points="
//				+ points + ", hasEnvoy=" + hasEnvoy + ", hasAid=" + hasAid + ", province=" + province + "]";
		return toHumanReadableString();
	}
	
	public String toHumanReadableString() {
		return "Player " + (getPlayerId()+1) + " stuff:\n"
				+ "  points: [" + countPoints() + "]\n"
				+ "  buildings: [" + countBuildings() + "]\n"
				+ "  wood/gold/stone: [" + countWood() + "/" + countGold() + "/" + countStone() + "]\n"
				+ ((countPlus2() > 0) ? "  plus2: " + countPlus2() + "\n": "")
				+ ((unchosenResources > 0) ? "  unchosenResources: " + unchosenResources + "\n" : "")
				+ ((unpaidResources > 0) ? "  unpaidResources: " + unpaidResources + "\n" : "")
				+ ((hasEnvoy) ? "Envoy\n" : "")
				+ ((hasAid) ? "+1 Aid die\n" : "");
	}
	
}
