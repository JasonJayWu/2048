
package game2048;

import ucb.util.CommandArgs;

import game2048.gui.Game;
import static game2048.Main.Side.*;

/** The main class for the 2048 game.
 *  @author Jason Wu
 */
public class Main {

    /** Size of the board: number of rows and of columns. */
    static final int SIZE = 4;
    /** Number of squares on the board. */
    static final int SQUARES = SIZE * SIZE;
    static final int WIN = 2048;
    static final int LOSE = 16;

    /** Symbolic names for the four sides of a board. */
    static enum Side { NORTH, EAST, SOUTH, WEST };

    /** The main program.  ARGS may contain the options --seed=NUM,
     *  (random seed); --log (record moves and random tiles
     *  selected.); --testing (take random tiles and moves from
     *  standard input); and --no-display. */
    public static void main(String... args) {
        CommandArgs options =
            new CommandArgs("--seed=(\\d+) --log --testing --no-display",
                            args);
        if (!options.ok()) {
            System.err.println("Usage: java game2048.Main [ --seed=NUM ] "
                               + "[ --log ] [ --testing ] [ --no-display ]");
            System.exit(1);
        }

        Main game = new Main(options);
        game.clear();
        while (game.play()) {
            /* No action */
        }
        System.exit(0);
    }

    /** A new Main object using OPTIONS as options (as for main). */
    Main(CommandArgs options) {
        boolean log = options.contains("--log"),
            display = !options.contains("--no-display");
        long seed = !options.contains("--seed") ? 0 : options.getLong("--seed");
        _testing = options.contains("--testing");
        _game = new Game("2048", SIZE, seed, log, display, _testing);
    }

    /** Reset the score for the current game to 0 and clear the board. */
    void clear() {
        _score = 0;
        _count = 0;
        _game.clear();
        _game.setScore(_score, _maxScore);
        for (int r = 0; r < SIZE; r += 1) {
            for (int c = 0; c < SIZE; c += 1) {
                _board[r][c] = 0;
            }
        }
    }

    /** Play one game of 2048, updating the maximum score. Return true
     *  iff play should continue with another game, or false to exit. */
    boolean play() {
        // FIXME?
        while (true) {
            // FIXME?

            setRandomPiece();
            if (gameOver()) {
                // FIXME? My code
                if (_score > _maxScore) {
                    _maxScore = _score;
                }
                _game.endGame();
            }

        GetMove:
            while (true) {
                String key = _game.readKey();

                switch (key) {
                case "Up": case "Down": case "Left": case "Right":
                    if (!gameOver() && tiltBoard(keyToSide(key))) {
                        break GetMove;
                    }
                    break;
                // FIXME?
                case "Quit":
                    return false;
                case "New Game":
                    clear();
                    return true;
                default:
                    break;
                }
            }
            // FIXME?
            _game.setScore(_score, _maxScore);
        }
    }

    /** Return true iff the current game is over (no more moves
     *  possible). */
    boolean gameOver() {
        // FIXME? My code
        if ((_count == LOSE && !checkAdj()) || check2048()) {
            return true;
        } else {
            return false;
        }
    }

    /** Add a tile to a random, empty position, choosing a value (2 or
     *  4) at random.  Has no effect if the board is currently full. */
    void setRandomPiece() {
        if (_count == SQUARES) {
            return;
        }

        // FIXME?
        /** Spawns 2 random tiles at the beginning of the game */
        if (_count == 0) {
            for (int i = 0; i < 2; i++) {
                int[] spawn = _game.getRandomTile();
                while (_board[spawn[1]][spawn[2]] != 0) {
                    spawn = _game.getRandomTile();
                }
                _game.addTile(spawn[0], spawn[1], spawn[2]);
                _board[spawn[1]][spawn[2]] = spawn[0];
            }
            _count += 2;
        } else {
            int[] spawn = _game.getRandomTile();
            while (_board[spawn[1]][spawn[2]] != 0) {
                spawn = _game.getRandomTile();
            }
            _game.addTile(spawn[0], spawn[1], spawn[2]);
            _board[spawn[1]][spawn[2]] = spawn[0];
            _count += 1;
        }
    }

