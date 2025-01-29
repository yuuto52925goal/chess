package chess.piece;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoves implements ChessPieceCalculator{

    int[][] kingMoves = {
            {0, 1},
            {1, 0},
            {0, -1},
            {-1, 0},
            {1, 1},
            {-1, -1},
            {-1, 1},
            {1, -1},
    };

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor team = board.getPiece(myPosition).getTeamColor();
        ChessMoveUtil.addValidMoves(board, myPosition, team, kingMoves, moves);
        return moves;
    }
}
