package com.verisjudge.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.verisjudge.TestCaseResult;
import com.verisjudge.Verdict;
import com.verisjudge.Veris;
import com.verisjudge.VerisListener;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class ResultsController implements VerisListener {
	@FXML private Label labelMainTitle;
	@FXML private Label labelCompilingCode;
	@FXML private Label labelRunningTestCases;
	@FXML private FlowPane flowPaneTestCases;
	@FXML private Label labelWorstTime;
	@FXML private Label labelTotalTime;
	@FXML private Label labelVerdict;
	
	private final Image TEST_CASE_BACKGROUND_CORRECT = new Image(this.getClass().getResourceAsStream("/images/verdictAccepted.png"));
	private final Image TEST_CASE_BACKGROUND_INTERNAL_ERROR = new Image(this.getClass().getResourceAsStream("/images/verdictInternalError.png"));
	private final Image TEST_CASE_BACKGROUND_WRONG_ANSWER = new Image(this.getClass().getResourceAsStream("/images/verdictWrongAnswer.png"));
	private final Image TEST_CASE_BACKGROUND_RUNTIME_ERROR = new Image(this.getClass().getResourceAsStream("/images/verdictRuntimeError.png"));
	private final Image TEST_CASE_BACKGROUND_TIME_LIMIT_EXCEEDED = new Image(this.getClass().getResourceAsStream("/images/verdictTimeLimitExceeded.png"));
	private final Image TEST_CASE_BACKGROUND_QUEUED = new Image(this.getClass().getResourceAsStream("/images/verdictQueued.png"));
	
	private Parent[] testCaseParents;
	private TestCaseResult[] testCaseResults;
	private int numTestCases;
	
	private Stage stage;
	private Veris veris;
	private long totalTime;
	private long worstTime;
	
	private Thread verisThread;
	
	// Do some hacky stuff to fix our tooltip's timing
	static {
        try {
            Tooltip obj = new Tooltip();
            Class<?> clazz = obj.getClass().getDeclaredClasses()[0];
            Constructor<?> constructor = clazz.getDeclaredConstructor(
                    Duration.class,
                    Duration.class,
                    Duration.class,
                    boolean.class);
            constructor.setAccessible(true);
            Object tooltipBehavior = constructor.newInstance(
                    new Duration(250),  //open
                    new Duration(20000), //visible
                    new Duration(250),  //close
                    false);
            Field fieldBehavior = obj.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            fieldBehavior.set(obj, tooltipBehavior);
        }
        catch (Exception e) {
        	// Ignore the error.
        }
    }

	
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
		veris.setListener(this);
		labelMainTitle.setText("Judging " + veris.getSolutionFile().getName());
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
		ArrayList<Parent> allTestCases = new ArrayList<>(numTestCases);
		for (int i = 0; i < numTestCases; i++) {
			try {
				Parent testCaseParent = createTestCaseParent();
				testCaseParents[i] = testCaseParent;
				Label labelTestCaseNumber = (Label) testCaseParent.lookup("#labelTestCaseNumber");
				labelTestCaseNumber.setText("" + (i + 1));
				updateTestCase(i, null, false);
				allTestCases.add(testCaseParent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				flowPaneTestCases.getChildren().addAll(allTestCases);
			}
		});
	}
	
	private Parent createTestCaseParent() {
		StackPane stackPane = new StackPane();
		stackPane.setAlignment(Pos.CENTER_RIGHT);
		
		ImageView imageView = new ImageView();
		imageView.setId("imageViewTestCase");
		imageView.setFitWidth(63.0);
		imageView.setPickOnBounds(true);
		imageView.setPreserveRatio(true);
		
		Label label = new Label();
		label.setId("labelTestCaseNumber");
		label.setAlignment(Pos.CENTER);
		label.setMaxWidth(1e300);
		label.setText("000");
		label.setTextAlignment(TextAlignment.CENTER);
		label.setTextFill(Paint.valueOf("WHITE"));
		label.setFont(new Font("Monospaced Bold", 16.0));
		
		ProgressIndicator progressIndicator = new ProgressIndicator();
		progressIndicator.setId("progressIndicatorRunning");
		progressIndicator.setFocusTraversable(false);
		progressIndicator.setMaxHeight(Double.NEGATIVE_INFINITY);
		progressIndicator.setMaxWidth(Double.NEGATIVE_INFINITY);
		progressIndicator.setMinHeight(Double.NEGATIVE_INFINITY);
		progressIndicator.setMinWidth(Double.NEGATIVE_INFINITY);
		progressIndicator.setPrefHeight(18.0);
		progressIndicator.setPrefWidth(18.0);
		progressIndicator.setStyle("-fx-progress-color: white;");
		
		stackPane.getChildren().add(imageView);
		stackPane.getChildren().add(label);
		stackPane.getChildren().add(progressIndicator);
		
		StackPane.setMargin(label, new Insets(0.0, 22.0, 0.0, 2.0));
		StackPane.setMargin(progressIndicator, new Insets(0.0, 3.0, 0.0, 0.0));
		StackPane.setAlignment(progressIndicator, Pos.CENTER_RIGHT);
		
		return stackPane;
	}
	
	private void updateTestCase(int caseNumber, TestCaseResult result, boolean running) {
		Parent testCaseParent = testCaseParents[caseNumber];
		ImageView imageView =
				(ImageView) testCaseParent.lookup("#imageViewTestCase");
		imageView.setImage(getTestCaseImageForVerdict(result == null ? null : result.verdict));
		if(result != null) {
			Tooltip tooltip = new Tooltip(result.getTooltipString());
			// This requires Java 9 to do but Eclipse can't build the Java 9 jar yet.
			// tooltip.setShowDuration(new Duration(10000));
			// So, we call these methods using reflection in case we are running on Java 9.
			try {
				Tooltip obj = new Tooltip();
	            Class<?> c = obj.getClass();
			    @SuppressWarnings("rawtypes")
				Class[] argTypes = new Class[] { Duration.class };
			    Method setShowDelayMethod = c.getDeclaredMethod("setShowDelay", argTypes);
			    Method setHideDelayMethod = c.getDeclaredMethod("setHideDelay", argTypes);
			    Method setShowDurationMethod = c.getDeclaredMethod("setShowDuration", argTypes);
			    
			    setShowDelayMethod.invoke(tooltip, (Object) new Duration(250));
			    setHideDelayMethod.invoke(tooltip, (Object) new Duration(250));
			    setShowDurationMethod.invoke(tooltip, (Object) new Duration(20000));
	        }
	        catch (Exception e) {
	        	// Ignore the error.
	        }
			
			Tooltip.install(testCaseParent, tooltip);
		}
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
		updateTestCase(caseNumber, result, false);
		worstTime = Math.max(worstTime, result.runtime);
		totalTime += result.runtime;
		updateTimeLabels();
	}

	@Override
	public void handleJudgingStarting(String solutionName, String language, int numTestCases) {
		stage.setTitle("Verisimilitude - " + solutionName);
		initializeTestCases(numTestCases);
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
	public void handleCompileFinished(Verdict compileVerdict) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				labelCompilingCode.setText("Compiling code. . . " + compileVerdict.getName().toUpperCase());
				if (compileVerdict == Verdict.COMPILE_SUCCESS) {
					labelRunningTestCases.setText(
							String.format("Running %d test case%s",
									testCaseParents.length,
									testCaseParents.length == 1 ? "" : "s"));
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
				TestCaseResult smallestFailure = getSmallestFailure(finalVerdict);
				if (smallestFailure == null)
					labelVerdict.setText(finalVerdict.getName());
				else
					labelVerdict.setText(finalVerdict.getName() + "  \"" + smallestFailure.inputFile.getName() + "\"");
			}
		});
	}
	
	private TestCaseResult getSmallestFailure(Verdict finalVerdict) {
		if (finalVerdict == Verdict.COMPILE_SUCCESS || finalVerdict == Verdict.COMPILE_ERROR || testCaseResults == null)
			return null;
		TestCaseResult smallestFailure = null;
		for (TestCaseResult result : testCaseResults) {
			if (result == null)
				continue;
			if (result.verdict == finalVerdict
					&& (smallestFailure == null || result.inputFile.length() < smallestFailure.inputFile.length())) {
				smallestFailure = result;
			}
		}
		return smallestFailure;
	}
}
