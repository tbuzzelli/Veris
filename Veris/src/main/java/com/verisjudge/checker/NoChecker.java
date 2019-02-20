package com.verisjudge.checker;
import com.verisjudge.Verdict;
import com.verisjudge.utils.FastScanner;

public class NoChecker extends Checker {

    @Override
    public CheckerVerdict check(FastScanner input, FastScanner pScanner, FastScanner ansScanner) {
    	return new CheckerVerdict.Builder()
    			.setVerdict(Verdict.CORRECT)
    			.setMessage("Ignoring checking. Judged as Correct.")
    			.build();
	}
}
