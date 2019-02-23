package com.verisjudge.ui;

import java.io.File;

import com.verisjudge.Main;
import com.verisjudge.utils.FileUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class TextViewerController {
	public final static int DEFAULT_TEXT_VIEWER_WIDTH = 652;
	public final static int DEFAULT_TEXT_VIEWER_HEIGHT = 480;
	
	public final static long DEFAULT_MAX_TEXT_LENGTH = 1_000_000;
	
	@FXML private Label labelMainTitle;
	@FXML private TextArea mainTextArea;

	private Stage stage;
	
	public static boolean createAndOpenTextViewer(String title, String label, File file) {
		if (file == null)
			return false;
		String text = FileUtils.readLimitedFile(file, DEFAULT_MAX_TEXT_LENGTH);
		return createAndOpenTextViewer(title, label, text);
	}
	
	public static boolean createAndOpenTextViewer(String title, String label, String text) {
		try {
			FXMLLoader loader = new FXMLLoader(TextViewerController.class.getResource("/fxml/textViewer.fxml"));
			Parent root = (Parent) loader.load();
			TextViewerController controller = (TextViewerController) loader.getController();
			controller.setLabelText(label);
			controller.setText(text);
			
			Stage stage = new Stage();

	        Scene scene = new Scene(root, DEFAULT_TEXT_VIEWER_WIDTH, DEFAULT_TEXT_VIEWER_HEIGHT);

	        stage.setTitle(title);
	        stage.setScene(scene);
	        stage.setResizable(true);
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
		// Do nothing.
	}
	
	public void setLabelText(String labelText) {
		if (labelText == null)
			labelMainTitle.setText("");
		else
			labelMainTitle.setText(labelText);
	}
	
	public void setText(String text) {
		if (text == null)
			mainTextArea.clear();
		else
			mainTextArea.setText(text);
	}

}
