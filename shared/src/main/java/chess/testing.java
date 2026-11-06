package chess;

public class testing {
    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        System.out.print(board);
        System.out.print(board.toStringBlack());
    }
}