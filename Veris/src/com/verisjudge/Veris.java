package com.verisjudge;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.verisjudge.checker.Checker;
import com.verisjudge.checker.TokenChecker;
import com.verisjudge.utils.BoxWriter;
import com.verisjudge.utils.FastScanner;
import com.verisjudge.utils.NullOutputStream;

public class Veris {

    public static final String[] ansFileTypes = { "ans", "out", "sol", "a", "sol" };
    public static final String[] inFileTypes = { "in", "data" };
    public static final long DEFAULT_TIME_LIMIT = 5000;
    public static final long MINIMUM_TIME_LIMIT = 100;
    public static final long MAXIMUM_TIME_LIMIT = 60 * 60 * 1000; // 1 hour
    
    private HashMap<String, File> answerFiles, inputFiles;

    private BoxWriter boxWriter;
    private Problem problem;
    private Checker checker;
    private ArrayList<TestCase> cases;
    private File solutionFile;
    private File directory;
    private String className;
    private String language;
    private long longestTime;
    private long totalTime;
    private long timeLimit;
    private File dataFolder;
    private boolean sortCasesBySize = true;
    private boolean isVerbose;
    private WeakReference<VerisListener> listener;

    /**
     * Veris constructor which defaults to writing to System.out.
     */
    public Veris() {
        this(System.out);
    }

