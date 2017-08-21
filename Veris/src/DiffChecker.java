import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;

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
    public Verdict check(FastScanner input, FastScanner pScanner, FastScanner ansScanner) {
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
            return Verdict.INTERNAL_ERROR;
        }
        if (ignoreTrailingBlankLines) {
            while (!pLines.isEmpty() && pLines.peek().isEmpty()) pLines.pop();
            while (!ansLines.isEmpty() && ansLines.peek().isEmpty()) ansLines.pop();
        }
        if (pLines.size() != ansLines.size()) {
            // If the # of lines is off by 1, add a blank line to allow
            // for one extra or missing blank line
            if (pLines.size() + 1 == ansLines.size()) {
                pLines.push("");
            } else if(ansLines.size() + 1 == pLines.size()) {
                ansLines.push("");
            } else {
                return Verdict.WRONG_ANSWER;
            }
        }
        while (!pLines.isEmpty()) {
            if (!pLines.pop().equals(ansLines.pop())) {
                return Verdict.WRONG_ANSWER;
            }
        }
        return Verdict.CORRECT;
    }
}
