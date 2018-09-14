package com.verisjudge;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.verisjudge.checker.Checker;
import com.verisjudge.checker.CheckerVerdict;
import com.verisjudge.checker.TokenChecker;
import com.verisjudge.utils.FastScanner;

public class Veris {

    public static final String[] ansFileTypes = { "ans", "out", "sol", "a" };
    public static final String[] inFileTypes = { "in", "data" };
    public static final long DEFAULT_TIME_LIMIT = 5000;
    public static final long MINIMUM_TIME_LIMIT = 100;
    public static final long MAXIMUM_TIME_LIMIT = 60 * 60 * 1000; // 1 hour
    
    public final static boolean DEFAULT_SORT_CASES_BY_SIZE = false;
    public final static boolean DEFAULT_STOP_AT_FIRST_NON_CORRECT_VERDICT = false;
	
    private HashMap<String, File> answerFiles, inputFiles;

    private Config config = Config.getConfig();
    private Checker checker;
    private ArrayList<TestCase> cases;
    private File solutionFile;
    private File directory;
    private File errorStreamsDirectory;
    private File programOutputsDirectory;
    private LanguageSpec languageSpec;
    private String className;
    private String language;
    private long longestTime;
    private long timeLimit;
    private File dataFolder;
    private boolean sortCasesBySize = DEFAULT_SORT_CASES_BY_SIZE;
    private boolean stopAtFirstNonCorrectVerdict = DEFAULT_STOP_AT_FIRST_NON_CORRECT_VERDICT;
    private boolean isVerbose;
    private PrintStream logger = System.out;
    private WeakReference<VerisListener> listener;

    /**
     * Veris constructor
     */
    public Veris() {
        Path tmpDir;
        try {
            tmpDir = Files.createTempDirectory("veris");
            this.directory = tmpDir.toFile();
            this.programOutputsDirectory = Files.createTempDirectory("verisProgramOutputs").toFile();
            this.errorStreamsDirectory = Files.createTempDirectory("verisErrorStreams").toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setChecker(new TokenChecker());
        setTimeLimit(DEFAULT_TIME_LIMIT);
        setIsVerbose(false);
    }
    
    /**
     * Sets the listener to receive events while judging.
     * @param listener The listener that should receive events while judging
     * or null to clear the listener.
     */
    public void setListener(VerisListener listener) {
    	this.listener = new WeakReference<VerisListener>(listener);
    }

    /**
     * Retrieves the Time Limit, Checker and Data Folder from a Problem.
     * @param problem The Problem to load these settings from.
     */
    public void fromProblem(Problem problem) {
        setTimeLimit(problem.getTimeLimit());
        setChecker(problem.getChecker());
        try {
            setDataFolder(new File(problem.getDataPath()));
        } catch (IOException e) {
            logger.println("Failed to find data folder '" + problem.getDataPath() + "'");
            System.exit(1);
        }
    }

    /**
     * Sets whether or not the test cases should be sorted by size.
     * @param sortCasesBySize Whether or not the test cases should be
     * sorted by size.
     */
    public void setSortCasesBySize(boolean sortCasesBySize) {
        this.sortCasesBySize = sortCasesBySize;
    }
    
    /**
     * Sets whether or not the judging will stop once a non-correct verdict is reached.
     * @param stopAtFirstNonCorrectVerdict Whether or not the judging will stop once a
     * non-correct verdict is reached.
     */
    public void setStopAtFirstNonCorrectVerdict(boolean stopAtFirstNonCorrectVerdict) {
        this.stopAtFirstNonCorrectVerdict = stopAtFirstNonCorrectVerdict;
    }

    /**
     * Sets the time limit for this problem in milliseconds.
     * @param timeLimit The time limit in milliseconds.
     */
    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }
    
    /**
     * Sets the languageSpec to use while judging this solution.
     * @param languageSpec The languageSpec to use or null to auto detect language.
     */
    public void setLanguageSpec(LanguageSpec languageSpec) {
        this.languageSpec = languageSpec;
    }

