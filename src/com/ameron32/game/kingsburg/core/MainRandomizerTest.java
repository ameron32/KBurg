package com.ameron32.game.kingsburg.core;

/**
 * Created by klemeilleur on 6/6/2016.
 */
public class MainRandomizerTest {

    public static void main(String[] args) {
        int repeat = BasicRandomizer.get().nextInt("repeat", 6);

        for (int i = 0; i < 100; i++) {
            if (!repeat()) {
                Printer.get().log("failed at iteration: " + i);
            }
        }
        Printer.get().log("done");
    }

    static boolean repeat() {
        int repeat = BasicRandomizer.get().nextInt("repeat", 6);
        int repeat2 = BasicRandomizer.get().nextInt("repeat", 6);
        return (repeat == repeat2);
    }
}
