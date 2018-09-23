package com.verisjudge.ui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.verisjudge.Config;
import com.verisjudge.LanguageSpec;
import com.verisjudge.Main;

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
	@FXML private Button buttonSave;

	@FXML private TextField textFieldLanguageName;
	@FXML private TextField textFieldFileExtensions;
	@FXML private TextField textFieldAutoDetectLanguagePriority;

	@FXML private TextArea textAreaCompileCommand;
	@FXML private TextArea textAreaExecutionCommand;
	
	@FXML private CheckBox checkBoxIsAllowed;
	@FXML private CheckBox checkBoxNeedsCompile;

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
				.setIsAllowed(checkBoxIsAllowed.isSelected())
				.setNeedsCompile(checkBoxNeedsCompile.isSelected())
				.build();
	}
	
	private String[] getCompileArgs() {
		return LanguageSpec.convertStringToArgsList(textAreaCompileCommand.getText()).toArray(new String[0]);
	}

	private String[] getExecutionArgs() {
		return LanguageSpec.convertStringToArgsList(textAreaExecutionCommand.getText()).toArray(new String[0]);
	}

	private String[] getFileExtensions() {
		return textFieldFileExtensions.getText().split("(\\s|,)+");
	}
	
	private long getDetectLanguagePriority() {
		try {
			return Long.parseLong(textFieldAutoDetectLanguagePriority.getText());
		} catch (NumberFormatException e) {
			// TODO: Somehow use this error or don't use the result when we would get an error.
			e.printStackTrace();
			return 0;
		}
	}
	
	private void loadFromLanguageSpec(LanguageSpec languageSpec) {
		textFieldLanguageName.setText(languageSpec.getLanguageName());
		textFieldFileExtensions.setText(Arrays.stream(languageSpec.getFileExtensions()).collect(Collectors.joining(", ")));
		textFieldAutoDetectLanguagePriority.setText("" + languageSpec.getDetectLanguagePriority());

		textAreaCompileCommand.setText(Arrays.stream(languageSpec.getCompileArgs()).collect(Collectors.joining(" ")));
		textAreaExecutionCommand.setText(Arrays.stream(languageSpec.getExecutionArgs()).collect(Collectors.joining(" ")));

		checkBoxIsAllowed.setSelected(languageSpec.isAllowed());
		checkBoxNeedsCompile.setSelected(languageSpec.needsCompile());
	}

}
