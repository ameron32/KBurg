import java.util.List;

public interface PlayerProxyListener {
	
	/**
	 * WARNING: MUST USE roll.useStandardDice()
	 * @param advisor
	 * @param choice
	 */
	void onRewardChoiceSelected(Advisor advisor, RewardChoice choice);
	void onGoodsSelected(Reward total);
	void onLossesSelected(Cost total);
	void onUseStatueResponse(boolean useStatue);
	void onUseChapelResponse(boolean useChapel);
	void onSoldiersRecruited(int count);
	void onBuild(List<ProvinceBuilding> buildings);
}
