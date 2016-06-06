package com.ameron32.game.kingsburg.core;
import com.ameron32.game.kingsburg.core.advisor.Advisor;
import com.ameron32.game.kingsburg.core.advisor.WallOfAdvisors;

import java.util.List;


public class MainAdvisorTests {

	static WallOfAdvisors wall;
	static Randomizer rand;
	
	public static void main(String[] args) throws java.lang.Exception {
		wall = WallOfAdvisors.get();
		rand = Randomizer.get();
		
		_advisorsInOrder();
	}
	
	private static void _listOfAdvisors() {
		List<Advisor> advisors = wall.getAdvisors();
		for (Advisor a : advisors) {
			System.out.println(a);
		}
	}
	
	private static void _advisorsInOrder() {
		for (int i = 0; i < 18; i++) {
			_displayAdvisor(i);
		}
	}
	
	private static void _randomAdvisors() {
		for (int i = 0; i < 10; i++) {
			int dice = rand.nextInt("advisorTest", 18);
			_displayAdvisor(dice);
		}
	}
	
	private static void _displayAdvisor(int position) {

		Advisor a = wall.getAdvisors().get(position);
		int optionCount = a.getOptions().size();
		String message = "If I use a " + (position + 1) + "\n" +
				" for influence, I can influence a " + a.getName() + " who offers " + optionCount + " option(s) to choose from. ";
		message += (optionCount > 1) ? "\nThose options are: " + a.getHumanReadableOptions(): "\nThat option is: " + a.getHumanReadableOptions();
		System.out.println(message);
	}
}
