package javachess.javachessfx.backend;

import javachess.javachessfx.backend.pieces.*;
import javachess.javachessfx.exceptions.EmptySquareException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**TODO:
 * większa szachownica (ważne)
 * kolor szachownicy - ma być np. biało niebieska, może też być tekstura z dodanymi numerami pól (ważne) [ https://github.com/lichess-org/lila/blob/master/public/images/board/maple2.jpg ]
 * białe powinny być na dole (ważne)
 *
 * zegar szachowy (średnio ważne) (np. gracze mają po 10 minut, jeśli gracz myślał nad swoim ruchem 15 sekund to od 10 minut jest odejmowane 15 sekund)
 *
 * numery pól, chyba że będą na teksturze (mało ważne)
 * awans pionka - wybieranie na jaką figurę (mało ważne)
 * anti aliasing na figurach (mało ważne bo pewnie trudne do zrobienia)
 *
 * przycisk importuj grę który otwiera pole tekstowe do którego wkleja się stringa z zapisem partii a następnie można grać od tego momentu
 * przycisk eksportuj grę który pozwala skopiować zapis aktualnej gry
 *
 */


public class Chessboard {
    public static final int boardSize=8;
    private Piece[][] board = new Piece[boardSize][boardSize];//chessBoard[x][y]
    public char currentMove;//which player has a move: 'w'=white, 'b'=black
    private String algebraicNotation;
    public int turnIndex;//one turn is white AND black move, then new turn begins

    public List<Piece> whiteDeadPieces = new ArrayList<>();
    public List<Piece> blackDeadPieces = new ArrayList<>();

    /**
     * constructor that creates regular chessboard with proper setting of Pieces
     */
    public Chessboard(){
        initialSetup();
    }


    /**
     * Creates copy of given chessboard
     * @param chessboard that we want copy of
     */
    public Chessboard(Chessboard chessboard){
        this.currentMove = chessboard.currentMove;
        this.algebraicNotation = chessboard.algebraicNotation;
        this.turnIndex = chessboard.turnIndex;

        for (int y=0; y<boardSize; y++){
            for (int x=0; x<boardSize; x++){
                if (chessboard.getBoard()[x][y] == null)
                    this.board[x][y] = null;
                else {
                    switch (chessboard.getBoard()[x][y].getLetter()){
                        case 'k' -> this.board[x][y] = new King(chessboard.getPieceAt(x,y));
                        case 'q' -> this.board[x][y] = new Queen(chessboard.getPieceAt(x,y));
                        case 'r' -> this.board[x][y] = new Rook(chessboard.getPieceAt(x,y));
                        case 'b' -> this.board[x][y] = new Bishop(chessboard.getPieceAt(x,y));
                        case 'n' -> this.board[x][y] = new Knight(chessboard.getPieceAt(x,y));
                        case 'p' -> this.board[x][y] = new Pawn(chessboard.getPieceAt(x,y));
                    }
                }

            }
        }
    }


    private void initialSetup(){
        currentMove='w';//white player begins
        algebraicNotation="";
        turnIndex=1;

        for(int y=0; y<boardSize; y++){
            for(int x=0; x<boardSize; x++){
                board[x][y]=null;
            }
        }

        //white pieces
        for(int i=0; i<boardSize; i++){

            board[i][1]=new Pawn('w');
        }

        board[0][0]=new Rook('w');
        board[1][0]=new Knight('w');
        board[2][0]=new Bishop('w');
        board[3][0]=new Queen('w');

        board[4][0]=new King('w');

        board[5][0]=new Bishop('w');
        board[6][0]=new Knight('w');
        board[7][0]=new Rook('w');


        //black pieces
        for(int i=0; i<boardSize; i++){
            board[i][6]=new Pawn('b');
        }

        board[0][7]=new Rook('b');
        board[1][7]=new Knight('b');
        board[2][7]=new Bishop('b');
        board[3][7]=new Queen('b');

        board[4][7]=new King('b');


        board[5][7]=new Bishop('b');
        board[6][7]=new Knight('b');
        board[7][7]=new Rook('b');

        //experimental pieces
//        board[3][4]=new Queen('w');
//        board[4][3]=new Knight('b');
////        board[2][5]=new Bishop('w');
//        board[5][2]=new Rook('b');
//        board[2][2]=new King('w');
    }

    public Piece[][] getBoard() { return board; }

    public List<Piece> getWhiteDeadPieces() { return whiteDeadPieces; }
    public List<Piece> getBlackDeadPieces() { return blackDeadPieces; }

    public char getCurrentMove(){ return currentMove; }

    public Piece getPieceAt(int x, int y){ return board[x][y]; }

    public boolean isTherePieceAt(int x, int y){ return board[x][y] != null; }

    public void changeCurrentMove(){
        currentMove = currentMove == 'w' ? 'b' : 'w';
    }

    public char getLetterOfPieceAt(int x, int y){
        return isTherePieceAt(x,y) ? board[x][y].getLetterOfPiece() : ' ';
    }

    /**
     * Calculates value of Pieces that was taken/beaten (list white/blackDeadPieces)
     * @param color of pieces, can be either 'w' or 'b'
     * @return value of Pieces that was taken/beaten
     */
    public int getWorthOfDeadPieces(char color){
        List<Piece> tempList = color == 'w' ? getWhiteDeadPieces() : getBlackDeadPieces();
        int result=0;
        for (int i = 0; i < tempList.size(); i++)
            result += tempList.get(i).getWorth();
        return result;
    }


    /**
     * Checks if given coordinates aren't out of chessboard's borders
     * @param x
     * @param y
     * @return
     */
    public static boolean areCoordinatesInChessboard(int x, int y){
        if(x > boardSize-1 || y > boardSize-1 || x<0 || y<0)
            return false;
        else
            return true;
    }

    /**
     * Calculating legal moves of Piece in given coordinates in Piece[][] chessBoard
     * @param x x position of Piece
     * @param y y position of Piece
     * @return boolean[][] array, where legal moves are true and illegal are false
     * @throws EmptySquareException
     * todo:
     * checking player's turn
     * checking if king isn't in danger
     */
    public boolean[][] getChessboardOfPossibleMovesFromSquareAt(int x, int y) throws EmptySquareException{
        if(board[x][y]!=null){
            return board[x][y].getPossibleMoves(this, x, y);
        } else
            throw new EmptySquareException("Square you choose is empty");
    }

    /**
     * Executes move if it is legal
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @return true if move was actually executed, false if move was illegal to execute
     */
    public boolean executeMove(int fromX, int fromY, int toX, int toY){
        if(getChessboardOfPossibleMovesFromSquareAt(fromX, fromY)[toX][toY]){
            Piece temp= getPieceAt(fromX, fromY);

            // CASTLING - moving only rook, king will be moved like any normal move under this if statement
            if(temp.getLetter() == 'k'){
                int row = temp.getColor() == 'w' ? 0 : 7;
                if (temp.isSpecialMoveAvailable()){
                    if(toX == 2){
                        Piece temp2= getPieceAt(0, row);
                        board[0][row]=null;
                        board[3][row]=temp2;
                        temp2.changeState();
                    } else if (toX == 6){
                        Piece temp2= getPieceAt(7, row);
                        board[7][row]=null;
                        board[5][row]=temp2;
                        temp2.changeState();
                    }
                }
            }
            //adding taken Piece to right deadPieces list
            if(isTherePieceAt(toX, toY)) {
                if (getPieceAt(toX, toY).getColor() == 'w'){
                    whiteDeadPieces.add(getPieceAt(toX, toY));
                    Collections.sort(whiteDeadPieces, Collections.reverseOrder());
                } else {
                    blackDeadPieces.add(getPieceAt(toX, toY));
                    Collections.sort(blackDeadPieces, Collections.reverseOrder());
                }
            }
            // NORMAL MOVE

            board[fromX][fromY]=null;
            board[toX][toY]=temp;

            temp.changeState();

            // PROMOTION
            if(temp.getLetter() == 'p'){
                int promotionRow = temp.getColor() == 'w' ? 7 : 0;
                if(toY == promotionRow){
                    //TODO
                    // dodać tutaj faktyczne wybieranie na jaką figurę gracz chce awansować piona, czyli musi być połączenie z front-endem
                    // w linijce poniżej trzeba zmienić trzeci argument na jeden z czterech wybranych przez użytkownika: q, r, k lub b
                    promotion(toX, temp, 'q');
                }
            }

            addMoveToNotation(fromX, fromY, toX, toY);
/*            System.out.print(algebraicNotation+"\n\n\n\n");
            System.out.print("\n\n\n");
            for(int i=0; i<blackDeadPieces.size(); i++){
                System.out.print(blackDeadPieces.get(i).getLetter());
            }
            System.out.print("\n");
            for(int i=0; i<whiteDeadPieces.size(); i++){
                System.out.print(whiteDeadPieces.get(i).getLetter());
            }
*/
            turnIndex = currentMove == 'w' ? turnIndex : turnIndex+1;
            changeCurrentMove();
            return true;
        } else {
            return false;
        }
    }

    public boolean isPromotionPossible(Piece pawn, int toY)
    {
        if(pawn.getLetter() == 'p'){
            int promotionRow = pawn.getColor() == 'w' ? 7 : 0;
            return toY == promotionRow;
        }

        return false;
    }


    /**
     * Adds executed move to algebraicNotation String
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     */
    public void addMoveToNotation(int fromX, int fromY, int toX, int toY){
        if(currentMove == 'w')
            algebraicNotation += Integer.toString(turnIndex) + ". ";
        else
            algebraicNotation += " , ";

        algebraicNotation += numberToLetter(fromX) + Integer.toString(fromY) + "-" + numberToLetter(toX) + Integer.toString(toY);

        if(currentMove == 'b')
            algebraicNotation += "\n";
    }

    /**
     * Modifies entire object to given stage from algebraicNotation created by addMoveToNotation(...)
     * @param notation algebraicNotation created by addMoveToNotation(...)
     */
    public void loadChessboardFromAlgebraicNotation(String notation){
        initialSetup();

        String[] split = notation.split(" , ");
        List<String> extractedNotation = new LinkedList<>();
        for (String i : split)
        {
            String[] tmp = i.split("\\d. ");
            for (String j : tmp)
            {
                if (!j.isBlank())
                {
                    j = j.replace("\n", "");
                    extractedNotation.add(j);
                }
            }
        }

        extractedNotation.forEach(s -> executeMove(letterToNumber((s.charAt(0))), Character.getNumericValue((s.charAt(1))),
                letterToNumber((s.charAt(3))), Character.getNumericValue((s.charAt(4)))));
    }


    /**
     * Creates temporary Chessboard object to check if king is safe in simulated environment
     * @param chessboard original chessboard isn't modified by this method
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @return temporary Chessboard object with executed move
     */
    public static Chessboard getChessboardWithExecutedMoveWithoutCheckingIfMoveIsLegal(Chessboard chessboard, int fromX, int fromY, int toX, int toY){
        Chessboard tempChessboard = new Chessboard(chessboard);
        Piece[][] board = tempChessboard.getBoard();

        Piece temp=board[fromX][fromY];

        board[fromX][fromY]=null;
        board[toX][toY]=temp;

        return tempChessboard;
    }


    /**
     *
     * @return 8x8 boolean array filled with false
     */
    public static boolean[][] getChessboardOfFalse(){
        boolean[][] chessboardOfPossibleMoves = new boolean[8][8];
        for(int i=0; i<boardSize; i++){
            for(int j=0; j<boardSize; j++){
                chessboardOfPossibleMoves[i][j]=false;
            }
        }
        return chessboardOfPossibleMoves;
    }


    /**
     * Finds in the board the king of given color
     * @param color can be either 'w' or 'b'
     * @return [X position of King, Y position of King]
     */
    public int[] getPositionOfKing(char color){
        for(int y=0; y<boardSize; y++){
            for(int x=0; x<boardSize; x++){
                if(board[x][y] != null && board[x][y].getLetter() == 'k' && board[x][y].getColor() == color)
                    return new int[]{x,y};
            }
        }
        return null;
    }


    /**
     * Finds Piece on the end of straight or diagonal line
     * @param posX excluded starting X position of line
     * @param posY excluded starting X position of line
     * @param vecX direction, can be only 3 states: -1:left, 0:none, 1:right
     * @param vecY direction, can be only 3 states: -1:down, 0:none, 1:up
     * @return
     */
    public Piece getPieceAtTheEndOfLine(int posX, int posY, int vecX, int vecY){
        for(int x = posX + vecX, y = posY + vecY; Chessboard.areCoordinatesInChessboard(x,y); x+=vecX, y+=vecY){
            if(isTherePieceAt( x, y))
                return board[x][y];
        }
        return null;
    }

    public String getAlgebraicNotation()
    {
        return algebraicNotation;
    }

    /**
     * Modifies board to promote Pawn to chosen Piece
     * @param posX X position of Pawn
     * @param pawn Pawn that should be promoted
     * @param chosenPiece letter of chosen Piece that Pawn should be promoted to, can be either: 'n' - Knight, 'b' - Bishop, 'r' - Rook or 'q' - Queen
     */
    public void promotion(int posX, Piece pawn, char chosenPiece){
        int promotionRow = pawn.getColor() == 'w' ? 7 : 0;
        char color = pawn.getColor();
        switch (chosenPiece){
            case 'n' -> board[posX][promotionRow] = new Knight(color);
            case 'b' -> board[posX][promotionRow] = new Bishop(color);
            case 'r' -> board[posX][promotionRow] = new Rook(color, false);
            case 'q' -> board[posX][promotionRow] = new Queen(color);
        }

    }


    /**
     * Checks if some enemy Piece on the board can beat King of given color
     * method should be used on temporary Chessboard object, created by getChessboardWithExecutedMoveWithoutCheckingIfMoveIsLegal(...) method
     * @param color of King that we want to know if is safe
     * @return true if King is safe, false if King can be beaten
     */
    public Boolean checkIfKingIsSafe(char color){
        int[] temp = getPositionOfKing(color);
        //coordinates of king
        int x=temp[0];
        int y=temp[1];

        //checking if Queen, Rook or Bishop can threaten the King
        int[] vecX = new int[]{-1, 0, 1, 1, 1, 0, -1, -1};
        int[] vecY = new int[]{-1,-1,-1, 0, 1, 1,  1,  0};
        boolean isLineDiagonal=true;

        for(int i=0; i<vecX.length; i++){
            Piece piece = getPieceAtTheEndOfLine(x, y, vecX[i], vecY[i]);
            if(piece == null) {
                isLineDiagonal = !isLineDiagonal;
                continue;
            }

            if(piece.getColor() != color &&
                    (piece.getLetter() == 'q' ||
                            (isLineDiagonal && piece.getLetter() == 'b')  ||
                            (!isLineDiagonal && piece.getLetter() == 'r')
                    )
            ) {
                return false;
            }
            isLineDiagonal= !isLineDiagonal;
        }

        //checking if Knight can threaten the King
        int[] posX=new int[]{-2, -1, 1, 2, 2, 1, -1, -2};
        int[] posY=new int[]{ 1,  2, 2, 1,-1,-2, -2, -1};

        for(int i=0; i<posX.length; i++){
            Piece piece = null;
            if(areCoordinatesInChessboard(x+posX[i], y+posY[i]))
                piece = getPieceAt(x+posX[i], y+posY[i]);
            if(piece == null)
                continue;

            if(piece.getColor() != color && piece.getLetter() == 'n')
                return false;
        }

        //checking if Pawn can threaten the King
        posX=new int[]{-1, 1};
        int direction = color == 'w' ? 1 : -1;

        for(int i=0; i<posX.length; i++){
            Piece piece = null;
            if(areCoordinatesInChessboard(x+posX[i], y+direction))
                piece = getPieceAt(x+posX[i], y+direction);
            if(piece == null)
                continue;

            if(piece.getColor() != color && piece.getLetter() == 'p')
                return false;
        }


        //checking if enemy King can threaten the King
        posX = new int[]{-1, 0, 1, 1, 1, 0, -1, -1};
        posY = new int[]{-1,-1,-1, 0, 1, 1,  1,  0};

        for(int i=0; i<posX.length; i++){
            Piece piece = null;
            if(areCoordinatesInChessboard(x+posX[i], y+posY[i]))
                piece = getPieceAt(x+posX[i], y+posY[i]);
            if(piece == null)
                continue;

            if(piece.getColor() != color && piece.getLetter() == 'k')
                return false;
        }

        return true;
    }

    /**
     * converts number to alphabet letter
     * @param number range: 0-7
     * @return small letter, range a-h
     */
    public static String numberToLetter(int number){
        return switch (number){
            case 0 -> "a";
            case 1 -> "b";
            case 2 -> "c";
            case 3 -> "d";
            case 4 -> "e";
            case 5 -> "f";
            case 6 -> "g";
            case 7 -> "h";
            default -> "!";
        };
    }
    public static int letterToNumber(char letter){
        return switch (letter){
            case 'a' -> 0;
            case 'b' -> 1;
            case 'c' -> 2;
            case 'd' -> 3;
            case 'e' -> 4;
            case 'f' -> 5;
            case 'g' -> 6;
            case 'h' -> 7;
            default -> 2137;
        };
    }

    public static void printChessboard(Chessboard chessboard){
        Piece[][] board = chessboard.getBoard();
        for(int y=0; y<boardSize; y++){
            for(int x=0; x<boardSize; x++){
                if(board[x][y] != null)
                    System.out.print(board[x][y].getLetter());
                else
                    System.out.print(' ');
            }
            System.out.print('\n');
        }
    }

}
