package com.ameron32.game.kingsburg.core.next;

import com.ameron32.game.kingsburg.core.state.Board;
import com.ameron32.game.kingsburg.core.state.Stage;

/**
 * Created by klemeilleur on 6/7/2016.
 */
public interface GameLoop {

    void setup(int players, int phases, int rounds, Board board);

    void performStage(int round, int phase, Stage stage);

    void complete();
}
