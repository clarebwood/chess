package chess;

import java.util.ArrayList;
import java.util.Collection;
import static java.lang.Math.abs;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    public final ChessGame.TeamColor pieceColor;
    public final PieceType type;


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
        MoveCalc listMoves = new MoveCalc (board, myPosition, this);
        return listMoves.listMoves ();
    }

    private class MoveCalc {
        ChessBoard board;
        ChessPosition myPosition;
        int curRow;
        int curCol;
        ChessPiece myPiece;

        public MoveCalc(ChessBoard board, ChessPosition myPosition, ChessPiece myPiece) {
            this.board = board;
            this.myPosition = myPosition;
            this.myPiece = myPiece;
            this.curCol = curCol;
            this.curRow = curRow;
        }

        public Collection<ChessMove> listMoves(){
            return switch (myPiece.getPieceType()) {
                case PieceType.KING -> kingMoves();
                case PieceType.QUEEN -> queenMoves();
                case PieceType.BISHOP -> bishopMoves();
                case PieceType.KNIGHT -> knightMoves();
                case PieceType.ROOK -> rookMoves();
                case PieceType.PAWN -> pawnMoves();
            };
        }

        public Collection<ChessMove> pawnMoves(){
            Collection<ChessMove> pawnList = new ArrayList<>();
            int row = curRow;
            int col = curCol;
            if (myPiece.getTeamColor()== ChessGame.TeamColor.BLACK) {
                row -= 1;
            } else {
                row += 1;
            }
            ChessPosition newPos = new ChessPosition(row, col);
            checkPawnMove (row, col, newPos, pawnList);
            return pawnList;
        }

        private void checkPawnMove(int row, int col, ChessPosition newPos,Collection<ChessMove> pawnList) {
            if (board.getPiece(newPos) == null) {
                if (row == 1 || row == 8) {
                    promotePawn (pawnList, newPos);
                } else if (row == 2 || row == 7){
                    pawnList.add(new ChessMove(myPosition, newPos, null));
                    int i;
                    if (myPiece.getTeamColor()== ChessGame.TeamColor.BLACK) {
                        i = -1;
                    } else {
                        i = 1;
                    }
                    int doubleRow = row + i;
                    newPos = new ChessPosition(doubleRow, col);
                } else {
                    pawnList.add(new ChessMove(myPosition, newPos, null));
                }
                for (int i = -1; i <= 1; i++) {
                    if (col >= 1 && col <= 8) {
                        newPos = new ChessPosition(row, col);
                    }
                    if (board.getPiece(newPos)!= null && board.getPiece(newPos).getTeamColor() != myPiece.getTeamColor()) {
                        if (row == 1 || row == 8) {
                            promotePawn(pawnList, newPos);
                        } else {
                            pawnList.add(new ChessMove(myPosition, newPos, null));
                        }
                    }
                }
            }
        }
        private void promotePawn(Collection<ChessMove> pawnList, ChessPosition newPos) {
            pawnList.add(new ChessMove(myPosition, newPos, PieceType.ROOK));
            pawnList.add(new ChessMove(myPosition, newPos, PieceType.BISHOP));
            pawnList.add(new ChessMove(myPosition, newPos, PieceType.KNIGHT));
            pawnList.add(new ChessMove(myPosition, newPos, PieceType.QUEEN));
        }

        public Collection<ChessMove> kingMoves(){
            Collection<ChessMove> kingList = new ArrayList<>();
            int row;
            int col;
            for (int i = -1; i <= 1; i++) {
                row = curRow + i;
                for (int x = -1; x <= 1; x++) {
                    col = curCol + x;
                    if (!checkSpot(row, col, kingList)) {
                        break;
                    }
                }
            }
            return kingList;
        }

        public Collection<ChessMove> queenMoves(){
            Collection<ChessMove> queenList = new ArrayList<>();
            queenList.addAll(rookMoves());
            queenList.addAll(bishopMoves());
            return queenList;
        }

        public Collection<ChessMove> bishopMoves(){
            Collection<ChessMove> bishopList = new ArrayList<>();
            for (int i = 1; i <= 8; i++) {
                if (!checkSpot(curRow + i, curCol + i, bishopList)){
                    break;
                }
            }
            for (int i = 1; i <= 8; i++) {
                if (!checkSpot(curRow - i, curCol + i, bishopList)){
                    break;
                }
            }
            for (int i = 1; i <= 8; i++) {
                if (!checkSpot(curRow - i, curCol - i, bishopList)){
                    break;
                }
            }
            for (int i = 1; i <= 8; i++) {
                if (!checkSpot(curRow + i, curCol - i, bishopList)){
                    break;
                }
            }
            return bishopList;
        }

        public Collection<ChessMove> knightMoves(){
            Collection<ChessMove> knightList = new ArrayList<>();
            int row;
            int col;
            for (int i = -2; i <= 2; i++) {
                for (int x = -2; x <= 2; x++) {
                    if (abs(i) == abs(x)){
                        continue;
                    } else if (!checkSpot(curRow + i, curCol + x, knightList)){
                        continue;
                    }
                }
            }
            return knightList;
        }

        public Collection<ChessMove> rookMoves(){
            Collection<ChessMove> rookList = new ArrayList<>();
            for (int i = 1; i <= 8; i++) {
                if (!checkSpot(curRow + i, curCol, rookList)){
                    break;
                }
            }
            for (int i = 1; i <= 8; i++) {
                if (!checkSpot(curRow - i, curCol, rookList)){
                    break;
                }
            }
            for (int i = 1; i <= 8; i++) {
                if (!checkSpot(curRow, curCol + i, rookList)){
                    break;
                }
            }
            for (int i = 1; i <= 8; i++) {
                if (!checkSpot(curRow, curCol - i, rookList)){
                    break;
                }
            }
            return rookList;
        }

        public boolean checkSpot( int row, int col, Collection<ChessMove> lst) {
            if (row < 1 || row > 8 || col < 1 || col > 8) {
                return false;
            }
            ChessPosition newPos = new ChessPosition(row, col);
            if (board.getPiece(newPos) == null) {
                lst.add(new ChessMove(myPosition, newPos, null));
                return true;
            } else if (board.getPiece(newPos).getTeamColor() != myPiece.getTeamColor()) {
                lst.add(new ChessMove(myPosition, newPos, null));
                return false;
            } else {
                return false;
            }
        }
    }

}