package com.verisjudge.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.verisjudge.LanguageSpec;
import com.verisjudge.Main;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LanguageSpecEditorController {

	@FXML private TextField textFieldLanguageName;
	@FXML private TextField textFieldFileExtensions;
	@FXML private TextField textFieldDetectLanguagePriority;

	@FXML private TextArea textAreaCompileCommand;
	@FXML private TextArea textAreaExecutionCommand;
	
	@FXML private CheckBox checkBoxIsAllowed;
	@FXML private CheckBox checkBoxNeedsCompile;
	
	@FXML private Button buttonCancel;
	@FXML private Button buttonSave;

	private Stage stage;
	private LanguageSpec originalLanguageSpec;
	private LanguageSpecEventHandler eventHandler;
	
	public static boolean createAndOpen(LanguageSpec originalLanguageSpec, LanguageSpecEventHandler eventHandler) {
		try {
			FXMLLoader loader = new FXMLLoader(LanguageSpecEditorController.class.getResource("/fxml/languageSpecEditor.fxml"));
			Parent root = (Parent) loader.load();
			LanguageSpecEditorController controller = (LanguageSpecEditorController) loader.getController();
			controller.setOriginalLanguageSpec(originalLanguageSpec);
			controller.setEventHandler(eventHandler);
			Stage stage = new Stage();
			
	        Scene scene = new Scene(root);

	        if (originalLanguageSpec == null) {
	        	stage.setTitle("Create New Language Spec");
	        } else {
	        	stage.setTitle("Edit - " + originalLanguageSpec.getLanguageName());
	        }
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
	
	private void setOriginalLanguageSpec(LanguageSpec originalLanguageSpec) {
		this.originalLanguageSpec = originalLanguageSpec;
		loadFromLanguageSpec(getOriginalLanguageSpec());
	}
	
	private LanguageSpec getOriginalLanguageSpec() {
		return originalLanguageSpec;
	}
	
	private void setEventHandler(LanguageSpecEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	@FXML
    protected void initialize() {
		setUpInputValidation();
		updateSaveButton();
	}
	
	private void setUpInputValidation() {
		textFieldLanguageName.textProperty().addListener((o, ov, nv) -> validateLanguageName());
		textFieldFileExtensions.textProperty().addListener((o, ov, nv) -> validateFileExtensions());
		textAreaCompileCommand.textProperty().addListener((o, ov, nv) -> validateCompileCommand());
		textAreaExecutionCommand.textProperty().addListener((o, ov, nv) -> validateExecutionCommand());
		checkBoxNeedsCompile.selectedProperty().addListener((o, ov, nv) ->
				{
					validateCompileCommand();
					updateTextAreaCompileCommand();
				});
		textFieldDetectLanguagePriority.textProperty().addListener((o, ov, nv) -> validateDetectLanguagePriority());
		validateLanguageName();
		validateFileExtensions();
		validateCompileCommand();
		updateTextAreaCompileCommand();
		validateExecutionCommand();
		validateDetectLanguagePriority();
	}
	
	private void validateLanguageName() {
		ObservableList<String> styleClass = textFieldLanguageName.getStyleClass();
        if (!isLanguageNameValid()) {
            if (!styleClass.contains("error")) {
                styleClass.add("error");
            }
        } else {
            // remove all occurrences:
            styleClass.removeAll(Collections.singleton("error"));                    
        }
        updateSaveButton();
	}
	
	private void validateFileExtensions() {
		ObservableList<String> styleClass = textFieldFileExtensions.getStyleClass();
		if (!isFileExtensionsValid()) {
            if (!styleClass.contains("error")) {
                styleClass.add("error");
            }
        } else {
            // remove all occurrences:
            styleClass.removeAll(Collections.singleton("error"));                    
        }
        updateSaveButton();
	}

	private void validateCompileCommand() {
		ObservableList<String> styleClass = textAreaCompileCommand.getStyleClass();
        if (!isCompileCommandValid()) {
            if (!styleClass.contains("error")) {
                styleClass.add("error");
            }
        } else {
            // remove all occurrences:
            styleClass.removeAll(Collections.singleton("error"));                    
        }
        updateSaveButton();
	}
	
	private void validateExecutionCommand() {
		ObservableList<String> styleClass = textAreaExecutionCommand.getStyleClass();
        if (!isExecutionCommandValid()) {
            if (!styleClass.contains("error")) {
                styleClass.add("error");
            }
        } else {
            // remove all occurrences:
            styleClass.removeAll(Collections.singleton("error"));                    
        }
        updateSaveButton();
	}

	private void validateDetectLanguagePriority() {
		ObservableList<String> styleClass = textFieldDetectLanguagePriority.getStyleClass();
        if (!isDetectLanguagePriorityValid()) {
            if (!styleClass.contains("error")) {
                styleClass.add("error");
            }
        } else {
            // remove all occurrences:
            styleClass.removeAll(Collections.singleton("error"));                    
        }
        updateSaveButton();
	}

	private void updateTextAreaCompileCommand() {
		textAreaCompileCommand.setDisable(!checkBoxNeedsCompile.isSelected());
	}

	private void updateSaveButton() {
		if (isLanguageNameValid()
				&& isFileExtensionsValid()
				&& isCompileCommandValid()
				&& isExecutionCommandValid()
				&& isDetectLanguagePriorityValid()) {
			buttonSave.setDisable(false);
		} else {
			buttonSave.setDisable(true);
		}
	}

	@FXML protected void handleCancelButtonAction(ActionEvent event) {
		if (eventHandler != null) {
			eventHandler.handleLanguageSpecEditorCancel(getOriginalLanguageSpec());
		}
		stage.close();
		event.consume();
	}
	
	@FXML protected void handleSaveButtonAction(ActionEvent event) {
		if (eventHandler != null) {
			eventHandler.handleLanguageSpecEditorSave(getOriginalLanguageSpec(), getEditedLanguageSpec());
		}
		stage.close();
		event.consume();
	}
	
	private LanguageSpec getEditedLanguageSpec() {
		return new LanguageSpec.Builder()
				.setLanguageName(textFieldLanguageName.getText())
				.setFileExtensions(getFileExtensions())
				.setCompileArgs(getCompileArgs())
				.setExecutionArgs(getExecutionArgs())
				.setDetectLanguagePriority(getDetectLanguagePriority())
				.setIsAllowed(getIsAllowed())
				.setNeedsCompile(getNeedsCompile())
				.build();
	}
	
	private boolean isLanguageNameValid() {
		return !getLanguageName().isEmpty();
	}

	private String getLanguageName() {
		return textFieldLanguageName.getText();
	}
	
	private boolean getIsAllowed() {
		return checkBoxIsAllowed.isSelected();
	}
	
	private boolean getNeedsCompile() {
		return checkBoxNeedsCompile.isSelected();
	}

	private boolean isCompileCommandValid() {
		return !getNeedsCompile() || !getCompileArgs().isEmpty();
	}
	
	private List<String> getCompileArgs() {
		return LanguageSpec.convertStringToArgsList(textAreaCompileCommand.getText());
	}

	private boolean isExecutionCommandValid() {
		return !getExecutionArgs().isEmpty();
	}

	private List<String> getExecutionArgs() {
		return LanguageSpec.convertStringToArgsList(textAreaExecutionCommand.getText());
	}
	
	private boolean isFileExtensionsValid() {
		return !getFileExtensions().isEmpty();
	}

	private List<String> getFileExtensions() {
		return Arrays.stream(textFieldFileExtensions.getText().split("(\\s|,)+"))
				.map(a -> a.replace(",", "").replace(" ", ""))
				.filter(a -> !a.isEmpty())
				.collect(Collectors.toList());
	}
	
	private boolean isDetectLanguagePriorityValid() {
		try {
			Long.parseLong(textFieldDetectLanguagePriority.getText());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private long getDetectLanguagePriority() {
		try {
			return Long.parseLong(textFieldDetectLanguagePriority.getText());
		} catch (NumberFormatException e) {
			return 1L;
		}
	}
	
	private void loadFromLanguageSpec(LanguageSpec languageSpec) {
		textFieldLanguageName.setText(languageSpec.getLanguageName());
		textFieldFileExtensions.setText(languageSpec.getFileExtensions().stream().collect(Collectors.joining(", ")));
		textFieldDetectLanguagePriority.setText("" + languageSpec.getDetectLanguagePriority());

		textAreaCompileCommand.setText(languageSpec.getCompileArgs().stream().collect(Collectors.joining(" ")));
		textAreaExecutionCommand.setText(languageSpec.getExecutionArgs().stream().collect(Collectors.joining(" ")));

		checkBoxIsAllowed.setSelected(languageSpec.isAllowed());
		checkBoxNeedsCompile.setSelected(languageSpec.needsCompile());
	}
	
	public static interface LanguageSpecEventHandler {
		public void handleLanguageSpecEditorSave(LanguageSpec originalLanguageSpec, LanguageSpec editedLanguageSpec);
		public void handleLanguageSpecEditorCancel(LanguageSpec originalLanguageSpec);
	}

}
