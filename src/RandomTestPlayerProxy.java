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
	
	@Override
	public void setListener(PlayerProxyListener listener) {
		this.listener = listener;
	}	
	
	@Override
	public void onAdvisorChoice(Roll myRoll) {
		try {
			Thread.sleep(HUMAN_DELAY * ((SPEED_UP) ? 1 : 1000));
			
			//do logic
			int rollTotal = myRoll.getRoll();
			if (rollTotal <= 18) {
				// ADVISOR SELECTION AND COMPENSATION
				// grab first option carelessly
				Advisor advisor = WallOfAdvisors.getAdvisorFor(rollTotal);
				RewardChoice rewardChoice = advisor.getOptions().get(0);
				listener.onRewardChoiceSelected(advisor, rewardChoice);
			} else {
				// skip the turn
				listener.onRewardChoiceSelected(null, null);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	int goldCount = 0, woodCount = 0, stoneCount = 0;

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
	
	private boolean shouldChooseGold() {
		return (goldCount == woodCount && woodCount == stoneCount);
	}
	
	private boolean shouldChooseWood() {
		return (goldCount > woodCount);
	}
	
	private boolean shouldChooseStone() {
		return (woodCount > stoneCount);
	}
	
	@Override
	public void onRecruitOption(PlayerStuff stuff) {
		//never recruits
		listener.onSoldiersRecruited(0);
	}

	@Override
	public void onBuildOption(PlayerStuff stuff) {
		try {
			Thread.sleep(HUMAN_DELAY * ((SPEED_UP) ? 1 : 1000));
			
			//build first affordable building
			ProvinceBuilding building = null;
			for (int i = 0; i < ProvinceBoard.TOTAL_ROWS; i++) {
				if (stuff.canAffordNextBuilding(i)) {
					building = stuff.buyNextBuilding(i);
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
}
