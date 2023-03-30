package javachess.javachessfx.backend.pieces;

import javachess.javachessfx.backend.Chessboard;

public class Pawn extends Piece{

    public Pawn(char color){
        super(color);
        this.isSpecialMoveAvailable=true;
        this.letter='p';
        this.pathToIcon="icons/"+color+"_"+letter+".png";
        this.worth = 1;
    }

    public Pawn(Piece piece){
        super(piece);
    }

    public Pawn(char color, boolean isSpecialMoveAvailable){
        super(color);
        this.isSpecialMoveAvailable=isSpecialMoveAvailable;
        this.letter='p';
        this.pathToIcon="icons/"+color+"_"+letter+".png";
    }

    public void changeState(){
        isSpecialMoveAvailable = false;
    }


    public boolean[][] getPossibleMoves(Chessboard chessboard, int x, int y){
        boolean[][] chessBoardOfPossibleMoves = Chessboard.getChessboardOfFalse();
        Piece[][] board = chessboard.getBoard();

        if( chessboard.getCurrentMove() != color )
            return chessBoardOfPossibleMoves;


        int[] posX=new int[]{-1, 0 ,1};
        int direction = color == 'w' ? 1 : -1; //pawn can ony move on one direction - up or down, in dependence of color

        for(int i=0; i<posX.length; i++){//single tile move
            if(Chessboard.areCoordinatesInChessboard(x + posX[i], y+direction)){
                if(posX[i] == 0 && !chessboard.isTherePieceAt( x + posX[i], y+direction)&&
                        Chessboard.getChessboardWithExecutedMoveWithoutCheckingIfMoveIsLegal(chessboard, x, y, x+posX[i], y+direction).checkIfKingIsSafe(color) ){ // forward move
                    chessBoardOfPossibleMoves[x+posX[i]][y+direction]=true;
                } else if (posX[i] != 0 && chessboard.isTherePieceAt( x + posX[i], y+direction) && board[x+posX[i]][y+direction].getColor() != this.color &&
                        Chessboard.getChessboardWithExecutedMoveWithoutCheckingIfMoveIsLegal(chessboard, x, y, x+posX[i], y+direction).checkIfKingIsSafe(color) ){
                    chessBoardOfPossibleMoves[x+posX[i]][y+direction]=true; //diagonal move - capturing a Piece
                }
            }
        }
        //double move
        if(isSpecialMoveAvailable && !chessboard.isTherePieceAt( x , y+direction) && !chessboard.isTherePieceAt( x , y+direction*2) &&
                Chessboard.getChessboardWithExecutedMoveWithoutCheckingIfMoveIsLegal(chessboard, x, y, x, y+direction*2).checkIfKingIsSafe(color)){
            chessBoardOfPossibleMoves[x][y+direction*2]=true;
        }


        return chessBoardOfPossibleMoves;
    }


}
