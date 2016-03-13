package com.ameron32.game.kingsburg.core.bot;

import com.ameron32.game.kingsburg.core.Roll;
import com.ameron32.game.kingsburg.core.state.Board;
import com.ameron32.game.kingsburg.core.state.PlayerStuff;

public class DummyBotPlayerProxy implements PlayerProxy {

	@Override
	public void setListener(PlayerProxyListener listener) { /*ignore me*/ }



// SCREEN 1: CHOOSE AN ADVISOR
// ---------------------------
// This screen is probably the MOST complecated in terms of information as well as
// user choices.
// ---------------------------
// Find the corresponding instructions in the PDF rules, on page 3:
// Phase 2: Spring � First Productive Season
// b) Influence the King�s Advisors

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



// SCREEN 2: CHOOSE GOODS
// ---------------------------
// any time a player receives a "green bag" from an advisor or victory against an enemy, 
// the player needs to trade it for 1:1 for a wood, gold, or stone. In the board game,
// this is inherently simple, in the digital game, a screen needs to give the option.
// ---------------------------
// See screen 5 for an inverse functionality screen.

	@Override
	public void onGoodsChoice(int unchosenResourcesCount) {
		// you have gathered some "green bag" selectable resources
		// what resource(s) will you pick?
		// -------------
 
	}




// SCREEN 3a: STATUE ABILITY
// ---------------------------
// Find the [STATUE] building on the player Province Board [aka Player Building Page] image.
// When this ability is available, the user will need to be prompted to decide
// whether to use the Statue or not. In the board game, this is an "oops I forgot" situation
// ALL THE TIME, but in the digital it will be required to decide.
// ---------------------------
// This screen can VERY EASILY be the same or almost the same as 3b: Chapel Ability.

	@Override
	public void onOfferUseStatue(Roll roll) {
		// do you want to use the Statue?
		// -------------
		
	}



// SCREEN 3b: CHAPEL ABILITY
// ---------------------------
// Find the [CHAPEL] building on the player Province Board [aka Player Building Page] image.
// When this ability is available, the user will need to be prompted to decide
// whether to use the Chapel or not. In the board game, this is an "oops I forgot" situation
// ALL THE TIME, but in the digital it will be required to decide.
// ---------------------------
// This screen can VERY EASILY be the same or almost the same as 3a: Statue Ability.

	@Override
	public void onOfferUseChapel(Roll roll) {
		// do you want to use the chapel
		// -------------
		
	}



// SCREEN 4: TOWN HALL ABILITY
// ---------------------------
// Find the [TOWN HALL] building on the player Province Board [aka Player Building Page] image.
// When this ability is available, the user will need to be prompted to decide
// whether to use the Town Hall or not. In the board game, this is an "oops I forgot" situation
// ALL THE TIME, but in the digital it will be required to decide what resource to spend OR to skip it.

	@Override
	public void onOfferUseTownHall(PlayerStuff stuff) {
		// do you want to use the Town Hall to gain a victory point?
		// if yes, you'll onChooseLosses next
		// -------------
		
	}




// SCREEN 5: SPEND RESOURCES
// ---------------------------
// On a very rare occasion, a "green bag" resource must be spent. An example is the "Town Hall",
// another example is losing to a certain enemy.
// ---------------------------
// See screen 2 for an inverse functionality screen.

	@Override
	public void onChooseSpentResources(int unchosenLossesCount, PlayerStuff stuff) {
		// when you lose to an enemy that costs a "green bag" selectable resource,
		// or when you choose to spend a resource on the Town Hall,
		// or when you spend resources onRecruitOption,
		// which resource(s) do you pick?
		// -------------
		
	}




// SCREEN 6: RECRUIT SOLDIERS
// ---------------------------
// Find the corresponding instructions in the PDF rules, on page 6:
// Phase 7: Recruit Soldiers

	@Override
	public void onRecruitOption(PlayerStuff stuff) {
		// how much will you spend on soldiers this year?
		// -------------
		// you can check your stuff
		// I should probably provide an "enemy strength range" 
		// since that probably matters to making a good decision.
		
	}




// SCREEN 7: BUILD BUILDING(S)
// ---------------------------
// Find the corresponding instructions in the PDF rules, on page 5:
// Phase 2: Spring � First Productive Season
// d) Construct Buildings and Building Actions

	@Override
	public void onBuildOption(PlayerStuff stuff) {
		// what will you build?
		// -------------
		// you can check "what do I already have?"
		// you can check "can I afford _________?", etc.
		
	}

	
	
// SCREEN 8: PEEK AT ENEMY CARD
// ---------------------------
// Display enemy card to player
	
	@Override
	public void onPeek() {
		// look at the enemy card
		
	}
}