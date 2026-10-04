package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board;
    TeamColor teamTurn;

    public ChessGame() {

        board = new ChessBoard();
        board.resetBoard();

        teamTurn = TeamColor.WHITE;

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
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
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> okMoves = new ArrayList<>();

        for(ChessMove move : possibleMoves) {
            ChessPosition startPos = move.getStartPosition();
            ChessPosition endPos = move.getEndPosition();

            ChessBoard ogBoard = board;
            ChessBoard boardCopy = copyBoard(board);

            boardCopy.addPiece(endPos, piece);
            boardCopy.squares[startPos.getRow()-1][startPos.getColumn()-1] = null;

            board = boardCopy;

            if (!isInCheck(piece.getTeamColor())) {
                okMoves.add(move);
            }

            board = ogBoard;
        }

        return okMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (move == null) {
            throw new InvalidMoveException();
        }

        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();

        ChessPiece piece = board.getPiece(startPos);
        Collection<ChessMove> okayMoves = validMoves(startPos);

        if (piece == null || piece.getTeamColor() != teamTurn || !okayMoves.contains(move)) {
            throw new InvalidMoveException();
        }

        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }

        board.addPiece(endPos, piece);
        board.squares[startPos.getRow()-1][startPos.getColumn()-1] = null;

        if (teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        } else {
            teamTurn = TeamColor.WHITE;
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = null;

        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <=8; c++) {
                ChessPosition position = new ChessPosition(r,c);
                ChessPiece piece = board.getPiece(position);

                if (piece != null) {
                    if (piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING ) {
                        kingPos = position;
                    }
                }
            }
        }

        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <=8; c++) {
                ChessPosition position = new ChessPosition(r,c);
                ChessPiece piece = board.getPiece(position);

                if (piece != null) {
                    if (piece.getTeamColor() != teamColor) {
                        Collection<ChessMove> opMoves = piece.pieceMoves(board, position);

                        for (ChessMove move : opMoves) {
                            if (move.getEndPosition().equals(kingPos)) {
                                return true;
                            }
                        }
                    }
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
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <=8; c++) {
                ChessPosition position = new ChessPosition(r,c);
                ChessPiece piece = board.getPiece(position);

                if (piece != null) {
                    if (piece.getTeamColor() == teamColor) {
                        Collection<ChessMove> teamMoves = validMoves(position);

                        if (!teamMoves.isEmpty()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <=8; c++) {
                ChessPosition position = new ChessPosition(r,c);
                ChessPiece piece = board.getPiece(position);

                if (piece != null) {
                    if (piece.getTeamColor() == teamColor) {
                        Collection<ChessMove> teamMoves = validMoves(position);

                        if (!teamMoves.isEmpty()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public ChessBoard copyBoard(ChessBoard board) {
        ChessBoard copy = new ChessBoard();

        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition position = new ChessPosition(r, c);

                ChessPiece piece = board.getPiece(position);

                if (piece != null) {
                    copy.addPiece(position, piece);
                }
            }
        }
        return copy;
    }
}
