package com.verisjudge.checker;
import com.verisjudge.Verdict;
import com.verisjudge.utils.FastScanner;

public class NoChecker extends Checker {

    @Override
    public Verdict check(FastScanner input, FastScanner pScanner, FastScanner ansScanner) {
        return Verdict.CORRECT;
	}
}