    /**
     * Sets whether or not to be verbose when printing.
     * @param isVerbose A boolean representing whether or not verbose printing
     * should be used.
     */
    public void setIsVerbose(boolean isVerbose) {
        this.isVerbose = isVerbose;
    }

    public boolean isVerbose() {
        return isVerbose;
    }
    
    /**
     * Checks whether or not this instance of Veris is ready to judge.
     * @return True if there is a solution and data that is set up and ready
     * to judge.
     */
    public boolean isReady() {
    	if (getSolutionFile() == null)
    		return false;
    	if (getDataFolder() == null)
    		return false;
    	if (cases == null || cases.size() == 0)
    		return false;
    	return true;
    }

    /**
     * Sets the solution file to judge.
     * @param solutionFile The source file to judge. Absolute or relative to
     * running directory.
     * @throws IOException If an IO error was encountered while reading the
     * source file.
     */
    public void setSolutionFile(File solutionFile) throws IOException {
        this.solutionFile = solutionFile;
        fetchSolutionFile();
    }
    
    /**
     * Returns the source file which will be judged
     * @return The source file which will be judged
     */
    public File getSolutionFile() {
    	return this.solutionFile;
    }
    
    /**
     * Copies the solution file to a private version so changes while judging
     * will not cause any problems.
     * @throws IOException If an IO error was encountered while reading the
     * source file.
     */
    public void fetchSolutionFile() throws IOException {
    	if (solutionFile == null)
    		return;

        Path p = solutionFile.toPath();
        Path newSourceFile = directory.toPath().resolve(solutionFile.getName());
        if (!newSourceFile.toFile().exists())
        	Files.createFile(newSourceFile);

        Files.copy(p, newSourceFile, StandardCopyOption.REPLACE_EXISTING);
        this.className = solutionFile.getName();
        this.language = className.substring(className.lastIndexOf('.')+1);
        className = className.substring(0, className.lastIndexOf('.'));
    }
    
    /**
     * Prepares to run cases again after already judging. Also re-fetches the
     * solution file.
     * @throws IOException If an error occurs while copying the solution file.
     */
    public void refresh() throws IOException {
    	fetchSolutionFile();
    }

    /**
     * Sets the checker to use 
     * @param checker
     */
    public void setChecker(Checker checker) {
        this.checker = checker;
    }

    /**
     * Sets the data folder. All input/output files will be used from this
     * folder and from any and all sub folders.
     * @param dataFolder The folder where the data files are.
     * @throws IOException If there an IO error was encountered while reading
     * the data files.
     */
    public void setDataFolder(File dataFolder) throws IOException {
        this.inputFiles = new HashMap<>();
        this.answerFiles = new HashMap<>();
        this.dataFolder = dataFolder;

        addDataFolder(dataFolder);
        this.cases = new ArrayList<>();
        for (String name : inputFiles.keySet()) {
            if (!answerFiles.containsKey(name))
                continue;
            File inputFile = inputFiles.get(name);
            File answerFile = answerFiles.get(name);
            cases.add(new TestCase(inputFile, answerFile));
        }
    }
    
    /**
     * Gets the data folder if it was set.
     * @return The data folder if one was set or null otherwise.
     */
    public File getDataFolder(){
        return dataFolder;
    }
    
