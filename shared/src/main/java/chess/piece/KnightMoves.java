package chess.piece;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoves implements ChessPieceCalculator{
    int[][] knightMoves = {
            {2, 1},
            {1, 2},
            {-1, 2},
            {-2, 1},
            {-2, -1},
            {-1, -2},
            {1, -2},
            {2, -1},
    };
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor team = board.getPiece(myPosition).getTeamColor();
        ChessMoveUtil.addValidMoves(board, myPosition, team, knightMoves, moves);
        return moves;
    }
}
