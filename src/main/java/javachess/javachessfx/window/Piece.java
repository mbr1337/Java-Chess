package javachess.javachessfx.window;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class Piece
{
    private double x;
    private double y;
    private double radius;
    private Circle circle;
    private javachess.javachessfx.backend.pieces.Piece piece;

    public Piece(double x, double y, double radius, Circle circle, javachess.javachessfx.backend.pieces.Piece piece)
    {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.circle = circle;
        this.piece = piece;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public javachess.javachessfx.backend.pieces.Piece getPiece()
    {
        return piece;
    }

    public void draw()
    {
        circle.setRadius(radius);
        circle.setTranslateX(x);
        circle.setTranslateY(y);
        Image img = new Image("file:src/main/resources/javachess/javachessfx/" + piece.getPathToIcon());
        circle.setFill(new ImagePattern(img));
    }
}
