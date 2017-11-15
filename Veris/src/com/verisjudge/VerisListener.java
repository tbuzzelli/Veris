package com.verisjudge;

public interface VerisListener {

	public void handleJudgingStarting(String solutionName, String language, int numTestCases);
	public void handleCompileStarting();
	public void handleCompileFinished(boolean wasSuccess);
	public void handleTestCaseStarting(int caseNumber);
	public void handleTestCaseFinished(int caseNumber, TestCaseResult result);
	public void handleJudgingFinished(Verdict finalVerdict);
}
