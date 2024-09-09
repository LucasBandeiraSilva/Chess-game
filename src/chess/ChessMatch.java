package chess;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.Pieces.Bishop;
import chess.Pieces.King;
import chess.Pieces.Knight;
import chess.Pieces.Pawn;
import chess.Pieces.Queen;
import chess.Pieces.Rook;

public class ChessMatch {
    private Board board;
    private int turn;
    private Color currentPlayer;
    private boolean check;
    private boolean checkmate;
    private ChessPiece enPassantVulnerable;
    private ChessPiece prometed;
    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public ChessMatch() {
        board = new Board(8, 8);
        turn = 1;
        currentPlayer = Color.WHITE;
        initialSetup();
    }

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isCheck() {
        return check;
    }

    public boolean isCheckmate() {
        return checkmate;
    }

    public ChessPiece getEnPassantVulnerable() {
        return enPassantVulnerable;
    }

    public ChessPiece getPrometed() {
        return prometed;
    }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    public ChessPiece perfomeChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece caputeredPiece = makeMove(source, target);
        if (testCheck(currentPlayer)) {
            undoMove(source, target, caputeredPiece);
            throw new ChessException("You can't put yourself in check!");
        }
        ChessPiece movedPiece = (ChessPiece) board.piece(target);

        // #Special Move - Promotion
        prometed = null;
        if (movedPiece instanceof Pawn) {
            if ((movedPiece.getColor() == Color.WHITE && target.getRow() == 0)
                    || (movedPiece.getColor() == Color.BLACK && target.getRow() == 7)) {
                prometed = (ChessPiece) board.piece(target);
                prometed = replacePrometedPiece("Q");

            }
        }
        check = (testCheck(opponent(currentPlayer))) ? true : false;
        checkmate = (testCheckMate(opponent(currentPlayer))) ? true : false;
        if (testCheckMate(opponent(currentPlayer))) {
            checkmate = true;
        } else
            nextTurn();

