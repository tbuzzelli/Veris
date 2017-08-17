import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Scanner;

public class CheckerUtils {

    enum Error {
        EMPTY_CHECKER_STRING,
        CLASS_DOES_NOT_EXTEND_CHECKER,
        UNABLE_TO_FIND_CHECKER,
        UNABLE_TO_FIND_CONSTRUCTOR
    }

    private Error error;
    private String errorMessage;

    /**
     * Checks whether or not an error was found while getting
     * the checker from the CheckerString.
     *
     * @return true if there is an error, false otherwise.
     */
    public boolean hasError() {
        return getError() != null;
    }

    /**
     * Gets the error found while getting the checker from
     * the checker string if it exists.
     *
     * @return The error if one exists or null otherwise.
     */
    public Error getError() {
        return error;
    }

    /**
     * Gets the message associated with the error found while
     * getting the checker from the CheckerString.
     *
     * @return A String representing the error message or
     * null if no error was found
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Clears the error and the error message.
     */
    public void clearError() {
        error = null;
        errorMessage = null;
    }

    /**
     * Sets the error and error message for this CheckerUtils.
     * @param error The error enum representing for this error.
     * @param errorMessage The error string which will be
     * displayed to the user.
     */
    private void setError(Error error, String errorMessage) {
        this.error = error;
        this.errorMessage = errorMessage;
    }

    /**
     * Attempts to extract the Checker object from the given
     * checker string.
     * @param checkerString A string describing the checker to be
     * created.<br/>
     * Examples:<br/>
     * 'DiffChecker 1e-6 1e-6'  =>  new DiffChecker(1e-6, 1e-6)<br/>
     * 'SomeChecker string false 123'  =>  
     * new SomeChecker("string", false, 123)<br/>
     * @return The checker object if a Checker was found which
     * matched the checker string or null if an error was encountered.
     */
    public Checker getCheckerFromString(String checkerString) {
        // Clear any error that may currently exist.
        clearError();
        // If the checker string is null or empty,
        // return null and report an error.
        if (checkerString == null || checkerString.isEmpty()) {
            setError(Error.EMPTY_CHECKER_STRING, "Checker string is empty");
            return null;
        }

        Scanner scanner = new Scanner(checkerString);
        // Read in the checker class name.
        String name = scanner.next();
        ArrayList<Class<?>> types = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        ArrayList<String> argStrings = new ArrayList<>();
        try {
            // Attempt to get the checker class from the class name.
            Class<?> c1 = Class.forName(name);
            // If the class we find does not extend checker, report an error.
            if (!Checker.class.isAssignableFrom(c1)) {
                setError(Error.CLASS_DOES_NOT_EXTEND_CHECKER,
                    "Checker class " + c1.getCanonicalName() + " does not extend Checker");
                scanner.close();
                return null;
            }
            // Case to a Checker.
            @SuppressWarnings("unchecked")
            Class<Checker> c2 = (Class<Checker>) c1;

            // Read through all the args and parse them as the best fit.
            while (scanner.hasNext()) {
                String arg = scanner.next();
                argStrings.add(arg);
                Object value = arg;
                try {
                    value = Integer.parseInt(arg);
                } catch (Exception e) {
                    try {
                        value = Long.parseLong(arg);
                    } catch (Exception e2) {
                        try {
                            value = Double.parseDouble(arg);
                        } catch (Exception e3) {
                            try {
                                value = Boolean.parseBoolean(arg);
                            } catch (Exception e4) {
                            }
                        }
                    }
                }
                // Get the type and then fix it (example: Integer => int).
                Class<?> type = value.getClass();
                type = fixType(type);
                types.add(type);
                values.add(value);
            }

            // Attempt to get the constructor for these arguments.
            Constructor<Checker> cons = c2.getConstructor(types.toArray(new Class<?>[0]));
            // Get the checker and return it.
            Checker checker = cons.newInstance(values.toArray());
            scanner.close();
            return checker;
        } catch (NoSuchMethodException e) {
            // Generate a string which gives how we parsed all the arguments and report
            // an error telling that we were unable to find a checker constructor
            // which matched the given arguments.
            String allArgs = "";
            for (int i = 0; i < types.size(); i++) {
                if (i > 0)
                    allArgs += ", ";
                Class<?> type = types.get(i);
                String arg = argStrings.get(i);
                if (type.equals(long.class)) {
                    arg = arg + "L";
                } else if (type.equals(String.class)) {
                    arg = "\"" + arg + "\"";
                }
                allArgs += arg;
            }
            setError(Error.UNABLE_TO_FIND_CONSTRUCTOR,
                "Unable to find checker constructor which applies to " + name + "(" + allArgs + ")");
            scanner.close();
            return null;
        } catch (Exception e) {
            // Report an error saying we were unable to find a checker class matching
            // the name given.
            setError(Error.UNABLE_TO_FIND_CHECKER,
                "Unable to find checker class '" + name + "'");
            scanner.close();
            return null;
        }
    }

    /**
     * Takes a type and fixes it to use primitive types when possible.
     * @param type The input type.
     * @return A type (int, long, double, boolean, or other)
     */
    private static Class<?> fixType(Class<?> type) {
        if (type.equals(Integer.class))
            return int.class;
        if (type.equals(Long.class))
            return long.class;
        if (type.equals(Double.class))
            return double.class;
        if (type.equals(Boolean.class))
            return boolean.class;
        return type;
    }

}
