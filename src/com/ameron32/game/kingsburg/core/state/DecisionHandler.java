package com.ameron32.game.kingsburg.core.state;

import java.util.HashMap;

/**
 * Created by klemeilleur on 6/6/16.
 */
public class DecisionHandler {

    static DecisionHandler dh;

    public static DecisionHandler get() {
        if (dh == null) {
            dh = new DecisionHandler();
        }
        return dh;
    }

    HashMap<String, Decision> decisions;

    private DecisionHandler() {
        decisions = new HashMap<>();
    }

    public void makeDecision(String decisionId, Decision decision) {
        decisions.put(decisionId, decision);
    }

    public Decision getDecisionAt(String decisionId) {
        return decisions.get(decisionId);
    }
}
