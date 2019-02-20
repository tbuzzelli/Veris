package com.verisjudge;

import java.io.File;

public class TestCaseResult {

	public final String name;
	public final Verdict verdict;
	public final String checkerMessage;
	public final File inputFile;
	public final File answerFile;
	public final File programOutputFile;
	public final File errorStreamFile;
	public final long runtime;
	public final boolean wasStoppedEarly;
	
	public TestCaseResult(
			String name,
			File inputFile,
			File answerFile,
			long runtime,
			boolean wasStoppedEarly,
			Verdict verdict,
			String checkerMessage,
			File programOutputFile,
			File errorStreamFile
	) {
		this.name = name;
		this.inputFile = inputFile;
		this.answerFile = answerFile;
		this.runtime = runtime;
		this.wasStoppedEarly = wasStoppedEarly;
		this.verdict = verdict;
		this.checkerMessage = checkerMessage;
		this.programOutputFile = programOutputFile;
		this.errorStreamFile = errorStreamFile;
	}
	
	public TestCaseResult(String name, File inputFile, File answerFile, long runtime, boolean wasStoppedEarly, Verdict verdict) {
		this(name, inputFile, answerFile, runtime, wasStoppedEarly, verdict, null, null, null);
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
		if (wasStoppedEarly) {
			runtimeString = ">" + runtimeString;
		}
		tooltipStringBuilder.append(String.format("Test case %s - Runtime: %s", name, runtimeString));
		if (getCheckerMessage() != null && !getCheckerMessage().isEmpty()) {
			tooltipStringBuilder.append("\n\n" + getCheckerMessage());
		}
		return tooltipStringBuilder.toString();
	}
	
	static class Builder {
		private String name;
		private Verdict verdict;
		private String checkerMessage;
		private File inputFile;
		private File answerFile;
		private File programOutputFile;
		private File errorStreamFile;
		private long runtime;
		private boolean wasStoppedEarly;
		
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
		
		public Builder setWasStoppedEarly(boolean wasStoppedEarly) {
			this.wasStoppedEarly = wasStoppedEarly;
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
		
		public boolean getWasStoppedEarly() {
			return wasStoppedEarly;
		}
		
		public TestCaseResult build() {
			return new TestCaseResult(name, inputFile, answerFile, runtime, wasStoppedEarly, verdict, checkerMessage, programOutputFile, errorStreamFile);
		}
	}
}
