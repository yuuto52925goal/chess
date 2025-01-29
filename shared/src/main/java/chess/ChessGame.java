package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard chessBoard;
    private boolean blackCastling;
    private boolean whiteCastling;
    private boolean blackCastRook1;
    private boolean whiteCastRook1;
    private boolean blackCastRook2;
    private boolean whiteCastRook2;
    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        blackCastling = true;
        whiteCastling = true;
        whiteCastRook1 = true;
        whiteCastRook2 = true;
        blackCastRook1 = true;
        blackCastRook2 = true;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = chessBoard.getPiece(startPosition);
        ChessGame.TeamColor team = piece.getTeamColor();
        Collection<ChessMove> possibleMoves = piece.pieceMoves(chessBoard, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove possibleMove : new ArrayList<>(possibleMoves)) {
            ChessPiece temPiece = chessBoard.getPiece(possibleMove.getEndPosition());
            chessBoard.addPiece(possibleMove.getEndPosition(), piece);
            chessBoard.addPiece(possibleMove.getStartPosition(), null);
            if (!isInCheck(team)) {
                validMoves.add(possibleMove);
            }
            chessBoard.addPiece(possibleMove.getEndPosition(), temPiece);
            chessBoard.addPiece(possibleMove.getStartPosition(), piece);
        }
        boolean isCastling = team == TeamColor.BLACK ? blackCastling : whiteCastling;
        boolean isRook1 = team == TeamColor.WHITE ? whiteCastRook1 : blackCastRook1;
        boolean isRook2 = team == TeamColor.WHITE ? whiteCastRook2 : blackCastRook2;
        int row = team == TeamColor.BLACK ? 8 : 1;
        if (piece.getPieceType() == ChessPiece.PieceType.KING && startPosition.getRow() == row && startPosition.getColumn() == 5 && !isInCheck(team) && isCastling & (isRook1 || isRook2)){
            ChessPosition kingPosition = new ChessPosition(row, 5);
            if (isRook1 && chessBoard.getPiece(new ChessPosition(row, 1)) != null && chessBoard.getPiece(new ChessPosition(row, 1)).getPieceType() == ChessPiece.PieceType.ROOK && chessBoard.getPiece(new ChessPosition(row, 2)) == null && chessBoard.getPiece(new ChessPosition(row, 3)) == null && chessBoard.getPiece(new ChessPosition(row, 4)) == null) {
                ChessPosition rookPosition = new ChessPosition(row, 1);
                chessBoard.addPiece(kingPosition, null);
                chessBoard.addPiece(rookPosition, null);
                chessBoard.addPiece(new ChessPosition(row, 3), new ChessPiece(team, ChessPiece.PieceType.KING));
                chessBoard.addPiece(new ChessPosition(row, 4), new ChessPiece(team, ChessPiece.PieceType.KING));
                if (!isInCheck(team)) {
                    chessBoard.addPiece(new ChessPosition(row, 4), null);
                    if (!isInCheck(team)) {
                        validMoves.add(new ChessMove(kingPosition, new ChessPosition(row, 3), null));
                    }
                }
                chessBoard.addPiece(new ChessPosition(row, 3), null);
                chessBoard.addPiece(new ChessPosition(row, 4), null);
                chessBoard.addPiece(kingPosition, new ChessPiece(team, ChessPiece.PieceType.KING));
                chessBoard.addPiece(rookPosition, new ChessPiece(team, ChessPiece.PieceType.ROOK));
            }

            if (isRook2 && chessBoard.getPiece(new ChessPosition(row, 8)) != null && chessBoard.getPiece(new ChessPosition(row, 8)).getPieceType() == ChessPiece.PieceType.ROOK &&chessBoard.getPiece(new ChessPosition(row, 6)) == null && chessBoard.getPiece(new ChessPosition(row, 7)) == null) {
                ChessPosition rookPosition = new ChessPosition(row, 8);
                chessBoard.addPiece(kingPosition, null);
                chessBoard.addPiece(rookPosition, null);
                chessBoard.addPiece(new ChessPosition(row, 7), new ChessPiece(team, ChessPiece.PieceType.KING));
                chessBoard.addPiece(new ChessPosition(row, 6), new ChessPiece(team, ChessPiece.PieceType.KING));
                if (!isInCheck(team)) {
                    chessBoard.addPiece(new ChessPosition(row, 7), null);
                    if (!isInCheck(team)) {
                        validMoves.add(new ChessMove(kingPosition, new ChessPosition(row, 7), null));
                    }
                }
                chessBoard.addPiece(new ChessPosition(row, 7), null);
                chessBoard.addPiece(new ChessPosition(row, 6), null);
                chessBoard.addPiece(kingPosition, new ChessPiece(team, ChessPiece.PieceType.KING));
                chessBoard.addPiece(rookPosition, new ChessPiece(team, ChessPiece.PieceType.ROOK));
            }
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = chessBoard.getPiece(startPosition);

        if (piece == null) {
            throw  new InvalidMoveException();
        }

        if (this.validMoves(startPosition).contains(move) && piece.getTeamColor() == teamTurn){
            if (move.getPromotionPiece() != null){
                    chessBoard.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            }else {
                if (piece.getPieceType() == ChessPiece.PieceType.KING && (startPosition.getColumn() - move.getEndPosition().getColumn() == -2 || startPosition.getColumn() - move.getEndPosition().getColumn() == 2)){
                    chessBoard.addPiece(move.getEndPosition(), piece);
                    if (startPosition.getColumn() - move.getEndPosition().getColumn() == 2) {
                        chessBoard.addPiece(new ChessPosition(startPosition.getRow(), move.getEndPosition().getColumn() + 1), new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.ROOK));
                        chessBoard.addPiece(new ChessPosition(startPosition.getRow(), 1), null);
                    }else if (startPosition.getColumn() - move.getEndPosition().getColumn() == -2){
                        chessBoard.addPiece(new ChessPosition(startPosition.getRow(), move.getEndPosition().getColumn() - 1), new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.ROOK));
                        chessBoard.addPiece(new ChessPosition(startPosition.getRow(), 8), null);
                    }
                }else{
                    chessBoard.addPiece(move.getEndPosition(), piece);
                }
            }
            chessBoard.addPiece(move.getStartPosition(), null);
        }else{
            throw new InvalidMoveException("Invalid move");
        }

        if (piece.getPieceType() == ChessPiece.PieceType.KING){
            if (piece.getTeamColor() == TeamColor.WHITE) {
                whiteCastling = false;
            } else {
                blackCastling = false;
            }
        }
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            if (startPosition.getRow() == 1 && piece.getTeamColor() == TeamColor.WHITE && (startPosition.getColumn() == 1 || startPosition.getColumn() == 8)){
                if (startPosition.getColumn() == 1){
                    whiteCastRook1 = false;
                }else if (startPosition.getColumn() == 8){
                    whiteCastRook2 = false;
                }
            }else if(startPosition.getRow() == 8 && piece.getTeamColor() == TeamColor.BLACK && (startPosition.getColumn() == 1 || startPosition.getColumn() == 8)) {
                if (startPosition.getColumn() == 1){
                    blackCastRook1 = false;
                }else if (startPosition.getColumn() == 8){
                    blackCastRook2 = false;
                }
            }
        }
        this.teamTurn = this.teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        ArrayList<ChessPiece> opposingPieces = getOpposingPieces(teamColor);
        ArrayList<ChessPosition> opposingPositions = getOpposingPositions(teamColor);
        for (int i = 0; i < opposingPieces.size(); i++) {
            ChessPiece opposingPiece = opposingPieces.get(i);
            ChessPosition opposingPosition = opposingPositions.get(i);
            for(ChessMove opposingMove : opposingPiece.pieceMoves(chessBoard, opposingPosition)) {
                if (opposingMove.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        ArrayList<ChessPiece> opposingPieces = getOpposingPieces(teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        ArrayList<ChessPosition> opposingPositions = getOpposingPositions(teamColor == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK);
        for (int i = 0; i < opposingPieces.size(); i++) {
            ChessPiece opposingPiece = opposingPieces.get(i);
            ChessPosition opposingPosition = opposingPositions.get(i);
            for (ChessMove opposingMove: opposingPiece.pieceMoves(chessBoard, opposingPosition)) {
                ChessPiece temp = chessBoard.getPiece(opposingMove.getEndPosition());
                chessBoard.addPiece(opposingMove.getEndPosition(), opposingPiece);
                chessBoard.addPiece(opposingMove.getStartPosition(), null);
                if (!isInCheck(teamColor)) {
                    return false;
                }
                chessBoard.addPiece(opposingMove.getEndPosition(), temp);
                chessBoard.addPiece(opposingMove.getStartPosition(), opposingPiece);
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ArrayList<ChessPosition> opposingPositions = getOpposingPositions(teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessPosition opposingPosition : opposingPositions) {
            validMoves.addAll(this.validMoves(opposingPosition));
        }
        if (isInCheckmate(teamColor)){
            return false;
        }
        return validMoves.isEmpty();
    }

    public ChessPosition findKing(TeamColor teamColor){
        int row = 0;
        int col = 0;
        for (int i = 1; i < 9; i ++){
            for (int j = 1; j < 9; j ++){
                ChessPiece piece = chessBoard.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType().equals(ChessPiece.PieceType.KING)) {
                    row = i;
                    col = j;
                }
            }
        }
        return new ChessPosition(row, col);
    }

    public ArrayList<ChessPiece> getOpposingPieces(TeamColor teamColor) {
        ArrayList<ChessPiece> opposingPieces = new ArrayList<>();

        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPiece piece = chessBoard.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    opposingPieces.add(piece);
                }
            }
        }
        return opposingPieces;
    }

    public ArrayList<ChessPosition> getOpposingPositions(TeamColor teamColor) {
        ArrayList<ChessPosition> opposingPositions= new ArrayList<>();

        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPiece piece = chessBoard.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    opposingPositions.add(new ChessPosition(row, col));
                }
            }
        }
        return opposingPositions;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        blackCastling = true;
        whiteCastling = true;
        whiteCastRook1 = true;
        whiteCastRook2 = true;
        blackCastRook1 = true;
        blackCastRook2 = true;
        chessBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessBoard;
    }
}
