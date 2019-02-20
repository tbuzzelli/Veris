package com.verisjudge.ui;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.verisjudge.CompileResult;
import com.verisjudge.Main;
import com.verisjudge.TestCaseResult;
import com.verisjudge.Verdict;
import com.verisjudge.Veris;
import com.verisjudge.VerisListener;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
	
	private final ContextMenu testCaseContextMenu = new ContextMenu();
	private final ContextMenu verdictContextMenu = new ContextMenu();
	
	private Integer activeContextMenuTestCaseNumber;
	private int numRejudgingCases;
	private Parent[] testCaseParents;
	private TestCaseResult[] testCaseResults;
	private boolean isJudging = false;
	private boolean isRejudging = false;

	private Stage stage;
	private Veris veris;
	
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

	public static boolean createAndJudge(Veris.Builder verisBuilder) {
		try {
			return createAndJudge(verisBuilder.build());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean createAndJudge(Veris veris) {
		try {
			FXMLLoader loader = new FXMLLoader(ResultsController.class.getResource("/fxml/results.fxml"));
			Parent root = (Parent) loader.load();
			ResultsController controller = (ResultsController) loader.getController();
			Stage stage = new Stage();
			
	        Scene scene = new Scene(root);

	        stage.setTitle(veris.getSolutionFile().getName() + " - Verisimilitude");
	        stage.setScene(scene);
	        stage.setResizable(false);
	        Main.addIconToStage(stage);
	        
	        controller.setStage(stage);
			controller.setVeris(veris);
	        controller.judge();
	        stage.show();
	        
	        return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
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
		Main.updateTheme(this.stage);
	}
	
	public void setVeris(Veris veris) {
		this.veris = veris;
	}
	
	public void judge() {
		refreshTimeLabels();
		veris.setListener(this);
		labelMainTitle.setText("Judging " + veris.getSolutionFile().getName());
		verisThread = new Thread() {
			public void run() {
				veris.testCode();
			}
		};
		verisThread.start();
	}
	
	private void rejudge() {
		for (int caseNumber = 0; caseNumber < testCaseResults.length; caseNumber++) {
			testCaseResults[caseNumber] = null;
			refreshTestCase(caseNumber);
		}
		refreshTimeLabels();
		numRejudgingCases = testCaseResults.length;
		labelMainTitle.setText("Judging " + veris.getSolutionFile().getName());
		verisThread = new Thread() {
			public void run() {
				veris.reTestCode(null);
			}
		};
		verisThread.start();
	}
	
	private void rerunCase(int caseNumber) {
		if (isJudging())
			return;
		testCaseResults[caseNumber] = null;
		refreshTestCase(caseNumber);
		refreshTimeLabels();
		numRejudgingCases = 1;
		labelMainTitle.setText("Judging " + veris.getSolutionFile().getName());
		verisThread = new Thread() {
			public void run() {
				veris.reTestCode(List.of(caseNumber));
			}
		};
		verisThread.start();
	}
	
	private void rerunFailingCases() {
		if (isJudging())
			return;
		List<Integer> failingCases = new ArrayList<>();
		for (int caseNumber = 0; caseNumber < testCaseResults.length; caseNumber++) {
			if (testCaseResults[caseNumber] != null
					&& testCaseResults[caseNumber].verdict != Verdict.CORRECT) {
				failingCases.add(caseNumber);
				testCaseResults[caseNumber] = null;
				refreshTestCase(caseNumber);
			}
		}

		refreshTimeLabels();
		numRejudgingCases = failingCases.size();
		
		labelMainTitle.setText("Judging " + veris.getSolutionFile().getName());
		verisThread = new Thread() {
			public void run() {
				veris.reTestCode(failingCases);
			}
		};
		verisThread.start();
	}

	@FXML
    protected void initialize() {
		buildTestCaseContextMenu();
		buildVerdictContextMenu();
		
		// Set the context menu to show on rightclick.
		labelVerdict.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				if (e.getButton() == MouseButton.PRIMARY || e.getButton() == MouseButton.SECONDARY) {
					verdictContextMenu.show(labelVerdict, Side.TOP, 0, 0);
				}
			}
		});
	}
	
	private void refreshTestCaseContextMenu() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				buildTestCaseContextMenu();
			}
		});
	}

	private void buildTestCaseContextMenu() {
		testCaseContextMenu.setOnShowing(new EventHandler<WindowEvent>() {
		    public void handle(WindowEvent e) {
		        // Do nothing.
		    }
		});
		testCaseContextMenu.setOnShown(new EventHandler<WindowEvent>() {
		    public void handle(WindowEvent e) {
		        // Do nothing.
		    }
		});
		
		testCaseContextMenu.getItems().clear();

		MenuItem itemOpenInputFile = new MenuItem("Open input file");
		itemOpenInputFile.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) {
		    	if (activeContextMenuTestCaseNumber != null) {
		    		TestCaseResult result = testCaseResults[activeContextMenuTestCaseNumber];
		    		if (result != null && result.getInputFile() != null) {
		    			TextViewerController.createAndOpenTextViewer(
		    					"Verisimilitude - " + result.name,
		    					result.getInputFile().getName(),
		    					result.getInputFile());
		    		} else {
		    			// TODO: show error message "Failed to open input file."
		    		}
		    	}
		    }
		});
		
		MenuItem itemOpenAnswerFile = new MenuItem("Open answer file");
		itemOpenAnswerFile.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) {
		    	if (activeContextMenuTestCaseNumber != null) {
		    		TestCaseResult result = testCaseResults[activeContextMenuTestCaseNumber];
		    		if (result != null && result.getAnswerFile() != null) {
		    			TextViewerController.createAndOpenTextViewer(
		    					"Verisimilitude - " + result.name,
		    					result.getAnswerFile().getName(),
		    					result.getAnswerFile());
		    		} else {
		    			// TODO: show error message "Failed to open answer file."
		    		}
		    	}
		    }
		});
		
		MenuItem itemOpenProgramOutputFile = new MenuItem("Open program output");
		itemOpenProgramOutputFile.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) {
		    	if (activeContextMenuTestCaseNumber != null) {
		    		TestCaseResult result = testCaseResults[activeContextMenuTestCaseNumber];
		    		if (result != null && result.getProgramOutputFile() != null) {
		    			TextViewerController.createAndOpenTextViewer(
		    					"Verisimilitude - " + result.name,
		    					result.name + " - Program Output",
		    					result.getProgramOutputFile());
		    		} else {
		    			// TODO: show error message "Failed to open program output."
		    		}
		    	}
		    }
		});
		
		MenuItem itemOpenDiff = new MenuItem("Open output diff");
		itemOpenDiff.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) {
		    	if (activeContextMenuTestCaseNumber != null) {
		    		TestCaseResult result = testCaseResults[activeContextMenuTestCaseNumber];
		    		if (result != null && result.getAnswerFile() != null && result.getProgramOutputFile() != null) {
		    			DiffViewerController.createAndOpenDiffViewer(
		    					"Verisimilitude - " + result.name,
		    					"Expected Output vs. Program Output",
		    					result.getAnswerFile(),
		    					result.getProgramOutputFile());
		    		} else {
		    			// TODO: show error message "Failed to open input file."
		    		}
		    	}
		    }
		});
		
		MenuItem itemViewErrorStream = new MenuItem("View error stream");
		itemViewErrorStream.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) {
		    	if (activeContextMenuTestCaseNumber != null) {
		    		TestCaseResult result = testCaseResults[activeContextMenuTestCaseNumber];
		    		if (result != null && result.getErrorStreamFile() != null) {
		    			TextViewerController.createAndOpenTextViewer(
		    					"Verisimilitude - " + result.name,
		    					result.name + " - Error Stream",
		    					result.getErrorStreamFile());
		    		} else {
		    			// TODO: show error message "Failed to open error stream file."
		    		}
		    	}
		    }
		});

		testCaseContextMenu.getItems().addAll(
				itemOpenInputFile,
				itemOpenAnswerFile,
				itemOpenProgramOutputFile,
				itemOpenDiff,
				itemViewErrorStream);
		
		if (!isJudging()) {
			MenuItem itemRerun = new MenuItem("Rerun this case");
			itemRerun.setOnAction(new EventHandler<ActionEvent>() {
			    public void handle(ActionEvent e) {
			    	if (activeContextMenuTestCaseNumber != null) {
			    		rerunCase(activeContextMenuTestCaseNumber);
			    	}
			    }
			});
			
			testCaseContextMenu.getItems().add(itemRerun);
		}
	}
	
	private void refreshVerdictContextMenu() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				buildVerdictContextMenu();
			}
		});
	}

	private void buildVerdictContextMenu() {
		verdictContextMenu.setOnShowing(new EventHandler<WindowEvent>() {
		    public void handle(WindowEvent e) {
		        // Do nothing.
		    }
		});
		verdictContextMenu.setOnShown(new EventHandler<WindowEvent>() {
		    public void handle(WindowEvent e) {
		        // Do nothing.
		    }
		});
		
		verdictContextMenu.getItems().clear();
		
		if (!isJudging()) {
			MenuItem itemRerunFailing = new MenuItem("Rerun failing cases");
			itemRerunFailing.setOnAction(new EventHandler<ActionEvent>() {
			    public void handle(ActionEvent e) {
			    	rerunFailingCases();
			    }
			});
			
			MenuItem itemRejudge = new MenuItem("Rejudge");
			itemRejudge.setOnAction(new EventHandler<ActionEvent>() {
			    public void handle(ActionEvent e) {
			    	rejudge();
			    }
			});
			
			verdictContextMenu.getItems().addAll(itemRerunFailing, itemRejudge);
		}
	}

	private void initializeTestCases(int numTestCases) {
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
		
		if(result == null) {
			Tooltip.uninstall(testCaseParent, null);
			testCaseParent.setOnMouseClicked(null);
		} else {
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
			
			// Set the tooltip to show.
			Tooltip.install(testCaseParent, tooltip);
			
			// Set the context menu to show on rightclick.
			testCaseParent.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent e) {
					if (e.getButton() == MouseButton.PRIMARY || e.getButton() == MouseButton.SECONDARY) {
						activeContextMenuTestCaseNumber = caseNumber;
						testCaseContextMenu.show(imageView, Side.BOTTOM, 0, 0);
					}
				}
			});
		}
		
		ProgressIndicator progressIndicator =
				(ProgressIndicator) testCaseParent.lookup("#progressIndicatorRunning");
		progressIndicator.setVisible(running);
	}
	
	private long getWorstTime() {
		long worstTime = 0;
		if (testCaseResults != null) {
			for (TestCaseResult result : testCaseResults) {
				if (result != null)
					worstTime = Math.max(worstTime, result.runtime);
			}
		}
		return worstTime;
	}
	
	private long getTotalTime() {
		long totalTime = 0;
		if (testCaseResults != null) {
			for (TestCaseResult result : testCaseResults) {
				if (result != null)
					totalTime += result.runtime;
			}
		}
		return totalTime;
	}

	private void refreshTimeLabels() {
		labelWorstTime.setText(String.format("%.2f seconds", getWorstTime() / 1000.0));
		labelTotalTime.setText(String.format("%.2f seconds", getTotalTime() / 1000.0));
	}
	
	private void setIsJudging(boolean isJudging) {
		this.isJudging = isJudging;
	}
	
	public boolean isJudging() {
		return isJudging;
	}
	
	private void setIsRejudging(boolean isRejudging) {
		this.isRejudging = isRejudging;
	}
	
	public boolean isRejudging() {
		return isRejudging;
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
		refreshTestCase(caseNumber);
		refreshTimeLabels();
	}
	
	private void refreshTestCase(int caseNumber) {
		updateTestCase(caseNumber, testCaseResults[caseNumber], false);
	}
	
	private void updateJudgingString(boolean isJudging, boolean isFinished, boolean wasRejudge) {
		int numCases = wasRejudge ? numRejudgingCases : testCaseParents.length;
		if (isJudging) {
			if (wasRejudge) {
				labelRunningTestCases.setText(
						String.format("Rerunning %d test case%s",
								numCases,
								numCases == 1 ? "" : "s"));
			} else {
				labelRunningTestCases.setText(
						String.format("Running %d test case%s",
								numCases,
								numCases == 1 ? "" : "s"));
			}
		} else if (isFinished) {
			if (wasRejudge) {
				labelRunningTestCases.setText("Finished rejudging");
			} else {
				labelRunningTestCases.setText("Finished judging");
			}
		} else {
			labelRunningTestCases.setText("");
		}
	}

	@Override
	public void handleJudgingStarting(String solutionName, String language, int numTestCases) {
		setIsJudging(true);
		setIsRejudging(false);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				stage.setTitle("Verisimilitude - " + solutionName);
				initializeTestCases(numTestCases);
				updateJudgingString(false, false, false);
			}
		});
		refreshTestCaseContextMenu();
		refreshVerdictContextMenu();
	}
	
	@Override
	public void handleRejudgingStarting(String solutionName, String language, int numTestCases) {
		setIsJudging(true);
		setIsRejudging(true);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				updateJudgingString(false, false, true);
			}
		});
		refreshTestCaseContextMenu();
		refreshVerdictContextMenu();
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
	public void handleCompileFinished(CompileResult compileResult) {
		Verdict compileVerdict = compileResult.getVerdict();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				labelCompilingCode.setText("Compiling code. . . " + compileVerdict.getName().toUpperCase());
				if (compileVerdict == Verdict.COMPILE_SUCCESS) {
					updateJudgingString(true, false, isRejudging());
				} else if (compileResult.getErrorStreamFile() != null) {
					updateJudgingString(false, false, isRejudging());
					labelCompilingCode.setOnMouseClicked(new EventHandler<MouseEvent>() {
						public void handle(MouseEvent e) {
							if (e.getButton() == MouseButton.PRIMARY) {
								TextViewerController.createAndOpenTextViewer("Verisimilitude - Compile Error", "Compile Error", compileResult.getErrorStreamFile());
							}
						}
					});
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
				if (finalVerdict == Verdict.CORRECT || smallestFailure == null)
					labelVerdict.setText(finalVerdict.getName());
				else
					labelVerdict.setText(finalVerdict.getName() + " - \"" + smallestFailure.inputFile.getName() + "\"");
				
				if (finalVerdict != Verdict.COMPILE_ERROR)
					updateJudgingString(false, true, false);
			}
		});
		setIsJudging(false);
		setIsRejudging(false);
		refreshTestCaseContextMenu();
		refreshVerdictContextMenu();
	}
	
	@Override
	public void handleRejudgingFinished(Verdict finalVerdict, boolean isFullRejudge) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				TestCaseResult smallestFailure = getSmallestFailure(finalVerdict);
				if (finalVerdict == Verdict.CORRECT || smallestFailure == null)
					labelVerdict.setText(finalVerdict.getName());
				else
					labelVerdict.setText(finalVerdict.getName() + " - \"" + smallestFailure.inputFile.getName() + "\"");
				
				if (finalVerdict != Verdict.COMPILE_ERROR)
					updateJudgingString(false, true, true);
			}
		});
		setIsJudging(false);
		setIsRejudging(false);
		refreshTestCaseContextMenu();
		refreshVerdictContextMenu();
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
