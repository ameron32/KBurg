
public class Logger {

	Logger(){}

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
}
