package com.verisjudge;

import java.io.File;

public class CompileResult {

	private final Verdict verdict;
	private final File errorStreamFile;
	
	public CompileResult(Verdict verdict, File errorStreamFile) {
		this.verdict = verdict;
		this.errorStreamFile = errorStreamFile;
	}
	
	public Verdict getVerdict() {
		return verdict;
	}
	
	public File getErrorStreamFile() {
		return errorStreamFile;
	}
	
	static class Builder {
		private Verdict verdict;
		private File errorStreamFile;
		
		public Builder setVerdict(Verdict verdict) {
			this.verdict = verdict;
			return this;
		}
		
		public Builder setErrorStreamFile(File errorStreamFile) {
			this.errorStreamFile = errorStreamFile;
			return this;
		}
		
		public Verdict getVerdict() {
			return verdict;
		}
		
		public File getErrorStreamFile() {
			return errorStreamFile;
		}
		
		public CompileResult build() {
			return new CompileResult(verdict, errorStreamFile);
		}
	}
}
