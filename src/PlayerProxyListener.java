
public interface PlayerProxyListener {
	
	void onRewardChoiceSelected(Advisor advisor, RewardChoice choice);
	void onGoodsSelected(RewardTotal total);
	void onSoldiersRecruited(int count);
}
