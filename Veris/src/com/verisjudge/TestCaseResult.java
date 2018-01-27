package com.verisjudge;

import java.io.File;

public class TestCaseResult {

	public final static int MAX_OUTPUT_BLOCK_WIDTH = 24;
	public final static int MAX_OUTPUT_BLOCK_HEIGHT = 4;
	
	public final String name;
	public final Verdict verdict;
	public final File inputFile;
	public final File answerFile;
	public final String expectedOutput;
	public final String output;
	public final long runtime;
	
	public TestCaseResult(String name, File inputFile, File answerFile, long runtime, Verdict verdict, String expectedOutput, String output) {
		this.name = name;
		this.inputFile = inputFile;
		this.answerFile = answerFile;
		this.runtime = runtime;
		this.verdict = verdict;
		this.expectedOutput = expectedOutput;
		this.output = output;
	}
	
	public TestCaseResult(String name, File inputFile, File answerFile, long runtime, Verdict verdict) {
		this(name, inputFile, answerFile, runtime, verdict, null, null);
	}
	
	public String getTooltipString() {
		StringBuilder tooltipStringBuilder = new StringBuilder();
		tooltipStringBuilder.append("Test case " + name);
		if (verdict == Verdict.WRONG_ANSWER && expectedOutput != null && output != null) {
			tooltipStringBuilder.append("\n\nExpected output:\n");
			tooltipStringBuilder.append(getExpectedOutputBlockString());
			tooltipStringBuilder.append("\nYour output:\n");
			tooltipStringBuilder.append(getOutputBlockString());
		}
		return tooltipStringBuilder.toString();
	}
	
	public String getExpectedOutputBlockString() {
		return getBlockString(expectedOutput, MAX_OUTPUT_BLOCK_WIDTH, MAX_OUTPUT_BLOCK_HEIGHT);
	}
	
	public String getOutputBlockString() {
		return getBlockString(output, MAX_OUTPUT_BLOCK_WIDTH, MAX_OUTPUT_BLOCK_HEIGHT);
	}
	
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
		private File inputFile;
		private File answerFile;
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
		
		public Builder setInputFile(File inputFile) {
			this.inputFile = inputFile;
			return this;
		}
		
		public Builder setAnswerFile(File answerFile) {
			this.answerFile = answerFile;
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
		
		public File getInputFile() {
			return inputFile;
		}
		
		public File getAnswerFile() {
			return answerFile;
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
			return new TestCaseResult(name, inputFile, answerFile, runtime, verdict, expectedOutput, output);
		}
	}
}
