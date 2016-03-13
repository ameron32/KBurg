package com.ameron32.game.kingsburg.core.state;

import com.ameron32.game.kingsburg.core.state.Board;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by klemeilleur on 3/9/2016.
 */
public class BoardTest {

    Board testBoard;

    @Before
    public void setUp() throws Exception {
        testBoard = new TestBoard();
    }

    @After
    public void tearDown() throws Exception {
        testBoard = null;
    }

    @Test
    public void testInitialize() throws Exception {
        testBoard.initialize(5);
//        assertTrue(testBoard.soldiers.length == 5);
    }

    @Test
    public void testIncrementYear() throws Exception {

    }

    @Test
    public void testIncrementPhase() throws Exception {

    }

    @Test
    public void testAddKingsReinforcements() throws Exception {

    }

    @Test
    public void testIncreaseSoldiers() throws Exception {

    }

    @Test
    public void testGetSoldiersFor() throws Exception {

    }

    @Test
    public void testResetSoldiers() throws Exception {

    }

    @Test
    public void testReserveAdvisor() throws Exception {

    }

    @Test
    public void testIsAdvisorReserved() throws Exception {

    }

    @Test
    public void testResetAdvisors() throws Exception {

    }

    @Test
    public void testPullSynchronize() throws Exception {

    }

    @Test
    public void testPushUpdate() throws Exception {

    }

    class TestBoard extends Board {

        @Override
        public void pullSynchronize() {

        }

        @Override
        public void pushUpdate() {

        }
    }
}