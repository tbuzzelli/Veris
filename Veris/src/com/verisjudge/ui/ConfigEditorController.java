package com.verisjudge.ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.verisjudge.Config;
import com.verisjudge.LanguageSpec;
import com.verisjudge.Main;
import com.verisjudge.ui.LanguageSpecEditorController.LanguageSpecEventHandler;
import com.verisjudge.utils.ParsingUtils;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConfigEditorController implements LanguageSpecEventHandler {

	@FXML private Button buttonCancel;
	@FXML private Button buttonSave;
	
	@FXML private Accordion mainAccordion;
	
	@FXML private TextField textFieldDefaultTimeLimit;
	@FXML private TextField textFieldMinimumTimeLimit;
	@FXML private TextField textFieldMaximumTimeLimit;

	@FXML private TextField textFieldMaximumIdleTime;
	@FXML private TextField textFieldCompileTimeLimit;
	
	@FXML private TextField textFieldInputFileExtensions;
	@FXML private TextField textFieldOutputFileExtensions;

	@FXML private VBox vBoxLanguageSpecs;
	
	private Stage stage;
	
	public static boolean createAndOpen() {
		try {
			FXMLLoader loader = new FXMLLoader(ConfigEditorController.class.getResource("/fxml/configEditor.fxml"));
			Parent root = (Parent) loader.load();
			ConfigEditorController controller = (ConfigEditorController) loader.getController();
			Stage stage = new Stage();
			
	        Scene scene = new Scene(root);

	        stage.setTitle("Config");
	        stage.setScene(scene);
	        stage.setResizable(false);
	        Main.addIconToStage(stage);
	        
	        controller.setStage(stage);
	        stage.show();
	        
	        return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
		Main.updateTheme(this.stage);
	}
	
	@FXML
    protected void initialize() {
		mainAccordion.heightProperty().addListener((obs, oldHeight, newHeight) -> {
			stage.sizeToScene();
			for (TitledPane titledPane : mainAccordion.getPanes()) {
				titledPane.requestLayout();
			}
		} );
		for (TitledPane titledPane : mainAccordion.getPanes()) {
			titledPane.setAnimated(false);
			titledPane.expandedProperty().addListener((ov, b, b1) -> {
				titledPane.requestLayout();
	        });
		}
		loadDefaults();
		
		setUpInputValidation();
		updateSaveButton();
	}

	private void setUpInputValidation() {
		textFieldDefaultTimeLimit.textProperty().addListener((o, ov, nv) -> validateDefaultTimeLimit());
		textFieldMinimumTimeLimit.textProperty().addListener((o, ov, nv) -> 
				{
					validateDefaultTimeLimit();
					validateMinimumTimeLimit();
					validateMaximumTimeLimit();
				});
		textFieldMaximumTimeLimit.textProperty().addListener((o, ov, nv) -> 
				{
					validateDefaultTimeLimit();
					validateMinimumTimeLimit();
					validateMaximumTimeLimit();
				});
		textFieldMaximumIdleTime.textProperty().addListener((o, ov, nv) -> validateMaximumIdleTime());
		textFieldCompileTimeLimit.textProperty().addListener((o, ov, nv) -> validateCompileTimeLimit());
		validateDefaultTimeLimit();
		validateMinimumTimeLimit();
		validateMaximumTimeLimit();
		validateMaximumIdleTime();
		validateCompileTimeLimit();
	}
	
	private void validateDefaultTimeLimit() {
		ObservableList<String> styleClass = textFieldDefaultTimeLimit.getStyleClass();
        if (!isDefaultTimeLimitValid()) {
            if (!styleClass.contains("error")) {
                styleClass.add("error");
            }
        } else {
            // remove all occurrences:
            styleClass.removeAll(Collections.singleton("error"));                    
        }
        updateSaveButton();
	}
	
	private void validateMinimumTimeLimit() {
		ObservableList<String> styleClass = textFieldMinimumTimeLimit.getStyleClass();
        if (!isMinimumTimeLimitValid()) {
            if (!styleClass.contains("error")) {
                styleClass.add("error");
            }
        } else {
            // remove all occurrences:
            styleClass.removeAll(Collections.singleton("error"));                    
        }
        updateSaveButton();
	}
	
	private void validateMaximumTimeLimit() {
		ObservableList<String> styleClass = textFieldMaximumTimeLimit.getStyleClass();
        if (!isMaximumTimeLimitValid()) {
            if (!styleClass.contains("error")) {
                styleClass.add("error");
            }
        } else {
            // remove all occurrences:
            styleClass.removeAll(Collections.singleton("error"));                    
        }
        updateSaveButton();
	}
	
	private void validateMaximumIdleTime() {
		ObservableList<String> styleClass = textFieldMaximumIdleTime.getStyleClass();
        if (!isMaximumIdleTimeValid()) {
            if (!styleClass.contains("error")) {
                styleClass.add("error");
            }
        } else {
            // remove all occurrences:
            styleClass.removeAll(Collections.singleton("error"));                    
        }
        updateSaveButton();
	}
	
	private void validateCompileTimeLimit() {
		ObservableList<String> styleClass = textFieldCompileTimeLimit.getStyleClass();
        if (!isCompileTimeLimitValid()) {
            if (!styleClass.contains("error")) {
                styleClass.add("error");
            }
        } else {
            // remove all occurrences:
            styleClass.removeAll(Collections.singleton("error"));                    
        }
        updateSaveButton();
	}

	@FXML protected void handleCancelButtonAction(ActionEvent event) {
		stage.close();
		event.consume();
	}
	
	@FXML protected void handleSaveButtonAction(ActionEvent event) {
		System.err.println(createConfig().toJsonString());
		event.consume();
	}
	
	private void updateSaveButton() {
		if (isDefaultTimeLimitValid()
				&& isMinimumTimeLimitValid()
				&& isMaximumTimeLimitValid()
				&& isMaximumIdleTimeValid()
				&& isCompileTimeLimitValid()) {
			buttonSave.setDisable(false);
		} else {
			buttonSave.setDisable(true);
		}
	}
	
	private boolean isDefaultTimeLimitValid() {
		Long defaultTimeLimit = getDefaultTimeLimit();
		if (defaultTimeLimit == null || defaultTimeLimit < 0) {
			return false;
		}
		Long minimumTimeLimit = getMinimumTimeLimit();
		if (minimumTimeLimit != null && minimumTimeLimit > defaultTimeLimit) {
			return false;
		}
		Long maximumTimeLimit = getMaximumTimeLimit();
		if (maximumTimeLimit != null && maximumTimeLimit < defaultTimeLimit) {
			return false;
		}
		return true;
	}
	
	private String getDefaultTimeLimitString() {
		return textFieldDefaultTimeLimit.getText();
	}

	private Long getDefaultTimeLimit() {
		return ParsingUtils.parseTime(getDefaultTimeLimitString());
	}
	
	private boolean isMinimumTimeLimitValid() {
		Long minimumTimeLimit = getMinimumTimeLimit();
		if (minimumTimeLimit == null || minimumTimeLimit < 0) {
			return false;
		}
		Long maximumTimeLimit = getMaximumTimeLimit();
		if (maximumTimeLimit != null && maximumTimeLimit < minimumTimeLimit) {
			return false;
		}
		return true;
	}
	
	private String getMinimumTimeLimitString() {
		return textFieldMinimumTimeLimit.getText();
	}
	
	private Long getMinimumTimeLimit() {
		return ParsingUtils.parseTime(getMinimumTimeLimitString());
	}
	
	private boolean isMaximumTimeLimitValid() {
		Long maximumTimeLimit = getMaximumTimeLimit();
		if (maximumTimeLimit == null || maximumTimeLimit < 0) {
			return false;
		}
		Long minimumTimeLimit = getMinimumTimeLimit();
		if (minimumTimeLimit != null && minimumTimeLimit > maximumTimeLimit) {
			return false;
		}
		return true;
	}
	
	private String getMaximumTimeLimitString() {
		return textFieldMaximumTimeLimit.getText();
	}
	
	private Long getMaximumTimeLimit() {
		return ParsingUtils.parseTime(getMaximumTimeLimitString());
	}
	
	private boolean isMaximumIdleTimeValid() {
		Long maximumIdleTime = getMaximumIdleTime();
		return maximumIdleTime != null && maximumIdleTime >= 0;
	}
	
	private String getMaximumIdleTimeString() {
		return textFieldMaximumIdleTime.getText();
	}
	
	private Long getMaximumIdleTime() {
		return ParsingUtils.parseTime(getMaximumIdleTimeString());
	}
	
	private boolean isCompileTimeLimitValid() {
		Long compileTimeLimit = getCompileTimeLimit();
		return compileTimeLimit != null && compileTimeLimit > 0;
	}
	
	private String getCompileTimeLimitString() {
		return textFieldCompileTimeLimit.getText();
	}
	
	private Long getCompileTimeLimit() {
		return ParsingUtils.parseTime(getCompileTimeLimitString());
	}
	
	private String[] getInputFileExtensions() {
		return Arrays.stream(textFieldInputFileExtensions.getText().split("(\\s|,)+"))
				.map(a -> a.replace(",", "").replace(" ", ""))
				.filter(a -> !a.isEmpty())
				.toArray(String[]::new);
	}
	
	private String[] getOutputFileExtensions() {
		return Arrays.stream(textFieldOutputFileExtensions.getText().split("(\\s|,)+"))
				.map(a -> a.replace(",", "").replace(" ", ""))
				.filter(a -> !a.isEmpty())
				.toArray(String[]::new);
	}
	
	private Config createConfig() {
		return new Config.Builder()
				.setInputFileTypes(getInputFileExtensions())
				.setOutputFileTypes(getOutputFileExtensions())
				.setDefaultTimeLimitString(getDefaultTimeLimitString())
				.setMinimumTimeLimitString(getMinimumTimeLimitString())
				.setMaximumTimeLimitString(getMaximumTimeLimitString())
				.setMaximumIdleTimeString(getMaximumIdleTimeString())
				.setCompileTimeLimitString(getCompileTimeLimitString())
				.setLanguageSpecs(new LanguageSpec[0])
				.build();
	}
	
	@Override
	public void handleLanguageSpecEditorSave(LanguageSpec originalLanguageSpec, LanguageSpec editedLanguageSpec) {
		if (originalLanguageSpec == null) {
			addLanguageSpec(editedLanguageSpec);
		}  else {
			replaceLanguageSpec(originalLanguageSpec, editedLanguageSpec);
		}
	}
	
	@Override
	public void handleLanguageSpecEditorCancel(LanguageSpec originalLanguageSpec) {
		// Do nothing.
	}
	
	private void addLanguageSpec(LanguageSpec languageSpec) {
		vBoxLanguageSpecs.getChildren().add(createLanguageSpecPane(languageSpec));
	}
	
	private void openLanguageSpecEditor(LanguageSpec languageSpec) {
		LanguageSpecEditorController.createAndOpen(languageSpec, this);
	}
	
	private void deleteLanguageSpec(LanguageSpec languageSpec) {
		vBoxLanguageSpecs.getChildren().removeIf(a -> languageSpec.equals(a.getUserData()));
	}
	
	private void replaceLanguageSpec(LanguageSpec originalLanguageSpec, LanguageSpec editedLanguageSpec) {
		List<Node> nodes = vBoxLanguageSpecs.getChildren().filtered(a -> originalLanguageSpec.equals(a.getUserData()));
		Node node = nodes.isEmpty() ? null : nodes.get(0);
		// If this language spec was already deleted, just return.
		if (node == null) {
			return;
		}
		int index = vBoxLanguageSpecs.getChildren().indexOf(node);
		vBoxLanguageSpecs.getChildren().set(index, createLanguageSpecPane(editedLanguageSpec));
	}
	
	private Pane createLanguageSpecPane(LanguageSpec languageSpec) {
		Pane pane;
		try {
			pane = FXMLLoader.load(getClass().getResource("/fxml/languageSpec.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		pane.setUserData(languageSpec);
		
		Label labelLanguageName = (Label) pane.lookup("#labelLanguageName");
		labelLanguageName.setText(languageSpec.getLanguageName());

		MenuButton menuButtonOptions = (MenuButton) pane.lookup("#menuButtonOptions");

		MenuItem menuItemEdit = menuButtonOptions.getItems().stream()
				.filter(item -> item.getId().equals("menuItemEdit"))
				.findFirst()
				.get();
		menuItemEdit.setOnAction(e ->
			{
				openLanguageSpecEditor(languageSpec);
				e.consume();
			}
		);

		MenuItem menuItemDelete = menuButtonOptions.getItems().stream()
				.filter(item -> item.getId().equals("menuItemDelete"))
				.findFirst()
				.get();
		menuItemDelete.setOnAction(e ->
			{
				deleteLanguageSpec(languageSpec);
				e.consume();
			}
		);

		return pane;
	}
	
	private void loadDefaults() {
		Config config = Config.getConfig();

		if (config.hasDefaultTimeLimitString())
			textFieldDefaultTimeLimit.setText(config.getDefaultTimeLimitString());
		
		if (config.hasMaximumTimeLimitString())
			textFieldMaximumTimeLimit.setText(config.getMaximumTimeLimitString());
		
		if (config.hasMinimumTimeLimitString())
			textFieldMinimumTimeLimit.setText(config.getMinimumTimeLimitString());
		
		if (config.hasMaximumIdleTimeString())
			textFieldMaximumIdleTime.setText(config.getMaximumIdleTimeString());
		
		if (config.hasCompileTimeLimitString())
			textFieldCompileTimeLimit.setText(config.getCompileTimeLimitString());
	
		textFieldInputFileExtensions.setText(
				Arrays.stream(config.getInputFileTypes())
				.collect(Collectors.joining(", "))
		);
		textFieldOutputFileExtensions.setText(
				Arrays.stream(config.getOutputFileTypes())
				.collect(Collectors.joining(", "))
		);
		
		for (LanguageSpec languageSpec : config.getLanguageSpecs()) {
			addLanguageSpec(languageSpec);
		}
	}

}
