
public class MainRollTest {
	
	public static void main(String[] args) throws java.lang.Exception {
		Roll roll = Roll.rollTheDice(0, 5, 6);
		System.out.println(roll.toString());
		Long die = roll.getUnusedStandardDice().get(0);
		roll.useStandardDice(die);
		System.out.println(roll.toString());
	}
}
