package com.verisjudge;

import java.io.File;

public class TestCaseResult {

	/**
	 * The maximum number of characters wide of output to show in the tooltip.
	 */
	public final static int MAX_OUTPUT_BLOCK_WIDTH = 24;
	/**
	 * The maximum number of lines of output to show in the tooltip.
	 */
	public final static int MAX_OUTPUT_BLOCK_HEIGHT = 4;
	
	public final String name;
	public final Verdict verdict;
	public final String checkerMessage;
	public final File inputFile;
	public final File answerFile;
	public final File programOutputFile;
	public final File errorStreamFile;
	public final String expectedOutput;
	public final String output;
	public final long runtime;
	
	public TestCaseResult(String name, File inputFile, File answerFile, long runtime, Verdict verdict, String checkerMessage, String expectedOutput, String output, File programOutputFile, File errorStreamFile) {
		this.name = name;
		this.inputFile = inputFile;
		this.answerFile = answerFile;
		this.runtime = runtime;
		this.verdict = verdict;
		this.checkerMessage = checkerMessage;
		this.expectedOutput = expectedOutput;
		this.output = output;
		this.programOutputFile = programOutputFile;
		this.errorStreamFile = errorStreamFile;
	}
	
	public TestCaseResult(String name, File inputFile, File answerFile, long runtime, Verdict verdict) {
		this(name, inputFile, answerFile, runtime, verdict, null, null, null, null, null);
	}
	
	public String getCheckerMessage() {
		return checkerMessage;
	}
	
	public File getInputFile() {
		return inputFile;
	}
	
	public File getAnswerFile() {
		return answerFile;
	}
	
	public File getProgramOutputFile() {
		return programOutputFile;
	}
	
	public File getErrorStreamFile() {
		return errorStreamFile;
	}
	
	/**
	 * Gets the text to show in the tooltip when mousing over this test case
	 * @return A potentially multiline string which includes the test case name, runtime, and some output (if WA)
	 */
	public String getTooltipString() {
		StringBuilder tooltipStringBuilder = new StringBuilder();
		String runtimeString = String.format("%.3f s", runtime / 1000.0);
		if (verdict == Verdict.TIME_LIMIT_EXCEEDED) {
			runtimeString = ">" + runtimeString;
		}
		tooltipStringBuilder.append(String.format("Test case %s - Runtime: %s", name, runtimeString));
		if (getCheckerMessage() != null && !getCheckerMessage().isEmpty()) {
			tooltipStringBuilder.append("\n\n" + getCheckerMessage());
		}
		/*
		if (verdict == Verdict.WRONG_ANSWER && expectedOutput != null && output != null) {
			tooltipStringBuilder.append("\n\nExpected output:\n");
			tooltipStringBuilder.append(getExpectedOutputBlockString());
			tooltipStringBuilder.append("\nYour output:\n");
			tooltipStringBuilder.append(getOutputBlockString());
		}
		*/
		return tooltipStringBuilder.toString();
	}
	
	/**
	 * Gets the text to display of the expected output cut to size.
	 * @return A potentially multiline string of the expected output.
	 */
	public String getExpectedOutputBlockString() {
		return getBlockString(expectedOutput, MAX_OUTPUT_BLOCK_WIDTH, MAX_OUTPUT_BLOCK_HEIGHT);
	}
	
	/**
	 * Gets the text to display of the submission's output cut to size.
	 * @return A potentially multiline string of the submission's output.
	 */
	public String getOutputBlockString() {
		return getBlockString(output, MAX_OUTPUT_BLOCK_WIDTH, MAX_OUTPUT_BLOCK_HEIGHT);
	}
	
	/**
	 * Takes a potentially multiline string and trims it to fit in the block of given size.
	 * Will add ellipses (...) as appropriate.
	 * @param str The initial multiline string to trim.
	 * @param maxWidth The maximum width in number of characters. (Will be maxed with 4)
	 * @param maxHeight The maximum number of lines allowed. (Will be maxed with 2)
	 * @return A multiline string which is str but trimmed to fit the constraints.
	 */
	public static String getBlockString(String str, int maxWidth, int maxHeight) {
		if (str == null)
			return "";
		maxWidth = Math.max(maxWidth, 4);
		maxHeight = Math.max(maxHeight, 2);

		String[] lines = str.split("\n");
		StringBuilder sb = new StringBuilder();
		boolean lastLineIsEllipsis = lines.length > maxHeight;
		
		for (int i = 0; i < lines.length && i < maxHeight; i++) {
			if (i > 0)
				sb.append('\n');
			String line = lines[i];
			if (i == maxHeight - 1 && lastLineIsEllipsis) {
				sb.append("...");
			} else if (line.length() <= maxWidth) {
				sb.append(line);
			} else {
				sb.append(line.substring(0, maxWidth - 3) + "...");
			}
		}
		return sb.toString();
	}
	
	static class Builder {
		private String name;
		private Verdict verdict;
		private String checkerMessage;
		private File inputFile;
		private File answerFile;
		private File programOutputFile;
		private File errorStreamFile;
		private String output;
		private String expectedOutput;
		private long runtime;
		
		public Builder setName(String name) {
			this.name = name;
			return this;
		}
		
		public Builder setVerdict(Verdict verdict) {
			this.verdict = verdict;
			return this;
		}
		
		public Builder setCheckerMessage(String checkerMessage) {
			this.checkerMessage = checkerMessage;
			return this;
		}
		
		public Builder setInputFile(File inputFile) {
			this.inputFile = inputFile;
			return this;
		}
		
		public Builder setAnswerFile(File answerFile) {
			this.answerFile = answerFile;
			return this;
		}
		
		public Builder setProgramOutputFile(File programOutputFile) {
			this.programOutputFile = programOutputFile;
			return this;
		}
		
		public Builder setErrorStreamFile(File errorStreamFile) {
			this.errorStreamFile = errorStreamFile;
			return this;
		}
		
		public Builder setRuntime(long runtime) {
			this.runtime = runtime;
			return this;
		}
		
		public Builder setExpectedOutput(String expectedOutput) {
			this.expectedOutput = expectedOutput;
			return this;
		}
		
		public Builder setOutput(String output) {
			this.output = output;
			return this;
		}
		
		public String getName() {
			return name;
		}
		
		public Verdict getVerdict() {
			return verdict;
		}
		
		public String getCheckerMessage() {
			return checkerMessage;
		}
		
		public File getInputFile() {
			return inputFile;
		}
		
		public File getAnswerFile() {
			return answerFile;
		}
		
		public File getProgramOutputFile() {
			return programOutputFile;
		}
		
		public File getErrorStreamFile() {
			return errorStreamFile;
		}
		
		public long getRuntime() {
			return runtime;
		}
		
		public String getExpectedOutput() {
			return expectedOutput;
		}
		
		public String getOutput() {
			return output;
		}
		
		public TestCaseResult build() {
			return new TestCaseResult(name, inputFile, answerFile, runtime, verdict, checkerMessage, expectedOutput, output, programOutputFile, errorStreamFile);
		}
	}
}
