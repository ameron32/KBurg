package com.ameron32.game.kingsburg.core;

public class DummyBotPlayerProxy implements PlayerProxy {

	@Override
	public void setListener(PlayerProxyListener listener) { /*ignore me*/ }

	@Override
	public void onAdvisorChoice(Roll roll, Board board, PlayerStuff stuff) {
		// which advisor do you choose?
		// also, which option do you pick from that advisor?
		// -------------
		// the most important piece of information is:
		// "what did I roll?" which is available in the roll object.
		// -------------
		// you can check your stuff for a lot of stuff, 
		// like "do I have a market? how many buildings do I have? etc."
		// or "how much of a resource, all resources, points do I have?"
		// this is available in the stuff object
		
	}

	@Override
	public void onGoodsChoice(int unchosenResourcesCount) {
		// you have gathered some "green bag" selectable resources
		// what resource(s) will you pick?
		// -------------
 
	}

	@Override
	public void onOfferUseStatue(Roll roll) {
		// do you want to use the Statue?
		// -------------
		
	}

	@Override
	public void onOfferUseChapel(Roll roll) {
		// do you want to use the chapel
		// -------------
		
	}

	@Override
	public void onOfferUseTownHall(PlayerStuff stuff) {
		// do you want to use the Town Hall to gain a victory point?
		// if yes, you'll onChooseLosses next
		// -------------
		
	}

	@Override
	public void onChooseSpentResources(int unchosenLossesCount, PlayerStuff stuff) {
		// when you lose to an enemy that costs a "green bag" selectable resource,
		// or when you choose to spend a resource on the Town Hall,
		// or when you spend resources onRecruitOption,
		// which resource(s) do you pick?
		// -------------
		
	}

	@Override
	public void onRecruitOption(PlayerStuff stuff) {
		// how much will you spend on soldiers this year?
		// -------------
		// you can check your stuff
		// I should probably provide an "enemy strength range" 
		// since that probably matters to making a good decision.
		
	}

	@Override
	public void onBuildOption(PlayerStuff stuff) {
		// what will you build?
		// -------------
		// you can check "what do I already have?"
		// you can check "can I afford _________?", etc.
		
	}
}
