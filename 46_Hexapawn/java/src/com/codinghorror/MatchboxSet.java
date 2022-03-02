package com.codinghorror;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * The set of four matchboxes for each board position, each containing a certain number of beads.
 * There are four matchboxes for each board position, because there are only a maximum of four
 * possible moves for black.  The counts of the beads in each box is used (along with several
 * cryptic formulas) to determine the next move for black.  If a particular move is ends up being
 * the last move for black before losing, that box is emptied and that move is not tried again
 * in that position.
 */
final class MatchboxSet {
    private final int[] count;

    private MatchboxSet(int[] count) {
        this.count = count;
    }

    static MatchboxSet of(int c1, int c2, int c3, int c4) {
        return new MatchboxSet(new int[]{c1, c2, c3, c4});
    }

    public int get(int index) {
        return count[index - 1];
    }

    public void clear(int index) {
        count[index - 1] = 0;
    }

    public IntStream stream() {
        return Arrays.stream(count);
    }

    @Override
    public String toString() {
        return "MatchboxSet{" +
                "count=" + Arrays.toString(count) +
                '}';
    }
}
