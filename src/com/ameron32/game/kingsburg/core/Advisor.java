package com.ameron32.game.kingsburg.core;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * RESOURCE POJO for a single Advisor in the WallOfAdvisors.
 *
 */
public class Advisor {

	// TODO Note: when choosing any item (green bag), will require selector

	private String name;
	private int ordinal;
	private final List<RewardChoice> options;
	
	public Advisor(String name, int ordinal, RewardChoice... options) {
		super();
		this.name = name;
		this.ordinal = ordinal;
		this.options = Arrays.asList(options);
	}

	@Override
	public String toString() {
		return "Advisor [name=" + name + ", ordinal=" + ordinal + ", options="
				+ Arrays.toString(options.toArray()) + "]";
	}
	
	String getHumanReadableOptions() {
		String optionsDescription = "";
		int i = 0;
		for (RewardChoice option: options) {
			i++;
			optionsDescription += "\n";
			optionsDescription += " Option " + i + ": ";
			optionsDescription += option.getHumanReadableReward();
		}
		return optionsDescription + "\n\n";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public List<RewardChoice> getOptions() {
		return options;
	}
	
}
