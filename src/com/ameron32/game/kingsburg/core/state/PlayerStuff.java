package com.ameron32.game.kingsburg.core.state;


import com.ameron32.game.kingsburg.core.advisor.Cost;
import com.ameron32.game.kingsburg.core.advisor.Reward;

/**
 * 
 * CONTAINS one player's state, including the buildings owned, resource supply, and special assistance from the king.
 *
 */
public abstract class PlayerStuff {
	
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
	
	protected PlayerStuff(int playerId) {
		this.playerId = playerId;
		province = new ProvinceBoard();
	}
	
	public int getPlayerId() { return playerId; }
	
	public boolean hasAid() { return hasAid; }	
	public boolean hasEnvoy() { return hasEnvoy; }
	
	public void gainEnvoy() { hasEnvoy = true; }
	public void gainAid() { hasAid = true; }
	public void useEnvoy() { hasEnvoy = false; }
	public void useAid() { hasAid = false; }
	
	public int countUnchosenResources() { return unchosenResources; }
	public int countUnpaidDebts() { return unpaidResources; }
	public int countWood() { return wood; }
	public int countGold() { return gold; }
	public int countStone() { return stone; }
	public int countPlus2() { return plus2; }
	public int countPoints() { return points; }
	
	public void spendUnpaidDebt(int qty) { unpaidResources = unpaidResources + qty; }
	public void spendPoints(int qty) { points = points - qty; }
	public void spendWood(int qty) { 
		wood = wood - qty;
		if (wood < 0) { wood = 0; }
	}
	public void spendGold(int qty) { 
		gold = gold - qty; 
		if (gold < 0) { gold = 0; }
	}
	public void spendStone(int qty) { 
		stone = stone - qty;
		if (stone < 0) { stone = 0; }
	}
	public void usePlus2() { plus2--; }
	
	public void clearUnchosenResources() { unchosenResources = 0; }
	public void clearUnpaidDebts() { unpaidResources = 0; }
	
	public void gainUnchosenResources(int qty) { unchosenResources = unchosenResources + qty; }
	public void gainPoints(int qty) { points = points + qty; }
	public void gainWood(int qty) { wood = wood + qty; }
	public void gainGold(int qty) { gold = gold + qty; }
	public void gainStone(int qty) { stone = stone + qty; }
	public void gainPlus2(int qty) { plus2 = plus2 + qty; }
	
	public boolean canPayCost(Cost cost) {
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
	
	public void payCost(Cost cost) {
		spendWood(cost.getWood());
		spendGold(cost.getGold());
		spendStone(cost.getStone());
		spendPoints(cost.getPoints());
	}

	public void receiveReward(Reward reward) {
		gainWood(reward.getWood());
		gainGold(reward.getGold());
		gainStone(reward.getStone());
		gainPoints(reward.getPoints());
		gainPlus2(reward.getPlus2());
		gainUnchosenResources(reward.getChoose());
	}

	// DOES NOT INCLUDE UNCHOSEN RESOURCES OR UNPAID RESOURCES
	public int countResources() {
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
	public boolean hasStatue() {
		return province.hasBuilding("Statue");
	}
	
	public boolean hasChapel() {
		return province.hasBuilding("Chapel");
	}
	
	public boolean hasChurch() {
		return province.hasBuilding("Church");
	}
	
	public boolean hasCathedral() {
		return province.hasBuilding("Cathedral");
	}
	
	public boolean hasInn() {
		return province.hasBuilding("Inn");
	}
	
	public boolean hasMarket() {
		return province.hasBuilding("Market");
	}
	
	public boolean hasFarms() {
		return province.hasBuilding("Farms");
	}
	
	public boolean hasMerchantsGuild() {
		return province.hasBuilding("Merchants' Guild");
	}
	
	public boolean hasGuardTower() {
		return province.hasBuilding("Guard Tower");
	}
	
	public boolean hasBlacksmith() {
		return province.hasBuilding("Blacksmith");
	}
	
	public boolean hasBarracks() {
		return province.hasBuilding("Barracks");
	}
	
	public boolean hasWizardsGuild() {
		return province.hasBuilding("Wizards' Guild");
	}
	
	public boolean hasPalisade() {
		return province.hasBuilding("Palisade");
	}
	
	public boolean hasStable() {
		return province.hasBuilding("Stable");
	}
	
	public boolean hasStoneWall() {
		return province.hasBuilding("Stone Wall");
	}
	
	public boolean hasFortress() {
		return province.hasBuilding("Fortress");
	}
	
	public boolean hasBarricade() {
		return province.hasBuilding("Barricade");
	}
	
	public boolean hasCrane() {
		return province.hasBuilding("Crane");
	}
	
	public boolean hasTownHall() {
		return province.hasBuilding("Town Hall");
	}
	
	public boolean hasEmbassy() {
		return province.hasBuilding("Embassy");
	}
	
	public int countBuildings() {
		return province.countBuildings();
	}
	
	public boolean canAffordNextBuilding(int row) {
		return province.canAffordNextBuilding(row, countGold(), countWood(), countStone(), hasCrane());
	}
	
	public ProvinceBuilding buyNextBuilding(int row) {
		Cost cost = province.getCostOfNextBuilding(row, hasCrane());
		spendGold(cost.getGold());
		spendWood(cost.getWood());
		spendStone(cost.getStone());
		return province.buyNextBuilding(row, hasCrane());
	}
	
	public ProvinceBuilding loseABuilding() {
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
	
	public abstract void pullSynchronize();
	public abstract void pushUpdate();
}
