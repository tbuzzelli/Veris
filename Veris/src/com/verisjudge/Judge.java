package com.verisjudge;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.verisjudge.checker.Checker;
import com.verisjudge.checker.TokenChecker;
import com.verisjudge.utils.BoxWriter;
import com.verisjudge.utils.CheckerUtils;

/*

cd workspace/Checker/bin
java Veris "/ptl/home/acoleman/workspace/Checker/solutions/b.java" "/ptl/home/acoleman/Downloads/data/data/b" -t 100

java -jar Veris.jar a.java a -checker='TokenChecker false' -time_limit=3000 -verbose
java -jar Veris.jar a.java a -c='TokenChecker false' -t=3000 -v

java -jar Veris.jar a.java -contest
java -jar Veris.jar a.java -contest=HSPT.contest -problem=a
java -jar Veris.jar -contest=HSPT.contest -verify


*/
public class Judge {
    public static void usage() {
        System.out.println(
                "usage: java -jar veris.jar source [-data=\"path_to_data_folder\"] [-checker=\"(EpsilonChecker | TokenChecker | CustomChecker) [arguments]\"] [-time_limit=\"1200ms\"] [-verbose]");
        System.exit(1);
    }
    static boolean exited = false;

    public static final Checker DEFAULT_CHECKER = new TokenChecker();
    public static final int DEFAULT_TIME_LIMIT = 1000; // 1 second
    public static final String DEFAULT_CONTEST_FILENAME = "veris.contest";
    public static final String DEFAULT_DATA_FOLDER_PATH = ".";
    public Map<String, String> argMap;
    public String[] args;

    public Judge(String[] args) {
        this.args = args;
        // Read through all the arguments and create the command line argument map.
        // -key=value  =>  key -> value
        // -key  =>  key -> ""
        argMap = new HashMap<>();
        for (String arg : args) {
            if (arg.length() > 0 && arg.charAt(0) == '-') {
                int eqIndex = arg.indexOf('=');
                if (eqIndex == -1) {
                    argMap.put(arg.substring(1), "");
                } else {
                    String key = arg.substring(1, eqIndex);
                    String value = arg.substring(eqIndex + 1);
                    argMap.put(key, value);
                }
            }
        }
    }

    public void run() {
        // Attempt to judge this as a contest.
        judgeFromContest();

        // Create the Veris object.
        Veris veris = new Veris();

        // Get the source file and data folder and give them to Veris.
        String sourceFileString = null;
        if (args.length > 0 && args[0].charAt(0) != '-') {
            sourceFileString = args[0];
        } else {
            File[] files = new File(".").listFiles();
            for (File f : files) {
                if (f.isDirectory()) continue;
                String name = f.getName();
                if (name.endsWith(".java")
                        || name.endsWith(".c")
                        || name.endsWith(".cc")
                        || name.endsWith(".py")) {
                    if (sourceFileString == null) {
                        sourceFileString = name;
                    } else {
                        usage();
                        return;
                    }
                }
            }
            if (sourceFileString == null) {
                usage();
                return;
            }
            System.out.printf("***Inferring solution file of '%s'\n", sourceFileString);
        }
        String dataFolderString = getDataFolderPathFromArgs();
        if (dataFolderString == null) {
            dataFolderString = DEFAULT_DATA_FOLDER_PATH;
        }
        try {
            veris.setSourceFile(new File(sourceFileString));
        } catch (Exception e) {
            exitWithError("Unable to find source file '" + sourceFileString + "'");
            return;
        }
        try {
            veris.setDataFolder(new File(dataFolderString));
        } catch (Exception e) {
            exitWithError("Unable to find or read data folder '" + dataFolderString + "'");
            return;
        }

        // Set whether or not to use verbose printing while judging.
        // Verbose printing will be used if either -verbose or -v is
        // included in the command line arguments.
        veris.setIsVerbose(getIsVerboseFromArgs());

        // Get the checker from the command line arguments.
        Checker checker = getCheckerFromArgs();
        // If no checker was given from command line arguments, use
        // the default checker.
        if (checker == null) {
            checker = DEFAULT_CHECKER;
        }
        // Set the checker in Veris.
        veris.setChecker(checker);

        // Get the time limit from the command line arguments if it
        // was provided using -time_limit=# or -t=#.
        int timeLimit = getTimeLimitFromArgs();
        // If no time limit was given from command line arguments,
        // use the default time limit.
        if (timeLimit == -1) {
            timeLimit = DEFAULT_TIME_LIMIT;
        }
        // Set the time limit in Veris.
        veris.setTimeLimit(timeLimit);

        // Test the code.
        veris.testCode();
        exited = true;
    }

