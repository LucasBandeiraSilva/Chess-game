package application;

import java.util.Scanner;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ChessMatch chessMatch = new ChessMatch();
        while (true) {

            UI.printBoard(chessMatch.getPieces());
            System.out.println();
            System.out.print("Source: ");
            ChessPosition source = UI.readChessPosition(scanner);
            System.out.println();
            System.out.print("Target: ");
            ChessPosition target = UI.readChessPosition(scanner);
            ChessPiece capturedPiece = chessMatch.perfomeChessMove(source, target);

        }

    }
}
