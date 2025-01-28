package chess;

import chess.piece.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor teamColor;
    private PieceType pieceType;
    private Collection<ChessMove> moves;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceType = type;
        this.teamColor = pieceColor;
        this.moves = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType && Objects.equals(moves, that.moves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType, moves);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (pieceType == PieceType.KNIGHT){
            KnightMoves knightMoves = new KnightMoves();
            Collection<ChessMove> newMoves = knightMoves.pieceMoves(board, myPosition);
            moves = new ArrayList<>(newMoves);
        }else if (pieceType == PieceType.ROOK){
            RookMoves rookMoves = new RookMoves();
            Collection<ChessMove> newMoves = rookMoves.pieceMoves(board, myPosition);
            moves = new ArrayList<>(newMoves);
        }else if (pieceType == PieceType.KING){
            KingMoves kingMoves = new KingMoves();
            Collection<ChessMove> newMoves = kingMoves.pieceMoves(board, myPosition);
            moves = new ArrayList<>(newMoves);
        }else if (pieceType == PieceType.BISHOP){
            BishopMoves bishopMoves = new BishopMoves();
            Collection<ChessMove> newMoves = bishopMoves.pieceMoves(board, myPosition);
            moves = new ArrayList<>(newMoves);
        }else if (pieceType == PieceType.QUEEN){
            QueenMoves queenMoves = new QueenMoves();
            Collection<ChessMove> newMoves = queenMoves.pieceMoves(board, myPosition);
            moves.addAll(newMoves);
        }else if (pieceType == PieceType.PAWN){
            PawnMoves pawnMoves = new PawnMoves();
            Collection<ChessMove> newMoves = pawnMoves.pieceMoves(board, myPosition);
            moves = new ArrayList<>(newMoves);
        }
        return moves;
    }

    @Override
    public String toString() {
        return this.pieceType.toString() + " " + this.teamColor.toString() + " " + this.moves;
    }
}
