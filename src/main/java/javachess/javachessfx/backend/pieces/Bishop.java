package javachess.javachessfx.backend.pieces;

import javachess.javachessfx.backend.Chessboard;

public class Bishop extends Piece{
    public Bishop(char color){
        super(color);
        this.letter='b';
        this.pathToIcon="icons/"+color+"_"+letter+".png";
        this.worth = 3;
    }

    public Bishop(Piece piece){
        super(piece);
    }
    public void changeState(){}

    public boolean[][] getPossibleMoves(Chessboard chessboard, int x, int y){
        boolean[][] chessboardOfPossibleMoves = Chessboard.getChessboardOfFalse();

        int[] vecX = new int[]{-1,  1,  1, -1};
        int[] vecY = new int[]{-1, -1,  1,  1};
        for (int i = 0; i<vecX.length; i++)
            chessboardOfPossibleMoves=getChessBoardWithLineOfPossibleMoves(chessboard, chessboardOfPossibleMoves, x, y, vecX[i], vecY[i]);


        return chessboardOfPossibleMoves;
    }

}