package javachess.javachessfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends Application
{
    public static void main(String[] args)
    {
        launch();
        /*String notation = """
                1. d1-d3 , e6-e4
                2. d3-e4 , d7-g4
                3. c0-g4 , b7-c5
                4. d0-d6 , e7-d6""";
        String[] split = notation.split(" , ");
        List<String> extractedNotation = new ArrayList<>();
        for (String str : split)
        {
            String[] tmp = str.split("\\d. ");
            for (String i : tmp)
            {
                if (!i.isBlank())
                {
                    i = i.replace("\n", "");
                    extractedNotation.add(i);
                }
            }
        }

        extractedNotation.forEach(System.out::println);*/
    }

    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("board.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Java Chess FX");
        stage.getIcons().add(new Image("file:src/main/resources/javachess/javachessfx/icons/icon.png"));
        stage.setScene(scene);
        stage.show();
    }
}