Original source downloaded [from Vintage Basic](http://www.vintage-basic.net/games.html)

Conversion to [Oracle Java](https://openjdk.java.net/)

# Description from the original BASIC code (cleaned up)

Hexapawn:  Interpretation of Hexapawn game as presented in Martin Gardner's 
"The Unexpected Hanging and Other Mathematical Diversions", chapter eight:
'A Matchbox Game-Learning Machine'.  Original version for H-P Timeshare system
by R.A. Kaapke 5/5/76.  Instructions by Jeff Dalton.  Conversion to MITS BASIC
by Steve North.

# Instructions from the original BASIC code (cleaned up)

This program plays the game of Hexapawn.  Hexapawn is played with chess pawns
on a 3 by 3 board.  The pawns are moved as in chess - one space forward to
an empty space or one space forward and diagonally to capture an opposing man.
On the board, your pawns are 'O', the computer's pawns are 'X', and empty 
squares are '.'.  To enter a move, type the number of the square you are moving
from, followed by the number of the square you will move to.  The numbers must be
separated by a comma.

The computer starts a series of games knowing only when the game is won (a draw
is impossible) and how to move.  It has no strategy at first and just moves randomly.
However, it learns from each game.  Thus, winning becomes more and more difficult.
Also, to help offset your initial advantage, you will not be told how to win the
game but must learn this by playing.  The numbering of the board is as follows:

   123
   456
   789

For example, to move your rightmost pawn forward, you would type 9,6 in response to
the question 'YOUR MOVE ?'.  Since I'm a good sport, you'll always go first.

# Notes on translation to Java (Mark Schnitzius - schnitzi@gmail.com)

I'm old enough to actually remember Gardner's column, and punching in machine code to
play Hexapawn against our family's [KIM-1](https://en.wikipedia.org/wiki/KIM-1) in the
late seventies.  I even own the book referenced in the description
([The Unexpected Hanging](https://www.goodreads.com/book/show/415062.The_Unexpected_Hanging_and_Other_Mathematical_Diversions)).
So I had to do this translation!

The original BASIC code is convoluted, to say the least, but fortunately the key critical bits all worked
after a direct translation.  It was tempting to clean up the logic in a few places, but I mostly left it as
it was and improved the data structures instead.  Other points of note:

* Some critical functions in the original code relied on a MITS BASIC feature whereby an equality test returns 0 for false, or -1 for true.  A small function ('eq') that replicates that behaviour lets us keep the code true to the original.
* I also tried to keep the spirit of BASIC's 1-based arrays by converting in the getters and setters in the Board and MatchboxSet classes.