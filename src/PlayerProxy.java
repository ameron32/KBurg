
public interface PlayerProxy {

	void setListener(PlayerProxyListener listener);
	void onAdvisorChoice(Roll roll, PlayerStuff stuff);
	void onChooseGoods(int unchosenResourcesCount);
	void onOfferUseStatue();
	void onOfferUseChapel();
	void onChooseLosses(int unchosenLossesCount, PlayerStuff stuff);
	void onRecruitOption(PlayerStuff stuff);
	void onBuildOption(PlayerStuff stuff);
}
