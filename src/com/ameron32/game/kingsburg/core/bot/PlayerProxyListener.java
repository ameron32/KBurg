package com.ameron32.game.kingsburg.core.bot;
import com.ameron32.game.kingsburg.core.Roll;
import com.ameron32.game.kingsburg.core.advisor.Advisor;
import com.ameron32.game.kingsburg.core.advisor.Cost;
import com.ameron32.game.kingsburg.core.advisor.Reward;
import com.ameron32.game.kingsburg.core.advisor.RewardChoice;
import com.ameron32.game.kingsburg.core.state.ProvinceBuilding;

import java.util.List;

public interface PlayerProxyListener {
	
	void onAdvisorGiftSelection(int player, Advisor advisor, RewardChoice choice);
	void onGoodsSelection(int player, Reward total);
	void onLossesSelection(int player, Cost total);
	void onUseStatueResponse(int player, boolean useStatue, Roll roll, int diePosition);
	void onUseChapelResponse(int player, boolean useChapel, Roll roll);
	void onUseTownHallResponse(int player, boolean useTownHall);
	void onChooseRecruitQuantity(int player, int count);
	void onBuild(int player, List<ProvinceBuilding> buildings);
}
