package sample;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.*;
public class Main extends Application {

    @Override

    public void start(Stage primaryStage) throws Exception{
        AnchorPane root =(AnchorPane) FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("易行");

        Controller controller=new Controller();

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
