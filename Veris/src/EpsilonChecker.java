public class EpsilonChecker extends TokenChecker {
    private final double absEps, relEps;

    public EpsilonChecker() {
        this(1e-6, 1e-6);
    }

    public EpsilonChecker(double absEps) {
        this.absEps = absEps;
        this.relEps = 0.0d;
    }

    public EpsilonChecker(double absEps, double relEps) {
        this.absEps = absEps;
        this.relEps = relEps;
    }

    boolean check(String answer, String participant) {
        try {
            double a = Double.parseDouble(answer), b = Double.parseDouble(participant);
            double rel;
            if(a == 0) {
                rel = relEps+1;
            } else {
                rel = Math.abs((a-b)/a);
            }
            return Math.abs(a-b) <= absEps || rel <= relEps;
        } catch(Exception e) {
            return answer.equals(participant);
        }
        
    }
}
