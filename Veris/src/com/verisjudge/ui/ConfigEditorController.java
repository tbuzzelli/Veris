package com.verisjudge.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import com.verisjudge.Config;
import com.verisjudge.Main;
import com.verisjudge.utils.ParsingUtils;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConfigEditorController {

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
	
	private Long getDefaultTimeLimit() {
		return ParsingUtils.parseTime(textFieldDefaultTimeLimit.getText());
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
	
	private Long getMinimumTimeLimit() {
		return ParsingUtils.parseTime(textFieldMinimumTimeLimit.getText());
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
	
	private Long getMaximumTimeLimit() {
		return ParsingUtils.parseTime(textFieldMaximumTimeLimit.getText());
	}
	
	private boolean isMaximumIdleTimeValid() {
		Long maximumIdleTime = getMaximumIdleTime();
		return maximumIdleTime != null && maximumIdleTime >= 0;
	}
	
	private Long getMaximumIdleTime() {
		return ParsingUtils.parseTime(textFieldMaximumIdleTime.getText());
	}
	
	private boolean isCompileTimeLimitValid() {
		Long compileTimeLimit = getCompileTimeLimit();
		return compileTimeLimit != null && compileTimeLimit > 0;
	}
	
	private Long getCompileTimeLimit() {
		return ParsingUtils.parseTime(textFieldCompileTimeLimit.getText());
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
	}

}
