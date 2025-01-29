package chess.piece;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoves implements ChessPieceCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece piece = board.getPiece(myPosition);
        if (piece == null) {
            return moves;
        }
        ChessGame.TeamColor team = piece.getTeamColor();
        int forward         = (team == ChessGame.TeamColor.BLACK) ? -1 : 1;
        int startPosition   = (team == ChessGame.TeamColor.BLACK) ? 7  : 2;
        int promotePosition = (team == ChessGame.TeamColor.BLACK) ? 1  : 8;

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int[] captureOffsets = { -1, 1 };
        for (int offset : captureOffsets) {
            int targetRow = row + forward;
            int targetCol = col + offset;
            if (isInBounds(targetRow, targetCol)) {
                ChessPosition targetPos = new ChessPosition(targetRow, targetCol);
                ChessPiece targetPiece = board.getPiece(targetPos);
                if (targetPiece != null && targetPiece.getTeamColor() != team) {
                    addMoveOrPromotion(myPosition, targetPos, promotePosition, moves);
                }
            }
        }

        int stepRow = row + forward;
        if (isInBounds(stepRow, col)) {
            ChessPosition stepPos = new ChessPosition(stepRow, col);
            if (board.getPiece(stepPos) == null) {
                addMoveOrPromotion(myPosition, stepPos, promotePosition, moves);
                if (row == startPosition) {
                    int doubleRow = row + 2 * forward;
                    if (isInBounds(doubleRow, col)) {
                        ChessPosition doublePos = new ChessPosition(doubleRow, col);
                        if (board.getPiece(doublePos) == null) {
                            addMoveOrPromotion(myPosition, doublePos, promotePosition, moves);
                        }
                    }
                }
            }
        }

        return moves;
    }

    private boolean isInBounds(int row, int col) {
        return (row >= 1 && row <= 8 && col >= 1 && col <= 8);
    }

    private void addMoveOrPromotion(ChessPosition from, ChessPosition to,
                                    int promoteRow, Collection<ChessMove> moves) {
        if (to.getRow() == promoteRow) {
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(from, to, null));
        }
    }
}