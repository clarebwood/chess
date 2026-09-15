package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        MoveCalc movesList = new MoveCalc(board, myPosition, this);
        return movesList.listMoves();
    }
    
    private class MoveCalc {
        ChessBoard board;
        ChessPosition myPosition;
        ChessPiece myPiece;
        int curRow;
        int curCol;
        
        public MoveCalc (ChessBoard board, ChessPosition myPosition, ChessPiece myPiece) {
            this.board = board;
            this.myPosition = myPosition;
            this.myPiece = myPiece;
            this.curRow = myPosition.getRow();
            this.curCol = myPosition.getColumn();
        }
        
        public Collection<ChessMove> listMoves() {
            return switch (myPiece.getPieceType()) {
                case PieceType.BISHOP -> bishopMoves();
                case PieceType.KING -> kingMoves();
                case PieceType.KNIGHT -> knightMoves();
                case PieceType.PAWN -> pawnMoves();
                case PieceType.QUEEN -> bishopMoves();
                case PieceType.ROOK -> bishopMoves();
            };
        };
        
        public boolean checkSpot (int row, int col, Collection<ChessMove> lst) {
            if (row < 1 || row > 8 || col < 1 || col > 8) {
                return false;
            }
            
            ChessPosition newPos = new ChessPosition(row,col);
            
            if (board.getPiece(newPos) == null) {
                lst.add(new ChessMove(myPosition, newPos, null));
                return true;
            } else if (board.getPiece(newPos).getTeamColor() != myPiece.getTeamColor()) {
                lst.add(new ChessMove(myPosition, newPos, null));
                return false;
            }

            return false;
        }

        public Collection<ChessMove> bishopMoves() {
            Collection<ChessMove> bishopList = new ArrayList<>();
            for (int i = 1; i<=8; i++) {
                if (!checkSpot(curRow + i, curCol + i, bishopList)) {
                    break;
                }
            }
            for (int i = 1; i<=8; i++) {
                if (!checkSpot(curRow - i, curCol + i, bishopList)) {
                    break;
                }
            }
            for (int i = 1; i<=8; i++) {
                if (!checkSpot(curRow + i, curCol - i, bishopList)) {
                    break;
                }
            }
            for (int i = 1; i<=8; i++) {
                if (!checkSpot(curRow - i, curCol - i, bishopList)) {
                    break;
                }
            }
            return bishopList;
        }

        public Collection<ChessMove> kingMoves() {
            Collection<ChessMove> kingList = new ArrayList<>();
            for (int r = -1; r<=1; r++) {
                int kingRow = curRow + r;
                for (int c = -1; c<=1; c++) {
                    int kingCol = curCol + c;
                    if (kingRow != curRow || kingCol != curCol) {
                        checkSpot(kingRow, kingCol, kingList);
                    }

                }
            }
            return kingList;
        }

        public Collection<ChessMove> knightMoves() {
            Collection<ChessMove> knightList = new ArrayList<>();
            int knightRow [] = {-2, -1, 1, 2};
            int knightCol [] = {-2, -1, 1, 2};

            for (int r : knightRow) {
                for (int c : knightCol) {
                    if (abs(r) != abs(c)) {
                        checkSpot(curRow + r, curCol+ c, knightList);
                    }
                }
            }

            return knightList;
        }

        public Collection<ChessMove> pawnMoves() {
            Collection<ChessMove> pawnList = new ArrayList<>();
            int direction;
            int startRow;
            int promoRow;

            if (myPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                direction = -1;
                startRow = 7;
                promoRow = 1;
            } else {
                direction = 1;
                startRow = 2;
                promoRow = 8;
            }

            int pawnRow = curRow + direction;
            int doublePawnRow = curRow + (direction *2);

            if (pawnRow >=1 && pawnRow <=8) {
                ChessPosition newPawnPos = new ChessPosition(pawnRow, curCol);

                if (board.getPiece(newPawnPos) == null) {

                    if (pawnRow == promoRow) {
                        pawnList.add(new ChessMove(myPosition, newPawnPos, PieceType.ROOK));
                        pawnList.add(new ChessMove(myPosition, newPawnPos, PieceType.BISHOP));
                        pawnList.add(new ChessMove(myPosition, newPawnPos, PieceType.QUEEN));
                        pawnList.add(new ChessMove(myPosition, newPawnPos, PieceType.KNIGHT));
                    } else {
                        pawnList.add(new ChessMove(myPosition, newPawnPos, null));
                    }

                    if (curRow == startRow) {
                        ChessPosition doublePawnPos = new ChessPosition(doublePawnRow, curCol);

                        if (board.getPiece(doublePawnPos) == null) {
                            pawnList.add(new ChessMove(myPosition, doublePawnPos, null));
                        }
                    }
                }
            }

            int pawnCol [] = {curCol -1, curCol +1};

            for (int c : pawnCol) {
                if ( c >= 1 && c <= 8) {
                    ChessPosition capturePos = new ChessPosition(pawnRow, c);
                    ChessPiece target = board.getPiece((capturePos));

                    if (target != null && target.getTeamColor() != myPiece.getTeamColor()) {
                        if (pawnRow == promoRow) {
                            pawnList.add(new ChessMove(myPosition, capturePos, PieceType.ROOK));
                            pawnList.add(new ChessMove(myPosition, capturePos, PieceType.BISHOP));
                            pawnList.add(new ChessMove(myPosition, capturePos, PieceType.QUEEN));
                            pawnList.add(new ChessMove(myPosition, capturePos, PieceType.KNIGHT));
                        } else {
                            pawnList.add(new ChessMove(myPosition, capturePos, null));
                        }
                    }

                }
            }

            return pawnList;
        }
    }
}