    public void judgeFromContest() {
        if (!hasArg("contest")) {
            return;
        }

        // Get the contest filename from the command line arguments.
        // If -contest is given as an argument without a contest filename,
        // the default contest filename is used.
        String contestFilename = getContestFilenameFromArgs();
        // If the -contest arg was not provided, return without judging.
        if (contestFilename == null) {
            return;
        } else if (contestFilename.isEmpty()) {
            contestFilename = DEFAULT_CONTEST_FILENAME;
        }

        // Attempt to create the contest from the contest file.
        Contest contest = Contest.fromContestFile(contestFilename);
        // If the contest could not be created because the contest file did
        // not exist or it was incorrectly formatted, exit with an error.
        if (contest == null) {
            exitWithError("Either contest file '" + contestFilename
                + "' does not exist or it is incorrectly formatted");
            return;
        }
        // If the contest has errors, print the errors and exit.
        if (contest.hasErrors()) {
            System.out.println("Errors found in contest file '" + contestFilename + "':");
            for (String errorMessage : contest.getErrorMessages()) {
                System.out.println("    " + errorMessage);
            }
            exited = true;
            System.exit(1);
            return;
        }

        if (hasArg("verify")) {
            BoxWriter boxWriter = new BoxWriter(System.out);
            String solutionsPath = contest.getContestPath() + "/" + contest.getJudgeSolutionsPath();
            File[] solutions = contest.getAllSolutions();
            boxWriter.println("Verifying all judge solutions in " + solutionsPath + "...");
            if (solutions == null) {
                boxWriter.println("Solutions folder does not exist or is not a directory");
                boxWriter.close();
                System.exit(1);
            }
            boxWriter.println("\nFound " + solutions.length + " solutions.");
            ArrayList<File> validSolutions = new ArrayList<>();
            ArrayList<File> failedSolutions = new ArrayList<>();
            ArrayList<Verdict> failedSolutionResults = new ArrayList<>();
            ArrayList<File> errorSolutons = new ArrayList<>();
            for (int i = 0; i < solutions.length; i++) {
                File solution = solutions[i];
                Veris veris = new Veris();
                boxWriter.println("===========================================================================\n");
                boxWriter.printf("[%d / %d]: Judging %s ...\n", i + 1, solutions.length, solution.getPath());
                try {
                    veris.setSourceFile(solution);
                } catch (Exception e) {
                    boxWriter.println("Unable to find source file '" + solution.getPath() + "'");
                    errorSolutons.add(solution);
                    continue;
                }
                String problemFilename = contest.inferProblemFilename(solution.getName());
                if (problemFilename != null) {
                    boxWriter.println("Inferring problem filename of " + problemFilename + ".\n");
                } else {
                    boxWriter.println("Unable to infer problem filename from '" + solution.getName() + "'\n");
                    errorSolutons.add(solution);
                    continue;
                }
                Problem problem = contest.getProblem(problemFilename);
                veris.fromProblem(problem);
                veris.setSortCasesBySize(false);
                Verdict result = veris.testCode();
                if (result == Verdict.CORRECT) {
                    validSolutions.add(solution);
                } else {
                    failedSolutions.add(solution);
                    failedSolutionResults.add(result);
                }
            }
            boxWriter.println("===========================================================================");
            boxWriter.println("\n");
            boxWriter.openBox(80);
            boxWriter.println();
            boxWriter.centerOn();
            boxWriter.println("Contest Judge Solution Verification Results");
            boxWriter.centerOff();
            boxWriter.println();
            boxWriter.printDivider();
            boxWriter.printf(" Valid solutions: %d\n", validSolutions.size());
            boxWriter.printDivider();
            boxWriter.printf(" Failed solutions: %d\n", failedSolutions.size());

            for (int i = 0; i < failedSolutions.size(); i++) {
                boxWriter.printf( "   %s\n", failedSolutions.get(i).getPath() + "   (" + failedSolutionResults.get(i).getName() + ")");
            }

            boxWriter.printDivider();
            boxWriter.printf( " Solutions that could not be run: %d\n", errorSolutons.size());

            for (int i = 0; i < errorSolutons.size(); i++) {
                boxWriter.printf( "   %s\n", errorSolutons.get(i).getPath());
            }

            boxWriter.closeBox();
            boxWriter.close();
            System.exit(0);
            return;
        }

        // Create the Veris object.
        Veris veris = new Veris();

        // Get the source filename from the command line arguments.
        String sourceName = args[0];
        // Get the source relative to the contest's submissions path.
        String source = contest.getSubmissionsPath() + "/" + sourceName;
        // Attempt to set the source file in Veris.
        try {
            veris.setSourceFile(new File(source));
        } catch (Exception e) {
            // If the source file could not be set because of an IO error,
            // exit with an error message.
            exitWithError("Unable to find or read source file '" + source + "'");
            return;
        }

        // Get the problem filename in lower case from the command line
        // arguments if it was provided.
        String problemFilename = getProblemFilenameFromArgs();
        // If no problem filename was provided, attempt to infer it from
        // the source filename.
        if (problemFilename == null) {
            problemFilename =
                contest.inferProblemFilename(new File(source).getName());
            // If the filename was inferred, print a message letting the
            // use know how the problem was inferred.
            if (problemFilename != null) {
                System.out.println("Inferring problem filename of "
                    + problemFilename + ".\n");
            }
        }
        // If the problem filename was not provided and could not be
        // inferred from the source filename, exit with an error.
        if (problemFilename == null || problemFilename.isEmpty()) {
            exitWithError("Unable to infer problem filename from '"
                + sourceName + "'");
            return;
        }

        // Attempt to get the problem from the contest with the given
        // or inferred problem filename.
        Problem problem = contest.getProblem(problemFilename);
        // If the problem could not be found, exit with an error.
        if (problem == null) {
            exitWithError("No problem in contest with filename '"
                + problemFilename + "'");
            return;
        }

        // Load the Veris settings from the problem.
        veris.fromProblem(problem);
        // Turn off sorting by case size so the cases will run in the
        // natural order.
        veris.setSortCasesBySize(false);

        // Set whether or not to use verbose printing while judging.
        // Verbose printing will be used if either -verbose or -v is
        // included in the command line arguments.
        veris.setIsVerbose(getIsVerboseFromArgs());

        // Get the checker from the command line arguments.
        Checker checker = getCheckerFromArgs();
        // If a checker was given from the command line arguments, set
        // it in Veris.
        if (checker != null) {
            veris.setChecker(checker);
        }

        // Get the time limit from the command line arguments if it
        // was provided using -time_limit=# or -t=#.
        int timeLimit = getTimeLimitFromArgs();
        // If a time limit was given from command line arguments, set
        // it in veris
        if (timeLimit != -1) {
            veris.setTimeLimit(timeLimit);
        }

        // Test the code.
        System.out.println("Judging " + sourceName + "...");
        veris.testCode();
        exited = true;
        System.exit(0);
    }

