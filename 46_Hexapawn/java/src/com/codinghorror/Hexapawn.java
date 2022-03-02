package com.codinghorror;

import java.util.*;

import static com.codinghorror.Board.Square.WHITE;
import static com.codinghorror.Board.Square.BLACK;
import static com.codinghorror.Board.Square.EMPTY;

/**
 * HEXAPAWN:  INTERPRETATION OF HEXAPAWN GAME AS PRESENTED IN
 * MARTIN GARDNER'S "THE UNEXPECTED HANGING AND OTHER MATHEMATIC-
 * AL DIVERSIONS", CHAPTER EIGHT:  A MATCHBOX GAME-LEARNING MACHINE
 * ORIGINAL VERSION FOR H-P TIMESHARE SYSTEM BY R.A. KAAPKE 5/5/76
 * INSTRUCTIONS BY JEFF DALTON
 * CONVERSION TO MITS BASIC BY STEVE NORTH
 */
public class Hexapawn {

    private static final Board START_POSITION = Board.of(
            BLACK, BLACK, BLACK,
            EMPTY, EMPTY, EMPTY,
            WHITE, WHITE, WHITE);

    private static final Map<Board, MatchboxSet> LOOKUP = new HashMap<>();
    private static final Map<Board, MatchboxSet> TRANSPOSED_LOOKUP = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        // Original program had parallel arrays of board positions versus
        // matchbox bead counts; we'll make a map instead for easy lookup.
        LOOKUP.put(Board.of(BLACK, BLACK, BLACK, WHITE, EMPTY, EMPTY, EMPTY, WHITE, WHITE),
                MatchboxSet.of(24, 25, 36, 0));
        LOOKUP.put(Board.of(BLACK, BLACK, BLACK, EMPTY, WHITE, EMPTY, WHITE, EMPTY, WHITE),
                MatchboxSet.of(14, 15, 36, 0));
        LOOKUP.put(Board.of(BLACK, EMPTY, BLACK, BLACK, WHITE, EMPTY, EMPTY, EMPTY, WHITE),
                MatchboxSet.of(15, 35, 36, 47));
        LOOKUP.put(Board.of(EMPTY, BLACK, BLACK, WHITE, BLACK, EMPTY, EMPTY, EMPTY, WHITE),
                MatchboxSet.of(36, 58, 59, 0));
        LOOKUP.put(Board.of(BLACK, EMPTY, BLACK, WHITE, WHITE, EMPTY, EMPTY, WHITE, EMPTY),
                MatchboxSet.of(15, 35, 36, 0));
        LOOKUP.put(Board.of(BLACK, BLACK, EMPTY, WHITE, EMPTY, WHITE, EMPTY, EMPTY, WHITE),
                MatchboxSet.of(24, 25, 26, 0));
        LOOKUP.put(Board.of(EMPTY, BLACK, BLACK, EMPTY, BLACK, WHITE, WHITE, EMPTY, EMPTY),
                MatchboxSet.of(26, 57, 58, 0));
        LOOKUP.put(Board.of(EMPTY, BLACK, BLACK, BLACK, WHITE, WHITE, WHITE, EMPTY, EMPTY),
                MatchboxSet.of(26, 35, 0, 0));
        LOOKUP.put(Board.of(BLACK, EMPTY, BLACK, BLACK, EMPTY, WHITE, EMPTY, WHITE, EMPTY),
                MatchboxSet.of(47, 48, 0, 0));
        LOOKUP.put(Board.of(EMPTY, BLACK, BLACK, EMPTY, WHITE, EMPTY, EMPTY, EMPTY, WHITE),
                MatchboxSet.of(35, 36, 0, 0));
        LOOKUP.put(Board.of(EMPTY, BLACK, BLACK, EMPTY, WHITE, EMPTY, WHITE, EMPTY, EMPTY),
                MatchboxSet.of(35, 36, 0, 0));
        LOOKUP.put(Board.of(BLACK, EMPTY, BLACK, WHITE, EMPTY, EMPTY, EMPTY, EMPTY, WHITE),
                MatchboxSet.of(36, 0, 0, 0));
        LOOKUP.put(Board.of(EMPTY, EMPTY, BLACK, BLACK, BLACK, WHITE, EMPTY, EMPTY, EMPTY),
                MatchboxSet.of(47, 58, 0, 0));
        LOOKUP.put(Board.of(BLACK, EMPTY, EMPTY, WHITE, WHITE, WHITE, EMPTY, EMPTY, EMPTY),
                MatchboxSet.of(15, 0, 0, 0));
        LOOKUP.put(Board.of(EMPTY, BLACK, EMPTY, BLACK, WHITE, WHITE, EMPTY, EMPTY, EMPTY),
                MatchboxSet.of(26, 47, 0, 0));
        LOOKUP.put(Board.of(BLACK, EMPTY, EMPTY, BLACK, BLACK, WHITE, EMPTY, EMPTY, EMPTY),
                MatchboxSet.of(47, 58, 0, 0));
        LOOKUP.put(Board.of(EMPTY, EMPTY, BLACK, BLACK, WHITE, EMPTY, EMPTY, EMPTY, EMPTY),
                MatchboxSet.of(35, 36, 47, 0));
        LOOKUP.put(Board.of(EMPTY, BLACK, EMPTY, WHITE, BLACK, EMPTY, EMPTY, EMPTY, EMPTY),
                MatchboxSet.of(28, 58, 0, 0));
        LOOKUP.put(Board.of(BLACK, EMPTY, EMPTY, BLACK, WHITE, EMPTY, EMPTY, EMPTY, EMPTY),
                MatchboxSet.of(15, 47, 0, 0));

