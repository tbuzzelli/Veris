package com.verisjudge.checker;

public class EpsilonChecker extends TokenChecker {
	
	public final static double DEFAULT_ABSOLUTE_EPSILON = 1e-6;
	public final static double DEFAULT_RELATIVE_EPSILON = 1e-6;

    private final double absEps, relEps;

    public EpsilonChecker() {
        this(DEFAULT_ABSOLUTE_EPSILON, DEFAULT_RELATIVE_EPSILON);
    }

    public EpsilonChecker(double absEps) {
        this.absEps = absEps;
        this.relEps = 0.0d;
    }

    public EpsilonChecker(double absEps, double relEps) {
        this.absEps = absEps;
        this.relEps = relEps;
    }

    @Override
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
        } catch (Exception e) {
            return answer.equals(participant);
        }
        
    }
}
