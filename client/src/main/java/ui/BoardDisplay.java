package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class BoardDisplay {
    private static final String RESET = "\u001B[0m";
    private static final String LIGHT = "\u001B[107m";
    private static final String DARK = "\u001B[100m";

    public static void draw(boolean whiteView) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        System.out.println();
        printColumnLabels(whiteView);

        for (int row = 0; row < 8; row++) {
            int horizontal;
            if (whiteView) {
                horizontal = 8 - row;
            } else {
                horizontal = row + 1;
            }
            System.out.print(" " + horizontal + " ");

            for (int col = 0; col < 8; col++) {
                int actualRow = horizontal;
                int actualCol = whiteView ? col + 1 : 8 - col;

                ChessPosition position = new ChessPosition(actualRow, actualCol);
                ChessPiece piece = board.getPiece(position);

                String symbol = getSymbol(piece);

                boolean lightSquare = (horizontal + actualCol) % 2 != 0;
                String background = lightSquare ? LIGHT : DARK;

                System.out.print(background + " " + symbol + " " + RESET);
            }
            System.out.println(" " + horizontal);
        }
        printColumnLabels(whiteView);
    }

    private static String getSymbol(ChessPiece piece) {
        if (piece == null) {
            return " ";
        }

        boolean white = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        return switch (piece.getPieceType()) {
            case ChessPiece.PieceType.BISHOP -> white ? "♗" : "♝";
            case ChessPiece.PieceType.KING -> white ? "♔" : "♚";
            case ChessPiece.PieceType.KNIGHT -> white ? "♘" : "♞";
            case ChessPiece.PieceType.PAWN -> white ? "♙" : "♟";
            case ChessPiece.PieceType.QUEEN -> white ? "♕" : "♛";
            case ChessPiece.PieceType.ROOK -> white ? "♖" : "♜";
        };
    }

    private static void printColumnLabels(boolean whiteView) {
        System.out.print("   ");

        for (int col = 0; col < 8; col++) {
            char vertical = whiteView
                    ? (char) ('a' + col)
                    : (char) ('h' - col);

            System.out.print(" " + vertical + " ");
        }

        System.out.println();
    }
}
