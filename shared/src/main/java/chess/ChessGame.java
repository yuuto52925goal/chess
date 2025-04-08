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
    private ChessPosition enpassantPosition;
    private boolean gameStatus;
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
        enpassantPosition = null;
        gameStatus = false;
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

    public void setGameStatus(boolean gameStatus) {
        this.gameStatus = gameStatus;
    }

    public boolean getGameStatus() {
        return gameStatus;
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

        addCastlingMovesIfPossible(validMoves, piece, startPosition, team);
        addEnPassantMovesIfPossible(validMoves, piece, startPosition);

        return validMoves;
    }

    private void addCastlingMovesIfPossible(
            Collection<ChessMove> validMoves,
            ChessPiece piece,
            ChessPosition startPosition,
            ChessGame.TeamColor team
    ) {
        if (piece.getPieceType() != ChessPiece.PieceType.KING || isInCheck(team)) {
            return;
        }

        boolean isCastling = (team == TeamColor.BLACK) ? blackCastling : whiteCastling;
        boolean isRook1 = (team == TeamColor.WHITE) ? whiteCastRook1 : blackCastRook1;
        boolean isRook2 = (team == TeamColor.WHITE) ? whiteCastRook2 : blackCastRook2;
        // Identify the row where the king and rooks would be
        int row = (team == TeamColor.BLACK) ? 8 : 1;

        if (startPosition.getRow() != row || startPosition.getColumn() != 5 || !isCastling) {
            return;
        }

        if (isRook1) {
            tryCastleQueenSide(validMoves, team, row);
        }

        if (isRook2) {
            tryCastleKingSide(validMoves, team, row);
        }
    }

    /**
     * Attempts queen-side castling (also called long castling).
     */
    private void tryCastleQueenSide(Collection<ChessMove> validMoves, ChessGame.TeamColor team, int row) {
        ChessPosition kingPos = new ChessPosition(row, 5);
        ChessPosition rookPos = new ChessPosition(row, 1);

        // Ensure the rook is in place and squares between are empty
        ChessPiece rook = chessBoard.getPiece(rookPos);
        if (rook == null || rook.getPieceType() != ChessPiece.PieceType.ROOK) {
            return;
        }
        if (chessBoard.getPiece(new ChessPosition(row, 2)) != null
                || chessBoard.getPiece(new ChessPosition(row, 3)) != null
                || chessBoard.getPiece(new ChessPosition(row, 4)) != null) {
            return;
        }

        chessBoard.addPiece(kingPos, null);
        chessBoard.addPiece(rookPos, null);

        chessBoard.addPiece(new ChessPosition(row, 3), new ChessPiece(team, ChessPiece.PieceType.KING));
        chessBoard.addPiece(new ChessPosition(row, 4), new ChessPiece(team, ChessPiece.PieceType.KING));

        if (!isInCheck(team)) {
            chessBoard.addPiece(new ChessPosition(row, 4), null);
            if (!isInCheck(team)) {
                validMoves.add(new ChessMove(kingPos, new ChessPosition(row, 3), null));
            }
        }

        chessBoard.addPiece(new ChessPosition(row, 3), null);
        chessBoard.addPiece(new ChessPosition(row, 4), null);
        chessBoard.addPiece(kingPos, new ChessPiece(team, ChessPiece.PieceType.KING));
        chessBoard.addPiece(rookPos, new ChessPiece(team, ChessPiece.PieceType.ROOK));
    }

    /**
     * Attempts king-side castling (also called short castling).
     */
    private void tryCastleKingSide(Collection<ChessMove> validMoves, ChessGame.TeamColor team, int row) {
        ChessPosition kingPos = new ChessPosition(row, 5);
        ChessPosition rookPos = new ChessPosition(row, 8);

        ChessPiece rook = chessBoard.getPiece(rookPos);
        if (rook == null || rook.getPieceType() != ChessPiece.PieceType.ROOK) {
            return;
        }
        if (chessBoard.getPiece(new ChessPosition(row, 6)) != null
                || chessBoard.getPiece(new ChessPosition(row, 7)) != null) {
            return;
        }

        chessBoard.addPiece(kingPos, null);
        chessBoard.addPiece(rookPos, null);

        chessBoard.addPiece(new ChessPosition(row, 7), new ChessPiece(team, ChessPiece.PieceType.KING));
        chessBoard.addPiece(new ChessPosition(row, 6), new ChessPiece(team, ChessPiece.PieceType.KING));

        if (!isInCheck(team)) {
            chessBoard.addPiece(new ChessPosition(row, 7), null);
            if (!isInCheck(team)) {
                validMoves.add(new ChessMove(kingPos, new ChessPosition(row, 7), null));
            }
        }

        chessBoard.addPiece(new ChessPosition(row, 7), null);
        chessBoard.addPiece(new ChessPosition(row, 6), null);
        chessBoard.addPiece(kingPos, new ChessPiece(team, ChessPiece.PieceType.KING));
        chessBoard.addPiece(rookPos, new ChessPiece(team, ChessPiece.PieceType.ROOK));
    }

    private void addEnPassantMovesIfPossible(
            Collection<ChessMove> validMoves,
            ChessPiece piece,
            ChessPosition startPosition
    ) {
        if (piece.getPieceType() != ChessPiece.PieceType.PAWN || enpassantPosition == null) {
            return;
        }

        int direction = (piece.getTeamColor() == TeamColor.BLACK) ? -1 : 1;
        ChessPosition left = new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 1);
        ChessPosition right = new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 1);

        if (left.equals(enpassantPosition) || right.equals(enpassantPosition)) {
            ChessPosition capturePosition = new ChessPosition(
                    startPosition.getRow() + direction,
                    enpassantPosition.getColumn()
            );
            validMoves.add(new ChessMove(startPosition, capturePosition, null));
        }
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
            throw new InvalidMoveException();
        }

        if (this.validMoves(startPosition).contains(move)
                && piece.getTeamColor() == teamTurn)
        {
            if (move.getPromotionPiece() != null) {
                chessBoard.addPiece(move.getEndPosition(),
                        new ChessPiece(piece.getTeamColor(),
                                move.getPromotionPiece()));
            } else {
                boolean isKing = (piece.getPieceType() == ChessPiece.PieceType.KING);
                int colDiff = startPosition.getColumn() - move.getEndPosition().getColumn();

                // Handle castling
                if (isKing && (colDiff == -2 || colDiff == 2)) {
                    chessBoard.addPiece(move.getEndPosition(), piece);
                    if (colDiff == 2) {
                        chessBoard.addPiece(
                                new ChessPosition(startPosition.getRow(),
                                        move.getEndPosition().getColumn() + 1),
                                new ChessPiece(piece.getTeamColor(),
                                        ChessPiece.PieceType.ROOK)
                        );
                        chessBoard.addPiece(new ChessPosition(startPosition.getRow(), 1),
                                null);
                    } else if (colDiff == -2) {
                        chessBoard.addPiece(
                                new ChessPosition(startPosition.getRow(),
                                        move.getEndPosition().getColumn() - 1),
                                new ChessPiece(piece.getTeamColor(),
                                        ChessPiece.PieceType.ROOK)
                        );
                        chessBoard.addPiece(new ChessPosition(startPosition.getRow(), 8),
                                null);
                    }
                } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN
                        && enpassantPosition != null
                        && (move.getEndPosition().getColumn()
                        - move.getStartPosition().getColumn()) != 0
                        && chessBoard.getPiece(move.getEndPosition()) == null)
                {
                    int direction = (piece.getTeamColor() == TeamColor.BLACK) ? -1 : 1;
                    chessBoard.addPiece(
                            new ChessPosition(
                                    move.getEndPosition().getRow() - direction,
                                    move.getEndPosition().getColumn()
                            ),
                            null
                    );
                    chessBoard.addPiece(move.getEndPosition(), piece);
                } else {
                    // Normal move
                    chessBoard.addPiece(move.getEndPosition(), piece);
                }
            }
            chessBoard.addPiece(move.getStartPosition(), null);
        } else {
            throw new InvalidMoveException("Invalid move");
        }

        updateCastlingRights(piece, move.getStartPosition());
        updateEnPassant(piece, move);

        this.teamTurn = (this.teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    private void updateCastlingRights(ChessPiece piece, ChessPosition startPosition) {
        // Disable castling entirely if the king moves
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                whiteCastling = false;
            } else {
                blackCastling = false;
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            if (startPosition.getRow() == 1
                    && piece.getTeamColor() == TeamColor.WHITE
                    && (startPosition.getColumn() == 1 || startPosition.getColumn() == 8))
            {
                if (startPosition.getColumn() == 1) {
                    whiteCastRook1 = false;
                } else {
                    whiteCastRook2 = false;
                }
            } else if (startPosition.getRow() == 8
                    && piece.getTeamColor() == TeamColor.BLACK
                    && (startPosition.getColumn() == 1 || startPosition.getColumn() == 8))
            {
                if (startPosition.getColumn() == 1) {
                    blackCastRook1 = false;
                } else {
                    blackCastRook2 = false;
                }
            }
        }
    }

    private void updateEnPassant(ChessPiece piece, ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN
                && Math.abs(end.getRow() - start.getRow()) == 2) {
            enpassantPosition = new ChessPosition(end.getRow(), end.getColumn());
        } else {
            enpassantPosition = null;
        }
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
        enpassantPosition = null;
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
