package com.verisjudge;

import java.io.File;

public class TestCaseResult {

	public final String name;
	public final Verdict verdict;
	public final File inputFile;
	public final File answerFile;
	public final long runtime;
	
	public TestCaseResult(String name, File inputFile, File answerFile, long runtime, Verdict verdict) {
		this.name = name;
		this.inputFile = inputFile;
		this.answerFile = answerFile;
		this.runtime = runtime;
		this.verdict = verdict;
	}
	
	static class Builder {
		private String name;
		private Verdict verdict;
		private File inputFile;
		private File answerFile;
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
		public TestCaseResult build() {
			return new TestCaseResult(name, inputFile, answerFile, runtime, verdict);
		}
	}
}
