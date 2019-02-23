package com.verisjudge.ui;

import java.util.Locale;
import java.util.ResourceBundle;

import com.verisjudge.Main;
import com.verisjudge.Settings;
import com.verisjudge.Veris;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

public class SettingsController {
	
	@FXML private Button buttonCancel;
	@FXML private Button buttonSave;
	
	@FXML private CheckBox checkBoxUseDarkTheme;
	@FXML private CheckBox checkBoxRememberJudgingSettings;
	@FXML private CheckBox checkBoxSortCasesBySize;
	@FXML private CheckBox checkBoxStopAtFirstNonCorrectVerdict;

	private final static ResourceBundle MESSAGES = ResourceBundle.getBundle("MessagesBundle", Locale.ENGLISH);

	private Stage stage;
	
	public static boolean createAndOpen() {
		try {
			FXMLLoader loader = new FXMLLoader(SettingsController.class.getResource("/fxml/settings.fxml"), MESSAGES);
			Parent root = (Parent) loader.load();
			SettingsController controller = (SettingsController) loader.getController();
			Stage stage = new Stage();
			
	        Scene scene = new Scene(root);

	        stage.setTitle(MESSAGES.getString("settings_title"));
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
		loadSettings();
	}

	@FXML protected void handleCancelButtonAction(ActionEvent event) {
		stage.close();
		event.consume();
	}
	
	@FXML protected void handleSaveButtonAction(ActionEvent event) {
		saveSettings();
		stage.close();
		event.consume();
	}
	
	private void loadSettings() {
		if (checkBoxUseDarkTheme != null)
			checkBoxUseDarkTheme.setSelected(
					Settings.getSettings().getBooleanOrDefault(
							Settings.USE_DARK_THEME, false));
		
		if (checkBoxRememberJudgingSettings != null)
			checkBoxRememberJudgingSettings.setSelected(
					Settings.getSettings().getBooleanOrDefault(
							Settings.REMEMBER_JUDGING_SETTINGS, true));
		
		if (checkBoxSortCasesBySize != null)
			checkBoxSortCasesBySize.setSelected(
					Settings.getSettings().getBooleanOrDefault(
							Settings.SORT_CASES_BY_SIZE, Veris.DEFAULT_SORT_CASES_BY_SIZE));
		
		if (checkBoxStopAtFirstNonCorrectVerdict != null)
			checkBoxStopAtFirstNonCorrectVerdict.setSelected(
					Settings.getSettings().getBooleanOrDefault(
							Settings.STOP_AT_FIRST_NON_CORRECT_VERDICT,
							Veris.DEFAULT_STOP_AT_FIRST_NON_CORRECT_VERDICT));
	}
	
	private boolean saveSettings() {
		if (checkBoxUseDarkTheme != null)
			Settings.getSettings().set(Settings.USE_DARK_THEME,
					checkBoxUseDarkTheme.isSelected());
		
		if (checkBoxRememberJudgingSettings != null)
			Settings.getSettings().set(Settings.REMEMBER_JUDGING_SETTINGS,
					checkBoxRememberJudgingSettings.isSelected());
		
		if (checkBoxSortCasesBySize != null)
			Settings.getSettings().set(Settings.SORT_CASES_BY_SIZE,
					checkBoxSortCasesBySize.isSelected());
		
		if (checkBoxStopAtFirstNonCorrectVerdict != null)
			Settings.getSettings().set(Settings.STOP_AT_FIRST_NON_CORRECT_VERDICT,
					checkBoxStopAtFirstNonCorrectVerdict.isSelected());
		
		boolean status = Settings.saveSettings();
		return status;
	}

}
