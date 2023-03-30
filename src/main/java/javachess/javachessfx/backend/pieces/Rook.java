package javachess.javachessfx.backend.pieces;

import javachess.javachessfx.backend.Chessboard;

public class Rook extends Piece{

    public Rook(char color){
        super(color);
        this.letter='r';
        this.isSpecialMoveAvailable =true;
        this.pathToIcon="icons/"+color+"_"+letter+".png";
        this.worth = 5;
    }

    public Rook(Piece piece){
        super(piece);
    }

    public Rook(char color, boolean isCastlingAvailable){
        super(color);
        this.isSpecialMoveAvailable = isCastlingAvailable;
        this.letter='r';
        this.pathToIcon="icons/"+color+"_"+letter+".png";
    }

    public void changeState(){
        isSpecialMoveAvailable = false;
    }


    public boolean isSpecialMoveAvailable() {
        return isSpecialMoveAvailable;
    }

    public boolean[][] getPossibleMoves(Chessboard chessboard, int x, int y){
        boolean[][] chessboardOfPossibleMoves = Chessboard.getChessboardOfFalse();

        int[] vecX = new int[]{ 0, 1, 0, -1};
        int[] vecY = new int[]{-1, 0, 1,  0};
        for (int i = 0; i<vecX.length; i++)
            chessboardOfPossibleMoves=getChessBoardWithLineOfPossibleMoves(chessboard, chessboardOfPossibleMoves, x, y, vecX[i], vecY[i]);


        return chessboardOfPossibleMoves;
    }

}