    /**
     * Compiles then tests the code against all test cases.
     * This should be run in a separate thread.
     * @return The Verdict.
     */
    public Verdict testCode() {
    	// Notify listener that judging has started.
    	if (listener.get() != null)
    		listener.get().handleJudgingStarting(solutionFile.getName(), language, cases.size());

    	// Notify listener that compiling has started.
        if (listener.get() != null)
        	listener.get().handleCompileStarting();
        
        LanguageSpec languageSpec = this.languageSpec != null ? this.languageSpec : config.getLanguageSpecForExtension(language);
        // If we can't find a matching language spec or this language isn't allowed, return INTERNAL_ERROR.
        if (languageSpec == null || !languageSpec.isAllowed()) {
        	// Notify listener of the internal error.
            if (listener.get() != null) {
            	listener.get().handleCompileFinished(new CompileResult.Builder().setVerdict(Verdict.INTERNAL_ERROR).build());
            	listener.get().handleJudgingFinished(Verdict.INTERNAL_ERROR);
            }
        	return Verdict.INTERNAL_ERROR;
        }
        
        // Attempt to compile the code.
        CompileResult compileResult = compileCode(languageSpec);
        Verdict compileVerdict = compileResult.getVerdict();
        
        // If we were interrupted, return with an internal error.
        if (Thread.currentThread().isInterrupted()) {
        	// Notify listener of the internal error.
            if (listener.get() != null) {
            	listener.get().handleCompileFinished(compileResult);
            	listener.get().handleJudgingFinished(Verdict.INTERNAL_ERROR);
            }
        	return Verdict.INTERNAL_ERROR;
        }
        
        // Notify listener that compiling had finished.
        if (listener.get() != null)
        	listener.get().handleCompileFinished(compileResult);
        
        // If the compiling failed, return.
        if (compileVerdict != Verdict.COMPILE_SUCCESS) {
        	// Notify listener of the internal error.
            if (listener.get() != null)
            	listener.get().handleJudgingFinished(compileVerdict);
        	return compileVerdict;
        }

        Verdict result = Verdict.CORRECT;
        
        // Sort our test cases as appropriate.
        if (sortCasesBySize) {
        	Comparator<TestCase> testCaseComparator = Comparator.comparingLong(a -> a.inputFile.length());
        	testCaseComparator = testCaseComparator.thenComparing(Comparator.naturalOrder());
            Collections.sort(cases, testCaseComparator);
        } else {
        	Collections.sort(cases);
        }

        for (int ci = 0; ci < cases.size(); ci++) {
            // Run the case and get the result.
            TestCaseResult testCaseResult = runCase(ci, languageSpec);
            Verdict caseResult = testCaseResult.verdict;
            
            // If the current result is still correct or if we had an
            // internal error, set it to this one.
            if (result == Verdict.CORRECT || caseResult == Verdict.INTERNAL_ERROR)
                result = caseResult;

            // If we were interrupted, return with an Internal Error.
            if (Thread.currentThread().isInterrupted()) {
            	if (listener.get() != null)
                	listener.get().handleRejudgingFinished(Verdict.INTERNAL_ERROR);
            	return Verdict.INTERNAL_ERROR;
            }

            // Check if we should stop after an incorrect verdict.
            if (result != Verdict.CORRECT && stopAtFirstNonCorrectVerdict)
            	break;
        }

        // Notify listener that judging has finished.
        if (listener.get() != null)
        	listener.get().handleJudgingFinished(result);

        // Return the result.
        return result;
    }

