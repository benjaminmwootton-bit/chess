package chess;

import chess.ChessBoard;

public class testing
{
    public static void main(String[] args)
    {
        ChessBoard board1 = new ChessBoard();
        System.out.println(board1);
        board1.resetBoard();
        System.out.println(board1);
    }
}
