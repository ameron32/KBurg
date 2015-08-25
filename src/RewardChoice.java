
/**
 * 
 * CONTAINS choices when an Advisor is used. Each RewardChoice contains 
 * all the rewards, costs, and implications of using that advisor in one scenario.
 *
 */
public class RewardChoice {
	
	public static RewardChoice createEmpty() {
		return new RewardChoice();
	}
	
	//trade is cost & reward
	int[] cost_point_gold_wood_stone_choose = new int[5];
	int[] reward_point_gold_wood_stone_choose_soldier_plus2_peek = new int[9];
	
	private RewardChoice() {
		super();
	}

	@Override
	public String toString() {
//		String rewards = "reward of: [";
//		for (int i = 0; i < 9; i++) {
//			if (reward_point_gold_wood_stone_choose_soldier_plus2_peek[i] != 0) {
//				rewards += getLabel(i) + reward_point_gold_wood_stone_choose_soldier_plus2_peek[i] + " ";
//			}
//		}
//		rewards += "] ";
//		String costs = "costs of: [";
//		for (int i = 0; i < 5; i++) {
//			if (cost_point_gold_wood_stone_choose[i] != 0) {
//				costs += getLabel(i) + cost_point_gold_wood_stone_choose[i] + " ";
//			}
//		}
//		rewards += "] ";
//		
//		return "RewardChoice [" + 
//				rewards + costs + "]";
		return getHumanReadableReward();
	}
	
	private String getLabel(int position) {
		switch (position) {
		case 0:
			return "points: ";
		case 1:
			return "gold: ";
		case 2:
			return "wood: ";
		case 3:
			return "stone: ";
		case 4:
			return "choice of resources: ";
		case 5:
			return "soldiers: ";
		case 6:
			return "plus2: ";
		case 7:
			return "peek: ";
		default:
			return "bad position!";
		}
	}
	
	String getHumanReadableReward() {
		String rewards = "reward(s): ";
		for (int i = 0; i < 9; i++) {
			if (reward_point_gold_wood_stone_choose_soldier_plus2_peek[i] != 0) {
				rewards += getLabel(i) + reward_point_gold_wood_stone_choose_soldier_plus2_peek[i] + " ";
			}
		}
		rewards += " ";
		if (hasAnyCost()) {
			String costs = "but also costs: ";
			for (int i = 0; i < 5; i++) {
				if (cost_point_gold_wood_stone_choose[i] != 0) {
					costs += getLabel(i) + cost_point_gold_wood_stone_choose[i] + " ";
				}
			}
			rewards += costs + " ";	
		}
		return rewards;
	}
	
	boolean hasAnyCost() {
		for (int i : cost_point_gold_wood_stone_choose) {
			if (i != 0) { 
				return true; 
			}
		}
		return false;
	}
	
	Cost getCost() {
		int point = cost_point_gold_wood_stone_choose[0];
		int gold = cost_point_gold_wood_stone_choose[1];
		int wood = cost_point_gold_wood_stone_choose[2];
		int stone = cost_point_gold_wood_stone_choose[3];
		int choose = cost_point_gold_wood_stone_choose[4];
		return new Cost(gold, wood, stone, point, choose);
	}
	
	Reward getReward() {
		int point = reward_point_gold_wood_stone_choose_soldier_plus2_peek[0];
		int gold = reward_point_gold_wood_stone_choose_soldier_plus2_peek[1];
		int wood = reward_point_gold_wood_stone_choose_soldier_plus2_peek[2];
		int stone = reward_point_gold_wood_stone_choose_soldier_plus2_peek[3];
		int choose = reward_point_gold_wood_stone_choose_soldier_plus2_peek[4];
		int soldier = reward_point_gold_wood_stone_choose_soldier_plus2_peek[5];
		int plus2 = reward_point_gold_wood_stone_choose_soldier_plus2_peek[6];
		int peek = reward_point_gold_wood_stone_choose_soldier_plus2_peek[7];
		return new Reward(gold, wood, stone, point, choose, soldier, plus2, peek);
	}
}