    /**
     * Gets the data folder path from the -data or -d arguments.
     * @return The data folder path if provided or null otherwise.
     */
    public String getDataFolderPathFromArgs() {
        return getArg("data", "d");
    }

    /**
     * Gets the problem filename from the -problem or -p arguments.
     * @return The problem filename (in lower case) if provided or null otherwise.
     */
    public String getProblemFilenameFromArgs() {
        String problemFilename = getArg("problem", "p");
        if (problemFilename == null) {
            return null;
        }
        return problemFilename.toLowerCase();
    }

    /**
     * Gets the contest filename from the -contest argument.
     * @return The contest filename if provided or null otherwise.
     */
    public String getContestFilenameFromArgs() {
        return getArg("contest");
    }

    /**
     * Returns whether or not a command line arg was given requesting verbose
     * printing while judging.
     * @return Whether or not verbose printing should be used while judging.
     */
    public boolean getIsVerboseFromArgs() {
        return hasArg("verbose") || hasArg("v");
    }

    /**
     * Gets the time limit string from either the -time_limit or -t arg.
     * @return The time limit string if provided or null otherwise.
     */
    public String getTimeLimitStringFromArgs() {
        return getArg("time_limit", "t");
    }

    /**
     * Reads the time limit from command line args (-time_limit or -t) and returns
     * the time limit if it was parsed correctly and was in the allowed range.<br/>
     * If no time limit was provided, -1 is returned.<br/>
     * If the time limit could not be parsed or if it was out of the allowed range,
     * the Judge will exit with an error message.
     * @return The time limit in milliseconds or -1 if no time limit was provided.
     */
    public int getTimeLimitFromArgs() {
        String timeLimitString = getTimeLimitStringFromArgs();
        if (timeLimitString == null) {
            return -1;
        }
        Integer timeLimitInteger = Problem.parseTimeLimit(timeLimitString);
        if (timeLimitInteger == null) {
            exitWithError("Failed to parse time limit from '" + timeLimitString + "'");
            return -1;
        }
        int timeLimit = timeLimitInteger.intValue();
        if (timeLimit < Problem.MINIMUM_TIME_LIMIT
            || timeLimit > Problem.MAXIMUM_TIME_LIMIT) {
            exitWithError("Time limit of " + timeLimit + "ms is out of acceptable range ["
                + Problem.MINIMUM_TIME_LIMIT + "ms, " + Problem.MAXIMUM_TIME_LIMIT + "ms]");
            return -1;
        }
        return timeLimitInteger.intValue();
    }

