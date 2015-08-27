package com.ameron32.game.kingsburg.core;
import java.util.List;

public interface PlayerProxyListener {
	
	void onRewardChoiceSelected(Advisor advisor, RewardChoice choice);
	void onGoodsSelected(Reward total);
	void onLossesSelected(Cost total);
	void onUseStatueResponse(boolean useStatue, Roll roll, int diePosition);
	void onUseChapelResponse(boolean useChapel, Roll roll);
	void onUseTownHallResponse(boolean useTownHall);
	void onSoldiersRecruited(int count);
	void onBuild(List<ProvinceBuilding> buildings);
}
