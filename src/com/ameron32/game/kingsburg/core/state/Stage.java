package com.ameron32.game.kingsburg.core.state;

/**
 * Created by klemeilleur on 3/15/16.
 */
public enum Stage {
    //
    START_PHASE,

    // when statue or chapel are available, hold everyone until players have chosen to use them
    ROLL_AND_REROLL,

    // looping STAGE until all dice are used (or forfeit)
    CHOOSE_ADVISORS,

    // choose green bag resources then build buildings in the same stage
    SELECT_RESOURCES_AND_BUILD,

    // choose to use the town hall (always) & recruit (phase 7) in the same stage
    TOWNHALL_OPTION_AND_RECRUIT_SOLDIERS,

    // choose resources to lose when battle is lost and enemy steals green bag resources
    CHOOSE_DEFEAT_LOSSES,

    //
    END_PHASE;

	public static int getStageCount() {
		return Stage.values().length;
	}
}
