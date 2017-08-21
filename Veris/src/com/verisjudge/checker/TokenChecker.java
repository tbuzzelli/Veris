package com.verisjudge.checker;
import com.verisjudge.Verdict;
import com.verisjudge.utils.FastScanner;

public class TokenChecker extends Checker {

    private final boolean caseSensative;

    public TokenChecker() {
        this(true);
    }

    public TokenChecker(boolean caseSensative) {
        this.caseSensative = caseSensative;
    }

    @Override
    public Verdict check(FastScanner input, FastScanner pScanner, FastScanner ansScanner) {
        while(ansScanner.hasNext()) {
            if(!pScanner.hasNext()) {
                return Verdict.WRONG_ANSWER; // not enough output
            }
            String ans = ansScanner.next(), par = pScanner.next();
            if(!check(ans,par)) {
                return Verdict.WRONG_ANSWER; // output differs
            }
        }
        if(pScanner.hasNext()) {
            return Verdict.WRONG_ANSWER; // too much output
        }
        return Verdict.CORRECT; // correct
    }

    private boolean check(String answer, String participant) {
        if (caseSensative) {
            return answer.equals(participant);
        } else {
            return answer.equalsIgnoreCase(participant);
        }
    }
}
