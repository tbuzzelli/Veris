package com.verisjudge;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.verisjudge.checker.Checker;
import com.verisjudge.utils.CheckerUtils;

public class Problem {

    enum Error {
        MISSING_NAME,
        MISSING_FILENAME,
        INVALID_FILENAME,
        MISSING_DATA_PATH,
        MISSING_TIME_LIMIT,
        INVALID_TIME_LIMIT,
        CHECKER_ERROR
    }

    public static final long MINIMUM_TIME_LIMIT = 100; // 100 milliseconds
    public static final long MAXIMUM_TIME_LIMIT = 60 * 60 * 1000; // 1 hour
    public static final String JSON_FIELD_NAME = "name";
    public static final String JSON_FIELD_FILENAME = "filename";
    public static final String JSON_FIELD_DATA_PATH = "data_path";
    public static final String JSON_FIELD_TIME_LIMIT = "time_limit";
    public static final String JSON_FIELD_CHECKER_STRING = "checker";

    private Contest contest;
    private final String name;
    private final String filename;
    private final String dataPath;
    private final long timeLimit;
    private final String timeLimitString;
    private final String checkerString;
    private final Checker checker;
    private final Error[] errors;
    private final String[] errorMessages;
    private final CheckerUtils.Error checkerError;
    private final String checkerErrorMessage;

    public Problem(Contest contest, String name, String filename, String dataPath, String timeLimitString, String checkerString) {
        this.contest = contest;
        this.name = name;
        this.filename = filename;
        this.dataPath = dataPath;
        this.timeLimitString = timeLimitString;
        this.checkerString = checkerString;

        ArrayList<Error> errorsList = new ArrayList<>();
        ArrayList<String> errorMessagesList = new ArrayList<>();

        CheckerUtils checkerUtils = new CheckerUtils();
        this.checker = checkerUtils.getCheckerFromString(checkerString);

        if (name == null || name.isEmpty()) {
            errorsList.add(Error.MISSING_NAME);
            errorMessagesList.add("Missing problem name");
        }
        if (filename == null || filename.isEmpty()) {
            errorsList.add(Error.MISSING_FILENAME);
            errorMessagesList.add("Missing problem filename");
        } else if (!isFilenameValid(filename)) {
            errorsList.add(Error.INVALID_FILENAME);
            errorMessagesList.add("Filename '" + filename + "' is invalid");
        }
        if (dataPath == null || dataPath.isEmpty()) {
            errorsList.add(Error.MISSING_DATA_PATH);
            errorMessagesList.add("Missing problem data path");
        }
        if (timeLimitString == null || timeLimitString.isEmpty()) {
            timeLimit = -1;
            errorsList.add(Error.MISSING_TIME_LIMIT);
            errorMessagesList.add("Missing time limit");
        } else {
            Long timeLimitLong = parseTimeLimit(timeLimitString);
            if (timeLimitLong == null) {
                timeLimit = -1;
                errorsList.add(Error.INVALID_TIME_LIMIT);
                errorMessagesList.add(
                    "Unable to parse time limit from '" + timeLimitString + "'");
            } else {
                timeLimit = timeLimitLong.longValue();
                if (timeLimit < MINIMUM_TIME_LIMIT || timeLimit > MAXIMUM_TIME_LIMIT) {
                    errorsList.add(Error.INVALID_TIME_LIMIT);
                    errorMessagesList.add(
                        "Time limit of " + timeLimit + "ms is out of acceptable range ["
                            + MINIMUM_TIME_LIMIT + "ms, " + MAXIMUM_TIME_LIMIT + "ms]");
                }
            }
        }
        if (checkerUtils.hasError()) {
            checkerError = checkerUtils.getError();
            checkerErrorMessage = checkerUtils.getErrorMessage();
            errorsList.add(Error.CHECKER_ERROR);
            errorMessagesList.add(checkerErrorMessage);
        } else {
            checkerError = null;
            checkerErrorMessage = null;
        }

        errors = errorsList.toArray(new Error[0]);
        errorMessages = errorMessagesList.toArray(new String[0]);
    }