    /** Perform the result of tilting the board toward SIDE.
     *  Returns true iff the tilt changes the board. **/
    boolean tiltBoard(Side side) {
        /* As a suggestion (see the project text), you might try copying
         * the board to a local array, turning it so that edge SIDE faces
         * north.  That way, you can re-use the same logic for all
         * directions.  (As usual, you don't have to). */
        int[][] board = new int[SIZE][SIZE];
        boolean tileMove = false;
        /** Copy backend into frontend */
        for (int i = 0; i < _board.length; i++) {
            for (int j = 0; j < _board.length; j++) {
                board[i][j] = _board[tiltRow(side, i, j)][tiltCol(side, i, j)];
            }
        }

        /** Main body */
        for (int r = 0; r < SIZE; r += 1) {
            for (int c = 0; c < SIZE; c += 1) {
                board[r][c] =
                    _board[tiltRow(side, r, c)][tiltCol(side, r, c)];
                // FIXME?
                /** Check which spaces are free above tile */
                if (r == 0) {
                    break;
                } else if (board[r][c] == 0) {
                    continue;
                } else {
                    int origRow = r;
                    int newRow = r;
                    while (newRow > -1) {
                        if (newRow == 0) {
                            _game.moveTile(board[origRow][c],
                            tiltRow(side, origRow, c), tiltCol(side, origRow, c),
                            tiltRow(side, 0, c), tiltCol(side, 0, c));
                            board[0][c] = board[origRow][c];
                            board[origRow][c] = 0;
                            tileMove = true;
                            break;
                        } else {                          
                            if (board[newRow - 1][c] == 0) {
                                newRow -= 1;
                            } else if (board[newRow - 1][c] == board[origRow][c]) {
                                _game.mergeTile(board[origRow][c], board[origRow][c] * 2, 
                                tiltRow(side, origRow, c), tiltCol(side, origRow, c), tiltRow(side, newRow-1, c), tiltCol(side,newRow-1,c));
                                _count -= 1;
                                _score = _score + board[origRow][c] * 2;
                                board[newRow - 1][c] = board[origRow][c] * 2 + 1;
                                board[origRow][c] = 0;
                                tileMove = true;
                                break;
                            } else if (board[newRow - 1][c] != board[origRow][c] && newRow != origRow) {
                                _game.moveTile(board[origRow][c], tiltRow(side,origRow,c),
                                tiltCol(side ,origRow, c), tiltRow(side, newRow, c), 
                                tiltCol(side, newRow, c));
                                board[newRow][c] = board[origRow][c];
                                board[origRow][c] = 0;
                                tileMove = true;
                                break;
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }
        for (int r = 0; r < SIZE; r += 1) {
            for (int c = 0; c < SIZE; c += 1) {
                if ((board[r][c] % 2) != 0) {
                    board[r][c] -= 1;
                }
            }
        }

        /** Copy frontend into backend */
        for (int r = 0; r < SIZE; r += 1) {
            for (int c = 0; c < SIZE; c += 1) {
                _board[tiltRow(side, r, c)][tiltCol(side, r, c)]
                    = board[r][c];
            }
        }
        _game.displayMoves();
        return tileMove;
    }

    /** Return the row number on a playing board that corresponds to row R
     *  and column C of a board turned so that row 0 is in direction SIDE (as
     *  specified by the definitions of NORTH, EAST, etc.).  So, if SIDE
     *  is NORTH, then tiltRow simply returns R (since in that case, the
     *  board is not turned).  If SIDE is WEST, then column 0 of the tilted
     *  board corresponds to row SIZE - 1 of the untilted board, and
     *  tiltRow returns SIZE - 1 - C. */
    int tiltRow(Side side, int r, int c) {
        switch (side) {
        case NORTH:
            return r;
        case EAST:
            return c;
        case SOUTH:
            return SIZE - 1 - r;
        case WEST:
            return SIZE - 1 - c;
        default:
            throw new IllegalArgumentException("Unknown direction");
        }
    }

    /** Return the column number on a playing board that corresponds to row
     *  R and column C of a board turned so that row 0 is in direction SIDE
     *  (as specified by the definitions of NORTH, EAST, etc.). So, if SIDE
     *  is NORTH, then tiltCol simply returns C (since in that case, the
     *  board is not turned).  If SIDE is WEST, then row 0 of the tilted
     *  board corresponds to column 0 of the untilted board, and tiltCol
     *  returns R. */
    int tiltCol(Side side, int r, int c) {
        switch (side) {
        case NORTH:
            return c;
        case EAST:
            return SIZE - 1 - r;
        case SOUTH:
            return SIZE - 1 - c;
        case WEST:
            return r;
        default:
            throw new IllegalArgumentException("Unknown direction");
        }
    }
    /** Helper method to check if any adjacent tiles are the same.
     *  */
    boolean checkAdj() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                /** Make sure row check not out of bounds. */
                if (r - 1 < 0) {
                } else {
                    if (_board[r - 1][c] == _board[r][c]) {
                        return true;
                    }
                }
                /** Make sure row check not out of bounds. */
                if (r + 1 == SIZE) {
                } else {
                    if (_board[r + 1][c] == _board[r][c]) {
                        return true;
                    }
                }
                /** Make sure column check not out of bounds. */
                if (c - 1 < 0) {
                } else {
                    if (_board[r][c - 1] == _board[r][c]) {
                        return true;
                    }
                }
                /** Make sure column check not out of bounds. */
                if (c + 1 == SIZE) {
                } else {
                    if (_board[r][c + 1] == _board[r][c]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Helper method to check if there is a 2048 tile.
     *  */
    boolean check2048() {
        int tile = WIN;
        for (int i = 0; i < _board.length; i++) {
            for (int j = 0; j < _board.length; j++) {
                if (_board[i][j] == tile) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Return the side indicated by KEY ("Up", "Down", "Left",
     *  or "Right"). */
    Side keyToSide(String key) {
        switch (key) {
        case "Up":
            return NORTH;
        case "Down":
            return SOUTH;
        case "Left":
            return WEST;
        case "Right":
            return EAST;
        default:
            throw new IllegalArgumentException("unknown key designation");
        }
    }

    /** Represents the board: _board[r][c] is the tile value at row R,
     *  column C, or 0 if there is no tile there. */
    private final int[][] _board = new int[SIZE][SIZE];

    /** True iff --testing option selected. */
    private boolean _testing;
    /** THe current input source and output sink. */
    private Game _game;
    /** The score of the current game, and the maximum final score
     *  over all games in this session. */
    private int _score, _maxScore;
    /** Number of tiles on the board. */
    private int _count;
}
