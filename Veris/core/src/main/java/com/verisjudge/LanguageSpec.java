package com.verisjudge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LanguageSpec {
	
	public final static String JSON_FIELD_LANGUAGE_NAME = "languageName";
	public final static String JSON_FIELD_FILE_EXTENSIONS = "fileExtensions";
	public final static String JSON_FIELD_DETECT_LANGUAGE_PRIORITY = "detectLanguagePriority";
	public final static String JSON_FIELD_IS_ALLOWED = "isAllowed";
	public final static String JSON_FIELD_NEEDS_COMPILE = "needsCompile";
	public final static String JSON_FIELD_COMPILE_ARGS = "compileArgs";
	public final static String JSON_FIELD_EXECUTION_ARGS = "runtimeArgs";
	
	
	public final static Comparator<LanguageSpec> DETECT_LANGUAGE_COMPARATOR = Comparator.comparing(a -> a.detectLanguagePriority);
	public final static Comparator<LanguageSpec> DISPLAY_COMPARATOR = Comparator.comparing(a -> a.languageName);

	/**
	 * The default value of detectLanguagePriority.
	 */
	public final static long DEFAULT_DETECT_LANGUAGE_PRIORITY = 1;
	
	/**
	 * The display name of this language.
	 * This is used as a unique identifier for a LanguageSpec and thus two specs with the same languageName will be considered the same.
	 */
	private final String languageName;
	/**
	 * A list of file extensions that apply to this language.
	 */
	private final List<String> fileExtensions;
	/**
	 * The value to sort by when auto detecting a language. Smaller values come first.
	 * Defaulted to 1.
	 */
	private final long detectLanguagePriority;
	/**
	 * Whether or not this language is allowed for submissions.
	 */
	private final boolean isAllowed;
	/**
	 * Whether or not this language requires compiling before it can be run.
	 */
	private final boolean needsCompile;
	/**
	 * A list of the args/command that needs to be run to compile solutions of this language.
	 * Can be null if isAllowed or needsCompile is false.
	 * 
	 * "{file}" is replaced by the full name of the file (with extension)
	 * "{filename}" is replaced by the name of the file (not including the extension)
	 */
	private final List<String> compileArgs;
	/**
	 * A list of the args/command that needs to be run to execute solutions of this language.
	 * Can be null if isAllowed or needsCompile is false.
	 * 
	 * "{file}" is replaced by the full name of the file (with extension)
	 * "{filename}" is replaced by the name of the file (not including the extension)
	 */
	private final List<String> executionArgs;
	
	public LanguageSpec(
			String languageName,
			List<String> fileExtensions,
			long detectLanguagePriority,
			boolean isAllowed,
			boolean needsCompile,
			List<String> compileArgs,
			List<String> executionArgs
	) {
		if (languageName == null) {
			throw new IllegalArgumentException("LanguageSpec languageName cannot be null!");
		}
		if (fileExtensions == null) {
			throw new IllegalArgumentException("LanguageSpec fileExtensions cannot be null!");
		}
		if (compileArgs == null) {
			throw new IllegalArgumentException("LanguageSpec compileArgs cannot be null!");
		}
		if (executionArgs == null) {
			throw new IllegalArgumentException("LanguageSpec executionArgs cannot be null!");
		}
		this.languageName = languageName;
		this.detectLanguagePriority = detectLanguagePriority;
		this.fileExtensions = fileExtensions.stream()
				.collect(Collectors.toUnmodifiableList());
		this.isAllowed = isAllowed;
		this.needsCompile = needsCompile;
		this.compileArgs = compileArgs.stream()
				.collect(Collectors.toUnmodifiableList());
		this.executionArgs = executionArgs.stream()
				.collect(Collectors.toUnmodifiableList());
	}
	
	public static LanguageSpec fromJson(JsonObject j) {
        Builder builder = new Builder();
        
        if (j.has(JSON_FIELD_LANGUAGE_NAME)) {
        	JsonElement element = j.get(JSON_FIELD_LANGUAGE_NAME);
        	if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString())
        		builder.setLanguageName(element.getAsString());
        }
        
        if (j.has(JSON_FIELD_FILE_EXTENSIONS)) {
        	builder.setFileExtensions(Config.getStringList(
        			j, JSON_FIELD_FILE_EXTENSIONS));
        }
        
        if (j.has(JSON_FIELD_COMPILE_ARGS)) {
        	builder.setCompileArgs(Config.getStringList(
        			j, JSON_FIELD_COMPILE_ARGS));
        }
        
        if (builder.getCompileArgs() != null) {
        	builder.setNeedsCompile(true);
        }
        
        if (j.has(JSON_FIELD_DETECT_LANGUAGE_PRIORITY)) {
        	JsonElement element = j.get(JSON_FIELD_DETECT_LANGUAGE_PRIORITY);
        	if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
        		try {
        			builder.setDetectLanguagePriority(element.getAsLong());
        		} catch (NumberFormatException e) {
        			e.printStackTrace();
        			// TODO: report error.
        		}
        	}
        }
        
        if (j.has(JSON_FIELD_NEEDS_COMPILE)) {
        	JsonElement element = j.get(JSON_FIELD_NEEDS_COMPILE);
        	if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean())
        		builder.setNeedsCompile(element.getAsBoolean());
        }
        
        if (j.has(JSON_FIELD_EXECUTION_ARGS)) {
        	builder.setExecutionArgs(Config.getStringList(
        			j, JSON_FIELD_EXECUTION_ARGS));
        }
        
        if (builder.getCompileArgs() == null
        		&& builder.getExecutionArgs() == null
        		&& !builder.needsCompile) {
        	builder.setIsAllowed(false);
        }
        
        if (j.has(JSON_FIELD_IS_ALLOWED)) {
        	JsonElement element = j.get(JSON_FIELD_IS_ALLOWED);
        	if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
        		builder.setIsAllowed(element.getAsBoolean());
        	}
        }

        return builder.build();
    }
	
	public String getLanguageName() {
		return languageName;
	}
	
	public List<String> getFileExtensions() {
		return fileExtensions;
	}
	
	public long getDetectLanguagePriority() {
		return detectLanguagePriority;
	}

	public boolean isAllowed() {
		return isAllowed;
	}
	
	public boolean needsCompile() {
		return needsCompile;
	}
	
	public List<String> getCompileArgs() {
		return compileArgs;
	}
	
	public List<String> getExecutionArgs() {
		return executionArgs;
	}
	
	public boolean matchesExtension(String extension) {
		for (String myExtension : fileExtensions)
			if (myExtension.equals(extension))
				return true;
		return false;
	}
	
	public ProcessBuilder getCompileProcessBuilder(String filename, String classname) {
		String[] args = new String[compileArgs.size()];
		for (int i = 0; i < args.length; i++) {
			args[i] = compileArgs.get(i).replace("{file}", filename).replace("{filename}", classname);
		}
		return new ProcessBuilder(args);
	}
	public List<String> getCompileArgs(String directory, String filename, String classname) {
		return compileArgs.stream().map(
				arg -> arg.replace("{dir}", directory).replace("{file}", filename).replace("{filename}", classname)
			).collect(Collectors.toList());
	}
	
	public List<String> getExecutionArgs(String directory, String filename, String classname) {
		return executionArgs.stream().map(
				arg -> arg.replace("{dir}", directory).replace("{file}", filename).replace("{filename}", classname)
			).collect(Collectors.toList());
	}
	
	private JsonArray getFileExtensionsJsonArray() {
		JsonArray jsonArray = new JsonArray();
		for (String fileExtension : getFileExtensions()) {
			jsonArray.add(fileExtension);
		}
		return jsonArray;
	}
	
	private JsonArray getCompileArgsJsonArray() {
		JsonArray jsonArray = new JsonArray();
		for (String fileExtension : getCompileArgs()) {
			jsonArray.add(fileExtension);
		}
		return jsonArray;
	}
	
	private JsonArray getExecutionArgsJsonArray() {
		JsonArray jsonArray = new JsonArray();
		for (String fileExtension : getExecutionArgs()) {
			jsonArray.add(fileExtension);
		}
		return jsonArray;
	}

	public JsonObject toJsonObject() {
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addProperty(JSON_FIELD_LANGUAGE_NAME, getLanguageName());
		jsonObject.addProperty(JSON_FIELD_DETECT_LANGUAGE_PRIORITY, getDetectLanguagePriority());
		jsonObject.addProperty(JSON_FIELD_IS_ALLOWED, isAllowed());
		jsonObject.addProperty(JSON_FIELD_NEEDS_COMPILE, needsCompile());
		jsonObject.add(JSON_FIELD_FILE_EXTENSIONS, getFileExtensionsJsonArray());
		jsonObject.add(JSON_FIELD_COMPILE_ARGS, getCompileArgsJsonArray());
		jsonObject.add(JSON_FIELD_EXECUTION_ARGS, getExecutionArgsJsonArray());

		return jsonObject;
	}
	
	public static List<String> convertStringToArgsList(String str) {
		List<String> argsList = new ArrayList<>();
		StringBuilder currentArg = new StringBuilder();
		boolean inDoubleQuotes = false;
		boolean inSingleQuotes = false;
		boolean isEscaped = false;
		for (char c : str.toCharArray()) {
			if (!isEscaped && c == '\\') {
				isEscaped = !isEscaped;
			} else {
				isEscaped = false;
			}
			if (inDoubleQuotes && !isEscaped && c == '"') {
				inDoubleQuotes = false;
				continue;
			}
			if (inSingleQuotes && !isEscaped && c == '\'') {
				inSingleQuotes = false;
				continue;
			}
			if (inDoubleQuotes || inSingleQuotes) {
				currentArg.append(c);
				continue;
			}
			if (!isEscaped && c == '"') {
				inDoubleQuotes = true;
				continue;
			}
			if (!isEscaped && c == '\'') {
				inSingleQuotes = true;
				continue;
			}
			if (c == ' ' || c == '\t' || c == '\n') {
				if (currentArg.length() > 0) {
					argsList.add(currentArg.toString());
					currentArg = new StringBuilder();
				}
			} else {
				currentArg.append(c);
			}
		}
		if (inDoubleQuotes || inSingleQuotes) {
			// TODO: Throw some incorrect format exception here.
		}
		if (currentArg.length() > 0) {
			argsList.add(currentArg.toString());
			currentArg = new StringBuilder();
		}
		return argsList;
	}

	@Override
	public String toString() {
		return String.format(
				  "{ languageName: %s\n"
				+ "  fileExtensions: %s\n"
				+ "  detectLanguagePriority: %d\n"
				+ "  isAllowed: %s\n"
				+ "  needsCompile: %s\n"
				+ "  compileArgs: %s\n"
				+ "  executionArgs: %s }\n",
				languageName,
				fileExtensions,
				detectLanguagePriority,
				isAllowed,
				needsCompile,
				compileArgs,
				executionArgs);
	}
	
	public static class Builder {
		private String languageName;
		private List<String> fileExtensions = new ArrayList<>();
		private long detectLanguagePriority = DEFAULT_DETECT_LANGUAGE_PRIORITY;
		private boolean isAllowed = true;
		private boolean needsCompile = false;
		private List<String> compileArgs = new ArrayList<>();
		private List<String> executionArgs = new ArrayList<>();
		
		public Builder() {
		}
		
		public LanguageSpec build() {
			return new LanguageSpec(languageName, fileExtensions, detectLanguagePriority, isAllowed, needsCompile, compileArgs, executionArgs);
		}
		
		public String getLanguageName() {
			return languageName;
		}
		
		public Builder setLanguageName(String languageName) {
			this.languageName = languageName;
			return this;
		}
		
		public List<String> getFileExtensions() {
			return fileExtensions;
		}
		
		public Builder setFileExtensions(List<String> fileExtensions) {
			this.fileExtensions = fileExtensions;
			return this;
		}
		
		public long getDetectLanguagePriority() {
			return detectLanguagePriority;
		}
		
		public Builder setDetectLanguagePriority(long detectLanguagePriority) {
			this.detectLanguagePriority = detectLanguagePriority;
			return this;
		}
		
		public boolean isAllowed() {
			return isAllowed;
		}
		
		public Builder setIsAllowed(boolean isAllowed) {
			this.isAllowed = isAllowed;
			return this;
		}
		
		public boolean needsCompile() {
			return needsCompile;
		}
		
		public Builder setNeedsCompile(boolean needsCompile) {
			this.needsCompile = needsCompile;
			return this;
		}
		
		public List<String> getCompileArgs() {
			return compileArgs;
		}
		
		public Builder setCompileArgs(List<String> compileArgs) {
			this.compileArgs = compileArgs;
			return this;
		}
		
		public List<String> getExecutionArgs() {
			return executionArgs;
		}
		
		public Builder setExecutionArgs(List<String> executionArgs) {
			this.executionArgs = executionArgs;
			return this;
		}
	}
	
}
