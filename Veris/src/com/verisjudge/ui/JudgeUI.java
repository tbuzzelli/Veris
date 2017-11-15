package com.verisjudge.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JudgeUI extends Application {

	public static void main(String[] args) {
        launch(args);
    }

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
		Parent root = (Parent) loader.load();
		MainController controller = (MainController) loader.getController();
		controller.setStage(stage);
		
        Scene scene = new Scene(root);
    
        stage.setTitle("Verisimilitude");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
	}
}
