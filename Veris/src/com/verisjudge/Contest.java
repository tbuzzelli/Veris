package com.verisjudge;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Contest {

    enum Error {
        MISSING_NAME,
        MISSING_CONTEST_PATH,
        MISSING_SUBMISSIONS_PATH,
        MISSING_PROBLEMS,
        DUPLICATE_PROBLEM_FILENAME,
        PROBLEM_ERROR
    }
    public static final String JSON_FIELD_NAME = "name";
    public static final String JSON_FIELD_CONTEST_PATH = "contest_path";
    public static final String JSON_FIELD_SUBMISSIONS_PATH = "submissions_path";
    public static final String JSON_FIELD_JUDGE_SOLUTIONS_PATH = "judge_solutions_path";
    public static final String JSON_FIELD_PROBLEMS = "problems";

    private final String name;
    private final String contestPath;
    private final String submissionsPath;
    private final String judgeSolutionsPath;
    private final Problem[] problems;
    private final HashMap<String, Problem> problemMap;
    private final Error[] errors;
    private final String[] errorMessages;

    public Contest(String name, String contestPath, String submissionsPath, String judgeSolutionsPath, Problem[] problems) {
        this.name = name;
        this.contestPath = contestPath;
        this.submissionsPath = submissionsPath;
        this.judgeSolutionsPath = judgeSolutionsPath != null ? judgeSolutionsPath : "";
        this.problems = problems.clone();
        problemMap = new HashMap<>();
        for (Problem p : problems) {
            problemMap.put(p.getFilename(), p);
            p.setContest(this);
        }

        ArrayList<Error> errorsList = new ArrayList<>();
        ArrayList<String> errorMessagesList = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            errorsList.add(Error.MISSING_NAME);
            errorMessagesList.add("Missing contest name");
        }
        if (contestPath == null || contestPath.isEmpty()) {
            errorsList.add(Error.MISSING_CONTEST_PATH);
            errorMessagesList.add("Missing contest path");
        }
        if (submissionsPath == null || submissionsPath.isEmpty()) {
            errorsList.add(Error.MISSING_SUBMISSIONS_PATH);
            errorMessagesList.add("Missing submissions path");
        }
        if (problems != null) {
            HashSet<String> problemFilenames = new HashSet<>();
            HashSet<String> duplicatesFound = new HashSet<>();
            for (Problem problem : problems) {
                String filename = problem.getFilename();
                if (filename == null || filename.isEmpty())
                    continue;
                if (problemFilenames.contains(filename)) {
                    if (!duplicatesFound.contains(filename)) {
                        duplicatesFound.add(filename);
                        errorsList.add(Error.DUPLICATE_PROBLEM_FILENAME);
                        errorMessagesList.add("More than one problem has the same filename of '" + filename + "'");
                    }
                } else {
                    problemFilenames.add(filename);
                }
            }
        }
        if (problems == null || problems.length == 0) {
            errorsList.add(Error.MISSING_PROBLEMS);
            errorMessagesList.add("No problems provided");
        }
        if (problems != null) {
            for (int i = 0; i < problems.length; i++) {
                Problem problem = problems[i];
                if (problem.hasErrors()) {
                    for (String problemErrorMessage : problem.getErrorMessages()) {
                        errorsList.add(Error.PROBLEM_ERROR);
                        String problemNameAndNumber = "#" + i;
                        if (problem.getName() != null) {
                            problemNameAndNumber += " [" + problem.getName() + "]";
                        }
                        errorMessagesList.add("Problem " + problemNameAndNumber + ": " + problemErrorMessage);
                    }
                }
            }
        }

        errors = errorsList.toArray(new Error[0]);
        errorMessages = errorMessagesList.toArray(new String[0]);
    }

    public static Contest fromContestFile(String filename) {
        return fromContestFile(new File(filename));
    }

    public static Contest fromContestFile(File f) {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            br.close();
            return fromJsonString(sb.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static Contest fromJsonString(String str) {
        JsonParser parser = new JsonParser();
        try {
            return fromJson(parser.parse(str).getAsJsonObject());
        } catch (Exception e) {
            return null;
        }
    }

    public static Contest fromJson(JsonObject j) {
        Builder builder = new Builder();
        try {
            builder.setName(j.get(JSON_FIELD_NAME).getAsString());
        } catch (Exception e) {}
        try {
            builder.setContestPath(j.get(JSON_FIELD_CONTEST_PATH).getAsString());
        } catch (Exception e) {}
        try {
            builder.setSubmissionsPath(j.get(JSON_FIELD_SUBMISSIONS_PATH).getAsString());
        } catch (Exception e) {}
        try {
            builder.setJudgeSolutionsPath(j.get(JSON_FIELD_JUDGE_SOLUTIONS_PATH).getAsString());
        } catch (Exception e) {}
        try {
            JsonArray arr = j.get(JSON_FIELD_PROBLEMS).getAsJsonArray();
            for (JsonElement elem : arr) {
                try {
                    builder.addProblem(Problem.fromJson(elem.getAsJsonObject()));
                } catch (Exception e) {}
            }
        } catch (Exception e) {}

        return builder.build();
    }

    public String getName() {
        return name;
    }

    public String getContestPath() {
        return contestPath;
    }

    public String getSubmissionsPath() {
        return submissionsPath;
    }

    public String getJudgeSolutionsPath() {
        return judgeSolutionsPath;
    }

    public Problem[] getProblems() {
        return problems;
    }

    public Problem getProblem(String problemName) {
        return problemMap.get(problemName);
    }

    public boolean hasErrors() {
        return getErrors() != null && getErrors().length > 0;
    }
 
    public Error[] getErrors() {
        return errors;
    }

    public String[] getErrorMessages() {
        return errorMessages;
    }

    public String inferProblemFilename(String source) {
        if (source != null && source.split("\\.").length > 0) {
            String potentialName = source.split("\\.")[0].toLowerCase();
            for (int i = potentialName.length(); i > 0; i--) {
                if (getProblem(potentialName.substring(0, i)) != null) {
                    return potentialName.substring(0, i);
                }
            }
        }
        return null;
    }

    public File[] getAllSolutions() {
        String solutionsPath = getContestPath() + "/" + getJudgeSolutionsPath();
        File solutionsFolder = new File(solutionsPath);
        if (!solutionsFolder.exists() || !solutionsFolder.isDirectory()) {
            return null;
        }
        ArrayList<File> solutions = new ArrayList<>();
        ArrayDeque<File> q = new ArrayDeque<>();
        q.add(solutionsFolder);
        while (!q.isEmpty()) {
            File f = q.poll();
            if (f.isDirectory()) {
                for (File child : f.listFiles())
                    q.add(child);
            } else if (f.getName().endsWith(".java")
                || f.getName().endsWith(".c")
                || f.getName().endsWith(".cpp")
                || f.getName().endsWith(".py")) {
                solutions.add(f);
            }
        }
        return solutions.toArray(new File[0]);
    }

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(toJson());
    }

    public JsonObject toJson() {
        JsonObject j = new JsonObject();
        if (getName() != null)
            j.addProperty(JSON_FIELD_NAME, getName());
        if (getContestPath() != null)
            j.addProperty(JSON_FIELD_CONTEST_PATH, getContestPath());
        if (getSubmissionsPath() != null)
            j.addProperty(JSON_FIELD_SUBMISSIONS_PATH, getSubmissionsPath());
        if (getSubmissionsPath() != null && !getJudgeSolutionsPath().isEmpty())
            j.addProperty(JSON_FIELD_JUDGE_SOLUTIONS_PATH, getJudgeSolutionsPath());
        if (getProblems() != null) {
            JsonArray arr = new JsonArray();
            for (Problem p : getProblems()) {
                if (p == null) continue;
                arr.add(p.toJson());
            }
            j.add(JSON_FIELD_PROBLEMS, arr);
        }
        return j;
    }

    @Override
    public String toString() {
        return toJsonString();
    }

    static class Builder {
        private String name;
        private String contestPath;
        private String submissionsPath;
        private String judgeSolutionsPath = "";
        private ArrayList<Problem> problems;

        public Builder() {
            problems = new ArrayList<>();
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setContestPath(String contestPath) {
            this.contestPath = contestPath;
            return this;
        }

        public Builder setSubmissionsPath(String submissionsPath) {
            this.submissionsPath = submissionsPath;
            return this;
        }

        public Builder setJudgeSolutionsPath(String judgeSolutionsPath) {
            this.judgeSolutionsPath = judgeSolutionsPath;
            return this;
        }

        public Builder addProblem(Problem problem) {
            problems.add(problem);
            return this;
        }

        public Contest build() {
            Problem[] problemsArray = problems.toArray(new Problem[0]);
            return new Contest(name, contestPath, submissionsPath, judgeSolutionsPath, problemsArray);
        }
    }
}
