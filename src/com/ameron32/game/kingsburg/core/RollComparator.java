package com.ameron32.game.kingsburg.core;
import java.util.Comparator;

/**
 * 
 * KINGSBURG-BASED roll comparator using this logic:
 * -------------------------------------------------
 * 1. rolls sorted lowest to highest
 * 2. in a tie, maintain previous turn order
 *
 */
public class RollComparator implements Comparator<Roll> {
	
	int[] turnOrder;

	public RollComparator(int[] turnOrder) {
		super();
		this.turnOrder = turnOrder;
	}

	@Override
	public int compare(Roll r1, Roll r2) {
		return (r1.getUnusedTotal() * 1000 + turnOrder[r1.getPlayer()])
				- (r2.getUnusedTotal() * 1000 + turnOrder[r2.getPlayer()]);
	}
}
