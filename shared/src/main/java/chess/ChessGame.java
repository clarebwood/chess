package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static java.lang.Math.abs;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board;
    TeamColor teamTurn;

    boolean whiteKingMoved;
    boolean blackKingMoved;

    boolean kingRookMoved;
    boolean queenRookMoved;



    public ChessGame() {

        board = new ChessBoard();
        board.resetBoard();

        teamTurn = TeamColor.WHITE;

        whiteKingMoved = false;
        blackKingMoved = false;

        kingRookMoved = false;
        queenRookMoved = false;

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
        Collection<ChessMove> okMoves = new ArrayList<>();

        if (piece != null) {
            Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);

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

            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                okMoves.addAll(castlingMoves(startPosition));
            }
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

        if (piece.getPieceType() == ChessPiece.PieceType.KING && abs(endPos.getColumn() - startPos.getColumn()) == 2) {
            int startRow = startPos.getRow();

            if (endPos.getColumn() > startPos.getColumn()) {
                ChessPosition rookStart = new ChessPosition(startRow, 8);
                ChessPosition rookEnd = new ChessPosition(startRow, 6);

                ChessPiece rook = board.getPiece(rookStart);

                board.addPiece(rookEnd, rook);
                board.squares[startRow - 1][7] = null;
            } else {
                ChessPosition rookStart = new ChessPosition(startRow, 1);
                ChessPosition rookEnd = new ChessPosition(startRow, 4);

                ChessPiece rook = board.getPiece(rookStart);

                board.addPiece(rookEnd, rook);
                board.squares[startRow - 1][0] = null;
            }
        }

        board.addPiece(endPos, piece);
        board.squares[startPos.getRow()-1][startPos.getColumn()-1] = null;

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            if (startPos.getRow() == 1 && startPos.getColumn() == 1) {
                queenRookMoved = true;
            }

            if (startPos.getRow() == 1 && startPos.getColumn() == 8) {
                kingRookMoved = true;
            }
        }


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

    Collection<ChessMove> castlingMoves (ChessPosition startPos) {
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece king = board.getPiece(startPos);
        TeamColor color = king.getTeamColor();

        int startRow;

        if (color == TeamColor.WHITE) {
            startRow = 1;
        } else {
            startRow = 8;
        }

        if (startRow != startPos.getRow() || startPos.getColumn() != 5) {
            return moves;
        }

        boolean kingMoved = false;

        if (color == TeamColor.WHITE) {
            if (whiteKingMoved) {
                kingMoved = true;
            }
        } else {
            if (blackKingMoved) {
                kingMoved = true;
            }
        }

        if (kingMoved || isInCheck(color)) {
            return moves;
        }

        ChessPosition kingRookPosition = new ChessPosition(startRow, 8);
        ChessPiece kingRook = board.getPiece(kingRookPosition);

        ChessPosition queenRookPosition = new ChessPosition(startRow, 1);
        ChessPiece queenRook = board.getPiece(queenRookPosition);


        if (!kingRookMoved && kingRook != null && kingRook.getPieceType() == ChessPiece.PieceType.ROOK
                && board.getPiece(new ChessPosition(startRow, 6)) == null
                && board.getPiece(new ChessPosition(startRow, 7)) == null) {

            ChessBoard boardCopy = copyBoard(board);
            boardCopy.addPiece(new ChessPosition(startRow, 6), king);
            boardCopy.squares[startRow - 1][4] = null;

            ChessBoard ogBoard = board;
            board = boardCopy;

            if (!isInCheck(color)) {
                board = ogBoard;
                boardCopy = copyBoard(board);
                ChessPosition destination = new ChessPosition(startRow, 7);

                boardCopy.addPiece(destination, king);
                boardCopy.squares[startRow - 1][4] = null;

                board = boardCopy;

                if (!isInCheck(color)) {
                    board = ogBoard;
                    moves.add(new ChessMove(startPos, destination, null));
                }
            }

            board = ogBoard;

        }

        if (!queenRookMoved && queenRook != null && queenRook.getPieceType() == ChessPiece.PieceType.ROOK
                && board.getPiece(new ChessPosition(startRow, 2)) == null
                && board.getPiece(new ChessPosition(startRow, 3)) == null
                && board.getPiece(new ChessPosition(startRow, 4)) == null){

            ChessBoard boardCopy = copyBoard(board);
            boardCopy.addPiece(new ChessPosition(startRow, 4), king);
            boardCopy.squares[startRow - 1][4] = null;

            ChessBoard ogBoard = board;
            board = boardCopy;

            if (!isInCheck(color)) {
                board = ogBoard;
                boardCopy = copyBoard(board);
                ChessPosition destination = new ChessPosition(startRow, 3);

                boardCopy.addPiece(destination, king);
                boardCopy.squares[startRow - 1][4] = null;

                board = boardCopy;

                if (!isInCheck(color)) {
                    board = ogBoard;
                    moves.add(new ChessMove(startPos, destination, null));
                }
            }

            board = ogBoard;

        }

        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
