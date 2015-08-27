package com.ameron32.game.kingsburg.core;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EnemyDeck {

	private static final List<EnemyCard> allCards = new ArrayList<EnemyCard>(25);
	static {
		// USE ENEMYCARDBUILDER
		allCards.add(EnemyCardBuilder.with()
				.name("Barbarians")
				.year(1).strength(2)
				.victory(MakeReward.withReward().gold(1).make())
				.defeat(MakeReward.withReward().andCost().choose(1).make())
				.andLoseABuilding()
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Goblins").isGoblin()
				.year(1).strength(2)
				.victory(MakeReward.withReward().wood(1).make())
				.defeat(MakeReward.withReward().andCost().points(1).wood(1).stone(1).make())
				.andLoseABuilding()
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Goblins").isGoblin()
				.year(1).strength(3)
				.victory(MakeReward.withReward().stone(1).make())
				.defeat(MakeReward.withReward().andCost().gold(1).make())
				.andLoseABuilding()
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Orcs")
				.year(1).strength(3)
				.victory(MakeReward.withReward().gold(1).make())
				.defeat(MakeReward.withReward().andCost().points(1).choose(2).make())
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Zombies").isZombie()
				.year(1).strength(4)
				.victory(MakeReward.withReward().points(1).make())
				.defeat(MakeReward.withReward().andCost().points(2).make())
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Goblins").isGoblin()
				.year(2).strength(3)
				.victory(MakeReward.withReward().wood(1).make())
				.defeat(MakeReward.withReward().andCost().points(1).wood(2).stone(2).make())
				.andLoseABuilding()
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Goblins").isGoblin()
				.year(2).strength(4)
				.victory(MakeReward.withReward().stone(1).make())
				.defeat(MakeReward.withReward().andCost().gold(2).make())
				.andLoseABuilding()
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Orcs")
				.year(2).strength(4)
				.victory(MakeReward.withReward().gold(1).make())
				.defeat(MakeReward.withReward().andCost().choose(2).make())
				.andLoseABuilding()
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Barbarians")
				.year(2).strength(5)
				.victory(MakeReward.withReward().gold(1).make())
				.defeat(MakeReward.withReward().andCost().points(1).choose(2).make())
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Zombies").isZombie()
				.year(2).strength(5)
				.victory(MakeReward.withReward().points(1).make())
				.defeat(MakeReward.withReward().andCost().points(2).make())
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Goblins").isGoblin()
				.year(3).strength(4)
				.victory(MakeReward.withReward().wood(1).make())
				.defeat(MakeReward.withReward().andCost().points(1).wood(3).stone(3).make())
				.andLoseABuilding()
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Goblins").isGoblin()
				.year(3).strength(5)
				.victory(MakeReward.withReward().stone(1).make())
				.defeat(MakeReward.withReward().andCost().gold(3).make())
				.andLoseABuilding()
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Orcs")
				.year(3).strength(5)
				.victory(MakeReward.withReward().gold(1).make())
				.defeat(MakeReward.withReward().andCost().choose(2).make())
				.andLoseABuilding()
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Demons").isDemon()
				.year(3).strength(6)
				.victory(MakeReward.withReward().points(1).make())
				.defeat(MakeReward.withReward().andCost().points(1).gold(2).wood(1).stone(1).make())
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Zombies").isZombie()
				.year(3).strength(6)
				.victory(MakeReward.withReward().points(1).make())
				.defeat(MakeReward.withReward().andCost().points(2).make())
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Goblins").isGoblin()
				.year(4).strength(5)
				.victory(MakeReward.withReward().wood(1).make())
				.defeat(MakeReward.withReward().andCost().points(1).wood(4).stone(4).make())
				.andLoseABuilding()
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Goblins").isGoblin()
				.year(4).strength(6)
				.victory(MakeReward.withReward().stone(1).make())
				.defeat(MakeReward.withReward().andCost().gold(4).make())
				.andLoseABuilding()
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Demons").isDemon()
				.year(4).strength(6)
				.victory(MakeReward.withReward().gold(1).make())
				.defeat(MakeReward.withReward().andCost().choose(4).make())
				.andLoseABuilding()
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Orcs")
				.year(4).strength(7)
				.victory(MakeReward.withReward().choose(1).make())
				.defeat(MakeReward.withReward().andCost().choose(2).make())
				.andLoseABuilding()
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Zombies").isZombie()
				.year(4).strength(7)
				.victory(MakeReward.withReward().points(1).make())
				.defeat(MakeReward.withReward().andCost().points(2).make())
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Barbarians")
				.year(5).strength(7)
				.victory(MakeReward.withReward().points(1).make())
				.defeat(MakeReward.withReward().andCost().points(8).make())
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Barbarians")
				.year(5).strength(8)
				.victory(MakeReward.withReward().points(1).make())
				.defeat(MakeReward.withReward().andCost().make())
				.andLoseABuilding()
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Demons").isDemon()
				.year(5).strength(8)
				.victory(MakeReward.withReward().points(2).make())
				.defeat(MakeReward.withReward().andCost().points(2).make())
				.andLoseABuilding()
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Demons").isDemon()
				.year(5).strength(9)
				.victory(MakeReward.withReward().points(2).make())
				.defeat(MakeReward.withReward().andCost().make())
				.andLoseABuilding()
				.make());
		allCards.add(EnemyCardBuilder.with()
				.name("Dragons")
				.year(5).strength(9)
				.victory(MakeReward.withReward().points(3).make())
				.defeat(MakeReward.withReward().andCost().points(5).make())
				.make());
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
//		return "EnemyDeck [years=" + years + ", enemyDeck="
//				+ Arrays.toString(enemyDeck) + "]";
		return toHumanReadableString();
	}
	
	public String toHumanReadableString() {
		return "EnemyDeck (" + years + ")\n"
				+ Arrays.toString(enemyDeck);
	}
	
	
	
	public static class EnemyCardBuilder {
	
		String name;
		int year;
		int strength;
		RewardChoice victory;
		RewardChoice defeat;
		boolean defeatLoseBuilding;
		boolean isGoblin, isZombie, isDemon;
		
		public static EnemyCardBuilder with() {
			return new EnemyCardBuilder();
		}
	
		public EnemyCardBuilder name(String name) {
			this.name = name;
			return this;
		}
		
		public EnemyCardBuilder year(int year) {
			this.year = year;
			return this;
		}
		
		public EnemyCardBuilder strength(int strength) {
			this.strength = strength;
			return this;
		}
		
		public EnemyCardBuilder victory(RewardChoice victory) {
			this.victory = victory;
			return this;
		}
		
		public EnemyCardBuilder defeat(RewardChoice defeat) {
			this.defeat = defeat;
			return this;
		}
		
		public EnemyCardBuilder andLoseABuilding() {
			this.defeatLoseBuilding = true;
			return this;
		}
		
		public EnemyCardBuilder isGoblin() {
			this.isGoblin = true;
			return this;
		}
		
		public EnemyCardBuilder isZombie() {
			this.isZombie = true;
			return this;
		}
		
		public EnemyCardBuilder isDemon() {
			this.isDemon = true;
			return this;
		}		
		
		public EnemyCard make() {
			EnemyCard card = new EnemyCard(name, year, strength, victory, defeat, defeatLoseBuilding);
			card.setGoblin(isGoblin);
			card.setZombie(isZombie);
			card.setDemon(isDemon);
			return card;
		}
	}
}
