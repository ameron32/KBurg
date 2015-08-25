
public class Logger {

	private boolean filter;
	private boolean[] playerEnabled;

	Logger(boolean filter, boolean[] playerEnabled){
		this.filter = filter;
		this.playerEnabled = playerEnabled;
	}

	String getRound(int round) {
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

	String getPlayer(int player) {
		player++;
		return "player " + player;
	}

	void log(String message) {
		if (Printer.DEBUG) {
			System.out.println(message);
		}
	}
	
	void log(int player, String message) {
		if (Printer.DEBUG) {
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
