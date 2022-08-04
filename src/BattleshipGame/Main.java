package BattleshipGame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("GUI/MainStage.fxml"));
        primaryStage.setTitle("Battleships the game");
        primaryStage.setScene(new Scene(root, 900, 615));
        primaryStage.setMinHeight(630);
        primaryStage.setMinWidth(900);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
