package com.verisjudge.ui;

import com.verisjudge.TestCaseResult;
import com.verisjudge.Verdict;
import com.verisjudge.Veris;
import com.verisjudge.VerisListener;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ResultsController implements VerisListener {
	@FXML private Label labelMainTitle;
	@FXML private Label labelCompilingCode;
	@FXML private Label labelRunningTestCases;
	@FXML private FlowPane flowPaneTestCases;
	@FXML private Label labelWorstTime;
	@FXML private Label labelTotalTime;
	@FXML private Label labelVerdict;
	
	private final Image TEST_CASE_BACKGROUND_CORRECT = new Image(this.getClass().getResourceAsStream("/res/verdictAccepted.png"));
	private final Image TEST_CASE_BACKGROUND_INTERNAL_ERROR = new Image(this.getClass().getResourceAsStream("/res/verdictInternalError.png"));
	private final Image TEST_CASE_BACKGROUND_WRONG_ANSWER = new Image(this.getClass().getResourceAsStream("/res/verdictWrongAnswer.png"));
	private final Image TEST_CASE_BACKGROUND_RUNTIME_ERROR = new Image(this.getClass().getResourceAsStream("/res/verdictRuntimeError.png"));
	private final Image TEST_CASE_BACKGROUND_TIME_LIMIT_EXCEEDED = new Image(this.getClass().getResourceAsStream("/res/verdictTimeLimitExceeded.png"));
	private final Image TEST_CASE_BACKGROUND_QUEUED = new Image(this.getClass().getResourceAsStream("/res/verdictQueued.png"));
	
	private Parent[] testCaseParents;
	private TestCaseResult[] testCaseResults;
	private int numTestCases;
	
	private Stage stage;
	private Veris veris;
	private long totalTime;
	private long worstTime;
	
	private Thread verisThread;
	
	public void setStage(Stage stage) {
		this.stage = stage;
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				// Interrupt Veris so it finishes early
				if (verisThread != null)
					verisThread.interrupt();
			}
		});    
	}
	
	public void setVeris(Veris veris) {
		this.veris = veris;
	}
	
	public void judge() {
		totalTime = 0;
		worstTime = 0;
		updateTimeLabels();
		veris.clearOutputStream();
		veris.setListener(this);
		labelMainTitle.setText("Judging " + veris.getSourceFile().getName());
		verisThread = new Thread() {
			public void run() {
				veris.testCode();
			}
		};
		verisThread.start();
	}

	@FXML
    protected void initialize() {
	}

	private void initializeTestCases(int numTestCases) {
		this.numTestCases = numTestCases;
		testCaseResults = new TestCaseResult[numTestCases];
		testCaseParents = new Pane[numTestCases];
		for (int i = 0; i < numTestCases; i++) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("testCaseResult.fxml"));
				Parent testCaseParent = (Parent) loader.load();
				testCaseParents[i] = testCaseParent;
				Label labelTestCaseNumber = (Label) testCaseParent.lookup("#labelTestCaseNumber");
				labelTestCaseNumber.setText("" + (i + 1));
				updateTestCase(i, null, false);
				flowPaneTestCases.getChildren().add(testCaseParent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateTestCase(int caseNumber, Verdict verdict, boolean running) {
		Parent testCaseParent = testCaseParents[caseNumber];
		ImageView imageView =
				(ImageView) testCaseParent.lookup("#imageViewTestCase");
		imageView.setImage(getTestCaseImageForVerdict(verdict));
		ProgressIndicator progressIndicator =
				(ProgressIndicator) testCaseParent.lookup("#progressIndicatorRunning");
		progressIndicator.setVisible(running);
	}
	
	private void updateTimeLabels() {
		labelWorstTime.setText(String.format("%.2f seconds", worstTime / 1000.0));
		labelTotalTime.setText(String.format("%.2f seconds", totalTime / 1000.0));
	}

	private Image getTestCaseImageForVerdict(Verdict verdict) {
		if (verdict == null)
			return TEST_CASE_BACKGROUND_QUEUED;
		switch (verdict) {
			case CORRECT:
				return TEST_CASE_BACKGROUND_CORRECT;
			case WRONG_ANSWER:
				return TEST_CASE_BACKGROUND_WRONG_ANSWER;
			case RUNTIME_ERROR:
				return TEST_CASE_BACKGROUND_RUNTIME_ERROR;
			case TIME_LIMIT_EXCEEDED:
				return TEST_CASE_BACKGROUND_TIME_LIMIT_EXCEEDED;
			case INTERNAL_ERROR:
				return TEST_CASE_BACKGROUND_INTERNAL_ERROR;
			default:
				return TEST_CASE_BACKGROUND_INTERNAL_ERROR;
		}
	}
	
	private void processTestCaseResult(int caseNumber, TestCaseResult result) {
		testCaseResults[caseNumber] = result;
		updateTestCase(caseNumber, result.verdict, false);
		worstTime = Math.max(worstTime, result.runtime);
		totalTime += result.runtime;
		updateTimeLabels();
	}

	@Override
	public void handleJudgingStarting(String solutionName, String language, int numTestCases) {
		stage.setTitle("Verisimilitude - " + solutionName);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initializeTestCases(numTestCases);
			}
		});
	}

	@Override
	public void handleCompileStarting() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				labelCompilingCode.setText("Compiling code. . .");
			}
		});
	}

	@Override
	public void handleCompileFinished(boolean wasSuccess) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (wasSuccess) {
					labelCompilingCode.setText("Compiling code. . . SUCCESS");
					labelRunningTestCases.setText(
							String.format("Running %d test case%s",
									testCaseParents.length,
									testCaseParents.length == 1 ? "" : "s"));
				} else {
					labelCompilingCode.setText("Compiling code. . . COMPILE ERROR");
				}
			}
		});
	}

	@Override
	public void handleTestCaseStarting(int caseNumber) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				updateTestCase(caseNumber, null, true);
			}
		});
	}

	@Override
	public void handleTestCaseFinished(int caseNumber, TestCaseResult result) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				processTestCaseResult(caseNumber, result);
			}
		});
	}

	@Override
	public void handleJudgingFinished(Verdict finalVerdict) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				labelVerdict.setText(finalVerdict.getName());
			}
		});
	}
}
