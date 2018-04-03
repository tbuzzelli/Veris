package com.verisjudge.checker;
import com.verisjudge.Verdict;
import com.verisjudge.utils.FastScanner;

public class TokenChecker extends Checker {

    private final boolean caseSensitive;

    public TokenChecker() {
        this(true);
    }

    public TokenChecker(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
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

    boolean check(String answer, String participant) {
        if (caseSensitive) {
            return answer.equals(participant);
        } else {
            return answer.equalsIgnoreCase(participant);
        }
    }
}
