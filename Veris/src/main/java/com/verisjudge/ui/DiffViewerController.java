package com.verisjudge.ui;

import java.io.File;
import java.util.List;

import difflib.DiffRow;
import difflib.DiffRowGenerator;
import com.verisjudge.Main;
import com.verisjudge.utils.FileUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class DiffViewerController {
	public final static int DEFAULT_DIFF_VIEWER_WIDTH = 652;
	public final static int DEFAULT_DIFF_VIEWER_HEIGHT = 480;
	
	public final static long DEFAULT_MAX_TEXT_LENGTH = 1_000_000;
	
	@FXML private Label labelMainTitle;
	@FXML private WebView mainWebView;

	private Stage stage;
	
	public static boolean createAndOpenDiffViewer(String title, String label, File originalFile, File revisedFile) {
		if (originalFile == null || revisedFile == null)
			return false;
		List<String> original = FileUtils.fileToLines(originalFile);
		List<String> revised = FileUtils.fileToLines(revisedFile);
		return createAndOpenDiffViewer(title, label, original, revised);
	}
	
	public static boolean createAndOpenDiffViewer(String title, String label, List<String> original, List<String> revised) {
		try {
			FXMLLoader loader = new FXMLLoader(DiffViewerController.class.getResource("/fxml/diffViewer.fxml"));
			Parent root = (Parent) loader.load();
			DiffViewerController controller = (DiffViewerController) loader.getController();
			controller.setLabelText(label);
			controller.setText(original, revised);
			
			Stage stage = new Stage();

	        Scene scene = new Scene(root, DEFAULT_DIFF_VIEWER_WIDTH, DEFAULT_DIFF_VIEWER_HEIGHT);

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
	
	public void setText(List<String> original, List<String> revised) {
		final WebEngine webEngine = mainWebView.getEngine();
		if (original == null || revised == null) {
			webEngine.loadContent("");
			return;
		}
		List<DiffRow> diffRows = new DiffRowGenerator.Builder().build().generateDiffRows(original, revised);

		StringBuilder sb = new StringBuilder();
		sb.append("<html>\n");
		sb.append("<head>\n");
		sb.append(
				"<style>\n" +
				"html, body, div, span, applet, object, iframe, h1, h2, h3, h4, h5, h6, p, blockquote, pre, a, abbr, acronym, address, big, cite, code, del, dfn, em, img, ins, kbd, q, s, samp, small, strike, strong, sub, sup, tt, var, b, u, i, center, dl, dt, dd, ol, ul, li, fieldset, form, label, legend, table, caption, tbody, tfoot, thead, tr, th, td, article, aside, canvas, details, embed, figure, figcaption, footer, header, hgroup, menu, nav, output, ruby, section, summary, time, mark, audio, video {\n" + 
				"    margin: 0;\n" + 
				"    padding: 0;\n" + 
				"    border: 0;\n" + 
				"    font-size: 100%;\n" + 
				"    font: inherit;\n" + 
				"    vertical-align: baseline;\n" + 
				"}\n" +
				"table {\n" + 
				"    border-collapse: collapse;\n" + 
				"    border-spacing: 0;\n" + 
				"}\n" + 
				"\n" + 
				".diff-table {\n" + 
				"    font-family: \"Inconsolata\", \"Consolas\", \"Monaco\", monospace;\n" + 
				"    background: #fff;\n" + 
				"    width: 100%;\n" + 
				"}\n" + 
				"\n" + 
				".diff-line-number {\n" + 
				"    color: rgb(102, 102, 102);\n" + 
				"    text-align: right;\n" + 
				"    padding: 0px 4px;\n" + 
				"}\n" + 
				"\n" + 
				".diff-line {\n" + 
				"    position: relative;\n" + 
				"    width: 50%;\n" + 
				"    white-space: pre-wrap;\n" + 
				"    word-break: break-all;\n" + 
				"    line-height: 1rem;\n" + 
				"    margin: 0px 1px;\n" + 
				"}\n" + 
				"\n" + 
				".diff-line-empty {\n" + 
				"    background: rgb(204, 204, 204) !important;\n" + 
				"    border-left: 1px solid rgb(136, 136, 136) !important;\n" + 
				"    border-right: 1px solid rgb(136, 136, 136) !important;\n" + 
				"}\n" + 
				"\n" + 
				".diff-line-empty.end {\n" + 
				"    border-bottom: 1px solid rgb(136, 136, 136) !important;\n" + 
				"}\n" + 
				"\n" + 
				".diff-line-inserted.end, .diff-line-with-inserts.diff-line-with-inserts.end {\n" + 
				"    border-bottom: 1px solid rgb(26, 152, 31);\n" + 
				"}\n" + 
				"\n" + 
				".diff-line-inserted.start, .diff-line-with-inserts.diff-line-with-inserts.start {\n" + 
				"    border-top: 1px solid rgb(26, 152, 31);\n" + 
				"}\n" + 
				"\n" + 
				".diff-line-modified.diff-line-with-inserts {\n" + 
				"    background: rgb(224, 252, 208);\n" + 
				"}\n" + 
				"\n" + 
				".diff-line-inserted, .diff-line-with-inserts.diff-line-with-inserts {\n" + 
				"    border-left: 1px solid rgb(26, 152, 31);\n" + 
				"    border-right: 1px solid rgb(26, 152, 31);\n" + 
				"}\n" + 
				"\n" + 
				".diff-line-removed, .diff-line-modified.diff-line-with-removes {\n" + 
				"    border-left: 1px solid rgb(154, 35, 40);\n" + 
				"    border-right: 1px solid rgb(154, 35, 40);\n" + 
				"}\n" + 
				"\n" + 
				".diff-line-modified.diff-line-with-removes {\n" + 
				"    background: rgb(252, 216, 217);\n" + 
				"}\n" + 
				"\n" + 
				".diff-line-removed.start, .diff-line-modified.diff-line-with-removes.start {\n" + 
				"    border-top: 1px solid rgb(154, 35, 40);\n" + 
				"}\n" + 
				"\n" + 
				".diff-line-removed.end, .diff-line-modified.diff-line-with-removes.end {\n" + 
				"    border-bottom: 1px solid rgb(154, 35, 40);\n" + 
				"}\n" + 
				"\n" + 
				".diff-line-modified .diff-chunk {\n" + 
				"    display: inline-block;\n" + 
				"}\n" + 
				"\n" + 
				".diff-line-modified .diff-chunk-removed {\n" + 
				"    background: rgb(255, 136, 136);\n" + 
				"}\n" + 
				"\n" + 
				".diff-line-modified .diff-chunk-inserted {\n" + 
				"    background: rgb(153, 255, 153);\n" + 
				"}\n" + 
				"\n" + 
				"* {\n" + 
				"    box-sizing: border-box;\n" + 
				"}\n" + 
				"</style>\n"
				);
		sb.append("</head>\n");
		sb.append("<body>\n");
		sb.append("<table class='diff-table'>\n");
		
		int originalLineNumber = 0;
		int revisedLineNumber = 0;

		for (int i = 0; i < diffRows.size(); i++) {
			DiffRow diffRow = diffRows.get(i);
			DiffRow prevDiffRow = i > 0 ? diffRows.get(i - 1) : null;
			DiffRow nextDiffRow = i + 1 < diffRows.size() ? diffRows.get(i + 1) : null;
			
			boolean hasOriginalLine = false;
			boolean hasRevisedLine = false;
			
			String originalLineClass = "diff-line";
			String revisedLineClass = "diff-line";
			
			String originalChunkClass = "diff-chunk";
			String revisedChunkClass = "diff-chunk";
			
			if (diffRow.getTag() != DiffRow.Tag.EQUAL) {
				if (prevDiffRow == null || prevDiffRow.getTag() == DiffRow.Tag.EQUAL) {
					originalLineClass += " start";
					revisedLineClass += " start";
				}
				if (nextDiffRow == null || nextDiffRow.getTag() == DiffRow.Tag.EQUAL) {
					originalLineClass += " end";
					revisedLineClass += " end";
				}
			}
			
			if (diffRow.getTag() == DiffRow.Tag.EQUAL) {
				hasOriginalLine = true;
				hasRevisedLine = true;
				
				originalChunkClass += " diff-chunk-equal";
				revisedChunkClass += " diff-chunk-equal";
			} else if (diffRow.getTag() == DiffRow.Tag.CHANGE) {
				hasOriginalLine = true;
				hasRevisedLine = true;
				
				originalLineClass += " diff-line-modified diff-line-with-removes";
				originalChunkClass += " diff-chunk-removed";
				
				revisedLineClass += " diff-line-modified diff-line-with-inserts";
				revisedChunkClass += " diff-chunk-inserted";
			} else if (diffRow.getTag() == DiffRow.Tag.DELETE) {
				hasOriginalLine = true;
				
				originalLineClass += " diff-line-modified diff-line-with-removes";
				originalChunkClass += " diff-chunk-removed";
				
				revisedLineClass += " diff-line-empty diff-line-modified diff-line-with-inserts";
			} else if (diffRow.getTag() == DiffRow.Tag.INSERT) {
				hasRevisedLine = true;
				
				originalLineClass += " diff-line-empty diff-line-modified diff-line-with-removes";
				
				revisedLineClass += " diff-line-modified diff-line-with-inserts";
				revisedChunkClass += " diff-chunk-inserted";
			}
			
			sb.append("<tr class='diff-row'>\n");
			
			if (hasOriginalLine) {
				originalLineNumber++;
				sb.append("<td class='diff-line-number'>" + originalLineNumber + "</td>\n");
			} else {
				sb.append("<td class='diff-line-number'></td>\n");
			}
			sb.append("<td class='" + originalLineClass + "'>");
			sb.append("<span class='" + originalChunkClass + "'>" + diffRow.getOldLine() + "</span>\n");
			sb.append("</td>\n");
			
			if (hasRevisedLine) {
				revisedLineNumber++;
				sb.append("<td class='diff-line-number'>" + revisedLineNumber + "</td>\n");
			} else {
				sb.append("<td class='diff-line-number'></td>\n");
			}
			sb.append("<td class='" + revisedLineClass + "'>");
			sb.append("<span class='" + revisedChunkClass + "'>" + diffRow.getNewLine() + "</span>\n");
			sb.append("</td>\n");
			
			sb.append("</tr>\n");
			
		}

		sb.append("</table>\n");
		sb.append("</body>\n");
		sb.append("</html>\n");
		
		webEngine.loadContent(sb.toString());
	}

}
