package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private ChessPosition from;
    private ChessPosition to;
    private ChessPiece.PieceType pieceType;
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.from = startPosition;
        this.to = endPosition;
        this.pieceType = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return from;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return to;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return pieceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(from, chessMove.from) && Objects.equals(to, chessMove.to) && pieceType == chessMove.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, pieceType);
    }
}
