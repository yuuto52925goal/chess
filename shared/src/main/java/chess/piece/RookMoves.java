package chess.piece;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoves implements ChessPieceCalculator {
    String[] rookMoves = {"VP", "VN", "HP", "HN"};

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor team = board.getPiece(myPosition).getTeamColor();
        int row;
        int column;
        for (String moveDirection : rookMoves) {
            row = myPosition.getRow();
            column = myPosition.getColumn();
            while (true){
                if (moveDirection.equals("VP")){
                    row++;
                    if (row > 8){
                        break;
                    }
                }else if (moveDirection.equals("VN")){
                    row --;
                    if (row <= 0){
                        break;
                    }
                }else if (moveDirection.equals("HP")){
                    column++;
                    if (column > 8){
                        break;
                    }
                }else{
                    column --;
                    if (column <= 0){
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
    };
}
