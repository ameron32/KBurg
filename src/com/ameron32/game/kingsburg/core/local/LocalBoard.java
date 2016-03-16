package com.ameron32.game.kingsburg.core.local;

import com.ameron32.game.kingsburg.core.state.Board;

public class LocalBoard extends Board {

	private final BoardListener listener;

	public LocalBoard() {
		super();
		listener = null;
	}

	public LocalBoard(BoardListener listener) {
		this.listener = listener;
	}

	@Override
	public void pullSynchronize() {
		
	}

	@Override
	public void pushUpdate() {
		if (listener != null) {
			listener.currentState(getCurrentYear(), getCurrentPhase(), getCurrentStage(), getCurrentTurn());
		}
	}

	public interface BoardListener {
		void currentState(int year, int phase, int stage, int turn);
	}
}
