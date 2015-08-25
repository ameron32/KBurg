
public interface PlayerProxy {

	void setListener(PlayerProxyListener listener);
	void onAdvisorChoice(Roll roll);
	void onChooseGoods(int unchosenResourcesCount);
	void onChooseLosses(int unchosenLossesCount, PlayerStuff stuff);
	void onRecruitOption(PlayerStuff stuff);
	void onBuildOption(PlayerStuff stuff);
}
