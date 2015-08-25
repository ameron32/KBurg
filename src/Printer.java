
public class Printer {
	
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	
	public static final boolean DEBUG = true;
	public static final boolean IS_FILTER_ON = false;
	public static final boolean[] playersEnabled = { true, false, false, false, false };
	
	static Logger printer;
	
	public static Logger get() {
		if (printer == null) {
			printer = new Logger(IS_FILTER_ON, playersEnabled);
		}
		return printer;
	}

	private Printer() {}
}
