
public class TokenChecker extends Checker {

    private final boolean caseSensative;

    public TokenChecker() {
        this(true);
    }

    public TokenChecker(boolean caseSensative) {
        this.caseSensative = caseSensative;
    }

    @Override
    public Veris.Verdict check(FastScanner input, FastScanner pScanner, FastScanner ansScanner) {
        while(ansScanner.hasNext()) {
            if(!pScanner.hasNext()) {
                return Veris.Verdict.WRONG_ANSWER; // not enough output
            }
            String ans = ansScanner.next(), par = pScanner.next();
            if(!check(ans,par)) {
                return Veris.Verdict.WRONG_ANSWER; // output differs
            }
        }
        if(pScanner.hasNext()) {
            return Veris.Verdict.WRONG_ANSWER; // too much output
        }
        return Veris.Verdict.CORRECT; // correct
    }

    private boolean check(String answer, String participant) {
        if (caseSensative) {
            return answer.equals(participant);
        } else {
            return answer.equalsIgnoreCase(participant);
        }
    }
}
