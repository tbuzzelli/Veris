package com.verisjudge.checker;

import com.verisjudge.Verdict;

public class CheckerVerdict {

	private final Verdict verdict;
	private final String message;
	
	public CheckerVerdict(Verdict verdict, String message) {
		if (verdict == null)
			throw new IllegalArgumentException("Verdict can't be null!");
		this.verdict = verdict;
		this.message = message;
	}
	
	public Verdict getVerdict() {
		return verdict;
	}
	
	public String getMessage() {
		return message;
	}
	
	static class Builder {
		private Verdict verdict;
		private String message;
		
		public CheckerVerdict build() {
			return new CheckerVerdict(verdict, message);
		}
		
		public Builder setVerdict(Verdict verdict) {
			this.verdict = verdict;
			return this;
		}
		
		public Verdict getVerdict() {
			return verdict;
		}
		
		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}
		
		public String getMessage() {
			return message;
		}
	}
}
