package chess.piece;

import chess.*;

import java.util.Collection;

public class ChessMoveUtil {
    /**
     * Adds valid moves for a piece based on its possible moves.
     */
    public static void addValidMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor team,
                                     int[][] possibleMoves, Collection<ChessMove> moves) {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        for (int[] move : possibleMoves) {
            int newRow = row + move[0];
            int newCol = column + move[1];

            if (isWithinBounds(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != team) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
    }

    private static boolean isWithinBounds(int row, int column) {
        return row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }
}
