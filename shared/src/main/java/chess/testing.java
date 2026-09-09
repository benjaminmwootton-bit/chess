package chess;

public class testing
{
    public static void main(String[] args)
    {
        ChessBoard board1 = new ChessBoard();
        System.out.println(board1);
        board1.resetBoard();
        System.out.println(board1);
        board1 = new ChessBoard();
        board1.addPiece(5,5,new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        System.out.println(board1);
        System.out.print("Bishop Moves:");
        System.out.println(board1.getPiece(5,5).pieceMoves(board1,new ChessPosition(5,5)));
    }
}
