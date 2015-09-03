package com.ameron32.game.kingsburg.core;

import java.util.List;

/*
 * Before we talk about who the bots influence, first I think we have to
 * consider the goal of the bot. This I think is broken down to 3 things.
 * Highest priority is to obtain VPs. 2nd priority is to obtain end game
 * structures. 3rd Priority (maybe 2.5 priority) is to make sure army is
 * taken care of.
 */

public class SuperBotPlayerProxy implements PlayerProxy {
	
	@Override
	public void setListener(PlayerProxyListener listener) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * For that it checks to see what it can gain from each advisor, and
	 * compares that with what resources it already has. It then looks to see
	 * what structure it can get next with the fewest # of resources. IF it has
	 * already built a structure, the next building in that line gets a higher
	 * priority consideration than buildings in the lower tiers. If for example,
	 * the bot has 3 tier 1 buildings, it decides on what advisor to choose by
	 * looking at available options, calculating possible resource gains,
	 * comparing highest priority structure options, and then selecting the
	 * advisor that brings it the closest to obtaining the easiest of the high
	 * priority structured options. Every X # of turns, army count gets the
	 * highest priority both in structure choice, and influence choice, and it
	 * builds the army that way.
	 */

	/*
	 * POSSIBLE AI LOGIC: At the beginning of the game the bot picks it's
	 * starting resource at random. Then it considers what resource it has looks
	 * at the starter buildings that use this resource, and picks at random, one
	 * of those to get first. If it gets stone it goes after guard tower first.
	 * But if it does not have any options on it's first advisers to get gold,
	 * then it considers what options it does have picks at random. (Keep in
	 * mind this is first influence pick of the game.) If it's able to build, it
	 * does. Then in the next season let's say it has 1 stone and a palisade.
	 * Let’s say it obtains 1 gold. Now it is on its way to build a stable, BUT
	 * it has what it needs RIGHT NOW to build a guard tower. We can say that a
	 * tier 1 building that can be built NOW is equal in priority to a tier 2
	 * building that can maybe be built in the next productive season. When the
	 * bot has more than 1 option to choose from that are the same level of
	 * priority, it picks one of those options at random. i think from this
	 * point on, we can pick up the logic with my macro ideas to guide it. This
	 * is very specific in the first few rounds, until the bot has built
	 * something to guide it for the rest of the game and it allows for random
	 * strategy and hopefully, less predictable play patterns that still result
	 * in a reasonable intelligent AI.
	 */

	@Override
	public void onAdvisorChoice(Roll roll, Board board, PlayerStuff stuff) {
		/**
		 * First the bot has to look at the die roll and take into account
		 * structures that affect choices, and +2 tokens. (What is possible?) So
		 * the bot first checks die roll, then looks for structures like Market,
		 * then looks to see if it has any +2 tokens. After it gets all this
		 * information, it checks to see what options are possible on the board,
		 * then checks to see if any of these options have been taken by other
		 * players (unless it has the envoy). Then it has to decide, of these
		 * options, what is the best choice for it’s goals.
		 */
		int totalOfRemainingDice = roll.getUnusedTotal();
		List<Long> unusedStandardDice = roll.getUnusedStandardDice();
		List<Long> unusedBonusDice = roll.getUnusedBonusDice();
		boolean hasMarket = stuff.hasMarket();
		int plus2TokenCount = stuff.countPlus2();
		boolean hasEnvoy = stuff.hasEnvoy();


		List<Integer> allAdvisorOptions = getAllPossibleAdvisorOptions();
		removeReservedAdvisors(allAdvisorOptions, hasEnvoy);
		Advisor bestGoalAdvisor = determineBestAdvisorForGoals(allAdvisorOptions);

	}
	
	
	private List<Integer> getAllPossibleAdvisorOptions() {
		// TODO determine all combinations of advisors that could be used
		return null;
	}
	
	private void removeReservedAdvisors(List<Integer> advisorOptions, boolean hasEnvoy) {
		// if hasEnvoy, don’t remove the advisors
		if (hasEnvoy) {
			return;
		}
		// TODO remove reserved advisors
		
	}
	
	private Advisor determineBestAdvisorForGoals(List<Integer> allAdvisorOptions) {
		// TODO discussion involved StrategicValues object and possibly List<ProvinceBuilding> priorities
		return null;
	}


	@Override
	public void onGoodsChoice(int unchosenResourcesCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOfferUseStatue(Roll roll) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOfferUseChapel(Roll roll) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOfferUseTownHall(PlayerStuff stuff) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChooseSpentResources(int unchosenLossesCount, PlayerStuff stuff) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRecruitOption(PlayerStuff stuff) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBuildOption(PlayerStuff stuff) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onPeek() {
		// TODO Auto-generated method stub
		
	}
}
