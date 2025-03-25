package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessBoardDrawer {

    public static void drawChessBoard(ChessBoard chessBoard, String teamColor) {
        if (teamColor.equals("black")) {
            System.out.println(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE +
                               "    h  g  f  e  d  c  b  a    " +
                               EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
        }else{
            System.out.println(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE +
                               "    a  b  c  d  e  f  g  h    " +
                               EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
        }

        int startRow = (teamColor.equals("black")) ? 1 : 8;
        int endRow = (teamColor.equals("black")) ? 8 : 1;
        int step = (teamColor.equals("black")) ? 1 : -1;

        for (int row = startRow; row != endRow + step; row += step) {
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE +
                             " " + row + " " +
                             EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);

            for (int col = 1; col <= 8; col++) {
                String squareColor = ((row + col) % 2 == 0) ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                        : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                if (teamColor.equals("white")) {
                    squareColor = ((row + col) % 2 == 0) ? EscapeSequences.SET_BG_COLOR_DARK_GREY
                            : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                }

                ChessPiece piece = chessBoard.getPiece(new ChessPosition(row, col));
                String pieceRepresentation = getPieceRepresentation(piece);

                System.out.print(squareColor + pieceRepresentation + EscapeSequences.RESET_BG_COLOR);
            }

            System.out.println(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE +
                               " " + row + " " +
                               EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
        }

        if (teamColor.equals("black")) {
            System.out.println(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE +
                               "    h  g  f  e  d  c  b  a    " +
                               EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
        }else{
            System.out.println(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE +
                               "    a  b  c  d  e  f  g  h    " +
                               EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
        }
    }

    private static String getPieceRepresentation(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY; // Empty square
        }

        switch (piece.getPieceType()) {
            case KING:
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN:
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case BISHOP:
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT:
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ROOK:
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case PAWN:
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            default:
                return EscapeSequences.EMPTY;
        }
    }
}