        // And a second map to look up transposed (flipped left-to-right) versions of the board.
        for (Map.Entry<Board, MatchboxSet> entry : LOOKUP.entrySet()) {
            TRANSPOSED_LOOKUP.put(entry.getKey().transposed(), entry.getValue());
        }
    }

    private MatchboxSet lastMoveMatchboxSet;
    private int lastMoveIndex;
    private int wins, losses;

    /**
     * Returns 1 if x equals y, 0 otherwise.  This is to assist in
     * emulating the "MITS BASIC" feature where an equal test apparently
     * returns 0 or -1.
     */
    private static int eq(int x, int y) {
        return x == y ? -1 : 0;
    }

    /*
     * These three mysterious functions come straight from the original code.  Their actual
     * purpose is left as an exercise for the reader.  :)
     */
    static int FNR(int x) {
        return -3*eq(x,1)-eq(x,3)-4*eq(x,6)-6*eq(x,4)-7*eq(x,9)-9*eq(x,7)+FNS(x);
    }

    private static int FNS(int x) {
        return -x*(eq(x,2)+eq(x,5)+eq(x,8));
    }

    static private int FNM(int y) {
        return y - ((int) (y/10.0))*10;
    }

    /**
     * Method containing the main execution loop.
     */
    public void run() {

        maybeShowInstructions();

        while (true) {

            Board s = START_POSITION.copy();
            s.print();

            while (true) {

                if (!doPlayerMove(s)) {
                    System.out.println("I WIN.");
                    wins++;
                    break;
                }

                s.print();

                if (s.playerWins() ||  !computerHasMove(s) || !doComputerMove(s)) {
                    System.out.println("YOU WIN.");
                    losses++;
                    lastMoveMatchboxSet.clear(lastMoveIndex);
                    break;
                }

                s.print();

                if (s.computerWins()) {
                    System.out.println("I WIN.");
                    wins++;
                    break;
                }
            }

            System.out.println("I HAVE WON " + wins + " AND YOU " + losses + " OUT OF " + (wins+losses) + " GAMES.");
        }
    }

    private boolean doPlayerMove(Board s) {

        boolean legal = false;
        Scanner in = new Scanner(System.in);
        int from = 0;
        int to = 0;
        do {
            System.out.print("YOUR MOVE ? ");
            String m = in.next();
            try {
                StringTokenizer tokenizer = new StringTokenizer(m, ",");
                from = Integer.parseInt(tokenizer.nextToken());
                to = Integer.parseInt(tokenizer.nextToken());
                if (!(from > 0 && from < 10 && to > 0 && to < 10)) {
                    System.out.println("ILLEGAL CO-ORDINATES.");
                } else if (s.isLegalPlayerMove(from, to)) {
                    System.out.println("ILLEGAL MOVE.");
                } else {
                    legal = true;
                }
            } catch (Exception e) {
                System.out.println("ILLEGAL MOVE.");
            }
        } while (!legal);

        s.set(from, EMPTY);
        s.set(to, WHITE);
        return true;
    }

    private boolean computerHasMove(Board s) {
        for (int pos=1; pos<=9; pos++) {
            if (s.get(pos) == BLACK) {
                if (s.get(pos+3) == EMPTY) {
                    return true;
                }
                if (FNR(pos) == pos) {
                    if (s.get(pos+2) == WHITE || s.get(pos+4) == WHITE) {
                        return true;
                    }
                } else if (pos>3) {
                    if (s.get(8) == WHITE) {
                        return true;
                    }
                } else if (s.get(5) == WHITE) {
                    return true;
                } else if (s.get(pos+2) == WHITE || s.get(pos+4) == WHITE) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean doComputerMove(Board s) {

        boolean transposed;
        if (LOOKUP.containsKey(s)) {
            lastMoveMatchboxSet = LOOKUP.get(s);
            transposed = false;
        } else if (TRANSPOSED_LOOKUP.containsKey(s)) {
            lastMoveMatchboxSet = TRANSPOSED_LOOKUP.get(s);
            transposed = true;
        } else {
            System.out.println("ILLEGAL BOARD PATTERN");
            throw new RuntimeException("ILLEGAL BOARD PATTERN");
        }

        if (lastMoveMatchboxSet.stream().noneMatch(c -> c != 0)) {
            System.out.println("I RESIGN");
            return false;
        }

        int count;
        do {
            lastMoveIndex = RANDOM.nextInt(4)+1;
            count = lastMoveMatchboxSet.get(lastMoveIndex);
        } while (count == 0);

        int from, to;
        if (transposed) {
            from = FNR(count/10);
            to = FNR(FNM(count));
        } else {
            from = count/10;
            to = FNM(count);
        }

        System.out.println("I MOVE FROM " + from + " TO " + to);
        s.set(from, EMPTY);
        s.set(to, BLACK);
        return true;
    }

    private void maybeShowInstructions() {
        System.out.print("INSTRUCTIONS (Y-N) ? ");
        Scanner in = new Scanner(System.in);
        char a;
        do {
            a = Character.toUpperCase(in.next().charAt(0));
        } while (a != 'Y' && a != 'N');
        if (a == 'Y') printInstructions();
    }


    private void printInstructions() {
        System.out.println();
        System.out.println("THIS PROGRAM PLAYS THE GAME OF HEXAPAWN.");
        System.out.println("HEXAPAWN IS PLAYED WITH CHESS PAWNS ON A 3 BY 3 BOARD.");
        System.out.println("THE PAWNS ARE MOVED AS IN CHESS - ONE SPACE FORWARD TO");
        System.out.println("AN EMPTY SPACE OR ONE SPACE FORWARD AND DIAGONALLY TO");
        System.out.println("CAPTURE AN OPPOSING MAN.  ON THE BOARD, YOUR PAWNS");
        System.out.println("ARE 'O', THE COMPUTER'S PAWNS ARE 'X', AND EMPTY ");
        System.out.println("SQUARES ARE '.'.  TO ENTER A MOVE, TYPE THE NUMBER OF");
        System.out.println("THE SQUARE YOU ARE MOVING FROM, FOLLOWED BY THE NUMBER");
        System.out.println("OF THE SQUARE YOU WILL MOVE TO.  THE NUMBERS MUST BE");
        System.out.println("SEPERATED BY A COMMA.");
        System.out.println();
        System.out.println("THE COMPUTER STARTS A SERIES OF GAMES KNOWING ONLY WHEN");
        System.out.println("THE GAME IS WON (A DRAW IS IMPOSSIBLE) AND HOW TO MOVE.");
        System.out.println("IT HAS NO STRATEGY AT FIRST AND JUST MOVES RANDOMLY.");
        System.out.println("HOWEVER, IT LEARNS FROM EACH GAME.  THUS, WINNING BECOMES");
        System.out.println("MORE AND MORE DIFFICULT.  ALSO, TO HELP OFFSET YOUR");
        System.out.println("INITIAL ADVANTAGE, YOU WILL NOT BE TOLD HOW TO WIN THE");
        System.out.println("GAME BUT MUST LEARN THIS BY PLAYING.");
        System.out.println();
        System.out.println("THE NUMBERING OF THE BOARD IS AS FOLLOWS:");
        System.out.println("          123");
        System.out.println("          456");
        System.out.println("          789");
        System.out.println();
        System.out.println("FOR EXAMPLE, TO MOVE YOUR RIGHTMOST PAWN FORWARD,");
        System.out.println("YOU WOULD TYPE 9,6 IN RESPONSE TO THE QUESTION");
        System.out.println("'YOUR MOVE ?'.  SINCE I'M A GOOD SPORT, YOU'LL ALWAYS");
        System.out.println("GO FIRST.");
        System.out.println();
    }

    public static void main(String[] args) {
        Hexapawn h = new Hexapawn();
        h.run();
    }
}
