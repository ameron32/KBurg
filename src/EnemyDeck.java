import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EnemyDeck {

	private static final List<EnemyCard> allCards = new ArrayList<EnemyCard>(25);
	static {
		allCards.add(new EnemyCard("Barbarians", 1, 2, 
				Reward.withReward().gold(1).make(), 
				Reward.withReward().andCost().choose(1).make(), true));
		allCards.add(new EnemyCard("Goblins", 1, 2, 
				Reward.withReward().wood(1).make(), 
				Reward.withReward().andCost().points(1).wood(1).stone(1).make(), true));
		allCards.add(new EnemyCard("Goblins", 1, 3, 
				Reward.withReward().stone(1).make(), 
				Reward.withReward().andCost().gold(1).make(), true));
		allCards.add(new EnemyCard("Orcs", 1, 3, 
				Reward.withReward().gold(1).make(), 
				Reward.withReward().andCost().points(1).choose(2).make(), false));
		allCards.add(new EnemyCard("Zombies", 1, 4, 
				Reward.withReward().points(1).make(),
				Reward.withReward().andCost().points(2).make(), false));
		allCards.add(new EnemyCard("Goblins", 2, 3, 
				Reward.withReward().wood(1).make(),
				Reward.withReward().andCost().points(1).wood(2).stone(2).make(), true));
		allCards.add(new EnemyCard("Goblins", 2, 4,
				Reward.withReward().stone(1).make(),
				Reward.withReward().andCost().gold(2).make(), true));
		allCards.add(new EnemyCard("Orcs", 2, 4,
				Reward.withReward().gold(1).make(),
				Reward.withReward().andCost().choose(2).make(), true));
		allCards.add(new EnemyCard("Barbarians", 2, 5,
				Reward.withReward().gold(1).make(),
				Reward.withReward().andCost().points(1).choose(2).make(), false));
		allCards.add(new EnemyCard("Zombies", 2, 5,
				Reward.withReward().points(1).make(),
				Reward.withReward().andCost().points(2).make(), false));
		allCards.add(new EnemyCard("Goblins", 3, 4,
				Reward.withReward().wood(1).make(),
				Reward.withReward().andCost().points(1).wood(3).stone(3).make(), true));
		allCards.add(new EnemyCard("Goblins", 3, 5,
				Reward.withReward().stone(1).make(),
				Reward.withReward().andCost().gold(3).make(), true));
		allCards.add(new EnemyCard("Orcs", 3, 5,
				Reward.withReward().gold(1).make(),
				Reward.withReward().andCost().choose(2).make(), true));
		allCards.add(new EnemyCard("Demons", 3, 6, 
				Reward.withReward().points(1).make(),
				Reward.withReward().andCost().points(1).gold(2).wood(1).stone(1).make(), false));
		allCards.add(new EnemyCard("Zombies", 3, 6, 
				Reward.withReward().points(1).make(),
				Reward.withReward().andCost().points(2).make(), false));
		allCards.add(new EnemyCard("Goblins", 4, 5, 
				Reward.withReward().wood(1).make(), 
				Reward.withReward().andCost().points(1).wood(4).stone(4).make(), true));
		allCards.add(new EnemyCard("Goblins", 4, 6, 
				Reward.withReward().stone(1).make(), 
				Reward.withReward().andCost().gold(4).make(), true));
		allCards.add(new EnemyCard("Demons", 4, 6, 
				Reward.withReward().gold(1).make(),
				Reward.withReward().andCost().choose(4).make(), true));
		allCards.add(new EnemyCard("Orcs", 4, 7, 
				Reward.withReward().choose(1).make(),
				Reward.withReward().andCost().choose(2).make(), true));
		allCards.add(new EnemyCard("Zombies", 4, 7, 
				Reward.withReward().points(1).make(),
				Reward.withReward().andCost().points(2).make(), false));
		allCards.add(new EnemyCard("Barbarians", 5, 7, 
				Reward.withReward().points(1).make(),
				Reward.withReward().andCost().points(8).make(), false));
		allCards.add(new EnemyCard("Barbarians", 5, 8, 
				Reward.withReward().points(1).make(),
				Reward.withReward().andCost().make(), true));
		allCards.add(new EnemyCard("Demons", 5, 8, 
				Reward.withReward().points(2).make(),
				Reward.withReward().andCost().points(2).make(), true));
		allCards.add(new EnemyCard("Demons", 5, 9, 
				Reward.withReward().points(2).make(),
				Reward.withReward().andCost().make(), true));
		allCards.add(new EnemyCard("Dragons", 5, 9, 
				Reward.withReward().points(3).make(),
				Reward.withReward().andCost().points(5).make(), false));
	}
	static EnemyCard getRandomCardForYear(int year) {
		List<EnemyCard> cardsInTheYear = new ArrayList<>();
		for (EnemyCard card : allCards) {
			if (card.getYear() == year) {
				cardsInTheYear.add(card);
			}
		}
		int countOfCards = cardsInTheYear.size();
		int seed = BasicRandomizer.get().nextInt(countOfCards);
		return cardsInTheYear.get(seed);
	}
	
	int years;
		
	EnemyCard[] enemyDeck;
	
	public EnemyDeck(int years) {
		this.years = years;
		enemyDeck = new EnemyCard[years];
		for (int loop = 0; loop < years; loop++) {
			int year = loop + 1;
			enemyDeck[loop] = getRandomCardForYear(year);
		}
	}
	
	EnemyCard getCard(int year) {
		return enemyDeck[year - 1];
	}
	
	@Override
	public String toString() {
		return "EnemyDeck [years=" + years + ", enemyDeck="
				+ Arrays.toString(enemyDeck) + "]";
	}
}
