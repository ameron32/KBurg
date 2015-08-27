package com.ameron32.game.kingsburg.core;

public class Printer {
	
	public static final boolean DEBUG = true;
	public static final boolean IS_FILTER_ON = false;
	public static final boolean[] playersEnabled = { true, false, false, false, false };
	
	static Logger printer;
	
	public static Logger get() {
		if (printer == null) {
			printer = new JavaLogger();
			printer.setFilter(IS_FILTER_ON, playersEnabled);
		}
		return printer;
	}
	
	public static void setLogger(Logger logger) {
		printer = logger;
	}

	private Printer() {}
		
}
