package com.ameron32.game.kingsburg.core.bot;
import com.ameron32.game.kingsburg.core.Printer;
import com.ameron32.game.kingsburg.core.Roll;
import com.ameron32.game.kingsburg.core.advisor.*;
import com.ameron32.game.kingsburg.core.state.Board;
import com.ameron32.game.kingsburg.core.state.PlayerStuff;
import com.ameron32.game.kingsburg.core.state.ProvinceBoard;
import com.ameron32.game.kingsburg.core.state.ProvinceBuilding;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * IMPLEMENTATION FOR TESTING PURPOSES ONLY
 * RandomTestPlayerProxy handles the necessary user options in a careless way.
 * -----------------
 * Uses the entire dice roll to one advisor OR skips the turn. Very dumb, but easy to test.
 * 
 */
public class BasicBotPlayerProxy implements PlayerProxy {

	private static final int HUMAN_DELAY = 10;
	private static final boolean SPEED_UP = true;
	
	PlayerProxyListener listener;
	int goldCount = 0;
	int woodCount = 0;
	int stoneCount = 0;
	
	public PlayerProxyListener getListener() {
		return listener;
	}
	
	@Override
	public void setListener(PlayerProxyListener listener) {
		this.listener = listener;
	}	
	
	@Override
	public void onPeek() {
		// do nothing but describe
		Printer.get().log("     'I am peeking at the enemy.'");
	}
	
	@Override
	public void onAdvisorChoice(Roll roll, Board board, PlayerStuff stuff) {
		botChooseAdvisor(roll, board, stuff);
	}
	
