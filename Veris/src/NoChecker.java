public class NoChecker extends Checker {

    @Override
    public Veris.Verdict check(FastScanner input, FastScanner pScanner, FastScanner ansScanner) {
        return Veris.Verdict.CORRECT;
	}
}
