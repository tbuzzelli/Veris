package com.verisjudge.checker;

import com.verisjudge.Verdict;
import com.verisjudge.utils.FastScanner;

public class EpsilonChecker extends Checker {
	
	public final static double DEFAULT_ABSOLUTE_EPSILON = 1e-6;
	public final static double DEFAULT_RELATIVE_EPSILON = 1e-6;

    private final double absEps, relEps;
    private final boolean caseSensitive;

    public EpsilonChecker() {
        this(DEFAULT_ABSOLUTE_EPSILON, DEFAULT_RELATIVE_EPSILON);
    }

    public EpsilonChecker(double absEps) {
        this.absEps = absEps;
        this.relEps = 0.0d;
        this.caseSensitive = false;
    }

    public EpsilonChecker(double absEps, double relEps) {
        this.absEps = absEps;
        this.relEps = relEps;
        this.caseSensitive = false;
    }
    
    public EpsilonChecker(double absEps, double relEps, boolean caseSensitive) {
        this.absEps = absEps;
        this.relEps = relEps;
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
            	// TODO: make it show the difference that was out of bound.
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
     * Attempts to use epsilon checking but resorts to comparing the strings
     * if either strings can't be parsed as a double.
     * @param answer The answer token to check.
     * @param participant The participant token to check.
     * @return True if the tokens match; false otherwise.
     */
    private boolean check(String answer, String participant) {
        try {
            double a = Double.parseDouble(answer), b = Double.parseDouble(participant);
            double rel;
            if(Math.abs(a) == 0) {
                rel = relEps + 1;
            } else {
                rel = Math.abs((a - b) / a);
            }
            return Math.abs(a-b) <= absEps + 1e-10 || rel <= relEps + 1e-10;
        } catch (Exception e) {
        	// Ignore any errors and just compare the strings.
        	
        	if (caseSensitive) {
        		return answer.equals(participant);
        	} else {
        		return answer.equalsIgnoreCase(participant);
        	}
        }
        
    }
}
