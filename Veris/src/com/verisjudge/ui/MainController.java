package com.verisjudge.ui;

import java.io.File;
import java.util.List;

import com.verisjudge.Config;
import com.verisjudge.LanguageSpec;
import com.verisjudge.Main;
import com.verisjudge.Veris;
import com.verisjudge.checker.Checker;
import com.verisjudge.checker.DiffChecker;
import com.verisjudge.checker.EpsilonChecker;
import com.verisjudge.checker.TokenChecker;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	@FXML private ChoiceBox<String> choiceBoxLanguage;
	@FXML private ChoiceBox<String> choiceBoxChecker;
	
	@FXML private GridPane gridPaneTokenCheckerSettings;
	@FXML private GridPane gridPaneDiffCheckerSettings;
	@FXML private GridPane gridPaneEpsilonCheckerSettings;
	
	@FXML private CheckBox checkBoxTokenCheckerCaseSensitive;
	
	@FXML private CheckBox checkBoxDiffCheckerIgnoreTrailingWhitespace;
	@FXML private CheckBox checkBoxDiffCheckerIgnoreTrailingBlankLines;
	
	@FXML private TextField textFieldEpsilonCheckerAbsoluteEpsilon;
	@FXML private TextField textFieldEpsilonCheckerRelativeEpsilon;
	
	public final static String DETECT_LANGUAGE_STR = "Detect Language";
	public final static String DEFAULT_CHECKER_STR = "Token Checker";
	
	private Veris.Builder verisBuilder;
	private Stage stage;
	private boolean isJudging = false;

	public MainController() {
		verisBuilder = new Veris.Builder();
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	@FXML
    protected void initialize() {
		setTimeLimit(Veris.DEFAULT_TIME_LIMIT / 1000.0);
		
		ObservableList<String> languageList = FXCollections.observableArrayList();
		languageList.add(DETECT_LANGUAGE_STR);
		for (LanguageSpec languageSpec : Config.getConfig().getLanguageSpecsForDisplay())
			languageList.add(languageSpec.getLanguageName());
		choiceBoxLanguage.setItems(languageList);
		choiceBoxLanguage.setValue(DETECT_LANGUAGE_STR);
		
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
    		    	if (verisBuilder.getDataFolder() == null) {
    		    		setDataFolder(f.getParentFile());
    		    	}
    		    } else if (f.isDirectory()) {
    		    	setDataFolder(f);
    		    }
    		}
        }
        event.consume();
	}

    @FXML protected void handleSolutionButtonAction(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	if (verisBuilder.getSolutionFile() != null)
    		fileChooser.setInitialDirectory(verisBuilder.getSolutionFile().getParentFile());
    	fileChooser.setTitle("Select Solution File");
    	fileChooser.getExtensionFilters().add(new ExtensionFilter("Solution files (*.java, *.c, *.cc, *.cpp, *.py, *.exe)", "*.java", "*.c", "*.cc", "*.cpp", "*.py", "*.exe"));
    	File file = fileChooser.showOpenDialog(stage);
    	if (file != null && Veris.isValidSolutionFile(file)) {
    		setSolutionFile(file);
    	}
		event.consume();
	}

    @FXML protected void handleDataPathButtonAction(ActionEvent event) {
    	DirectoryChooser directoryChooser = new DirectoryChooser();
    	directoryChooser.setTitle("Select Data Folder");
    	if (verisBuilder.getDataFolder() != null)
    		directoryChooser.setInitialDirectory(verisBuilder.getDataFolder().getParentFile());
    	File file = directoryChooser.showDialog(stage);
    	if (file != null && file.isDirectory()) {
    		setDataFolder(file);
    	}
		event.consume();
	}
    
	@FXML protected void handleJudgeButtonAction(ActionEvent event) {
		setTimeLimit(getTimeLimit());
		setChecker(getSelectedChecker());
		setLanguageSpec(getSelectedLanguageSpec());
		System.out.println("Checker set to " + getSelectedChecker());
		
		judge();
		
		event.consume();
	}
	
	private void judge() {
		try {
			Veris veris = verisBuilder.build();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/results.fxml"));
			Parent root = (Parent) loader.load();
			ResultsController controller = (ResultsController) loader.getController();
			Stage stage = new Stage();
			
			controller.setStage(stage);
			controller.setVeris(veris);
			
	        Scene scene = new Scene(root, 652, 480);

	        stage.setTitle(veris.getSolutionFile().getName() + " - Verisimilitude");
	        stage.setScene(scene);
	        stage.setResizable(false);
	        if (Main.MAIN_ICON != null)
	        	stage.getIcons().add(Main.MAIN_ICON);
	        
	        controller.judge();
	        stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	private LanguageSpec getSelectedLanguageSpec() {
		String str = choiceBoxLanguage.getValue();
		for (LanguageSpec languageSpec : Config.getConfig().getLanguageSpecsForDisplay())
			if (languageSpec.getLanguageName().equals(str))
				return languageSpec;
		return null;
	}
	
	private Checker getSelectedChecker() {
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
		boolean caseSensitive =
				checkBoxTokenCheckerCaseSensitive.isSelected();
		return new TokenChecker(caseSensitive);
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
		verisBuilder.setChecker(checker);
	}
	
	private void setLanguageSpec(LanguageSpec languageSpec) {
		verisBuilder.setLanguageSpec(languageSpec);
	}

	private void setTimeLimit(double timeLimit) {
		long millis = Math.round(timeLimit * 1000.0);
		millis = Math.max(millis, Veris.MINIMUM_TIME_LIMIT);
		millis = Math.min(millis, Veris.MAXIMUM_TIME_LIMIT);
		verisBuilder.setTimeLimit(millis);
		textFieldTimeLimit.setText("" + millis / 1000.0);
	}

	private void setSolutionFile(File solutionFile) {
		verisBuilder.setSolutionFile(solutionFile);
		if (solutionFile != null) {
			buttonSolution.setText(solutionFile.getPath());
			buttonSolution.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
			stage.setTitle("Verisimilitude - " + solutionFile.getName());
		} else {
			buttonSolution.setText("?");
			stage.setTitle("Verisimilitude - ?");
		}
    	updateJudgeButton();
	}
	
	private void setDataFolder(File dataFolder) {
		verisBuilder.setDataFolder(dataFolder);
		if (dataFolder != null) {
			buttonDataPath.setText(dataFolder.getPath());
			buttonDataPath.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
		} else {
			buttonDataPath.setText("?");
		}
		updateJudgeButton();
	}
	
	private void updateJudgeButton() {
		boolean isReady = !isJudging && verisBuilder != null && verisBuilder.isReady();
		buttonJudge.setDisable(!isReady);
	}
}
