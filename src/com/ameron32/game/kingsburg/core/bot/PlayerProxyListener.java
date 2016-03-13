package com.ameron32.game.kingsburg.core.bot;
import com.ameron32.game.kingsburg.core.Roll;
import com.ameron32.game.kingsburg.core.advisor.Advisor;
import com.ameron32.game.kingsburg.core.advisor.Cost;
import com.ameron32.game.kingsburg.core.advisor.Reward;
import com.ameron32.game.kingsburg.core.advisor.RewardChoice;
import com.ameron32.game.kingsburg.core.state.ProvinceBuilding;

import java.util.List;

public interface PlayerProxyListener {
	
	void onAdvisorGiftSelection(Advisor advisor, RewardChoice choice);
	void onGoodsSelection(Reward total);
	void onLossesSelection(Cost total);
	void onUseStatueResponse(boolean useStatue, Roll roll, int diePosition);
	void onUseChapelResponse(boolean useChapel, Roll roll);
	void onUseTownHallResponse(boolean useTownHall);
	void onChooseRecruitQuantity(int count);
	void onBuild(List<ProvinceBuilding> buildings);
}
