package javachess.javachessfx.backend.pieces;

import javachess.javachessfx.backend.Chessboard;

public class Knight extends Piece{
    public Knight(char color){
        super(color);
        this.letter='n';
        this.pathToIcon="icons/"+color+"_"+letter+".png";
        this.worth = 3;
    }

    public Knight (Piece piece){
        super(piece);
    }

    public void changeState(){}

    public boolean[][] getPossibleMoves(Chessboard chessboard, int x, int y){
        boolean[][] chessBoardOfPossibleMoves = Chessboard.getChessboardOfFalse();
        Piece[][] board = chessboard.getBoard();

        if( chessboard.getCurrentMove() != color )
            return chessBoardOfPossibleMoves;

        int[] posX=new int[]{-2, -1, 1, 2, 2, 1, -1, -2};
        int[] posY=new int[]{ 1,  2, 2, 1,-1,-2, -2, -1};
        for(int i=0; i<posX.length; i++){
            if ( Chessboard.areCoordinatesInChessboard(x+posX[i],y+posY[i]) &&
                            (( chessboard.isTherePieceAt( x+posX[i],y+posY[i]) && !isPieceAnAlly( board[x+posX[i]][y+posY[i]] )) ||
                            !chessboard.isTherePieceAt( x+posX[i],y+posY[i])) &&
                            Chessboard.getChessboardWithExecutedMoveWithoutCheckingIfMoveIsLegal(chessboard, x, y, x+posX[i], y+posY[i]).checkIfKingIsSafe(color) )
                chessBoardOfPossibleMoves[x + posX[i]][y + posY[i]] = true;
        }

        return chessBoardOfPossibleMoves;
    }

}