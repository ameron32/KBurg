package com.ameron32.game.kingsburg.core;

import com.ameron32.game.kingsburg.core.local.JavaLogger;

public class Printer {
	
	public static boolean debugOn = true;
	public static boolean filterOn = false;
	public static boolean[] playersEnabled = { true, false, false, false, false };
	
	private static Logger printer;
	
	public static Logger get() {
		if (Printer.printer == null) {
			Printer.printer = new JavaLogger();
			Printer.printer.setFilter(Printer.filterOn, Printer.playersEnabled);
		}
		return Printer.printer;
	}
	
	public static void setLogger(Logger logger) {
		Printer.printer = logger;
	}
	
	public static void setDebug(boolean debug) {
		Printer.debugOn = debug;
	}
	
	public static void setFilterState(boolean on) {
		Printer.filterOn = on;
	}
	
	public static void setPlayersEnabled(boolean[] playersEnabled) {
		Printer.playersEnabled = playersEnabled;
	}
	
	

	private Printer() {}
}
