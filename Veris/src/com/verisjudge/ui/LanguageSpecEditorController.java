package com.verisjudge.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import com.verisjudge.Config;
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
	
	public static boolean createAndOpen() {
		try {
			FXMLLoader loader = new FXMLLoader(LanguageSpecEditorController.class.getResource("/fxml/languageSpecEditor.fxml"));
			Parent root = (Parent) loader.load();
			LanguageSpecEditorController controller = (LanguageSpecEditorController) loader.getController();
			Stage stage = new Stage();
			
	        Scene scene = new Scene(root);

	        stage.setTitle("Edit Language Spec");
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
		loadFromLanguageSpec(Config.getConfig().getLanguageSpecs()[0]);
		
		setUpInputValidation();
	}
	
	private void setUpInputValidation() {
		textFieldLanguageName.textProperty().addListener((o, ov, nv) -> validateLanguageName());
		textFieldFileExtensions.textProperty().addListener((o, ov, nv) -> validateFileExtensions());
		textAreaCompileCommand.textProperty().addListener((o, ov, nv) -> validateCompileCommand());
		textAreaExecutionCommand.textProperty().addListener((o, ov, nv) -> validateExecutionCommand());
		checkBoxNeedsCompile.selectedProperty().addListener((o, ov, nv) -> validateCompileCommand());
		textFieldDetectLanguagePriority.textProperty().addListener((o, ov, nv) -> validateDetectLanguagePriority());
		validateLanguageName();
		validateFileExtensions();
		validateCompileCommand();
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
		System.err.println(getFileExtensions().length + "  " + Arrays.toString(getFileExtensions()));
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
		event.consume();
	}
	
	@FXML protected void handleSaveButtonAction(ActionEvent event) {
		System.err.println(createLanguageSpec());
		
		event.consume();
	}
	
	private LanguageSpec createLanguageSpec() {
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
		return !getNeedsCompile() || getCompileArgs().length > 0;
	}
	
	private String[] getCompileArgs() {
		return LanguageSpec.convertStringToArgsList(textAreaCompileCommand.getText()).toArray(new String[0]);
	}

	private boolean isExecutionCommandValid() {
		return getExecutionArgs().length > 0;
	}

	private String[] getExecutionArgs() {
		return LanguageSpec.convertStringToArgsList(textAreaExecutionCommand.getText()).toArray(new String[0]);
	}
	
	private boolean isFileExtensionsValid() {
		return getFileExtensions().length > 0;
	}

	private String[] getFileExtensions() {
		return Arrays.stream(textFieldFileExtensions.getText().split("(\\s|,)+"))
				.map(a -> a.replace(",", "").replace(" ", ""))
				.filter(a -> !a.isEmpty())
				.toArray(String[]::new);
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
		textFieldFileExtensions.setText(Arrays.stream(languageSpec.getFileExtensions()).collect(Collectors.joining(", ")));
		textFieldDetectLanguagePriority.setText("" + languageSpec.getDetectLanguagePriority());

		textAreaCompileCommand.setText(Arrays.stream(languageSpec.getCompileArgs()).collect(Collectors.joining(" ")));
		textAreaExecutionCommand.setText(Arrays.stream(languageSpec.getExecutionArgs()).collect(Collectors.joining(" ")));

		checkBoxIsAllowed.setSelected(languageSpec.isAllowed());
		checkBoxNeedsCompile.setSelected(languageSpec.needsCompile());
	}

}
