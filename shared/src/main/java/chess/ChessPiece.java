package chess;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    ChessGame.TeamColor color;
    PieceType coolType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        color = pieceColor;
        coolType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return coolType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        switch (coolType) {
            case KING:
                kingMoves(board, myPosition, moves);
                break;
            case QUEEN:
                queenMoves(board, myPosition, moves);
                break;
            case BISHOP:
                bishopMoves(board, myPosition, moves);
                break;
            case KNIGHT:
                knightMoves(board, myPosition, moves);
                break;
            case ROOK:
                rookMoves(board, myPosition, moves);
                break;
            case PAWN:
                pawnMoves(board, myPosition, moves);
                break;
        }

        return moves;
    }


    public void kingMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int[] directions = {-1, 0, 1};
        for (int dr : directions) {
            for (int dc : directions) {
                if (dr == 0 && dc == 0) continue;
                ChessPosition newPos = myPosition.move(dr, dc);
                if (board.isInBounds(newPos)) {
                    ChessPiece piece = board.getPiece(newPos);
                    if (piece == null || piece.getTeamColor() != color) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    }
                }
            }
        }
    }

    public void queenMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        rookMoves(board, myPosition, moves);
        bishopMoves(board, myPosition, moves);
    }

    public void bishopMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        diagonalMoves(board, myPosition, moves);
    }
    public void diagonalMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        linearMoves(board, myPosition, moves, 1, 1);
        linearMoves(board, myPosition, moves, 1, -1);
        linearMoves(board, myPosition, moves, -1, 1);
        linearMoves(board, myPosition, moves, -1, -1);
    }

    public void knightMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int[] rowOffsets = {-2, -1, 1, 2, 2, 1, -1, -2};
        int[] colOffsets = {1, 2, 2, 1, -1, -2, -2, -1};

        for (int i = 0; i < 8; i++) {
            ChessPosition newPos = myPosition.move(rowOffsets[i], colOffsets[i]);
            if (board.isInBounds(newPos)) {
                ChessPiece piece = board.getPiece(newPos);
                if (piece == null || piece.getTeamColor() != color) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                }
            }
        }
    }

    public void rookMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        linearMoves(board, myPosition, moves, 0, 1);
        linearMoves(board, myPosition, moves, 0, -1);
        linearMoves(board, myPosition, moves, 1, 0);
        linearMoves(board, myPosition, moves, -1, 0);
    }
    private void linearMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int rowDir, int colDir) {
        ChessPosition newPos = myPosition;

        while (true) {
            newPos = newPos.move(rowDir, colDir);
            if (board.isInBounds(newPos)) {
                ChessPiece piece = board.getPiece(newPos);
                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                } else {
                    if (piece.getTeamColor() != color) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    }
                    break;
                }
            } else {
                break;
            }
        }
    }

    private void pawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int direction = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;

        ChessPosition forwardOne = myPosition.move(direction, 0);
        if (board.isInBounds(forwardOne) && board.getPiece(forwardOne) == null) {
            pawnMove(board, moves, myPosition, forwardOne);
        }

        int startingRow = (color == ChessGame.TeamColor.WHITE) ? 1 : 6;
        if (myPosition.getRow() == startingRow) {
            ChessPosition forwardTwo = myPosition.move(2 * direction, 0);
            if (board.isInBounds(forwardTwo) && board.getPiece(forwardOne) == null && board.getPiece(forwardTwo) == null) {
                moves.add(new ChessMove(myPosition, forwardTwo, null));
            }
        }

        int[] captureOffsets = {-1, 1};
        for (int offset : captureOffsets) {
            ChessPosition capturePos = myPosition.move(direction, offset);
            if (board.isInBounds(capturePos)) {
                ChessPiece capturedPiece = board.getPiece(capturePos);
                if (capturedPiece != null && capturedPiece.getTeamColor() != color) {
                    pawnMove(board, moves, myPosition, capturePos);
                }
            }
        }
    }

    private void pawnMove(ChessBoard board, Collection<ChessMove> moves, ChessPosition start, ChessPosition end) {
        int promotionRow = (color == ChessGame.TeamColor.WHITE) ? 7 : 0;

        if (end.getRow() == promotionRow) {
            for (ChessPiece.PieceType promotion : new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT}) {
                moves.add(new ChessMove(start, end, promotion));
            }
        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }




    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && coolType == that.coolType;
    }

}
