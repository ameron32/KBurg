
public interface PlayerProxy {

	void setListener(PlayerProxyListener listener);
	void onAdvisorChoice(Roll roll);
	void onChooseGoods(int unchosenResourcesCount);
	void onRecruitOption(PlayerStuff stuff);
}
