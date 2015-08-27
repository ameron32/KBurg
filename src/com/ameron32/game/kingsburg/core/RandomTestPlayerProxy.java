package com.ameron32.game.kingsburg.core;
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
public class RandomTestPlayerProxy implements PlayerProxy {

	private static final int HUMAN_DELAY = 10;
	private static final boolean SPEED_UP = true;
	
	PlayerProxyListener listener;
	int goldCount = 0;
	int woodCount = 0;
	int stoneCount = 0;
	
	@Override
	public void setListener(PlayerProxyListener listener) {
		this.listener = listener;
	}	
	
	@Override
	public void onAdvisorChoice(Roll myRoll, PlayerStuff stuff) {
		try {
			Thread.sleep(HUMAN_DELAY * ((SPEED_UP) ? 1 : 1000));
			
			// remember Market
			if (stuff.hasMarket()) {
				// this bot doesn't care
			}
			
			// grab first option carelessly
			int rollTotal = myRoll.getUnusedTotal();
			if (rollTotal <= 18) {
				// ADVISOR SELECTION AND COMPENSATION
				Advisor advisor = WallOfAdvisors.getAdvisorFor(rollTotal);
				RewardChoice rewardChoice = advisor.getOptions().get(0);
				// use all dice
				myRoll.useAllDice();
				listener.onRewardChoiceSelected(advisor, rewardChoice);
			} else {
				// use the first dice with a plus 2 (if available)
				int diePosition = 0;
				Long die = myRoll.getUnusedStandardDice().get(diePosition);
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
				Advisor advisor = WallOfAdvisors.getAdvisorFor(advisorTotal);
				RewardChoice rewardChoice = advisor.getOptions().get(0);
				// use dice
				myRoll.useStandardDice(die);
				listener.onRewardChoiceSelected(advisor, rewardChoice);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			
		}
	}

	@Override
	public void onChooseGoods(int unchosenResourcesCount) {
		try {
			Thread.sleep(HUMAN_DELAY * ((SPEED_UP) ? 1 : 1000));
			
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
			listener.onGoodsSelected(new Reward(goldChoice, woodChoice, stoneChoice));
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	@Override
	public void onChooseLosses(int unchosenLossesCount, PlayerStuff stuff) {
		// TODO: does this AI decision REALLY make sense?
		try {
			Thread.sleep(HUMAN_DELAY * ((SPEED_UP) ? 1 : 1000));
			
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
			listener.onLossesSelected(new Cost(goldChoice, woodChoice, stoneChoice));
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			
		}		
	}
	
	@Override
	public void onBuildOption(PlayerStuff stuff) {
		try {
			Thread.sleep(HUMAN_DELAY * ((SPEED_UP) ? 1 : 1000));
			
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
			
			listener.onBuild(buildings);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	@Override
	public void onOfferUseStatue(Roll roll) {
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
			listener.onUseStatueResponse(true, roll, diePosition);
		} else {
			// if a high roll, then no
			listener.onUseStatueResponse(false, roll, 0);
		}
	}

	@Override
	public void onOfferUseChapel(Roll roll) {
		// always yes
		listener.onUseChapelResponse(true, roll);
	}
	
	@Override
	public void onRecruitOption(PlayerStuff stuff) {
		//never recruits
		if (stuff.hasBarracks()) {
			// doesn't matter
		}
		listener.onSoldiersRecruited(0);
	}
	
	@Override
	public void onOfferUseTownHall(PlayerStuff stuff) {
		//always use town hall
		listener.onUseTownHallResponse(true);
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