    /**
     * Compiles then tests the code against a selected number of test cases.
     * This should be run in a separate thread.
     * @return The Verdict.
     */
    public Verdict reTestCode(Collection<Integer> caseNumbers) {
    	try {
			refresh();
		} catch (IOException e) {
			e.printStackTrace();
			return Verdict.INTERNAL_ERROR;
		}
    	
    	// Notify listener that rejudging has started.
    	if (listener.get() != null)
    		listener.get().handleRejudgingStarting(solutionFile.getName(), language, cases.size());


    	// Notify listener that compiling has started.
        if (listener.get() != null)
        	listener.get().handleCompileStarting();
        
        LanguageSpec languageSpec = this.languageSpec != null ? this.languageSpec : config.getLanguageSpecForExtension(language);
        // If we can't find a matching language spec or this language isn't allowed, return INTERNAL_ERROR.
        if (languageSpec == null || !languageSpec.isAllowed()) {
        	// Notify listener of the internal error.
            if (listener.get() != null) {
            	listener.get().handleCompileFinished(new CompileResult.Builder().setVerdict(Verdict.INTERNAL_ERROR).build());
            	listener.get().handleRejudgingFinished(Verdict.INTERNAL_ERROR);
            }
        	return Verdict.INTERNAL_ERROR;
        }
        
        // Attempt to compile the code.
        CompileResult compileResult = compileCode(languageSpec);
        Verdict compileVerdict = compileResult.getVerdict();
        
        // If we were interrupted, return with an internal error.
        if (Thread.currentThread().isInterrupted()) {
        	// Notify listener of the internal error.
            if (listener.get() != null) {
            	listener.get().handleCompileFinished(compileResult);
            	listener.get().handleRejudgingFinished(Verdict.INTERNAL_ERROR);
            }
        	return Verdict.INTERNAL_ERROR;
        }
        
        // Notify listener that compiling had finished.
        if (listener.get() != null)
        	listener.get().handleCompileFinished(compileResult);
        
        // If the compiling failed, return.
        if (compileVerdict != Verdict.COMPILE_SUCCESS) {
        	// Notify listener of the internal error.
            if (listener.get() != null)
            	listener.get().handleRejudgingFinished(compileVerdict);
        	return compileVerdict;
        }

        Verdict result = Verdict.CORRECT;
        
        // Sort our test cases as appropriate.
        if (sortCasesBySize) {
        	Comparator<TestCase> testCaseComparator = Comparator.comparingLong(a -> a.inputFile.length());
        	testCaseComparator = testCaseComparator.thenComparing(Comparator.naturalOrder());
            Collections.sort(cases, testCaseComparator);
        } else {
        	Collections.sort(cases);
        }

        for (int ci = 0; ci < cases.size(); ci++) {
        	if (!caseNumbers.contains(ci)) {
        		continue;
        	}

            // Run the case and get the result.
            TestCaseResult testCaseResult = runCase(ci, languageSpec);
            Verdict caseResult = testCaseResult.verdict;
            
            // If the current result is still correct or if we had an
            // internal error, set it to this one.
            if (result == Verdict.CORRECT || caseResult == Verdict.INTERNAL_ERROR)
                result = caseResult;

            // If we were interrupted, return with an Internal Error.
            if (Thread.currentThread().isInterrupted()) {
            	if (listener.get() != null)
                	listener.get().handleRejudgingFinished(Verdict.INTERNAL_ERROR);
            	return Verdict.INTERNAL_ERROR;
            }
            
            // Check if we should stop after an incorrect verdict.
            if (result != Verdict.CORRECT && stopAtFirstNonCorrectVerdict)
            	break;
        }

        // Notify listener that judging has finished.
        if (listener.get() != null)
        	listener.get().handleRejudgingFinished(result);
        
        // Return the result.
        return result;
    }

    /**
     * Compile the solution
     * @param languageSpec The languageSpec to use while compiling the code. (Cannot be null).
     * @return A compile result with either COMPILE_SUCCESS or COMPILE_ERROR depending on whether or not
     * the solution compiled successfully. May return INTERNAL_ERROR if an
     * error occurred.
     */
    public CompileResult compileCode(LanguageSpec languageSpec) {
    	CompileResult.Builder compileResultBuilder = new CompileResult.Builder();
    	
        // If this language doesn't need compiling, just return CORRECT.
        if (!languageSpec.needsCompile())
        	return compileResultBuilder.setVerdict(Verdict.COMPILE_SUCCESS).build();
 
        // Build the compile process for this language.
        ProcessBuilder builder = languageSpec.getCompileProcessBuilder(solutionFile.getName(), className);

        File compileErrorStreamFile = null;
		try {
			compileErrorStreamFile = Files.createTempFile(errorStreamsDirectory.toPath(), "compileErrorStream", ".txt").toFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			// Ignore error.
		}
		
        // Set the working directory to the temporary directory.
        builder.directory(directory);
        
        // Redirect the compile error stream to a file for later use.
        if (compileErrorStreamFile != null)
        	builder.redirectError(compileErrorStreamFile);
        
        int resInt;
        
        // Attempt to compile the program.
        Process process = null;
        try {
            process = builder.start();
            resInt = process.waitFor();
        } catch (IOException e) {
            return compileResultBuilder.setVerdict(Verdict.INTERNAL_ERROR).build();
        } catch (InterruptedException e) {
        	if (process != null)
        		process.destroyForcibly();
        	Thread.currentThread().interrupt();
        	return compileResultBuilder.setVerdict(Verdict.INTERNAL_ERROR).build();
        }
        
        // Set the error stream file.
        if (compileErrorStreamFile != null)
        	compileResultBuilder.setErrorStreamFile(compileErrorStreamFile);
        
        // Get the result and print it.
        if (resInt == 0) {
        	compileResultBuilder.setVerdict(Verdict.COMPILE_SUCCESS);
        } else {
        	compileResultBuilder.setVerdict(Verdict.COMPILE_ERROR);
        }

        // Return the compile result.
        return compileResultBuilder.build();
    }

