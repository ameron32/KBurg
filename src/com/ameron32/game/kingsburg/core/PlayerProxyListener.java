package com.ameron32.game.kingsburg.core;
import java.util.List;

public interface PlayerProxyListener {
	
	/**
	 * WARNING: MUST USE roll.useStandardDice()
	 * @param advisor
	 * @param choice
	 */
	void onAdvisorGiftSelection(Advisor advisor, RewardChoice choice);
	void onGoodsSelection(Reward total);
	void onLossesSelection(Cost total);
	void onUseStatueResponse(boolean useStatue, Roll roll, int diePosition);
	void onUseChapelResponse(boolean useChapel, Roll roll);
	void onUseTownHallResponse(boolean useTownHall);
	void onChooseRecruitQuantity(int count);
	void onBuild(List<ProvinceBuilding> buildings);
}
