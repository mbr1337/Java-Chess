module javachess.javachessfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens javachess.javachessfx to javafx.fxml;
    exports javachess.javachessfx;
    exports javachess.javachessfx.window;
    opens javachess.javachessfx.window to javafx.fxml;
}