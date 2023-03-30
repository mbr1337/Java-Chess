package javachess.javachessfx.backend.pieces;

import javachess.javachessfx.backend.Chessboard;

public abstract class Piece implements Comparable<Piece> {
    protected char color;//w=white b=black
    protected char letter;//k=king, q=queen, r=rook, b=bishop, n=kNight, p=pawn
    protected String pathToIcon;
    protected boolean isSpecialMoveAvailable; //is castling available (King and Rook), is double move available (Pawn)
    protected int worth;

    Piece(char color){
        this.color=color;
        this.isSpecialMoveAvailable = false;
    }

    Piece(Piece piece){
        this.color = piece.getColor();
        this.letter = piece.getLetter();
        this.pathToIcon = piece.getPathToIcon();
        this.isSpecialMoveAvailable = piece.isSpecialMoveAvailable();
        this.worth =piece.worth;
    }

    public Piece getPiece() { return this; }

    public  char getLetterOfPiece(){ return letter; }

    public char getColor() {
        return color;
    }

    public char getLetter() {
        return letter;
    }

    public String getPathToIcon() { return pathToIcon; }

    public int getWorth() { return worth; }

    public boolean isSpecialMoveAvailable() { return isSpecialMoveAvailable; }

    public boolean isPieceAnAlly(Piece piece){ return piece.color == this.color; }

    /**
     * used only in King, Rook and Piece classes
     * switches isSpecialMove boolean to false after first move to turn off Castling (King and Rook) or double move (Pawn)
     */
    public abstract void changeState();

    /**
     * Calculates moves available to Piece in given position on given chessboard.
     * Every Piece moves differently, so every chess piece that extends Piece class implements this method in different way
     * @param chessboard with current game
     * @param x position
     * @param y position
     * @return boolean 8x8 array with true (if Piece can move to that square) or false (if Piece can't move to that square)
     */
    public abstract boolean[][] getPossibleMoves(Chessboard chessboard, int x, int y);



    /**
     * Adds line of possible moves to given inputChessBoardOfPossibleMoves.
     * Used to define moves of rook, bishop and queen.
     * Line can be either diagonal or straight, there are 8 possible directions.
     * @param chessboard with current game
     * @param inputChessBoardOfPossibleMoves previously calculated possible moves
     * @param posX x position of Piece
     * @param posY y position of Piece
     * @param vecX direction, can be only 3 states: -1:left, 0:none, 1:right
     * @param vecY direction, can be only 3 states: -1:down, 0:none, 1:up
     * @return returns given inputChessBoardOfPossibleMoves + new possible line of moves
     */
    public boolean[][] getChessBoardWithLineOfPossibleMoves(Chessboard chessboard, boolean[][] inputChessBoardOfPossibleMoves, int posX, int posY, int vecX, int vecY){

        if( chessboard.getCurrentMove() != color )
            return inputChessBoardOfPossibleMoves;

        Piece[][] board = chessboard.getBoard();

        for(int x = posX+vecX, y = posY+vecY; Chessboard.areCoordinatesInChessboard(x,y); x+=vecX, y+=vecY){
            if (chessboard.isTherePieceAt(x, y)) {
                if (isPieceAnAlly(board[x][y])) {
                    break;
                } else if (Chessboard.getChessboardWithExecutedMoveWithoutCheckingIfMoveIsLegal(chessboard, posX, posY, x, y).checkIfKingIsSafe(this.getColor())) {
                    inputChessBoardOfPossibleMoves[x][y] = true;
                    break;
                } else {
                    break;
                }
            } else if (Chessboard.getChessboardWithExecutedMoveWithoutCheckingIfMoveIsLegal(chessboard, posX, posY, x, y).checkIfKingIsSafe(this.getColor())) {
                inputChessBoardOfPossibleMoves[x][y] = true;
            }
        }

        return inputChessBoardOfPossibleMoves;
    }

    public Piece getPieceAtTheEndOfLine(Chessboard chessboard, int posX, int posY, int vecX, int vecY){
        for(int x = posX, y = posY; Chessboard.areCoordinatesInChessboard(x,y); x+=vecX, y+=vecY){
            if(chessboard.isTherePieceAt(x, y))
                return chessboard.getBoard()[x][y];
        }
        return null;
    }

    @Override
    public int compareTo(Piece piece) {
        if( piece.getLetter() == 'n' && getLetter() == 'b' )
            return 1;
        else if ( piece.getLetter() == 'b' && getLetter() == 'n' )
            return -1;
        else
            return getWorth() - piece.getWorth();
    }
}
