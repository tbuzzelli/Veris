package com.verisjudge.ui;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.verisjudge.Config;
import com.verisjudge.LanguageSpec;
import com.verisjudge.Main;
import com.verisjudge.Settings;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainController {
	@FXML private BorderPane borderPaneMain;
	
	@FXML private TextField textFieldTimeLimit;
	@FXML private Button buttonDataPath;
	@FXML private Button buttonSolution;
	@FXML private Button buttonJudge;
	@FXML private ChoiceBox<String> choiceBoxLanguage;
	@FXML private ChoiceBox<String> choiceBoxChecker;
	
	@FXML private Accordion accordionAdvancedSettings;
	@FXML private TitledPane titledPaneAdvancedSettings;
	@FXML private TextField textFieldDataRegex;
	
	@FXML private GridPane gridPaneTokenCheckerSettings;
	@FXML private GridPane gridPaneDiffCheckerSettings;
	@FXML private GridPane gridPaneEpsilonCheckerSettings;
	
	@FXML private CheckBox checkBoxTokenCheckerCaseSensitive;
	
	@FXML private CheckBox checkBoxDiffCheckerIgnoreTrailingWhitespace;
	@FXML private CheckBox checkBoxDiffCheckerIgnoreTrailingBlankLines;
	
	@FXML private TextField textFieldEpsilonCheckerAbsoluteEpsilon;
	@FXML private TextField textFieldEpsilonCheckerRelativeEpsilon;
	
	private final static ResourceBundle MESSAGES = ResourceBundle.getBundle("MessagesBundle", Locale.ENGLISH);

	public final static String DETECT_LANGUAGE_STR = "Detect Language";
	public final static String DEFAULT_CHECKER_STR = "Token Checker";
	
	private final ContextMenu mainContextMenu = new ContextMenu();
	
	private Veris.Builder verisBuilder;
	private Stage stage;
	private boolean isJudging = false;

	public MainController() {
		verisBuilder = new Veris.Builder();
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
		Main.updateTheme(this.stage);
	}
	
	public boolean loadPrevious() {
		// If we shouldn't remember the judging settings, clear the settings then return.
		if (!Settings.getSettings().getBooleanOrDefault(Settings.REMEMBER_JUDGING_SETTINGS, true)) {
			clearPrevious();
			return true;
		}
		
		Long previousUseTime = Settings.getSettings().getLong(Settings.PREVIOUS_USE_TIME);
		// If we have no record of previous use or the previous use was over 15 minutes ago, don't load.
		if (previousUseTime == null || System.currentTimeMillis() - previousUseTime >= 7L * 24 * 60 * 60 * 1000)
			return true;
		
		String previousSolutionPath = Settings.getSettings().getString(Settings.PREVIOUS_SOLUTION_PATH);
		String previousDataPath = Settings.getSettings().getString(Settings.PREVIOUS_DATA_PATH);
		String previousLanguageString = Settings.getSettings().getString(Settings.PREVIOUS_LANGUAGE);
		String previousTimeLimitString = Settings.getSettings().getString(Settings.PREVIOUS_TIME_LIMIT);
		String previousCheckerString = Settings.getSettings().getString(Settings.PREVIOUS_CHECKER);
		Boolean previousTokenCheckerCaseSensitive =
				Settings.getSettings().getBoolean(Settings.PREVIOUS_TOKEN_CHECKER_CASE_SENSITIVE);
		Boolean previousDiffCheckerIgnoreTrailingWhitespace =
				Settings.getSettings().getBoolean(Settings.PREVIOUS_DIFF_CHECKER_IGNORE_TRAILING_WHITESPACE);
		Boolean previousDiffCheckerIgnoreTrailingBlanklines =
				Settings.getSettings().getBoolean(Settings.PREVIOUS_DIFF_CHECKER_IGNORE_TRAILING_BLANKLINES);
		String previousEpsilonCheckerAbsoluteEpsilon =
				Settings.getSettings().getString(Settings.PREVIOUS_EPSILON_CHECKER_ABSOLUTE_EPSILON);
		String previousEpsilonCheckerRelativeEpsilon =
				Settings.getSettings().getString(Settings.PREVIOUS_EPSILON_CHECKER_RELATIVE_EPSILON);
		String previousDataRegex = Settings.getSettings().getString(Settings.PREVIOUS_DATA_REGEX);
		
		// If we don't have a previous solution path or data path, return.
		if (previousSolutionPath == null || previousDataPath == null)
			return false;
		
		File previousSolutionFile = new File(previousSolutionPath);
		File previousDataFolder = new File(previousDataPath);
		
		// If either the solution file or data folder don't exist, return.
		if (!previousSolutionFile.exists() || !previousDataFolder.isDirectory())
			return false;
		if (!Veris.isValidSolutionFile(previousSolutionFile))
			return false;
		
		setSolutionFile(previousSolutionFile);
		setDataFolder(previousDataFolder);
		
		// Load all other settings we have.
		if (choiceBoxLanguage != null && previousLanguageString != null) {
			choiceBoxLanguage.getSelectionModel().select(previousLanguageString);
		}
		if (textFieldTimeLimit != null && previousTimeLimitString != null) {
			textFieldTimeLimit.setText(previousTimeLimitString);
		}
		if (choiceBoxChecker != null && previousCheckerString != null) {
			choiceBoxChecker.getSelectionModel().select(previousCheckerString);
		}
		if (checkBoxTokenCheckerCaseSensitive != null && previousTokenCheckerCaseSensitive != null) {
			checkBoxTokenCheckerCaseSensitive.setSelected(previousTokenCheckerCaseSensitive);
		}
		if (checkBoxDiffCheckerIgnoreTrailingWhitespace != null && previousDiffCheckerIgnoreTrailingWhitespace != null) {
			checkBoxDiffCheckerIgnoreTrailingWhitespace.setSelected(previousDiffCheckerIgnoreTrailingWhitespace);
		}
		if (checkBoxDiffCheckerIgnoreTrailingBlankLines != null && previousDiffCheckerIgnoreTrailingBlanklines != null) {
			checkBoxDiffCheckerIgnoreTrailingBlankLines.setSelected(previousDiffCheckerIgnoreTrailingBlanklines);
		}
		if (textFieldEpsilonCheckerAbsoluteEpsilon != null && previousEpsilonCheckerAbsoluteEpsilon != null) {
			textFieldEpsilonCheckerAbsoluteEpsilon.setText(previousEpsilonCheckerAbsoluteEpsilon);
		}
		if (textFieldEpsilonCheckerRelativeEpsilon != null && previousEpsilonCheckerRelativeEpsilon != null) {
			textFieldEpsilonCheckerRelativeEpsilon.setText(previousEpsilonCheckerRelativeEpsilon);
		}
		if (textFieldDataRegex != null && previousDataRegex != null) {
			textFieldDataRegex.setText(previousDataRegex);
		}
		
		return true;
	}
	
	private boolean updatePreviousUseTime() {
		Settings.getSettings().set(Settings.PREVIOUS_USE_TIME, System.currentTimeMillis());
		return Settings.saveSettings();
	}
	
	private boolean clearPrevious() {
		Settings.getSettings().clear(Settings.PREVIOUS_SOLUTION_PATH);
		Settings.getSettings().clear(Settings.PREVIOUS_DATA_PATH);
		Settings.getSettings().clear(Settings.PREVIOUS_LANGUAGE);
		Settings.getSettings().clear(Settings.PREVIOUS_TIME_LIMIT);
		Settings.getSettings().clear(Settings.PREVIOUS_CHECKER);
		Settings.getSettings().clear(Settings.PREVIOUS_TOKEN_CHECKER_CASE_SENSITIVE);
		Settings.getSettings().clear(Settings.PREVIOUS_DIFF_CHECKER_IGNORE_TRAILING_WHITESPACE);
		Settings.getSettings().clear(Settings.PREVIOUS_DIFF_CHECKER_IGNORE_TRAILING_BLANKLINES);
		Settings.getSettings().clear(Settings.PREVIOUS_EPSILON_CHECKER_ABSOLUTE_EPSILON);
		Settings.getSettings().clear(Settings.PREVIOUS_EPSILON_CHECKER_RELATIVE_EPSILON);
		Settings.getSettings().clear(Settings.PREVIOUS_DATA_REGEX);
		return Settings.saveSettings();
	}
	
	private boolean savePrevious() {
		// If we shouldn't remember the judging settings, clear the settings then return.
		if (!Settings.getSettings().getBooleanOrDefault(Settings.REMEMBER_JUDGING_SETTINGS, true)) {
			clearPrevious();
			return true;
		}
		
		File solutionFile = verisBuilder.getSolutionFile();
		String solutionPath = solutionFile == null ? null : solutionFile.getAbsolutePath();
		
		File dataFolder = verisBuilder.getDataFolder();
		String dataPath = dataFolder == null ? null : dataFolder.getAbsolutePath();
		
		Settings.getSettings().set(Settings.PREVIOUS_SOLUTION_PATH, solutionPath);
		Settings.getSettings().set(Settings.PREVIOUS_DATA_PATH, dataPath);
		Settings.getSettings().set(Settings.PREVIOUS_LANGUAGE,
				choiceBoxLanguage == null ? null : choiceBoxLanguage.getValue());
		Settings.getSettings().set(Settings.PREVIOUS_TIME_LIMIT,
				textFieldTimeLimit == null ? null : textFieldTimeLimit.getText());
		Settings.getSettings().set(Settings.PREVIOUS_CHECKER,
				choiceBoxChecker == null ? null : choiceBoxChecker.getValue());
		Settings.getSettings().set(Settings.PREVIOUS_TOKEN_CHECKER_CASE_SENSITIVE,
				checkBoxTokenCheckerCaseSensitive == null ? null : checkBoxTokenCheckerCaseSensitive.isSelected());
		Settings.getSettings().set(Settings.PREVIOUS_DIFF_CHECKER_IGNORE_TRAILING_WHITESPACE,
				checkBoxDiffCheckerIgnoreTrailingWhitespace == null ? null
						: checkBoxDiffCheckerIgnoreTrailingWhitespace.isSelected());
		Settings.getSettings().set(Settings.PREVIOUS_DIFF_CHECKER_IGNORE_TRAILING_BLANKLINES,
				checkBoxDiffCheckerIgnoreTrailingBlankLines == null ? null
						: checkBoxDiffCheckerIgnoreTrailingBlankLines.isSelected());
		Settings.getSettings().set(Settings.PREVIOUS_EPSILON_CHECKER_ABSOLUTE_EPSILON,
				textFieldEpsilonCheckerAbsoluteEpsilon == null ? null
						: textFieldEpsilonCheckerAbsoluteEpsilon.getText());
		Settings.getSettings().set(Settings.PREVIOUS_EPSILON_CHECKER_RELATIVE_EPSILON,
				textFieldEpsilonCheckerRelativeEpsilon == null ? null
						: textFieldEpsilonCheckerRelativeEpsilon.getText());
		Settings.getSettings().set(Settings.PREVIOUS_DATA_REGEX,
				textFieldDataRegex == null ? null : textFieldDataRegex.getText());
		
		updatePreviousUseTime();
		
		return Settings.saveSettings();
	}
	
	@FXML
    protected void initialize() {
		textFieldTimeLimit.setText("" + Settings.getSettings().getDoubleOrDefault(
				Settings.DEFAULT_TIME_LIMIT, Veris.DEFAULT_TIME_LIMIT / 1000.0));
		
		initContextMenu();

		titledPaneAdvancedSettings.setAnimated(false);
		accordionAdvancedSettings.heightProperty().addListener((obs, oldHeight, newHeight) -> {
			stage.sizeToScene();
		} );
		
		titledPaneAdvancedSettings.expandedProperty().addListener((ov, b, b1) -> {
            titledPaneAdvancedSettings.requestLayout();
        });
		
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
		
		setUpInputValidation();
		updateJudgeButton();
	}
	
	private void setUpInputValidation() { 
        textFieldDataRegex.textProperty().addListener((o, ov, nv) -> validateDataRegex());
        textFieldTimeLimit.textProperty().addListener((o, ov, nv) -> validateTimeLimitString());
        validateDataRegex();
        validateTimeLimitString();
    }

    private void validateDataRegex() {
        ObservableList<String> styleClass = textFieldDataRegex.getStyleClass();
        if (!isDataRegexValid()) {
            if (!styleClass.contains("error")) {
                styleClass.add("error");
            }
        } else {
            // remove all occurrences:
            styleClass.removeAll(Collections.singleton("error"));                    
        }
        updateJudgeButton();
    }

    private void validateTimeLimitString() {
        ObservableList<String> styleClass = textFieldTimeLimit.getStyleClass();
        if (!isTimeLimitStringValid()) {
            if (!styleClass.contains("error")) {
                styleClass.add("error");
            }
        } else {
            // remove all occurrences:
            styleClass.removeAll(Collections.singleton("error"));                    
        }
        updateJudgeButton();
    }
	
	private void initContextMenu() {
		mainContextMenu.setOnShowing(new EventHandler<WindowEvent>() {
		    public void handle(WindowEvent e) {
		        // Do nothing.
		    }
		});
		mainContextMenu.setOnShown(new EventHandler<WindowEvent>() {
		    public void handle(WindowEvent e) {
		        // Do nothing.
		    }
		});

		MenuItem itemOpenSettings = new MenuItem(MESSAGES.getString("settings"));
		itemOpenSettings.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) {
		    	boolean status = SettingsController.createAndOpen();
	    		if (!status) {
	    			// TODO: show error message "Failed to open settings."
	    		}
		    }
		});
		
		MenuItem itemOpenConfigEditor = new MenuItem(MESSAGES.getString("edit_config"));
		itemOpenConfigEditor.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) {
		    	boolean status = ConfigEditorController.createAndOpen();
	    		if (!status) {
	    			// TODO: show error message "Failed to open config editor."
	    		}
		    }
		});
		
		mainContextMenu.getItems().addAll(
				itemOpenSettings,
				itemOpenConfigEditor
		);
		
		borderPaneMain.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				if (e.getButton() == MouseButton.SECONDARY) {
					mainContextMenu.show(borderPaneMain, e.getScreenX(), e.getScreenY());
				} else {
					mainContextMenu.hide();
				}
			}
		});
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
    	fileChooser.setTitle(MESSAGES.getString("select_solution_file"));
    	fileChooser.getExtensionFilters().add(Config.getConfig().getExtensionFilterFromLanguageSpecs());
    	File file = fileChooser.showOpenDialog(stage);
    	if (file != null && Veris.isValidSolutionFile(file)) {
    		setSolutionFile(file);
    	}
		event.consume();
	}

    @FXML protected void handleDataPathButtonAction(ActionEvent event) {
    	DirectoryChooser directoryChooser = new DirectoryChooser();
    	directoryChooser.setTitle(MESSAGES.getString("select_data_folder"));
    	if (verisBuilder.getDataFolder() != null)
    		directoryChooser.setInitialDirectory(verisBuilder.getDataFolder().getParentFile());
    	File file = directoryChooser.showDialog(stage);
    	if (file != null && file.isDirectory()) {
    		setDataFolder(file);
    	}
		event.consume();
	}
    
	@FXML protected void handleJudgeButtonAction(ActionEvent event) {
		judge();
		
		event.consume();
	}
	
	private void judge() {
		setTimeLimit(getTimeLimit());
		setChecker(getSelectedChecker());
		setLanguageSpec(getSelectedLanguageSpec());
		verisBuilder.setSortCasesBySize(
				Settings.getSettings().getBooleanOrDefault(
						Settings.SORT_CASES_BY_SIZE,
						Veris.DEFAULT_SORT_CASES_BY_SIZE));
		verisBuilder.setStopAtFirstNonCorrectVerdict(
				Settings.getSettings().getBooleanOrDefault(
						Settings.STOP_AT_FIRST_NON_CORRECT_VERDICT,
						Veris.DEFAULT_STOP_AT_FIRST_NON_CORRECT_VERDICT));
		verisBuilder.setDataRegex(getDataRegex());
		
		boolean status = ResultsController.createAndJudge(verisBuilder);
		if (status) {
			savePrevious();
		} else {
			// TODO: Show error message.
		}
	}
	
	private long getTimeLimit() {
		try {
			return Math.round(Math.ceil(1000 * Double.parseDouble(getTimeLimitString())));
		} catch (NumberFormatException e) {
			return Veris.DEFAULT_TIME_LIMIT;
		}
	}
	
	private String getTimeLimitString() {
		return textFieldTimeLimit.getText();
	}
	
	private boolean isTimeLimitStringValid() {
		try {
			Double.parseDouble(getTimeLimitString());
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	private String getDataRegex() {
		String dataRegex = textFieldDataRegex.getText();
		return dataRegex.isEmpty() ? null : dataRegex;
	}
	
	private boolean isDataRegexValid() {
		String dataRegex = getDataRegex();
        if (dataRegex != null) {
	        try {
	        	Pattern.compile(dataRegex);
	        } catch (PatternSyntaxException e) {
	        	return false;
	        }
        }
        return true;
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

	private void setTimeLimit(long timeLimit) {
		timeLimit = Math.max(timeLimit, Veris.MINIMUM_TIME_LIMIT);
		timeLimit = Math.min(timeLimit, Veris.MAXIMUM_TIME_LIMIT);
		verisBuilder.setTimeLimit(timeLimit);
	}

	private void setSolutionFile(File solutionFile) {
		verisBuilder.setSolutionFile(solutionFile);
		if (solutionFile != null) {
			buttonSolution.setText(solutionFile.getPath());
			buttonSolution.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
			stage.setTitle(MESSAGES.getString("main_title") + " - " + solutionFile.getName());
		} else {
			buttonSolution.setText("?");
			stage.setTitle(MESSAGES.getString("main_title") + " - ?");
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
		textFieldDataRegex.clear();
		updateJudgeButton();
	}
	
	private void updateJudgeButton() {
		boolean isReady = !isJudging && verisBuilder != null && verisBuilder.isReady() && isTimeLimitStringValid() && isDataRegexValid();
		buttonJudge.setDisable(!isReady);
	}
}
