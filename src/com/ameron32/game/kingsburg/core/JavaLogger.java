package com.ameron32.game.kingsburg.core;

public class JavaLogger implements Logger {

	private boolean filter;
	private boolean[] playerEnabled;

	public JavaLogger() {}
	
	@Override
	public void setFilter(boolean filter, boolean[] playerEnabled){
		this.filter = filter;
		this.playerEnabled = playerEnabled;
	}

	String _getRound(int round) {
		round++;
		return "year " + round;
	}

	String _getPhase(int phase) {
		phase++;
		String phaseDetail;
		switch (phase) {
		case 2:
		case 4:
		case 6:
			phaseDetail = "productive season";
			break;
		case 1:
			phaseDetail = "aid from King";
			break;
		case 3:
			phaseDetail = "king's reward";
			break;
		case 5:
			phaseDetail = "king's envoy";
			break;
		case 7:
			phaseDetail = "recruit soldiers";
			break;
		case 8:
			phaseDetail = "do battle";
			break;
		default:
			phaseDetail = "phase missing: " + phase;
		}
		return phaseDetail;
	}

	String _getPlayer(int player) {
		player++;
		return "player " + player;
	}

	@Override
	public void log(String message) {
		if (Printer.debugOn) {
			System.out.println(message);
		}
	}
	
	@Override
	public void log(int player, String message) {
		if (Printer.debugOn) {
			if (!filter) {
				log("Player " + (player+1) + ": " + message);
			}
			if (filter) {
				if (player >= playerEnabled.length) {
					return;
				}
				boolean shouldDisplay = playerEnabled[player];
				if (!shouldDisplay) {
					return;
				}
				log("Player " + (player+1) + ": " + message);
			}
		}
	}
}
