package com.ameron32.game.kingsburg.core;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by klemeilleur on 6/6/2016.
 */
public class NonRandomProducer implements Randomizer {

    static HashMap<String, Integer> cachedRandoms;

    static NonRandomProducer randomizer;

    public static NonRandomProducer get() {
        if (randomizer == null) {
            randomizer = new NonRandomProducer();
        }
        return randomizer;
    }

    private NonRandomProducer() {
        super();
        cachedRandoms = new HashMap<>();
    }

    int counter = 0;

    @Override
    public int nextInt(String requestId, int max) {

        Integer value = cachedRandoms.get(requestId);
        if (value == null) {
            int next = counter % max;
            cachedRandoms.put(requestId, next);
            counter++;
            return next;
        } else {
            return value;
        }
    }
}
