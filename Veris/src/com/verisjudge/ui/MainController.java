package com.verisjudge.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.verisjudge.Veris;
import com.verisjudge.checker.Checker;
import com.verisjudge.checker.DiffChecker;
import com.verisjudge.checker.EpsilonChecker;
import com.verisjudge.checker.TokenChecker;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainController {
	@FXML private TextField textFieldTimeLimit;
	@FXML private Button buttonDataPath;
	@FXML private Button buttonSolution;
	@FXML private Button buttonJudge;
	@FXML private ChoiceBox<String> choiceBoxChecker;
	
	@FXML private GridPane gridPaneTokenCheckerSettings;
	@FXML private GridPane gridPaneDiffCheckerSettings;
	@FXML private GridPane gridPaneEpsilonCheckerSettings;
	
	@FXML private CheckBox checkBoxTokenCheckerCaseSensative;
	
	@FXML private CheckBox checkBoxDiffCheckerIgnoreTrailingWhitespace;
	@FXML private CheckBox checkBoxDiffCheckerIgnoreTrailingBlankLines;
	
	@FXML private TextField textFieldEpsilonCheckerAbsoluteEpsilon;
	@FXML private TextField textFieldEpsilonCheckerRelativeEpsilon;
	
	public final static String DEFAULT_CHECKER_STR = "Token Checker";
	
	private Veris veris;
	private Stage stage;
	private boolean isJudging = false;

	public MainController() {
		veris = new Veris();
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	@FXML
    protected void initialize() {
		setTimeLimit(Veris.DEFAULT_TIME_LIMIT / 1000.0);
		choiceBoxChecker.setItems(FXCollections.observableArrayList("Token Checker", "Diff Checker", "Epsilon Checker"));
		choiceBoxChecker.setValue(DEFAULT_CHECKER_STR);
		choiceBoxChecker.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
				onCheckerSelected(choiceBoxChecker.getItems().get((Integer) number2));
			}
		});
		onCheckerSelected(DEFAULT_CHECKER_STR);
		updateJudgeButton();
	}

	@FXML protected void handleDragOver(DragEvent event) {
    	if (event.getDragboard().hasFiles()) {
    		List<File> files = event.getDragboard().getFiles();
    		if (files.size() == 1) {
    			File f = files.get(0);
    		    if (Veris.isValidSolutionFile(f)) {
    		    	event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
    		    } else if (f.isDirectory()) {
    		    	event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
    		    }
    		}
        }
        event.consume();
	}
    
    @FXML protected void handleDragDropped(DragEvent event) {
    	if (event.getDragboard().hasFiles()) {
    		List<File> files = event.getDragboard().getFiles();
    		if (files.size() == 1) {
    			File f = files.get(0);
    		    if (Veris.isValidSolutionFile(f)) {
    		    	setSolutionFile(f);
    		    	if (veris.getDataFolder() == null) {
    		    		setDataPath(f.getParentFile());
    		    	}
    		    } else if (f.isDirectory()) {
    		    	setDataPath(f);
    		    }
    		}
        }
        event.consume();
	}

    @FXML protected void handleSolutionButtonAction(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	if (veris.getSourceFile() != null)
    		fileChooser.setInitialDirectory(veris.getSourceFile().getParentFile());
    	fileChooser.setTitle("Select Solution File");
    	fileChooser.getExtensionFilters().add(new ExtensionFilter("Solution files (*.java, *.c, *.cc, *.cpp, *.py)", "*.java", "*.c", "*.cc", "*.cpp", "*.py"));
    	File file = fileChooser.showOpenDialog(stage);
    	if (file != null && Veris.isValidSolutionFile(file)) {
    		setSolutionFile(file);
    	}
		event.consume();
	}

    @FXML protected void handleDataPathButtonAction(ActionEvent event) {
    	DirectoryChooser directoryChooser = new DirectoryChooser();
    	directoryChooser.setTitle("Select Data Folder");
    	if (veris.getDataFolder() != null)
    		directoryChooser.setInitialDirectory(veris.getDataFolder().getParentFile());
    	File file = directoryChooser.showDialog(stage);
    	if (file != null && file.isDirectory()) {
    		setDataPath(file);
    	}
		event.consume();
	}
    
	@FXML protected void handleJudgeButtonAction(ActionEvent event) {
		setTimeLimit(getTimeLimit());
		setChecker(getChecker());
		
		judge();
		
		event.consume();
	}
	
	private void remakeVeris(Veris oldVeris) {
		veris = new Veris();
		try {
			veris.setSourceFile(oldVeris.getSourceFile());
			veris.setDataFolder(oldVeris.getDataFolder());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void judge() {
		Veris oldVeris = veris;
		remakeVeris(oldVeris);
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("results.fxml"));
			Parent root = (Parent) loader.load();
			ResultsController controller = (ResultsController) loader.getController();
			 Stage stage = new Stage();
			
			controller.setStage(stage);
			controller.setVeris(oldVeris);
			
	        Scene scene = new Scene(root, 652, 480);

	        stage.setTitle("Verisimilitude - " + oldVeris.getSourceFile().getName());
	        stage.setScene(scene);
	        stage.setResizable(false);
	        
	        stage.show();
	        controller.judge();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void reportIsJudgingStatus(boolean isJudging) {
		this.isJudging = isJudging;
		updateJudgeButton();
	}
	
	private double getTimeLimit() {
		try {
			return Double.parseDouble(textFieldTimeLimit.getText());
		} catch (Exception e) {
			return Veris.DEFAULT_TIME_LIMIT / 1000.0;
		}
	}
	
	private void onCheckerSelected(String str) {
		gridPaneTokenCheckerSettings.setVisible(false);
		gridPaneDiffCheckerSettings.setVisible(false);
		gridPaneEpsilonCheckerSettings.setVisible(false);
		switch (str) {
			case "Token Checker":
				gridPaneTokenCheckerSettings.setVisible(true);
				return;
			case "Diff Checker":
				gridPaneDiffCheckerSettings.setVisible(true);
				return;
			case "Epsilon Checker":
				gridPaneEpsilonCheckerSettings.setVisible(true);
				return;
			default:
				return;
		}
	}
	
	private Checker getChecker() {
		String str = choiceBoxChecker.getValue();
		switch (str) {
			case "Token Checker":
				return createTokenChecker();
			case "Diff Checker":
				return createDiffChecker();
			case "Epsilon Checker":
				return createEpsilonChecker();
			default:
				return new TokenChecker();
		}
	}
	
	private Checker createTokenChecker() {
		boolean caseSensative =
				checkBoxTokenCheckerCaseSensative.isSelected();
		return new TokenChecker(caseSensative);
	}
	
	private Checker createDiffChecker() {
		boolean ignoreTrailingWhitespace =
				checkBoxDiffCheckerIgnoreTrailingWhitespace.isSelected();
		boolean ignoreTrailingBlankLines =
				checkBoxDiffCheckerIgnoreTrailingBlankLines.isSelected();
		return new DiffChecker(
				ignoreTrailingWhitespace,
				ignoreTrailingBlankLines);
	}
	
	private Checker createEpsilonChecker() {
		String absoluteEpsilonStr =
				textFieldEpsilonCheckerAbsoluteEpsilon.getText();
		String relativeEpsilonStr =
				textFieldEpsilonCheckerRelativeEpsilon.getText();
		
		double absoluteEpsilon = EpsilonChecker.DEFAULT_ABSOLUTE_EPSILON;
		double relativeEpsilon = EpsilonChecker.DEFAULT_RELATIVE_EPSILON;
		
		try {
			absoluteEpsilon = Double.parseDouble(absoluteEpsilonStr);
		} catch (Exception e) {}
		
		try {
			relativeEpsilon = Double.parseDouble(relativeEpsilonStr);
		} catch (Exception e) {}
		
		return new EpsilonChecker(absoluteEpsilon, relativeEpsilon);
	}

	private void setChecker(Checker checker) {
		veris.setChecker(checker);
	}

	private void setTimeLimit(double timeLimit) {
		long millis = Math.round(timeLimit * 1000.0);
		// TODO insert validation
		veris.setTimeLimit(millis);
		textFieldTimeLimit.setText("" + timeLimit);
	}

	private void setSolutionFile(File f) {
    	try {
			veris.setSourceFile(f);
			buttonSolution.setText(f.getPath());
			buttonSolution.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
			stage.setTitle("Verisimilitude - " + f.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	updateJudgeButton();
	}
	
	private void setDataPath(File f) {
		try {
			veris.setDataFolder(f);
			buttonDataPath.setText(f.getPath());
			buttonDataPath.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		updateJudgeButton();
	}
	
	private void updateJudgeButton() {
		boolean isReady = !isJudging && veris != null && veris.isReady();
		buttonJudge.setDisable(!isReady);
	}
}