    /**
     * Gets the checker string from either the -checker or -c arg.
     * @return The checker string if provided or null otherwise.
     */
    public String getCheckerStringFromArgs() {
        return getArg("checker", "c");
    }

    /**
     * Gets the checker from command line arguments if it was either -checker
     * or -c was provided.<br/>
     * If no checker was provided, null is returned.<br/>
     * If an error occurred while trying to parse the checker given and create the
     * checker object, the Judge will exit with an error message.
     * @return The checker object if provided in the command line arguments or
     * null if no checker was provided.
     */
    public Checker getCheckerFromArgs() {
        String checkerString = getCheckerStringFromArgs();
        if (checkerString == null) {
            return null;
        }
        CheckerUtils checkerUtils = new CheckerUtils();
        Checker checker = checkerUtils.getCheckerFromString(checkerString);
        if (checkerUtils.hasError()) {
            exitWithError(checkerUtils.getErrorMessage());
            return null;
        }
        return checker;
    }

    /**
     * Gets the string from a list of command line argument keys.<br/>
     * Command line arguments are parsed according to the following:<br/>
     * -key=value or -key
     * @param keys A list of the keys to get the string from in the order of
     * preference.
     * @return The string given in the command line arguments matching one
     * of the keys given or null if none of the keys were included in the
     * command line arguments.
     */
    public String getArg(String... keys) {
        for (String key : keys) {
            if (hasArg(key)) {
                return getArg(key);
            }
        }
        return null;
    }

    /**
     * Gets the string for a particular key from the command line arguments.
     * @param key The key for the argument to retrieve.
     * @return The string given with the format -key=value, "" if only
     * -key was provided, or null if no argument was given matching the key.
     */
    public String getArg(String key) {
        return argMap.get(key);
    }

    /**
     * Checks whether or not a command line argument was provided matching the
     * given key.
     * @param key The key to look for in command line arguments.
     * @return A boolean representing whether or not an argument was provided
     * matching the given key.
     */
    public boolean hasArg(String key) {
        return argMap.containsKey(key);
    }

    /**
     * Prints an error message and exits with an error code of 1.
     * @param errorMessage The error message to print.
     */
    public void exitWithError(String errorMessage) {
        System.out.println("\n" + errorMessage);
        exited = true;
        System.exit(1);
    }

    public static void main(String[] args) {
        // Create a shutdown hook to print blank lines when being shut down.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if(!exited) {
                    System.out.println();
                    System.out.println();
                }
            }
        });
        Judge judge = new Judge(args);
        judge.run();
    }
}
