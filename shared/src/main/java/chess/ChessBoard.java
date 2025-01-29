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
                initializePiece(i, j);
            }
        }
    }

    private void initializePiece(int row, int col) {
        if (row == 1) {
            chessPieces[row][col] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            return;
        }
        if (row == 6) {
            chessPieces[row][col] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            return;
        }

        if (row == 0 || row == 7) {
            ChessGame.TeamColor color = (row == 0) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            chessPieces[row][col] = createBackRankPiece(color, col);
        }
    }

    private ChessPiece createBackRankPiece(ChessGame.TeamColor color, int col) {
        return switch (col) {
            case 0, 7 -> new ChessPiece(color, ChessPiece.PieceType.ROOK);
            case 1, 6 -> new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
            case 2, 5 -> new ChessPiece(color, ChessPiece.PieceType.BISHOP);
            case 3 -> new ChessPiece(color, ChessPiece.PieceType.QUEEN);
            case 4 -> new ChessPiece(color, ChessPiece.PieceType.KING);
            default -> null;
        };
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