    /**
     * Run a single test case against the solution.
     * @param caseNumber The test case number to run.
     * @param languageSpec The languageSpec to use while running the code. (Cannot be null).
     * @return A TestCaseResult representing the result from running the solution against
     * this test case. Returns INTERNAL_ERROR if an error occurred.
     */
    public TestCaseResult runCase(int caseNumber,  LanguageSpec languageSpec) {
    	TestCase c = cases.get(caseNumber);
    	
    	// Notify listener that this case is now being run.
    	if (listener.get() != null)
    		listener.get().handleTestCaseStarting(caseNumber);

        // Run the case and get the result.
        TestCaseResult testCaseResult = runCase(c, languageSpec);

        // Notify listener that this case has been judged.
        if (listener.get() != null)
        	listener.get().handleTestCaseFinished(caseNumber, testCaseResult);

    	return testCaseResult;
    }

    /**
     * Run a single test case against the solution.
     * @param c The test case to run.
     * @param languageSpec The languageSpec to use while running the code. (Cannot be null).
     * @return A TestCaseResult representing the result from running the solution against
     * this test case. Returns INTERNAL_ERROR if an error occurred.
     */
    public TestCaseResult runCase(TestCase c, LanguageSpec languageSpec) {
    	TestCaseResult.Builder resultBuilder = new TestCaseResult.Builder()
    			.setName(c.name)
    			.setInputFile(c.inputFile)
    			.setAnswerFile(c.answerFile);
    	
        // Create the output file to use.
        File programOutputFile;
        try {
        	programOutputFile = Files.createTempFile(programOutputsDirectory.toPath(), "programOutput", ".out").toFile();
        } catch (Exception e) {
        	e.printStackTrace();
        	
        	// Return internal error.
        	resultBuilder.setVerdict(Verdict.INTERNAL_ERROR);
        	return resultBuilder.build();
        }

        // Build the execution process for this language.
        ProcessBuilder builder = languageSpec.getExecutionProcessBuilder(directory.getAbsolutePath(), solutionFile.getName(), className);
        
        File errorStreamFile = null;
		try {
			errorStreamFile = Files.createTempFile(errorStreamsDirectory.toPath(), "errorStream", ".txt").toFile();
		} catch (Exception e) {
			e.printStackTrace();
			// Ignore error.
		}
        
        // Set the working directory and redirect their output to the file.
        builder.directory(directory);
        builder.redirectOutput(programOutputFile);
        
        // Redirect the error stream to a file so we can show it later.
        if (errorStreamFile != null)
        	builder.redirectError(errorStreamFile);

        // Create the process and attempt to start it.
        Process process;
        try {
            process = builder.start();
        } catch (IOException e) {
        	e.printStackTrace();
        	resultBuilder.setVerdict(Verdict.INTERNAL_ERROR);
        	return resultBuilder.build();
        }
    
        // Pipe them their input
        Thread inputThread = new Thread() {
            public void run() {
                try {
                    FileInputStream inputFIS = new FileInputStream(c.inputFile);
                    OutputStream out = process.getOutputStream();
                    pipe(inputFIS, out);
                    inputFIS.close();
                    out.close();
                } catch(Exception e) {
                }
            }
        };

        // Record the time before starting their program.
        long t1 = System.nanoTime();
        
        // Start their program.
        inputThread.start();
        boolean completed = false;
        
        // Wait for it to complete with a timeout of 105% of the timeLimit
        try {
            completed = process.waitFor(timeLimit * 105 / 100 + 1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        	process.destroyForcibly();
            resultBuilder.setVerdict(Verdict.INTERNAL_ERROR);
        	Thread.currentThread().interrupt();
        	return resultBuilder.build();
        }

        // Calculate the time it took in milliseconds.
        long time = (System.nanoTime() - t1 + 999999) / 1000000;
        boolean wasStoppedEarly = time >= timeLimit + 1000;
        time = Math.min(timeLimit + 1000, time);
        
        // Keep track of the longest time and the total time.
        longestTime = Math.max(longestTime, time);

        // Get the result.
        Verdict verdict;
        String checkerMessage = null;
        if (!completed || time > timeLimit) {
            verdict = Verdict.TIME_LIMIT_EXCEEDED;
            if (!completed) {
                process.destroyForcibly();
            }
        } else {
            if (process.exitValue() != 0) {
                verdict = Verdict.RUNTIME_ERROR;
                checkerMessage = "Exit code: " + process.exitValue();
            } else {
                // Check the solution's output.
            	FastScanner inputScanner = new FastScanner(c.inputFile);
            	FastScanner pScanner = new FastScanner(programOutputFile);
            	FastScanner ansScanner = new FastScanner(c.answerFile);
            	
                CheckerVerdict checkerVerdict = checker.check(inputScanner, pScanner, ansScanner);
                verdict = checkerVerdict.getVerdict();
                checkerMessage = checkerVerdict.getMessage();
                
                inputScanner.close();
                pScanner.close();
                ansScanner.close();
                
                final int NUM_CHARS_TO_READ = 256;
                
                // Build the expectedOutput and output strings.
                try {
                	StringBuilder expectedOutputStringBuilder = new StringBuilder();
                	StringBuilder outputStringBuilder = new StringBuilder();
                	String line;
					BufferedReader expectedOutputBufferedReader = new BufferedReader(new FileReader(c.answerFile));
					while ((line = expectedOutputBufferedReader.readLine()) != null) {
						if (expectedOutputStringBuilder.length() > 0)
							expectedOutputStringBuilder.append('\n');
						if (line.length() + expectedOutputStringBuilder.length() <= NUM_CHARS_TO_READ) {
							expectedOutputStringBuilder.append(line);
						} else {
							int numChars = Math.min(line.length(), NUM_CHARS_TO_READ - 1 - expectedOutputStringBuilder.length());
							if (numChars > 0) {
								expectedOutputStringBuilder.append(line.substring(0, numChars) + "...");
							}
							break;
						}
					}
					if (expectedOutputBufferedReader.readLine() != null) {
						expectedOutputStringBuilder.append("\n...");
					}
					expectedOutputBufferedReader.close();
					
					BufferedReader outputBufferedReader = new BufferedReader(new FileReader(programOutputFile));
					while ((line = outputBufferedReader.readLine()) != null) {
						if (outputStringBuilder.length() > 0)
							outputStringBuilder.append('\n');
						if (line.length() + outputStringBuilder.length() <= NUM_CHARS_TO_READ) {
							outputStringBuilder.append(line);
						} else {
							int numChars = Math.min(line.length(), NUM_CHARS_TO_READ - 1 - outputStringBuilder.length());
							if (numChars > 0) {
								outputStringBuilder.append(line.substring(0, numChars) + "...");
							}
							break;
						}
					}
					if (outputBufferedReader.readLine() != null) {
						outputStringBuilder.append("\n...");
					}
					outputBufferedReader.close();
					
					resultBuilder.setExpectedOutput(expectedOutputStringBuilder.toString());
					resultBuilder.setOutput(outputStringBuilder.toString());
				} catch (IOException e) {
					// Ignore any errors.
				}
            }
        }
        
        resultBuilder.setVerdict(verdict);
        resultBuilder.setCheckerMessage(checkerMessage);
        resultBuilder.setRuntime(time);
        resultBuilder.setWasStoppedEarly(wasStoppedEarly);
        if (errorStreamFile != null)
        	resultBuilder.setErrorStreamFile(errorStreamFile);
        
        // Set the program output file in the result builder.
        resultBuilder.setProgramOutputFile(programOutputFile);

    	return resultBuilder.build();
    }

    /**
     * Pipes all data from an input stream to an output stream.
     * @param is The input stream to read from.
     * @param os The output stream to write to.
     * @throws IOException if an IO error occurred.
     */
    public static void pipe(InputStream is, OutputStream os) throws IOException {
        int n;
        byte[] buffer = new byte[1024 * 1024];
        while ((n = is.read(buffer)) > -1) {
            os.write(buffer, 0, n);
        }
        os.close();
    }

    /**
     * Adds a folder and all its sub folders/files to the set of data files.
     * @param folder The data folder to add.
     */
    private void addDataFolder(File folder) {
        // If this is not a directory, just return.
        if (!folder.isDirectory()) {
            return;
        }
        // If this directory contains a file called ".nodata", skip this folder.
        for (File f : folder.listFiles()) {
            if (f.getName().toLowerCase().equals(".nodata")) {
                return;
            }
        }
        // Go through each file in this data folder.
        for (File f : folder.listFiles()) {
            // If this file is a folder, add it recursively.
            if (f.isDirectory()) {
                addDataFolder(f);
                continue;
            }
            // Get the file name and file type.
            String name = f.getName();
            String filetype = "";
            if (name.contains(".")) {
                filetype = name.substring(name.lastIndexOf('.') + 1);
                name = name.substring(0, name.lastIndexOf('.'));
            }
            // If this is an answer file or an input file,
            // add it to the appropriate list.
            for (String t : ansFileTypes) {
                if (filetype.equals(t.toLowerCase())) {
                    answerFiles.put(name, f);
                    break;
                }
                // If the filename contains the ansFileType string, add it.
                // Ex: case.out5 case.5.out
                if(f.getName().contains("." + t)) {
                    answerFiles.put(f.getName().replace("."+t, ""), f);
                    break;
                }
            }
            for (String t : inFileTypes) {
                if (filetype.equals(t.toLowerCase())) {
                    inputFiles.put(name, f);
                    break;
                }
                // If the filename contains the inFileType string, add it.
                // Ex: case.in5 case.5.in
                if(f.getName().contains("." + t)) {
                	inputFiles.put(f.getName().replace("."+t, ""), f);
                    break;
                }
            }
        }
    }
    
    /**
     * Returns whether or not the file given is an acceptable source file.
     * Makes sure that the file has a valid extension
     * @param f
     * @return whether or not this file is an acceptable solution file.
     */
    public static boolean isValidSolutionFile(File f) {
    	String extension = getFileExtension(f).toLowerCase();
    	switch (extension) {
    		case "java":
    		case "c":
    		case "cc":
    		case "cpp":
    		case "exe":
    		case "pas":
    		case "py":
    			return true;
    		default:
    			return false;
    	}
    }
    
    /**
     * Returns the extension from the file given
     * @param f The file
     * @return The extension of the file or an empty string if there is
     * no file extension.
     */
    public static String getFileExtension(File f) {
    	return getFileExtension(f.getName());
    }
    
    /**
     * Returns the extension from the filename given
     * @param name The file name
     * @return The extension of the file or an empty string if there is
     * no file extension.
     */
    public static String getFileExtension(String name) {
    	int idx = name.indexOf(".");
    	if (idx == -1) return "";
    	return name.substring(idx + 1);
    }

    public class TestCase implements Comparable<TestCase> {
        File inputFile, answerFile;
        String name;

        public TestCase(File inputFile, File answerFile) {
            this.inputFile = inputFile;
            this.answerFile = answerFile;
            name = inputFile.getName();

            File tmp = inputFile.getParentFile();
            while (!tmp.equals(dataFolder)) {
                name = tmp.getName() + "/" + name;
                tmp = tmp.getParentFile();

            }
        }

        @Override
        public int compareTo(TestCase o) {
        	return name.compareTo(o.name);
        }
    }

    public static class Builder {
    	private File solutionFile;
    	private File dataFolder;
    	private Long timeLimit;
    	private LanguageSpec languageSpec;
    	private Boolean sortCasesBySize;
    	private Boolean stopAtFirstNonCorrectVerdict;
    	private Boolean isVerbose;
    	private Checker checker;
    	private VerisListener listener;
    	
    	public Veris build() throws IOException {
    		Veris veris = new Veris();
    		if (solutionFile != null)
    			veris.setSolutionFile(solutionFile);
    		if (dataFolder != null)
    			veris.setDataFolder(dataFolder);
    		if (timeLimit != null)
    			veris.setTimeLimit(timeLimit);
    		if (languageSpec != null)
    			veris.setLanguageSpec(languageSpec);
    		if (sortCasesBySize != null)
    			veris.setSortCasesBySize(sortCasesBySize);
    		if (stopAtFirstNonCorrectVerdict != null)
    			veris.setStopAtFirstNonCorrectVerdict(stopAtFirstNonCorrectVerdict);
    		if (isVerbose != null)
    			veris.setIsVerbose(isVerbose);
    		if (listener != null)
    			veris.setListener(listener);
    		if (checker != null)
    			veris.setChecker(checker);
    		
    		return veris;
    	}
    	
    	public boolean isReady() {
    		if (getSolutionFile() == null)
        		return false;
        	if (getDataFolder() == null)
        		return false;
        	return true;
    	}

    	public Builder setSolutionFile(File solutionFile) {
    		this.solutionFile = solutionFile;
    		return this;
    	}
    	
    	public File getSolutionFile() {
    		return solutionFile;
    	}
    	
    	public Builder setDataFolder(File dataFolder) {
    		this.dataFolder = dataFolder;
    		return this;
    	}
    	
    	public File getDataFolder() {
    		return dataFolder;
    	}
    	
    	public Builder setTimeLimit(Long timeLimit) {
    		this.timeLimit = timeLimit;
    		return this;
    	}
    	
    	public Long getTimeLimit() {
    		return timeLimit;
    	}
    	
    	public Builder setLanguageSpec(LanguageSpec languageSpec) {
    		this.languageSpec = languageSpec;
    		return this;
    	}
    	
    	public LanguageSpec getLanguageSpec() {
    		return languageSpec;
    	}
    	
    	public Builder setSortCasesBySize(Boolean sortCasesBySize) {
    		this.sortCasesBySize = sortCasesBySize;
    		return this;
    	}
    	
    	public Boolean getSortCasesBySize() {
    		return sortCasesBySize;
    	}
    	
    	public Builder setStopAtFirstNonCorrectVerdict(Boolean stopAtFirstNonCorrectVerdict) {
    		this.stopAtFirstNonCorrectVerdict = stopAtFirstNonCorrectVerdict;
    		return this;
    	}
    	
    	public Boolean getStopAtFirstNonCorrectVerdict() {
    		return stopAtFirstNonCorrectVerdict;
    	}
    	
    	public Builder setIsVerbose(Boolean isVerbose) {
    		this.isVerbose = isVerbose;
    		return this;
    	}
    	
    	public Boolean isVerbose() {
    		return isVerbose;
    	}
    	
    	public Builder setChecker(Checker checker) {
    		this.checker = checker;
    		return this;
    	}
    	
    	public Checker getChecker() {
    		return checker;
    	}
    	
    	public Builder setListener(VerisListener listener) {
    		this.listener = listener;
    		return this;
    	}
    	
    	public VerisListener getListener() {
    		return listener;
    	}
    }
}
