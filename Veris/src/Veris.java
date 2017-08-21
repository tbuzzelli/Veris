import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Veris {

    public static final String[] ansFileTypes = { "ans", "out", "sol", "a" };
    public static final String[] inFileTypes = { "in", "data" };
    private HashMap<String, File> answerFiles, inputFiles;

    private BoxWriter boxWriter;
    private Problem problem;
    private Checker checker;
    private ArrayList<Case> cases;
    private File sourceFile;
    private File directory;
    private String className;
    private String language;
    private long longestTime;
    private long totalTime;
    private long timeLimit;
    private File dataFolder;
    private boolean sortCasesBySize = true;
    private boolean isVerbose;

    public Veris() {
        Path tmpDir;
        try {
            tmpDir = Files.createTempDirectory("veris");
            this.directory = tmpDir.toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setChecker(new TokenChecker());
        setTimeLimit(2000);
        setIsVerbose(false);
        boxWriter = new BoxWriter(System.out);
    }

    /**
     * Retrieves the Time Limit, Checker and Data Folder from a Problem.
     * @param problem The Problem to load these settings from.
     */
    public void fromProblem(Problem problem) {
        this.problem = problem;
        setTimeLimit(problem.getTimeLimit());
        setChecker(problem.getChecker());
        try {
            setDataFolder(new File(problem.getDataPath()));
        } catch (IOException e) {
            boxWriter.println("Failed to find data folder '" + problem.getDataPath() + "'");
            System.exit(1);
        }
    }

    /**
     * Sets whether or not the test cases should be sorted by size.
     * @param sortCasesBySize Whether or not the test cases should be
     * sorted by size.
     */
    public void setSortCasesBySize(boolean sortCasesBySize) {
        this.sortCasesBySize = sortCasesBySize;
    }

    /**
     * Sets the time limit for this problem in milliseconds.
     * @param timeLimit The time limit in milliseconds.
     */
    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * Sets whether or not to be verbose when printing.
     * @param isVerbose A boolean representing whether or not verbose printing
     * should be used.
     */
    public void setIsVerbose(boolean isVerbose) {
        this.isVerbose = isVerbose;
    }

    public boolean isVerbose() {
        return isVerbose;
    }

    /**
     * Sets the source file to judge.
     * @param sourceFile The source file to judge. Absolute or relative to
     * running directory.
     * @throws IOException If an IO error was encountered while reading the
     * source file.
     */
    public void setSourceFile(File sourceFile) throws IOException {
        this.sourceFile = sourceFile;
        Path p = sourceFile.toPath();
        Path newSourceFile = directory.toPath().resolve(sourceFile.getName());
        Files.createFile(newSourceFile);

        Files.copy(p, newSourceFile, StandardCopyOption.REPLACE_EXISTING);
        this.className = sourceFile.getName();
        this.language = className.substring(className.lastIndexOf('.')+1);
        className = className.substring(0, className.lastIndexOf('.'));
    }

    /**
     * Sets the checker to use 
     * @param checker
     */
    public void setChecker(Checker checker) {
        this.checker = checker;
    }

    /**
     * Sets the data folder. All input/output files will be used from this
     * folder and from any and all sub folders.
     * @param dataFolder The folder where the data files are.
     * @throws IOException If there an IO error was encountered while reading
     * the data files.
     */
    public void setDataFolder(File dataFolder) throws IOException {
        this.inputFiles = new HashMap<>();
        this.answerFiles = new HashMap<>();
        this.dataFolder = dataFolder;

        addDataFolder(dataFolder);
        this.cases = new ArrayList<>();
        for (String name : inputFiles.keySet()) {
            if (!answerFiles.containsKey(name))
                continue;
            File inputFile = inputFiles.get(name);
            File answerFile = answerFiles.get(name);
            cases.add(new Case(inputFile, answerFile));
        }
    }

    public Verdict testCode() {
        boxWriter.println();
        boxWriter.openBox(80);
        if (problem != null) {
            boxWriter.println();
            boxWriter.centerOn();
            boxWriter.println(problem.getName() + " (" + problem.getFilename() + ")");
            boxWriter.centerOff();
            boxWriter.println();
            boxWriter.printDivider();
        }
        boxWriter.println("Judging solution: " + sourceFile.getName());
        boxWriter.println();
        Verdict result;
        try {
            result = compileCode();
        } catch (Exception e) {
            result = Verdict.INTERNAL_ERROR;
        }
        String caseName = null;
        if (result == Verdict.CORRECT) {
            int digits = 0;
            int n = cases.size();
            while (n > 0) {
                digits++;
                n /= 10;
            }
            if (sortCasesBySize) {
                Collections.sort(cases);
            }
            int cnt = 1;
            boxWriter.println("Running " + cases.size() + " test case" + (cases.size() != 1 ? "s" : ""));
            boxWriter.println();
            for (Case c : cases) {
                Verdict caseResult;
                try {
                    caseResult = runCase(className, c);
                } catch (Exception e) {
                    caseResult = Verdict.INTERNAL_ERROR;
                    e.printStackTrace();
                }
                if (result == Verdict.CORRECT) {
                    result = caseResult;
                    caseName = c.name;
                }
                int caseWidth = 4 + digits;
                if (caseResult != Verdict.CORRECT && isVerbose()) {
                    caseWidth += 2 + c.name.length();
                }
                if (boxWriter.getLineLength() > 1
                        && boxWriter.getRemainingWidth() < caseWidth) {
                    boxWriter.println();
                }
                boxWriter.print("\033[" + caseResult.getColorString() + "m");
                boxWriter.print(" ");
                boxWriter.printf(String.format("%%0%dd", digits), cnt++);
                boxWriter.print(" " + caseResult.getCharacter());
                boxWriter.print(" ");
                if (caseResult != Verdict.CORRECT && isVerbose()) {
                    boxWriter.print("(" + c.name + ") ");
                }
                boxWriter.print("\033[0m");
            }
            boxWriter.println();
            boxWriter.println();
        }
        boxWriter.printDivider();
        boxWriter.print("Verdict: ");
        boxWriter.print(result.getName());
        if (result != Verdict.CORRECT && caseName != null) {
            boxWriter.print(" (on '" + caseName + "')");
        }
        boxWriter.printDivider();
        boxWriter.printf("Worst time: %.2fs\n", longestTime / 1000.0);
        boxWriter.printf("Total time: %.2fs\n", totalTime / 1000.0);
        boxWriter.closeBox();
        
        return result;
    }

    public Verdict compileCode() throws Exception {
        boxWriter.print("Compiling code: ");
        ProcessBuilder builder;
        if(language.equals("java")) {
            builder = new ProcessBuilder("javac", className + ".java");
        } else if(language.equals("c")) {
            builder = new ProcessBuilder("gcc", className + ".c", "-std=c99", "-o", "a");
        }  else if(language.equals("cpp")) {
            builder = new ProcessBuilder("g++", className + ".cpp", "-std=c++11", "-o", "a");
        } else if(language.equals("py")) {
            boxWriter.println("N/A");
            return Verdict.CORRECT;
        } else {
            boxWriter.println("Unknown language " + language);
            return Verdict.INTERNAL_ERROR;
        }
        //builder.redirectError(Redirect.INHERIT);
        builder.directory(directory);
        final Process process = builder.start();
        int resInt = process.waitFor();
        Verdict res;
        if (resInt == 0) {
            res = Verdict.CORRECT;
        } else {
            res = Verdict.COMPILE_ERROR;
        }
        boxWriter.print("\033[" + res.getCompileColorString() + "m");
        boxWriter.print(res.getCharacter());
        boxWriter.print("\033[0m");
        boxWriter.println();
        return res;
    }

    public Verdict runCase(String className, Case c) throws Exception {
        File pOut = new File(directory, className + ".out");
        ProcessBuilder builder = null;
        if(language.equals("java")) {
            builder = new ProcessBuilder("java", className);
        } else if(language.equals("c")) {
            builder = new ProcessBuilder(directory  + "/a");
        }  else if(language.equals("cpp")) {
            builder = new ProcessBuilder(directory  + "/a");
        } else if(language.equals("py")) {
            builder = new ProcessBuilder("python3", className + ".py");
        } else {
            boxWriter.println("Unknown language " + language);
            return Verdict.INTERNAL_ERROR;
        }
        builder.directory(directory);
        builder.redirectOutput(pOut);
        if(isVerbose())
            builder.redirectError(Redirect.INHERIT);

        Process process = builder.start();

        // Pipe them their input
        Thread inputThread = new Thread() {
            public void run() {
                try {
                    FileInputStream inputFIS = new FileInputStream(c.inputFile);
                    OutputStream out = process.getOutputStream();
                    pipe(inputFIS, out);
                    inputFIS.close();
                    out.close();
                } catch(Exception e) {
                }
            }
        };

        long t1 = System.currentTimeMillis();
        inputThread.start();
        boolean completed = process.waitFor(timeLimit, TimeUnit.MILLISECONDS);
        long time = System.currentTimeMillis() - t1;
        t1 = Math.min(timeLimit, time);
        longestTime = Math.max(longestTime, time);
        totalTime += time;

        Verdict res;
        if (!completed) {
            res = Verdict.TIME_LIMIT_EXCEEDED;
            process.destroyForcibly();
        } else {
            if (process.exitValue() != 0) {
                res = Verdict.RUNTIME_ERROR;
            } else {
                res = checker.check(new FastScanner(c.inputFile), new FastScanner(pOut), new FastScanner(c.answerFile));
            }
        }
        return res;
    }

    public void pipe(InputStream is, OutputStream os) throws IOException {
        int n;
        byte[] buffer = new byte[1024*1024];
        while ((n = is.read(buffer)) > -1) {
            os.write(buffer, 0, n);
        }
        os.close();
    }

    void addDataFolder(File folder) {
        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                addDataFolder(f);
                continue;
            }
            String name = f.getName();
            String filetype = "";
            if (name.contains(".")) {
                filetype = name.substring(name.lastIndexOf('.') + 1);
                name = name.substring(0, name.lastIndexOf('.'));
            }
            for (String t : ansFileTypes) {
                if (filetype.equals(t.toLowerCase())) {
                    answerFiles.put(name, f);
                    break;
                }
                if(f.getName().contains("." + t)) {
                    answerFiles.put(f.getName().replace("."+t, ""), f);
                    break;
                }
            }
            for (String t : inFileTypes) {
                if (filetype.equals(t.toLowerCase())) {
                    inputFiles.put(name, f);
                    break;
                }
                if(f.getName().contains("." + t)) {
                    answerFiles.put(f.getName().replace("."+t, ""), f);
                    break;
                }
            }
        }
    }

    class Case implements Comparable<Case> {
        File inputFile, answerFile;
        String name;

        public Case(File inputFile, File answerFile) {
            this.inputFile = inputFile;
            this.answerFile = answerFile;
            name = inputFile.getName();

            File tmp = inputFile.getParentFile();
            while (!tmp.equals(dataFolder)) {
                name = tmp.getName() + "/" + name;
                tmp = tmp.getParentFile();

            }
        }

        @Override
        public int compareTo(Case o) {
            return Long.compare(inputFile.length(), o.inputFile.length());
        }
    }

    /*
    public void gotoxy(int r, int c) {
        char escCode = 0x1B;
        boxWriter.print(String.format("%c[%d;%df", escCode, r, c));
    }

    public void deltaxy(int dr, int dc) {
        char escCode = 0x1B;
        if (dr < 0)
            boxWriter.print(String.format("%c[%dA", escCode, -dr));
        if (dr > 0)
            boxWriter.print(String.format("%c[%dB", escCode, dr));
        if (dc < 0)
            boxWriter.print(String.format("%c[%dD", escCode, -dc));
        if (dc > 0)
            boxWriter.print(String.format("%c[%dC", escCode, dc));
    }

    public void clear() {
        try {
            final String operatingSystem = System.getProperty("os.name");

            if (operatingSystem.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (Exception e) {

        }
    }
*/
}
