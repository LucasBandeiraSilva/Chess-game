package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ChessMatch chessMatch = new ChessMatch();
        List<ChessPiece> capturedChessPieces = new ArrayList<>();
        while (!chessMatch.isCheckmate()) {
            try {
                UI.clearScreen();
                UI.printMatch(chessMatch,capturedChessPieces);
                System.out.println();
                System.out.print("Source: ");
                ChessPosition source = UI.readChessPosition(scanner);
                boolean[][] possibleMoves = chessMatch.possibleMoves(source);
                UI.clearScreen();
                UI.printBoard(chessMatch.getPieces(),possibleMoves);
                System.out.println();
                System.out.print("Target: ");
                ChessPosition target = UI.readChessPosition(scanner);
                ChessPiece capturedPiece = chessMatch.perfomeChessMove(source, target);
                if (capturedPiece != null) {
                    capturedChessPieces.add(capturedPiece);
                }
            } catch (ChessException exception) {
                System.out.println(exception.getMessage());
                scanner.nextLine();
            } catch (InputMismatchException exception) {
                System.out.println(exception.getMessage());
                scanner.nextLine();

            }

        }
        UI.clearScreen();
        UI.printMatch(chessMatch,capturedChessPieces);

    }
}
