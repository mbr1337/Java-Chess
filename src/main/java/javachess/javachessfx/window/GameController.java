package javachess.javachessfx.window;

import javachess.javachessfx.Main;
import javachess.javachessfx.backend.Chessboard;
import javachess.javachessfx.window.util.GameTime;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameController
{
    @FXML
    private Pane chessboard;
    @FXML
    private BorderPane gamePane;
    @FXML
    private Spinner<Integer> minutes;
    @FXML
    private Spinner<Integer> seconds;
    @FXML
    private Label errorLabel;
    @FXML
    private Label dragAndDropSave;
    @FXML
    private Label whiteTime;
    @FXML
    private Label blackTime;
    @FXML
    private AnchorPane configPane;
    @FXML
    private HBox deadBlackPieces;
    @FXML
    private HBox deadWhitePieces;
    private Pane currentPane;

    private final int size = 800;
    private final int spots = 8;
    private final int squareSize = size / spots;

    private GameTime gameTime;
    private long timer;

    private Rectangle[][] grid;
    private Chessboard gameField;
    private final List<Circle> planesForPieces = new ArrayList<>();

    private final int[] move = new int[2];
    private String loadedNotation;

    @FXML
    public void initialize()
    {
        currentPane = configPane;

        SpinnerValueFactory<Integer> minutesValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 1000);
        SpinnerValueFactory<Integer> secondsValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000);
        minutesValueFactory.setValue(0);
        secondsValueFactory.setValue(0);

        minutes.setValueFactory(minutesValueFactory);
        seconds.setValueFactory(secondsValueFactory);

        dragAndDropSave.setOnDragOver(event ->
        {
            dragAndDropSave.setStyle("-fx-background-color: white");
            if (event.getGestureSource() != dragAndDropSave
                    && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        dragAndDropSave.setOnDragExited(event -> dragAndDropSave.setStyle("-fx-background-color: #a6a6a6"));
        dragAndDropSave.setOnDragDropped(this::dropFile);
    }

    /**
     * Method which sets the timers, and draws the board.
     */
    private void startTheGame()
    {
        gameField = new Chessboard();
        if (whiteTime.getText().equals("00:00") && blackTime.getText().equals("00:00"))
        {
            gameTime = new GameTime(minutes.getValue(), seconds.getValue());
        }
        else
        {
            gameTime = new GameTime();
            String[] tmp = blackTime.getText().split(":");
            gameTime.setBlackPlayTimeMinutes(Integer.parseInt(tmp[0]));
            gameTime.setBlackPlayTimeSeconds(Integer.parseInt(tmp[1]));
            tmp = whiteTime.getText().split(":");
            gameTime.setWhitePlayTimeMinutes(Integer.parseInt(tmp[0]));
            gameTime.setWhitePlayTimeSeconds(Integer.parseInt(tmp[1]));
        }

        if (loadedNotation != null)
        {
            gameField.loadChessboardFromAlgebraicNotation(loadedNotation);
        }

        grid = new Rectangle[spots][spots];
        for (int i = 0; i < size; i += squareSize)
        {
            for (int j = 0; j < size; j += squareSize)
            {
                Rectangle r = new Rectangle(i, j, squareSize, squareSize);
                grid[i / squareSize][j / squareSize] = r;
                r.setFill(Color.WHITE);
                r.setOpacity(0.0);
                chessboard.getChildren().add(r);
            }
        }

        chessboard.setBackground(new Background(
                new BackgroundImage(
                        new Image("file:src/main/resources/javachess/board.jpg", size, size, true, true),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));
        addPieces();
        blackTime.setText(String.format("%02d:%02d", gameTime.getBlackPlayTimeMinutes(), gameTime.getBlackPlayTimeSeconds()));
        whiteTime.setText(String.format("%02d:%02d", gameTime.getWhitePlayTimeMinutes(), gameTime.getWhitePlayTimeSeconds()));

        long[] startNanoTime = {System.nanoTime()};

        // Timer which is used as a chess clock.
        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                double deltaTime = (currentNanoTime - startNanoTime[0]) / 1000000000.0;
                if (deltaTime >= 1.0)
                {
                    startNanoTime[0] = System.nanoTime();
                    timer++;
                    updateTime();
                }

                if ((gameTime.getBlackPlayTimeMinutes() == 0 && gameTime.getBlackPlayTimeSeconds() == 0)
                        || (gameTime.getWhitePlayTimeMinutes() == 0 && gameTime.getWhitePlayTimeSeconds() == 0))
                {
                    this.stop();
                    chessboard.setDisable(true);

                    Alert end = new Alert(Alert.AlertType.INFORMATION);
                    end.setTitle("Koniec gry");
                    end.setHeaderText(null);
                    end.setContentText("Czas dobiegł końca.");
                    end.show();
                }
            }
        }.start();
    }

    /**
     * Method used to populate the board with pieces from the table from Chessboard class.
     */
    private void addPieces()
    {
        javachess.javachessfx.backend.pieces.Piece[][] board = gameField.getBoard();

        for (int i = board.length - 1; i >= 0; i--)
        {
            for (int j = board[i].length - 1; j >= 0; j--)
            {
                if (board[i][j] == null)
                {
                    continue;
                }

                Circle c = new Circle();

                double radius = squareSize / 3.0;
                int x = squareSize / 2 + squareSize * i;
                int y = squareSize / 2 + squareSize * j;

                Piece p = new Piece(x, y, radius, c, board[i][j]);

                c.setOnMousePressed(event -> pressed(event, p));
                c.setOnMouseDragged(event -> dragged(event, p));
                c.setOnMouseReleased(event -> released(event, p));
                planesForPieces.add(c);

                chessboard.getChildren().add(c);
                p.draw();
            }
        }
    }

    /**
     * Method to handle 'released' mouse event.
     * @param event mouse event
     * @param p Piece object that is sued to display a piece on the board.
     */
    private void released(MouseEvent event, Piece p)
    {
        restoreBoard();

        int gridX = (int) p.getX() / squareSize;
        int gridY = (int) p.getY() / squareSize;
        p.setX((double) (squareSize / 2) + squareSize * gridX);
        p.setY((double) (squareSize / 2) + squareSize * gridY);

        if (gameField.executeMove(move[0], move[1], gridX, gridY))
        {
            if (gameField.isPromotionPossible(p.getPiece(), gridY))
            {
                try
                {
                    gameField.promotion(gridX, p.getPiece(), promotionWindow());
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }

            p.setX((double) (squareSize / 2) + squareSize * gridX);
            p.setY((double) (squareSize / 2) + squareSize * gridY);
            chessboard.getChildren().removeAll(planesForPieces);
            planesForPieces.clear();
            addPieces();

            updateDeadPieces();
        }
        else
        {
            p.setX((double) (squareSize / 2) + squareSize * move[0]);
            p.setY((double) (squareSize / 2) + squareSize * move[1]);
            p.draw();
        }
    }

    /**
     * Method to handle 'dragged' mouse event.
     * @param event mouse event
     * @param p Piece object that is sued to display a piece on the board.
     */
    private void dragged(MouseEvent event, Piece p)
    {
        p.setX(p.getX() + event.getX());
        p.setY(p.getY() + event.getY());
        p.draw();
    }

    /**
     * Method to handle 'pressed' mouse event.
     * @param event mouse event
     * @param p Piece object that is sued to display a piece on the board.
     */
    private void pressed(MouseEvent event, Piece p)
    {
        move[0] = (int) p.getX() / squareSize;
        move[1] = (int) p.getY() / squareSize;
        showPossibleMoves((int) p.getX() / squareSize, (int) p.getY() / squareSize);
    }

    /**
     * This method displays possible moves of a given piece on the board.
     * @param x coordinate x of a piece
     * @param y coordinate y of a piece
     */
    private void showPossibleMoves(int x, int y)
    {
        boolean[][] moves = gameField.getChessboardOfPossibleMovesFromSquareAt(x, y);
        for (int i = 0; i < grid.length; i++)
        {
            for (int j = 0; j < grid[i].length; j++)
            {
                if (moves[i][j])
                {
                    grid[i][j].setFill(Color.GREEN);
                    grid[i][j].setOpacity(0.5);
                }
            }
        }
    }

    /**
     * Method used to restore board after use of showPossibleMoves.
     */
    private void restoreBoard()
    {
        for (Rectangle[] rectangles : grid)
        {
            for (Rectangle rectangle : rectangles)
            {
                rectangle.setFill(Color.WHITE);
                rectangle.setOpacity(0.0);
            }
        }
    }

    /**
     * Method which updates displayed dead pieces on the screen.
     */
    private void updateDeadPieces()
    {
        deadBlackPieces.getChildren().clear();
        deadWhitePieces.getChildren().clear();
        gameField.getBlackDeadPieces().forEach(piece ->
        {
            Circle c = new Circle(20.0);
            c.setFill(new ImagePattern(new Image("file:src/main/resources/javachess/javachessfx/" + piece.getPathToIcon())));
            c.setRotate(180.0);
            deadBlackPieces.getChildren().add(c);
        });
        gameField.getWhiteDeadPieces().forEach(piece ->
        {
            Circle c = new Circle(20.0);
            c.setFill(new ImagePattern(new Image("file:src/main/resources/javachess/javachessfx/" + piece.getPathToIcon())));
            c.setRotate(180.0);
            deadWhitePieces.getChildren().add(c);
        });
    }

    /**
     * Displays the promotion window to change a piece.
     * @return character which represents the chosen piece
     * @throws IOException
     */
    private char promotionWindow() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("promotion.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Promotion");
        stage.setScene(scene);
        stage.showAndWait();

        return PromotionWindowController.chosenPawn;
    }

    /**
     * Updates the chess clocks on the screen.
     */
    private void updateTime()
    {

        if (gameField.getCurrentMove() == 'w')
        {
            long playTimeInSeconds = 60 * (long) gameTime.getWhitePlayTimeMinutes() + (long) gameTime.getWhitePlayTimeSeconds();
            long newTime = playTimeInSeconds - timer;
            gameTime.setWhitePlayTimeMinutes((int) (newTime / 60));
            gameTime.setWhitePlayTimeSeconds((int) (newTime % 60));

            whiteTime.setText(String.format("%02d:%02d", gameTime.getWhitePlayTimeMinutes(), gameTime.getWhitePlayTimeSeconds()));
        }
        else
        {
            long playTimeInSeconds = 60 * (long) gameTime.getBlackPlayTimeMinutes() + (long) gameTime.getBlackPlayTimeSeconds();
            long newTime = playTimeInSeconds - timer;
            gameTime.setBlackPlayTimeMinutes((int) (newTime / 60));
            gameTime.setBlackPlayTimeSeconds((int) (newTime % 60));

            blackTime.setText(String.format("%02d:%02d", gameTime.getBlackPlayTimeMinutes(), gameTime.getBlackPlayTimeSeconds()));
        }

        timer = 0;
    }

    /**
     * Method which is used to save the game using method from Chessboard class.
     * @param mouseEvent mouse event
     */
    public void save(MouseEvent mouseEvent)
    {
        Stage stage = (Stage) chessboard.getScene().getWindow();
        DirectoryChooser directory = new DirectoryChooser();
        File selectedDirectory = directory.showDialog(stage);
        String algebraicNotation = "w " + whiteTime.getText() + "\n" + "b " + blackTime.getText() + "\n" + gameField.getAlgebraicNotation();

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(selectedDirectory.getPath() + File.separator + "game.txt")))
        {
            writer.write(algebraicNotation);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Method used to handle pane change.
     * @param mouseEvent mouseEvent
     */
    public void changePane(MouseEvent mouseEvent)
    {
        startTheGame();

        currentPane.setVisible(false);
        currentPane = gamePane;
        currentPane.setVisible(true);
    }

    /**
     * Method to handle drop event. It is used in the configuration window so user can just drag and drop
     * a file with saved game.
     * @param event
     */
    private void dropFile(DragEvent event)
    {
        Dragboard dragboard = event.getDragboard();
        File droppedFile = dragboard.getFiles().get(0);
        boolean success = false;

        errorLabel.setVisible(false);
        if (!droppedFile.getName().substring(droppedFile.getName().lastIndexOf('.') + 1).equalsIgnoreCase("txt"))
        {
            errorLabel.setVisible(true);
            errorLabel.setText("Nieprawidłowe rozszerzenie pliku z notacją algebraiczną. Tylko .txt. " +
                    "Jeśli uruchomisz grę, rozpocznie się ona od początku.");
            return;
        }

        if (dragboard.hasFiles())
        {
            if (parseTheFile(droppedFile))
            {
                errorLabel.setVisible(true);
                errorLabel.setTextFill(Color.color(0,0.6,0));
                errorLabel.setText("Wczytywanie powiodło się.");
            }
            else
            {
                errorLabel.setVisible(true);
                errorLabel.setTextFill(Color.color(1,0,0));
                errorLabel.setText("Nie można załadować zapisanej gry. Plik prawdopodobnie jest uszkodzony. " +
                        "Jeśli uruchomisz grę, rozpocznie się ona od początku.");
                
                whiteTime.setText("00:00");
                blackTime.setText("00:00");
            }

            success = true;
        }

        event.setDropCompleted(success);
        event.consume();
    }

    /**
     * Parses the given file to extract information about saved game.
     * @param savedGame
     */
    private boolean parseTheFile(File savedGame)
    {
        StringBuilder result = new StringBuilder();
        errorLabel.setVisible(false);

        try(BufferedReader reader = new BufferedReader(new FileReader(savedGame)))
        {
            String line;
            while ((line= reader.readLine()) != null)
            {
                if (line.matches("\\d\\. (([a-z]\\d-[a-z]\\d , )|([a-z]\\d-[a-z]\\d))+"))
                {
                    result.append(line).append("\n");
                }
                else if (line.matches("w (\\d+:\\d+)"))
                {
                    whiteTime.setText(line.substring(2));
                }
                else if (line.matches("b (\\d+:\\d+)"))
                {
                    blackTime.setText(line.substring(2));
                }
                else
                {
                    result.setLength(0);
                    return false;
                }
            }

            loadedNotation = result.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Method used for choosing a file with saved game.
     * @param mouseEvent
     */
    public void chooseFile(MouseEvent mouseEvent)
    {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        chooser.getExtensionFilters().add(extensionFilter);
        Stage stage = (Stage) chessboard.getScene().getWindow();
        File savedGame = chooser.showOpenDialog(stage);

        if (savedGame == null)
        {
            errorLabel.setVisible(true);
            errorLabel.setText("Nieprawidłowe rozszerzenie pliku z notacją algebraiczną. Tylko .txt. " +
                    "Jeśli uruchomisz grę, rozpocznie się ona od początku.");
        }
        else
        {
            if (parseTheFile(savedGame))
            {
                errorLabel.setVisible(true);
                errorLabel.setTextFill(Color.color(0,0.6,0));
                errorLabel.setText("Wczytywanie powiodło się.");
            }
            else
            {
                errorLabel.setVisible(true);
                errorLabel.setTextFill(Color.color(1,0,0));
                errorLabel.setText("Nie można załadować zapisanej gry. Plik prawdopodobnie jest uszkodzony. " +
                        "Jeśli uruchomisz grę, rozpocznie się ona od początku.");

                whiteTime.setText("00:00");
                blackTime.setText("00:00");
            }
        }
    }
}