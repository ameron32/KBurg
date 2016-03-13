package com.ameron32.game.kingsburg.core.bot;

import com.ameron32.game.kingsburg.core.Roll;
import com.ameron32.game.kingsburg.core.state.Board;
import com.ameron32.game.kingsburg.core.state.PlayerStuff;

public interface PlayerProxy {

	void setListener(PlayerProxyListener listener);
	void onAdvisorChoice(Roll roll, Board board, PlayerStuff stuff);
	void onGoodsChoice(int unchosenResourcesCount);
	void onOfferUseStatue(Roll roll);
	void onOfferUseChapel(Roll roll);
	void onOfferUseTownHall(PlayerStuff stuff);
	void onChooseSpentResources(int unchosenLossesCount, PlayerStuff stuff);
	void onRecruitOption(PlayerStuff stuff);
	void onBuildOption(PlayerStuff stuff);
	void onPeek();
}