    /**
     * Veris constructor which writes to given output stream.
     * @param out The output stream to write to.
     */
    public Veris(OutputStream out) {
        Path tmpDir;
        try {
            tmpDir = Files.createTempDirectory("veris");
            this.directory = tmpDir.toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setChecker(new TokenChecker());
        setTimeLimit(DEFAULT_TIME_LIMIT);
        setIsVerbose(false);
        boxWriter = new BoxWriter(out);
    }

    /**
     * Set the output stream for veris to write to.
     * @param out The output stream to have veris write to.
     */
    public void setOutputStream(OutputStream out) {
        if (boxWriter != null) {
            boxWriter.close();
        }
        boxWriter = new BoxWriter(out);
    }

    /**
     * Sets the output stream to NullOutputStream so nothing
     * will be written anywhere.
     */
    public void clearOutputStream() {
        setOutputStream(new NullOutputStream());
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
        this.problem = problem;
        setTimeLimit(problem.getTimeLimit());
        setChecker(problem.getChecker());
        try {
            setDataFolder(new File(problem.getDataPath()));
        } catch (IOException e) {
            boxWriter.println("Failed to find data folder '" + problem.getDataPath() + "'");
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
     * Sets the time limit for this problem in milliseconds.
     * @param timeLimit The time limit in milliseconds.
     */
    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
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
     * Returns the source file which will be judged
     * @return The source file which will be judged
     */
    public File getSolutionFile() {
    	return this.solutionFile;
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
     * Test the code against all test cases
     * @return The Verdict.
     */
    public Verdict testCode() {
    	if (listener.get() != null)
    		listener.get().handleJudgingStarting(solutionFile.getName(), language, cases.size());
        // Print the header
        boxWriter.println();
        boxWriter.openBox(80);
        if (problem != null) {
            boxWriter.println();
            boxWriter.centerOn();
            boxWriter.println(problem.getName() + " (" + problem.getFilename() + ")");
            boxWriter.centerOff();
            boxWriter.println();
            boxWriter.printDivider();
        }
        boxWriter.println("Judging solution: " + solutionFile.getName());
        boxWriter.println();

        Verdict result;

        if (listener.get() != null)
        	listener.get().handleCompileStarting();
        // Attempt to compile the code.
        result = compileCode();
        
        if (Thread.currentThread().isInterrupted()) {
        	return Verdict.INTERNAL_ERROR;
        }

        if (listener.get() != null)
        	listener.get().handleCompileFinished(result == Verdict.CORRECT);

        // As long as the code compiled, run it against the cases.
        String caseName = null;
        if (result == Verdict.CORRECT) {
            // Calculate the number of digits to use when printing each case.
            int n = cases.size();
            int digits = 0;
            while (n > 0) {
                digits++;
                n /= 10;
            }
            // Sort our test cases if needed.
            if (sortCasesBySize) {
                Collections.sort(cases);
            }
            // Print a line saying how many test cases are being run.
            int cnt = 1;
            boxWriter.println("Running " + cases.size() + " test case" + (cases.size() != 1 ? "s" : ""));
            boxWriter.println();
            for (int ci = 0; ci < cases.size(); ci++) {
            	TestCase c = cases.get(ci);
            	if (listener.get() != null)
            		listener.get().handleTestCaseStarting(ci);
            	TestCaseResult testCaseResult;
                Verdict caseResult;
                // Run the case and get the result.
                testCaseResult = runCase(className, c);
                caseResult = testCaseResult.verdict;
                
                // If the current result is still correct or if we had an
                // internal error, set it to this one.
                if (result == Verdict.CORRECT || caseResult == Verdict.INTERNAL_ERROR) {
                    result = caseResult;
                    caseName = c.name;
                }
                // Calculate the case width we need to print.
                int caseWidth = 4 + digits;
                if (caseResult != Verdict.CORRECT && isVerbose()) {
                    caseWidth += 2 + c.name.length();
                }
                // If this won't fit in the box when we print it, print a new line.
                if (boxWriter.getLineLength() > 1
                        && boxWriter.getRemainingWidth() < caseWidth) {
                    boxWriter.println();
                }
                // Print this case as the colored block with the number/name.
                boxWriter.print("\033[" + caseResult.getColorString() + "m");
                boxWriter.print(" ");
                boxWriter.printf(String.format("%%0%dd", digits), cnt++);
                boxWriter.print(" " + caseResult.getCharacter());
                boxWriter.print(" ");
                if (caseResult != Verdict.CORRECT && isVerbose()) {
                    boxWriter.print("(" + c.name + ") ");
                }
                boxWriter.print("\033[0m");
                
                if (listener.get() != null)
                	listener.get().handleTestCaseFinished(ci, testCaseResult);
                
                if (Thread.currentThread().isInterrupted()) {
                	return Verdict.INTERNAL_ERROR;
                }
            }
            boxWriter.println();
            boxWriter.println();
        }
        boxWriter.printDivider();
        // Print the verdict and what case was wrong if the verdict was not correct.
        boxWriter.print("Verdict: ");
        boxWriter.print(result.getName());
        if (result != Verdict.CORRECT && caseName != null) {
            boxWriter.print(" (on '" + caseName + "')");
        }
        boxWriter.printDivider();
        // Print the worst time and the total time.
        boxWriter.printf("Worst time: %.2fs\n", longestTime / 1000.0);
        boxWriter.printf("Total time: %.2fs\n", totalTime / 1000.0);
        boxWriter.closeBox();

        if (listener.get() != null)
        	listener.get().handleJudgingFinished(result);

        return result;
    }

    /**
     * Compile the solution
     * @return Either CORRECT or COMPILE_ERROR depending on whether or not
     * the solution compiled successfully. May return INTERNAL_ERROR if an
     * error occured.
     */
    public Verdict compileCode() {
        // Print the header.
        boxWriter.print("Compiling code: ");
        // Create the compile process.
        ProcessBuilder builder;
        if(language.equals("java")) {
            builder = new ProcessBuilder("javac", className + ".java");
        } else if(language.equals("c")) {
            builder = new ProcessBuilder("gcc", className + ".c", "-std=c99", "-o", "a");
        } else if(language.equals("cpp")) {
            builder = new ProcessBuilder("g++", className + ".cpp", "-std=c++11", "-o", "a");
        } else if(language.equals("cc")) {
            builder = new ProcessBuilder("g++", className + ".cc", "-std=c++11", "-o", "a");
        } else if(language.equals("py")) {
            boxWriter.println("N/A");
            return Verdict.CORRECT;
        } else {
            boxWriter.println("Unknown language " + language);
            return Verdict.INTERNAL_ERROR;
        }
        // Set the working directory to the temporary directory.
        builder.directory(directory);
        int resInt;
        // Attempt to compile the program/
        Process process = null;
        try {
            process = builder.start();
            resInt = process.waitFor();
        } catch (IOException e) {
            return Verdict.INTERNAL_ERROR;
        } catch (InterruptedException e) {
        	if (process != null)
        		process.destroyForcibly();
        	Thread.currentThread().interrupt();
        	return Verdict.INTERNAL_ERROR;
        }
        // Get the result and print it.
        Verdict res;
        if (resInt == 0) {
            res = Verdict.CORRECT;
        } else {
            res = Verdict.COMPILE_ERROR;
        }
        boxWriter.print("\033[" + res.getCompileColorString() + "m");
        boxWriter.print(res.getCharacter());
        boxWriter.print("\033[0m");
        boxWriter.println();
        // Return the compile result.
        return res;
    }

    /**
     * Run a single test case against the solution.
     * @param className The Java or Python class/filenames to use.
     * @param c The test case to run.
     * @return A TestCaseResult reprenting the result from running the solution against
     * this test case. Returns INTERNAL_ERROR if an error occurred.
     */
    public TestCaseResult runCase(String className, TestCase c) {
    	TestCaseResult.Builder resultBuilder = new TestCaseResult.Builder()
    			.setName(c.name)
    			.setInputFile(c.inputFile)
    			.setAnswerFile(c.answerFile);
        // Create the output file to use.
        File pOut = new File(directory, className + ".out");
        ProcessBuilder builder = null;
        if(language.equals("java")) {
            builder = new ProcessBuilder("java", className);
        } else if(language.equals("c")) {
            builder = new ProcessBuilder(directory  + "/a");
        } else if(language.equals("cpp")) {
            builder = new ProcessBuilder(directory  + "/a");
        } else if(language.equals("cc")) {
            builder = new ProcessBuilder(directory  + "/a");
        } else if(language.equals("py")) {
            builder = new ProcessBuilder("python3", className + ".py");
        } else {
            boxWriter.println("Unknown language " + language);
            resultBuilder.setVerdict(Verdict.INTERNAL_ERROR);
        	return resultBuilder.build();
        }
        // Set the working directory and redirect their output to the file.
        builder.directory(directory);
        builder.redirectOutput(pOut);

        // If we are verbose, inherit their error stream.
        if(isVerbose()) {
            builder.redirectError(Redirect.INHERIT);
        }

        // Create the process and attempt to start it.
        Process process;
        try {
            process = builder.start();
        } catch (IOException e) {
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
            completed = process.waitFor(timeLimit * 105 / 100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        	process.destroyForcibly();
            resultBuilder.setVerdict(Verdict.INTERNAL_ERROR);
        	Thread.currentThread().interrupt();
        	return resultBuilder.build();
        }

        // Calculate the time it took in milliseconds.
        long time = (System.nanoTime() - t1 + 999999) / 1000000;
        t1 = Math.min(timeLimit, time);
        // Keep track of the longest time and the total time.
        longestTime = Math.max(longestTime, t1);
        totalTime += t1;

        // Get the result.
        Verdict res;
        if (!completed || time > timeLimit) {
            res = Verdict.TIME_LIMIT_EXCEEDED;
            if (!completed) {
                process.destroyForcibly();
            }
        } else {
            if (process.exitValue() != 0) {
                res = Verdict.RUNTIME_ERROR;
            } else {
                // Check the solution's output.
                res = checker.check(new FastScanner(c.inputFile), new FastScanner(pOut), new FastScanner(c.answerFile));
            }
        }
        
        resultBuilder.setVerdict(res);
        resultBuilder.setRuntime(t1);

    	return resultBuilder.build();
    }

    /**
     * Pipes all data from an input stream to an output stream.
     * @param is The input stream to read from.
     * @param os The ouput stream to write to.
     * @throws IOException if an io error occurred.
     */
    public static void pipe(InputStream is, OutputStream os) throws IOException {
        int n;
        byte[] buffer = new byte[1024*1024];
        while ((n = is.read(buffer)) > -1) {
            os.write(buffer, 0, n);
        }
        os.close();
    }

    /**
     * Adds a folder and all its subfolders/files to the set of data files.
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
                    answerFiles.put(f.getName().replace("."+t, ""), f);
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

    class TestCase implements Comparable<TestCase> {
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
            return Long.compare(inputFile.length(), o.inputFile.length());
        }
    }

    public static class Builder {
    	private File solutionFile;
    	private File dataFolder;
    	private Long timeLimit;
    	private Boolean sortCasesBySize;
    	private Boolean isVerbose;
    	private Checker checker;
    	private OutputStream outputStream;
    	private VerisListener listener;
    	
    	public Veris build() throws IOException {
    		Veris veris = new Veris();
    		if (solutionFile != null)
    			veris.setSolutionFile(solutionFile);
    		if (dataFolder != null)
    			veris.setDataFolder(dataFolder);
    		if (timeLimit != null)
    			veris.setTimeLimit(timeLimit);
    		if (sortCasesBySize != null)
    			veris.setSortCasesBySize(sortCasesBySize);
    		if (isVerbose != null)
    			veris.setIsVerbose(isVerbose);
    		if (outputStream != null)
    			veris.setOutputStream(outputStream);
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
        	// if (cases == null || cases.size() == 0)
        	// 	return false;
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
    	
    	public Builder setSortCasesBySize(Boolean sortCasesBySize) {
    		this.sortCasesBySize = sortCasesBySize;
    		return this;
    	}
    	
    	public Boolean getSortCasesBySize() {
    		return sortCasesBySize;
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
    	
    	public Builder setOutputStream(OutputStream outputStream) {
    		this.outputStream = outputStream;
    		return this;
    	}
    	
    	public Builder clearOutputStream() {
    		this.outputStream = new NullOutputStream();
    		return this;
    	}
    	
    	public OutputStream getOutputStream() {
    		return outputStream;
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
