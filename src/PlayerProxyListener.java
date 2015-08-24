import java.util.List;

public interface PlayerProxyListener {
	
	void onRewardChoiceSelected(Advisor advisor, RewardChoice choice);
	void onGoodsSelected(RewardTotal total);
	void onSoldiersRecruited(int count);
	void onBuild(List<ProvinceBuilding> buildings);
}
