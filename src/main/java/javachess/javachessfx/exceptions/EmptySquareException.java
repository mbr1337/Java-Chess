package javachess.javachessfx.exceptions;

public class EmptySquareException extends RuntimeException{
    public EmptySquareException(String s){
        super(s);
    }
    EmptySquareException(){}
}