    public static Problem fromJson(JsonObject j) {
        Builder builder = new Builder();
        try {
            builder.setName(j.get(JSON_FIELD_NAME).getAsString());
        } catch (Exception e) {}
        try {
            builder.setFilename(j.get(JSON_FIELD_FILENAME).getAsString().toLowerCase());
        } catch (Exception e) {}
        try {
            builder.setDataPath(j.get(JSON_FIELD_DATA_PATH).getAsString());
        } catch (Exception e) {}
        try {
            builder.setTimeLimitString(j.get(JSON_FIELD_TIME_LIMIT).getAsString());
        } catch (Exception e) {}
        try {
            builder.setCheckerString(j.get(JSON_FIELD_CHECKER_STRING).getAsString());
        } catch (Exception e) {}

        return builder.build();
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }

    public Contest getContest() {
        return contest;
    }

    public String getName() {
        return name;
    }

    public String getFilename() {
        return filename;
    }

    public String getDataPath() {
        if (contest != null) {
            return contest.getContestPath() + "/" + getRelativeDataPath();
        }
        return getRelativeDataPath();
    }

    public String getRelativeDataPath() {
        return dataPath;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public String getTimeLimitString() {
        return timeLimitString;
    }

    public String getCheckerString() {
        return checkerString;
    }

    public Checker getChecker() {
        return checker;
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

    public boolean hasCheckerError() {
        return getCheckerError() != null;
    }

    public CheckerUtils.Error getCheckerError() {
        return checkerError;
    }

    public String getCheckerErrorMessage() {
        return checkerErrorMessage;
    }

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(toJson());
    }

    public JsonObject toJson() {
        JsonObject j = new JsonObject();
        if (getName() != null)
            j.addProperty(JSON_FIELD_NAME, getName());
        if (getFilename() != null)
            j.addProperty(JSON_FIELD_FILENAME, getFilename());
        if (getDataPath() != null)
            j.addProperty(JSON_FIELD_DATA_PATH, getRelativeDataPath());
        if (getTimeLimitString() != null)
            j.addProperty(JSON_FIELD_TIME_LIMIT, getTimeLimitString());
        if (getCheckerString() != null)
            j.addProperty(JSON_FIELD_CHECKER_STRING, getCheckerString());
        return j;
    }

    @Override
    public String toString() {
        return toJsonString();
    }

    public static boolean isFilenameValid(String filename) {
        if (filename == null || filename.isEmpty())
            return false;
        // Problem filenames cannot contain spaces
        if (filename.contains(" "))
            return false;
        // Problem filename must begin with a letter
        if (!Character.isAlphabetic(filename.charAt(0)))
            return false;
        return true;
    }

    public static Long parseTimeLimit(String timeLimitString) {
        timeLimitString = timeLimitString.toLowerCase().replace(" ", "");
        try {
            return Long.parseLong(timeLimitString);
        } catch (Exception e) {}
        if (timeLimitString.endsWith("ms")) { // milliseconds
            try {
                return Long.parseLong(timeLimitString.substring(
                    0, timeLimitString.length() - 2));
            } catch (Exception e) {
                return null;
            }
        } else if (timeLimitString.endsWith("ns")) { // nanoseconds
            try {
                return (Long.parseLong(timeLimitString.substring(
                    0, timeLimitString.length() - 2)) + 999) / 1000;
            } catch (Exception e) {
                return null;
            }
        } else if (timeLimitString.endsWith("s")) { // seconds
            try {
                return Long.parseLong(timeLimitString.substring(
                    0, timeLimitString.length() - 1)) * 1000;
            } catch (Exception e) {
                return null;
            }
        } else { // assume milliseconds
        	try {
                return Long.parseLong(timeLimitString.substring(
                    0, timeLimitString.length() - 2));
            } catch (Exception e) {
                return null;
            }
        }
    }

    static class Builder {
        private Contest contest;
        private String name;
        private String filename;
        private String dataPath;
        private String timeLimitString;
        private String checkerString;

        public Builder setContest(Contest contest) {
            this.contest = contest;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder setDataPath(String dataPath) {
            this.dataPath = dataPath;
            return this;
        }

        public Builder setTimeLimitString(String timeLimitString) {
            this.timeLimitString = timeLimitString;
            return this;
        }

        public Builder setCheckerString(String checkerString) {
            this.checkerString = checkerString;
            return this;
        }

        public Problem build() {
            return new Problem(
                contest,
                name,
                filename,
                dataPath,
                timeLimitString,
                checkerString);
        }
    }
}
