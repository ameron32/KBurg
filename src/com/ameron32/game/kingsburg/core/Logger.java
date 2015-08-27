package com.ameron32.game.kingsburg.core;

public interface Logger {

	void setFilter(boolean filter, boolean[] playerEnabled);
	void log(String message);
	void log(int player, String message);
}
