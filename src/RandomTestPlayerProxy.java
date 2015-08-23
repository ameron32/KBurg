
/**
 * 
 * IMPLEMENTATION FOR TESTING PURPOSES ONLY
 * RandomTestPlayerProxy handles the necessary user options in a careless way.
 * -----------------
 * Uses the entire dice roll to one advisor OR skips the turn. Very dumb, but easy to test.
 * 
 */
public class RandomTestPlayerProxy implements PlayerProxy {

	PlayerProxyListener listener;
	
	@Override
	public void setListener(PlayerProxyListener listener) {
		this.listener = listener;
	}	
	
	@Override
	public void onAdvisorChoice(Roll myRoll) {
		try {
			Thread.sleep(1);
			
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

	@Override
	public void onChooseGoods(int unchosenResourcesCount) {
		//always choose a gold
		listener.onGoodsSelected(new RewardTotal(unchosenResourcesCount, 0, 0));
	}

	@Override
	public void onRecruitOption(PlayerStuff stuff) {
		//never recruits
		listener.onSoldiersRecruited(0);
	}
}
