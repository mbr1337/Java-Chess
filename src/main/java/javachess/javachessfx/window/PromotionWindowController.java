package javachess.javachessfx.window;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class PromotionWindowController
{
    @FXML
    private ImageView queen;

    public static char chosenPawn;

    public void choosePawn(MouseEvent mouseEvent)
    {
        String target = mouseEvent.getTarget().toString();
        if (target.contains("knight"))
        {
            chosenPawn = 'n';
        }
        else if (target.contains("bishop"))
        {
            chosenPawn = 'b';
        }
        else if (target.contains("rook"))
        {
            chosenPawn = 'r';
        }
        else if (target.contains("queen"))
        {
            chosenPawn = 'q';
        }

        ((Stage) queen.getScene().getWindow()).close();
    }
}