        // #Special move En Passant
        if (movedPiece instanceof Pawn && target.getRow() == source.getRow() - 2
                || target.getRow() == source.getRow() + 2)
            enPassantVulnerable = movedPiece;
        else
            enPassantVulnerable = null;
        return (ChessPiece) caputeredPiece;
    }

    public ChessPiece replacePrometedPiece(String pieceType) {
        if (prometed == null) {
            throw new IllegalStateException("There is no piece to be promoted");
        }
        if (!pieceType.equals("B") && !pieceType.equals("N") && !pieceType.equals("R") && !pieceType.equals("Q")) {
            throw new InvalidParameterException("Invalid type for promotion");
        }
        Position position = prometed.getChessPosition().toPosition();
        Piece piece = board.removePiece(position);
        piecesOnTheBoard.remove(piece);
        ChessPiece newPiece = newPiece(pieceType, prometed.getColor());
        board.placePiece(newPiece, position);
        piecesOnTheBoard.add(newPiece);
        return newPiece;
    }

    private ChessPiece newPiece(String pieceType, Color color) {
        switch (pieceType) {
            case "B":
                return new Bishop(board, color);
            case "N":
                return new Knight(board, color);
            case "Q":
                return new Queen(board, color);
            default:
                return new Rook(board, color);

        }
    }

    private Piece makeMove(Position sourcePosition, Position targetPosition) {
        ChessPiece piece = (ChessPiece) board.removePiece(sourcePosition);
        piece.increaseMoveCount();
        Piece capturedPiece = board.removePiece(targetPosition);
        board.placePiece(piece, targetPosition);
        if (capturedPiece != null) {
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }
        // special move castling kingside rook
        if (piece instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() + 2) {
            Position sourcePositionRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 3);
            Position targetPositionRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 1);
            ChessPiece rook = (ChessPiece) board.removePiece(sourcePositionRook);
            board.placePiece(rook, targetPositionRook);
            rook.increaseMoveCount();
        }
        // special move castling queenside rook
        if (piece instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() - 2) {
            Position sourcePositionRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 4);
            Position targetPositionRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 1);
            ChessPiece rook = (ChessPiece) board.removePiece(sourcePositionRook);
            board.placePiece(rook, targetPositionRook);
            rook.increaseMoveCount();
        }
        // #Special move En Passant
        if (piece instanceof Pawn) {
            if (sourcePosition.getRow() != targetPosition.getColumn() && capturedPiece == null) {
                Position pawnPosition;
                if (piece.getColor() == Color.WHITE)
                    pawnPosition = new Position(targetPosition.getRow() + 1, targetPosition.getColumn());
                else
                    pawnPosition = new Position(targetPosition.getRow() - 1, targetPosition.getColumn());
                capturedPiece = board.removePiece(pawnPosition);
                capturedPieces.add(capturedPiece);
                piecesOnTheBoard.remove(capturedPiece);
            }
        }

        return capturedPiece;
    }

    private void undoMove(Position sourcePosition, Position targetPosition, Piece capturedPiece) {
        ChessPiece piece = (ChessPiece) board.removePiece(targetPosition);
        piece.decreaseMoveCount();
        board.placePiece(piece, sourcePosition);
        if (capturedPiece != null) {
            board.placePiece(capturedPiece, targetPosition);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }
        // special move castling kingside rook
        if (piece instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() + 2) {
            Position sourcePositionRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 3);
            Position targetPositionRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 1);
            ChessPiece rook = (ChessPiece) board.removePiece(targetPositionRook);
            board.placePiece(rook, sourcePositionRook);
            rook.decreaseMoveCount();
        }
        // special move castling queenside rook
        if (piece instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() - 2) {
            Position sourcePositionRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 4);
            Position targetPositionRook = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 1);
            ChessPiece rook = (ChessPiece) board.removePiece(targetPositionRook);
            board.placePiece(rook, sourcePositionRook);
            rook.increaseMoveCount();
        }
        // #Special move En Passant
        if (piece instanceof Pawn) {
            if (sourcePosition.getRow() != targetPosition.getColumn() && capturedPiece == enPassantVulnerable) {
                ChessPiece pawn = (ChessPiece) board.removePiece(targetPosition);
                Position pawnPosition;
                if (piece.getColor() == Color.WHITE)
                    pawnPosition = new Position(3, targetPosition.getColumn());
                else
                    pawnPosition = new Position(4, targetPosition.getColumn());
                board.placePiece(pawn, pawnPosition);
            }
        }

    }

    private void validateSourcePosition(Position position) {
        if (!board.thereIsAPiece(position)) {
            throw new ChessException("There is no piece in the source position");
        }
        if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
            throw new ChessException("The chosen piece is not yours");
        }
        if (!board.piece(position).isThereAnyPossibleMove()) {
            throw new ChessException("There is no possible move for the piece in the source position");
        }
    }

    public void validateTargetPosition(Position sourcePosition, Position targetPosition) {
        if (!board.piece(sourcePosition).possibleMove(targetPosition)) {
            throw new ChessException("The chosen piece can not move to target postion");
        }
    }

    private void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private Color opponent(Color color) {
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king(Color color) {
        List<Piece> pieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
                .collect(Collectors.toList());
        for (Piece piece : pieces) {
            if (piece instanceof King) {
                return (ChessPiece) piece;
            }
        }
        throw new IllegalStateException("There is no " + color + " King on the board");
    }

    private boolean testCheck(Color color) {
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = piecesOnTheBoard.stream()
                .filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());
        for (Piece piece : opponentPieces) {
            boolean[][] matrix = piece.possibleMoves();
            if (matrix[kingPosition.getRow()][kingPosition.getColumn()]) {
                return true;
            }
        }
        return false;
    }

    private boolean testCheckMate(Color color) {
        if (!testCheck(color)) {
            return false;
        }
        List<Piece> oneColorPiecesList = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
                .collect(Collectors.toList());
        for (Piece piece : oneColorPiecesList) {
            boolean[][] moves = piece.possibleMoves();
            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getColumns(); j++) {
                    if (moves[i][j]) {
                        Position sourcePosition = ((ChessPiece) piece).getChessPosition().toPosition();
                        Position targetPosition = new Position(i, j);
                        Piece capturedPiece = makeMove(sourcePosition, targetPosition);
                        boolean testCheck = testCheck(color);
                        undoMove(sourcePosition, targetPosition, capturedPiece);
                        if (!testCheck) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }

    private void initialSetup() {
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
    }

}
