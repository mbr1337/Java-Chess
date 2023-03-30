package javachess.javachessfx.backend.pieces;

import javachess.javachessfx.backend.Chessboard;

public class Queen extends Piece{
    public Queen(char color){
        super(color);
        this.letter='q';
        this.pathToIcon="icons/"+color+"_"+letter+".png";
        this.worth = 9;
    }

    public Queen(Piece piece){
        super(piece);
    }

    public void changeState(){}

    public boolean[][] getPossibleMoves(Chessboard chessboard, int x, int y) {
        boolean[][] chessboardOfPossibleMoves = Chessboard.getChessboardOfFalse();


        int[] vecX = new int[]{-1, 0, 1, 1, 1, 0, -1, -1};
        int[] vecY = new int[]{-1,-1,-1, 0, 1, 1,  1,  0};
        for (int i = 0; i<vecX.length; i++)
            chessboardOfPossibleMoves=getChessBoardWithLineOfPossibleMoves(chessboard, chessboardOfPossibleMoves, x, y, vecX[i], vecY[i]);


        return chessboardOfPossibleMoves;
    }
}