package chess.piece;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoves implements ChessPieceCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessGame.TeamColor team = board.getPiece(myPosition).getTeamColor();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();
        if (team == ChessGame.TeamColor.BLACK){
            ChessPosition newPosition1 = new ChessPosition(row - 1, column -1);
            ChessPosition newPosition2 = new ChessPosition(row - 1, column +1);
            if (row - 1 > 0 && column - 1 > 0 && board.getPiece(newPosition1) != null){
                if (board.getPiece(newPosition1).getTeamColor() != team){
                    if (row - 1 == 1){
                        ChessMove newChessMove1 = new ChessMove(myPosition, newPosition1, ChessPiece.PieceType.ROOK);
                        ChessMove newChessMove2 = new ChessMove(myPosition, newPosition1, ChessPiece.PieceType.KNIGHT);
                        ChessMove newChessMove3 = new ChessMove(myPosition, newPosition1, ChessPiece.PieceType.BISHOP);
                        ChessMove newChessMove4 = new ChessMove(myPosition, newPosition1, ChessPiece.PieceType.QUEEN);
                        moves.add(newChessMove1);
                        moves.add(newChessMove2);
                        moves.add(newChessMove3);
                        moves.add(newChessMove4);
                    }else {
                        ChessMove newChessMove = new ChessMove(myPosition, newPosition1, null);
                        moves.add(newChessMove);
                    }
                }
            }
            if (row - 1 > 0 && column + 1 <= 8 && board.getPiece(newPosition2) != null){
                if (board.getPiece(newPosition2).getTeamColor() != team){
                    if (row - 1 == 1){
                        ChessMove newChessMove1 = new ChessMove(myPosition, newPosition2, ChessPiece.PieceType.ROOK);
                        ChessMove newChessMove2 = new ChessMove(myPosition, newPosition2, ChessPiece.PieceType.KNIGHT);
                        ChessMove newChessMove3 = new ChessMove(myPosition, newPosition2, ChessPiece.PieceType.BISHOP);
                        ChessMove newChessMove4 = new ChessMove(myPosition, newPosition2, ChessPiece.PieceType.QUEEN);
                        moves.add(newChessMove1);
                        moves.add(newChessMove2);
                        moves.add(newChessMove3);
                        moves.add(newChessMove4);
                    }else {
                        ChessMove newChessMove = new ChessMove(myPosition, newPosition2, null);
                        moves.add(newChessMove);
                    }
                }
            }
        }else if (team == ChessGame.TeamColor.WHITE){
            ChessPosition newPosition1 = new ChessPosition(row + 1, column + 1);
            ChessPosition newPosition2 = new ChessPosition(row + 1, column - 1);
            if (row + 1 <= 8 && column + 1 <= 8 && board.getPiece(newPosition1) != null){
                if (board.getPiece(newPosition1).getTeamColor() != team){
                    if (row + 1 == 8){
                        ChessMove newChessMove1 = new ChessMove(myPosition, newPosition1, ChessPiece.PieceType.ROOK);
                        ChessMove newChessMove2 = new ChessMove(myPosition, newPosition1, ChessPiece.PieceType.KNIGHT);
                        ChessMove newChessMove3 = new ChessMove(myPosition, newPosition1, ChessPiece.PieceType.BISHOP);
                        ChessMove newChessMove4 = new ChessMove(myPosition, newPosition1, ChessPiece.PieceType.QUEEN);
                        moves.add(newChessMove1);
                        moves.add(newChessMove2);
                        moves.add(newChessMove3);
                        moves.add(newChessMove4);
                    }else {
                        ChessMove newChessMove = new ChessMove(myPosition, newPosition1, null);
                        moves.add(newChessMove);
                    }
                }
            }
            if (row + 1 > 0 && column - 1 > 0 && board.getPiece(newPosition2) != null){
                if (board.getPiece(newPosition2).getTeamColor() != team){
                    if (row + 1 == 8){
                        ChessMove newChessMove1 = new ChessMove(myPosition, newPosition2, ChessPiece.PieceType.ROOK);
                        ChessMove newChessMove2 = new ChessMove(myPosition, newPosition2, ChessPiece.PieceType.KNIGHT);
                        ChessMove newChessMove3 = new ChessMove(myPosition, newPosition2, ChessPiece.PieceType.BISHOP);
                        ChessMove newChessMove4 = new ChessMove(myPosition, newPosition2, ChessPiece.PieceType.QUEEN);
                        moves.add(newChessMove1);
                        moves.add(newChessMove2);
                        moves.add(newChessMove3);
                        moves.add(newChessMove4);
                    }else{
                        ChessMove newChessMove = new ChessMove(myPosition, newPosition2, null);
                        moves.add(newChessMove);
                    }
                }
            }
        }
        row = myPosition.getRow();
        column = myPosition.getColumn();
        if ((row == 2 && team == ChessGame.TeamColor.WHITE)|| (row == 7 && team == ChessGame.TeamColor.BLACK)){
            ChessPosition newPosition;
            if (row == 2){
                newPosition = new ChessPosition(row + 1, column);
            }else{
                newPosition = new ChessPosition(row - 1, column);
            }
            if ( board.getPiece(newPosition) == null){
                ChessMove newChessMove = new ChessMove(myPosition, newPosition, null);
                moves.add(newChessMove);
                if (row == 2){
                    newPosition = new ChessPosition(row + 2, column);
                }else{
                    newPosition = new ChessPosition(row - 2, column);
                }
                if (board.getPiece(newPosition) == null){
                    ChessMove newChessMove2 = new ChessMove(myPosition, newPosition, null);
                    moves.add(newChessMove2);
                }
            }
        }else if (team == ChessGame.TeamColor.WHITE){
            ChessPosition newPosition = new ChessPosition(row + 1, column);
            if (row + 1 == 8 && board.getPiece(newPosition) == null){
                ChessMove newChessMove1 = new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK);
                ChessMove newChessMove2 = new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN);
                ChessMove newChessMove3 = new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT);
                ChessMove newChessMove4 = new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP);
                moves.add(newChessMove1);
                moves.add(newChessMove2);
                moves.add(newChessMove3);
                moves.add(newChessMove4);
            }else if (row + 1 < 8 && board.getPiece(newPosition) == null){
                ChessMove newChessMove = new ChessMove(myPosition, newPosition, null);
                moves.add(newChessMove);
            }
        }else{
            ChessPosition newPosition = new ChessPosition(row - 1, column);
            if (row - 1 == 1 && board.getPiece(newPosition) == null){
                ChessMove newChessMove1 = new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK);
                ChessMove newChessMove2 = new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN);
                ChessMove newChessMove3 = new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT);
                ChessMove newChessMove4 = new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP);
                moves.add(newChessMove1);
                moves.add(newChessMove2);
                moves.add(newChessMove3);
                moves.add(newChessMove4);
            }else if (row - 1 > 1 && board.getPiece(newPosition) == null){
                ChessMove newChessMove = new ChessMove(myPosition, newPosition, null);
                moves.add(newChessMove);
            }
        }


        return moves;
    }
}
