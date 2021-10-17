package com.letflo.tictactoe;

public class TicTacToe {

    private Player.Type board[];
    private boolean endGame;
    private Player.Type winner;

    public TicTacToe() {
        initializeGame();
    }

    void initializeGame() {
        board = new Player.Type[9];
        endGame = false;
        winner = null;
    }

    public void place(Player player, int pos) throws ArrayIndexOutOfBoundsException, IllegalStateException {
        place(player.type, pos);
    }

    public void place(Player.Type type, int pos) throws ArrayIndexOutOfBoundsException, IllegalStateException {
        if (board[pos] != null) {
            throw new IllegalStateException("You can't place the piece here");
        }
        board[pos] = type;
    }

    public void checkWin(Player player) {
        // check horizontal win
        for (int i = 0; i <= 6; i += 3) {
            if (board[i] == player.type &&
                    board[i + 1] == player.type &&
                    board[i + 2] == player.type) {
                endGame = true;
                winner = player.type;
            }
        }

        // check vertical win
        for (int i = 0; i <= 2; i++) {
            if (board[i] == player.type &&
                    board[i + 3] == player.type &&
                    board[i + 6] == player.type) {
                endGame = true;
                winner = player.type;
            }
        }

        // check diagonal win
        if ((board[0] == player.type && board[4] == player.type && board[8] == player.type) || //up left to down right
                (board[2] == player.type && board[4] == player.type && board[6] == player.type)) { //up right to down left
            endGame = true;
            winner = player.type;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = 0; i < 9; i++) {
            if (board[i] == null) {
                sb.append(" ");
            } else {
                sb.append(board[i]);
            }
            count++;
            if (count == 3) {
                sb.append("\n");
                count = 0;
            } else {
                sb.append("|");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        TicTacToe ticTacToe = new TicTacToe();
        Player p1 = new Player(Player.Type.Y);
        Player p2 = new Player(Player.Type.X);
        ticTacToe.place(p2, 0);
        ticTacToe.place(p2, 1);
        ticTacToe.place(p1, 3);
        ticTacToe.place(p1, 4);
        ticTacToe.place(p1, 5);
        System.out.println(ticTacToe);
        ticTacToe.checkWin(p1);
        System.out.println(ticTacToe.endGame);
        System.out.println(ticTacToe.winner);
    }

}
