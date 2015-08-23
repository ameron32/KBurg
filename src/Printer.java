
public class Printer {
	
	public static final boolean DEBUG = true;
	
	static Logger printer;
	
	public static Logger get() {
		if (printer == null) {
			printer = new Logger();
		}
		return printer;
	}

	private Printer() {}
}
