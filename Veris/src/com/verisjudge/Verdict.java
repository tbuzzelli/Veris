package com.verisjudge;

public enum Verdict {

    CORRECT("Accepted"),
    WRONG_ANSWER("Wrong Answer"),
    RUNTIME_ERROR("Runtime Error"),
    COMPILE_ERROR("Compilation Error"),
    TIME_LIMIT_EXCEEDED("Time-Limit Exceeded"),
    INTERNAL_ERROR("Internal Error"),
	COMPILE_SUCCESS("Success");

    private final String name;

    Verdict(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
