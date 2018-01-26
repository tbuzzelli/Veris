package com.verisjudge.ui;

import java.io.File;
import java.io.IOException;

import com.verisjudge.Problem;
import com.verisjudge.Veris;
import com.verisjudge.checker.Checker;
import com.verisjudge.utils.CheckerUtils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JudgeUI extends Application {

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
        Integer timeLimitInteger = Problem.parseTimeLimit(timeLimitString);
        if (timeLimitInteger == null) {
            exitWithError("Failed to parse time limit from '" + timeLimitString + "'");
            return -1;
        }
        int timeLimit = timeLimitInteger.intValue();
        if (timeLimit < Problem.MINIMUM_TIME_LIMIT
            || timeLimit > Problem.MAXIMUM_TIME_LIMIT) {
            exitWithError("Time limit of " + timeLimit + "ms is out of acceptable range ["
                + Problem.MINIMUM_TIME_LIMIT + "ms, " + Problem.MAXIMUM_TIME_LIMIT + "ms]");
            return -1;
        }
        return timeLimitInteger.intValue();
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
		if (args.length == 0) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
			Parent root = (Parent) loader.load();
			MainController controller = (MainController) loader.getController();
			controller.setStage(stage);
			
	        Scene scene = new Scene(root);
	    
	        stage.setTitle("Verisimilitude");
	        stage.setScene(scene);
	        stage.setResizable(false);
	        stage.show();
		} else {
			Veris.Builder verisBuilder = new Veris.Builder();
			File solutionFile = new File(args[0]);
			if (!solutionFile.exists())
				exitWithError("Failed to find solution file '" + args[0] + "'");
			verisBuilder.setSolutionFile(solutionFile);
			verisBuilder.setDataFolder(new File("."));
			for (int i = 1; i < args.length; i++) {
				String arg = args[i];
				if (!arg.startsWith("--"))
					usage();
				int eqIdx = arg.indexOf("=");
				String key, value;
				if (eqIdx < 0) {
					key = arg.substring(2).toLowerCase();
					value = "";
				} else {
					key = arg.substring(2, eqIdx).toLowerCase();
					value = arg.substring(eqIdx + 1);
				}

				switch (key) {
					case "d":
					case "data":
					case "datapath":
					case "data_path":
						if (value.length() == 0)
							exitWithError("The data folder name can't be blank/empty");
						File dataFolder = new File(value);
						if (!dataFolder.exists() || !dataFolder.isDirectory())
							exitWithError("Failed to find data folder '" + value + "'");
						verisBuilder.setDataFolder(dataFolder);
						break;
					case "t":
					case "time":
					case "timelimit":
					case "time_limit":
						int timeLimit = parseTimeLimit(value);
						verisBuilder.setTimeLimit((long) timeLimit);
						break;
					case "c":
					case "checker":
						Checker checker = parseChecker(value);
						verisBuilder.setChecker(checker);
						break;
					default:
						usage();
				}
			}
			try {
				Veris veris = verisBuilder.build();
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("results.fxml"));
					Parent root = (Parent) loader.load();
					ResultsController controller = (ResultsController) loader.getController();

					controller.setStage(stage);
					controller.setVeris(veris);
					
			        Scene scene = new Scene(root, 652, 480);

			        stage.setTitle("Verisimilitude - " + veris.getSolutionFile().getName());
			        stage.setScene(scene);
			        stage.setResizable(false);
			        
			        controller.judge();
			        stage.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				exitWithError("Failed to read solution file and/or data folder");
			}
		}
	}
}