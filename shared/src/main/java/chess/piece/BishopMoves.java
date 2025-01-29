package chess.piece;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoves implements ChessPieceCalculator{
    String[] bishopsMoves = {"RP", "RN", "LP", "LN"};

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor team = board.getPiece(myPosition).getTeamColor();
        int row;
        int column;
        for (String moveDirection : bishopsMoves) {
            row = myPosition.getRow();
            column = myPosition.getColumn();
            while (true){
                if (moveDirection.equals("RP")){
                    row++;
                    column++;
                    if (row > 8 || column > 8){
                        break;
                    }
                }else if (moveDirection.equals("RN")){
                    row --;
                    column ++;
                    if (row <= 0 || column > 8){
                        break;
                    }
                }else if (moveDirection.equals("LP")){
                    column--;
                    row ++;
                    if (column <= 0 || row > 8){
                        break;
                    }
                }else{
                    column --;
                    row --;
                    if (column <= 0 || row <= 0){
                        break;
                    }
                }

                ChessPosition newPosition = new ChessPosition(row, column);
                if (board.getPiece(newPosition) == null ){
                    ChessMove newChessMove = new ChessMove(myPosition, newPosition, null);
                    moves.add(newChessMove);
                }else if (board.getPiece(newPosition).getTeamColor() != team){
                    ChessMove newChessMove = new ChessMove(myPosition, newPosition, null);
                    moves.add(newChessMove);
                    break;
                }else{
                    break;
                }
            }

        }
        return moves;
    }
}
