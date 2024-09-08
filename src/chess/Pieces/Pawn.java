package chess.Pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

    private ChessMatch chessMatch;

    public Pawn(Board board, Color color, ChessMatch ChessMatch) {
        super(board, color);
        this.chessMatch = ChessMatch;
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] matrix = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position pos = new Position(0, 0);
        if (getColor() == Color.WHITE) {
            pos.setValues(position.getRow() - 1, position.getColumn());
            if (getBoard().positionExists(pos) && !getBoard().thereIsAPiece(pos)) {
                matrix[pos.getRow()][pos.getColumn()] = true;
            }
            pos.setValues(position.getRow() - 2, position.getColumn());
            Position position2 = new Position(position.getRow() - 1, position.getColumn());
            if (getBoard().positionExists(pos) && !getBoard().thereIsAPiece(pos) && getBoard().positionExists(position2)
                    && !getBoard().thereIsAPiece(position2) && getMoveCount() == 0) {
                matrix[pos.getRow()][pos.getColumn()] = true;
            }
            pos.setValues(position.getRow() - 1, position.getColumn() - 1);
            if (getBoard().positionExists(pos) && isThereOpponentPiece(pos)) {
                matrix[pos.getRow()][pos.getColumn()] = true;
            }
            pos.setValues(position.getRow() - 1, position.getColumn() + 1);
            if (getBoard().positionExists(pos) && isThereOpponentPiece(pos)) {
                matrix[pos.getRow()][pos.getColumn()] = true;
            }
            // #Special move En Passant white
            if (position.getRow() == 3) {
                Position positionEnPassantLeft = new Position(position.getRow(), position.getColumn() - 1);
                if (getBoard().positionExists(positionEnPassantLeft) && isThereOpponentPiece(positionEnPassantLeft)
                        && getBoard().piece(positionEnPassantLeft) == chessMatch.getEnPassantVulnerable()) {
                    matrix[positionEnPassantLeft.getRow() - 1][positionEnPassantLeft.getColumn()] = true;
                }
                Position positionEnPassantRight = new Position(position.getRow(), position.getColumn() + 1);
                if (getBoard().positionExists(positionEnPassantRight) && isThereOpponentPiece(positionEnPassantRight)
                        && getBoard().piece(positionEnPassantRight) == chessMatch.getEnPassantVulnerable()) {
                    matrix[positionEnPassantRight.getRow()-1][positionEnPassantRight.getColumn()] = true;
                }
            }
        } else {
            pos.setValues(position.getRow() + 1, position.getColumn());
            if (getBoard().positionExists(pos) && !getBoard().thereIsAPiece(pos)) {
                matrix[pos.getRow()][pos.getColumn()] = true;
            }
            pos.setValues(position.getRow() + 2, position.getColumn());
            Position position2 = new Position(position.getRow() + 1, position.getColumn());
            if (getBoard().positionExists(pos) && !getBoard().thereIsAPiece(pos) && getBoard().positionExists(position2)
                    && !getBoard().thereIsAPiece(position2) && getMoveCount() == 0) {
                matrix[pos.getRow()][pos.getColumn()] = true;
            }
            pos.setValues(position.getRow() + 1, position.getColumn() - 1);
            if (getBoard().positionExists(pos) && isThereOpponentPiece(pos)) {
                matrix[pos.getRow()][pos.getColumn()] = true;
            }
            pos.setValues(position.getRow() + 1, position.getColumn() + 1);
            if (getBoard().positionExists(pos) && isThereOpponentPiece(pos)) {
                matrix[pos.getRow()][pos.getColumn()] = true;
            }
            // #Special move En Passant black
            if (position.getRow() == 4) {
                Position positionEnPassantLeft = new Position(position.getRow(), position.getColumn() - 1);
                if (getBoard().positionExists(positionEnPassantLeft) && isThereOpponentPiece(positionEnPassantLeft)
                        && getBoard().piece(positionEnPassantLeft) == chessMatch.getEnPassantVulnerable()) {
                    matrix[positionEnPassantLeft.getRow() + 1][positionEnPassantLeft.getColumn()] = true;
                }
                Position positionEnPassantRight = new Position(position.getRow(), position.getColumn() + 1);
                if (getBoard().positionExists(positionEnPassantRight) && isThereOpponentPiece(positionEnPassantRight)
                        && getBoard().piece(positionEnPassantRight) == chessMatch.getEnPassantVulnerable()) {
                    matrix[positionEnPassantRight.getRow()+1][positionEnPassantRight.getColumn()] = true;
                }
            }
        }
        return matrix;
    }

    public ChessMatch getChessMatch() {
        return chessMatch;
    }

    @Override
    public String toString() {
        return "P";
    }

}
