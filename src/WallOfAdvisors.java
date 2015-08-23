import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * SELECTION CONTAINER intended to house human logic for player options related to Advisor influence.
 * TODO complete method list for Advisors
 * 
 */
public class WallOfAdvisors {
	
	private static final List<Advisor> advisors = new ArrayList<>(18);
	static {
		advisors.add(new Advisor("Jester", 1, 
				Reward.withReward().points(1).make()));
		advisors.add(new Advisor("Squire", 2, 
				Reward.withReward().gold(1).make()));
		advisors.add(new Advisor("Architect", 3, 
				Reward.withReward().wood(1).make()));
		advisors.add(new Advisor("Merchant", 4, 
				Reward.withReward().wood(1).make(), 
				Reward.withReward().gold(1).make()));
		advisors.add(new Advisor("Sergeant", 5,
				Reward.withReward().soldier(1).make()));
		advisors.add(new Advisor("Alchemist", 6,
				Reward.withReward().wood(1).stone(1).andCost().gold(1).make(),
				Reward.withReward().gold(1).stone(1).andCost().wood(1).make(),
				Reward.withReward().gold(1).wood(1).andCost().stone(1).make()));
		advisors.add(new Advisor("Astronomer", 7,
				Reward.withReward().choose(1).plus2(1).make()));
		advisors.add(new Advisor("Treasurer", 8, 
				Reward.withReward().gold(2).make()));
		advisors.add(new Advisor("Master Hunter", 9, 
				Reward.withReward().wood(1).gold(1).make(),
				Reward.withReward().wood(1).stone(1).make()));
		advisors.add(new Advisor("General", 10, 
				Reward.withReward().soldier(2).peek().make()));
		advisors.add(new Advisor("Swordsmith", 11, 
				Reward.withReward().stone(1).wood(1).make(), 
				Reward.withReward().stone(1).gold(1).make()));
		advisors.add(new Advisor("Duchess", 12, 
				Reward.withReward().choose(2).plus2(1).make()));
		advisors.add(new Advisor("Champion", 13, 
				Reward.withReward().stone(3).make()));
		advisors.add(new Advisor("Smuggler", 14,
				Reward.withReward().choose(3).andCost().points(1).make()));
		advisors.add(new Advisor("Inventor", 15, 
				Reward.withReward().gold(1).wood(1).stone(1).make()));
		advisors.add(new Advisor("Wizard", 16, 
				Reward.withReward().gold(4).make()));
		advisors.add(new Advisor("Queen", 17, 
				Reward.withReward().choose(2).peek().points(3).make()));
		advisors.add(new Advisor("King", 18,
				Reward.withReward().gold(1).wood(1).stone(1).soldier(1).make()));
	}
	static List<Advisor> getAdvisors() {
		return advisors;
	}
	static Advisor getAdvisorFor(int number) {
		//returns FIRST advisor to match the number
		for (Advisor advisor : getAdvisors()) {
			if (advisor.getOrdinal() == number) {
				return advisor;
			}
		}
		return null;
	}
}