	protected void botChooseAdvisor(Roll roll, Board board, PlayerStuff stuff) {
		try {
			Thread.sleep(HUMAN_DELAY * ((SPEED_UP) ? 1 : 100));
			
			// remember Market
			if (stuff.hasMarket()) {
				// this bot doesn't care
			}
			
			boolean successful = choose(roll, board, stuff);
			if (!successful) {
				Printer.get().log("     'I feel like I had options, but I gave up.' says Player " + (stuff.getPlayerId()+1) + ".\n");
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			
		}
	}

	@Override
	public void onGoodsChoice(int unchosenResourcesCount) {
		botChooseGoods(unchosenResourcesCount);
	}
	
	protected void botChooseGoods(int unchosenResourcesCount) {
		try {
			Thread.sleep(HUMAN_DELAY * ((SPEED_UP) ? 1 : 100));
			
			//balance the selection of resources evenly
			int goldChoice = 0, woodChoice = 0, stoneChoice = 0;
			for (int i = 0; i < unchosenResourcesCount; i++) {
				if (shouldChooseGold()) {
					goldCount++;
					goldChoice++;
				}
				if (shouldChooseWood()) {
					woodCount++;
					woodChoice++;
				}
				if (shouldChooseStone()) {
					stoneCount++;
					stoneChoice++;
				}
			}
			getListener().onGoodsSelection(Reward.make(goldChoice, woodChoice, stoneChoice));
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	@Override
	public void onChooseSpentResources(int unchosenLossesCount, PlayerStuff stuff) {
		botChooseResourcesToSpend(unchosenLossesCount, stuff);	
	}
	
	protected void botChooseResourcesToSpend(int unchosenLossesCount, PlayerStuff stuff) {
		// TODO: does this AI decision REALLY make sense?
		try {
			Thread.sleep(HUMAN_DELAY * ((SPEED_UP) ? 1 : 100));
			
			//balance the selection of resources evenly
			int goldChoice = 0, woodChoice = 0, stoneChoice = 0;
			for (int i = 0; i < unchosenLossesCount; i++) {
				if (!shouldChooseGold()) {
					goldCount--;
					goldChoice++;
				}
				if (!shouldChooseWood()) {
					woodCount--;
					woodChoice++;
				}
				if (!shouldChooseStone()) {
					stoneCount--;
					stoneChoice++;
				}
			}
			getListener().onLossesSelection(Cost.make(goldChoice, woodChoice, stoneChoice));
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			
		}	
	}

	@Override
	public void onBuildOption(PlayerStuff stuff) {
		botDecideBuilding(stuff);
	}
	
	protected void botDecideBuilding(PlayerStuff stuff) {
		try {
			Thread.sleep(HUMAN_DELAY * ((SPEED_UP) ? 1 : 100));
			
			//build first affordable building
			ProvinceBuilding building = null;
			for (int i = 0; i < ProvinceBoard.TOTAL_ROWS; i++) {
				if (building == null) {
					if (stuff.canAffordNextBuilding(i)) {
						building = stuff.buyNextBuilding(i);
					}
				}
			}
			
			List<ProvinceBuilding> buildings = new ArrayList<>(1);
			if (building != null) {
				buildings.add(building);
			}
			
			getListener().onBuild(buildings);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			
		}
	}

	@Override
	public void onOfferUseStatue(Roll roll) {
		botDecideUseStatue(roll);
	}

	protected void botDecideUseStatue(Roll roll) {
		if (roll.getUnusedTotal() < 12) {
			// if a low roll, then yes
			int lowestNumber = 31000;
			int diePosition = 0;
			List<Long> allUnusedDice = roll.getAllUnusedDice();
			for (int position = 0; position < allUnusedDice.size(); position++) {
				int dieValue = allUnusedDice.get(position).intValue();
				if (dieValue < lowestNumber) {
					lowestNumber = dieValue;
					diePosition = position;
				}
			}
			getListener().onUseStatueResponse(true, roll, diePosition);
		} else {
			// if a high roll, then no
			getListener().onUseStatueResponse(false, roll, 0);
		}		
	}

	@Override
	public void onOfferUseChapel(Roll roll) {
		botDecideUseChapel(roll);
	}
	
	protected void botDecideUseChapel(Roll roll) {
		// always yes
		getListener().onUseChapelResponse(true, roll);
	}
	
	@Override
	public void onRecruitOption(PlayerStuff stuff) {
		botDecideRecruit(stuff);
	}
	
	protected void botDecideRecruit(PlayerStuff stuff) {
		//never recruits
		if (stuff.hasBarracks()) {
			// doesn't matter
		}
		getListener().onChooseRecruitQuantity(0);		
	}

	@Override
	public void onOfferUseTownHall(PlayerStuff stuff) {
		botDecideUseTownHall(stuff);
	}
	
	protected void botDecideUseTownHall(PlayerStuff stuff) {
		//always use town hall
		getListener().onUseTownHallResponse(true);
	}
	
	

	private boolean choose(Roll roll, Board board, PlayerStuff stuff) {
		// grab first option carelessly
		int optionChosen = 1;
		int rollTotal = roll.getUnusedTotal();
		if (rollTotal <= 18) {
			// try to use all the dice at the same time
			Advisor advisor = WallOfAdvisors.get().getAdvisorFor(roll.getUnusedTotal());
			if (canUse(advisor, board)) {
				emptyRemainingDice(roll);
				select(advisor, optionChosen);
				return true;
			} else {
				// that advisor is already taken, so Envoy if possible...
				if (stuff.hasEnvoy()) {
					// use my envoy!
					stuff.useEnvoy();
					emptyRemainingDice(roll);
					select(advisor, optionChosen);
					return true;
				} else {
					// ... or change advisor choice to a single dice.
					Printer.get().log("     'Curses, my advisor was reserved!' says Player " + (stuff.getPlayerId()+1) + ".");
					return useFirstDiceOnly(roll, board, stuff);
				}
			}			
		} else {
			return useFirstDiceOnly(roll, board, stuff);
		}
	}
	
	private void select(Advisor advisor, int option) {
		int position = option - 1;
		RewardChoice rewardChoice = advisor.getOptions().get(position);
		// use all dice
		getListener().onAdvisorGiftSelection(advisor, rewardChoice);
	}
	
	private boolean canUse(Advisor advisor, Board board) {
		// check if advisor is taken
		boolean advisorTaken = board.isAdvisorReserved(advisor.getOrdinal());
		if (advisorTaken) {
			return false;
		}
		return true;
	}
	
	private boolean useFirstDiceOnly(Roll roll, Board board, PlayerStuff stuff) {
		// use the first dice with a plus 2 (if available)
		int diePosition = 0;
		Long die = roll.getUnusedStandardDice().get(diePosition);
		boolean andPlus2Token = false;
		if (stuff.countPlus2() > 0) {
			andPlus2Token = true;
		}
		int advisorTotal = die.intValue();
		advisorTotal +=	((andPlus2Token) ? 2 : 0);
		if (andPlus2Token) {
			stuff.usePlus2();
		}
		
		// ADVISOR SELECTION AND COMPENSATION
		Advisor advisor = WallOfAdvisors.get().getAdvisorFor(advisorTotal);
		if (canUse(advisor, board)) {
			RewardChoice rewardChoice = advisor.getOptions().get(0);
			// use dice
			roll.useStandardDice(die);
			getListener().onAdvisorGiftSelection(advisor, rewardChoice);
		} else {
			Printer.get().log("     'But--another one taken?... oh, I pass!' says Player " + (stuff.getPlayerId()+1) + ".");
			emptyRemainingDice(roll);
			skipTurn();
			return false;
		}
		return true;
	}
	
	private void emptyRemainingDice(Roll myRoll) {
		myRoll.useAllDice();
	}
	
	private void skipTurn() {
		getListener().onAdvisorGiftSelection(null, null);
	}

	private boolean shouldChooseGold() {
		return (goldCount == woodCount && woodCount == stoneCount);
	}
	
	private boolean shouldChooseWood() {
		return (goldCount > woodCount);
	}
	
	private boolean shouldChooseStone() {
		return (woodCount > stoneCount);
	}
}