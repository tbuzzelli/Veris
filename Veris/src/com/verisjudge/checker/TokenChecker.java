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
    public CheckerVerdict check(FastScanner input, FastScanner pScanner, FastScanner ansScanner) {
    	CheckerVerdict.Builder verdictBuilder = new CheckerVerdict.Builder();
    	int tokenNumber = 0;
        while(ansScanner.hasNext()) {
        	tokenNumber++;
        	// Check for not enough output.
            if(!pScanner.hasNext()) {
            	int numMissing = 0;
            	while (ansScanner.hasNext()) {
            		ansScanner.next();
            		numMissing++;
            	}
            	return verdictBuilder.setVerdict(Verdict.WRONG_ANSWER)
            			.setMessage(String.format("Missing last %d token(s).", numMissing))
            			.build();
            }
            String ans = ansScanner.next();
            String par = pScanner.next();
            // Check if the tokens aren't the same.
            if(!check(ans, par)) {
            	return verdictBuilder.setVerdict(Verdict.WRONG_ANSWER)
            			.setMessage(String.format("Token #%d differs.%s", tokenNumber,
            					ans.length() + par.length() < 50 ?
            							" Expected \"" + ans + "\" but found \"" + par + "\"." : ""))
            			.build();
            }
        }
        // Check for too much output.
        if(pScanner.hasNext()) {
        	int numExtra = 0;
        	while (pScanner.hasNext()) {
        		pScanner.next();
        		numExtra++;
        	}
        	return verdictBuilder.setVerdict(Verdict.WRONG_ANSWER)
        			.setMessage(String.format("Found %d extra token(s).", numExtra))
        			.build();
        }
        // Return verdict of Correct.
        return verdictBuilder.setVerdict(Verdict.CORRECT)
        		.build();
    }

    /**
     * Checks a single answer token against a participant token.
     * @param answer The answer token to check.
     * @param participant The participant token to check.
     * @return True if the tokens match; false otherwise.
     */
    private boolean check(String answer, String participant) {
        if (caseSensitive) {
            return answer.equals(participant);
        } else {
            return answer.equalsIgnoreCase(participant);
        }
    }
}
