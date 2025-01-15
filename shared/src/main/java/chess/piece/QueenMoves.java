package chess.piece;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMoves implements ChessPieceCalculator {


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        RookMoves rook = new RookMoves();
        Collection<ChessMove> rookMoves = rook.pieceMoves(board, myPosition);

        BishopMoves bishop = new BishopMoves();
        Collection<ChessMove> bishopMoves = bishop.pieceMoves(board, myPosition);

        moves.addAll(rookMoves);
        moves.addAll(bishopMoves);

        return moves;
    }
}
