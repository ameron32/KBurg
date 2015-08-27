package com.ameron32.game.kingsburg.core;

public interface PlayerProxy {

	void setListener(PlayerProxyListener listener);
	void onAdvisorChoice(Roll roll, PlayerStuff stuff);
	void onChooseGoods(int unchosenResourcesCount);
	void onOfferUseStatue(Roll roll);
	void onOfferUseChapel(Roll roll);
	void onOfferUseTownHall(PlayerStuff stuff);
	void onChooseLosses(int unchosenLossesCount, PlayerStuff stuff);
	void onRecruitOption(PlayerStuff stuff);
	void onBuildOption(PlayerStuff stuff);
}
