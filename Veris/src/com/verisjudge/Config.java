package com.verisjudge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.verisjudge.utils.FileUtils;

public class Config {

	public final static String JSON_FIELD_INPUT_FILE_TYPES = "inputFileTypes";
	public final static String JSON_FIELD_OUTPUT_FILE_TYPES = "outputFileTypes";
	public final static String JSON_FIELD_DEFAULT_TIME_LIMIT = "defaultTimeLimit";
	public final static String JSON_FIELD_MINIMUM_TIME_LIMIT = "minimumTimeLimit";
	public final static String JSON_FIELD_MAXIMUM_TIME_LIMIT = "maximumTimeLimit";
	public final static String JSON_FIELD_LANGUAGE_SPECS = "languageSpecs";
	
	public final static String JSON_FIELD_LANGUAGE_SPEC_LANGUAGE_NAME = "languageName";
	public final static String JSON_FIELD_LANGUAGE_SPEC_FILE_EXTENSIONS = "fileExtensions";
	public final static String JSON_FIELD_LANGUAGE_SPEC_DETECT_LANGUAGE_PRIORITY = "detectLanguagePriority";
	public final static String JSON_FIELD_LANGUAGE_SPEC_IS_ALLOWED = "isAllowed";
	public final static String JSON_FIELD_LANGUAGE_SPEC_NEEDS_COMPILE = "needsCompile";
	public final static String JSON_FIELD_LANGUAGE_SPEC_COMPILE_ARGS = "compileArgs";
	public final static String JSON_FIELD_LANGUAGE_SPEC_EXECUTION_ARGS = "runtimeArgs";
	
	public final static String USER_CONFIG_FILENAME = "config.json";
	public final static String DEFAULT_CONFIG_RESOURCE_PATH = "/default_config.json";
	
	private static Config CONFIG;
	
	private final String[] inputFileTypes;
	private final String[] outputFileTypes;
	
	private final Long defaultTimeLimit;
	private final Long minimumTimeLimit;
	private final Long maximumTimeLimit;
	
	private final LanguageSpec[] languageSpecs;
	private final LanguageSpec[] languageSpecsForDetectLanguage;
	private final LanguageSpec[] languageSpecsForDisplay;

	public Config(String[] inputFileTypes, String[] outputFileTypes, Long defaultTimeLimit, Long minimumTimeLimit,
			Long maximumTimeLimit, LanguageSpec[] languageSpecs) {
		this.inputFileTypes = inputFileTypes;
		this.outputFileTypes = outputFileTypes;
		this.defaultTimeLimit = defaultTimeLimit;
		this.minimumTimeLimit = minimumTimeLimit;
		this.maximumTimeLimit = maximumTimeLimit;
		this.languageSpecs = languageSpecs;
		this.languageSpecsForDetectLanguage = languageSpecs == null ? new LanguageSpec[0] : languageSpecs.clone();
		Arrays.sort(this.languageSpecsForDetectLanguage, LanguageSpec.DETECT_LANGUAGE_COMPARATOR);
		this.languageSpecsForDisplay = languageSpecs == null ? new LanguageSpec[0] : languageSpecs.clone();
		Arrays.sort(this.languageSpecsForDisplay, LanguageSpec.DISPLAY_COMPARATOR);
	}
	
	public static Config fromConfigFile(String filename) {
		return fromConfigFile(new File(filename));
	}
	
