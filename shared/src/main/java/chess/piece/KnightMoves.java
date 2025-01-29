package chess.piece;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMoves implements ChessPieceCalculator{
    int[][] kinightMoves = {
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
        ChessGame.TeamColor team = board.getPiece(myPosition).getTeamColor();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();
        for (int[] move : kinightMoves) {
            if (row + move[0] <= 8 && row + move[0] > 0 && column + move[1] <= 8 && column + move[1] > 0) {
                ChessPosition newPosition = new ChessPosition(row + move[0], column + move[1]);
                if (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != team){
                    ChessMove newChessMove = new ChessMove(myPosition, new ChessPosition(row + move[0], column + move[1]), null);
                    moves.add(newChessMove);
                }else if (board.getPiece(newPosition) == null){
                    ChessMove newChessMove = new ChessMove(myPosition, new ChessPosition(row + move[0], column + move[1]), null);
                    moves.add(newChessMove);
                }
            }
        }
        return moves;
    }
}
