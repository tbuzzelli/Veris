package com.verisjudge.checker;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;

import com.verisjudge.Verdict;
import com.verisjudge.utils.FastScanner;

public class DiffChecker extends Checker {

    private final boolean ignoreTrailingWhitespace;
    private final boolean ignoreTrailingBlankLines;

    public DiffChecker() {
        this(false, false);
    }

    public DiffChecker(boolean ignoreTrailingWhitespace, boolean ignoreTrailingBlankLines) {
        this.ignoreTrailingWhitespace = ignoreTrailingWhitespace;
        this.ignoreTrailingBlankLines = ignoreTrailingBlankLines;
    }

    @Override
    public CheckerVerdict check(FastScanner input, FastScanner pScanner, FastScanner ansScanner) {
    	CheckerVerdict.Builder verdictBuilder = new CheckerVerdict.Builder();
    	
        BufferedReader pBr = pScanner.getBufferedReader();
        BufferedReader ansBr = ansScanner.getBufferedReader();
        ArrayDeque<String> pLines = new ArrayDeque<>();
        ArrayDeque<String> ansLines = new ArrayDeque<>();
        
        String line;
        try {
            while ((line = pBr.readLine()) != null) {
                if (ignoreTrailingWhitespace) line = line.trim();
                pLines.push(line);
            }
            while ((line = ansBr.readLine()) != null) {
                if (ignoreTrailingWhitespace) line = line.trim();
                ansLines.push(line);
            }
        } catch (IOException e) {
        	return verdictBuilder.setVerdict(Verdict.INTERNAL_ERROR)
        			.setMessage(e.getMessage())
        			.build();
        }
        
        // If we should ignore trailing blank lines, remove them from the queues.
        if (ignoreTrailingBlankLines) {
            while (!pLines.isEmpty() && pLines.peekLast().isEmpty()) pLines.pollLast();
            while (!ansLines.isEmpty() && ansLines.peekLast().isEmpty()) ansLines.pollLast();
        }
        // Check each line one at a time.
        int lineNumber = 0;
        while (!pLines.isEmpty() && !ansLines.isEmpty()) {
        	lineNumber++;
        	String pLine = pLines.pollFirst();
        	String ansLine = ansLines.pollFirst();
        	if (!pLine.equals(ansLine)) {
        		return verdictBuilder.setVerdict(Verdict.WRONG_ANSWER)
        				.setMessage(String.format("Line %s does not match.%s", lineNumber,
        						pLine.length() + ansLine.length() < 100 ?
        								"\nExpected: \"" + ansLine + "\"\nFound: \"" + pLine + "\"" : ""))
        				.build();
        	}
        }
        // Allow the participant or answer to have one extra blank line at the end.
        if ((ansLines.size() == 1 && ansLines.peek().isEmpty()) || (pLines.size() == 1 && pLines.peek().isEmpty())) {
        	return verdictBuilder.setVerdict(Verdict.CORRECT)
        			.build();
        }
        // Check if the participant output is missing lines at the end.
        if (!ansLines.isEmpty()) {
        	return verdictBuilder.setVerdict(Verdict.WRONG_ANSWER)
        			.setMessage(String.format("Missing last %d lines.", ansLines.size()))
        			.build();
        }
        // Check if the participant output has extra lines at the end.
        if (!pLines.isEmpty()) {
        	return verdictBuilder.setVerdict(Verdict.WRONG_ANSWER)
        			.setMessage(String.format("Found %d extra lines of output.", pLines.size()))
        			.build();
        }
        // Return a correct verdict.
        return verdictBuilder.setVerdict(Verdict.CORRECT)
    			.build();
    }
}
