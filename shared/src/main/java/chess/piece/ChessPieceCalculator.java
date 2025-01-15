package chess.piece;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public interface ChessPieceCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
