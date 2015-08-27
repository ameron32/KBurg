package com.ameron32.game.kingsburg.core;

/**
 * 
 * BUILDER CONTAINER CLASS
 * Provides a RewardChoice to include in an Advisor
 * 
 * usage: Reward.withReward().points(1)
 * 			.andCost().gold(1)
 * 			.make();
 *
 */
public class MakeReward {
	public static RewardBuilder withReward() {
		return new RewardBuilder();
	}
	
	public static class RewardBuilder {
		
		RewardChoice choice = RewardChoice.createEmpty();			
		
		RewardBuilder points(int qty) {
			choice.reward_point_gold_wood_stone_choose_soldier_plus2_peek[0] = qty;
			return this;
		}
		
		RewardBuilder gold(int qty) {
			choice.reward_point_gold_wood_stone_choose_soldier_plus2_peek[1] = qty;
			return this;
		}
		
		RewardBuilder wood(int qty) {
			choice.reward_point_gold_wood_stone_choose_soldier_plus2_peek[2] = qty;
			return this;
		}
		
		RewardBuilder stone(int qty) {
			choice.reward_point_gold_wood_stone_choose_soldier_plus2_peek[3] = qty;
			return this;
		}

		RewardBuilder choose(int qty) {
			choice.reward_point_gold_wood_stone_choose_soldier_plus2_peek[4] = qty;
			return this;
		}

		RewardBuilder soldier(int qty) {
			choice.reward_point_gold_wood_stone_choose_soldier_plus2_peek[5] = qty;
			return this;
		}

		RewardBuilder plus2(int qty) {
			choice.reward_point_gold_wood_stone_choose_soldier_plus2_peek[6] = qty;
			return this;
		}

		RewardBuilder peek() {
			choice.reward_point_gold_wood_stone_choose_soldier_plus2_peek[7] = 1;
			return this;
		}
		
		CostBuilder andCost() {
			return new CostBuilder(this);
		}
		
		RewardChoice make() {
			return choice;
		}
	}
	
	public static class CostBuilder {
		RewardBuilder rb;
					
		CostBuilder(RewardBuilder rb) {
			this.rb = rb;
		}
		
		CostBuilder points(int qty) {
			rb.choice.cost_point_gold_wood_stone_choose[0] = qty;
			return this;
		}
		
		CostBuilder gold(int qty) {
			rb.choice.cost_point_gold_wood_stone_choose[1] = qty;
			return this;
		}
		
		CostBuilder wood(int qty) {
			rb.choice.cost_point_gold_wood_stone_choose[2] = qty;
			return this;
		}
		
		CostBuilder stone(int qty) {
			rb.choice.cost_point_gold_wood_stone_choose[3] = qty;
			return this;
		}
		
		CostBuilder choose(int qty) {
			rb.choice.cost_point_gold_wood_stone_choose[4] = qty;
			return this;
		}
		
		RewardChoice make() {
			return rb.choice;				
		}
	}
}
