package javachess.javachessfx.backend.pieces;

import javachess.javachessfx.backend.Chessboard;

public class King extends Piece{

    public King(char color){
        super(color);
        this.isSpecialMoveAvailable =true;
        this.letter='k';
        this.pathToIcon="icons/"+color+"_"+letter+".png";
    }

    public King(Piece piece){
        super(piece);
    }

    public King(char color, boolean isCastlingAvailable){
        super(color);
        this.isSpecialMoveAvailable =isCastlingAvailable;
        this.letter='k';
        this.pathToIcon="icons/"+color+"_"+letter+".png";
        this.worth = 10;
    }


    public boolean isSpecialMoveAvailable() {
        return isSpecialMoveAvailable;
    }

    public void changeState(){
        isSpecialMoveAvailable = false;
    }

    public boolean[][] getPossibleMoves(Chessboard chessboard, int x, int y){
        boolean[][] chessBoardOfPossibleMoves = Chessboard.getChessboardOfFalse();
        Piece[][] board = chessboard.getBoard();

        if( chessboard.getCurrentMove() != color )
            return chessBoardOfPossibleMoves;

        int[] posX=new int[]{-1, 0, 1, 1, 1, 0, -1, -1};
        int[] posY=new int[]{ 1, 1, 1, 0,-1,-1, -1,  0};
        for(int i=0; i<posX.length; i++){
            if ( Chessboard.areCoordinatesInChessboard(x+posX[i],y+posY[i])
                    && (!chessboard.isTherePieceAt( x + posX[i], y + posY[i]) || !isPieceAnAlly(board[x + posX[i]][y + posY[i]]))
                    && Chessboard.getChessboardWithExecutedMoveWithoutCheckingIfMoveIsLegal(chessboard, x, y, x+posX[i], y+posY[i]).checkIfKingIsSafe(color) )
                chessBoardOfPossibleMoves[x + posX[i]][y + posY[i]] = true;
        }


        //  CASTLING

        if(isSpecialMoveAvailable()) {
            int row = color == 'w' ? 0 : 7;

            int[] leftSide = new int[]{3, 2, 1};
            int[] rightSide = new int[]{5, 6};

            for (int i = 0; i < leftSide.length; i++) {
                if (chessboard.isTherePieceAt(leftSide[i], row)) {
                    break;
                } else if (i == leftSide.length - 1 && chessboard.isTherePieceAt(0, row) && chessboard.getPieceAt(0, row).getLetter() == 'r') {
                    chessBoardOfPossibleMoves[leftSide[i-1]][row] = true;
                }
            }

            for (int i = 0; i < rightSide.length; i++) {
                if (chessboard.isTherePieceAt(rightSide[i], row)) {
                    break;
                } else if (i == rightSide.length - 1 && chessboard.isTherePieceAt(7, row) && chessboard.getPieceAt(7, row).getLetter() == 'r') {
                    chessBoardOfPossibleMoves[rightSide[i]][row] = true;
                }
            }
        }
        return chessBoardOfPossibleMoves;
    }



}
