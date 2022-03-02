package com.codinghorror;

import java.util.Arrays;


class Board {

    public enum Square {
        BLACK("X"),
        WHITE("O"),
        EMPTY(".");

        private final String displayValue;

        Square(String displayValue) {
            this.displayValue = displayValue;
        }

        @Override
        public String toString() {
            return displayValue;
        }
    }


    private final Square[] squares;

    Board(Square[] squares) {
        this.squares = squares;
    }

    static Board of(Square s1, Square s2, Square s3, Square s4, Square s5, Square s6, Square s7, Square s8, Square s9) {
        return new Board(new Square[]{s1, s2, s3, s4, s5, s6, s7, s8, s9});
    }

    void print() {
        for (int rank = 1; rank <= 3; rank++) {
            System.out.print("          ");
            for (int file = 1; file <= 3; file++) {
                System.out.print(squares[(rank-1) * 3 + file - 1]);
            }
            System.out.println();
        }
    }

    /**
     * Returns the piece at the given position (1 through 9).
     */
    public Square get(int pos) {
        return squares[pos - 1];
    }

    /**
     * Sets the piece at the given position (1 through 9).
     */
    public void set(int pos, Square value) {
        squares[pos - 1] = value;
    }

    /**
     * Determines if player won by advancing piece to last rank, or by capturing all black pawns.
     */
    public boolean playerWins() {
        return get(1) == Square.WHITE || get(2) == Square.WHITE || get(3) == Square.WHITE ||
                Arrays.stream(squares).noneMatch(s -> s == Square.BLACK);
    }

    /**
     * Determines if computer won by advancing piece to last rank, or by capturing all white pawns,
     * or if the player has no moves.
     */
    public boolean computerWins() {
        if (get(7) == Square.BLACK || get(8) == Square.BLACK || get(9) == Square.BLACK ||
                Arrays.stream(squares).noneMatch(s -> s == Square.WHITE)) {
            return true;
        }

        // See if player has no moves.
        boolean playerHasMoves = false;
        for (int pos=1; pos<=9; pos++) {
            if (get(pos) == Square.WHITE) {
                if (get(pos-3) == Square.EMPTY) {
                    playerHasMoves = true;
                    break;
                }
                if (Hexapawn.FNR(pos) == pos) {
                    if (get(pos-2) == Square.BLACK || get(pos-4) == Square.BLACK) {
                        playerHasMoves = true;
                        break;
                    }
                } else if (pos<7) {
                       if (get(2) == Square.BLACK) {
                           playerHasMoves = true;
                           break;
                       }
                } else  if (get(5) == Square.BLACK) {
                    playerHasMoves = true;
                    break;
                }
            }
        }
        if (!playerHasMoves) {
            System.out.print("YOU CAN'T MOVE, SO ");
        }
        return !playerHasMoves;
    }

    boolean isLegalPlayerMove(int from, int to) {
        return get(from) != Square.WHITE ||
                get(to) == Square.WHITE ||
                (to - from != -3 && get(to) != Square.BLACK) ||
                to > from ||
                (to - from == -3 && get(to) != Square.EMPTY) ||
                to - from < -4 ||
                (from == 7 && to == 3);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Arrays.equals(squares, board.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(squares);
    }

    /**
     * Returns a new Board, transposed (flipped left to right).
     */
    public Board transposed() {
        // Original code used two loops and a formula to put transposed squares
        // into T; much easier to just hardcode it.
        Square[] transposedSquares = new Square[] {
                get(3), get(2), get(1),
                get(6), get(5), get(4),
                get(9), get(8), get(7)
        };
        return new Board(transposedSquares);
    }

    public Board copy() {
        return new Board(Arrays.copyOf(squares, squares.length));
    }
}
