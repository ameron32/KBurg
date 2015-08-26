import java.util.ArrayList;
import java.util.List;


public class PhaseHandler {
	
	private static final String PRODUCTIVE_SUMMER_NAME = "Summer Season";

	static List<Phase> phases = new ArrayList<>(8);
	static {
		phases.add(Phase.Builder.of()
				.name("Aid From the King")
				.ordinal(1).gainsAid().make());
		phases.add(Phase.Builder.of()
				.name("Spring Season")
				.ordinal(2).productive().usesAid().make());
		phases.add(Phase.Builder.of()
				.name("The King's Reward")
				.ordinal(3).reward().make());
		phases.add(Phase.Builder.of()
				.name(PRODUCTIVE_SUMMER_NAME)
				.ordinal(4).productive().make());
		phases.add(Phase.Builder.of()
				.name("The King's Envoy")
				.ordinal(5).envoy().make());
		phases.add(Phase.Builder.of()
				.name("Autumn Season")
				.ordinal(6).productive().make());
		phases.add(Phase.Builder.of()
				.name("Recruit Soldiers")
				.ordinal(7).recruit().make());
		phases.add(Phase.Builder.of()
				.name("Winter Season")
				.ordinal(8).battle().make());
	}
	static Phase getPhase(int ordinal) {
		for (Phase phase : phases) {
			if (phase.getOrdinal() == ordinal) {
				return phase;
			}
		}
		return null;
	}
	static int getPhaseCount() {
		return phases.size();
	}
	static boolean isSummer(Phase phase) {
		return (phase.getName().equalsIgnoreCase(PRODUCTIVE_SUMMER_NAME));
	}
	
}