	public static Config fromConfigFile(File f) {
		try {
            return fromConfigInputStream(new FileInputStream(f));
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
	}
	
	public static Config fromConfigInputStream(InputStream inputStream) {
		return fromJsonString(FileUtils.readEntireInputStream(inputStream));
	}
	
	public static Config fromJsonString(String str) {
        JsonParser parser = new JsonParser();
        try {
        	return fromJson(parser.parse(str).getAsJsonObject());
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }
	
	public static Config fromJson(JsonObject j) {
		Builder builder = new Builder();

		if (j.has(Config.JSON_FIELD_INPUT_FILE_TYPES)) {
			builder.setInputFileTypes(Config.getStringArray(
        			j, Config.JSON_FIELD_INPUT_FILE_TYPES));
        }
        
        if (j.has(Config.JSON_FIELD_OUTPUT_FILE_TYPES)) {
        	builder.setOutputFileTypes(Config.getStringArray(
        			j, Config.JSON_FIELD_OUTPUT_FILE_TYPES));
        }
        
        if (j.has(Config.JSON_FIELD_DEFAULT_TIME_LIMIT)) {
        	JsonElement element = j.get(Config.JSON_FIELD_DEFAULT_TIME_LIMIT);
        	if (element.isJsonPrimitive()) {
        		Long defaultTimeLimit = Problem.parseTimeLimit(element.getAsString());
        		if (defaultTimeLimit != null) {
        			builder.setDefaultTimeLimit(defaultTimeLimit);
        		}
        	}
        }
        
        if (j.has(Config.JSON_FIELD_MINIMUM_TIME_LIMIT)) {
        	JsonElement element = j.get(Config.JSON_FIELD_MINIMUM_TIME_LIMIT);
        	if (element.isJsonPrimitive()) {
        		Long defaultTimeLimit = Problem.parseTimeLimit(element.getAsString());
        		if (defaultTimeLimit != null) {
        			builder.setMinimumTimeLimit(defaultTimeLimit);
        		}
        	}
        }
        
        if (j.has(Config.JSON_FIELD_MAXIMUM_TIME_LIMIT)) {
        	JsonElement element = j.get(Config.JSON_FIELD_MAXIMUM_TIME_LIMIT);
        	if (element.isJsonPrimitive()) {
        		Long defaultTimeLimit = Problem.parseTimeLimit(element.getAsString());
        		if (defaultTimeLimit != null) {
        			builder.setMaximumTimeLimit(defaultTimeLimit);
        		}
        	}
        }
        
        if (j.has(Config.JSON_FIELD_LANGUAGE_SPECS)) {
        	JsonElement jElement = j.get(Config.JSON_FIELD_LANGUAGE_SPECS);
    		if (jElement.isJsonArray()) {
	    		JsonArray jArray = jElement.getAsJsonArray();
	    		int size = jArray.size();
	    		LanguageSpec[] arr = new LanguageSpec[size];
	    		boolean isValid = true;
	    		for (int i = 0; i < size; i++) {
	    			JsonElement arrayElement = jArray.get(i);
	    			if (!arrayElement.isJsonObject()) {
	    				isValid = false;
	    				// TODO: Throw error.
	    				break;
	    			}
	    			arr[i] = LanguageSpec.fromJson(arrayElement.getAsJsonObject());
	    		}
	    		if (isValid) {
		        	builder.setLanguageSpecs(arr);
	    		}
    		} else {
    			// TODO: Throw error.
    		}
        }
        
		return builder.build();
	}
	
	/**
	 * Merges the two Configs having newConfig override anything they both have set.
	 * @param defaultConfig The Config which has all the default info.
	 * @param newConfig The Config which should override defaults.
	 * @return A Config of the two merged together.
	 */
	public static Config merge(Config defaultConfig, Config newConfig) {
		Builder builder = new Builder();

		if (newConfig.hasInputFileTypes()) {
			builder.setInputFileTypes(newConfig.getInputFileTypes());
		} else {
			builder.setInputFileTypes(defaultConfig.getInputFileTypes());
		}
		
		if (newConfig.hasOutputFileTypes()) {
			builder.setOutputFileTypes(newConfig.getOutputFileTypes());
		} else {
			builder.setOutputFileTypes(defaultConfig.getOutputFileTypes());
		}
		
		if (newConfig.hasDefaultTimeLimit()) {
			builder.setDefaultTimeLimit(newConfig.getDefaultTimeLimit());
		} else {
			builder.setDefaultTimeLimit(defaultConfig.getDefaultTimeLimit());
		}
		
		if (newConfig.hasMinimumTimeLimit()) {
			builder.setMinimumTimeLimit(newConfig.getMinimumTimeLimit());
		} else {
			builder.setMinimumTimeLimit(defaultConfig.getMinimumTimeLimit());
		}
		
		if (newConfig.hasMaximumTimeLimit()) {
			builder.setMaximumTimeLimit(newConfig.getMaximumTimeLimit());
		} else {
			builder.setMaximumTimeLimit(defaultConfig.getMaximumTimeLimit());
		}
		
		ArrayList<LanguageSpec> languageSpecs = new ArrayList<>();
		if (newConfig.hasLanguageSpecs()) {
			for (LanguageSpec languageSpec : newConfig.getLanguageSpecs())
				languageSpecs.add(languageSpec);
		}
		if (defaultConfig.hasLanguageSpecs()) {
			for (LanguageSpec languageSpec : defaultConfig.getLanguageSpecs()) {
				boolean isRepeat = false;
				for (LanguageSpec newLanguageSpec : languageSpecs) {
					if (languageSpec.getLanguageName().equals(newLanguageSpec.getLanguageName())) {
						isRepeat = true;
						break;
					}
				}
				if (!isRepeat) {
					languageSpecs.add(languageSpec);
				}
			}
		}
		builder.setLanguageSpecs(languageSpecs.toArray(new LanguageSpec[0]));
		return builder.build();
	}

	public static Config getConfig() {
		if (CONFIG != null)
			return CONFIG;
		CONFIG = fromConfigInputStream(Config.class.getResourceAsStream(DEFAULT_CONFIG_RESOURCE_PATH));
		if (CONFIG == null) {
			System.err.println("Failed to load internal config!");
			System.exit(1);
			// TODO: Show pop-up error message.
		}
		createUserConfigFileIfNeeded();
		Config userConfig = fromConfigFile(FileUtils.getConfigFile(USER_CONFIG_FILENAME));
		if (userConfig != null) {
			CONFIG = merge(CONFIG, userConfig);
		}
		return CONFIG;
	}
	
	public static boolean createUserConfigFileIfNeeded() {
		File userConfigFile = FileUtils.getConfigFile(USER_CONFIG_FILENAME);
		// If the user config file already exists, we don't need to create it.
		if (userConfigFile.exists()) {
			return true;
		}
		
		// Copy the default config to the user config file.
		try {
			InputStream inputStream = Config.class.getResourceAsStream(DEFAULT_CONFIG_RESOURCE_PATH);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(userConfigFile));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				bufferedWriter.write(line);
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
			bufferedReader.close();
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			
			return false;
		}
	}
	
	public String[] getInputFileTypes() {
		return inputFileTypes;
	}

	public boolean hasInputFileTypes() {
		return inputFileTypes != null;
	}
	
	public String[] getOutputFileTypes() {
		return outputFileTypes;
	}
	
	public boolean hasOutputFileTypes() {
		return outputFileTypes != null;
	}

	public Long getDefaultTimeLimit() {
		return defaultTimeLimit;
	}
	
	public boolean hasDefaultTimeLimit() {
		return defaultTimeLimit != null;
	}

	public Long getMinimumTimeLimit() {
		return minimumTimeLimit;
	}
	
	public boolean hasMinimumTimeLimit() {
		return minimumTimeLimit != null;
	}

	public Long getMaximumTimeLimit() {
		return maximumTimeLimit;
	}
	
	public boolean hasMaximumTimeLimit() {
		return maximumTimeLimit != null;
	}

	public LanguageSpec[] getLanguageSpecs() {
		return languageSpecs;
	}
	
	public boolean hasLanguageSpecs() {
		return languageSpecs != null;
	}
	
	public LanguageSpec[] getLanguageSpecsForDetectLanguage() {
		return languageSpecsForDetectLanguage;
	}
	
	public LanguageSpec[] getLanguageSpecsForDisplay() {
		return languageSpecsForDisplay;
	}
	
	public LanguageSpec getLanguageSpecForExtension(String extension) {
		for (LanguageSpec languageSpec : getLanguageSpecsForDetectLanguage()) {
			if (languageSpec != null && languageSpec.matchesExtension(extension)) {
				return languageSpec;
			}
		}
		return null;
	}
	
	public static String[] getStringArray(JsonObject j, String fieldName) {
		if (!j.has(fieldName))
			return null; // Throw error (if appropriate).
		JsonElement jElement = j.get(fieldName);
		if (!jElement.isJsonArray())
			return null; // Throw error.
		JsonArray jArray = jElement.getAsJsonArray();
		int size = jArray.size();
		String[] arr = new String[size];
		for (int i = 0; i < size; i++) {
			JsonElement arrayElement = jArray.get(i);
			if (!arrayElement.isJsonPrimitive())
				return null; // Throw error.
			arrayElement.getAsString();
			JsonPrimitive arrayPrimitive = arrayElement.getAsJsonPrimitive();
			arr[i] = arrayPrimitive.getAsString();
		}
		return arr;
	}
	
	@Override
	public String toString() {
		return String.format(
				  "{ inputFileTypes: %s\n"
				+ "  outputFileTypes: %s\n"
				+ "  defaultTimeLimit: %s\n"
				+ "  minimumTimeLimit: %s\n"
				+ "  maximumTimeLimit: %s\n"
				+ "  languageSpecs: %s }\n",
				inputFileTypes == null ? null : Arrays.toString(inputFileTypes),
				outputFileTypes == null ? null : Arrays.toString(outputFileTypes),
				defaultTimeLimit, minimumTimeLimit, maximumTimeLimit,
				languageSpecs == null ? null : Arrays.toString(languageSpecs));
	}
	
	static class Builder {
		private String[] inputFileTypes;
		private String[] outputFileTypes;
		
		private Long defaultTimeLimit;
		private Long minimumTimeLimit;
		private Long maximumTimeLimit;
		
		private LanguageSpec[] languageSpecs;
		
		public Builder() {
			
		}
		
		public Config build() {
			return new Config(inputFileTypes, outputFileTypes, defaultTimeLimit, minimumTimeLimit, maximumTimeLimit, languageSpecs);
		}

		public String[] getInputFileTypes() {
			return inputFileTypes;
		}

		public Builder setInputFileTypes(String[] inputFileTypes) {
			this.inputFileTypes = inputFileTypes;
			return this;
		}

		public String[] getOutputFileTypes() {
			return outputFileTypes;
		}

		public Builder setOutputFileTypes(String[] outputFileTypes) {
			this.outputFileTypes = outputFileTypes;
			return this;
		}

		public Long getDefaultTimeLimit() {
			return defaultTimeLimit;
		}

		public Builder setDefaultTimeLimit(Long defaultTimeLimit) {
			this.defaultTimeLimit = defaultTimeLimit;
			return this;
		}

		public Long getMinimumTimeLimit() {
			return minimumTimeLimit;
		}

		public Builder setMinimumTimeLimit(Long minimumTimeLimit) {
			this.minimumTimeLimit = minimumTimeLimit;
			return this;
		}

		public Long getMaximumTimeLimit() {
			return maximumTimeLimit;
		}

		public Builder setMaximumTimeLimit(Long maximumTimeLimit) {
			this.maximumTimeLimit = maximumTimeLimit;
			return this;
		}

		public LanguageSpec[] getLanguageSpecs() {
			return languageSpecs;
		}

		public Builder setLanguageSpecs(LanguageSpec[] languageSpecs) {
			this.languageSpecs = languageSpecs;
			return this;
		}
		
		
	}
}
