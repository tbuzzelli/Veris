package com.verisjudge;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import com.verisjudge.checker.Checker;
import com.verisjudge.ui.MainController;
import com.verisjudge.ui.ResultsController;
import com.verisjudge.utils.CheckerUtils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

	public final static Image MAIN_ICON = new Image(Main.class.getResourceAsStream("/images/icon.png"));
	public final static String STYLES = Main.class.getResource("/css/styles.css").toExternalForm();
	public final static String DARK_THEME = Main.class.getResource("/css/styles-dark.css").toExternalForm();
	
	public static void main(String[] args) {
		launch(args);
    }
	
	public void usage() {
		System.out.println("USAGE");
		System.exit(1);
	}
	
	public void exitWithError(String errorString) {
		System.err.println(errorString);
		System.exit(1);
	}
	
	public int parseTimeLimit(String timeLimitString) {
        if (timeLimitString == null) {
            return -1;
        }
        Long timeLimitLong = Problem.parseTimeLimit(timeLimitString);
        if (timeLimitLong == null) {
            exitWithError("Failed to parse time limit from '" + timeLimitString + "'");
            return -1;
        }
        int timeLimit = timeLimitLong.intValue();
        if (timeLimit < Problem.MINIMUM_TIME_LIMIT
            || timeLimit > Problem.MAXIMUM_TIME_LIMIT) {
            exitWithError("Time limit of " + timeLimit + "ms is out of acceptable range ["
                + Problem.MINIMUM_TIME_LIMIT + "ms, " + Problem.MAXIMUM_TIME_LIMIT + "ms]");
            return -1;
        }
        return timeLimitLong.intValue();
    }
	
	public Checker parseChecker(String checkerString) {
        if (checkerString == null) {
            return null;
        }
        CheckerUtils checkerUtils = new CheckerUtils();
        Checker checker = checkerUtils.getCheckerFromString(checkerString);
        if (checkerUtils.hasError()) {
            exitWithError(checkerUtils.getErrorMessage());
            return null;
        }
        return checker;
    }

	@Override
	public void start(Stage stage) throws IOException {
		String[] args = getParameters().getRaw().toArray(new String[0]);
		Locale locale = Locale.ENGLISH;
		ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle", locale);
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"), bundle);
		Parent root = (Parent) loader.load();
		
		MainController controller = (MainController) loader.getController();
		
        Scene scene = new Scene(root);
    
        stage.setTitle("Verisimilitude");
        stage.setScene(scene);
        stage.setResizable(false);
        addIconToStage(stage);
        
        controller.setStage(stage);
        
        controller.loadPrevious();
        
        stage.show();
	}
	
	public static void addIconToStage(Stage stage) {
		if (stage == null || MAIN_ICON == null)
			return;
		stage.getIcons().add(MAIN_ICON); 
	}
	
	public static void updateTheme(Stage stage) {
		if (stage == null || stage.getScene() == null)
			return;
		stage.getScene().getStylesheets().add(STYLES);
		if (Settings.getSettings().getBooleanOrDefault(Settings.USE_DARK_THEME, false))
			stage.getScene().getStylesheets().add(DARK_THEME);
	}
}