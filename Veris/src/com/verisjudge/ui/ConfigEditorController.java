package com.verisjudge.ui;

import com.verisjudge.Config;
import com.verisjudge.Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

public class ConfigEditorController {
	@FXML private Button buttonSave;
	
	@FXML private Accordion mainAccordion;
	
	@FXML private TextField textFieldDefaultTimeLimit;
	@FXML private TextField textFieldMinimumTimeLimit;
	@FXML private TextField textFieldMaximumTimeLimit;

	@FXML private TextField textFieldMaximumIdleTime;
	@FXML private TextField textFieldCompileTimeLimit;
	
	@FXML private TextField textFieldInputFileTypes;
	@FXML private TextField textFieldOutputFileTypes;

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
		} );
		for (TitledPane titledPane : mainAccordion.getPanes()) {
			titledPane.setAnimated(false);
			titledPane.expandedProperty().addListener((ov, b, b1) -> {
				titledPane.requestLayout();
	        });
		}
		loadDefaults();
	}

	@FXML protected void handleSaveButtonAction(ActionEvent event) {
		event.consume();
	}
	
	private void loadDefaults() {
		Config config = Config.getConfig();

		if (textFieldDefaultTimeLimit != null && config.hasDefaultTimeLimit())
			textFieldDefaultTimeLimit.setText(config.getDefaultTimeLimit() + " ms");
		
		if (textFieldMaximumTimeLimit != null && config.hasMaximumTimeLimit())
			textFieldMaximumTimeLimit.setText(config.getMaximumTimeLimit() + " ms");
		
		if (textFieldMinimumTimeLimit != null && config.hasMinimumTimeLimit())
			textFieldMinimumTimeLimit.setText(config.getMinimumTimeLimit() + " ms");
		
		if (textFieldMaximumIdleTime != null && config.hasMaximumIdleTime())
			textFieldMaximumIdleTime.setText(config.getMaximumIdleTime() + " ms");
		
		if (textFieldCompileTimeLimit != null && config.hasCompileTimeLimit())
			textFieldCompileTimeLimit.setText(config.getCompileTimeLimit() + " ms");
	}

}
