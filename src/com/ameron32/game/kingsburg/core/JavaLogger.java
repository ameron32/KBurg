package com.ameron32.game.kingsburg.core;

public class JavaLogger implements Logger {

	private boolean filter = false;
	private boolean[] playerEnabled = { true, true, true, true, true };

	public JavaLogger() {}
	
	@Override
	public void setFilter(boolean filter, boolean[] playerEnabled){
		this.filter = filter;
		this.playerEnabled = playerEnabled;
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
