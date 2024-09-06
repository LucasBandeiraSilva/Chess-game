package chess.Pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

    public Pawn(Board board, Color color) {
        super(board, color);
        // TODO Auto-generated constructor stub
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
            pos.setValues(position.getRow() - 1, position.getColumn()-1);
            if (getBoard().positionExists(pos) && isThereOpponentPiece(pos)) {
                matrix[pos.getRow()][pos.getColumn()] = true;
            }
            pos.setValues(position.getRow() - 1, position.getColumn()+1);
            if (getBoard().positionExists(pos) && isThereOpponentPiece(pos)) {
                matrix[pos.getRow()][pos.getColumn()] = true;
            }
        }else{
            pos.setValues(position.getRow() + 1, position.getColumn());
            if (getBoard().positionExists(pos) && !getBoard().thereIsAPiece(pos)) {
                matrix[pos.getRow()][pos.getColumn()] = true;
            }
            pos.setValues(position.getRow() + 2, position.getColumn());
            Position position2 = new Position(position.getRow() - 1, position.getColumn());
            if (getBoard().positionExists(pos) && !getBoard().thereIsAPiece(pos) && getBoard().positionExists(position2)
                    && !getBoard().thereIsAPiece(position2) && getMoveCount() == 0) {
                matrix[pos.getRow()][pos.getColumn()] = true;
            }
            pos.setValues(position.getRow() + 1, position.getColumn()-1);
            if (getBoard().positionExists(pos) && isThereOpponentPiece(pos)) {
                matrix[pos.getRow()][pos.getColumn()] = true;
            }
            pos.setValues(position.getRow() + 1, position.getColumn()+1);
            if (getBoard().positionExists(pos) && isThereOpponentPiece(pos)) {
                matrix[pos.getRow()][pos.getColumn()] = true;
            }
        }
        return matrix;
    }

    @Override
    public String toString() {
        return "P";
    }
    

}
