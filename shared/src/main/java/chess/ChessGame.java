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
    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        chessBoard = new ChessBoard();
        chessBoard.resetBoard();
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

        if (this.validMoves(startPosition).contains(move)){
            if (piece.getTeamColor() == teamTurn) {
                if (move.getPromotionPiece() != null){
                    chessBoard.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
                }else {
                    chessBoard.addPiece(move.getEndPosition(), piece);
                }
                chessBoard.addPiece(move.getStartPosition(), null);
            }else{
                throw new InvalidMoveException("Team color not changed after move made");
            }
        }else{
            throw new InvalidMoveException("Invalid move");
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
            Collection<ChessMove> opposingMoves = opposingPiece.pieceMoves(chessBoard, opposingPosition);
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
