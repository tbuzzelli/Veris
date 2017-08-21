
public enum Verdict {

    CORRECT("Correct", '✓', "1;37;42", "1;51;32"),
    WRONG_ANSWER("Wrong Answer", 'x', "1;37;41", "1;33"),
    RUNTIME_ERROR("Runtime Error", 'r', "1;37;101", "1;33"),
    COMPILE_ERROR("Compilation Error", 'c', "1;37;43", "1;33"),
    TIME_LIMIT_EXCEEDED("Time-Limit Exceeded", 't', "1;37;44", "1;33"),
    INTERNAL_ERROR("Internal Error", '?', "1;37;45", "1;33");

    private final String name;
    private final char character;
    private final String colorString;
    private final String compileColorString;

    Verdict(String name, char character, String colorString, String compileColorString) {
        this.name = name;
        this.character = character;
        this.colorString = colorString;
        this.compileColorString = compileColorString;
    }

    public String getName() {
        return name;
    }

    public char getCharacter() {
        return character;
    }

    public String getColorString() {
        return colorString;
    }

    public String getCompileColorString() {
        return compileColorString;
    }
}
