package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] chessPieces;
    public ChessBoard() {
        this.chessPieces = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chessPieces[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return chessPieces[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.chessPieces = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chessPieces[i][j] == null) {
                    if (i == 1){
                        chessPieces[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                    }else if (i == 6){
                        chessPieces[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                    }else if (i == 0){
                        if (j == 0 || j == 7){
                            chessPieces[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
                        }else if (j == 1 || j == 6){
                            chessPieces[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                        }else if (j == 2 || j == 5){
                            chessPieces[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                        }else if (j == 3){
                            chessPieces[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
                        }else{
                            chessPieces[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
                        }
                    }else if (i == 7){
                        if (j == 0 || j == 7){
                            chessPieces[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
                        }else if (j == 1 || j == 6){
                            chessPieces[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
                        }else if (j == 2 || j == 5){
                            chessPieces[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
                        }else if (j == 3){
                            chessPieces[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
                        }else{
                            chessPieces[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition position = new ChessPosition(i + 1, j + 1);
                if (chessPieces[i][j] != null || that.chessPieces[i][j] != null){
                    if (chessPieces[i][j].getPieceType() != that.getPiece(position).getPieceType()){
                        return false;
                    }
                    if (chessPieces[i][j].getTeamColor() != that.getPiece(position).getTeamColor()){
                        System.out.println(chessPieces[i][j].getTeamColor());
                        System.out.println(that.getPiece(position).getTeamColor());
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(chessPieces);
    }
}
