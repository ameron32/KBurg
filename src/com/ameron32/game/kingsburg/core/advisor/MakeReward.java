package com.ameron32.game.kingsburg.core.advisor;

import com.ameron32.game.kingsburg.core.advisor.RewardChoice;

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
		
		public RewardBuilder points(int qty) {
			choice.reward_point_gold_wood_stone_choose_soldier_plus2_peek[0] = qty;
			return this;
		}

		public RewardBuilder gold(int qty) {
			choice.reward_point_gold_wood_stone_choose_soldier_plus2_peek[1] = qty;
			return this;
		}

		public RewardBuilder wood(int qty) {
			choice.reward_point_gold_wood_stone_choose_soldier_plus2_peek[2] = qty;
			return this;
		}

		public RewardBuilder stone(int qty) {
			choice.reward_point_gold_wood_stone_choose_soldier_plus2_peek[3] = qty;
			return this;
		}

		public RewardBuilder choose(int qty) {
			choice.reward_point_gold_wood_stone_choose_soldier_plus2_peek[4] = qty;
			return this;
		}

		public RewardBuilder soldier(int qty) {
			choice.reward_point_gold_wood_stone_choose_soldier_plus2_peek[5] = qty;
			return this;
		}

		public RewardBuilder plus2(int qty) {
			choice.reward_point_gold_wood_stone_choose_soldier_plus2_peek[6] = qty;
			return this;
		}

		public RewardBuilder peek() {
			choice.reward_point_gold_wood_stone_choose_soldier_plus2_peek[7] = 1;
			return this;
		}

		public CostBuilder andCost() {
			return new CostBuilder(this);
		}

		public RewardChoice make() {
			return choice;
		}
	}
	
	public static class CostBuilder {
		RewardBuilder rb;
					
		CostBuilder(RewardBuilder rb) {
			this.rb = rb;
		}

		public CostBuilder points(int qty) {
			rb.choice.cost_point_gold_wood_stone_choose[0] = qty;
			return this;
		}

		public CostBuilder gold(int qty) {
			rb.choice.cost_point_gold_wood_stone_choose[1] = qty;
			return this;
		}

		public CostBuilder wood(int qty) {
			rb.choice.cost_point_gold_wood_stone_choose[2] = qty;
			return this;
		}

		public CostBuilder stone(int qty) {
			rb.choice.cost_point_gold_wood_stone_choose[3] = qty;
			return this;
		}

		public CostBuilder choose(int qty) {
			rb.choice.cost_point_gold_wood_stone_choose[4] = qty;
			return this;
		}

		public RewardChoice make() {
			return rb.choice;				
		}
	}
}
